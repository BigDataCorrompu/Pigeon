package fr.pigeon.tests;

import fr.pigeon.entity.*;
import fr.pigeon.multithreading.*;

import java.util.List;

public class TestSuite {

    private static int failures = 0;
    private static int successes = 0;

    public static void main(String[] args) {
        try {
            testMealFreshnessAndDecrease();
            testPigeonSleepsWhenNoFood();
            testPigeonTargetsFreshestMeal();
            testEatingFreshMealRemovesIt();
            testIgnoreRottenMeal();
            testForceDisperse();
            testConcurrentEatSingleRemoval();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        }

        System.out.println("Tests passed: " + successes + ", failed: " + failures);
        if (failures > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(boolean cond, String message) {
        if (!cond) fail(message);
        else successes++;
    }

    private static void fail(String message) {
        System.err.println("FAIL: " + message);
        failures++;
    }

    // 1. Test freshness decrease
    private static void testMealFreshnessAndDecrease() {
        Meal m = new Meal(new Coordinate(0, 0));
        for (int i = 0; i < 100; i++) m.decreaseFreshness();
        assertTrue(!m.isFresh(), "Meal should be rotten after 100 decreases");
    }

    // 2. Pigeons sleep when no food
    private static void testPigeonSleepsWhenNoFood() {
        GameState gs = new GameState();
        Pigeon p = new Pigeon(new Coordinate(10, 10));
        gs.addPigeon(p);
        // initially no meals
        gs.updateFlyingPigeons();
        assertTrue(!p.isFlying(), "Pigeon should be stopped when no food");
    }

    // 3. Pigeon targets the freshest meal
    private static void testPigeonTargetsFreshestMeal() {
        GameState gs = new GameState();
        Pigeon p = new Pigeon(new Coordinate(0, 0));
        gs.addPigeon(p);
        Meal m1 = new Meal(new Coordinate(50, 50));
        Meal m2 = new Meal(new Coordinate(100, 100));
        // make m2 less fresh
        for (int i = 0; i < 10; i++) m2.decreaseFreshness();
        gs.addMeal(m1);
        gs.addMeal(m2);
        gs.updatePositionsPigeons();
        Coordinate target = p.getTarget();
        assertTrue(target != null, "Pigeon should have a target");
        // target should be m1 (more fresh)
        assertTrue(Math.abs(target.getX() - m1.getPosition().getX()) < 0.001f &&
                Math.abs(target.getY() - m1.getPosition().getY()) < 0.001f,
                "Pigeon should target the freshest meal (m1)");
    }

    // 4. Eating a fresh meal removes it
    private static void testEatingFreshMealRemovesIt() {
        GameState gs = new GameState();
        Pigeon p = new Pigeon(new Coordinate(200, 200));
        gs.addPigeon(p);
        Meal m = new Meal(new Coordinate(200, 200));
        gs.addMeal(m);
        // call collisions update which should eat the meal
        gs.updateCollisions();
        List<Meal> meals = gs.getMeals();
        assertTrue(!meals.contains(m), "Fresh meal should be removed after being eaten");
    }

    // 5. Rotten meal is ignored
    private static void testIgnoreRottenMeal() {
        GameState gs = new GameState();
        Pigeon p = new Pigeon(new Coordinate(300, 300));
        gs.addPigeon(p);
        Meal m = new Meal(new Coordinate(300, 300));
        // make it rotten
        for (int i = 0; i < 200; i++) m.decreaseFreshness();
        gs.addMeal(m);
        gs.updateCollisions();
        // rotten food should remain (ignored)
        assertTrue(gs.getMeals().contains(m), "Rotten meal should be ignored and remain in the meals list or moved to rottenMeals by freshness updater");
    }

    // 6. Force disperse moves pigeons to positions inside window
    private static void testForceDisperse() {
        GameState gs = new GameState();
        Pigeon p = new Pigeon(new Coordinate(10, 10));
        gs.addPigeon(p);
        gs.forceDisperse();
        Coordinate t = p.getTarget();
        assertTrue(t != null, "After disperse pigeon should have a target");
        assertTrue(t.getX() >= 0 && t.getX() <= 800 && t.getY() >= 0 && t.getY() <= 600,
                "Dispersed target should be inside window bounds");
    }

    // 7. Concurrent eat: only one pigeon should get to feed when two try to eat same meal concurrently
    private static void testConcurrentEatSingleRemoval() throws InterruptedException {
        final GameState gs = new GameState();
        final Meal m = new Meal(new Coordinate(400, 400));
        gs.addMeal(m);
        final Pigeon p1 = new Pigeon(new Coordinate(400, 400));
        final Pigeon p2 = new Pigeon(new Coordinate(400, 400));
        gs.addPigeon(p1);
        gs.addPigeon(p2);

        Thread t1 = new Thread(() -> gs.eatMeal(p1, m));
        Thread t2 = new Thread(() -> gs.eatMeal(p2, m));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        int eaten1 = p1.getMealsEaten();
        int eaten2 = p2.getMealsEaten();
        int totalEaten = eaten1 + eaten2;
        assertTrue(totalEaten == 1, "Only one pigeon should have eaten the meal (total eaten == 1), got: " + totalEaten);
    }

}

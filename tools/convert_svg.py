#!/usr/bin/env python3
"""
Convert SVG assets to PNG using CairoSVG.

Usage:
  python tools/convert_svg.py [--width W] [--height H] [--src PATH] [--out PATH]

This script is intended to be run from the project root. It will by default
convert `src/fr/pigeon/resources/pigeon.svg` to
`src/fr/pigeon/resources/pigeon.png` at 32x32.
"""
import argparse
import os
import sys

try:
    import cairosvg
except Exception as e:
    print("Error: cairosvg not installed. Run: python -m pip install -r requirements.txt")
    raise


def main():
    parser = argparse.ArgumentParser(description="Convert SVG to PNG for the Pigeon project")
    parser.add_argument("--width", type=int, default=32, help="output width in px")
    parser.add_argument("--height", type=int, default=32, help="output height in px")
    parser.add_argument("--src", type=str, default="src/fr/pigeon/resources/pigeon.svg", help="source SVG path")
    parser.add_argument("--out", type=str, default="src/fr/pigeon/resources/pigeon.png", help="output PNG path")
    args = parser.parse_args()

    src = args.src
    out = args.out

    if not os.path.exists(src):
        print(f"Source SVG not found: {src}")
        sys.exit(2)

    os.makedirs(os.path.dirname(out), exist_ok=True)

    print(f"Rasterizing {src} -> {out} at {args.width}x{args.height}...")
    try:
        cairosvg.svg2png(url=src, write_to=out, output_width=args.width, output_height=args.height)
    except Exception as e:
        print("Rasterization failed:", e)
        sys.exit(3)

    print("Done.")


if __name__ == '__main__':
    main()

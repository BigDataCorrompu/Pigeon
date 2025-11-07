package fr.pigeon.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Helper that tries to rasterize an SVG InputStream to a BufferedImage using
 * Apache Batik via reflection. This avoids a hard compile-time dependency on
 * Batik: if the Batik jars are present on the classpath at runtime the
 * rasterization will be attempted. Otherwise the method returns null.
 */
public final class SvgRasterizer {
    private SvgRasterizer() {}

    /**
     * Attempt to rasterize the given SVG InputStream to a BufferedImage.
     * If Batik is not available or an error occurs, returns null.
     *
     * @param svgStream SVG input stream (will not be closed by this method)
     * @param targetWidth desired width (if <=0 the original SVG size is used)
     * @param targetHeight desired height (if <=0 the original SVG size is used)
     * @return a BufferedImage, or null if rasterization failed / Batik not present
     */
    public static BufferedImage rasterizeSVG(InputStream svgStream, int targetWidth, int targetHeight) {
        try {
            // Check presence of Batik PNGTranscoder
            Class<?> pngClass = Class.forName("org.apache.batik.transcoder.image.PNGTranscoder");

            // Create transcoder instance
            Object transcoder = pngClass.getDeclaredConstructor().newInstance();

            // Prepare input and output (TranscoderInput, TranscoderOutput)
            Class<?> inputClass = Class.forName("org.apache.batik.transcoder.TranscoderInput");
            Object input = inputClass.getConstructor(java.io.InputStream.class).newInstance(svgStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Class<?> outputClass = Class.forName("org.apache.batik.transcoder.TranscoderOutput");
            Object output = outputClass.getConstructor(java.io.OutputStream.class).newInstance(baos);

            // Invoke transcode(input, output)
            java.lang.reflect.Method transcodeMethod = pngClass.getMethod("transcode", inputClass, outputClass);
            transcodeMethod.invoke(transcoder, input, output);

            // Read PNG bytes into BufferedImage
            byte[] pngBytes = baos.toByteArray();
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(pngBytes));

            // Optionally scale if target size requested
            if (img != null && targetWidth > 0 && targetHeight > 0
                    && (img.getWidth() != targetWidth || img.getHeight() != targetHeight)) {
                BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                        java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(img, 0, 0, targetWidth, targetHeight, null);
                g.dispose();
                return scaled;
            }

            return img;
        } catch (ClassNotFoundException cnf) {
            // Batik not present on classpath
            return null;
        } catch (Exception e) {
            // Any failure in reflection/rasterization -> return null to fallback
            return null;
        }
    }
}

package net.burningtnt.webp.awt;

import net.burningtnt.webp.SimpleWEBPLoader;
import net.burningtnt.webp.utils.RGBABuffer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

public final class AWTImageLoader {
    private AWTImageLoader() {
    }

    public static BufferedImage decode(InputStream inputStream) throws IOException {
        RGBABuffer.AbsoluteRGBABuffer rgbaBuffer = SimpleWEBPLoader.decodeStreamByImageLoaders(inputStream);

        int width = rgbaBuffer.getWidth(), height = rgbaBuffer.getHeight();
        BufferedImage awtImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        WritableRaster writableRaster = awtImage.getRaster();
        byte[] rgbaData = rgbaBuffer.getRGBAData(), cache = new byte[4];
        for (int y = 0; y < height; y++) {
            int lineIndex = y * width * 4;
            for (int x = 0; x < width; x++) {
                System.arraycopy(rgbaData, lineIndex + x * 4, cache, 0, 4);
                writableRaster.setDataElements(x, y, cache);
            }
        }

        return awtImage;
    }
}

package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import javafx.scene.image.Image;
import net.burningtnt.webp.utils.AwtJavaFxTranslator;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class WEBPImageLoader extends ImageLoaderImpl {
    private final InputStream inputStream;

    public WEBPImageLoader(InputStream inputStream) {
        super(WEBPDescriptor.getInstance());
        this.inputStream = inputStream;
    }

    @Override
    public void dispose() {
    }

    @Override
    public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth) throws IOException {
        Image writableImage = VP8LDecoder.decodeStream(this.inputStream);

        ByteBuffer argbImageBuffer = AwtJavaFxTranslator.getImageBuffer(writableImage);

        // The ImageBuffer is BGRA, however we want RGBA instead.

        byte[] rgbaImageByteArray = new byte[((int) writableImage.getWidth()) * ((int) writableImage.getHeight()) * 4];

        for (int y = 0; y < (int) writableImage.getHeight(); y++) {
            for (int x = 0; x < (int) writableImage.getWidth(); x++) {
                int index = y * ((int) writableImage.getWidth()) * 4 + x * 4;

                rgbaImageByteArray[index] = argbImageBuffer.get(index + 2); // R
                rgbaImageByteArray[index + 1] = argbImageBuffer.get(index + 1); // G
                rgbaImageByteArray[index + 2] = argbImageBuffer.get(index); // B
                rgbaImageByteArray[index + 3] = argbImageBuffer.get(index + 3); // A
            }
        }

        return new ImageFrame(
                ImageStorage.ImageType.RGBA,
                ByteBuffer.wrap(rgbaImageByteArray),
                (int) writableImage.getWidth(), (int) writableImage.getHeight(),
                ((int) writableImage.getWidth()) * 4, null,
                new ImageMetadata(
                        null, Boolean.FALSE, null, null, null, null, null,
                        (int) writableImage.getWidth(), (int) writableImage.getHeight(),
                        null, null, null
                )
        );
    }
}

package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import net.burningtnt.webp.WEBPImageLoader;
import net.burningtnt.webp.utils.RGBABuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class JavaFxWEBPImageLoader extends ImageLoaderImpl {
    private final InputStream inputStream;

    public JavaFxWEBPImageLoader(InputStream inputStream) {
        super(JavaFxWEBPDescriptor.getInstance());
        this.inputStream = inputStream;
    }

    @Override
    public void dispose() {
    }

    @Override
    public ImageFrame load(int imageIndex, int width, int height, boolean preserveAspectRatio, boolean smooth) throws IOException {
        RGBABuffer rgbaBuffer = WEBPImageLoader.decodeStreamByImageLoaders(this.inputStream);

        return new ImageFrame(
                ImageStorage.ImageType.RGBA,
                ByteBuffer.wrap(rgbaBuffer.getRGBAData()),
                rgbaBuffer.getWidth(), rgbaBuffer.getHeight(),
                rgbaBuffer.getWidth() * 4, null,
                new ImageMetadata(
                        null, Boolean.FALSE, null, null, null, null, null,
                        rgbaBuffer.getWidth(), rgbaBuffer.getHeight(),
                        null, null, null
                )
        );
    }
}

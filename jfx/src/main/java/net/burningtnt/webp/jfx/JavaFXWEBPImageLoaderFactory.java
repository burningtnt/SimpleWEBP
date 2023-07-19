package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import com.sun.javafx.iio.ImageStorage;

import java.io.InputStream;

public final class JavaFxWEBPImageLoaderFactory implements ImageLoaderFactory {
    private static final JavaFxWEBPImageLoaderFactory instance = new JavaFxWEBPImageLoaderFactory();

    private JavaFxWEBPImageLoaderFactory() {
    }

    @Override
    public ImageFormatDescription getFormatDescription() {
        return JavaFxWEBPDescriptor.getInstance();
    }

    @Override
    public ImageLoader createImageLoader(InputStream input) {
        return new JavaFxWEBPImageLoader(input);
    }

    public static void setupListener() {
        ImageStorage.addImageLoaderFactory(instance);
    }
}

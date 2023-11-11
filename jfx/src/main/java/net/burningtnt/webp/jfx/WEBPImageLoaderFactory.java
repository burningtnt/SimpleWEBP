package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageLoaderFactory;
import com.sun.javafx.iio.ImageStorage;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public final class WEBPImageLoaderFactory implements ImageLoaderFactory {
    private static final WEBPImageLoaderFactory instance = new WEBPImageLoaderFactory();

    private WEBPImageLoaderFactory() {
    }

    @Override
    public ImageFormatDescription getFormatDescription() {
        return WEBPImageLoader.getImageDescriptor();
    }

    @Override
    public ImageLoader createImageLoader(InputStream input) {
        return new WEBPImageLoader(input);
    }

    public static void setupListener() {
        ImageStorage imageStorage; // Get the instance of ImageStorage if needed.
        try {
            imageStorage = (ImageStorage) ImageStorage.class.getMethod("getInstance").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            imageStorage = null;
        }

        try {
            ImageStorage.class.getMethod("addImageLoaderFactory", ImageLoaderFactory.class).invoke(imageStorage, instance);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot install WEBPImageLoader", e);
        }
    }
}

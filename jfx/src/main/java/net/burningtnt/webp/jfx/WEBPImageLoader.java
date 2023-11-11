package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.common.ImageDescriptor;
import com.sun.javafx.iio.common.ImageLoaderImpl;
import com.sun.javafx.iio.common.ImageTools;
import net.burningtnt.webp.SimpleWEBPLoader;
import net.burningtnt.webp.utils.RGBABuffer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class WEBPImageLoader extends ImageLoaderImpl {
    private static final ImageDescriptor IMAGE_DESCRIPTOR = initImageDescriptor();

    public static ImageDescriptor getImageDescriptor() {
        return IMAGE_DESCRIPTOR;
    }

    private static ImageDescriptor initImageDescriptor() {
        Throwable throwable1;
        try {
            return ImageDescriptor.class.getConstructor(String.class, String[].class, ImageFormatDescription.Signature[].class)
                    .newInstance(
                            "WEBP",
                            new String[]{"webp"},
                            new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')}
                    );
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throwable1 = e;
        }

        try {
            return ImageDescriptor.class.getConstructor(String.class, String[].class, ImageFormatDescription.Signature[].class, String[].class)
                    .newInstance(
                            "WEBP",
                            new String[]{"webp"},
                            new ImageFormatDescription.Signature[]{new ImageFormatDescription.Signature((byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F')},
                            new String[]{"webp"}
                    );
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            IllegalStateException t = new IllegalStateException("Cannot construct a ImageDescriptor.", e);
            t.addSuppressed(throwable1);
            throw t;
        }
    }

    private final InputStream inputStream;

    public WEBPImageLoader(InputStream inputStream) {
        super(getImageDescriptor());
        this.inputStream = inputStream;
    }

    @Override
    public void dispose() {
    }

    @Override
    public ImageFrame load(int imageIndex, int rWidth, int rHeight, boolean preserveAspectRatio, boolean smooth) throws IOException {
        RGBABuffer.AbsoluteRGBABuffer rgbaBuffer = SimpleWEBPLoader.decodeStreamByImageLoaders(this.inputStream);

        int width = rgbaBuffer.getWidth(), height = rgbaBuffer.getHeight();

        int[] outWH = ImageTools.computeDimensions(width, height, rWidth, rHeight, preserveAspectRatio);
        rWidth = outWH[0];
        rHeight = outWH[1];

        ImageFrame imageFrame = new ImageFrame(
                ImageStorage.ImageType.RGBA,
                ByteBuffer.wrap(rgbaBuffer.getRGBAData()),
                width, height,
                width * 4, null,
                new ImageMetadata(
                        null, Boolean.TRUE, null, null, null, null, null,
                        rWidth, rHeight,
                        null, null, null
                )
        );

        return width != rWidth || height != rHeight ? ImageTools.scaleImageFrame(imageFrame, rWidth, rHeight, smooth) : imageFrame;
    }
}

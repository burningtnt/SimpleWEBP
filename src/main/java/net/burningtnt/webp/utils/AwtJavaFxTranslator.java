package net.burningtnt.webp.utils;

import com.sun.javafx.image.PixelGetter;
import com.sun.javafx.image.PixelSetter;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class AwtJavaFxTranslator {
    private AwtJavaFxTranslator() {
    }

    private static final Method imageGetWritablePlatformImageMethod;

    private static final Method platformImageGetPixelAccessorMethod;

    private static final Method baseAccessorGetSetterMethod;
    private static final Method baseAccessorGetGetterMethod;
    private static final Method baseAccessorGetBufferMethod;
    private static final Method baseAccessorGetIndexMethod;

    private static final Field imageReaderField;
    private static final Field writableImageWriterField;

    static {
        try {
            imageGetWritablePlatformImageMethod = Image.class.getDeclaredMethod("getWritablePlatformImage");
            imageGetWritablePlatformImageMethod.setAccessible(true);

            platformImageGetPixelAccessorMethod = Class.forName("com.sun.prism.Image").getDeclaredMethod("getPixelAccessor");
            platformImageGetPixelAccessorMethod.setAccessible(true);

            baseAccessorGetSetterMethod = Class.forName("com.sun.prism.Image$BaseAccessor").getDeclaredMethod("getSetter");
            baseAccessorGetSetterMethod.setAccessible(true);

            baseAccessorGetGetterMethod = Class.forName("com.sun.prism.Image$BaseAccessor").getDeclaredMethod("getGetter");
            baseAccessorGetGetterMethod.setAccessible(true);

            baseAccessorGetBufferMethod = Class.forName("com.sun.prism.Image$BaseAccessor").getDeclaredMethod("getBuffer");
            baseAccessorGetBufferMethod.setAccessible(true);

            baseAccessorGetIndexMethod = Class.forName("com.sun.prism.Image$BaseAccessor").getDeclaredMethod("getIndex", int.class, int.class);
            baseAccessorGetIndexMethod.setAccessible(true);

            imageReaderField = Image.class.getDeclaredField("reader");
            imageReaderField.setAccessible(true);

            writableImageWriterField = WritableImage.class.getDeclaredField("writer");
            writableImageWriterField.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getDataElementsAsByteArrayFromWritableImage(Image writableImage, int x, int y, byte[] rgba) {
        int argb;

        try {
            com.sun.prism.Image platformImage = (com.sun.prism.Image) imageGetWritablePlatformImageMethod.invoke(writableImage);
            Object accessor = platformImageGetPixelAccessorMethod.invoke(platformImage);

            PixelGetter<Buffer> pixelGetter = (PixelGetter<Buffer>) baseAccessorGetGetterMethod.invoke(accessor);
            Buffer pixelBuffer = (Buffer) baseAccessorGetBufferMethod.invoke(accessor);
            int offset = (Integer) baseAccessorGetIndexMethod.invoke(accessor, x, y);

            argb = pixelGetter.getArgbPre(pixelBuffer, offset);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        rgba[0] = (byte) (argb >>> 16 & 0xFF);
        rgba[1] = (byte) (argb >>> 8 & 0xFF);
        rgba[2] = (byte) (argb & 0xFF);
        rgba[3] = (byte) (argb >>> 24 & 0xFF);
    }

    public static void setDataElementsFromByteArrayToWritableImage(WritableImage writableImage, int x, int y, byte[] rgba) {
        int argb = (rgba[3] & 0xFF) << 24 | (rgba[0] & 0xFF) << 16 | (rgba[1] & 0xFF) << 8 | (rgba[2] & 0xFF);

        /*
            Stack Trace:
                com.sun.javafx.image.PixelUtils.NonPretoPre(int)
                com.sun.javafx.image.impl.ByteBgraPre.Accessor.setArgb(java.nio.ByteBuffer, int, int)
                com.sun.prism.Image.BaseAccessor.setArgb
                javafx.scene.image.PixelWriter.setArgb (anonymous)
            Description:
                if alpha is 0x00, red / green / blue will be ignored.
            Solution:
                F*** you JavaFx
        */

        try {
            com.sun.prism.Image platformImage = (com.sun.prism.Image) imageGetWritablePlatformImageMethod.invoke(writableImage);
            Object accessor = platformImageGetPixelAccessorMethod.invoke(platformImage);

            PixelSetter<Buffer> pixelSetter = (PixelSetter<Buffer>) baseAccessorGetSetterMethod.invoke(accessor);
            Buffer pixelBuffer = (Buffer) baseAccessorGetBufferMethod.invoke(accessor);
            int offset = (Integer) baseAccessorGetIndexMethod.invoke(accessor, x, y);

            pixelSetter.setArgbPre(pixelBuffer, offset, argb);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (writableImage instanceof SubWritableImage) {
            SubWritableImage subWritableImage = (SubWritableImage) writableImage;
            setDataElementsFromByteArrayToWritableImage(subWritableImage.parent, x + subWritableImage.x, y + subWritableImage.y, rgba);
        }
    }

    public static int getSampleAsIntFromWritableImage(WritableImage writableImage, int x, int y, int sample) {
        byte[] rgba = new byte[4];
        getDataElementsAsByteArrayFromWritableImage(writableImage, x, y, rgba);
        return rgba[sample];
    }

    public static ByteBuffer getImageBuffer(Image image) {
        try {
            com.sun.prism.Image platformImage = (com.sun.prism.Image) imageGetWritablePlatformImageMethod.invoke(image);
            Object accessor = platformImageGetPixelAccessorMethod.invoke(platformImage);

            return (ByteBuffer) baseAccessorGetBufferMethod.invoke(accessor);


        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class SubWritableImage extends WritableImage {
        public final WritableImage parent;
        public final int x;
        public final int y;
        public final int w;
        public final int h;

        public SubWritableImage(WritableImage parent, int x, int y, int w, int h) {
            super(w, h);

            this.parent = parent;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

            disableVanillaPixelAccessor(this);

            byte[] rgba = new byte[4];
            for (int dy = 0; dy < h; dy++) {
                for (int dx = 0; dx < w; dx++) {
                    getDataElementsAsByteArrayFromWritableImage(parent, x + dx, y + dy, rgba);
                    setDataElementsFromByteArrayToWritableImage(this, dx, dy, rgba);
                }
            }
        }
    }

    public static WritableImage createSubWritableImage(WritableImage parentWritableImage, int x, int y, int subW, int subH) {
        return new SubWritableImage(parentWritableImage, x, y, subW, subH);
    }

    public static WritableImage createWritableImage(int w, int h) {
        WritableImage writableImage = new WritableImage(w, h);

        disableVanillaPixelAccessor(writableImage);

        return writableImage;
    }

    private static final PixelReader blankPixelReader = new PixelReader() {
        @Override
        public PixelFormat<?> getPixelFormat() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getArgb(int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Color getColor(int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException();
        }
    };

    private static final PixelWriter blankPixelWriter = new PixelWriter() {
        @Override
        public PixelFormat<?> getPixelFormat() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setArgb(int x, int y, int argb) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setColor(int x, int y, Color c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T extends Buffer> void setPixels(int x, int y, int w, int h, PixelFormat<T> pixelformat, T buffer, int scanlineStride) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPixels(int x, int y, int w, int h, PixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPixels(int x, int y, int w, int h, PixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPixels(int dstx, int dsty, int w, int h, PixelReader reader, int srcx, int srcy) {
            throw new UnsupportedOperationException();
        }
    };

    public static void disableVanillaPixelAccessor(Image image) {
        try {
            imageReaderField.set(image, blankPixelReader);

            if (image instanceof WritableImage) {
                writableImageWriterField.set(image, blankPixelWriter);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void enableVanillaPixelAccessor(Image image) {
        try {
            imageReaderField.set(image, null);

            if (image instanceof WritableImage) {
                writableImageWriterField.set(image, null);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

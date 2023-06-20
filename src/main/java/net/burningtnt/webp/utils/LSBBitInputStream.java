package net.burningtnt.webp.utils;

import java.io.*;
import java.lang.reflect.Field;

public final class LSBBitInputStream {
    public static Object getDeclaredFieldAsObject(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getDeclaredFieldAsInt(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.getInt(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteArrayInputStream copyAsByteArrayInputStream(InputStream inputStream) {
        if (inputStream instanceof ByteArrayInputStream) {
            byte[] buffer = (byte[]) getDeclaredFieldAsObject(inputStream, "buf");
            int pos = getDeclaredFieldAsInt(inputStream, "pos");
            return new ByteArrayInputStream(buffer, pos, buffer.length);
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer, 0, 8192)) >= 0) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] buffer = (byte[]) getDeclaredFieldAsObject(byteArrayOutputStream, "buf");
            int size = getDeclaredFieldAsInt(byteArrayOutputStream, "count");
            return new ByteArrayInputStream(buffer, 0, size);
        }
    }

    private final ByteArrayInputStream inputStream;
    private int bitOffset = 64;
    private long buffer;
    private boolean used = false;

    public LSBBitInputStream(ByteArrayInputStream byteArrayInputStream) {
        this.inputStream = byteArrayInputStream;
    }

    public long readBits(int bits) {
        if (bits <= 56) {
            if (!used) {
                refillBuffer();
                used = true;
            }

            long ret = (buffer >>> bitOffset) & ((1L << bits) - 1);

            bitOffset += bits;

            if (bitOffset >= 8) {
                refillBuffer();
            }

            return ret;
        } else {
            long lower = readBits(56);
            return (readBits(bits - 56) << (56)) | lower;
        }
    }

    public long peekBits(int bits) throws IOException {
        if (bits <= 56) {
            return (buffer >>> bitOffset) & ((1L << bits) - 1);
        } else {
            throw new UnsupportedEncodingException("Cannot peek over 56 bits");
        }
    }

    public int readBit() throws IOException {
        return (int) readBits(1);
    }

    private void refillBuffer() {
        for (; bitOffset >= 8; bitOffset -= 8) {
            byte readByte = (byte) inputStream.read();
            buffer = ((long) readByte << 56) | buffer >>> 8;
        }
    }
}


package net.burningtnt.webp.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public final class Accessor {
    private Accessor() {
    }

    public static ByteArrayInputStream dupeInputStreamAsByteArrayInputStream(InputStream inputStream) throws IOException {
        if (inputStream instanceof ByteArrayInputStream) {
            try {
                byte[] buffer = (byte[]) getDeclaredFieldAsObject(ByteArrayInputStream.class, (ByteArrayInputStream) inputStream, "buf");
                int pos = getDeclaredFieldAsInt(ByteArrayInputStream.class, (ByteArrayInputStream) inputStream, "pos");
                return new ByteArrayInputStream(buffer, pos, buffer.length);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IOException(e);
            }
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer, 0, 8192)) >= 0) {
                    byteArrayOutputStream.write(buffer, 0, read);
                }
            }

            try {
                byte[] buffer = (byte[]) getDeclaredFieldAsObject(ByteArrayOutputStream.class, byteArrayOutputStream, "buf");
                int size = getDeclaredFieldAsInt(ByteArrayOutputStream.class, byteArrayOutputStream, "count");
                return new ByteArrayInputStream(buffer, 0, size);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Get a private field as an Object.
     *
     * @param object the object of the field. It can be null if the field is static
     * @param name   the name of the field.
     * @param clazz  the class of the object.
     * @return the value of the field.
     */
    private static <T> Object getDeclaredFieldAsObject(@NotNull Class<T> clazz, @Nullable T object, @NotNull String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Get a private field as an int.
     *
     * @param object the object of the field. It can be null if the field is static
     * @param name   the name of the field.
     * @param clazz  the class of the object.
     * @return the value of the field.
     */
    private static <T> int getDeclaredFieldAsInt(@NotNull Class<T> clazz, @Nullable T object, @NotNull String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(object);
    }
}

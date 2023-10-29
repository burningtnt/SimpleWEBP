package net.burningtnt.webp;

import net.burningtnt.webp.utils.RGBABuffer;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;
import java.io.InputStream;

public enum SimpleWEBPLoader {
    VP8L {
        @Override
        public RGBABuffer decode(InputStream inputStream) throws IOException {
            return VP8LDecoder.decodeStream(inputStream);
        }
    };

    public abstract RGBABuffer decode(InputStream inputStream) throws IOException;

    private static final int length = values().length;
    private static final SimpleWEBPLoader[] values = values();

    public static RGBABuffer decodeStreamByImageLoaders(InputStream inputStream) throws IOException {
        Throwable[] errors = null;

        for (int i = 0; i < length; i++) {
            try {
                return values[i].decode(inputStream);
            } catch (Throwable e) {
                if (errors == null) {
                    errors = new Throwable[length];
                }
                errors[i] = e;
            }
        }

        IOException e = new IOException(String.format("Failed to load image from %s.", inputStream));
        if (errors != null) {
            for (int i = 0; i < length; i++) {
                e.addSuppressed(new IOException(String.format("Image Loader %s encountered an exception.", values[i].name()), errors[i]));
            }
        }
        throw e;
    }
}

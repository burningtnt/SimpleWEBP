package net.burningtnt.webp;

import net.burningtnt.webp.utils.RGBABuffer;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;

public enum SimpleWEBPLoader {
    VP8L(VP8LDecoder::decodeStream);

    public static RGBABuffer decodeStreamByImageLoaders(InputStream inputStream) throws IOException {
        EnumMap<SimpleWEBPLoader, IOException> errors = new EnumMap<>(SimpleWEBPLoader.class);

        for (SimpleWEBPLoader simpleWebpLoader : values()) {
            try {
                return simpleWebpLoader.decodeStreamByCurrentImageLoader(inputStream);
            } catch (IOException e) {
                errors.put(simpleWebpLoader, e);
            }
        }

        IOException e = new IOException(String.format("Fail to load image from %s because all the image loaders throw an IOException.", inputStream));
        for (SimpleWEBPLoader simpleWebpLoader : values()) {
            e.addSuppressed(new IOException(String.format("Image Loader %s throw an IOException", simpleWebpLoader.name()), errors.get(simpleWebpLoader)));
        }
        throw e;
    }

    @FunctionalInterface
    private interface LoadAction {
        RGBABuffer load(InputStream inputStream) throws IOException;
    }

    private final LoadAction delegate;

    SimpleWEBPLoader(LoadAction delegate) {
        this.delegate = delegate;
    }

    public RGBABuffer decodeStreamByCurrentImageLoader(InputStream inputStream) throws IOException {
        return this.delegate.load(inputStream);
    }
}

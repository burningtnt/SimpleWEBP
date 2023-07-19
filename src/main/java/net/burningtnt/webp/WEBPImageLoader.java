package net.burningtnt.webp;

import net.burningtnt.webp.utils.RGBABuffer;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;

public enum WEBPImageLoader {
    VP8L(VP8LDecoder::decodeStream);

    public static RGBABuffer decodeStreamByImageLoaders(InputStream inputStream) throws IOException {
        EnumMap<WEBPImageLoader, IOException> errors = new EnumMap<>(WEBPImageLoader.class);

        for (WEBPImageLoader webpImageLoader : values()) {
            try {
                return webpImageLoader.decodeStreamByCurrentImageLoader(inputStream);
            } catch (IOException e) {
                errors.put(webpImageLoader, e);
            }
        }

        IOException e = new IOException(String.format("Fail to load image from %s because all the image loaders throw an IOException.", inputStream));
        for (WEBPImageLoader webpImageLoader : values()) {
            e.addSuppressed(new IOException(String.format("Image Loader %s throw an IOException", webpImageLoader.name()), errors.get(webpImageLoader)));
        }
        throw e;
    }

    @FunctionalInterface
    private interface LoadAction {
        RGBABuffer load(InputStream inputStream) throws IOException;
    }

    private final LoadAction delegate;

    WEBPImageLoader(LoadAction delegate) {
        this.delegate = delegate;
    }

    public RGBABuffer decodeStreamByCurrentImageLoader(InputStream inputStream) throws IOException {
        return this.delegate.load(inputStream);
    }
}

/*
 * Copyright 2023 Burning_TNT
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.burningtnt.webp;

import net.burningtnt.webp.utils.RGBABuffer;
import net.burningtnt.webp.vp8l.VP8LDecoder;

import java.io.IOException;
import java.io.InputStream;

public enum SimpleWEBPLoader {
    VP8L {
        @Override
        public RGBABuffer.AbsoluteRGBABuffer decode(InputStream inputStream) throws IOException {
            return VP8LDecoder.decodeStream(inputStream);
        }
    };

    // WEBP Constants
    public static final int RIFF_MAGIC = 'R' << 24 | 'I' << 16 | 'F' << 8 | 'F';
    public static final int WEBP_MAGIC = 'W' << 24 | 'E' << 16 | 'B' << 8 | 'P';
    public static final int CHUNK_VP8L = 'V' << 24 | 'P' << 16 | '8' << 8 | 'L';
    public static final byte LOSSLESSS_SIG = 0x2f;

    public abstract RGBABuffer.AbsoluteRGBABuffer decode(InputStream inputStream) throws IOException;

    private static final int length = values().length;
    private static final SimpleWEBPLoader[] values = values();

    public static RGBABuffer.AbsoluteRGBABuffer decodeStreamByImageLoaders(InputStream inputStream) throws IOException {
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

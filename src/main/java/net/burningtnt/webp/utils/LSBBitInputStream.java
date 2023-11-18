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
package net.burningtnt.webp.utils;

import java.io.IOException;
import java.io.InputStream;

public final class LSBBitInputStream {
    private final InputStream inputStream;
    private int bitOffset = 64;
    private long buffer;
    private boolean used = false;

    public LSBBitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public long readBits(int bits) throws IOException {
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
            return readBits(56) | (readBits(bits - 56) << (56));
        }
    }

    public long peekBits(int bits) throws IOException {
        if (bits <= 56) {
            return (buffer >>> bitOffset) & ((1L << bits) - 1);
        } else {
            throw new IOException("Cannot peek over 56 bits.");
        }
    }

    public int readBit() throws IOException {
        return (int) readBits(1);
    }

    private void refillBuffer() throws IOException {
        for (; bitOffset >= 8; bitOffset -= 8) {
            buffer = ((long) (byte) inputStream.read() << 56) | buffer >>> 8;
        }
    }
}


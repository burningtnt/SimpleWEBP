package net.burningtnt.webp.utils;

import java.io.*;

public final class LSBBitInputStream {
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

    public int readBit() {
        return (int) readBits(1);
    }

    private void refillBuffer() {
        for (; bitOffset >= 8; bitOffset -= 8) {
            byte readByte = (byte) inputStream.read();
            buffer = ((long) readByte << 56) | buffer >>> 8;
        }
    }
}


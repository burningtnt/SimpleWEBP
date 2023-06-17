/*
 * Copyright (c) 2017, Harald Kuhr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.burningtnt.webp.vp8l;

import net.burningtnt.webp.utils.LSBBitInputStream;
import net.burningtnt.webp.utils.RGBABuffer;
import net.burningtnt.webp.vp8l.colorcache.ColorCache;
import net.burningtnt.webp.vp8l.huffman.HuffmanCodeGroup;
import net.burningtnt.webp.vp8l.huffman.HuffmanInfo;
import net.burningtnt.webp.vp8l.transform.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * VP8LDecoder.
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author Simon Kammermeier
 */
public final class VP8LDecoder {
    /**
     * TransformType.
     *
     * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
     */
    private static final int PREDICTOR_TRANSFORM = 0;
    private static final int COLOR_TRANSFORM = 1;
    private static final int SUBTRACT_GREEN_TRANSFORM = 2;
    private static final int COLOR_INDEXING_TRANSFORM = 3;

    /**
     * Used for decoding backward references
     * Upper 4Bits are y distance, lower 4 Bits are 8 minus x distance
     */
    private final static byte[] DISTANCES = {
            0x18, 0x07, 0x17, 0x19, 0x28, 0x06, 0x27, 0x29, 0x16, 0x1a,
            0x26, 0x2a, 0x38, 0x05, 0x37, 0x39, 0x15, 0x1b, 0x36, 0x3a,
            0x25, 0x2b, 0x48, 0x04, 0x47, 0x49, 0x14, 0x1c, 0x35, 0x3b,
            0x46, 0x4a, 0x24, 0x2c, 0x58, 0x45, 0x4b, 0x34, 0x3c, 0x03,
            0x57, 0x59, 0x13, 0x1d, 0x56, 0x5a, 0x23, 0x2d, 0x44, 0x4c,
            0x55, 0x5b, 0x33, 0x3d, 0x68, 0x02, 0x67, 0x69, 0x12, 0x1e,
            0x66, 0x6a, 0x22, 0x2e, 0x54, 0x5c, 0x43, 0x4d, 0x65, 0x6b,
            0x32, 0x3e, 0x78, 0x01, 0x77, 0x79, 0x53, 0x5d, 0x11, 0x1f,
            0x64, 0x6c, 0x42, 0x4e, 0x76, 0x7a, 0x21, 0x2f, 0x75, 0x7b,
            0x31, 0x3f, 0x63, 0x6d, 0x52, 0x5e, 0x00, 0x74, 0x7c, 0x41,
            0x4f, 0x10, 0x20, 0x62, 0x6e, 0x30, 0x73, 0x7d, 0x51, 0x5f,
            0x40, 0x72, 0x7e, 0x61, 0x6f, 0x50, 0x71, 0x7f, 0x60, 0x70
    };

    private static final int RIFF_MAGIC = 'R' << 24 | 'I' << 16 | 'F' << 8 | 'F';
    private static final int WEBP_MAGIC = 'W' << 24 | 'E' << 16 | 'B' << 8 | 'P';
    private static final int CHUNK_VP8_ = 'V' << 24 | 'P' << 16 | '8' << 8 | ' ';
    private static final int CHUNK_VP8L = 'V' << 24 | 'P' << 16 | '8' << 8 | 'L';

    private static final byte LOSSLESSS_SIG = 0x2f;

    private final LSBBitInputStream lsbBitReader;

    private VP8LDecoder(LSBBitInputStream imageInput) {
        this.lsbBitReader = imageInput;
    }

    public static RGBABuffer decodeStream(InputStream inputStream) throws IOException {
        ByteArrayInputStream byteArrayInputStream;
        if (inputStream instanceof ByteArrayInputStream) {
            byteArrayInputStream = (ByteArrayInputStream) inputStream;
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer, 0, 8192)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, read);
            }
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }

        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        if (dataInputStream.readInt() != RIFF_MAGIC) {
            throw new IOException("Invalid RIFF_MAGIC.");
        }

        dataInputStream.readInt();

        if (dataInputStream.readInt() != WEBP_MAGIC) {
            throw new IOException("Invalid WEBP_MAGIC");
        }

        int chunk = dataInputStream.readInt();
        dataInputStream.readInt();

        if (chunk != CHUNK_VP8L) {
            throw new UnsupportedEncodingException("SimpleWEBP can only decode VP8L");
        }

        if (dataInputStream.readByte() != LOSSLESSS_SIG) {
            throw new IOException("Invalid LOSSLESS_SIG");
        }

        LSBBitInputStream lsbBitInputStream = new LSBBitInputStream(byteArrayInputStream);

        int width = 1 + (int) lsbBitInputStream.readBits(14);
        int height = 1 + (int) lsbBitInputStream.readBits(14);
        lsbBitInputStream.readBit();

        if ((int) lsbBitInputStream.readBits(3) != 0) {
            throw new IOException("Invalid Version.");
        }

        RGBABuffer outputBuffer = RGBABuffer.createAbsoluteImage(width, height);

        new VP8LDecoder(lsbBitInputStream).readVP8Lossless(outputBuffer, true, width, height);

        return outputBuffer;
    }

    private void readVP8Lossless(final RGBABuffer raster, final boolean topLevel, int width, int height) throws IOException {
        int xSize = width;

        // Read transforms
        ArrayList<Transform> transforms = new ArrayList<>();
        while (topLevel && lsbBitReader.readBit() == 1) {
            xSize = readTransform(xSize, height, transforms);
        }

        // Read color cache size
        int colorCacheBits = 0;
        if (lsbBitReader.readBit() == 1) {
            colorCacheBits = (int) lsbBitReader.readBits(4);

            if (colorCacheBits < 1 || colorCacheBits > 11) {
                throw new IOException("Corrupt WebP stream, colorCacheBits < 1 || > 11: " + colorCacheBits);
            }
        }

        // Read Huffman codes
        HuffmanInfo huffmanInfo = readHuffmanCodes(xSize, height, colorCacheBits, topLevel);

        ColorCache colorCache = null;

        if (colorCacheBits > 0) {
            colorCache = new ColorCache(colorCacheBits);
        }

        RGBABuffer decodeRaster;
        if (topLevel) {
            // If multiple indices packed into one pixel xSize is different from raster width
            decodeRaster = RGBABuffer.createChildImage(raster, 0, 0, xSize, height);
        } else {
            // All recursive calls have Rasters of the correct sizes with origin (0, 0)
            decodeRaster = raster;
        }

        // Use the Huffman trees to decode the LZ77 encoded data.
        decodeImage(decodeRaster, huffmanInfo, colorCache);

        for (Transform transform : transforms) {
            transform.applyInverse(raster);
        }
    }

    private void decodeImage(RGBABuffer raster, HuffmanInfo huffmanInfo, ColorCache colorCache) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();

        int huffmanMask = huffmanInfo.metaCodeBits == 0 ? -1 : ((1 << huffmanInfo.metaCodeBits) - 1);
        HuffmanCodeGroup curCodeGroup = huffmanInfo.huffmanGroups[0];

        byte[] rgba = new byte[4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((x & huffmanMask) == 0 && huffmanInfo.huffmanMetaCodes != null) {
                    // Crossed border into new metaGroup
//                    int index = huffmanInfo.huffmanMetaCodes.getSample(x >> huffmanInfo.metaCodeBits, y >> huffmanInfo.metaCodeBits, 0);
                    // huffmanInfo.huffmanMetaCodes IntRaster
                    int index = huffmanInfo.huffmanMetaCodes.getSample(x >> huffmanInfo.metaCodeBits, y >> huffmanInfo.metaCodeBits, 1);
                    curCodeGroup = huffmanInfo.huffmanGroups[index];
                }

                short code = curCodeGroup.mainCode.readSymbol(lsbBitReader);

                if (code < 256) { // Literal
                    decodeLiteral(raster, colorCache, curCodeGroup, rgba, y, x, code);
                } else if (code < 256 + 24) { // backward reference
                    int length = decodeBwRef(raster, colorCache, width, curCodeGroup, rgba, code, x, y);

                    // Decrement one because for loop already increments by one
                    x--;
                    y = y + ((x + length) / width);
                    x = (x + length) % width;

                    // Reset Huffman meta group
                    if (y < height && x < width && huffmanInfo.huffmanMetaCodes != null) {
//                        int index = huffmanInfo.huffmanMetaCodes.getSample(x >> huffmanInfo.metaCodeBits, y >> huffmanInfo.metaCodeBits, 0);
                        int index = huffmanInfo.huffmanMetaCodes.getSample(x >> huffmanInfo.metaCodeBits, y >> huffmanInfo.metaCodeBits, 1);
                        curCodeGroup = huffmanInfo.huffmanGroups[index];
                    }
                } else { // colorCache
                    decodeCached(raster, colorCache, rgba, y, x, code);
                }
            }
        }
    }

    private void decodeCached(RGBABuffer raster, ColorCache colorCache, byte[] rgba, int y, int x, short code) {
        int argb = colorCache.lookup(code - 256 - 24);

        rgba[0] = (byte) ((argb >> 16) & 0xff);
        rgba[1] = (byte) ((argb >> 8) & 0xff);
        rgba[2] = (byte) (argb & 0xff);
        rgba[3] = (byte) (argb >>> 24);

//        raster.setDataElements(x, y, rgba);
        raster.setDataElements(x, y, rgba);
    }

    private void decodeLiteral(RGBABuffer raster, ColorCache colorCache, HuffmanCodeGroup curCodeGroup, byte[] rgba, int y, int x, short code) throws IOException {
        byte red = (byte) curCodeGroup.redCode.readSymbol(lsbBitReader);
        byte blue = (byte) curCodeGroup.blueCode.readSymbol(lsbBitReader);
        byte alpha = (byte) curCodeGroup.alphaCode.readSymbol(lsbBitReader);

        rgba[0] = red;
        rgba[1] = (byte) code;
        rgba[2] = blue;
        rgba[3] = alpha;
//        raster.setDataElements(x, y, rgba);
        raster.setDataElements(x, y, rgba);

        if (colorCache != null) {
            colorCache.insert((alpha & 0xff) << 24 | (red & 0xff) << 16 | (code & 0xff) << 8 | (blue & 0xff));
        }
    }

    private int decodeBwRef(RGBABuffer raster, ColorCache colorCache, int width, HuffmanCodeGroup curCodeGroup, byte[] rgba, short code, int x, int y) throws IOException {
        int length = lz77decode(code - 256);

        short distancePrefix = curCodeGroup.distanceCode.readSymbol(lsbBitReader);
        int distanceCode = lz77decode(distancePrefix);

        int xSrc, ySrc;

        if (distanceCode > 120) {
            // Linear distance
            int distance = distanceCode - 120;
            ySrc = y - (distance / width);
            xSrc = x - (distance % width);
        } else {
            // See comment of distances array
            xSrc = x - (8 - (DISTANCES[distanceCode - 1] & 0xf));
            ySrc = y - (DISTANCES[distanceCode - 1] >> 4);
        }

        if (xSrc < 0) {
            ySrc--;
            xSrc += width;
        } else if (xSrc >= width) {
            xSrc -= width;
            ySrc++;
        }

        for (int l = length; l > 0; x++, l--) {
            // Check length and xSrc, ySrc not falling outside raster? (Should not occur if image is correct)
            if (x == width) {
                x = 0;
                y++;
            }

//            raster.getDataElements(xSrc++, ySrc, rgba);
            int x1 = xSrc++;
            raster.getDataElements(x1, ySrc, rgba);
            //            raster.setDataElements(x, y, rgba);
            raster.setDataElements(x, y, rgba);

            if (xSrc == width) {
                xSrc = 0;
                ySrc++;
            }
            if (colorCache != null) {
                colorCache.insert((rgba[3] & 0xff) << 24 | (rgba[0] & 0xff) << 16 | (rgba[1] & 0xff) << 8 | (rgba[2] & 0xff));
            }
        }

        return length;
    }

    private int lz77decode(int prefixCode) throws IOException {
        // According to specification
        if (prefixCode < 4) {
            return prefixCode + 1;
        } else {
            int extraBits = (prefixCode - 2) >> 1;
            int offset = (2 + (prefixCode & 1)) << extraBits;

            return offset + (int) lsbBitReader.readBits(extraBits) + 1;
        }
    }

    private int readTransform(int xSize, int ySize, List<Transform> transforms) throws IOException {
        int transformType = (int) lsbBitReader.readBits(2);

        // TODO: Each transform type can only be present once in the stream.

        switch (transformType) {
            case PREDICTOR_TRANSFORM:
                // Intentional Fallthrough
            case COLOR_TRANSFORM: {
                // The two first transforms contains the exact same data, can be combined

                byte sizeBits = (byte) (lsbBitReader.readBits(3) + 2);

                int blockWidth = subSampleSize(xSize, sizeBits);
                int blockHeight = subSampleSize(ySize, sizeBits);
                RGBABuffer raster =
//                        Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, blockWidth, blockHeight, 4 * blockWidth, 4,
//                                new int[]{0, 1, 2, 3}, null);
                        RGBABuffer.createAbsoluteImage(blockWidth, blockHeight);
                readVP8Lossless(raster, false, blockWidth, blockHeight);

                // Keep data as raster for convenient (x,y) indexing
                if (transformType == PREDICTOR_TRANSFORM) {
                    transforms.add(0, new PredictorTransform(raster, sizeBits));
                } else {
                    transforms.add(0, new ColorTransform(raster, sizeBits));
                }

                break;
            }
            case SUBTRACT_GREEN_TRANSFORM: {
                // No data here
                transforms.add(0, new SubtractGreenTransform());
                break;
            }
            case COLOR_INDEXING_TRANSFORM: {
                // 8 bit value for color table size
                int colorTableSize = ((int) lsbBitReader.readBits(8)) + 1; // 1-256

                // If the index is equal or larger than color_table_size,
                // the argb color value should be set to 0x00000000
                // We handle this by allocating a possibly larger buffer
                int safeColorTableSize = colorTableSize > 16 ? 256 :
                        colorTableSize > 4 ? 16 :
                                colorTableSize > 2 ? 4 : 2;

                byte[] colorTable = new byte[safeColorTableSize * 4];

                // The color table can be obtained by reading an image,
                // without the RIFF header, image size, and transforms,
                // assuming a height of one pixel and a width of
                // color_table_size. The color table is always
                // subtraction-coded to reduce image entropy.
                RGBABuffer colorTableRGBABuffer = RGBABuffer.createAbsoluteImage(colorTableSize, 1);
                readVP8Lossless(
//                        Raster.createInterleavedRaster(
//                                new DataBufferByte(colorTable, colorTableSize * 4),
//                                colorTableSize, 1, colorTableSize * 4, 4, new int[]{0, 1, 2, 3}, null),
                        colorTableRGBABuffer,
                        false, colorTableSize, 1);

                byte[] colorTableRGBACopyBuffer = new byte[4];
                for (int x = 0; x < colorTableSize; x++) {
                    colorTableRGBABuffer.getDataElements(x, 0, colorTableRGBACopyBuffer);
                    System.arraycopy(colorTableRGBACopyBuffer, 0, colorTable, x * 4, 4);
                }

                // resolve subtraction code
                for (int i = 4; i < colorTable.length; i++) {
                    colorTable[i] += colorTable[i - 4];
                }

                // The number of pixels packed into each green sample (byte)
                byte widthBits = (byte) (colorTableSize > 16 ? 0 :
                        colorTableSize > 4 ? 1 :
                                colorTableSize > 2 ? 2 : 3);

                xSize = subSampleSize(xSize, widthBits);

                // The colors components are stored in ARGB order at 4*index, 4*index + 1, 4*index + 2, 4*index + 3
                // TODO: Can we use this to produce an image with IndexColorModel instead of expanding the values in-memory?
                transforms.add(0, new ColorIndexingTransform(colorTable, widthBits));

                break;
            }
            default:
                throw new AssertionError("Invalid transformType: " + transformType);
        }

        return xSize;
    }

    private HuffmanInfo readHuffmanCodes(int xSize, int ySize, int colorCacheBits, boolean readMetaCodes) throws IOException {
        int huffmanGroupNum = 1;
        int huffmanXSize;
        int huffmanYSize;

        int metaCodeBits = 0;

        RGBABuffer huffmanMetaCodes = null;

        if (readMetaCodes && lsbBitReader.readBit() == 1) {
            // read in meta codes
            metaCodeBits = (int) lsbBitReader.readBits(3) + 2;
            huffmanXSize = subSampleSize(xSize, metaCodeBits);
            huffmanYSize = subSampleSize(ySize, metaCodeBits);

            // Raster with elements as BARG (only the RG components encode the meta group)
            RGBABuffer packedRaster =
//                    Raster.createPackedRaster(DataBuffer.TYPE_INT, huffmanXSize, huffmanYSize, new int[]{0x0000ff00, 0x000000ff, 0xff000000, 0x00ff0000}, null);
                    RGBABuffer.createAbsoluteImage(huffmanXSize, huffmanYSize);
            readVP8Lossless(packedRaster, false, huffmanXSize, huffmanYSize);

//            int[] data = ((DataBufferInt) packedRaster.getDataBuffer()).getData();
            // Max metaGroup is number of meta groups
            int maxCode = Integer.MIN_VALUE;
//            for (int code : data) {
//                maxCode = Math.max(maxCode, code & 0xffff);
//            }
            for (int x = 0; x < packedRaster.getWidth(); x++) {
                for (int y = 0; y < packedRaster.getHeight(); y++) {
                    maxCode = Math.max(maxCode, packedRaster.getSample(x, y, 1));
                }
            }
            huffmanGroupNum = maxCode + 1;

            // New Raster with just RG components exposed as single band
            // allowing simple access of metaGroupIndex with x,y lookup
            huffmanMetaCodes =
//                    Raster.createPackedRaster(packedRaster.getDataBuffer(), huffmanXSize, huffmanYSize, huffmanXSize, new int[]{0xffff}, null);
                    packedRaster;
        }

        HuffmanCodeGroup[] huffmanGroups = new HuffmanCodeGroup[huffmanGroupNum];

        for (int i = 0; i < huffmanGroups.length; i++) {
            huffmanGroups[i] = new HuffmanCodeGroup(lsbBitReader, colorCacheBits);
        }

        return new HuffmanInfo(huffmanMetaCodes, metaCodeBits, huffmanGroups);
    }

    private static int subSampleSize(final int size, final int samplingBits) {
        return (size + (1 << samplingBits) - 1) >> samplingBits;
    }
}

/*
 * Copyright (c) 2022, Harald Kuhr
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

package net.burningtnt.webp.vp8l.transform;

import net.burningtnt.webp.utils.RGBABuffer;

/**
 * @author Simon Kammermeier
 */
public final class ColorTransform implements Transform {
    private final RGBABuffer data;
    private final byte bits;

    public ColorTransform(RGBABuffer raster, byte bits) {
        this.data = raster;
        this.bits = bits;
    }

    @Override
    public void apply(RGBABuffer raster) {
        int width = raster.getWidth();
        int height = raster.getHeight();

        byte[] rgba = new byte[4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data.getDataElements(x >> bits, y >> bits, rgba);
                int green_to_red = rgba[2];
                int green_to_blue = rgba[1];
                int red_to_blue = rgba[0];
                raster.getDataElements(x, y, rgba);

                int tmp_red = rgba[0];
                int tmp_blue = rgba[2];

                tmp_red += (byte) (((byte) green_to_red * rgba[1]) >> 5);
                tmp_blue += (byte) (((byte) green_to_blue * rgba[1]) >> 5);
                tmp_blue += (byte) (((byte) red_to_blue * (byte) tmp_red) >> 5); // Spec has red & 0xff

                rgba[0] = (byte) (tmp_red & 0xff);
                rgba[2] = (byte) (tmp_blue & 0xff);
                raster.setDataElements(x, y, rgba);
            }
        }
    }

    // A conversion from the 8-bit unsigned representation (uint8) to the 8-bit
    // signed one (int8) is required before calling ColorTransformDelta(). It
    // should be performed using 8-bit two's complement (that is: uint8 range
    // [128-255] is mapped to the [-128, -1] range of its converted int8
    // value).
    // private static byte colorTransformDelta(final byte t, final byte c) { return (byte) ((t * c) >> 5); }
}

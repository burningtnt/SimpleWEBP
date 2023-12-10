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
package net.burningtnt.webp.awt;

import net.burningtnt.webp.SimpleWEBPLoader;
import net.burningtnt.webp.utils.RGBABuffer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

public final class AWTImageLoader {
    private AWTImageLoader() {
    }

    public static BufferedImage decode(InputStream inputStream) throws IOException {
        RGBABuffer.AbsoluteRGBABuffer rgbaBuffer = SimpleWEBPLoader.decodeStreamByImageLoaders(inputStream);

        int width = rgbaBuffer.getWidth(), height = rgbaBuffer.getHeight();
        BufferedImage awtImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        WritableRaster writableRaster = awtImage.getRaster();
        byte[] rgbaData = rgbaBuffer.getRGBAData(), cache = new byte[4];
        for (int y = 0; y < height; y++) {
            int lineIndex = y * width * 4;
            for (int x = 0; x < width; x++) {
                System.arraycopy(rgbaData, lineIndex + x * 4, cache, 0, 4);
                writableRaster.setDataElements(x, y, cache);
            }
        }

        return awtImage;
    }
}

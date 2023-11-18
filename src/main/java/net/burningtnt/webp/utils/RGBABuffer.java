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

public abstract class RGBABuffer {
    protected final int w;
    protected final int h;

    protected RGBABuffer(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public static RGBABuffer.AbsoluteRGBABuffer createAbsoluteImage(int w, int h) {
        long n = ((long) w) * ((long) h) * 4L;
        if (n > Integer.MAX_VALUE || n < 0) {
            throw new IndexOutOfBoundsException("Image is too big.");
        }

        return new AbsoluteRGBABuffer(w, h);
    }

    public static RGBABuffer.RelativeRGBABuffer createRelativeImage(RGBABuffer parent, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || w < 0 || h < 0 || x > parent.w || y > parent.h || x + w > parent.w || y + h > parent.h) {
            throw new IndexOutOfBoundsException(String.format("Child Image (%d, %d, %d, %d) is out of Parent Image (%d, %d).", x, y, w, h, parent.w, parent.h));
        }

        return new RelativeRGBABuffer(x, y, w, h, parent);
    }

    public int getWidth() {
        return this.w;
    }

    public int getHeight() {
        return this.h;
    }

    protected final void checkBound(int x, int y) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of Image (%d, %d).", x, y, this.w, this.h));
        }
    }

    public abstract void getDataElements(int x, int y, byte[] rgba);

    public abstract void setDataElements(int x, int y, byte[] rgba);

    public abstract byte getSample(int x, int y, int sample);

    public static final class AbsoluteRGBABuffer extends RGBABuffer {
        private final byte[] rgbaData;
        private final int lineOffset;

        private AbsoluteRGBABuffer(int w, int h) {
            super(w, h);
            this.rgbaData = new byte[w * h * 4];
            this.lineOffset = this.w * 4;
        }

        public byte[] getRGBAData() {
            return this.rgbaData;
        }

        @Override
        public void getDataElements(int x, int y, byte[] rgba) {
            checkBound(x, y);
            System.arraycopy(this.rgbaData, this.lineOffset * y + x * 4, rgba, 0, 4);
        }

        @Override
        public void setDataElements(int x, int y, byte[] rgba) {
            checkBound(x, y);
            System.arraycopy(rgba, 0, this.rgbaData, this.lineOffset * y + x * 4, 4);
        }

        @Override
        public byte getSample(int x, int y, int sample) {
            checkBound(x, y);
            if (sample < 0 || sample > 3) {
                throw new IndexOutOfBoundsException(String.format("RGBA format doesn't contain sample %d.", sample));
            }
            return this.rgbaData[this.lineOffset * y + x * 4 + sample];
        }
    }

    public static final class RelativeRGBABuffer extends RGBABuffer {
        private final int x;
        private final int y;
        private final RGBABuffer parent;

        private RelativeRGBABuffer(int x, int y, int w, int h, RGBABuffer parent) {
            super(w, h);
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        @Override
        public void getDataElements(int x, int y, byte[] rgba) {
            this.parent.getDataElements(x + this.x, y + this.y, rgba);
        }

        @Override
        public void setDataElements(int x, int y, byte[] rgba) {
            this.parent.setDataElements(x + this.x, y + this.y, rgba);
        }

        @Override
        public byte getSample(int x, int y, int sample) {
            return this.parent.getSample(x + this.x, y + this.y, sample);
        }
    }
}

package net.burningtnt.webp.utils;

public abstract class RGBABuffer {
    protected final int x;
    protected final int y;
    protected final int w;
    protected final int h;

    protected RGBABuffer(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public static RGBABuffer createAbsoluteImage(int w, int h) {
        if (((long) w) * ((long) h) * 4L > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("Image is too big.");
        }

        return new AbsoluteRGBABuffer(0, 0, w, h);
    }

    public static RGBABuffer createRelativeImage(RGBABuffer parent, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || w < 0 || h < 0 || x > parent.w || y > parent.h || x + w > parent.w || y + h > parent.h) {
            throw new IndexOutOfBoundsException(String.format("Child Image (%d, %d, %d, %d) is out of Parent Image (%d, %d)", x, y, w, h, parent.w, parent.h));
        }

        return new RelativeRGBABuffer(x, y, w, h, parent);
    }

    public int getWidth() {
        return this.w;
    }

    public int getHeight() {
        return this.h;
    }

    public abstract byte[] getRGBAData();

    protected final void checkBound(int x, int y) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of Image (%d, %d)", x, y, this.w, this.h));
        }
    }

    public abstract void getDataElements(int x, int y, byte[] rgba);

    public abstract void setDataElements(int x, int y, byte[] rgba);

    public abstract byte getSample(int x, int y, int sample) throws UnsupportedOperationException;

    private static final class AbsoluteRGBABuffer extends RGBABuffer {
        private final byte[] rgbaData;

        public AbsoluteRGBABuffer(int x, int y, int w, int h) {
            super(x, y, w, h);
            rgbaData = new byte[w * h * 4];
        }

        @Override
        public byte[] getRGBAData() {
            return rgbaData;
        }

        @Override
        public void getDataElements(int x, int y, byte[] rgba) {
            checkBound(x, y);
            System.arraycopy(this.rgbaData, this.w * y * 4 + x * 4, rgba, 0, 4);
        }

        @Override
        public void setDataElements(int x, int y, byte[] rgba) {
            checkBound(x, y);
            System.arraycopy(rgba, 0, this.rgbaData, this.w * y * 4 + x * 4, 4);
        }

        @Override
        public byte getSample(int x, int y, int sample) {
            checkBound(x, y);
            return this.rgbaData[this.w * y * 4 + x * 4 + sample];
        }
    }

    private static final class RelativeRGBABuffer extends RGBABuffer {
        private final RGBABuffer parent;

        public RelativeRGBABuffer(int x, int y, int w, int h, RGBABuffer parent) {
            super(x, y, w, h);
            this.parent = parent;
        }

        @Override
        public byte[] getRGBAData() {
            throw new UnsupportedOperationException();
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

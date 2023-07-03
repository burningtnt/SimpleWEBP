package net.burningtnt.webp.utils;

import org.jetbrains.annotations.Nullable;

public final class RGBABuffer {
    private final int x;
    private final int y;
    private final int w;
    private final int h;

    /**
     * If current RGBABuffer is absolute, parent will be null.
     * Otherwise, it won't be null.
     * Therefore, parent and rgbaData should not be null (and should not be not-null) at the same time.
     */
    @Nullable
    private final RGBABuffer parent;

    /**
     * If current RGBABuffer is absolute, parent will be null.
     * Otherwise, it won't be null.
     * Therefore, parent and rgbaData should not be null (and should not be not-null) at the same time.
     */
    private final byte @Nullable [] rgbaData;

    private RGBABuffer(int x, int y, int w, int h, RGBABuffer parent) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        if (parent == null) {
            this.parent = null;
            rgbaData = new byte[w * h * 4];
        } else {
            this.parent = parent;
            rgbaData = null;
        }
    }

    public int getWidth() {
        return this.w;
    }

    public int getHeight() {
        return this.h;
    }

    public byte[] getRGBAData() {
        return this.rgbaData;
    }

    public static RGBABuffer createAbsoluteImage(int w, int h) {
        if (((long) w) * ((long) h) * 4L > Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException("Image is too big.");
        }

        return new RGBABuffer(0, 0, w, h, null);
    }

    public static RGBABuffer createChildImage(RGBABuffer parent, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || w < 0 || h < 0 || x > parent.w || y > parent.h || x + w > parent.w || y + h > parent.h) {
            throw new IndexOutOfBoundsException(String.format("Child Image (%d, %d, %d, %d) is out of Parent Image (%d, %d)", x, y, w, h, parent.w, parent.h));
        }

        return new RGBABuffer(x, y, w, h, parent);
    }

    @SuppressWarnings("ConstantConditions")
    public void getDataElements(int x, int y, byte[] rgba) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of Image (%d, %d)", x, y, this.w, this.h));
        }

        if (this.parent == null) {
            System.arraycopy(this.rgbaData, this.w * y * 4 + x * 4, rgba, 0, 4);
        } else {
            this.parent.getDataElements(x + this.x, y + this.y, rgba);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void setDataElements(int x, int y, byte[] rgba) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of Image (%d, %d)", x, y, this.w, this.h));
        }

        if (this.parent == null) {
            System.arraycopy(rgba, 0, this.rgbaData, this.w * y * 4 + x * 4, 4);
        } else {
            this.parent.setDataElements(x + this.x, y + this.y, rgba);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public byte getSample(int x, int y, int sample) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of Image (%d, %d)", x, y, this.w, this.h));
        }

        if (this.parent == null) {
            return this.rgbaData[this.w * y * 4 + x * 4 + sample];
        } else {
            return this.parent.getSample(x + this.x, y + this.y, sample);
        }
    }
}

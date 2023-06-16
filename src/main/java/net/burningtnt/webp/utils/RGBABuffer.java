package net.burningtnt.webp.utils;

public final class RGBABuffer {
    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final RGBABuffer parent;

    private final byte[] data;

    private RGBABuffer(int x, int y, int w, int h, RGBABuffer parent) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        if (parent == null) {
            this.parent = null;
            data = new byte[w * h * 4];
        } else {
            this.parent = parent;
            data = null;
        }
    }

    public static int getSampleAsIntFromWritableImage(RGBABuffer writableImage, int x, int y, int sample) {
        byte[] rgba = new byte[4];
        getDataElements(writableImage, x, y, rgba);
        return rgba[sample];
    }

    public int getWidth() {
        return this.w;
    }

    public int getHeight() {
        return this.h;
    }

    public byte[] getData() {
        return this.data;
    }

    public static RGBABuffer createAbsoluteImage(int w, int h) {
        return new RGBABuffer(0, 0, w, h, null);
    }

    public static RGBABuffer createChildImage(RGBABuffer parent, int x, int y, int w, int h) {
        return new RGBABuffer(x, y, w, h, parent);
    }

    public void getDataElements(int x, int y, byte[] rgba) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of (%d, %d)", x, y, this.w, this.h));
        }

        if (this.parent == null) {
            System.arraycopy(this.data, this.w * y * 4 + x * 4, rgba, 0, 4);
        } else {
            this.parent.getDataElements(x + this.x, y + this.y, rgba);
        }
    }

    public void setDataElements(int x, int y, byte[] rgba) {
        if (x < 0 || y < 0 || x >= this.w || y >= this.h) {
            throw new IndexOutOfBoundsException(String.format("Pixel (%d, %d) is out of (%d, %d)", x, y, this.w, this.h));
        }

        if (this.parent == null) {
            System.arraycopy(rgba, 0, this.data, this.w * y * 4 + x * 4, 4);
        } else {
            this.parent.setDataElements(x + this.x, y + this.y, rgba);
        }
    }

    public static void getDataElements(RGBABuffer rgbaBuffer, int x, int y, byte[] rgba) {
        rgbaBuffer.getDataElements(x, y, rgba);
    }

    public static void setDataElements(RGBABuffer rgbaBuffer, int x, int y, byte[] rgba) {
        rgbaBuffer.setDataElements(x, y, rgba);
    }
}
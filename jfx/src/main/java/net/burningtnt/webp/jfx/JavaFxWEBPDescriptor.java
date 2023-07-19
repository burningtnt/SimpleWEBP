package net.burningtnt.webp.jfx;

import com.sun.javafx.iio.common.ImageDescriptor;

public final class JavaFxWEBPDescriptor extends ImageDescriptor {
    private static final ImageDescriptor instance = new JavaFxWEBPDescriptor();

    private JavaFxWEBPDescriptor() {
        super("WEBP", new String[]{"webp"}, new Signature[]{new Signature(
                (byte) 'R', (byte) 'I', (byte) 'F', (byte) 'F'
        )});
    }

    public static ImageDescriptor getInstance() {
        return instance;
    }
}

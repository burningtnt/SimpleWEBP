package net.burningtnt.webp.jfx.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specific the methods that are implemented by Bytecode Implementation Generator
 * in order to adapt different JavaFX versions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface JavaFXAdapter {
    enum State {
        INVLUDED_BEFORE, INVLUDED_AFTER
    }

    State state();

    String commit();
}

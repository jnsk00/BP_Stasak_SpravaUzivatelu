package cz.Stasak.desktop.GUI;

import javafx.application.Platform;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFXInitializer {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    public static void init() {
        if (!initialized.get()) {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // JavaFX toolkit už běží – nevadí
            }
            initialized.set(true);
        }
    }
}

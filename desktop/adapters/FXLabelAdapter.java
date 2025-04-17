package cz.Stasak.desktop.adapters;

import cz.Stasak.shared.ui.LabelInterface;
import javafx.scene.control.Label;

public class FXLabelAdapter implements LabelInterface {

    private final Label label;

    public FXLabelAdapter(Label label) {
        this.label = label;
    }

    @Override
    public void setText(String text) {
        label.setText(text);
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setVisible(boolean value) {
        label.setVisible(value);
    }

    @Override
    public boolean isVisible() {
        return label.isVisible();
    }

    public Label getFXLabel() {
        return label;
    }
}

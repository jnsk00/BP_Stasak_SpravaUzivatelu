package testutils;

import cz.Stasak.shared.ui.LabelInterface;

public class FakeLabel implements LabelInterface {
    private String text = "";
    private boolean visible = false;

    @Override
    public void setText(String text) {
        this.text = text;
        this.visible = true;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}

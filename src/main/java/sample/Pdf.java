package sample;

import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;

public class Pdf extends File {

    private SimpleBooleanProperty horizontal;

    public Pdf(String s) {
        super(s);
        horizontal = new SimpleBooleanProperty(false);
    }

    public Pdf(File file) {
        super(file.getPath());
        this.horizontal = horizontal;
    }

    public boolean isHorizontal() {
        return horizontal.get();
    }

    public SimpleBooleanProperty horizontalProperty() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal.set(horizontal);
    }
}

package me.tracker.boot;

import java.beans.PropertyEditorSupport;


public class PasswordPropertyEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        setValue(new Password(text));
    }
}

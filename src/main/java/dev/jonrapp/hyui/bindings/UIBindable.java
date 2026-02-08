package dev.jonrapp.hyui.bindings;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UIBindable<T> {
    
    private T value;
    private final UIBindingManager bindingManager;
    private final String fieldName;
    
    public UIBindable(@Nonnull UIBindingManager bindingManager, @Nonnull String fieldName, @Nullable T initialValue) {
        this.bindingManager = bindingManager;
        this.fieldName = fieldName;
        this.value = initialValue;
    }
    
    @Nullable
    public T get() {
        return value;
    }
    
    public void set(@Nullable T newValue) {
        if (this.value != newValue && (this.value == null || !this.value.equals(newValue))) {
            this.value = newValue;
            bindingManager.notifyValueChanged(fieldName);
        }
    }

    public void set(@Nullable T newValue, UICommandBuilder commands) {
        if (this.value != newValue && (this.value == null || !this.value.equals(newValue))) {
            this.value = newValue;
            bindingManager.notifyValueChanged(fieldName, commands);
        }
    }
    
    @Nonnull
    public Message toMessage() {
        if (value == null) {
            return Message.raw("");
        }
        if (value instanceof Message) {
            return (Message) value;
        }
        return Message.raw(String.valueOf(value));
    }
    
    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}

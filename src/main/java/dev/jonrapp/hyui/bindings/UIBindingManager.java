package dev.jonrapp.hyui.bindings;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static dev.jonrapp.hyui.utils.UiUtils.selectors;

public class UIBindingManager {
    
    private final Map<String, BindingInfo> bindings = new HashMap<>();
    private final Consumer<UICommandBuilder> updateCallback;
    private String rootSelector = "";
    
    public UIBindingManager(@Nonnull Consumer<UICommandBuilder> updateCallback) {
        this.updateCallback = updateCallback;
    }
    
    public void setRootSelector(@Nonnull String rootSelector) {
        this.rootSelector = rootSelector;
    }
    
    public void scanAndBind(@Nonnull Object target) {
        Class<?> clazz = target.getClass();

        // init all annotated instance vars first
        initializeBindableFields(clazz);
        
        for (Field field : clazz.getDeclaredFields()) {
            UIBinding annotation = field.getAnnotation(UIBinding.class);
            if (annotation != null) {
                field.setAccessible(true);
                
                try {
                    Object fieldValue = field.get(target);
                    if (fieldValue instanceof UIBindable<?>) {
                        bindings.put(field.getName(), new BindingInfo(
                            field,
                            target,
                            annotation.selector(),
                            (UIBindable<?>) fieldValue
                        ));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }

    private void initializeBindableFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            UIBinding annotation = field.getAnnotation(UIBinding.class);
            if (annotation != null && UIBindable.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    UIBindable<?> bindable = new UIBindable<>(this, field.getName(), null);
                    field.set(this, bindable);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to initialize UIBindable field: " + field.getName(), e);
                }
            }
        }
    }
    
    public void notifyValueChanged(@Nonnull String fieldName) {
        notifyValueChanged(fieldName, null);
    }

    public void notifyValueChanged(@Nonnull String fieldName, UICommandBuilder commands) {
        BindingInfo binding = bindings.get(fieldName);
        if (binding != null) {
            if (commands == null) {
                // create a new command builder and send off update
                commands = new UICommandBuilder();
                applyBinding(commands, binding);
                updateCallback.accept(commands);
            } else {
                // use existing command builder
                applyBinding(commands, binding);
            }
        }
    }
    
    public void updateAll() {
        UICommandBuilder commands = new UICommandBuilder();
        
        for (BindingInfo binding : bindings.values()) {
            applyBinding(commands, binding);
        }
        
        updateCallback.accept(commands);
    }
    
    private void applyBinding(@Nonnull UICommandBuilder commands, @Nonnull BindingInfo binding) {
        Message message = binding.bindable.toMessage();
        String fullSelector = buildSelector(binding.selector);
        commands.set(fullSelector, message);
    }
    
    @Nonnull
    private String buildSelector(@Nonnull String selector) {
        if (rootSelector.isEmpty()) {
            return selector;
        }
        return selectors(rootSelector, selector);
    }

    private record BindingInfo(Field field, Object target, String selector, UIBindable<?> bindable) {
            private BindingInfo(@Nonnull Field field, @Nonnull Object target, @Nonnull String selector,
                                @Nonnull UIBindable<?> bindable) {
                this.field = field;
                this.target = target;
                this.selector = selector;
                this.bindable = bindable;
            }
        }
}

package dev.jonrapp.hyui.events;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import dev.jonrapp.hyui.elements.Element;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventBinding {

    private final String action;
    private final List<KeyedCodec<?>> parameterCodecs = new ArrayList<>();
    private final Map<String, String> eventData = new HashMap<>();
    private Consumer<EventRouter.EventContext> handler;
    private Predicate<EventRouter.EventContext> conditionalHandler;

    private EventBinding(@Nonnull String action) {
        this.action = action;
    }

    @Nonnull
    public static EventBinding action(@Nonnull String action) {
        return new EventBinding(action);
    }

    @Nonnull
    public <T> EventBinding withEventData(@Nonnull String key, @Nonnull Codec<T> codec, @Nonnull String value) {
        parameterCodecs.add(new KeyedCodec<>(key, codec));
        eventData.put(key, value);
        return this;
    }

    @Nonnull
    public EventBinding onEvent(@Nonnull Consumer<EventRouter.EventContext> handler) {
        this.handler = handler;
        return this;
    }

    @Nonnull
    public EventBinding onEventConditional(@Nonnull Predicate<EventRouter.EventContext> handler) {
        this.conditionalHandler = handler;
        return this;
    }

    public void bindTo(@Nonnull CustomUIEventBindingType bindingType, @Nonnull String selector,
                       @Nonnull UIEventBuilder events, @Nonnull Element element) {
        EventHandler<?> eventHandler = createHandler();
        element.registerEventHandler(action, eventHandler);

        EventData data = EventData.of("Action", action);
        this.eventData.entrySet().forEach((entry) -> {
            data.append(entry.getKey(), entry.getValue());
        });

        events.addEventBinding(bindingType, selector, data);
    }

    @Nonnull
    private EventHandler<?> createHandler() {
        return new EventHandler<Void>() {
            @Override
            public boolean handle(@Nonnull EventRouter.EventContext context) {
                if (conditionalHandler != null) {
                    return conditionalHandler.test(context);
                } else if (handler != null) {
                    handler.accept(context);
                    return true;
                }
                return false;
            }

            @Nonnull
            @Override
            public List<KeyedCodec<?>> getParameterCodecs() {
                return parameterCodecs;
            }
        };
    }

    @Nonnull
    public String getAction() {
        return action;
    }

    @Nonnull
    public Map<String, String> getEventData() {
        return eventData;
    }

    @Nonnull
    public EventHandler<?> createHandlerForPage() {
        return createHandler();
    }
}

package dev.jonrapp.hyui.events;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.HashMap;
import java.util.Map;

public class EventCodec {

    private final Map<String, KeyedCodec> eventMappings = new HashMap<>();

    protected BuilderCodec.Builder<EventData> eventDataCodecBuilder;
    protected BuilderCodec<EventData> eventDataCodec;
    private boolean eventsHaveChanged = true; // init to true to ensure codec is built on first use

    public void register(KeyedCodec keyedCodec) {
        eventMappings.put(keyedCodec.getKey(), keyedCodec);
        eventsHaveChanged = true;
    }

    public void unregister(String key) {
        eventMappings.remove(key);
        eventsHaveChanged = true;
    }

    public BuilderCodec<EventData> getEventCodec() {
        // build the new codec if it's changed
        if (eventsHaveChanged) {
            eventsHaveChanged = false;

            BuilderCodec.Builder<EventData> builder = BuilderCodec.builder(EventData.class, EventData::new);
            eventMappings.forEach((key, codec) -> {
                builder.append(codec,
                        (data, v) -> data.put(key, v),
                        data -> data.get(key)
                ).add();
            });
            eventDataCodec = builder.build();
        }

        // return the codec
        return eventDataCodec;
    }

    public static class EventData {
        private Map<String, Object> values = new HashMap<>();

        public void put(String key, Object value) {
            values.put(key, value);
        }

        public <T> T get(String key) {
            return (T) values.get(key);
        }

        public boolean contains(String key) {
            return values.containsKey(key);
        }
    }

}

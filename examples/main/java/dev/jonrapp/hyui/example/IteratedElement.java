package dev.jonrapp.hyui.example;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import dev.jonrapp.hyui.bindings.UIBindable;
import dev.jonrapp.hyui.bindings.UIBinding;
import dev.jonrapp.hyui.elements.Element;

public class IteratedElement extends Element<ExamplePage> {

    private final int index;

    @UIBinding(selector = "#ElementIndex.TextSpans")
    private UIBindable<String> elementIndex;

    public IteratedElement(ExamplePage pageRef, int index) {
        super(pageRef);
        this.index = index;
    }

    @Override
    protected void onCreate(String root, UICommandBuilder commands, UIEventBuilder events) {
        commands.append(root, "Example/IteratedElement.ui");

        elementIndex.set(String.valueOf(index), commands);
    }
}

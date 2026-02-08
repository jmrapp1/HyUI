package dev.jonrapp.hyui.example;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import dev.jonrapp.hyui.elements.Element;
import dev.jonrapp.hyui.events.EventBinding;

public class Tab3 extends Element<ExamplePage> {

    public Tab3(ExamplePage pageRef) {
        super(pageRef);
    }

    @Override
    protected void onCreate(String root, UICommandBuilder commands, UIEventBuilder events) {
        commands.append(root, "Example/Tab3.ui");

        bindEvent(
                CustomUIEventBindingType.Activating,
                "#BackBtn",
                events,
                EventBinding.action("tab-3-back-btn-clicked")
                        .onEvent(context -> pageRef.showPrimaryElement(new Tab2(pageRef)))
        );
    }
}

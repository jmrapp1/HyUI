package dev.jonrapp.hyui.elements;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import dev.jonrapp.hyui.bindings.UIBindingManager;
import dev.jonrapp.hyui.events.EventBinding;
import dev.jonrapp.hyui.events.EventHandler;
import dev.jonrapp.hyui.events.EventRouter;
import dev.jonrapp.hyui.HyUIPage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static dev.jonrapp.hyui.utils.UiUtils.getArraySelector;
import static dev.jonrapp.hyui.utils.UiUtils.selectors;

public abstract class Element {

    protected final HyUIPage pageRef;
    private final List<EventRouter.EventHandlerRegistration> eventRegistrations = new ArrayList<>();
    private UIBindingManager bindingManager;

    public Element(HyUIPage pageRef) {
        this.pageRef = pageRef;
        this.bindingManager = new UIBindingManager(commands -> sendUpdate(commands, false));
        bindingManager.scanAndBind(this);
    }

    public void create(String root) {
        bindingManager.setRootSelector(root);
        
        UICommandBuilder commands = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();
        onCreate(root, commands, events);
        sendUpdate(commands, events, false);
    }

    public void create(String root, int index, UICommandBuilder commands, UIEventBuilder events) {
        String itemContainerSelector = getArraySelector("#" + getElementSelectorId(), index);
        commands.appendInline(root, "Group " + itemContainerSelector + " { } ");
        String itemRoot = selectors(root, itemContainerSelector);

        bindingManager.setRootSelector(itemRoot);
        onCreate(itemRoot, commands, events);
    }

    protected abstract void onCreate(String root, UICommandBuilder commands, UIEventBuilder events);

    public void onUnload() {
        for (EventRouter.EventHandlerRegistration registration : eventRegistrations) {
            pageRef.getEventRouter().unregisterHandler(registration);
        }
        eventRegistrations.clear();
    }

    public <T> void registerEventHandler(@Nonnull String action, @Nonnull EventHandler<T> handler) {
        EventRouter.EventHandlerRegistration registration = pageRef.getEventRouter().registerHandler(action, this, handler);
        eventRegistrations.add(registration);
    }

    protected void bindEvent(@Nonnull CustomUIEventBindingType bindingType, @Nonnull String selector,
                             @Nonnull UIEventBuilder events, @Nonnull EventBinding eventBinding) {
        eventBinding.bindTo(bindingType, selector, events, this);
    }

    protected void sendUpdate(UICommandBuilder commands, boolean clear) {
        pageRef.sendUpdate(commands, clear);
    }

    protected void sendUpdate(UICommandBuilder commands, UIEventBuilder events, boolean clear) {
        pageRef.sendUpdate(commands, events, clear);
    }

    public String getElementSelectorId() {
        return this.getClass().getSimpleName();
    }

}

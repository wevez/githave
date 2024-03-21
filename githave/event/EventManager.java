package githave.event;

import com.google.common.collect.Lists;

import java.util.List;

public class EventManager {

    private final List<EventListener> LISTENER_REGISTRY;

    public EventManager() {
        super();
        this.LISTENER_REGISTRY = Lists.newArrayList();
    }

    public void call(final EventArgument argument) {
        this.LISTENER_REGISTRY.forEach(argument::call);
    }

    public void register(final EventListener listener) {
        this.LISTENER_REGISTRY.add(listener);
    }

    public boolean unregister(final EventListener listener) {
        return this.LISTENER_REGISTRY.remove(listener);
    }
}

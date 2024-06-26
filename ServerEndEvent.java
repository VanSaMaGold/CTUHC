package org.vansama.ctuhc;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public ServerEndEvent() {
        super();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // ... 其他方法 ...
}
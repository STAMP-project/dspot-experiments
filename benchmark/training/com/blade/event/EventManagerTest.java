package com.blade.event;


import EventType.SERVER_STARTED;
import com.blade.Blade;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class EventManagerTest {
    @Test
    public void testManager() {
        EventManager eventManager = new EventManager();
        eventManager.addEventListener(SERVER_STARTED, ( b) -> System.out.println("server started"));
        eventManager.fireEvent(SERVER_STARTED, new Event().attribute("blade", Blade.of()));
    }
}


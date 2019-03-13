/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.eda.framework;


import com.iluwatar.eda.event.UserCreatedEvent;
import com.iluwatar.eda.event.UserUpdatedEvent;
import com.iluwatar.eda.handler.UserCreatedEventHandler;
import com.iluwatar.eda.handler.UserUpdatedEventHandler;
import com.iluwatar.eda.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Event Dispatcher unit tests to assert and verify correct event dispatcher behaviour
 */
public class EventDispatcherTest {
    /**
     * This unit test should register events and event handlers correctly with the event dispatcher
     * and events should be dispatched accordingly.
     */
    @Test
    public void testEventDriverPattern() {
        EventDispatcher dispatcher = Mockito.spy(new EventDispatcher());
        UserCreatedEventHandler userCreatedEventHandler = Mockito.spy(new UserCreatedEventHandler());
        UserUpdatedEventHandler userUpdatedEventHandler = Mockito.spy(new UserUpdatedEventHandler());
        dispatcher.registerHandler(UserCreatedEvent.class, userCreatedEventHandler);
        dispatcher.registerHandler(UserUpdatedEvent.class, userUpdatedEventHandler);
        User user = new User("iluwatar");
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(user);
        UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent(user);
        // fire a userCreatedEvent and verify that userCreatedEventHandler has been invoked.
        dispatcher.dispatch(userCreatedEvent);
        Mockito.verify(userCreatedEventHandler).onEvent(userCreatedEvent);
        Mockito.verify(dispatcher).dispatch(userCreatedEvent);
        // fire a userCreatedEvent and verify that userUpdatedEventHandler has been invoked.
        dispatcher.dispatch(userUpdatedEvent);
        Mockito.verify(userUpdatedEventHandler).onEvent(userUpdatedEvent);
        Mockito.verify(dispatcher).dispatch(userUpdatedEvent);
    }
}


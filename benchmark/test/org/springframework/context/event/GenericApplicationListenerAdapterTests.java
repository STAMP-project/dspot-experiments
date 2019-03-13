/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.context.event;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class GenericApplicationListenerAdapterTests extends AbstractApplicationEventListenerTests {
    @Test
    public void supportsEventTypeWithSmartApplicationListener() {
        SmartApplicationListener smartListener = Mockito.mock(SmartApplicationListener.class);
        GenericApplicationListenerAdapter listener = new GenericApplicationListenerAdapter(smartListener);
        ResolvableType type = ResolvableType.forClass(ApplicationEvent.class);
        listener.supportsEventType(type);
        Mockito.verify(smartListener, Mockito.times(1)).supportsEventType(ApplicationEvent.class);
    }

    @Test
    public void supportsSourceTypeWithSmartApplicationListener() {
        SmartApplicationListener smartListener = Mockito.mock(SmartApplicationListener.class);
        GenericApplicationListenerAdapter listener = new GenericApplicationListenerAdapter(smartListener);
        listener.supportsSourceType(Object.class);
        Mockito.verify(smartListener, Mockito.times(1)).supportsSourceType(Object.class);
    }

    @Test
    public void genericListenerStrictType() {
        supportsEventType(true, AbstractApplicationEventListenerTests.StringEventListener.class, getGenericApplicationEventType("stringEvent"));
    }

    // Demonstrates we can't inject that event because the generic type is lost
    @Test
    public void genericListenerStrictTypeTypeErasure() {
        AbstractApplicationEventListenerTests.GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
        ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
        supportsEventType(false, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    // But it works if we specify the type properly
    @Test
    public void genericListenerStrictTypeAndResolvableType() {
        ResolvableType eventType = ResolvableType.forClassWithGenerics(AbstractApplicationEventListenerTests.GenericTestEvent.class, String.class);
        supportsEventType(true, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    // or if the event provides its precise type
    @Test
    public void genericListenerStrictTypeAndResolvableTypeProvider() {
        ResolvableType eventType = new AbstractApplicationEventListenerTests.SmartGenericTestEvent<>(this, "foo").getResolvableType();
        supportsEventType(true, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    // Demonstrates it works if we actually use the subtype
    @Test
    public void genericListenerStrictTypeEventSubType() {
        AbstractApplicationEventListenerTests.StringEvent stringEvent = new AbstractApplicationEventListenerTests.StringEvent(this, "test");
        ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
        supportsEventType(true, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    @Test
    public void genericListenerStrictTypeNotMatching() {
        supportsEventType(false, AbstractApplicationEventListenerTests.StringEventListener.class, getGenericApplicationEventType("longEvent"));
    }

    @Test
    public void genericListenerStrictTypeEventSubTypeNotMatching() {
        AbstractApplicationEventListenerTests.LongEvent stringEvent = new AbstractApplicationEventListenerTests.LongEvent(this, 123L);
        ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
        supportsEventType(false, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    @Test
    public void genericListenerStrictTypeNotMatchTypeErasure() {
        AbstractApplicationEventListenerTests.GenericTestEvent<Long> longEvent = createGenericTestEvent(123L);
        ResolvableType eventType = ResolvableType.forType(longEvent.getClass());
        supportsEventType(false, AbstractApplicationEventListenerTests.StringEventListener.class, eventType);
    }

    @Test
    public void genericListenerStrictTypeSubClass() {
        supportsEventType(false, AbstractApplicationEventListenerTests.ObjectEventListener.class, getGenericApplicationEventType("longEvent"));
    }

    @Test
    public void genericListenerUpperBoundType() {
        supportsEventType(true, AbstractApplicationEventListenerTests.UpperBoundEventListener.class, getGenericApplicationEventType("illegalStateExceptionEvent"));
    }

    @Test
    public void genericListenerUpperBoundTypeNotMatching() {
        supportsEventType(false, AbstractApplicationEventListenerTests.UpperBoundEventListener.class, getGenericApplicationEventType("ioExceptionEvent"));
    }

    @Test
    public void genericListenerWildcardType() {
        supportsEventType(true, AbstractApplicationEventListenerTests.GenericEventListener.class, getGenericApplicationEventType("stringEvent"));
    }

    // Demonstrates we cant inject that event because the listener has a wildcard
    @Test
    public void genericListenerWildcardTypeTypeErasure() {
        AbstractApplicationEventListenerTests.GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
        ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
        supportsEventType(true, AbstractApplicationEventListenerTests.GenericEventListener.class, eventType);
    }

    @Test
    public void genericListenerRawType() {
        supportsEventType(true, AbstractApplicationEventListenerTests.RawApplicationListener.class, getGenericApplicationEventType("stringEvent"));
    }

    // Demonstrates we cant inject that event because the listener has a raw type
    @Test
    public void genericListenerRawTypeTypeErasure() {
        AbstractApplicationEventListenerTests.GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
        ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
        supportsEventType(true, AbstractApplicationEventListenerTests.RawApplicationListener.class, eventType);
    }
}


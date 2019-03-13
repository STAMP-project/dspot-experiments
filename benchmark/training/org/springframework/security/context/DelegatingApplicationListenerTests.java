/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.context;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;


@RunWith(MockitoJUnitRunner.class)
public class DelegatingApplicationListenerTests {
    @Mock
    SmartApplicationListener delegate;

    ApplicationEvent event;

    DelegatingApplicationListener listener;

    @Test
    public void processEventNull() {
        listener.onApplicationEvent(null);
        Mockito.verify(delegate, Mockito.never()).onApplicationEvent(ArgumentMatchers.any(ApplicationEvent.class));
    }

    @Test
    public void processEventSuccess() {
        Mockito.when(delegate.supportsEventType(event.getClass())).thenReturn(true);
        Mockito.when(delegate.supportsSourceType(event.getSource().getClass())).thenReturn(true);
        listener.onApplicationEvent(event);
        Mockito.verify(delegate).onApplicationEvent(event);
    }

    @Test
    public void processEventEventTypeNotSupported() {
        listener.onApplicationEvent(event);
        Mockito.verify(delegate, Mockito.never()).onApplicationEvent(ArgumentMatchers.any(ApplicationEvent.class));
    }

    @Test
    public void processEventSourceTypeNotSupported() {
        Mockito.when(delegate.supportsEventType(event.getClass())).thenReturn(true);
        listener.onApplicationEvent(event);
        Mockito.verify(delegate, Mockito.never()).onApplicationEvent(ArgumentMatchers.any(ApplicationEvent.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNull() {
        listener.addListener(null);
    }
}


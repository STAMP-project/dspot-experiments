/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.creation.bytebuddy;


import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.creation.settings.CreationSettings;
import org.mockito.internal.handler.MockHandlerImpl;
import org.mockitoutil.TestBase;


public class ByteBuddyMockMakerTest extends TestBase {
    @InjectMocks
    private ByteBuddyMockMaker mockMaker = new ByteBuddyMockMaker();

    @Mock
    private ClassCreatingMockMaker delegate;

    @Test
    public void should_delegate_call() {
        CreationSettings<Object> creationSettings = new CreationSettings<Object>();
        MockHandlerImpl<Object> handler = new MockHandlerImpl<Object>(creationSettings);
        mockMaker.createMockType(creationSettings);
        mockMaker.createMock(creationSettings, handler);
        mockMaker.getHandler(this);
        mockMaker.isTypeMockable(Object.class);
        mockMaker.resetMock(this, handler, creationSettings);
        Mockito.verify(delegate).createMock(creationSettings, handler);
        Mockito.verify(delegate).createMockType(creationSettings);
        Mockito.verify(delegate).getHandler(this);
        Mockito.verify(delegate).isTypeMockable(Object.class);
        Mockito.verify(delegate).resetMock(this, handler, creationSettings);
    }
}


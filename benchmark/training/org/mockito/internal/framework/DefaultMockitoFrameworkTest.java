/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.framework;


import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.RedundantListenerException;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.listeners.MockCreationListener;
import org.mockito.listeners.MockitoListener;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.InlineMockMaker;
import org.mockitoutil.TestBase;
import org.mockitoutil.ThrowableAssert;


public class DefaultMockitoFrameworkTest extends TestBase {
    private DefaultMockitoFramework framework = new DefaultMockitoFramework();

    @Test(expected = IllegalArgumentException.class)
    public void prevents_adding_null_listener() {
        framework.addListener(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prevents_removing_null_listener() {
        framework.removeListener(null);
    }

    @Test
    public void ok_to_remove_unknown_listener() {
        // it is safe to remove listener that was not added before
        framework.removeListener(new MockitoListener() {});
    }

    @Test
    public void ok_to_remove_listener_multiple_times() {
        MockitoListener listener = new MockitoListener() {};
        // when
        framework.addListener(listener);
        // then it is ok to:
        framework.removeListener(listener);
        framework.removeListener(listener);
    }

    @Test
    public void adds_creation_listener() {
        // given creation listener is added
        MockCreationListener listener = Mockito.mock(MockCreationListener.class);
        framework.addListener(listener);
        // when
        MockSettings settings = Mockito.withSettings().name("my list");
        List mock = Mockito.mock(List.class, settings);
        Set mock2 = Mockito.mock(Set.class);
        // then
        Mockito.verify(listener).onMockCreated(ArgumentMatchers.eq(mock), ArgumentMatchers.any(MockCreationSettings.class));
        Mockito.verify(listener).onMockCreated(ArgumentMatchers.eq(mock2), ArgumentMatchers.any(MockCreationSettings.class));
        Mockito.verifyNoMoreInteractions(listener);
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void removes_creation_listener() {
        // given creation listener is added
        MockCreationListener listener = Mockito.mock(MockCreationListener.class);
        framework.addListener(listener);
        // and hooked up correctly
        Mockito.mock(List.class);
        Mockito.verify(listener).onMockCreated(ArgumentMatchers.any(), ArgumentMatchers.any(MockCreationSettings.class));
        // when
        framework.removeListener(listener);
        Mockito.mock(Set.class);
        // then
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void prevents_duplicate_listeners_of_the_same_type() {
        // given creation listener is added
        framework.addListener(new DefaultMockitoFrameworkTest.MyListener());
        ThrowableAssert.assertThat(new Runnable() {
            @Override
            public void run() {
                framework.addListener(new DefaultMockitoFrameworkTest.MyListener());
            }
        }).throwsException(RedundantListenerException.class).throwsMessage(("\n" + ((((("Problems adding Mockito listener.\n" + "Listener of type \'MyListener\' has already been added and not removed.\n") + "It indicates that previous listener was not removed according to the API.\n") + "When you add a listener, don\'t forget to remove the listener afterwards:\n") + "  Mockito.framework().removeListener(myListener);\n") + "For more information, see the javadoc for RedundantListenerException class.")));
    }

    @Test
    public void clearing_all_mocks_is_safe_regardless_of_mock_maker_type() {
        List mock = Mockito.mock(List.class);
        // expect
        Assert.assertTrue(Mockito.mockingDetails(mock).isMock());
        clearInlineMocks();
    }

    @Test
    public void clears_all_mocks() {
        // clearing mocks only works with inline mocking
        Assume.assumeTrue(((Plugins.getMockMaker()) instanceof InlineMockMaker));
        // given
        List list1 = Mockito.mock(List.class);
        Assert.assertTrue(Mockito.mockingDetails(list1).isMock());
        List list2 = Mockito.mock(List.class);
        Assert.assertTrue(Mockito.mockingDetails(list2).isMock());
        // when
        clearInlineMocks();
        // then
        Assert.assertFalse(Mockito.mockingDetails(list1).isMock());
        Assert.assertFalse(Mockito.mockingDetails(list2).isMock());
    }

    @Test
    public void clears_mock() {
        // clearing mocks only works with inline mocking
        Assume.assumeTrue(((Plugins.getMockMaker()) instanceof InlineMockMaker));
        // given
        List list1 = Mockito.mock(List.class);
        Assert.assertTrue(Mockito.mockingDetails(list1).isMock());
        List list2 = Mockito.mock(List.class);
        Assert.assertTrue(Mockito.mockingDetails(list2).isMock());
        // when
        clearInlineMock(list1);
        // then
        Assert.assertFalse(Mockito.mockingDetails(list1).isMock());
        Assert.assertTrue(Mockito.mockingDetails(list2).isMock());
    }

    private static class MyListener implements MockitoListener {}
}


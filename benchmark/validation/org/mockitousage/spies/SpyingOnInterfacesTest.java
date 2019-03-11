/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.spies;


import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitoutil.TestBase;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Default.WRAPPER;


@SuppressWarnings({ "unchecked" })
public class SpyingOnInterfacesTest extends TestBase {
    @Test
    public void shouldFailFastWhenCallingRealMethodOnInterface() throws Exception {
        List<?> list = Mockito.mock(List.class);
        try {
            // when
            Mockito.when(list.get(0)).thenCallRealMethod();
            // then
            Assert.fail();
        } catch (MockitoException e) {
        }
    }

    @Test
    public void shouldFailInRuntimeWhenCallingRealMethodOnInterface() throws Exception {
        // given
        List<Object> list = Mockito.mock(List.class);
        Mockito.when(list.get(0)).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.callRealMethod();
            }
        });
        try {
            // when
            list.get(0);
            // then
            Assert.fail();
        } catch (MockitoException e) {
        }
    }

    @Test
    public void shouldAllowDelegatingToDefaultMethod() throws Exception {
        Assume.assumeTrue("Test can only be executed on Java 8 capable VMs", ClassFileVersion.ofThisVm().isAtLeast(ClassFileVersion.JAVA_V8));
        Class<?> type = new ByteBuddy().makeInterface().defineMethod("foo", String.class, Visibility.PUBLIC).intercept(FixedValue.value("bar")).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        Object object = Mockito.mock(type);
        // when
        Mockito.when(type.getMethod("foo").invoke(object)).thenCallRealMethod();
        // then
        Assertions.assertThat(type.getMethod("foo").invoke(object)).isEqualTo(((Object) ("bar")));
        type.getMethod("foo").invoke(Mockito.verify(object));
    }

    @Test
    public void shouldAllowSpyingOnDefaultMethod() throws Exception {
        Assume.assumeTrue("Test can only be executed on Java 8 capable VMs", ClassFileVersion.ofThisVm().isAtLeast(ClassFileVersion.JAVA_V8));
        Class<?> iFace = new ByteBuddy().makeInterface().defineMethod("foo", String.class, Visibility.PUBLIC).intercept(FixedValue.value("bar")).make().load(getClass().getClassLoader(), WRAPPER).getLoaded();
        Class<?> impl = new ByteBuddy().subclass(iFace).make().load(iFace.getClassLoader(), WRAPPER).getLoaded();
        Object object = Mockito.spy(impl.newInstance());
        // when
        Assertions.assertThat(impl.getMethod("foo").invoke(object)).isEqualTo(((Object) ("bar")));
        // then
        impl.getMethod("foo").invoke(Mockito.verify(object));
    }
}


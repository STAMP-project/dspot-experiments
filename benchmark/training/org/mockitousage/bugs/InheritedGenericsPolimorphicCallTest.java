/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 200
@SuppressWarnings("unchecked")
public class InheritedGenericsPolimorphicCallTest extends TestBase {
    protected interface MyIterable<T> extends Iterable<T> {
        InheritedGenericsPolimorphicCallTest.MyIterator<T> iterator();
    }

    // adds nothing here
    protected interface MyIterator<T> extends Iterator<T> {}

    InheritedGenericsPolimorphicCallTest.MyIterator<String> myIterator = Mockito.mock(InheritedGenericsPolimorphicCallTest.MyIterator.class);

    InheritedGenericsPolimorphicCallTest.MyIterable<String> iterable = Mockito.mock(InheritedGenericsPolimorphicCallTest.MyIterable.class);

    @Test
    public void shouldStubbingWork() {
        Mockito.when(iterable.iterator()).thenReturn(myIterator);
        Assert.assertNotNull(((Iterable<String>) (iterable)).iterator());
        Assert.assertNotNull(iterable.iterator());
    }

    @Test
    public void shouldVerificationWorks() {
        iterable.iterator();
        Mockito.verify(iterable).iterator();
        Mockito.verify(((Iterable<String>) (iterable))).iterator();
    }

    @Test
    public void shouldWorkExactlyAsJavaProxyWould() {
        // given
        final List<Method> methods = new LinkedList<Method>();
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                methods.add(method);
                return null;
            }
        };
        iterable = ((InheritedGenericsPolimorphicCallTest.MyIterable<String>) (Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{ InheritedGenericsPolimorphicCallTest.MyIterable.class }, handler)));
        // when
        iterable.iterator();
        ((Iterable<String>) (iterable)).iterator();
        // then
        Assert.assertEquals(2, methods.size());
        Assert.assertEquals(methods.get(0), methods.get(1));
    }
}


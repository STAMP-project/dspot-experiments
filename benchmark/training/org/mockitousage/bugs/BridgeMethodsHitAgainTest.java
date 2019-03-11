/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see issue 101
public class BridgeMethodsHitAgainTest extends TestBase {
    public interface Factory {}

    public interface ExtendedFactory extends BridgeMethodsHitAgainTest.Factory {}

    public interface SomeInterface {
        BridgeMethodsHitAgainTest.Factory factory();
    }

    public interface SomeSubInterface extends BridgeMethodsHitAgainTest.SomeInterface {
        BridgeMethodsHitAgainTest.ExtendedFactory factory();
    }

    public interface Base<T extends Serializable> {
        int test(T value);
    }

    public interface Extended extends BridgeMethodsHitAgainTest.Base<String> {
        @Override
        int test(String value);
    }

    @Mock
    BridgeMethodsHitAgainTest.SomeSubInterface someSubInterface;

    @Mock
    BridgeMethodsHitAgainTest.ExtendedFactory extendedFactory;

    @Test
    public void basicCheck() {
        Mockito.when(someSubInterface.factory()).thenReturn(extendedFactory);
        BridgeMethodsHitAgainTest.SomeInterface si = someSubInterface;
        Assert.assertTrue(((si.factory()) != null));
    }

    @Test
    public void checkWithExtraCast() {
        Mockito.when(((BridgeMethodsHitAgainTest.SomeInterface) (someSubInterface)).factory()).thenReturn(extendedFactory);
        BridgeMethodsHitAgainTest.SomeInterface si = someSubInterface;
        Assert.assertTrue(((si.factory()) != null));
    }

    @Test
    public void testBridgeInvocationIsRecordedForInterceptedMethod() {
        BridgeMethodsHitAgainTest.Extended ext = Mockito.mock(BridgeMethodsHitAgainTest.Extended.class);
        ext.test("123");
        Mockito.verify(ext).test("123");
        ((BridgeMethodsHitAgainTest.Base<String>) (ext)).test("456");
        Mockito.verify(ext).test("456");
    }
}


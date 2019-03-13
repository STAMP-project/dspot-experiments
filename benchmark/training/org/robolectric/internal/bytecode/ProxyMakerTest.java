package org.robolectric.internal.bytecode;


import ProxyMaker.MethodMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ProxyMakerTest {
    private static final MethodMapper IDENTITY_NAME = new ProxyMaker.MethodMapper() {
        @Override
        public String getName(String className, String methodName) {
            return methodName;
        }
    };

    @Test
    public void proxyCall() {
        ProxyMaker maker = new ProxyMaker(ProxyMakerTest.IDENTITY_NAME);
        ProxyMakerTest.Thing mock = Mockito.mock(ProxyMakerTest.Thing.class);
        ProxyMakerTest.Thing proxy = maker.createProxyFactory(ProxyMakerTest.Thing.class).createProxy(ProxyMakerTest.Thing.class, mock);
        assertThat(proxy.getClass()).isNotSameAs(ProxyMakerTest.Thing.class);
        proxy.returnNothing();
        Mockito.verify(mock).returnNothing();
        Mockito.when(mock.returnInt()).thenReturn(42);
        assertThat(proxy.returnInt()).isEqualTo(42);
        Mockito.verify(mock).returnInt();
        proxy.argument("hello");
        Mockito.verify(mock).argument("hello");
    }

    @Test
    public void cachesProxyClass() {
        ProxyMaker maker = new ProxyMaker(ProxyMakerTest.IDENTITY_NAME);
        ProxyMakerTest.Thing thing1 = Mockito.mock(ProxyMakerTest.Thing.class);
        ProxyMakerTest.Thing thing2 = Mockito.mock(ProxyMakerTest.Thing.class);
        ProxyMakerTest.Thing proxy1 = maker.createProxy(ProxyMakerTest.Thing.class, thing1);
        ProxyMakerTest.Thing proxy2 = maker.createProxy(ProxyMakerTest.Thing.class, thing2);
        assertThat(proxy1.getClass()).isSameAs(proxy2.getClass());
    }

    public static class Thing {
        public Thing() {
            throw new UnsupportedOperationException();
        }

        public void returnNothing() {
            throw new UnsupportedOperationException();
        }

        public int returnInt() {
            throw new UnsupportedOperationException();
        }

        public void argument(String arg) {
            throw new UnsupportedOperationException();
        }
    }
}


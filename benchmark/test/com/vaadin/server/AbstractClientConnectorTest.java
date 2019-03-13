package com.vaadin.server;


import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.FieldRpc.BlurServerRpc;
import com.vaadin.shared.ui.ClickRpc;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * We test that AbstractClientConnector has a suitable isThis method which is
 * needed to correctly perform an equals check between a proxy and it's
 * underlying instance.
 *
 * @author Vaadin Ltd
 */
public class AbstractClientConnectorTest {
    @Test
    public void registerRPCMultiInterfaceTest() {
        AbstractClientConnector mock = Mockito.mock(AbstractClientConnector.class);
        AbstractClientConnectorTest.MultiServerRpcMock implementation = new AbstractClientConnectorTest.MultiServerRpcMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        try {
            mock.registerRpc(implementation);
            Assert.fail("expected exception");
        } catch (Exception expected) {
            Assert.assertEquals(("Use registerRpc(T implementation, Class<T> rpcInterfaceType) " + "if the Rpc implementation implements more than one interface"), expected.getMessage());
        }
    }

    @Test
    public void registerRPCInterfaceTest() {
        AbstractClientConnector mock = Mockito.mock(AbstractClientConnector.class);
        AbstractClientConnectorTest.ServerRpcMock implementation = new AbstractClientConnectorTest.ServerRpcMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        mock.registerRpc(implementation);
        Mockito.verify(mock, Mockito.times(1)).registerRpc(implementation, ClickRpc.class);
    }

    @Test
    public void registerRPCInterfaceLastTest() {
        AbstractClientConnector mock = Mockito.mock(AbstractClientConnector.class);
        AbstractClientConnectorTest.ServerRpcLastMock implementation = new AbstractClientConnectorTest.ServerRpcLastMock();
        Mockito.doCallRealMethod().when(mock).registerRpc(implementation);
        mock.registerRpc(implementation);
        Mockito.verify(mock, Mockito.times(1)).registerRpc(implementation, ClickRpc.class);
    }

    @Test
    public void stateTypeCacheDoesNotLeakMemory() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        Field stateTypeCacheField = AbstractClientConnector.class.getDeclaredField("STATE_TYPE_CACHE");
        stateTypeCacheField.setAccessible(true);
        Map<Class<?>, ?> stateTypeCache = ((Map<Class<?>, ?>) (stateTypeCacheField.get(null)));
        WeakReference<Class<?>> classRef = loadClass("com.vaadin.server.AbstractClientConnector");
        stateTypeCache.put(classRef.get(), null);
        int size = stateTypeCache.size();
        Assert.assertNotNull("Class should not yet be garbage collected", classRef.get());
        for (int i = 0; i < 100; ++i) {
            System.gc();
            if ((stateTypeCache.size()) < size) {
                break;
            }
            Thread.sleep(100);
        }
        Assert.assertTrue(((stateTypeCache.size()) < size));
        Assert.assertNull("Class should be garbage collected", classRef.get());
    }

    private class ServerRpcLastMock implements ClickRpc , Comparable<AbstractClientConnectorTest.ServerRpcLastMock> {
        private static final long serialVersionUID = -2822356895755286180L;

        @Override
        public void click(MouseEventDetails mouseDetails) {
        }

        @Override
        public int compareTo(AbstractClientConnectorTest.ServerRpcLastMock o) {
            return 0;
        }
    }

    private class ServerRpcMock implements ClickRpc {
        private static final long serialVersionUID = 2822356895755286180L;

        @Override
        public void click(MouseEventDetails mouseDetails) {
        }
    }

    private class MultiServerRpcMock implements BlurServerRpc , ClickRpc {
        private static final long serialVersionUID = -7611999715560330373L;

        @Override
        public void blur() {
        }

        @Override
        public void click(MouseEventDetails mouseDetails) {
        }
    }

    private static class TestClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (!(name.startsWith("com.vaadin."))) {
                return super.loadClass(name);
            }
            String path = name.replace('.', '/').concat(".class");
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            try (InputStream stream = resource.openStream()) {
                byte[] bytes = IOUtils.toByteArray(stream);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException();
            }
        }
    }
}


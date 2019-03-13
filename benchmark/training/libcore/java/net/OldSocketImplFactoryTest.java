package libcore.java.net;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketImplFactory;
import junit.framework.TestCase;


public class OldSocketImplFactoryTest extends TestCase {
    SocketImplFactory oldFactory = null;

    Field factoryField = null;

    boolean isTestable = false;

    boolean iSocketImplCalled = false;

    boolean isCreateSocketImpl = false;

    public void test_createSocketImpl() throws IOException {
        OldSocketImplFactoryTest.MockSocketImplFactory factory = new OldSocketImplFactoryTest.MockSocketImplFactory();
        if (isTestable) {
            TestCase.assertFalse(isCreateSocketImpl);
            Socket.setSocketImplFactory(factory);
            try {
                new Socket();
                TestCase.assertTrue(isCreateSocketImpl);
                TestCase.assertTrue(iSocketImplCalled);
            } catch (Exception e) {
                TestCase.fail(("Exception during test : " + (e.getMessage())));
            }
            try {
                Socket.setSocketImplFactory(factory);
                TestCase.fail("SocketException was not thrown.");
            } catch (SocketException se) {
                // expected
            }
            try {
                Socket.setSocketImplFactory(null);
                TestCase.fail("SocketException was not thrown.");
            } catch (SocketException se) {
                // expected
            }
        } else {
            SocketImpl si = factory.createSocketImpl();
            try {
                TestCase.assertNull(si.getOption(0));
            } catch (SocketException e) {
                TestCase.fail("SocketException was thrown.");
            }
        }
    }

    class MockSocketImplFactory implements SocketImplFactory {
        public SocketImpl createSocketImpl() {
            isCreateSocketImpl = true;
            return new OldSocketImplFactoryTest.MockSocketImpl();
        }
    }

    class MockSocketImpl extends SocketImpl {
        public MockSocketImpl() {
            super();
            iSocketImplCalled = true;
        }

        @Override
        protected void accept(SocketImpl arg0) throws IOException {
        }

        @Override
        protected int available() throws IOException {
            return 0;
        }

        @Override
        protected void bind(InetAddress arg0, int arg1) throws IOException {
        }

        @Override
        protected void close() throws IOException {
        }

        @Override
        protected void connect(String arg0, int arg1) throws IOException {
        }

        @Override
        protected void connect(InetAddress arg0, int arg1) throws IOException {
        }

        @Override
        protected void connect(SocketAddress arg0, int arg1) throws IOException {
        }

        @Override
        protected void create(boolean arg0) throws IOException {
        }

        @Override
        protected InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        protected OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        protected void listen(int arg0) throws IOException {
        }

        @Override
        protected void sendUrgentData(int arg0) throws IOException {
        }

        public Object getOption(int arg0) throws SocketException {
            return null;
        }

        public void setOption(int arg0, Object arg1) throws SocketException {
        }
    }
}


package net.openhft.chronicle.queue;


import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.AbstractMarshallable;
import org.junit.Assert;
import org.junit.Test;


/**
 * To avoid the arg[] array created via the methodWriter java.lang.reflect.Proxy, this test shows how you can create a custom proxy
 *
 * Created by Rob Austin
 */
public class ProxyTest {
    public static class MyProxy implements ProxyTest.TestMessageListener {
        static Object[] a1 = null;

        static Method m1 = null;

        static {
            try {
                ProxyTest.MyProxy.m1 = ProxyTest.MyProxy.class.getMethod("onMessage", ProxyTest.Message.class);
                ProxyTest.MyProxy.a1 = new Object[ProxyTest.MyProxy.m1.getParameterTypes().length];
            } catch (NoSuchMethodException e) {
                Jvm.rethrow(e);
            }
        }

        private final Object proxy;

        private final InvocationHandler handler;

        public MyProxy(Object proxy, InvocationHandler handler) {
            this.proxy = proxy;
            this.handler = handler;
        }

        @Override
        public void onMessage(final ProxyTest.Message message) {
            ProxyTest.MyProxy.a1[0] = message;
            try {
                handler.invoke(proxy, ProxyTest.MyProxy.m1, ProxyTest.MyProxy.a1);
            } catch (Throwable throwable) {
                Jvm.rethrow(throwable);
            }
        }
    }

    @Test
    public void testReadWrite() {
        File tempDir = DirectoryUtils.tempDir("to-be-deleted");
        StringBuilder result = new StringBuilder();
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(tempDir).build()) {
            ProxyTest.TestMessageListener writer = queue.acquireAppender().methodWriterBuilder(ProxyTest.TestMessageListener.class).build();
            ProxyTest.Message message = new ProxyTest.Message();
            StringBuilder sb = new StringBuilder("test ");
            int length = sb.length();
            for (int i = 0; i < 10; i++) {
                sb.append(i);
                message.message(sb);
                writer.onMessage(message);
                sb.setLength(length);
            }
            MethodReader methodReader = queue.createTailer().methodReader(new ProxyTest.TestMessageListener() {
                @Override
                public void onMessage(final ProxyTest.Message message) {
                    result.append(message);
                }
            });
            for (int i = 0; i < 10; i++) {
                methodReader.readOne();
            }
        }
        Assert.assertEquals(("!net.openhft.chronicle.queue.ProxyTest$Message {\n" + (((((((((((((((((((((((((((("  message: test 0\n" + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 1\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 2\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 3\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 4\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 5\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 6\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 7\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 8\n") + "}\n") + "!net.openhft.chronicle.queue.ProxyTest$Message {\n") + "  message: test 9\n") + "}\n")), result.toString());
    }

    public interface TestMessageListener {
        void onMessage(ProxyTest.Message message);
    }

    public static class Message extends AbstractMarshallable {
        private final StringBuilder message = new StringBuilder();

        CharSequence message() {
            return message;
        }

        ProxyTest.Message message(final CharSequence message) {
            this.message.setLength(0);
            this.message.append(message);
            return this;
        }
    }
}


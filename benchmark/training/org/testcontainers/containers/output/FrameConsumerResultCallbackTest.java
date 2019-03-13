package org.testcontainers.containers.output;


import OutputType.STDERR;
import OutputType.STDOUT;
import com.github.dockerjava.api.model.StreamType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class FrameConsumerResultCallbackTest {
    private static final String FRAME_PAYLOAD = "\u001b[0;32m\u0422\u0435\u0441\u04421\u001b[0m\n\u001b[1;33mTest2\u001b[0m\n\u001b[0;31mTest3\u001b[0m";

    private static final String LOG_RESULT = "\u0422\u0435\u0441\u04421\nTest2\nTest3";

    @Test
    public void passStderrFrameWithoutColors() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer();
        callback.addConsumer(STDERR, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDERR, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        Assert.assertEquals(FrameConsumerResultCallbackTest.LOG_RESULT, consumer.toUtf8String());
    }

    @Test
    public void passStderrFrameWithColors() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDERR, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDERR, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        Assert.assertEquals(FrameConsumerResultCallbackTest.FRAME_PAYLOAD, consumer.toUtf8String());
    }

    @Test
    public void passStdoutFrameWithoutColors() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer();
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        Assert.assertEquals(FrameConsumerResultCallbackTest.LOG_RESULT, consumer.toUtf8String());
    }

    @Test
    public void passStdoutFrameWithColors() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        Assert.assertEquals(FrameConsumerResultCallbackTest.FRAME_PAYLOAD, consumer.toUtf8String());
    }

    @Test
    public void basicConsumer() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        FrameConsumerResultCallbackTest.BasicConsumer consumer = new FrameConsumerResultCallbackTest.BasicConsumer();
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        Assert.assertEquals(FrameConsumerResultCallbackTest.LOG_RESULT, consumer.toString());
    }

    @Test
    public void passStdoutNull() {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, null));
        Assert.assertEquals("", consumer.toUtf8String());
    }

    @Test
    public void passStdoutEmptyLine() {
        String payload = "";
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, payload.getBytes()));
        Assert.assertEquals(payload, consumer.toUtf8String());
    }

    @Test
    public void passStdoutSingleLine() {
        String payload = "Test";
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, payload.getBytes()));
        Assert.assertEquals(payload, consumer.toUtf8String());
    }

    @Test
    public void passStdoutSingleLineWithNewline() {
        String payload = "Test\n";
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.STDOUT, payload.getBytes()));
        Assert.assertEquals(payload, consumer.toUtf8String());
    }

    @Test
    public void passRawFrameWithoutColors() throws IOException, TimeoutException {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        WaitingConsumer waitConsumer = new WaitingConsumer();
        callback.addConsumer(STDOUT, waitConsumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.RAW, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("Test2")), 1, TimeUnit.SECONDS);
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("????1")), 1, TimeUnit.SECONDS);
        Exception exception = null;
        try {
            waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("Test3")), 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TimeoutException));
        callback.close();
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("Test3")), 1, TimeUnit.SECONDS);
    }

    @Test
    public void passRawFrameWithColors() throws IOException, TimeoutException {
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        WaitingConsumer waitConsumer = new WaitingConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, waitConsumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.RAW, FrameConsumerResultCallbackTest.FRAME_PAYLOAD.getBytes()));
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("\u001b[1;33mTest2\u001b[0m")), 1, TimeUnit.SECONDS);
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("\u001b[0;32m\u0422\u0435\u0441\u04421\u001b[0m")), 1, TimeUnit.SECONDS);
        Exception exception = null;
        try {
            waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("\u001b[0;31mTest3\u001b[0m")), 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TimeoutException));
        callback.close();
        waitConsumer.waitUntil(( frame) -> ((frame.getType()) == OutputType.STDOUT) && (frame.getUtf8String().equals("\u001b[0;31mTest3\u001b[0m")), 1, TimeUnit.SECONDS);
    }

    @Test
    public void reconstructBreakedUnicode() throws IOException {
        String payload = "????";
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = new byte[((int) ((payloadBytes.length) * 0.6))];
        byte[] bytes2 = new byte[(payloadBytes.length) - (bytes1.length)];
        System.arraycopy(payloadBytes, 0, bytes1, 0, bytes1.length);
        System.arraycopy(payloadBytes, bytes1.length, bytes2, 0, bytes2.length);
        FrameConsumerResultCallback callback = new FrameConsumerResultCallback();
        ToStringConsumer consumer = new ToStringConsumer().withRemoveAnsiCodes(false);
        callback.addConsumer(STDOUT, consumer);
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.RAW, bytes1));
        callback.onNext(new com.github.dockerjava.api.model.Frame(StreamType.RAW, bytes2));
        callback.close();
        Assert.assertEquals(payload, consumer.toUtf8String());
    }

    private static class BasicConsumer implements Consumer<OutputFrame> {
        private boolean firstLine = true;

        private StringBuilder input = new StringBuilder();

        @Override
        public void accept(OutputFrame outputFrame) {
            if (!(firstLine)) {
                input.append('\n');
            }
            firstLine = false;
            input.append(outputFrame.getUtf8String());
        }

        @Override
        public String toString() {
            return input.toString();
        }
    }
}


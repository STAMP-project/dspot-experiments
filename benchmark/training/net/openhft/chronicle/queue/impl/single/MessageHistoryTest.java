package net.openhft.chronicle.queue.impl.single;


import TailerDirection.BACKWARD;
import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.MessageHistory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public final class MessageHistoryTest {
    @Rule
    public final TestName testName = new TestName();

    private final AtomicLong clock = new AtomicLong(System.currentTimeMillis());

    private File inputQueueDir;

    private File outputQueueDir;

    @Test
    public void shouldAccessMessageHistory() throws Exception {
        try (final ChronicleQueue inputQueue = createQueue(inputQueueDir, 1);final ChronicleQueue outputQueue = createQueue(outputQueueDir, 2)) {
            generateTestData(inputQueue, outputQueue);
            final ExcerptTailer tailer = outputQueue.createTailer();
            final MessageHistoryTest.ValidatingSecond validatingSecond = new MessageHistoryTest.ValidatingSecond();
            final MethodReader validator = tailer.methodReader(validatingSecond);
            Assert.assertThat(validator.readOne(), CoreMatchers.is(true));
            Assert.assertThat(validatingSecond.messageHistoryPresent(), CoreMatchers.is(true));
        }
    }

    @Test
    public void shouldAccessMessageHistoryWhenTailerIsMovedToEnd() throws Exception {
        try (final ChronicleQueue inputQueue = createQueue(inputQueueDir, 1);final ChronicleQueue outputQueue = createQueue(outputQueueDir, 2)) {
            generateTestData(inputQueue, outputQueue);
            final ExcerptTailer tailer = outputQueue.createTailer();
            tailer.direction(BACKWARD).toEnd();
            final MessageHistoryTest.ValidatingSecond validatingSecond = new MessageHistoryTest.ValidatingSecond();
            final MethodReader validator = tailer.methodReader(validatingSecond);
            Assert.assertThat(validator.readOne(), CoreMatchers.is(true));
            Assert.assertThat(validatingSecond.messageHistoryPresent(), CoreMatchers.is(true));
        }
    }

    @FunctionalInterface
    interface First {
        void say(final String word);
    }

    @FunctionalInterface
    interface Second {
        void count(final int value);
    }

    private static final class LoggingFirst implements MessageHistoryTest.First {
        private final MessageHistoryTest.Second second;

        private LoggingFirst(final MessageHistoryTest.Second second) {
            this.second = second;
        }

        @Override
        public void say(final String word) {
            second.count(word.length());
        }
    }

    private static class ValidatingSecond implements MessageHistoryTest.Second {
        private boolean messageHistoryPresent = false;

        @Override
        public void count(final int value) {
            final MessageHistory messageHistory = MessageHistory.get();
            Assert.assertNotNull(messageHistory);
            Assert.assertThat(messageHistory.sources(), CoreMatchers.is(2));
            messageHistoryPresent = true;
        }

        boolean messageHistoryPresent() {
            return messageHistoryPresent;
        }
    }
}


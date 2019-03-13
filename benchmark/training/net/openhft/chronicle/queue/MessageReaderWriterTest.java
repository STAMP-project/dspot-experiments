/**
 * Copyright 2016 higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue;


import ClassAliasPool.CLASS_ALIASES;
import java.io.File;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.AbstractMarshallable;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


/* Created by Peter Lawrey on 25/03/2016. */
@RequiredForClient
public class MessageReaderWriterTest {
    private ThreadDump threadDump;

    @Test
    public void testWriteWhileReading() {
        CLASS_ALIASES.addAlias(MessageReaderWriterTest.Message1.class);
        CLASS_ALIASES.addAlias(MessageReaderWriterTest.Message2.class);
        File path1 = DirectoryUtils.tempDir("testWriteWhileReading1");
        File path2 = DirectoryUtils.tempDir("testWriteWhileReading2");
        try (ChronicleQueue queue1 = SingleChronicleQueueBuilder.binary(path1).testBlockSize().build();ChronicleQueue queue2 = SingleChronicleQueueBuilder.binary(path2).testBlockSize().build()) {
            MethodReader reader2 = queue1.createTailer().methodReader(ObjectUtils.printAll(MessageReaderWriterTest.MessageListener.class));
            MessageReaderWriterTest.MessageListener writer2 = queue2.acquireAppender().methodWriter(MessageReaderWriterTest.MessageListener.class);
            MessageReaderWriterTest.MessageListener processor = new MessageReaderWriterTest.MessageProcessor(writer2);
            MethodReader reader1 = queue1.createTailer().methodReader(processor);
            MessageReaderWriterTest.MessageListener writer1 = queue1.acquireAppender().methodWriter(MessageReaderWriterTest.MessageListener.class);
            for (int i = 0; i < 3; i++) {
                // write a message
                writer1.method1(new MessageReaderWriterTest.Message1("hello"));
                writer1.method2(new MessageReaderWriterTest.Message2(234));
                // read those messages
                Assert.assertTrue(reader1.readOne());
                Assert.assertTrue(reader1.readOne());
                Assert.assertFalse(reader1.readOne());
                // read the produced messages
                Assert.assertTrue(reader2.readOne());
                Assert.assertTrue(reader2.readOne());
                Assert.assertFalse(reader2.readOne());
            }
            // System.out.println(queue1.dump());
        }
    }

    interface MessageListener {
        void method1(MessageReaderWriterTest.Message1 message);

        void method2(MessageReaderWriterTest.Message2 message);
    }

    static class Message1 extends AbstractMarshallable {
        String text;

        public Message1(String text) {
            this.text = text;
        }
    }

    static class Message2 extends AbstractMarshallable {
        long number;

        public Message2(long number) {
            this.number = number;
        }
    }

    static class MessageProcessor implements MessageReaderWriterTest.MessageListener {
        private final MessageReaderWriterTest.MessageListener writer2;

        public MessageProcessor(MessageReaderWriterTest.MessageListener writer2) {
            this.writer2 = writer2;
        }

        @Override
        public void method1(@NotNull
        MessageReaderWriterTest.Message1 message) {
            message.text += "-processed";
            writer2.method1(message);
        }

        @Override
        public void method2(@NotNull
        MessageReaderWriterTest.Message2 message) {
            message.number += 1000;
            writer2.method2(message);
        }
    }
}


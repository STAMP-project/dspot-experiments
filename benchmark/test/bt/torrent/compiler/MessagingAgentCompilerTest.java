/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.torrent.compiler;


import bt.protocol.Bitfield;
import bt.protocol.Have;
import bt.protocol.KeepAlive;
import bt.protocol.Message;
import bt.protocol.Piece;
import bt.torrent.annotation.Consumes;
import bt.torrent.annotation.Produces;
import bt.torrent.messaging.MessageContext;
import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


public class MessagingAgentCompilerTest {
    private MessagingAgentCompiler compiler;

    public class C1 {
        private boolean executed;

        public boolean isExecuted() {
            return executed;
        }

        @Consumes
        public void consume(Message message, MessageContext context) {
            this.executed = true;
        }
    }

    public class C2 {
        @Consumes
        public void consume(Bitfield bitfield, MessageContext context) {
        }

        @Consumes
        public void consume(Have have) {
        }

        @Consumes
        public void consume(Piece piece, MessageContext context) {
        }
    }

    public class C3 {
        private boolean executed;

        public boolean isExecuted() {
            return executed;
        }

        @Consumes
        public void consume(KeepAlive keepAlive) {
            this.executed = true;
        }
    }

    public class C4 {
        @Consumes
        public void consume(Object object, MessageContext context) {
        }
    }

    public class C5 extends MessagingAgentCompilerTest.C3 {}

    public class P1 {
        private boolean executed;

        public boolean isExecuted() {
            return executed;
        }

        @Produces
        public void produce(Consumer<Message> messageConsumer, MessageContext context) {
            this.executed = true;
        }
    }

    public class P2 {
        private boolean executed;

        public boolean isExecuted() {
            return executed;
        }

        @Produces
        public void produce1(Consumer<Message> messageConsumer, MessageContext context) {
        }

        @Produces
        public void produce2(Consumer<Message> messageConsumer) {
            this.executed = true;
        }
    }

    public class P3 {
        private boolean executed;

        public boolean isExecuted() {
            return executed;
        }

        @Produces
        public void produce(Consumer<Message> messageConsumer) {
            this.executed = true;
        }
    }

    @Test
    public void testCompiler_Consumer_Generic() {
        Class<?>[] consumedType = new Class<?>[1];
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(( type, handle) -> consumedType[0] = type, null);
        compiler.compileAndVisit(new MessagingAgentCompilerTest.C1(), visitor);
        Assert.assertNotNull(consumedType[0]);
        Assert.assertEquals(Message.class, consumedType[0]);
    }

    @Test
    public void testCompiler_Consumer_MultipleTypes() {
        Set<Class<?>> consumedTypes = new HashSet<>();
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(( type, handle) -> consumedTypes.add(type), null);
        compiler.compileAndVisit(new MessagingAgentCompilerTest.C2(), visitor);
        Assert.assertEquals(3, consumedTypes.size());
        Assert.assertTrue(consumedTypes.containsAll(Arrays.asList(Bitfield.class, Piece.class, Have.class)));
    }

    @Test
    public void testCompiler_Consumer_FullHandle() {
        MessagingAgentCompilerTest.C1 c1 = new MessagingAgentCompilerTest.C1();
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(( type, handle) -> {
            try {
                handle.invoke(c1, null, null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, null);
        compiler.compileAndVisit(c1, visitor);
        Assert.assertTrue(c1.isExecuted());
    }

    @Test
    public void testCompiler_Consumer_SingleParameterHandle() {
        MessagingAgentCompilerTest.C3 c3 = new MessagingAgentCompilerTest.C3();
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(( type, handle) -> {
            try {
                handle.invoke(c3, null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, null);
        compiler.compileAndVisit(c3, visitor);
        Assert.assertTrue(c3.isExecuted());
    }

    @Test
    public void testCompiler_Consumer_Inherited() {
        Class<?>[] consumedType = new Class<?>[1];
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(( type, handle) -> consumedType[0] = type, null);
        compiler.compileAndVisit(new MessagingAgentCompilerTest.C5(), visitor);
        Assert.assertNotNull(consumedType[0]);
        Assert.assertEquals(KeepAlive.class, consumedType[0]);
    }

    @Test
    public void testCompiler_Producer() {
        Boolean[] compiled = new Boolean[]{ false };
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(null, ( handle) -> compiled[0] = true);
        compiler.compileAndVisit(new MessagingAgentCompilerTest.P1(), visitor);
        Assert.assertTrue(compiled[0]);
    }

    @Test
    public void testCompiler_Producer_MultipleProducers() {
        int[] producerCount = new int[1];
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(null, ( handle) -> (producerCount[0])++);
        compiler.compileAndVisit(new MessagingAgentCompilerTest.P2(), visitor);
        Assert.assertEquals(2, producerCount[0]);
    }

    @Test
    public void testCompiler_Producer_FullHandle() {
        MessagingAgentCompilerTest.P1 p1 = new MessagingAgentCompilerTest.P1();
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(null, ( handle) -> {
            try {
                handle.invoke(p1, null, null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        compiler.compileAndVisit(p1, visitor);
        Assert.assertTrue(p1.isExecuted());
    }

    @Test
    public void testCompiler_Producer_SingleParameterHandle() {
        MessagingAgentCompilerTest.P3 p3 = new MessagingAgentCompilerTest.P3();
        CompilerVisitor visitor = MessagingAgentCompilerTest.createVisitor(null, ( handle) -> {
            try {
                handle.invoke(p3, null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        compiler.compileAndVisit(p3, visitor);
        Assert.assertTrue(p3.isExecuted());
    }

    @Test
    public void testCompiler_Consumer_WrongParameters() {
        Exception e = null;
        try {
            compiler.compileAndVisit(new MessagingAgentCompilerTest.C4(), MessagingAgentCompilerTest.createVisitor(( c, h) -> {
            }, ( h) -> {
            }));
        } catch (Exception e1) {
            e = e1;
        }
        Assert.assertNotNull(e);
        Assert.assertEquals("Consumer method must have bt.protocol.Message or it's subclass as the first parameter", e.getMessage());
    }
}


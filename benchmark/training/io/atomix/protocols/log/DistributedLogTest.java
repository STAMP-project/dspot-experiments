/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.protocols.log;


import io.atomix.cluster.MemberId;
import io.atomix.primitive.PrimitiveBuilder;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.config.PrimitiveConfig;
import io.atomix.primitive.event.EventType;
import io.atomix.primitive.log.LogSession;
import io.atomix.primitive.operation.OperationId;
import io.atomix.primitive.partition.PrimaryElection;
import io.atomix.primitive.service.AbstractPrimitiveService;
import io.atomix.primitive.service.BackupInput;
import io.atomix.primitive.service.BackupOutput;
import io.atomix.primitive.service.Commit;
import io.atomix.primitive.service.PrimitiveService;
import io.atomix.primitive.service.ServiceConfig;
import io.atomix.primitive.service.ServiceExecutor;
import io.atomix.primitive.session.Session;
import io.atomix.protocols.log.protocol.TestLogProtocolFactory;
import io.atomix.utils.serializer.Serializer;
import io.atomix.utils.serializer.serializers.DefaultSerializers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.jodah.concurrentunit.ConcurrentTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Raft test.
 */
public class DistributedLogTest extends ConcurrentTestCase {
    private static final Serializer SERIALIZER = DefaultSerializers.BASIC;

    private volatile int memberId;

    private volatile int sessionId;

    private PrimaryElection election;

    protected volatile List<MemberId> nodes;

    protected volatile List<DistributedLogSessionClient> clients = new ArrayList<>();

    protected volatile List<DistributedLogServer> servers = new ArrayList<>();

    protected volatile TestLogProtocolFactory protocolFactory;

    @Test
    public void testProducerConsumer() throws Throwable {
        createServers(3);
        DistributedLogSessionClient client1 = createClient();
        LogSession session1 = createSession(client1);
        DistributedLogSessionClient client2 = createClient();
        LogSession session2 = createSession(client2);
        session1.consumer().consume(1, ( record) -> {
            threadAssertTrue(Arrays.equals("Hello world!".getBytes(), record.value()));
            resume();
        });
        session2.producer().append("Hello world!".getBytes());
        await(5000);
    }

    @Test
    public void testConsumeIndex() throws Throwable {
        createServers(3);
        DistributedLogSessionClient client1 = createClient();
        LogSession session1 = createSession(client1);
        DistributedLogSessionClient client2 = createClient();
        LogSession session2 = createSession(client2);
        for (int i = 1; i <= 10; i++) {
            session2.producer().append(String.valueOf(i).getBytes()).join();
        }
        session1.consumer().consume(10, ( record) -> {
            threadAssertTrue(((record.index()) == 10));
            threadAssertTrue(Arrays.equals("10".getBytes(), record.value()));
            resume();
        });
        await(5000);
    }

    @Test
    public void testConsumeAfterSizeCompact() throws Throwable {
        List<DistributedLogServer> servers = createServers(3);
        DistributedLogSessionClient client1 = createClient();
        LogSession session1 = createSession(client1);
        DistributedLogSessionClient client2 = createClient();
        LogSession session2 = createSession(client2);
        Predicate<List<DistributedLogServer>> predicate = ( s) -> s.stream().map(( sr) -> (sr.context.journal().segments().size()) > 2).reduce(Boolean::logicalOr).orElse(false);
        while (!(predicate.test(servers))) {
            session1.producer().append(UUID.randomUUID().toString().getBytes());
        } 
        servers.forEach(( server) -> server.context.compact());
        session2.consumer().consume(1, ( record) -> {
            threadAssertTrue(((record.index()) > 1));
            resume();
        });
        await(5000);
    }

    @Test
    public void testConsumeAfterAgeCompact() throws Throwable {
        List<DistributedLogServer> servers = createServers(3);
        DistributedLogSessionClient client1 = createClient();
        LogSession session1 = createSession(client1);
        DistributedLogSessionClient client2 = createClient();
        LogSession session2 = createSession(client2);
        Predicate<List<DistributedLogServer>> predicate = ( s) -> s.stream().map(( sr) -> (sr.context.journal().segments().size()) > 1).reduce(Boolean::logicalOr).orElse(false);
        while (!(predicate.test(servers))) {
            session1.producer().append(UUID.randomUUID().toString().getBytes());
        } 
        Thread.sleep(1000);
        servers.forEach(( server) -> server.context.compact());
        session2.consumer().consume(1, ( record) -> {
            threadAssertTrue(((record.index()) > 1));
            resume();
        });
        await(5000);
    }

    private static final OperationId WRITE = OperationId.command("write");

    private static final OperationId EVENT = OperationId.command("event");

    private static final OperationId EXPIRE = OperationId.command("expire");

    private static final OperationId CLOSE = OperationId.command("close");

    private static final OperationId READ = OperationId.query("read");

    private static final EventType CHANGE_EVENT = EventType.from("change");

    private static final EventType EXPIRE_EVENT = EventType.from("expire");

    private static final EventType CLOSE_EVENT = EventType.from("close");

    public static class TestPrimitiveType implements PrimitiveType {
        private static final DistributedLogTest.TestPrimitiveType INSTANCE = new DistributedLogTest.TestPrimitiveType();

        @Override
        public String name() {
            return "primary-backup-test";
        }

        @Override
        public PrimitiveConfig newConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrimitiveBuilder newBuilder(String primitiveName, PrimitiveConfig config, PrimitiveManagementService managementService) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PrimitiveService newService(ServiceConfig config) {
            return new DistributedLogTest.TestPrimitiveService();
        }
    }

    /**
     * Test state machine.
     */
    public static class TestPrimitiveService extends AbstractPrimitiveService<Object> {
        private Commit<Void> expire;

        private Commit<Void> close;

        public TestPrimitiveService() {
            super(DistributedLogTest.TestPrimitiveType.INSTANCE);
        }

        @Override
        public Serializer serializer() {
            return DistributedLogTest.SERIALIZER;
        }

        @Override
        protected void configure(ServiceExecutor executor) {
            executor.register(DistributedLogTest.WRITE, this::write);
            executor.register(DistributedLogTest.READ, this::read);
            executor.register(DistributedLogTest.EVENT, this::event);
            executor.<Void>register(DistributedLogTest.CLOSE, ( c) -> close(c));
            executor.register(DistributedLogTest.EXPIRE, ((Consumer<Commit<Void>>) (this::expire)));
        }

        @Override
        public void onExpire(Session session) {
            if ((expire) != null) {
                expire.session().publish(DistributedLogTest.EXPIRE_EVENT);
            }
        }

        @Override
        public void onClose(Session session) {
            if (((close) != null) && (!(session.equals(close.session())))) {
                close.session().publish(DistributedLogTest.CLOSE_EVENT);
            }
        }

        @Override
        public void backup(BackupOutput writer) {
            writer.writeLong(10);
        }

        @Override
        public void restore(BackupInput reader) {
            Assert.assertEquals(10, reader.readLong());
        }

        protected long write(Commit<Void> commit) {
            return commit.index();
        }

        protected long read(Commit<Void> commit) {
            return commit.index();
        }

        protected long event(Commit<Boolean> commit) {
            if (commit.value()) {
                commit.session().publish(DistributedLogTest.CHANGE_EVENT, commit.index());
            } else {
                for (Session session : getSessions()) {
                    session.publish(DistributedLogTest.CHANGE_EVENT, commit.index());
                }
            }
            return commit.index();
        }

        public void close(Commit<Void> commit) {
            this.close = commit;
        }

        public void expire(Commit<Void> commit) {
            this.expire = commit;
        }
    }
}


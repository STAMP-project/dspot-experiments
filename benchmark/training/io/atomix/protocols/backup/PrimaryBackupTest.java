/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.backup;


import Replication.ASYNCHRONOUS;
import Replication.SYNCHRONOUS;
import io.atomix.cluster.MemberId;
import io.atomix.primitive.PrimitiveBuilder;
import io.atomix.primitive.PrimitiveManagementService;
import io.atomix.primitive.PrimitiveType;
import io.atomix.primitive.config.PrimitiveConfig;
import io.atomix.primitive.event.EventType;
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
import io.atomix.protocols.backup.protocol.TestPrimaryBackupProtocolFactory;
import io.atomix.utils.serializer.Serializer;
import io.atomix.utils.serializer.serializers.DefaultSerializers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.jodah.concurrentunit.ConcurrentTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Raft test.
 */
public class PrimaryBackupTest extends ConcurrentTestCase {
    private static final Serializer SERIALIZER = DefaultSerializers.BASIC;

    private volatile int memberId;

    private volatile int sessionId;

    private PrimaryElection election;

    protected volatile List<MemberId> nodes;

    protected volatile List<PrimaryBackupClient> clients = new ArrayList<>();

    protected volatile List<PrimaryBackupServer> servers = new ArrayList<>();

    protected volatile TestPrimaryBackupProtocolFactory protocolFactory;

    @Test
    public void testOneNodeCommand() throws Throwable {
        testSubmitCommand(1, 0, SYNCHRONOUS);
    }

    @Test
    public void testSynchronousCommand() throws Throwable {
        testSubmitCommand(3, 2, SYNCHRONOUS);
    }

    @Test
    public void testAsynchronousCommand() throws Throwable {
        testSubmitCommand(3, 2, ASYNCHRONOUS);
    }

    @Test
    public void testOneNodeQuery() throws Throwable {
        testSubmitQuery(1, 0, SYNCHRONOUS);
    }

    @Test
    public void testSynchronousQuery() throws Throwable {
        testSubmitQuery(3, 2, SYNCHRONOUS);
    }

    @Test
    public void testAsynchronousQuery() throws Throwable {
        testSubmitQuery(3, 2, ASYNCHRONOUS);
    }

    @Test
    public void testOneNodeEvent() throws Throwable {
        testSequentialEvent(1, 0, SYNCHRONOUS);
    }

    @Test
    public void testSynchronousEvent() throws Throwable {
        testSequentialEvent(3, 2, SYNCHRONOUS);
    }

    @Test
    public void testAsynchronousEvent() throws Throwable {
        testSequentialEvent(3, 2, ASYNCHRONOUS);
    }

    @Test
    public void testOneNodeEvents() throws Throwable {
        testEvents(1, 0, SYNCHRONOUS);
    }

    @Test
    public void testSynchronousEvents() throws Throwable {
        testEvents(3, 2, SYNCHRONOUS);
    }

    @Test
    public void testAsynchronousEvents() throws Throwable {
        testEvents(3, 2, ASYNCHRONOUS);
    }

    @Test
    public void testOneNodeManyEvents() throws Throwable {
        testManyEvents(1, 0, SYNCHRONOUS);
    }

    @Test
    public void testManySynchronousEvents() throws Throwable {
        testManyEvents(3, 2, SYNCHRONOUS);
    }

    @Test
    public void testManyAsynchronousEvents() throws Throwable {
        testManyEvents(3, 2, ASYNCHRONOUS);
    }

    /**
     * Tests submitting linearizable events.
     */
    @Test
    public void testManySynchronousSessionsManyEvents() throws Throwable {
        testManySessionsManyEvents(SYNCHRONOUS);
    }

    /**
     * Tests submitting linearizable events.
     */
    @Test
    public void testManyAsynchronousSessionsManyEvents() throws Throwable {
        testManySessionsManyEvents(ASYNCHRONOUS);
    }

    @Test
    public void testSynchronousCloseEvent() throws Throwable {
        testSessionClose(SYNCHRONOUS);
    }

    @Test
    public void testAsynchronousCloseEvent() throws Throwable {
        testSessionClose(ASYNCHRONOUS);
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
        private static final PrimaryBackupTest.TestPrimitiveType INSTANCE = new PrimaryBackupTest.TestPrimitiveType();

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
            return new PrimaryBackupTest.TestPrimitiveService();
        }
    }

    /**
     * Test state machine.
     */
    public static class TestPrimitiveService extends AbstractPrimitiveService<Object> {
        private Commit<Void> expire;

        private Commit<Void> close;

        public TestPrimitiveService() {
            super(PrimaryBackupTest.TestPrimitiveType.INSTANCE);
        }

        @Override
        public Serializer serializer() {
            return PrimaryBackupTest.SERIALIZER;
        }

        @Override
        protected void configure(ServiceExecutor executor) {
            executor.register(PrimaryBackupTest.WRITE, this::write);
            executor.register(PrimaryBackupTest.READ, this::read);
            executor.register(PrimaryBackupTest.EVENT, this::event);
            executor.<Void>register(PrimaryBackupTest.CLOSE, ( c) -> close(c));
            executor.register(PrimaryBackupTest.EXPIRE, ((Consumer<Commit<Void>>) (this::expire)));
        }

        @Override
        public void onExpire(Session session) {
            if ((expire) != null) {
                expire.session().publish(PrimaryBackupTest.EXPIRE_EVENT);
            }
        }

        @Override
        public void onClose(Session session) {
            if (((close) != null) && (!(session.equals(close.session())))) {
                close.session().publish(PrimaryBackupTest.CLOSE_EVENT);
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
                commit.session().publish(PrimaryBackupTest.CHANGE_EVENT, commit.index());
            } else {
                for (Session session : getSessions()) {
                    session.publish(PrimaryBackupTest.CHANGE_EVENT, commit.index());
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


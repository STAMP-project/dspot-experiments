/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.command.runtime.rule;


import java.util.Arrays;
import java.util.List;
import org.drools.core.command.impl.NotTransactionalCommand;
import org.drools.core.fluent.impl.Batch;
import org.drools.core.fluent.impl.BatchImpl;
import org.drools.core.fluent.impl.InternalExecutable;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;


public class InternalExecutableTest {
    @Test
    public void notTransactionalCommandTest() {
        final Batch batch = new BatchImpl();
        batch.addCommand(new InternalExecutableTest.TransactionalCommandTest());
        batch.addCommand(new InternalExecutableTest.NotTransactionalCommandTest());
        final InternalExecutable internalExecutableImplTest = new InternalExecutableTest.InternalExecutableImplTest(batch);
        Assert.assertFalse(internalExecutableImplTest.canRunInTransaction());
    }

    @Test
    public void transactionalCommandTest() {
        final Batch batch = new BatchImpl();
        batch.addCommand(new InternalExecutableTest.TransactionalCommandTest());
        batch.addCommand(new InternalExecutableTest.TransactionalCommandTest());
        final InternalExecutable internalExecutableImplTest = new InternalExecutableTest.InternalExecutableImplTest(batch);
        Assert.assertTrue(internalExecutableImplTest.canRunInTransaction());
    }

    @Test
    public void emptyCommandTest() {
        final Batch batch = new BatchImpl();
        final InternalExecutable internalExecutableImplTest = new InternalExecutableTest.InternalExecutableImplTest(batch);
        Assert.assertTrue(internalExecutableImplTest.canRunInTransaction());
    }

    class TransactionalCommandTest implements ExecutableCommand<Void> {
        @Override
        public Void execute(Context context) {
            return null;
        }
    }

    class NotTransactionalCommandTest implements NotTransactionalCommand<Void> {
        @Override
        public Void execute(Context context) {
            return null;
        }
    }

    class InternalExecutableImplTest implements InternalExecutable {
        private final List<Batch> batches;

        @Override
        public List<Batch> getBatches() {
            return batches;
        }

        InternalExecutableImplTest(Batch... batches) {
            this.batches = Arrays.asList(batches);
        }
    }
}


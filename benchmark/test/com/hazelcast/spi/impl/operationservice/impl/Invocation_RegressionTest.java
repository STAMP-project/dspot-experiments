/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_RegressionTest extends HazelcastTestSupport {
    @Test(expected = ExecutionException.class, timeout = 120000)
    public void testIssue2509() throws Exception {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance h1 = nodeFactory.newHazelcastInstance();
        HazelcastInstance h2 = nodeFactory.newHazelcastInstance();
        Invocation_RegressionTest.UnDeserializable unDeserializable = new Invocation_RegressionTest.UnDeserializable(1);
        IExecutorService executorService = h1.getExecutorService("default");
        Invocation_RegressionTest.Issue2509Runnable task = new Invocation_RegressionTest.Issue2509Runnable(unDeserializable);
        Future<?> future = executorService.submitToMember(task, h2.getCluster().getLocalMember());
        future.get();
    }

    public static class Issue2509Runnable implements DataSerializable , Callable<Integer> {
        private Invocation_RegressionTest.UnDeserializable unDeserializable;

        Issue2509Runnable() {
        }

        Issue2509Runnable(Invocation_RegressionTest.UnDeserializable unDeserializable) {
            this.unDeserializable = unDeserializable;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeObject(unDeserializable);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            unDeserializable = in.readObject();
        }

        @Override
        public Integer call() {
            return unDeserializable.foo;
        }
    }

    public static class UnDeserializable implements DataSerializable {
        private int foo;

        UnDeserializable(int foo) {
            this.foo = foo;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt(foo);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            foo = in.readInt();
        }
    }
}


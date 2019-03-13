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
package com.hazelcast.spi;


import LockService.SERVICE_NAME;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NodeEngineTest extends HazelcastTestSupport {
    private NodeEngineImpl nodeEngine;

    @Test(expected = NullPointerException.class)
    public void getSharedService_whenNullName() {
        nodeEngine.getSharedService(null);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void getSharedService_whenNonExistingService() {
        SharedService sharedService = nodeEngine.getSharedService("notexist");
        Assert.assertNull(sharedService);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void getSharedService_whenExistingService() {
        SharedService sharedService = nodeEngine.getSharedService(SERVICE_NAME);
        Assert.assertNotNull(sharedService);
        Assert.assertTrue((sharedService instanceof LockService));
    }

    @Test(expected = NullPointerException.class)
    public void getService_whenNullName() {
        nodeEngine.getService(null);
    }

    @Test(expected = HazelcastException.class)
    public void getService_whenNonExistingService() {
        nodeEngine.getService("notexist");
    }

    @Test
    public void getService_whenExistingService() {
        Object sharedService = nodeEngine.getService(SERVICE_NAME);
        HazelcastTestSupport.assertInstanceOf(LockService.class, sharedService);
    }

    @Test
    public void toData_whenNull() {
        Data result = nodeEngine.toData(null);
        Assert.assertNull(result);
    }

    @Test(expected = HazelcastSerializationException.class)
    public void toData_whenSerializationProblem() {
        NodeEngineTest.SerializeFailureObject object = new NodeEngineTest.SerializeFailureObject();
        nodeEngine.toData(object);
    }

    public class SerializeFailureObject implements DataSerializable {
        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            throw new RuntimeException();
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
        }
    }

    @Test(expected = HazelcastSerializationException.class)
    public void toObject_whenDeserializeProblem() {
        NodeEngineTest.DeserializeFailureObject object = new NodeEngineTest.DeserializeFailureObject();
        Data data = nodeEngine.toData(object);
        nodeEngine.toObject(data);
    }

    @Test
    public void toObject_whenNull() {
        Object actual = nodeEngine.toObject(null);
        Assert.assertNull(actual);
    }

    @Test
    public void toObject_whenAlreadyDeserialized() {
        String expected = "foo";
        Object actual = nodeEngine.toObject(expected);
        Assert.assertSame(expected, actual);
    }

    public class DeserializeFailureObject implements DataSerializable {
        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            throw new RuntimeException();
        }
    }

    @Test(expected = NullPointerException.class)
    public void getLogger_whenNullString() {
        nodeEngine.getLogger(((String) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void getLogger_whenNullClass() {
        nodeEngine.getLogger(((Class) (null)));
    }
}


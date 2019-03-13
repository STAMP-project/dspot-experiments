/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import org.junit.Assert;
import org.junit.Test;


public class OperationTest {
    private static final String ID = "id";

    private static final String PRODUCER = "producer";

    private static final Boolean FIRST = true;

    private static final Boolean LAST = false;

    private static final Operation OPERATION = Operation.newBuilder(OperationTest.ID, OperationTest.PRODUCER).setFirst(OperationTest.FIRST).setLast(OperationTest.LAST).build();

    @Test
    public void testBuilder() {
        Assert.assertEquals(OperationTest.ID, OperationTest.OPERATION.getId());
        Assert.assertEquals(OperationTest.PRODUCER, OperationTest.OPERATION.getProducer());
        Assert.assertTrue(OperationTest.OPERATION.first());
        Assert.assertFalse(OperationTest.OPERATION.last());
    }

    @Test
    public void testToBuilder() {
        compareLogOperation(OperationTest.OPERATION, OperationTest.OPERATION.toBuilder().build());
        Operation operation = OperationTest.OPERATION.toBuilder().setId("newId").setProducer("newProducer").setFirst(false).setLast(true).build();
        Assert.assertEquals("newId", operation.getId());
        Assert.assertEquals("newProducer", operation.getProducer());
        Assert.assertFalse(operation.first());
        Assert.assertTrue(operation.last());
        operation = operation.toBuilder().setId(OperationTest.ID).setProducer(OperationTest.PRODUCER).setFirst(OperationTest.FIRST).setLast(OperationTest.LAST).build();
        compareLogOperation(OperationTest.OPERATION, operation);
    }

    @Test
    public void testOf() {
        Operation operation = Operation.of(OperationTest.ID, OperationTest.PRODUCER);
        Assert.assertEquals(OperationTest.ID, operation.getId());
        Assert.assertEquals(OperationTest.PRODUCER, operation.getProducer());
        Assert.assertFalse(operation.first());
        Assert.assertFalse(operation.last());
    }

    @Test
    public void testToAndFromPb() {
        compareLogOperation(OperationTest.OPERATION, Operation.fromPb(OperationTest.OPERATION.toPb()));
        Operation operation = Operation.of(OperationTest.ID, OperationTest.PRODUCER);
        compareLogOperation(operation, Operation.fromPb(operation.toPb()));
    }
}


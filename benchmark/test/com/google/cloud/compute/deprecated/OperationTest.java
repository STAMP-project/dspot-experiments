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
package com.google.cloud.compute.deprecated;


import Compute.OperationField.STATUS;
import Compute.OperationOption;
import Operation.Status;
import com.google.api.core.ApiClock;
import com.google.api.core.CurrentMillisClock;
import com.google.cloud.RetryOption;
import com.google.cloud.compute.deprecated.Operation.OperationError;
import com.google.cloud.compute.deprecated.Operation.OperationWarning;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.threeten.bp.Duration;


public class OperationTest {
    private static final OperationError OPERATION_ERROR1 = new OperationError("code1", "location1", "message1");

    private static final OperationError OPERATION_ERROR2 = new OperationError("code2", "location2", "message2");

    private static final OperationWarning OPERATION_WARNING1 = new OperationWarning("code1", "message1", ImmutableMap.of("k1", "v1"));

    private static final OperationWarning OPERATION_WARNING2 = new OperationWarning("code2", "location2", ImmutableMap.of("k2", "v2"));

    private static final String GENERATED_ID = "1";

    private static final String CLIENT_OPERATION_ID = "clientOperationId";

    private static final String OPERATION_TYPE = "delete";

    private static final String TARGET_LINK = "targetLink";

    private static final String TARGET_ID = "42";

    private static final Status STATUS = Status.DONE;

    private static final String STATUS_MESSAGE = "statusMessage";

    private static final String USER = "user";

    private static final Integer PROGRESS = 100;

    private static final Long INSERT_TIME = 1453293540000L;

    private static final Long START_TIME = 1453293420000L;

    private static final Long END_TIME = 1453293480000L;

    private static final List<OperationError> ERRORS = ImmutableList.of(OperationTest.OPERATION_ERROR1, OperationTest.OPERATION_ERROR2);

    private static final List<OperationWarning> WARNINGS = ImmutableList.of(OperationTest.OPERATION_WARNING1, OperationTest.OPERATION_WARNING2);

    private static final Integer HTTP_ERROR_STATUS_CODE = 404;

    private static final String HTTP_ERROR_MESSAGE = "NOT FOUND";

    private static final String DESCRIPTION = "description";

    private static final GlobalOperationId GLOBAL_OPERATION_ID = GlobalOperationId.of("project", "op");

    private static final ZoneOperationId ZONE_OPERATION_ID = ZoneOperationId.of("project", "zone", "op");

    private static final RegionOperationId REGION_OPERATION_ID = RegionOperationId.of("project", "region", "op");

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Compute serviceMockReturnsOptions = createStrictMock(Compute.class);

    private final ComputeOptions mockOptions = createMock(ComputeOptions.class);

    private Compute compute;

    private Operation globalOperation;

    private Operation regionOperation;

    private Operation zoneOperation;

    private Operation operation;

    @Test
    public void testBuilder() {
        initializeExpectedOperation(6);
        assertEqualsCommonFields(globalOperation);
        Assert.assertEquals(OperationTest.GLOBAL_OPERATION_ID, globalOperation.getOperationId());
        assertEqualsCommonFields(regionOperation);
        Assert.assertEquals(OperationTest.REGION_OPERATION_ID, regionOperation.getOperationId());
        assertEqualsCommonFields(zoneOperation);
        Assert.assertEquals(OperationTest.ZONE_OPERATION_ID, zoneOperation.getOperationId());
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.GLOBAL_OPERATION_ID).build();
        assertNullCommonFields(operation);
        Assert.assertEquals(OperationTest.GLOBAL_OPERATION_ID, operation.getOperationId());
        operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.ZONE_OPERATION_ID).build();
        assertNullCommonFields(operation);
        Assert.assertEquals(OperationTest.ZONE_OPERATION_ID, operation.getOperationId());
        operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.REGION_OPERATION_ID).build();
        assertNullCommonFields(operation);
        Assert.assertEquals(OperationTest.REGION_OPERATION_ID, operation.getOperationId());
    }

    @Test
    public void testToAndFromPb() {
        initializeExpectedOperation(24);
        compareOperation(globalOperation, Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb()));
        Assert.assertNotNull(regionOperation.toPb().getRegion());
        compareOperation(regionOperation, Operation.fromPb(serviceMockReturnsOptions, regionOperation.toPb()));
        Assert.assertNotNull(zoneOperation.toPb().getZone());
        compareOperation(zoneOperation, Operation.fromPb(serviceMockReturnsOptions, zoneOperation.toPb()));
        Operation operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.GLOBAL_OPERATION_ID).build();
        compareOperation(operation, Operation.fromPb(serviceMockReturnsOptions, operation.toPb()));
        operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.ZONE_OPERATION_ID).build();
        compareOperation(operation, Operation.fromPb(serviceMockReturnsOptions, operation.toPb()));
        operation = new Operation.Builder(serviceMockReturnsOptions).setOperationId(OperationTest.REGION_OPERATION_ID).build();
        compareOperation(operation, Operation.fromPb(serviceMockReturnsOptions, operation.toPb()));
    }

    @Test
    public void testDeleteTrue() {
        initializeExpectedOperation(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(true);
        replay(compute);
        initializeOperation();
        Assert.assertTrue(operation.delete());
        verify(compute);
    }

    @Test
    public void testDeleteFalse() {
        initializeExpectedOperation(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.deleteOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(false);
        replay(compute);
        initializeOperation();
        Assert.assertFalse(operation.delete());
        verify(compute);
    }

    @Test
    public void testExists_True() throws Exception {
        initializeExpectedOperation(3);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(globalOperation);
        replay(compute);
        initializeOperation();
        Assert.assertTrue(operation.exists());
        verify(compute);
    }

    @Test
    public void testExists_False() throws Exception {
        initializeExpectedOperation(3);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields() };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeOperation();
        Assert.assertFalse(operation.exists());
        verify(compute);
    }

    @Test
    public void testIsDone_True() throws Exception {
        initializeExpectedOperation(3);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(globalOperation);
        replay(compute);
        initializeOperation();
        Assert.assertTrue(operation.isDone());
        verify(compute);
    }

    @Test
    public void testIsDone_False() throws Exception {
        initializeExpectedOperation(4);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setStatus("PENDING")));
        replay(compute);
        initializeOperation();
        Assert.assertFalse(operation.isDone());
        verify(compute);
    }

    @Test
    public void testIsDone_NotExists() throws Exception {
        initializeExpectedOperation(3);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(null);
        replay(compute);
        initializeOperation();
        Assert.assertTrue(operation.isDone());
        verify(compute);
    }

    @Test
    public void testWaitFor() throws InterruptedException {
        initializeExpectedOperation(4);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        Operation successOperation = Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setError(null));
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(successOperation);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(successOperation);
        replay(compute, mockOptions);
        initializeOperation();
        Assert.assertSame(successOperation, operation.waitFor());
        verify(mockOptions);
    }

    @Test
    public void testWaitFor_Null() throws InterruptedException {
        initializeExpectedOperation(3);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(null);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(null);
        replay(compute, mockOptions);
        initializeOperation();
        Assert.assertNull(operation.waitFor());
        verify(mockOptions);
    }

    @Test
    public void testWaitForCheckingPeriod() throws InterruptedException {
        initializeExpectedOperation(5);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        Operation runningOperation = Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setError(null).setStatus("RUNNING"));
        Operation completedOperation = Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setError(null));
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(runningOperation);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(completedOperation);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(completedOperation);
        replay(compute, mockOptions);
        initializeOperation();
        Assert.assertSame(completedOperation, operation.waitFor(RetryOption.initialRetryDelay(Duration.ofMillis(1))));
        verify(mockOptions);
    }

    @Test
    public void testWaitForCheckingPeriod_Null() throws InterruptedException {
        initializeExpectedOperation(4);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        Operation runningOperation = Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setError(null).setStatus("RUNNING"));
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(CurrentMillisClock.getDefaultClock());
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(runningOperation);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(null);
        replay(compute, mockOptions);
        initializeOperation();
        Assert.assertNull(operation.waitFor(RetryOption.initialRetryDelay(Duration.ofMillis(1L))));
        verify(compute, mockOptions);
    }

    @Test
    public void testWaitForWithTimeout() throws InterruptedException {
        initializeExpectedOperation(4);
        Compute[] expectedOptions = new OperationOption[]{ OperationOption.fields(Compute.OperationField.STATUS) };
        ApiClock clock = createStrictMock(ApiClock.class);
        expect(clock.nanoTime()).andReturn(0L);
        expect(clock.nanoTime()).andReturn(1000000L);
        expect(clock.nanoTime()).andReturn(3000000L);
        Operation runningOperation = Operation.fromPb(serviceMockReturnsOptions, globalOperation.toPb().setError(null).setStatus("RUNNING"));
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(mockOptions.getClock()).andReturn(clock);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(runningOperation);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, expectedOptions)).andReturn(runningOperation);
        replay(compute, clock, mockOptions);
        initializeOperation();
        thrown.expect(ComputeException.class);
        operation.waitFor(RetryOption.initialRetryDelay(Duration.ofMillis(1L)), RetryOption.totalTimeout(Duration.ofMillis(3L)));
        verify(compute, clock, mockOptions);
    }

    @Test
    public void testReload() throws Exception {
        initializeExpectedOperation(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(globalOperation);
        replay(compute);
        initializeOperation();
        Operation updatedOperation = operation.reload();
        compareOperation(globalOperation, updatedOperation);
        verify(compute);
    }

    @Test
    public void testReloadNull() throws Exception {
        initializeExpectedOperation(3);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID)).andReturn(null);
        replay(compute);
        initializeOperation();
        Assert.assertNull(operation.reload());
        verify(compute);
    }

    @Test
    public void testReloadWithOptions() throws Exception {
        initializeExpectedOperation(5);
        expect(compute.getOptions()).andReturn(mockOptions);
        expect(compute.getOperation(OperationTest.GLOBAL_OPERATION_ID, OperationOption.fields())).andReturn(globalOperation);
        replay(compute);
        initializeOperation();
        Operation updatedOperation = operation.reload(OperationOption.fields());
        compareOperation(globalOperation, updatedOperation);
        verify(compute);
    }
}


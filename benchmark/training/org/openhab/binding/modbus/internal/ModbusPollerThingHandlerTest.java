/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingConstantsInternal.READ_TYPE_HOLDING_REGISTER;
import ModbusReadFunctionCode.READ_COILS;
import ModbusReadFunctionCode.READ_INPUT_DISCRETES;
import ModbusReadFunctionCode.READ_INPUT_REGISTERS;
import ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS;
import ThingStatus.OFFLINE;
import ThingStatus.ONLINE;
import ThingStatusDetail.BRIDGE_OFFLINE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.binding.modbus.internal.handler.ModbusDataThingHandler;
import org.openhab.binding.modbus.internal.handler.ModbusPollerThingHandlerImpl;
import org.openhab.binding.modbus.internal.handler.ModbusTcpThingHandler;
import org.openhab.io.transport.modbus.BitArray;
import org.openhab.io.transport.modbus.ModbusManager;
import org.openhab.io.transport.modbus.ModbusReadCallback;
import org.openhab.io.transport.modbus.ModbusReadRequestBlueprint;
import org.openhab.io.transport.modbus.ModbusRegisterArray;
import org.openhab.io.transport.modbus.PollTask;


@RunWith(MockitoJUnitRunner.class)
public class ModbusPollerThingHandlerTest {
    @Mock
    private ModbusManager modbusManager;

    @Mock
    private ThingRegistry thingRegistry;

    private Bridge endpoint;

    private Bridge poller;

    private List<Thing> things = new ArrayList<>();

    private ModbusTcpThingHandler tcpThingHandler;

    @Mock
    private ThingHandlerCallback thingCallback;

    @SuppressWarnings("null")
    @Test
    public void testInitializeNonPolling() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);// 0 -> non polling

        pollerConfig.put("start", 5);
        pollerConfig.put("length", 9);
        pollerConfig.put("type", READ_TYPE_HOLDING_REGISTER);
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl pollerThingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        hookItemRegistry(pollerThingHandler);
        pollerThingHandler.setCallback(thingCallback);
        poller.setHandler(pollerThingHandler);
        pollerThingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        // polling not setup
        Mockito.verifyZeroInteractions(modbusManager);
    }

    @Test
    public void testInitializePollingWithCoils() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        testPollingGeneric("coil", () -> isRequestOkGeneric(READ_COILS));
    }

    @Test
    public void testInitializePollingWithDiscrete() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        testPollingGeneric("discrete", () -> isRequestOkGeneric(READ_INPUT_DISCRETES));
    }

    @Test
    public void testInitializePollingWithInputRegisters() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        testPollingGeneric("input", () -> isRequestOkGeneric(READ_INPUT_REGISTERS));
    }

    @Test
    public void testInitializePollingWithHoldingRegisters() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        testPollingGeneric("holding", () -> isRequestOkGeneric(READ_MULTIPLE_REGISTERS));
    }

    @SuppressWarnings("null")
    @Test
    public void testDisconnectOnDispose() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        // verify registration
        final AtomicReference<ModbusReadCallback> callbackRef = new AtomicReference<>();
        Mockito.verify(modbusManager).registerRegularPoll(MockitoHamcrest.argThat(new TypeSafeMatcher<PollTask>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(PollTask item) {
                callbackRef.set(item.getCallback());
                return checkPollTask(item, READ_COILS);
            }
        }), ArgumentMatchers.eq(150L), ArgumentMatchers.eq(0L));
        Mockito.verifyNoMoreInteractions(modbusManager);
        // reset call counts for easy assertions
        Mockito.reset(modbusManager);
        thingHandler.dispose();
        // 1) should first unregister poll task
        Mockito.verify(modbusManager).unregisterRegularPoll(MockitoHamcrest.argThat(new TypeSafeMatcher<PollTask>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(PollTask item) {
                Assert.assertThat(item.getCallback(), CoreMatchers.is(CoreMatchers.sameInstance(callbackRef.get())));
                return checkPollTask(item, READ_COILS);
            }
        }));
        Mockito.verifyNoMoreInteractions(modbusManager);
    }

    @SuppressWarnings("null")
    @Test
    public void testInitializeWithNoBridge() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OFFLINE)));
        Assert.assertThat(poller.getStatusInfo().getStatusDetail(), CoreMatchers.is(CoreMatchers.equalTo(BRIDGE_OFFLINE)));
        Mockito.verifyNoMoreInteractions(modbusManager);
    }

    @SuppressWarnings("null")
    @Test
    public void testInitializeWithOfflineBridge() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        endpoint.setStatusInfo(new org.eclipse.smarthome.core.thing.ThingStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, ""));
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OFFLINE)));
        Assert.assertThat(poller.getStatusInfo().getStatusDetail(), CoreMatchers.is(CoreMatchers.equalTo(BRIDGE_OFFLINE)));
        Mockito.verifyNoMoreInteractions(modbusManager);
    }

    @Test
    public void testRegistersPassedToChildDataThings() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        ArgumentCaptor<PollTask> pollTaskCapturer = ArgumentCaptor.forClass(PollTask.class);
        Mockito.verify(modbusManager).registerRegularPoll(pollTaskCapturer.capture(), ArgumentMatchers.eq(150L), ArgumentMatchers.eq(0L));
        ModbusReadCallback readCallback = pollTaskCapturer.getValue().getCallback();
        Assert.assertNotNull(readCallback);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusRegisterArray registers = Mockito.mock(ModbusRegisterArray.class);
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        ModbusDataThingHandler child2 = Mockito.mock(ModbusDataThingHandler.class);
        // has one data child
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        readCallback.onRegisters(request, registers);
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyZeroInteractions(child2);
        Mockito.reset(child1);
        // two children (one child initialized)
        thingHandler.childHandlerInitialized(child2, Mockito.mock(Thing.class));
        readCallback.onRegisters(request, registers);
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verify(child2).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
        Mockito.reset(child1);
        Mockito.reset(child2);
        // one child disposed
        thingHandler.childHandlerDisposed(child1, Mockito.mock(Thing.class));
        readCallback.onRegisters(request, registers);
        Mockito.verify(child2).onRegisters(request, registers);
        Mockito.verifyZeroInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
    }

    @Test
    public void testBitsPassedToChildDataThings() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        ArgumentCaptor<PollTask> pollTaskCapturer = ArgumentCaptor.forClass(PollTask.class);
        Mockito.verify(modbusManager).registerRegularPoll(pollTaskCapturer.capture(), ArgumentMatchers.eq(150L), ArgumentMatchers.eq(0L));
        ModbusReadCallback readCallback = pollTaskCapturer.getValue().getCallback();
        Assert.assertNotNull(readCallback);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        BitArray bits = Mockito.mock(BitArray.class);
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        ModbusDataThingHandler child2 = Mockito.mock(ModbusDataThingHandler.class);
        // has one data child
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        readCallback.onBits(request, bits);
        Mockito.verify(child1).onBits(request, bits);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyZeroInteractions(child2);
        Mockito.reset(child1);
        // two children (one child initialized)
        thingHandler.childHandlerInitialized(child2, Mockito.mock(Thing.class));
        readCallback.onBits(request, bits);
        Mockito.verify(child1).onBits(request, bits);
        Mockito.verify(child2).onBits(request, bits);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
        Mockito.reset(child1);
        Mockito.reset(child2);
        // one child disposed
        thingHandler.childHandlerDisposed(child1, Mockito.mock(Thing.class));
        readCallback.onBits(request, bits);
        Mockito.verify(child2).onBits(request, bits);
        Mockito.verifyZeroInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
    }

    @Test
    public void testErrorPassedToChildDataThings() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 150L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        ArgumentCaptor<PollTask> pollTaskCapturer = ArgumentCaptor.forClass(PollTask.class);
        Mockito.verify(modbusManager).registerRegularPoll(pollTaskCapturer.capture(), ArgumentMatchers.eq(150L), ArgumentMatchers.eq(0L));
        ModbusReadCallback readCallback = pollTaskCapturer.getValue().getCallback();
        Assert.assertNotNull(readCallback);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        Exception error = Mockito.mock(Exception.class);
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        ModbusDataThingHandler child2 = Mockito.mock(ModbusDataThingHandler.class);
        // has one data child
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        readCallback.onError(request, error);
        Mockito.verify(child1).onError(request, error);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyZeroInteractions(child2);
        Mockito.reset(child1);
        // two children (one child initialized)
        thingHandler.childHandlerInitialized(child2, Mockito.mock(Thing.class));
        readCallback.onError(request, error);
        Mockito.verify(child1).onError(request, error);
        Mockito.verify(child2).onError(request, error);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
        Mockito.reset(child1);
        Mockito.reset(child2);
        // one child disposed
        thingHandler.childHandlerDisposed(child1, Mockito.mock(Thing.class));
        readCallback.onError(request, error);
        Mockito.verify(child2).onError(request, error);
        Mockito.verifyZeroInteractions(child1);
        Mockito.verifyNoMoreInteractions(child2);
    }

    @Test
    public void testRefresh() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        thingHandler.refresh();
        Mockito.verify(modbusManager).submitOneTimePoll(ArgumentMatchers.any());
    }

    /**
     * When there's no recently received data, refresh() will re-use that instead
     *
     * @throws IllegalArgumentException
     * 		
     * @throws IllegalAccessException
     * 		
     * @throws NoSuchFieldException
     * 		
     * @throws SecurityException
     * 		
     */
    @Test
    public void testRefreshWithPreviousData() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        pollerConfig.put("cacheMillis", 10000L);
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data is received
        ModbusReadCallback pollerReadCallback = getPollerCallback(thingHandler);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusRegisterArray registers = Mockito.mock(ModbusRegisterArray.class);
        pollerReadCallback.onRegisters(request, registers);
        // data child receives the data
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.reset(child1);
        // call refresh
        // cache is still valid, we should not have real data poll this time
        thingHandler.refresh();
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data child receives the cached data
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
    }

    /**
     * When there's no recently received data, refresh() will re-use that instead
     *
     * @throws IllegalArgumentException
     * 		
     * @throws IllegalAccessException
     * 		
     * @throws NoSuchFieldException
     * 		
     * @throws SecurityException
     * 		
     */
    @Test
    public void testRefreshWithPreviousDataCacheDisabled() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        pollerConfig.put("cacheMillis", 0L);
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data is received
        ModbusReadCallback pollerReadCallback = getPollerCallback(thingHandler);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusRegisterArray registers = Mockito.mock(ModbusRegisterArray.class);
        pollerReadCallback.onRegisters(request, registers);
        // data child receives the data
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.reset(child1);
        // call refresh
        // caching disabled, should poll from manager
        thingHandler.refresh();
        Mockito.verify(modbusManager).submitOneTimePoll(ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(modbusManager);
        // data child receives the cached data
        Mockito.verifyNoMoreInteractions(child1);
    }

    /**
     * Testing again caching, such that most recently received data is propagated to children
     *
     * @throws IllegalArgumentException
     * 		
     * @throws IllegalAccessException
     * 		
     * @throws NoSuchFieldException
     * 		
     * @throws SecurityException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRefreshWithPreviousData2() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        pollerConfig.put("cacheMillis", 10000L);
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data is received
        ModbusReadCallback pollerReadCallback = getPollerCallback(thingHandler);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusReadRequestBlueprint request2 = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusRegisterArray registers = Mockito.mock(ModbusRegisterArray.class);
        Exception error = Mockito.mock(Exception.class);
        pollerReadCallback.onRegisters(request, registers);
        // data child should receive the data
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.reset(child1);
        // Sleep to have time between the data
        Thread.sleep(5L);
        // error is received
        pollerReadCallback.onError(request2, error);
        // data child should receive the error
        Mockito.verify(child1).onError(request2, error);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.reset(child1);
        // call refresh, should return latest data (that is, error)
        // cache is still valid, we should not have real data poll this time
        thingHandler.refresh();
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data child receives the cached error
        Mockito.verify(child1).onError(request2, error);
        Mockito.verifyNoMoreInteractions(child1);
    }

    @Test
    public void testRefreshWithOldPreviousData() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException {
        Configuration pollerConfig = new Configuration();
        pollerConfig.put("refresh", 0L);
        pollerConfig.put("start", 5);
        pollerConfig.put("length", 13);
        pollerConfig.put("type", "coil");
        pollerConfig.put("cacheMillis", 10L);
        poller = ModbusPollerThingHandlerTest.createPollerThingBuilder("poller").withConfiguration(pollerConfig).withBridge(endpoint.getUID()).build();
        registerThingToMockRegistry(poller);
        hookStatusUpdates(poller);
        ModbusPollerThingHandlerImpl thingHandler = new ModbusPollerThingHandlerImpl(poller, () -> modbusManager);
        thingHandler.setCallback(thingCallback);
        poller.setHandler(thingHandler);
        hookItemRegistry(thingHandler);
        thingHandler.initialize();
        ModbusDataThingHandler child1 = Mockito.mock(ModbusDataThingHandler.class);
        thingHandler.childHandlerInitialized(child1, Mockito.mock(Thing.class));
        Assert.assertThat(poller.getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        // data is received
        ModbusReadCallback pollerReadCallback = getPollerCallback(thingHandler);
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        ModbusRegisterArray registers = Mockito.mock(ModbusRegisterArray.class);
        pollerReadCallback.onRegisters(request, registers);
        // data child should receive the data
        Mockito.verify(child1).onRegisters(request, registers);
        Mockito.verifyNoMoreInteractions(child1);
        Mockito.reset(child1);
        // Sleep to ensure cache expiry
        Thread.sleep(15L);
        // call refresh. Since cache expired, will poll for more
        Mockito.verify(modbusManager, Mockito.never()).submitOneTimePoll(ArgumentMatchers.any());
        thingHandler.refresh();
        Mockito.verify(modbusManager).submitOneTimePoll(ArgumentMatchers.any());
    }
}


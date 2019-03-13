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
package org.openhab.io.transport.modbus.test;


import com.google.common.collect.ImmutableSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.WriteMultipleCoilsRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.io.transport.modbus.BasicBitArray;
import org.openhab.io.transport.modbus.BasicPollTaskImpl;
import org.openhab.io.transport.modbus.BasicWriteTask;
import org.openhab.io.transport.modbus.BitArray;
import org.openhab.io.transport.modbus.ModbusConnectionException;
import org.openhab.io.transport.modbus.ModbusManagerListener;
import org.openhab.io.transport.modbus.ModbusReadCallback;
import org.openhab.io.transport.modbus.ModbusReadFunctionCode;
import org.openhab.io.transport.modbus.ModbusReadRequestBlueprint;
import org.openhab.io.transport.modbus.ModbusRegisterArray;
import org.openhab.io.transport.modbus.ModbusResponse;
import org.openhab.io.transport.modbus.ModbusSlaveErrorResponseException;
import org.openhab.io.transport.modbus.ModbusSlaveIOException;
import org.openhab.io.transport.modbus.ModbusWriteCallback;
import org.openhab.io.transport.modbus.ModbusWriteRequestBlueprint;
import org.openhab.io.transport.modbus.endpoint.EndpointPoolConfiguration;
import org.openhab.io.transport.modbus.endpoint.ModbusSlaveEndpoint;
import org.openhab.io.transport.modbus.endpoint.ModbusTCPSlaveEndpoint;
import org.openhab.io.transport.modbus.internal.BitArrayWrappingBitVector;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Sami Salonen
 */
public class SmokeTest extends IntegrationTestSupport {
    private static final int COIL_EVERY_N_TRUE = 2;

    private static final int DISCRETE_EVERY_N_TRUE = 3;

    private static final int HOLDING_REGISTER_MULTIPLIER = 1;

    private static final int INPUT_REGISTER_MULTIPLIER = 10;

    /**
     * Test handling of slave error responses. In this case, error code = 2, illegal data address, since no data.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testSlaveReadErrorResponse() throws InterruptedException {
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger okCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Exception> lastError = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 0, 5, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                lastError.set(error);
                errorCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(okCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(errorCount.get(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertTrue(lastError.toString(), ((lastError.get()) instanceof ModbusSlaveErrorResponseException));
    }

    /**
     * Test handling of connection error responses.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testSlaveConnectionError() throws InterruptedException {
        // In the test we have non-responding slave (see http://stackoverflow.com/a/904609), and we use short connection
        // timeout
        ModbusSlaveEndpoint endpoint = new ModbusTCPSlaveEndpoint("10.255.255.1", 9999);
        EndpointPoolConfiguration configuration = new EndpointPoolConfiguration();
        configuration.setConnectTimeoutMillis(100);
        modbusManager.setEndpointPoolConfiguration(endpoint, configuration);
        AtomicInteger okCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Exception> lastError = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 0, 5, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                lastError.set(error);
                errorCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(okCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(errorCount.get(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertTrue(lastError.toString(), ((lastError.get()) instanceof ModbusConnectionException));
    }

    /**
     * Have super slow connection response, eventually resulting as timeout (due to default timeout of 3 s in
     * net.wimpi.modbus.Modbus.DEFAULT_TIMEOUT)
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testIOError() throws InterruptedException {
        artificialServerWait = 30000;
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger okCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Exception> lastError = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 0, 5, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                lastError.set(error);
                errorCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                okCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(15, TimeUnit.SECONDS);
        Assert.assertThat(okCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(lastError.toString(), errorCount.get(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertTrue(lastError.toString(), ((lastError.get()) instanceof ModbusSlaveIOException));
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffReadWithCoil() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Object> lastData = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_COILS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                lastData.set(bits);
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        BitArray bits = ((BitArray) (lastData.get()));
        Assert.assertThat(bits.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
        testCoilValues(bits, 1);
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffReadWithDiscrete() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Object> lastData = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_INPUT_DISCRETES, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                lastData.set(bits);
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        BitArray bits = ((BitArray) (lastData.get()));
        Assert.assertThat(bits.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
        testDiscreteValues(bits, 1);
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffReadWithHolding() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Object> lastData = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                lastData.set(registers);
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        ModbusRegisterArray registers = ((ModbusRegisterArray) (lastData.get()));
        Assert.assertThat(registers.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
        testHoldingValues(registers, 1);
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffReadWithInput() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Object> lastData = new AtomicReference<>();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_INPUT_REGISTERS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                lastData.set(registers);
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimePoll(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        ModbusRegisterArray registers = ((ModbusRegisterArray) (lastData.get()));
        Assert.assertThat(registers.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
        testInputValues(registers, 1);
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffWriteMultipleCoil() throws InterruptedException {
        LoggerFactory.getLogger(this.getClass()).error("STARTING MULTIPLE");
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        AtomicReference<Object> lastData = new AtomicReference<>();
        BitArray bits = new BasicBitArray(true, true, false, false, true, true);
        BasicWriteTask task = new BasicWriteTask(endpoint, new org.openhab.io.transport.modbus.BasicModbusWriteCoilRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, 3, bits, true, 1), new ModbusWriteCallback() {
            @Override
            public void onWriteResponse(ModbusWriteRequestBlueprint request, ModbusResponse response) {
                lastData.set(response);
            }

            @Override
            public void onError(ModbusWriteRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
            }
        });
        modbusManager.submitOneTimeWrite(task);
        waitForAssert(() -> {
            assertThat(unexpectedCount.get(), is(equalTo(0)));
            assertThat(lastData.get(), is(notNullValue()));
            ModbusResponse response = ((ModbusResponse) (lastData.get()));
            assertThat(response.getFunctionCode(), is(equalTo(15)));
            assertThat(modbustRequestCaptor.getAllReturnValues().size(), is(equalTo(1)));
            ModbusRequest request = modbustRequestCaptor.getAllReturnValues().get(0);
            assertThat(request.getFunctionCode(), is(equalTo(15)));
            assertThat(getReference(), is(equalTo(3)));
            assertThat(getBitCount(), is(equalTo(bits.size())));
            assertThat(new BitArrayWrappingBitVector(getCoils(), bits.size()), is(equalTo(bits)));
        }, 6000, 10);
        LoggerFactory.getLogger(this.getClass()).error("ENDINGMULTIPLE");
    }

    /**
     * Write is out-of-bounds, slave should return error
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffWriteMultipleCoilError() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Exception> lastError = new AtomicReference<>();
        BitArray bits = new BasicBitArray(500);
        BasicWriteTask task = new BasicWriteTask(endpoint, new org.openhab.io.transport.modbus.BasicModbusWriteCoilRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, 3, bits, true, 1), new ModbusWriteCallback() {
            @Override
            public void onWriteResponse(ModbusWriteRequestBlueprint request, ModbusResponse response) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusWriteRequestBlueprint request, Exception error) {
                lastError.set(error);
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimeWrite(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertTrue(lastError.toString(), ((lastError.get()) instanceof ModbusSlaveErrorResponseException));
        Assert.assertThat(modbustRequestCaptor.getAllReturnValues().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        ModbusRequest request = modbustRequestCaptor.getAllReturnValues().get(0);
        Assert.assertThat(request.getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(15)));
        Assert.assertThat(getReference(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        Assert.assertThat(getBitCount(), CoreMatchers.is(CoreMatchers.equalTo(bits.size())));
        Assert.assertThat(new BitArrayWrappingBitVector(getCoils(), bits.size()), CoreMatchers.is(CoreMatchers.equalTo(bits)));
    }

    /**
     *
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffWriteSingleCoil() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Object> lastData = new AtomicReference<>();
        BitArray bits = new BasicBitArray(true);
        BasicWriteTask task = new BasicWriteTask(endpoint, new org.openhab.io.transport.modbus.BasicModbusWriteCoilRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, 3, bits, false, 1), new ModbusWriteCallback() {
            @Override
            public void onWriteResponse(ModbusWriteRequestBlueprint request, ModbusResponse response) {
                lastData.set(response);
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusWriteRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimeWrite(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        ModbusResponse response = ((ModbusResponse) (lastData.get()));
        Assert.assertThat(response.getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(5)));
        Assert.assertThat(modbustRequestCaptor.getAllReturnValues().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        ModbusRequest request = modbustRequestCaptor.getAllReturnValues().get(0);
        Assert.assertThat(request.getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(5)));
        Assert.assertThat(getReference(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        Assert.assertThat(getCoil(), CoreMatchers.is(CoreMatchers.equalTo(true)));
    }

    /**
     * Write is out-of-bounds, slave should return error
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testOneOffWriteSingleCoilError() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(1);
        AtomicReference<Exception> lastError = new AtomicReference<>();
        BitArray bits = new BasicBitArray(true);
        BasicWriteTask task = new BasicWriteTask(endpoint, new org.openhab.io.transport.modbus.BasicModbusWriteCoilRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, 300, bits, false, 1), new ModbusWriteCallback() {
            @Override
            public void onWriteResponse(ModbusWriteRequestBlueprint request, ModbusResponse response) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusWriteRequestBlueprint request, Exception error) {
                lastError.set(error);
                callbackCalled.countDown();
            }
        });
        modbusManager.submitOneTimeWrite(task);
        callbackCalled.await(5, TimeUnit.SECONDS);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertTrue(lastError.toString(), ((lastError.get()) instanceof ModbusSlaveErrorResponseException));
        Assert.assertThat(modbustRequestCaptor.getAllReturnValues().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        ModbusRequest request = modbustRequestCaptor.getAllReturnValues().get(0);
        Assert.assertThat(request.getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(5)));
        Assert.assertThat(getReference(), CoreMatchers.is(CoreMatchers.equalTo(300)));
        Assert.assertThat(getCoil(), CoreMatchers.is(CoreMatchers.equalTo(true)));
    }

    /**
     * Testing regular polling of coils
     *
     * Amount of requests is timed, and average poll period is checked
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRegularReadEvery150msWithCoil() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(5);
        AtomicInteger dataReceived = new AtomicInteger();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_COILS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                dataReceived.incrementAndGet();
                try {
                    Assert.assertThat(bits.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
                    testCoilValues(bits, 1);
                } catch (AssertionError e) {
                    unexpectedCount.incrementAndGet();
                }
                callbackCalled.countDown();
            }
        });
        long start = System.currentTimeMillis();
        modbusManager.registerRegularPoll(task, 150, 0);
        callbackCalled.await(5, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        assertPollDetails(unexpectedCount, dataReceived, start, end, 145, 500);
    }

    /**
     * Testing regular polling of holding registers
     *
     * Amount of requests is timed, and average poll period is checked
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testRegularReadEvery150msWithHolding() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(5);
        AtomicInteger dataReceived = new AtomicInteger();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                dataReceived.incrementAndGet();
                try {
                    Assert.assertThat(registers.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
                    testHoldingValues(registers, 1);
                } catch (AssertionError e) {
                    unexpectedCount.incrementAndGet();
                }
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        long start = System.currentTimeMillis();
        modbusManager.registerRegularPoll(task, 150, 0);
        callbackCalled.await(5, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        assertPollDetails(unexpectedCount, dataReceived, start, end, 145, 500);
    }

    @Test
    public void testRegularReadFirstErrorThenOK() throws InterruptedException {
        generateData();
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(5);
        AtomicInteger dataReceived = new AtomicInteger();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                dataReceived.incrementAndGet();
                try {
                    Assert.assertThat(registers.size(), CoreMatchers.is(CoreMatchers.equalTo(15)));
                    testHoldingValues(registers, 1);
                } catch (AssertionError e) {
                    unexpectedCount.incrementAndGet();
                }
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        long start = System.currentTimeMillis();
        modbusManager.registerRegularPoll(task, 150, 0);
        callbackCalled.await(5, TimeUnit.SECONDS);
        modbusManager.unregisterRegularPoll(task);
        long end = System.currentTimeMillis();
        assertPollDetails(unexpectedCount, dataReceived, start, end, 145, 500);
    }

    @Test
    public void testUnregisterPolling() throws InterruptedException {
        ModbusSlaveEndpoint endpoint = getEndpoint();
        AtomicInteger unexpectedCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(3);
        AtomicInteger expectedReceived = new AtomicInteger();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 15, 1), new ModbusReadCallback() {
            @Override
            public void onRegisters(ModbusReadRequestBlueprint request, ModbusRegisterArray registers) {
                expectedReceived.incrementAndGet();
                callbackCalled.countDown();
            }

            @Override
            public void onError(ModbusReadRequestBlueprint request, Exception error) {
                if ((spi.getDigitalInCount()) > 0) {
                    // No errors expected after server filled with data
                    unexpectedCount.incrementAndGet();
                } else {
                    expectedReceived.incrementAndGet();
                    errorCount.incrementAndGet();
                    generateData();
                    callbackCalled.countDown();
                }
            }

            @Override
            public void onBits(ModbusReadRequestBlueprint request, BitArray bits) {
                unexpectedCount.incrementAndGet();
                callbackCalled.countDown();
            }
        });
        long start = System.currentTimeMillis();
        modbusManager.registerRegularPoll(task, 200, 0);
        callbackCalled.await(5, TimeUnit.SECONDS);
        modbusManager.unregisterRegularPoll(task);
        long end = System.currentTimeMillis();
        assertPollDetails(unexpectedCount, expectedReceived, start, end, 190, 600);
        // wait some more and ensure nothing comes back
        Thread.sleep(500);
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @SuppressWarnings("null")
    @Test
    public void testPoolConfigurationWithoutListener() {
        EndpointPoolConfiguration defaultConfig = modbusManager.getEndpointPoolConfiguration(getEndpoint());
        Assert.assertThat(defaultConfig, CoreMatchers.is(CoreMatchers.notNullValue()));
        EndpointPoolConfiguration newConfig = new EndpointPoolConfiguration();
        newConfig.setConnectMaxTries(5);
        modbusManager.setEndpointPoolConfiguration(getEndpoint(), newConfig);
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()).getConnectMaxTries(), CoreMatchers.is(CoreMatchers.equalTo(5)));
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(defaultConfig))));
        // Reset config
        modbusManager.setEndpointPoolConfiguration(getEndpoint(), null);
        // Should matc hdefault
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()), CoreMatchers.is(CoreMatchers.equalTo(defaultConfig)));
    }

    @SuppressWarnings("null")
    @Test
    public void testPoolConfigurationListenerAndChanges() {
        AtomicInteger expectedCount = new AtomicInteger();
        AtomicInteger unexpectedCount = new AtomicInteger();
        CountDownLatch callbackCalled = new CountDownLatch(2);
        addListener(new ModbusManagerListener() {
            @Override
            public void onEndpointPoolConfigurationSet(ModbusSlaveEndpoint endpoint, EndpointPoolConfiguration configuration) {
                if ((((callbackCalled.getCount()) == 2L) && ((configuration.getConnectMaxTries()) == 50)) || (((callbackCalled.getCount()) == 1L) && (configuration == null))) {
                    expectedCount.incrementAndGet();
                } else {
                    unexpectedCount.incrementAndGet();
                }
                callbackCalled.countDown();
            }
        });
        EndpointPoolConfiguration defaultConfig = modbusManager.getEndpointPoolConfiguration(getEndpoint());
        Assert.assertThat(defaultConfig, CoreMatchers.is(CoreMatchers.notNullValue()));
        EndpointPoolConfiguration newConfig = new EndpointPoolConfiguration();
        newConfig.setConnectMaxTries(50);
        modbusManager.setEndpointPoolConfiguration(getEndpoint(), newConfig);
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()).getConnectMaxTries(), CoreMatchers.is(CoreMatchers.equalTo(50)));
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()), CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(defaultConfig))));
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(callbackCalled.getCount(), CoreMatchers.is(CoreMatchers.equalTo(1L)));
        // Reset config
        modbusManager.setEndpointPoolConfiguration(getEndpoint(), null);
        // Should match default
        Assert.assertThat(modbusManager.getEndpointPoolConfiguration(getEndpoint()), CoreMatchers.is(CoreMatchers.equalTo(defaultConfig)));
        // change callback should have been called twice (countdown at zero)
        Assert.assertThat(unexpectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
        Assert.assertThat(expectedCount.get(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(callbackCalled.getCount(), CoreMatchers.is(CoreMatchers.equalTo(0L)));
    }

    @Test
    public void testGetRegisteredRegularPolls() {
        ModbusSlaveEndpoint endpoint = getEndpoint();
        BasicPollTaskImpl task = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 15, 1), null);
        BasicPollTaskImpl task2 = new BasicPollTaskImpl(endpoint, new org.openhab.io.transport.modbus.BasicModbusReadRequestBlueprint(IntegrationTestSupport.SLAVE_UNIT_ID, ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS, 1, 16, 2), null);
        modbusManager.registerRegularPoll(task, 50, 0);
        modbusManager.registerRegularPoll(task2, 50, 0);
        Assert.assertThat(getRegisteredRegularPolls(), CoreMatchers.is(CoreMatchers.equalTo(ImmutableSet.of(task, task2))));
        modbusManager.unregisterRegularPoll(task);
        Assert.assertThat(getRegisteredRegularPolls(), CoreMatchers.is(CoreMatchers.equalTo(ImmutableSet.of(task2))));
    }
}


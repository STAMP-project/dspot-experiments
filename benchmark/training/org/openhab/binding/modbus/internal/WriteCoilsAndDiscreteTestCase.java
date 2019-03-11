/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import net.wimpi.modbus.procimg.DigitalIn;
import net.wimpi.modbus.procimg.DigitalOut;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.binding.modbus.ModbusBindingProvider;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.types.Command;


@RunWith(Parameterized.class)
public class WriteCoilsAndDiscreteTestCase extends TestCaseSupport {
    private static final int BIT_READ_COUNT = 2;

    private static Command[] ZERO_COMMANDS = new Command[]{ OnOffType.OFF, OpenClosedType.CLOSED };

    private static Command[] ONE_COMMANDS = new Command[]{ OnOffType.ON, OpenClosedType.OPEN };

    @SuppressWarnings("serial")
    public static class ExpectedFailure extends AssertionError {}

    private boolean nonZeroOffset;

    private String type;

    private int itemIndex;

    private Command command;

    private boolean expectedValue;

    private boolean coilInitialValue;

    private boolean discreteInitialValue;

    private DigitalIn[] dins;

    private DigitalOut[] douts;

    /**
     *
     *
     * @param serverType
     * 		type of server
     * @param discreteInitialValue
     * 		initial value of the discrete inputs
     * @param coilInitialValue
     * 		initial value of the coils
     * @param nonZeroOffset
     * 		whether to test non-zero start address in modbus binding
     * @param type
     * 		type of the slave (e.g. "holding")
     * @param itemIndex
     * 		index of the item that receives command
     * @param command
     * 		received command
     * @param expectedValue
     * 		expected boolean written to corresponding coil/discrete input.
     */
    public WriteCoilsAndDiscreteTestCase(TestCaseSupport.ServerType serverType, boolean discreteInitialValue, boolean coilInitialValue, boolean nonZeroOffset, String type, int itemIndex, Command command, boolean expectedValue) {
        this.serverType = serverType;
        this.discreteInitialValue = discreteInitialValue;
        this.coilInitialValue = coilInitialValue;
        this.nonZeroOffset = nonZeroOffset;
        this.type = type;
        this.itemIndex = itemIndex;
        this.command = command;
        this.expectedValue = expectedValue;
    }

    /**
     * Test writing of discrete inputs (i.e. digital inputs)/coils (i.e. digital
     * outputs), uses default valuetype
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteDigitalsNoReads() throws Exception {
        // XXX
        /* Test is ignored since current implementation synchronized(storage) on coil commands which throws
        NullPointerException
         */
        if ((type) == (ModbusBindingProvider.TYPE_COIL)) {
            return;
        }
        binding = new ModbusBinding();
        int offset = (nonZeroOffset) ? 1 : 0;
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, offset, 2));
        configureSwitchItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        try {
            binding.receiveCommand(String.format("Item%s", ((itemIndex) + 1)), command);
        } catch (NullPointerException e) {
            if ((type) != (ModbusBindingProvider.TYPE_COIL)) {
                Assert.fail("Expecting NullPointerException only with coil");
            }
            return;
        }
        if ((type) == (ModbusBindingProvider.TYPE_COIL)) {
            String msg = "Should have raised NullPointerException with coil";
            Assert.fail(msg);
        }
        verifyRequests(false);
    }

    @Test
    public void testWriteDigitalsAfterRead() throws Exception {
        binding = new ModbusBinding();
        int offset = (nonZeroOffset) ? 1 : 0;
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, offset, WriteCoilsAndDiscreteTestCase.BIT_READ_COUNT));
        configureSwitchItemBinding(2, TestCaseSupport.SLAVE_NAME, 0);
        // READ -- initializes register
        binding.execute();
        binding.receiveCommand(String.format("Item%s", ((itemIndex) + 1)), command);
        verifyRequests(true);
    }
}


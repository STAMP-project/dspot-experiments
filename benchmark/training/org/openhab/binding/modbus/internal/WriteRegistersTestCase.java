/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;


/**
 * Write registers test
 */
@RunWith(Parameterized.class)
public class WriteRegistersTestCase extends TestCaseSupport {
    private static final int READ_COUNT = 4;

    private static Command[] BOOL_COMMANDS = new Command[]{ OnOffType.OFF, OpenClosedType.CLOSED, OnOffType.ON, OpenClosedType.OPEN };

    private boolean nonZeroOffset;

    private String valueType;

    private String type;

    private int itemIndex;

    private Command command;

    private short[] expectedValue;

    private boolean expectingAssertionError;

    private short[] registerInitialValues;

    private State itemInitialState;

    /* @param serverType

    @param registerInitialValues
    initial registers (each short representing register from index 0)

    @itemInitialState item initial state

    @param nonZeroOffset
    whether to test non-zero start address in modbus binding

    @param type
    type of the slave (e.g. "holding")

    @param valueType value type to use for items

    @param itemIndex
    index of the item that receives command

    @param command
    received command

    @param expectedValue
    expected registers written to registers (in register order).

    @param expectingAssertionError
     */
    public WriteRegistersTestCase(TestCaseSupport.ServerType serverType, short[] registerInitialValues, State itemInitialState, boolean nonZeroOffset, String type, String valueType, int itemIndex, Command command, short[] expectedValue, boolean expectingAssertionError) {
        this.serverType = serverType;
        this.registerInitialValues = registerInitialValues;
        this.itemInitialState = itemInitialState;
        this.nonZeroOffset = nonZeroOffset;
        this.type = type;
        this.valueType = valueType;
        this.itemIndex = itemIndex;
        this.command = command;
        this.expectedValue = expectedValue;
        this.expectingAssertionError = expectingAssertionError;
    }

    /**
     * Test writing
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegistersNoReads() throws Exception {
        binding = new ModbusBinding();
        int offset = (nonZeroOffset) ? 1 : 0;
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, offset, 2));
        configureItems();
        binding.receiveCommand(String.format("Item%s", ((itemIndex) + 1)), command);
        errPrint("verifying: testRegistersNoReads");
        verifyRequests(false);
    }

    @Test
    public void testWriteRegistersAfterRead() throws Exception {
        binding = new ModbusBinding();
        int offset = (nonZeroOffset) ? 1 : 0;
        binding.updated(addSlave(TestCaseSupport.newLongPollBindingConfig(), TestCaseSupport.SLAVE_NAME, type, null, offset, WriteRegistersTestCase.READ_COUNT));
        configureItems();
        // READ -- initializes register
        binding.execute();
        binding.receiveCommand(String.format("Item%s", ((itemIndex) + 1)), command);
        errPrint("verifying: testWriteDigitalsAfterRead");
        verifyRequests(true);
    }
}


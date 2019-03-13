/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.StringItem;
import org.openhab.model.item.binding.BindingConfigParseException;


@RunWith(Parameterized.class)
public class ModbusBindingConfigTest {
    private Item item;

    private String configString;

    private ItemIOConnection[] expectedReadConnections;

    private ItemIOConnection[] expectedWriteConnections;

    private Exception expectedError;

    public ModbusBindingConfigTest(Item item, String configString, ItemIOConnection[] expectedReadConnections, ItemIOConnection[] expectedWriteConnections, Exception expectedError) {
        this.item = item;
        this.configString = configString;
        this.expectedReadConnections = expectedReadConnections;
        this.expectedWriteConnections = expectedWriteConnections;
        this.expectedError = expectedError;
    }

    @Test
    public void testParsing() throws BindingConfigParseException {
        ModbusBindingConfig config;
        try {
            config = new ModbusBindingConfig(item, configString);
        } catch (Exception e) {
            if ((expectedError) != null) {
                Assert.assertThat(e.getClass(), CoreMatchers.is(CoreMatchers.equalTo(expectedError.getClass())));
                Assert.assertThat(e.getMessage(), CoreMatchers.is(CoreMatchers.equalTo(expectedError.getMessage())));
                return;
            } else {
                // unexpected error
                throw e;
            }
        }
        Assert.assertThat(config.getItemClass(), CoreMatchers.is(CoreMatchers.equalTo(StringItem.class)));
        Assert.assertThat(config.getItemName(), CoreMatchers.is(CoreMatchers.equalTo(item.getName())));
        Assert.assertThat(config.getWriteConnections().toArray(), CoreMatchers.is(CoreMatchers.equalTo(expectedWriteConnections)));
        Assert.assertThat(config.getReadConnections().toArray(), CoreMatchers.is(CoreMatchers.equalTo(expectedReadConnections)));
        // Previously polled state should be initialized to null such that new updates are "changes"
        for (ItemIOConnection connection : config.getWriteConnections()) {
            Assert.assertThat(connection.getPreviouslyPolledState(), CoreMatchers.is(CoreMatchers.equalTo(null)));
        }
        for (ItemIOConnection connection : config.getReadConnections()) {
            Assert.assertThat(connection.getPreviouslyPolledState(), CoreMatchers.is(CoreMatchers.equalTo(null)));
        }
    }
}


/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import OnOffType.OFF;
import OnOffType.ON;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhab.binding.modbus.internal.Transformation.TransformationHelperWrapper;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.StringType;
import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationService;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.framework.BundleContext;


/**
 * Tests for items with extended syntax. Run only against TCP server.
 */
public class WriteCoilExtendedItemConfigurationTestCase extends TestCaseSupport {
    private ModbusGenericBindingProvider provider;

    @Test
    public void testWriteCoilItemManyConnections() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/issues/4745
        // item reads from coil index 0, and writes to coil index 1. Both ON and OFF are translated to "true" on the
        // wire
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%1$s:0:trigger=*],>[%1$s:1:trigger=ON,transformation=1],>[%1$s:1:trigger=OFF,transformation=1]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
        // send OFF to the item and ensure that coil is set as specified by the transformation
        binding.receiveCommand("Item1", OFF);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // reset coil
        spi.getDigitalOut(1).set(false);
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
        // send ON to the item
        binding.receiveCommand("Item1", ON);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testWriteCoilItemNonNumericConstantTransformationManyConnections() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/issues/4745
        // item reads from coil index 0, and writes to coil index 1. Both ON and OFF are translated to "true" on the
        // wire
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%1$s:0:trigger=*],>[%1$s:1:trigger=ON,transformation=ON],>[%1$s:1:trigger=OFF,transformation=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
        // send OFF to the item and ensure that coil is set as specified by the transformation
        binding.receiveCommand("Item1", OFF);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testCoilWriteFiltered() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format(">[%1$s:1:trigger=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verifyNoMoreInteractions(eventPublisher);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
        // send OFF to the item -- it is not processed
        binding.receiveCommand("Item1", OFF);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // ON command is processed
        binding.receiveCommand("Item1", ON);
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testRegisterStringItemWriteNumberItemComplexTransformation() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format(">[%1$s:1:trigger=*,transformation=PARSEBOOL()]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getWriteConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("PARSEBOOL".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String ignored, String arg) throws TransformationException {
                                if ((arg.equals("true")) || (arg.equals("T"))) {
                                    return "1";
                                } else
                                    if ((arg.equals("false")) || (arg.equals("F"))) {
                                        return "0";
                                    } else {
                                        throw new AssertionError("unexpected arg in test");
                                    }

                            }
                        };
                    } else {
                        throw new AssertionError("unexpected transformation");
                    }
                }
            });
        }
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", new StringType("T"));
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        binding.receiveCommand("Item1", new StringType("F"));
        Assert.assertThat(spi.getDigitalOut(0).isSet(), CoreMatchers.is(CoreMatchers.equalTo(true)));
        Assert.assertThat(spi.getDigitalOut(1).isSet(), CoreMatchers.is(CoreMatchers.equalTo(false)));
    }
}


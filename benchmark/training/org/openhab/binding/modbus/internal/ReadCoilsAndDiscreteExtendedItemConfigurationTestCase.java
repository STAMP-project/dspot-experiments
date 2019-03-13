/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingProvider.TYPE_COIL;
import ModbusBindingProvider.TYPE_DISCRETE;
import OnOffType.OFF;
import OnOffType.ON;
import java.net.UnknownHostException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.openhab.binding.modbus.internal.Transformation.TransformationHelperWrapper;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationService;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;


/**
 * Tests for items with extended syntax. Run only against TCP server.
 */
@RunWith(Parameterized.class)
public class ReadCoilsAndDiscreteExtendedItemConfigurationTestCase extends TestCaseSupport {
    private ModbusGenericBindingProvider provider;

    private String type;

    public ReadCoilsAndDiscreteExtendedItemConfigurationTestCase(String type) {
        if ((!(type.equals(TYPE_DISCRETE))) && (!(type.equals(TYPE_COIL)))) {
            throw new IllegalStateException("Test does not support this type");
        }
        this.type = type;
    }

    @Test
    public void testSwitchItemSingleReadWithTriggerNoMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:0:trigger=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadWithTriggerMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:0:trigger=OFF]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", OFF);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadWithTriggerMatch2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:1:trigger=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testReadOnlyItemCommandNoEffect() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:1:trigger=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", OFF);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterComplextTransformation() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:1:trigger=*,transformation=MULTIPLY(5)]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getReadConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("MULTIPLY".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String multiplier, String arg) throws TransformationException {
                                return String.valueOf(((Integer.valueOf(multiplier)) * (Integer.valueOf(arg))));
                            }
                        };
                    } else {
                        throw new AssertionError("unexpected transformation");
                    }
                }
            });
        }
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        // boolean was converted to number 5 (True -> 1 -> 1*5 = 5)
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(5));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterComplextTransformation2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format("<[%s:1:trigger=*,transformation=STRINGMULTIPLY(5)]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getReadConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("STRINGMULTIPLY".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String multiplier, String arg) throws TransformationException {
                                return "foob" + (String.valueOf(((Integer.valueOf(multiplier)) * (Integer.valueOf(arg)))));
                            }
                        };
                    } else {
                        throw new AssertionError("unexpected transformation");
                    }
                }
            });
        }
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType(("foob" + 5)));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemReadTwoDiscreteComplexTransformation3() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format("<[%1$s:0:type=STATE,trigger=CHANGED,transformation=STRINGMULTIPLY(5)],<[%1$s:1:type=STATE,trigger=CHANGED,transformation=PLUS(7)]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getReadConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("STRINGMULTIPLY".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String multiplier, String arg) throws TransformationException {
                                return "foobar_" + (String.valueOf(((Integer.valueOf(multiplier)) * (Integer.valueOf(arg)))));
                            }
                        };
                    } else
                        if ("PLUS".equals(transformationServiceName)) {
                            return new TransformationService() {
                                @Override
                                public String transform(String offset, String arg) throws TransformationException {
                                    return "foobar_" + (String.valueOf(((Integer.valueOf(offset)) + (Integer.valueOf(arg)))));
                                }
                            };
                        } else {
                            throw new AssertionError("unexpected transformation");
                        }

                }
            });
        }
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        // two events on the first time(the changed-on-poll check is done for each connection separately)
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_0"));// false * 5 = 0

        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_8"));// true + 7 = 8

        Mockito.verifyNoMoreInteractions(eventPublisher);
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(2);
        waitForRequests(2);
        // no events on the second time, nothing has changed
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // modify server data
        setBinary(0, true);
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(3);
        waitForRequests(3);
        // only the PLUS connection was activated
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_5"));// true * 5 = 5

        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}


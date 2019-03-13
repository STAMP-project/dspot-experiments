/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import ModbusBindingProvider.TYPE_HOLDING;
import ModbusBindingProvider.TYPE_INPUT;
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
public class ReadRegistersExtendedItemConfigurationTestCase extends TestCaseSupport {
    private ModbusGenericBindingProvider provider;

    private String type;

    public ReadRegistersExtendedItemConfigurationTestCase(String type) {
        if ((!(type.equals(TYPE_HOLDING))) && (!(type.equals(TYPE_INPUT)))) {
            throw new IllegalStateException("Test does not support this type");
        }
        this.type = type;
    }

    @Test
    public void testNumberItemSingleReadRegisterDefaults() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:1:trigger=*]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(5));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterDefaults2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:1:trigger=*]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(5));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterWithTriggerMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:1:trigger=5]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(5));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterWithTriggerNoMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        // trigger 999 does not match the state 9
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:1:trigger=999]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterWithValueTypeOverriden() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:0:trigger=*,valueType=int32]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        // 196613 in 32bit 2's complement -> 0x00030005
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(196613));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterWithValueTypeOverriden2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format("<[%s:2:trigger=*,valueType=float32]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        // constructed from 16456 and 62915
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(3.140000104904175));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadRegisterDefaults() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:1:trigger=*]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadRegisterWithTriggerMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:1:type=STATE,trigger=ON]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", ON);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadRegisterWithTriggerMatch2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:2:trigger=OFF]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", OFF);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testSwitchItemSingleReadRegisterWithTriggerNoMatch() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format("<[%s:1:trigger=OFF]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterComplexTransformation() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
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
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(25));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterComplexTransformation2() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
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
                                return "foobar_" + (String.valueOf(((Integer.valueOf(multiplier)) * (Integer.valueOf(arg)))));
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
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_25"));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // Execute again, since updates unchanged a new event should be sent
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(2);
        waitForRequests(2);
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_25"));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemSingleReadRegisterComplexTransformation2DefaultTrigger() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format("<[%s:1:transformation=STRINGMULTIPLY(5)]", TestCaseSupport.SLAVE_NAME));
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
                    } else {
                        throw new AssertionError("unexpected transformation");
                    }
                }
            });
        }
        binding.execute();
        waitForConnectionsReceived(1);
        waitForRequests(1);
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_25"));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // Execute again, since does not update unchanged (slave default) *NO* new event should be sent
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(2);
        waitForRequests(2);
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    public void testNumberItemReadTwoRegistersComplexTransformation3() throws UnknownHostException, BindingConfigParseException, ConfigurationException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format("<[%1$s:1:trigger=CHANGED,transformation=STRINGMULTIPLY(5)],<[%1$s:2:trigger=CHANGED,transformation=PLUS(7)]", TestCaseSupport.SLAVE_NAME));
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
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_25"));
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_7"));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(2);
        waitForRequests(2);
        // no events on the second time, nothing has changed
        Mockito.verifyNoMoreInteractions(eventPublisher);
        // modify server data
        setRegister(2, 5);
        Mockito.reset(eventPublisher);
        binding.execute();
        waitForConnectionsReceived(3);
        waitForRequests(3);
        // only the PLUS connection was activated
        Mockito.verify(eventPublisher).postUpdate("Item1", new StringType("foobar_12"));
        Mockito.verifyNoMoreInteractions(eventPublisher);
    }
}


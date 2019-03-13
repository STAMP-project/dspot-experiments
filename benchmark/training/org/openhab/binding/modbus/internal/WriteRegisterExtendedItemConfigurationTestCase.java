/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.modbus.internal;


import IncreaseDecreaseType.INCREASE;
import OnOffType.OFF;
import StopMoveType.MOVE;
import StopMoveType.STOP;
import UpDownType.DOWN;
import UpDownType.UP;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhab.binding.modbus.internal.Transformation.TransformationHelperWrapper;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.transform.TransformationException;
import org.openhab.core.transform.TransformationService;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.osgi.framework.BundleContext;


/**
 * Tests for items with extended syntax. Run only against TCP server.
 */
public class WriteRegisterExtendedItemConfigurationTestCase extends TestCaseSupport {
    private ModbusGenericBindingProvider provider;

    @Test
    public void testRegisterWriteRollershutterItemManyConnections() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/pull/4654
        provider.processBindingConfiguration("test.items", new RollershutterItem("Item1"), String.format((">[%1$s:0:trigger=UP,transformation=1],>[%1$s:0:trigger=DOWN,transformation=-1]" + ",>[%1$s:1:trigger=MOVE,transformation=1],>[%1$s:1:trigger=STOP,transformation=0]"), TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", UP);
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteIncreaseWithoutRead() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/pull/4654
        provider.processBindingConfiguration("test.items", new DimmerItem("Item1"), String.format(">[%1$s:0],<[%1$s:0]", TestCaseSupport.SLAVE_NAME));
        // binding.execute(); no read
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", INCREASE);
        // Binding cannot execute the command since there is no polled value
        // -> no change in registers
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(9)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteIncreaseWithRead() throws BindingConfigParseException {
        // Read index 1 (value=10) and (INCREASE command) increments it by one -> 11. Written to index 0
        provider.processBindingConfiguration("test.items", new DimmerItem("Item1"), String.format(">[%1$s:0],<[%1$s:1]", TestCaseSupport.SLAVE_NAME));
        binding.execute();// read

        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(10));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", INCREASE);
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(11)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteIncreaseWithRead2() throws BindingConfigParseException {
        // Read index 1 (value=10) and index 0 (value=9).
        // INCREASE command increments last read value (9) by one -> 10. Written to index 0
        provider.processBindingConfiguration("test.items", new DimmerItem("Item1"), String.format(">[%1$s:0],<[%1$s:1],<[%1$s:0]", TestCaseSupport.SLAVE_NAME));
        binding.execute();// read

        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(10));
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(9));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", INCREASE);
        // Binding cannot execute the command since there is no polled value
        // -> no change in registers
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteIncreaseWithRead3() throws BindingConfigParseException {
        // same as testRegisterWriteIncreaseWithRead2 but order of read connections is flipped
        provider.processBindingConfiguration("test.items", new DimmerItem("Item1"), String.format(">[%1$s:0],<[%1$s:0],<[%1$s:1]", TestCaseSupport.SLAVE_NAME));
        binding.execute();// read

        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(10));
        Mockito.verify(eventPublisher).postUpdate("Item1", new DecimalType(9));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", INCREASE);
        // Binding cannot execute the command since there is no polled value
        // -> no change in registers
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(11)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteIncreaseWithTransformation() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new DimmerItem("Item1"), String.format(">[%1$s:0:transformation=3],<[%1$s:0]", TestCaseSupport.SLAVE_NAME));
        Mockito.verifyNoMoreInteractions(eventPublisher);
        binding.receiveCommand("Item1", INCREASE);
        // Binding will be able to write the value even without previously polled value since the transformation
        // converts INCREASE to constant 3
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteRollershutterItemManyConnections2() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/pull/4654
        provider.processBindingConfiguration("test.items", new RollershutterItem("Item1"), String.format((">[%1$s:0:trigger=UP,transformation=1],>[%1$s:0:trigger=DOWN,transformation=-1]" + ",>[%1$s:1:trigger=MOVE,transformation=1],>[%1$s:1:trigger=STOP,transformation=0]"), TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", DOWN);
        // 65535 is same as -1, the SimpleRegister.getValue just returns the unsigned 16bit representation of the
        // register
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(65535)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteRollershutterItemManyConnections3() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/pull/4654
        provider.processBindingConfiguration("test.items", new RollershutterItem("Item1"), String.format((">[%1$s:0:trigger=UP,transformation=1],>[%1$s:0:trigger=DOWN,transformation=-1]" + ",>[%1$s:1:trigger=MOVE,transformation=1],>[%1$s:1:trigger=STOP,transformation=0]"), TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", MOVE);
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(9)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(1)));
    }

    @Test
    public void testRegisterWriteRollershutterItemManyConnections4() throws BindingConfigParseException {
        // Inspired by https://github.com/openhab/openhab/pull/4654
        provider.processBindingConfiguration("test.items", new RollershutterItem("Item1"), String.format((">[%1$s:0:trigger=UP,transformation=1],>[%1$s:0:trigger=DOWN,transformation=-1]" + ",>[%1$s:1:trigger=MOVE,transformation=1],>[%1$s:1:trigger=STOP,transformation=0]"), TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", STOP);
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(9)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test
    public void testRegisterWriteRollershutterWriteFiltered() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new RollershutterItem("Item1"), String.format((">[%1$s:0:trigger=UP,transformation=1],>[%1$s:0:trigger=DOWN,transformation=-1]" + ",>[%1$s:1:trigger=MOVE,transformation=1]"), TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", STOP);
        // Stop was not processed
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(9)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteSwitchItemNonNumbericTransformationAndTwoRegistersManyConnections4() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new SwitchItem("Item1"), String.format(">[%1$s:0:trigger=OFF,transformation=ON],>[%1$s:1:trigger=OFF,transformation=OFF]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", OFF);
        // two registers were changed at the same time
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(0)));
    }

    @Test
    public void testRegisterWritePercentTypeWithTransformation() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format(">[%1$s:0]", TestCaseSupport.SLAVE_NAME));
        binding.execute();
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", new PercentType("3.4"));
        // percent rounded down
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteNumberItemComplexTransformation() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new NumberItem("Item1"), String.format(">[%1$s:0:trigger=*,transformation=MULTIPLY(3)]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getWriteConnections()) {
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
        Mockito.verifyNoMoreInteractions(eventPublisher);// write-only item, no event sent

        binding.receiveCommand("Item1", new DecimalType("4"));
        // two registers were changed at the same time
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(12)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteNumberItemComplexTransformation2() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format(">[%1$s:0:trigger=*,transformation=LEN()]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getWriteConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("LEN".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String multiplier, String arg) throws TransformationException {
                                return String.valueOf(arg.length());
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

        binding.receiveCommand("Item1", new StringType("foob"));
        // two registers were changed at the same time
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(4)));
        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test
    public void testRegisterWriteNumberItemComplexTransformationTwoOutputs() throws BindingConfigParseException {
        provider.processBindingConfiguration("test.items", new StringItem("Item1"), String.format(">[%1$s:0:transformation=CHAR(0)],>[%1$s:1:transformation=CHAR(1)]", TestCaseSupport.SLAVE_NAME));
        ModbusBindingConfig config = provider.getConfig("Item1");
        // Inject transformation
        for (ItemIOConnection itemIOConnection : config.getWriteConnections()) {
            itemIOConnection.getTransformation().setTransformationHelper(new TransformationHelperWrapper() {
                @Override
                public TransformationService getTransformationService(BundleContext context, String transformationServiceName) {
                    if ("CHAR".equals(transformationServiceName)) {
                        return new TransformationService() {
                            @Override
                            public String transform(String index, String arg) throws TransformationException {
                                int charIdx = arg.charAt(Integer.valueOf(index));
                                return String.valueOf(charIdx);
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

        binding.receiveCommand("Item1", new StringType("foob"));
        // two registers were changed at the same time
        Assert.assertThat(spi.getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(102)));// 102 = f

        Assert.assertThat(spi.getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(111)));// 111 = o

    }
}


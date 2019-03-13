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


import ModbusConstants.ValueType.BIT;
import ModbusConstants.ValueType.FLOAT32;
import ModbusConstants.ValueType.INT16;
import ModbusConstants.ValueType.INT32;
import ModbusConstants.ValueType.INT8;
import ModbusReadFunctionCode.READ_COILS;
import ModbusReadFunctionCode.READ_INPUT_DISCRETES;
import ModbusReadFunctionCode.READ_MULTIPLE_REGISTERS;
import ModbusWriteFunctionCode.WRITE_COIL;
import ModbusWriteFunctionCode.WRITE_MULTIPLE_REGISTERS;
import ModbusWriteFunctionCode.WRITE_SINGLE_REGISTER;
import OnOffType.ON;
import RefreshType.REFRESH;
import ThingStatus.OFFLINE;
import ThingStatus.ONLINE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.common.registry.AbstractRegistry;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemBuilder;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemNotUniqueException;
import org.eclipse.smarthome.core.items.ItemProvider;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.ManagedItemProvider;
import org.eclipse.smarthome.core.items.RegistryHook;
import org.eclipse.smarthome.core.library.items.ContactItem;
import org.eclipse.smarthome.core.library.items.DateTimeItem;
import org.eclipse.smarthome.core.library.items.DimmerItem;
import org.eclipse.smarthome.core.library.items.NumberItem;
import org.eclipse.smarthome.core.library.items.RollershutterItem;
import org.eclipse.smarthome.core.library.items.StringItem;
import org.eclipse.smarthome.core.library.items.SwitchItem;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.thing.link.ItemChannelLinkRegistry;
import org.eclipse.smarthome.core.thing.link.ManagedItemChannelLinkProvider;
import org.eclipse.smarthome.core.transform.TransformationException;
import org.eclipse.smarthome.core.transform.TransformationService;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.test.java.JavaTest;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openhab.binding.modbus.internal.handler.ModbusDataThingHandler;
import org.openhab.binding.modbus.internal.handler.ModbusPollerThingHandler;
import org.openhab.io.transport.modbus.BasicModbusRegister;
import org.openhab.io.transport.modbus.ModbusManager;
import org.openhab.io.transport.modbus.ModbusReadFunctionCode;
import org.openhab.io.transport.modbus.ModbusReadRequestBlueprint;
import org.openhab.io.transport.modbus.ModbusRegister;
import org.openhab.io.transport.modbus.PollTask;
import org.openhab.io.transport.modbus.WriteTask;
import org.openhab.io.transport.modbus.endpoint.ModbusSlaveEndpoint;
import org.openhab.io.transport.modbus.endpoint.ModbusTCPSlaveEndpoint;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;


@RunWith(MockitoJUnitRunner.class)
public class ModbusDataHandlerTest extends JavaTest {
    private class ItemChannelLinkRegistryTestImpl extends ItemChannelLinkRegistry {
        private final class ManagedItemChannelLinkProviderExtension extends ManagedItemChannelLinkProvider {
            ManagedItemChannelLinkProviderExtension() {
                setStorageService(new VolatileStorageService());
            }
        }

        public ItemChannelLinkRegistryTestImpl() {
            super();
            this.setThingRegistry(thingRegistry);
            setItemRegistry(itemRegistry);
            ModbusDataHandlerTest.ItemChannelLinkRegistryTestImpl.ManagedItemChannelLinkProviderExtension provider = new ModbusDataHandlerTest.ItemChannelLinkRegistryTestImpl.ManagedItemChannelLinkProviderExtension();
            addProvider(provider);
            setManagedProvider(provider);
        }
    }

    private class ItemRegisteryTestImpl extends AbstractRegistry<Item, String, ItemProvider> implements ItemRegistry {
        private Map<String, Item> items = new ConcurrentHashMap<>();

        private final class ManagedProviderTestImpl extends ManagedItemProvider {
            public ManagedProviderTestImpl() {
                setStorageService(new VolatileStorageService());
            }
        }

        public ItemRegisteryTestImpl() {
            super(null);
            setManagedProvider(new ModbusDataHandlerTest.ItemRegisteryTestImpl.ManagedProviderTestImpl());
        }

        @Override
        @NonNull
        public Item getItem(String name) throws ItemNotFoundException {
            Item item = super.get(name);
            if (item == null) {
                throw new ItemNotFoundException(name);
            }
            return item;
        }

        @Override
        @NonNull
        public Item getItemByPattern(@NonNull
        String name) throws ItemNotFoundException, ItemNotUniqueException {
            throw new IllegalStateException();
        }

        @Override
        @NonNull
        public Collection<@NonNull
        Item> getItems() {
            return items.values();
        }

        @Override
        @NonNull
        public Collection<Item> getItemsOfType(@NonNull
        String type) {
            throw new IllegalStateException();
        }

        @Override
        @NonNull
        public Collection<@NonNull
        Item> getItems(@NonNull
        String pattern) {
            throw new IllegalStateException();
        }

        @Override
        @NonNull
        public Collection<Item> getItemsByTag(@NonNull
        String... tags) {
            throw new IllegalStateException();
        }

        @Override
        @NonNull
        public Collection<Item> getItemsByTagAndType(@NonNull
        String type, @NonNull
        String... tags) {
            throw new IllegalStateException();
        }

        @Override
        @Nullable
        public Item remove(@NonNull
        String itemName, boolean recursive) {
            if (recursive) {
                throw new IllegalStateException();
            }
            return items.remove(itemName);
        }

        @Override
        public void addRegistryHook(RegistryHook<Item> hook) {
            throw new IllegalStateException();
        }

        @Override
        public void removeRegistryHook(RegistryHook<Item> hook) {
            throw new IllegalStateException();
        }

        @Override
        public <T extends Item> @NonNull
        Collection<T> getItemsByTag(@NonNull
        Class<T> typeFilter, @NonNull
        String... tags) {
            throw new IllegalStateException();
        }

        @Override
        public ItemBuilder newItemBuilder(Item item) {
            throw new IllegalStateException();
        }

        @Override
        public ItemBuilder newItemBuilder(String itemType, String itemName) {
            throw new IllegalStateException();
        }
    }

    private static final Map<String, Class<? extends Item>> CHANNEL_TO_ITEM_CLASS = new HashMap<>();

    static {
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_SWITCH, SwitchItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_CONTACT, ContactItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_DATETIME, DateTimeItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_DIMMER, DimmerItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_NUMBER, NumberItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_STRING, StringItem.class);
        ModbusDataHandlerTest.CHANNEL_TO_ITEM_CLASS.put(CHANNEL_ROLLERSHUTTER, RollershutterItem.class);
    }

    private List<Thing> things = new ArrayList<>();

    private List<WriteTask> writeTasks = new ArrayList<>();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ThingHandlerCallback thingCallback;

    @Mock
    private ThingRegistry thingRegistry;

    private ItemRegistry itemRegistry = new ModbusDataHandlerTest.ItemRegisteryTestImpl();

    @Mock
    private ModbusManager manager;

    private ModbusDataHandlerTest.ItemChannelLinkRegistryTestImpl linkRegistry = new ModbusDataHandlerTest.ItemChannelLinkRegistryTestImpl();

    Map<ChannelUID, List<State>> stateUpdates = new HashMap<>();

    private static final Map<String, String> CHANNEL_TO_ACCEPTED_TYPE = new HashMap<>();

    static {
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_SWITCH, "Switch");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_CONTACT, "Contact");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_DATETIME, "DateTime");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_DIMMER, "Dimmer");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_NUMBER, "Number");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_STRING, "String");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_ROLLERSHUTTER, "Rollershutter");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_LAST_READ_SUCCESS, "DateTime");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_LAST_WRITE_SUCCESS, "DateTime");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_LAST_WRITE_ERROR, "DateTime");
        ModbusDataHandlerTest.CHANNEL_TO_ACCEPTED_TYPE.put(CHANNEL_LAST_READ_ERROR, "DateTime");
    }

    @Test
    public void testInitCoilsOutOfIndex() {
        testOutOfBoundsGeneric(4, 3, "8", READ_COILS, BIT, OFFLINE);
    }

    @Test
    public void testInitCoilsOK() {
        testOutOfBoundsGeneric(4, 3, "6", READ_COILS, BIT, ONLINE);
    }

    @Test
    public void testInitRegistersWithBitOutOfIndex() {
        testOutOfBoundsGeneric(4, 3, "8.0", READ_MULTIPLE_REGISTERS, BIT, OFFLINE);
    }

    @Test
    public void testInitRegistersWithBitOutOfIndex2() {
        testOutOfBoundsGeneric(4, 3, "7.16", READ_MULTIPLE_REGISTERS, BIT, OFFLINE);
    }

    @Test
    public void testInitRegistersWithBitOK() {
        testOutOfBoundsGeneric(4, 3, "6.0", READ_MULTIPLE_REGISTERS, BIT, ONLINE);
    }

    @Test
    public void testInitRegistersWithBitOK2() {
        testOutOfBoundsGeneric(4, 3, "6.15", READ_MULTIPLE_REGISTERS, BIT, ONLINE);
    }

    @Test
    public void testInitRegistersWithInt8OutOfIndex() {
        testOutOfBoundsGeneric(4, 3, "8.0", READ_MULTIPLE_REGISTERS, INT8, OFFLINE);
    }

    @Test
    public void testInitRegistersWithInt8OutOfIndex2() {
        testOutOfBoundsGeneric(4, 3, "7.2", READ_MULTIPLE_REGISTERS, INT8, OFFLINE);
    }

    @Test
    public void testInitRegistersWithInt8OK() {
        testOutOfBoundsGeneric(4, 3, "6.0", READ_MULTIPLE_REGISTERS, INT8, ONLINE);
    }

    @Test
    public void testInitRegistersWithInt8OK2() {
        testOutOfBoundsGeneric(4, 3, "6.1", READ_MULTIPLE_REGISTERS, INT8, ONLINE);
    }

    @Test
    public void testInitRegistersWithInt16OK() {
        testOutOfBoundsGeneric(4, 3, "6", READ_MULTIPLE_REGISTERS, INT16, ONLINE);
    }

    @Test
    public void testInitRegistersWithInt16OutOfBounds() {
        testOutOfBoundsGeneric(4, 3, "8", READ_MULTIPLE_REGISTERS, INT16, OFFLINE);
    }

    @Test
    public void testInitRegistersWithInt16NoDecimalFormatAllowed() {
        testOutOfBoundsGeneric(4, 3, "7.0", READ_MULTIPLE_REGISTERS, INT16, OFFLINE);
    }

    @Test
    public void testInitRegistersWithInt32OK() {
        testOutOfBoundsGeneric(4, 3, "5", READ_MULTIPLE_REGISTERS, INT32, ONLINE);
    }

    @Test
    public void testInitRegistersWithInt32OutOfBounds() {
        testOutOfBoundsGeneric(4, 3, "6", READ_MULTIPLE_REGISTERS, INT32, OFFLINE);
    }

    @Test
    public void testInitRegistersWithInt32AtTheEdge() {
        testOutOfBoundsGeneric(4, 3, "5", READ_MULTIPLE_REGISTERS, INT32, ONLINE);
    }

    @SuppressWarnings("null")
    @Test
    public void testOnError() {
        ModbusDataThingHandler dataHandler = testReadHandlingGeneric(READ_MULTIPLE_REGISTERS, "0.0", "default", BIT, null, null, new Exception("fooerror"));
        Assert.assertThat(stateUpdates.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(stateUpdates.get(dataHandler.getThing().getChannel(CHANNEL_LAST_READ_ERROR).getUID()), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testOnRegistersInt16StaticTransformation() {
        ModbusDataThingHandler dataHandler = testReadHandlingGeneric(READ_MULTIPLE_REGISTERS, "0", "-3", INT16, null, new org.openhab.io.transport.modbus.BasicModbusRegisterArray(new ModbusRegister[]{ new BasicModbusRegister(((byte) (255)), ((byte) (253))) }), null);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        // -3 converts to "true"
        assertSingleStateUpdate(dataHandler, CHANNEL_CONTACT, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_SWITCH, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_DIMMER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_NUMBER, new DecimalType((-3)));
        // roller shutter fails since -3 is invalid value (not between 0...100)
        // assertThatStateContains(state, CHANNEL_ROLLERSHUTTER, new PercentType(1));
        assertSingleStateUpdate(dataHandler, CHANNEL_STRING, new StringType("-3"));
        // no datetime, conversion not possible without transformation
    }

    @Test
    public void testOnRegistersRealTransformation() throws InvalidSyntaxException {
        mockTransformation("MULTIPLY", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return String.valueOf(((Integer.parseInt(function)) * (Integer.parseInt(source))));
            }
        });
        ModbusDataThingHandler dataHandler = testReadHandlingGeneric(READ_MULTIPLE_REGISTERS, "0", "MULTIPLY(10)", INT16, null, new org.openhab.io.transport.modbus.BasicModbusRegisterArray(new ModbusRegister[]{ new BasicModbusRegister(((byte) (255)), ((byte) (253))) }), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        // -3 converts to "true"
        assertSingleStateUpdate(dataHandler, CHANNEL_CONTACT, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_SWITCH, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_DIMMER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_NUMBER, new DecimalType((-30)));
        // roller shutter fails since -3 is invalid value (not between 0...100)
        assertSingleStateUpdate(dataHandler, CHANNEL_ROLLERSHUTTER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_STRING, new StringType("-30"));
        // no datetime, conversion not possible without transformation
    }

    @Test
    public void testOnRegistersRealTransformationNoLinks() throws InvalidSyntaxException {
        mockTransformation("MULTIPLY", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return String.valueOf(((Integer.parseInt(function)) * (Integer.parseInt(source))));
            }
        });
        ModbusDataThingHandler dataHandler = // Not linking items and channels
        testReadHandlingGeneric(READ_MULTIPLE_REGISTERS, "0", "MULTIPLY(10)", INT16, null, new org.openhab.io.transport.modbus.BasicModbusRegisterArray(new ModbusRegister[]{ new BasicModbusRegister(((byte) (255)), ((byte) (253))) }), null, bundleContext, false);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_SUCCESS, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        // Since channles are not linked, they are not updated (are null)
        assertSingleStateUpdate(dataHandler, CHANNEL_CONTACT, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_SWITCH, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_DIMMER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_NUMBER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_ROLLERSHUTTER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_STRING, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
    }

    @Test
    public void testOnRegistersRealTransformation2() throws InvalidSyntaxException {
        mockTransformation("ONOFF", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return (Integer.parseInt(source)) != 0 ? "ON" : "OFF";
            }
        });
        ModbusDataThingHandler dataHandler = testReadHandlingGeneric(READ_MULTIPLE_REGISTERS, "0", "ONOFF(10)", INT16, null, new org.openhab.io.transport.modbus.BasicModbusRegisterArray(new ModbusRegister[]{ new BasicModbusRegister(((byte) (255)), ((byte) (253))) }), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_READ_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_CONTACT, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_SWITCH, CoreMatchers.is(CoreMatchers.equalTo(ON)));
        assertSingleStateUpdate(dataHandler, CHANNEL_DIMMER, CoreMatchers.is(CoreMatchers.equalTo(ON)));
        assertSingleStateUpdate(dataHandler, CHANNEL_NUMBER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_ROLLERSHUTTER, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_STRING, CoreMatchers.is(CoreMatchers.equalTo(new StringType("ON"))));
    }

    @Test
    public void testWriteRealTransformation() throws InvalidSyntaxException {
        mockTransformation("MULTIPLY", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return String.valueOf(((Integer.parseInt(function)) * (Integer.parseInt(source))));
            }
        });
        ModbusDataThingHandler dataHandler = testWriteHandlingGeneric("50", "MULTIPLY(10)", BIT, "coil", WRITE_COIL, "number", new DecimalType("2"), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        Assert.assertThat(writeTasks.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        WriteTask writeTask = writeTasks.get(0);
        Assert.assertThat(writeTask.getRequest().getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(WRITE_COIL)));
        Assert.assertThat(writeTask.getRequest().getReference(), CoreMatchers.is(CoreMatchers.equalTo(50)));
        Assert.assertThat(getCoils().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Since transform output is non-zero, it is mapped as "true"
        Assert.assertThat(getCoils().getBit(0), CoreMatchers.is(CoreMatchers.equalTo(true)));
    }

    @Test
    public void testWriteRealTransformation2() throws InvalidSyntaxException {
        mockTransformation("ZERO", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return "0";
            }
        });
        ModbusDataThingHandler dataHandler = testWriteHandlingGeneric("50", "ZERO(foobar)", BIT, "coil", WRITE_COIL, "number", new DecimalType("2"), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        Assert.assertThat(writeTasks.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        WriteTask writeTask = writeTasks.get(0);
        Assert.assertThat(writeTask.getRequest().getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(WRITE_COIL)));
        Assert.assertThat(writeTask.getRequest().getReference(), CoreMatchers.is(CoreMatchers.equalTo(50)));
        Assert.assertThat(getCoils().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        // Since transform output is zero, it is mapped as "false"
        Assert.assertThat(getCoils().getBit(0), CoreMatchers.is(CoreMatchers.equalTo(false)));
    }

    @Test
    public void testWriteRealTransformation3() throws InvalidSyntaxException {
        mockTransformation("RANDOM", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return "5";
            }
        });
        ModbusDataThingHandler dataHandler = testWriteHandlingGeneric("50", "RANDOM(foobar)", INT16, "holding", WRITE_SINGLE_REGISTER, "number", new DecimalType("2"), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        Assert.assertThat(writeTasks.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        WriteTask writeTask = writeTasks.get(0);
        Assert.assertThat(writeTask.getRequest().getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(WRITE_SINGLE_REGISTER)));
        Assert.assertThat(writeTask.getRequest().getReference(), CoreMatchers.is(CoreMatchers.equalTo(50)));
        Assert.assertThat(getRegisters().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(getRegisters().getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(5)));
    }

    @Test
    public void testWriteRealTransformation4() throws InvalidSyntaxException {
        // assertThat(WriteRequestJsonUtilities.fromJson(55, "[{"//
        // + "\"functionCode\": 15,"//
        // + "\"address\": 5412,"//
        // + "\"value\": [1, 0, 5]"//
        // + "}]").toArray(),
        // arrayContaining((Matcher) new CoilMatcher(55, 5412, ModbusWriteFunctionCode.WRITE_MULTIPLE_COILS, true,
        // false, true)));
        mockTransformation("JSON", new TransformationService() {
            @Override
            public String transform(String function, String source) throws TransformationException {
                return "[{"// 
                 + (((((((("\"functionCode\": 16,"// 
                 + "\"address\": 5412,")// 
                 + "\"value\": [1, 0, 5]")// 
                 + "},")// 
                 + "{")// 
                 + "\"functionCode\": 6,")// 
                 + "\"address\": 555,")// 
                 + "\"value\": [3]")// 
                 + "}]");
            }
        });
        ModbusDataThingHandler dataHandler = testWriteHandlingGeneric("50", "JSON(foobar)", INT16, "holding", WRITE_MULTIPLE_REGISTERS, "number", new DecimalType("2"), null, bundleContext);
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_SUCCESS, CoreMatchers.is(CoreMatchers.notNullValue(State.class)));
        assertSingleStateUpdate(dataHandler, CHANNEL_LAST_WRITE_ERROR, CoreMatchers.is(CoreMatchers.nullValue(State.class)));
        Assert.assertThat(writeTasks.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        {
            WriteTask writeTask = writeTasks.get(0);
            Assert.assertThat(writeTask.getRequest().getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(WRITE_MULTIPLE_REGISTERS)));
            Assert.assertThat(writeTask.getRequest().getReference(), CoreMatchers.is(CoreMatchers.equalTo(5412)));
            Assert.assertThat(getRegisters().size(), CoreMatchers.is(CoreMatchers.equalTo(3)));
            Assert.assertThat(getRegisters().getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(1)));
            Assert.assertThat(getRegisters().getRegister(1).getValue(), CoreMatchers.is(CoreMatchers.equalTo(0)));
            Assert.assertThat(getRegisters().getRegister(2).getValue(), CoreMatchers.is(CoreMatchers.equalTo(5)));
        }
        {
            WriteTask writeTask = writeTasks.get(1);
            Assert.assertThat(writeTask.getRequest().getFunctionCode(), CoreMatchers.is(CoreMatchers.equalTo(WRITE_SINGLE_REGISTER)));
            Assert.assertThat(writeTask.getRequest().getReference(), CoreMatchers.is(CoreMatchers.equalTo(555)));
            Assert.assertThat(getRegisters().size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
            Assert.assertThat(getRegisters().getRegister(0).getValue(), CoreMatchers.is(CoreMatchers.equalTo(3)));
        }
    }

    @Test
    public void testCoilDoesNotAcceptFloat32ValueType() {
        testValueTypeGeneric(READ_COILS, FLOAT32, OFFLINE);
    }

    @Test
    public void testCoilAcceptsBitValueType() {
        testValueTypeGeneric(READ_COILS, BIT, ONLINE);
    }

    @Test
    public void testDiscreteInputDoesNotAcceptFloat32ValueType() {
        testValueTypeGeneric(READ_INPUT_DISCRETES, FLOAT32, OFFLINE);
    }

    @Test
    public void testDiscreteInputAcceptsBitValueType() {
        testValueTypeGeneric(READ_INPUT_DISCRETES, BIT, ONLINE);
    }

    @Test
    public void testRefreshOnData() throws InterruptedException {
        ModbusReadFunctionCode functionCode = ModbusReadFunctionCode.READ_COILS;
        ModbusSlaveEndpoint endpoint = new ModbusTCPSlaveEndpoint("thisishost", 502);
        int pollLength = 3;
        // Minimally mocked request
        ModbusReadRequestBlueprint request = Mockito.mock(ModbusReadRequestBlueprint.class);
        Mockito.doReturn(pollLength).when(request).getDataLength();
        Mockito.doReturn(functionCode).when(request).getFunctionCode();
        PollTask task = Mockito.mock(PollTask.class);
        Mockito.doReturn(endpoint).when(task).getEndpoint();
        Mockito.doReturn(request).when(task).getRequest();
        Bridge poller = createPollerMock("poller1", task);
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0");
        dataConfig.put("readTransform", "default");
        dataConfig.put("readValueType", "bit");
        String thingId = "read1";
        ModbusDataThingHandler dataHandler = createDataHandler(thingId, poller, ( builder) -> builder.withConfiguration(dataConfig), bundleContext);
        Assert.assertThat(dataHandler.getThing().getStatus(), CoreMatchers.is(CoreMatchers.equalTo(ONLINE)));
        Mockito.verify(manager, Mockito.never()).submitOneTimePoll(task);
        dataHandler.handleCommand(Mockito.mock(ChannelUID.class), REFRESH);
        // data handler asynchronously calls the poller.refresh() -- it might take some time
        // We check that refresh is finally called
        waitForAssert(() -> verify(((ModbusPollerThingHandler) (poller.getHandler()))).refresh(), 2500, 50);
    }

    @Test
    public void testReadOnlyData() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0");
        dataConfig.put("readValueType", "bit");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    /**
     * readValueType=bit should be assumed with coils, so it's ok to skip it
     */
    @Test
    public void testReadOnlyDataMissingValueTypeWithCoils() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0");
        // missing value type
        testInitGeneric(READ_COILS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testReadOnlyDataInvalidValueType() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0");
        dataConfig.put("readValueType", "foobar");
        testInitGeneric(READ_MULTIPLE_REGISTERS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    /**
     * We do not assume value type with registers, not ok to skip it
     */
    @Test
    public void testReadOnlyDataMissingValueTypeWithRegisters() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0");
        testInitGeneric(READ_MULTIPLE_REGISTERS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteOnlyData() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "bit");
        dataConfig.put("writeType", "coil");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testWriteHoldingInt16Data() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "int16");
        dataConfig.put("writeType", "holding");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testWriteHoldingInt8Data() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "int8");
        dataConfig.put("writeType", "holding");
        testInitGeneric(null, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteHoldingBitData() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "bit");
        dataConfig.put("writeType", "holding");
        testInitGeneric(null, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteOnlyDataChildOfEndpoint() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "bit");
        dataConfig.put("writeType", "coil");
        testInitGeneric(null, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testWriteOnlyDataMissingOneParameter() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeValueType", "bit");
        // missing writeType --> error
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
            assertThat(status.getDescription(), is(not(equalTo(null))));
        });
    }

    /**
     * OK to omit writeValueType with coils since bit is assumed
     */
    @Test
    public void testWriteOnlyDataMissingValueTypeWithCoilParameter() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeType", "coil");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testWriteOnlyIllegalValueType() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeType", "coil");
        dataConfig.put("writeValueType", "foobar");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteInvalidType() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0");
        dataConfig.put("writeType", "foobar");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testNoReadNorWrite() {
        Configuration dataConfig = new Configuration();
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteCoilBadStart() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0.4");
        dataConfig.put("writeType", "coil");
        testInitGeneric(null, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteHoldingBadStart() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("writeStart", "0.4");
        dataConfig.put("writeType", "holding");
        testInitGeneric(null, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testReadHoldingBadStart() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0.0");
        dataConfig.put("readValueType", "int16");
        testInitGeneric(READ_MULTIPLE_REGISTERS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testReadHoldingBadStart2() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0.0");
        dataConfig.put("readValueType", "bit");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testReadHoldingOKStart() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0.0");
        dataConfig.put("readType", "holding");
        dataConfig.put("readValueType", "bit");
        testInitGeneric(READ_MULTIPLE_REGISTERS, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testReadValueTypeIllegal() {
        Configuration dataConfig = new Configuration();
        dataConfig.put("readStart", "0.0");
        dataConfig.put("readType", "holding");
        dataConfig.put("readValueType", "foobar");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteOnlyTransform() {
        Configuration dataConfig = new Configuration();
        // no need to have start, JSON output of transformation defines everything
        dataConfig.put("writeTransform", "JS(myJsonTransform.js)");
        testInitGeneric(null, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }

    @Test
    public void testWriteTransformAndStart() {
        Configuration dataConfig = new Configuration();
        // It's illegal to have start and transform. Just have transform or have all
        dataConfig.put("writeStart", "3");
        dataConfig.put("writeTransform", "JS(myJsonTransform.js)");
        testInitGeneric(READ_COILS, dataConfig, ( status) -> {
            assertThat(status.getStatus(), is(equalTo(ThingStatus.OFFLINE)));
            assertThat(status.getStatusDetail(), is(equalTo(ThingStatusDetail.CONFIGURATION_ERROR)));
        });
    }

    @Test
    public void testWriteTransformAndNecessary() {
        Configuration dataConfig = new Configuration();
        // It's illegal to have start and transform. Just have transform or have all
        dataConfig.put("writeStart", "3");
        dataConfig.put("writeType", "holding");
        dataConfig.put("writeValueType", "int16");
        dataConfig.put("writeTransform", "JS(myJsonTransform.js)");
        testInitGeneric(null, dataConfig, ( status) -> assertThat(status.getStatus(), is(equalTo(ThingStatus.ONLINE))));
    }
}


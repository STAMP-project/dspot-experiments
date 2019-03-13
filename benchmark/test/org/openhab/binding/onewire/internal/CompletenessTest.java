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
package org.openhab.binding.onewire.internal;


import AdvancedMultisensorThingHandler.SUPPORTED_SENSOR_TYPES;
import OwBindingConstants.ACCEPTED_ITEM_TYPES_MAP;
import OwBindingConstants.SENSOR_TYPE_CHANNEL_MAP;
import OwBindingConstants.THING_LABEL_MAP;
import OwBindingConstants.THING_TYPE_COUNTER;
import OwBindingConstants.THING_TYPE_COUNTER2;
import OwBindingConstants.THING_TYPE_DIGITALIO;
import OwBindingConstants.THING_TYPE_DIGITALIO2;
import OwBindingConstants.THING_TYPE_DIGITALIO8;
import OwBindingConstants.THING_TYPE_IBUTTON;
import OwBindingConstants.THING_TYPE_MAP;
import OwBindingConstants.THING_TYPE_MS_TH;
import OwBindingConstants.THING_TYPE_MS_TV;
import OwBindingConstants.THING_TYPE_TEMPERATURE;
import OwSensorType.DS2409;
import OwSensorType.DS2431;
import OwSensorType.EDS;
import OwSensorType.MS_TH_S;
import OwSensorType.UNKNOWN;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.onewire.internal.device.OwSensorType;


/**
 * Tests cases for binding completeness
 *
 * @author Jan N. Klug - Initial contribution
 */
public class CompletenessTest {
    // internal/temporary types, DS2409 (MicroLAN Coupler), DS2431 (EEPROM)
    private static final Set<OwSensorType> IGNORED_SENSOR_TYPES = Collections.unmodifiableSet(Stream.of(DS2409, DS2431, EDS, MS_TH_S, UNKNOWN).collect(Collectors.toSet()));

    private static final Set<OwSensorType> THINGHANDLER_SENSOR_TYPES = Collections.unmodifiableSet(Stream.of(SUPPORTED_SENSOR_TYPES, BasicMultisensorThingHandler.SUPPORTED_SENSOR_TYPES, BasicThingHandler.SUPPORTED_SENSOR_TYPES, EDSSensorThingHandler.SUPPORTED_SENSOR_TYPES).flatMap(Set::stream).collect(Collectors.toSet()));

    private static final Set<ThingTypeUID> DEPRECATED_THING_TYPES = Collections.unmodifiableSet(Stream.of(THING_TYPE_MS_TH, THING_TYPE_MS_TV, THING_TYPE_COUNTER2, THING_TYPE_DIGITALIO, THING_TYPE_DIGITALIO2, THING_TYPE_DIGITALIO8, THING_TYPE_COUNTER2, THING_TYPE_COUNTER, THING_TYPE_IBUTTON, THING_TYPE_TEMPERATURE).collect(Collectors.toSet()));

    @Test
    public void allSupportedTypesInThingHandlerMap() {
        for (OwSensorType sensorType : EnumSet.allOf(OwSensorType.class)) {
            if ((!(THING_TYPE_MAP.containsKey(sensorType))) && (!(CompletenessTest.IGNORED_SENSOR_TYPES.contains(sensorType)))) {
                Assert.fail(("missing thing type map for sensor type " + (sensorType.name())));
            }
        }
    }

    @Test
    public void allSupportedTypesInThingChannelsMap() {
        for (OwSensorType sensorType : EnumSet.allOf(OwSensorType.class)) {
            if ((!(SENSOR_TYPE_CHANNEL_MAP.containsKey(sensorType))) && (!(CompletenessTest.IGNORED_SENSOR_TYPES.contains(sensorType)))) {
                Assert.fail(("missing thing type map for sensor type " + (sensorType.name())));
            }
        }
    }

    @Test
    public void allSensorsSupportedByThingHandlers() {
        for (OwSensorType sensorType : EnumSet.allOf(OwSensorType.class)) {
            if ((!(CompletenessTest.THINGHANDLER_SENSOR_TYPES.contains(sensorType))) && (!(CompletenessTest.IGNORED_SENSOR_TYPES.contains(sensorType)))) {
                Assert.fail(("missing thing handler for sensor type " + (sensorType.name())));
            }
        }
    }

    @Test
    public void allSensorTypesInLabelMap() {
        for (OwSensorType sensorType : EnumSet.allOf(OwSensorType.class)) {
            if ((!(THING_LABEL_MAP.containsKey(sensorType))) && (!(CompletenessTest.IGNORED_SENSOR_TYPES.contains(sensorType)))) {
                Assert.fail(("missing label for sensor type " + (sensorType.name())));
            }
        }
    }

    @Test
    public void acceptedItemTypeMapCompleteness() {
        List<String> channels = Arrays.stream(OwBindingConstants.class.getDeclaredFields()).filter(( f) -> Modifier.isStatic(f.getModifiers())).filter(( f) -> (f.getName().startsWith("CHANNEL")) && (!(f.getName().startsWith("CHANNEL_TYPE")))).map(( f) -> {
            try {
                return ((String) (f.get(null)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("unexpected", e);
            }
        }).collect(Collectors.toList());
        for (String channel : channels) {
            if (!(ACCEPTED_ITEM_TYPES_MAP.containsKey(channel))) {
                Assert.fail(("missing accepted item type for channel " + channel));
            }
        }
    }
}


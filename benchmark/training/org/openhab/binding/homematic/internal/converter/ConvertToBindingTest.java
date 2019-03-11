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
package org.openhab.binding.homematic.internal.converter;


import org.eclipse.smarthome.core.library.types.DecimalType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.converter.type.DecimalTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.QuantityTypeConverter;


/**
 * Tests for {@link AbstractTypeConverter#convertToBinding(org.eclipse.smarthome.core.types.Type, HmDatapoint)}.
 *
 * @author Michael Reitler - Initial Contribution
 */
public class ConvertToBindingTest extends BaseConverterTest {
    @Test
    public void testDecimalTypeConverter() throws ConverterException {
        Object convertedValue;
        TypeConverter<?> dTypeConverter = new DecimalTypeConverter();
        // the binding is backwards compatible, so clients may still use DecimalType, even if a unit is used
        floatDp.setUnit("?C");
        convertedValue = dTypeConverter.convertToBinding(new DecimalType(99.9), floatDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99.9));
        convertedValue = dTypeConverter.convertToBinding(new DecimalType(100.0), floatDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(100.0));
        convertedValue = dTypeConverter.convertToBinding(new DecimalType(99.0), integerDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99));
        convertedValue = dTypeConverter.convertToBinding(new DecimalType(99.9), integerDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99));
    }

    @Test
    public void testQuantityTypeConverter() throws ConverterException {
        Object convertedValue;
        TypeConverter<?> qTypeConverter = new QuantityTypeConverter();
        floatQuantityDp.setUnit("?C");
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Temperature>("99.9 ?C"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99.9));
        floatQuantityDp.setUnit("??C");// at some points datapoints come with such unit instead of ?C

        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Temperature>("451 ?F"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(232.777778));
        floatQuantityDp.setUnit("km/h");
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Speed>("70.07 m/s"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(252.252));
        integerQuantityDp.setUnit("%");
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("99.0 %"), integerQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99));
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("99.9 %"), integerQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(99));
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("1"), integerQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(100));
        floatQuantityDp.setUnit("100%");// not really a unit, but it occurs in homematic datapoints

        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("99.0 %"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(0.99));
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("99.9 %"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(0.999));
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("1"), floatQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(1.0));
        integerQuantityDp.setUnit("Lux");
        convertedValue = qTypeConverter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Illuminance>("42 lx"), integerQuantityDp);
        MatcherAssert.assertThat(convertedValue, CoreMatchers.is(42));
    }

    @Test(expected = ConverterException.class)
    public void testQuantityTypeConverterFailsToConvertDecimalType() throws ConverterException {
        QuantityTypeConverter converter = new QuantityTypeConverter();
        converter.convertToBinding(new DecimalType(99.9), floatDp);
    }

    @Test(expected = ConverterException.class)
    public void testDecimalTypeConverterFailsToConvertQuantityType() throws ConverterException {
        DecimalTypeConverter converter = new DecimalTypeConverter();
        converter.convertToBinding(new org.eclipse.smarthome.core.library.types.QuantityType<javax.measure.quantity.Dimensionless>("99.9 %"), floatDp);
    }
}


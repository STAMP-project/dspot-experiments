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


import QuantityDimension.NONE;
import QuantityDimension.TEMPERATURE;
import QuantityDimension.TIME;
import SmartHomeUnits.HERTZ;
import SmartHomeUnits.PERCENT;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.types.State;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AbstractTypeConverter#convertFromBinding(HmDatapoint)}.
 *
 * @author Michael Reitler - Initial Contribution
 */
public class ConvertFromBindingTest extends BaseConverterTest {
    @Test
    public void testDecimalTypeConverter() throws ConverterException {
        State convertedState;
        TypeConverter<?> decimalConverter = ConverterFactory.createConverter("Number");
        // the binding is backwards compatible, so clients may still use DecimalType, even if a unit is used
        floatDp.setUnit("%");
        floatDp.setValue(99.9);
        convertedState = decimalConverter.convertFromBinding(floatDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(DecimalType.class));
        Assert.assertThat(doubleValue(), CoreMatchers.is(99.9));
        floatDp.setValue(77.77777778);
        convertedState = decimalConverter.convertFromBinding(floatDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(DecimalType.class));
        Assert.assertThat(doubleValue(), CoreMatchers.is(77.777778));
        integerDp.setValue(99.0);
        convertedState = decimalConverter.convertFromBinding(integerDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(DecimalType.class));
        Assert.assertThat(doubleValue(), CoreMatchers.is(99.0));
        integerDp.setValue(99.9);
        convertedState = decimalConverter.convertFromBinding(integerDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(DecimalType.class));
        Assert.assertThat(doubleValue(), CoreMatchers.is(99.0));
    }

    @SuppressWarnings("null")
    @Test
    public void testQuantityTypeConverter() throws ConverterException {
        State convertedState;
        TypeConverter<?> temperatureConverter = ConverterFactory.createConverter("Number:Temperature");
        TypeConverter<?> frequencyConverter = ConverterFactory.createConverter("Number:Frequency");
        TypeConverter<?> timeConverter = ConverterFactory.createConverter("Number:Time");
        floatQuantityDp.setValue(10.5);
        floatQuantityDp.setUnit("?C");
        convertedState = temperatureConverter.convertFromBinding(floatQuantityDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(QuantityType.class));
        Assert.assertThat(((QuantityType<?>) (convertedState)).getDimension(), CoreMatchers.is(TEMPERATURE));
        Assert.assertThat(((QuantityType<?>) (convertedState)).doubleValue(), CoreMatchers.is(10.5));
        Assert.assertThat(doubleValue(), CoreMatchers.is(50.9));
        floatQuantityDp.setUnit("??C");
        Assert.assertThat(((QuantityType<?>) (convertedState)).getDimension(), CoreMatchers.is(TEMPERATURE));
        Assert.assertThat(((QuantityType<?>) (convertedState)).doubleValue(), CoreMatchers.is(10.5));
        integerQuantityDp.setValue(50000);
        integerQuantityDp.setUnit("mHz");
        convertedState = frequencyConverter.convertFromBinding(integerQuantityDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(QuantityType.class));
        Assert.assertThat(((QuantityType<?>) (convertedState)).getDimension(), CoreMatchers.is(NONE.divide(TIME)));
        Assert.assertThat(((QuantityType<?>) (convertedState)).intValue(), CoreMatchers.is(50000));
        Assert.assertThat(((QuantityType<?>) (convertedState)).toUnit(HERTZ).intValue(), CoreMatchers.is(50));
        floatQuantityDp.setValue(0.7);
        floatQuantityDp.setUnit("100%");
        convertedState = timeConverter.convertFromBinding(floatQuantityDp);
        Assert.assertThat(convertedState, CoreMatchers.instanceOf(QuantityType.class));
        Assert.assertThat(((QuantityType<?>) (convertedState)).getDimension(), CoreMatchers.is(NONE));
        Assert.assertThat(((QuantityType<?>) (convertedState)).doubleValue(), CoreMatchers.is(70.0));
        Assert.assertThat(((QuantityType<?>) (convertedState)).getUnit(), CoreMatchers.is(PERCENT));
        Assert.assertThat(doubleValue(), CoreMatchers.is(0.7));
    }
}


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


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.converter.type.DecimalTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.OnOffTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.OpenClosedTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.PercentTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.QuantityTypeConverter;
import org.openhab.binding.homematic.internal.converter.type.StringTypeConverter;


/**
 * Tests for {@link ConverterFactory}.
 *
 * @author Michael Reitler - Initial Contribution
 */
public class ConverterFactoryTest {
    @Test
    public void testTypesOfCreatedConverters() throws ConverterException {
        MatcherAssert.assertThat(ConverterFactory.createConverter("Switch"), CoreMatchers.instanceOf(OnOffTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Rollershutter"), CoreMatchers.instanceOf(PercentTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Dimmer"), CoreMatchers.instanceOf(PercentTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Contact"), CoreMatchers.instanceOf(OpenClosedTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("String"), CoreMatchers.instanceOf(StringTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Number"), CoreMatchers.instanceOf(DecimalTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Number:Temperature"), CoreMatchers.instanceOf(QuantityTypeConverter.class));
        MatcherAssert.assertThat(ConverterFactory.createConverter("Number:Percent"), CoreMatchers.instanceOf(QuantityTypeConverter.class));
    }
}


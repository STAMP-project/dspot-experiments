/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import ListOption.OptionType.PAGE_SIZE;
import ListOption.OptionType.PAGE_TOKEN;
import com.google.cloud.logging.Logging.ListOption;
import com.google.cloud.logging.Option.OptionType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class OptionTest {
    private static final OptionType OPTION_TYPE = OptionType.PAGE_SIZE;

    private static final OptionType ANOTHER_OPTION_TYPE = OptionType.PAGE_TOKEN;

    private static final String VALUE = "some value";

    private static final String OTHER_VALUE = "another value";

    private static final Option OPTION = new Option(OptionTest.OPTION_TYPE, OptionTest.VALUE) {};

    private static final Option OPTION_EQUALS = new Option(OptionTest.OPTION_TYPE, OptionTest.VALUE) {};

    private static final Option OPTION_NOT_EQUALS1 = new Option(OptionTest.ANOTHER_OPTION_TYPE, OptionTest.OTHER_VALUE) {};

    private static final Option OPTION_NOT_EQUALS2 = new Option(OptionTest.ANOTHER_OPTION_TYPE, OptionTest.VALUE) {};

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEquals() {
        Assert.assertEquals(OptionTest.OPTION, OptionTest.OPTION_EQUALS);
        Assert.assertNotEquals(OptionTest.OPTION, OptionTest.OPTION_NOT_EQUALS1);
        Assert.assertNotEquals(OptionTest.OPTION, OptionTest.OPTION_NOT_EQUALS2);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(OptionTest.OPTION.hashCode(), OptionTest.OPTION_EQUALS.hashCode());
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals(OptionTest.OPTION_TYPE, OptionTest.OPTION.getOptionType());
        Assert.assertEquals(OptionTest.VALUE, OptionTest.OPTION.getValue());
        Option option = new Option(OptionTest.OPTION_TYPE, null) {};
        Assert.assertEquals(OptionTest.OPTION_TYPE, option.getOptionType());
        Assert.assertNull(option.getValue());
        thrown.expect(NullPointerException.class);
        new Option(null, OptionTest.VALUE) {};
    }

    @Test
    public void testListOption() {
        Option option = ListOption.pageSize(42);
        Assert.assertEquals(PAGE_SIZE, option.getOptionType());
        Assert.assertEquals(42, option.getValue());
        option = ListOption.pageToken("cursor");
        Assert.assertEquals(PAGE_TOKEN, option.getOptionType());
        Assert.assertEquals("cursor", option.getValue());
    }
}


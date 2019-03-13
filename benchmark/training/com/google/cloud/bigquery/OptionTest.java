/**
 * Copyright 2015 Google LLC
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
package com.google.cloud.bigquery;


import BigQueryRpc.Option;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class OptionTest {
    private static final Option RPC_OPTION = Option.PAGE_TOKEN;

    private static final Option ANOTHER_RPC_OPTION = Option.FIELDS;

    private static final String VALUE = "some value";

    private static final String OTHER_VALUE = "another value";

    private static final Option OPTION = new Option(OptionTest.RPC_OPTION, OptionTest.VALUE) {};

    private static final Option OPTION_EQUALS = new Option(OptionTest.RPC_OPTION, OptionTest.VALUE) {};

    private static final Option OPTION_NOT_EQUALS1 = new Option(OptionTest.RPC_OPTION, OptionTest.OTHER_VALUE) {};

    private static final Option OPTION_NOT_EQUALS2 = new Option(OptionTest.ANOTHER_RPC_OPTION, OptionTest.VALUE) {};

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
        Assert.assertEquals(OptionTest.RPC_OPTION, OptionTest.OPTION.getRpcOption());
        Assert.assertEquals(OptionTest.VALUE, OptionTest.OPTION.getValue());
        Option option = new Option(OptionTest.RPC_OPTION, null) {};
        Assert.assertEquals(OptionTest.RPC_OPTION, option.getRpcOption());
        Assert.assertNull(option.getValue());
        thrown.expect(NullPointerException.class);
        new Option(null, OptionTest.VALUE) {};
    }
}


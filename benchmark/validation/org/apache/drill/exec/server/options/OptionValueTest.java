/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server.options;


import OptionValue.AccessibleScopes.ALL;
import OptionValue.Kind.BOOLEAN;
import OptionValue.Kind.DOUBLE;
import OptionValue.Kind.LONG;
import OptionValue.Kind.STRING;
import OptionValue.OptionScope.SYSTEM;
import org.junit.Assert;
import org.junit.Test;


public class OptionValueTest {
    @Test
    public void createBooleanKindTest() {
        final OptionValue createdValue = OptionValue.create(BOOLEAN, ALL, "myOption", "true", SYSTEM);
        final OptionValue expectedValue = OptionValue.create(ALL, "myOption", true, SYSTEM);
        Assert.assertEquals(expectedValue, createdValue);
    }

    @Test
    public void createDoubleKindTest() {
        final OptionValue createdValue = OptionValue.create(DOUBLE, ALL, "myOption", "1.5", SYSTEM);
        final OptionValue expectedValue = OptionValue.create(ALL, "myOption", 1.5, SYSTEM);
        Assert.assertEquals(expectedValue, createdValue);
    }

    @Test
    public void createLongKindTest() {
        final OptionValue createdValue = OptionValue.create(LONG, ALL, "myOption", "3000", SYSTEM);
        final OptionValue expectedValue = OptionValue.create(ALL, "myOption", 3000L, SYSTEM);
        Assert.assertEquals(expectedValue, createdValue);
    }

    @Test
    public void createStringKindTest() {
        final OptionValue createdValue = OptionValue.create(STRING, ALL, "myOption", "wabalubawubdub", SYSTEM);
        final OptionValue expectedValue = OptionValue.create(ALL, "myOption", "wabalubawubdub", SYSTEM);
        Assert.assertEquals(expectedValue, createdValue);
    }
}


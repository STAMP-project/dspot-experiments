/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.util;


import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for Structs.
 */
// TODO: Test builder operations.
@RunWith(JUnit4.class)
public class StructsTest {
    @Test
    public void testGetStringParameter() throws Exception {
        Map<String, Object> o = makeCloudDictionary();
        Assert.assertEquals("stringValue", Structs.getString(o, "singletonStringKey"));
        Assert.assertEquals("stringValue", Structs.getString(o, "singletonStringKey", "defaultValue"));
        Assert.assertEquals("defaultValue", Structs.getString(o, "missingKey", "defaultValue"));
        try {
            Structs.getString(o, "missingKey");
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("didn't find required parameter missingKey"));
        }
        try {
            Structs.getString(o, "noStringsKey");
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a string"));
        }
        Assert.assertThat(Structs.getStrings(o, "noStringsKey", null), Matchers.emptyIterable());
        Assert.assertThat(Structs.getObject(o, "noStringsKey").keySet(), Matchers.emptyIterable());
        Assert.assertThat(Structs.getDictionary(o, "noStringsKey").keySet(), Matchers.emptyIterable());
        Assert.assertThat(Structs.getDictionary(o, "noStringsKey", null).keySet(), Matchers.emptyIterable());
        try {
            Structs.getString(o, "multipleStringsKey");
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a string"));
        }
        try {
            Structs.getString(o, "emptyKey");
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a string"));
        }
    }

    @Test
    public void testGetBooleanParameter() throws Exception {
        Map<String, Object> o = makeCloudDictionary();
        Assert.assertEquals(true, Structs.getBoolean(o, "singletonBooleanKey", false));
        Assert.assertEquals(false, Structs.getBoolean(o, "missingKey", false));
        try {
            Structs.getBoolean(o, "emptyKey", false);
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a boolean"));
        }
    }

    @Test
    public void testGetLongParameter() throws Exception {
        Map<String, Object> o = makeCloudDictionary();
        Assert.assertEquals(((Long) (42L)), Structs.getLong(o, "singletonLongKey", 666L));
        Assert.assertEquals(((Integer) (42)), Structs.getInt(o, "singletonLongKey", 666));
        Assert.assertEquals(((Long) (666L)), Structs.getLong(o, "missingKey", 666L));
        try {
            Structs.getLong(o, "emptyKey", 666L);
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a long"));
        }
        try {
            Structs.getInt(o, "emptyKey", 666);
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not an int"));
        }
    }

    @Test
    public void testGetListOfMaps() throws Exception {
        Map<String, Object> o = makeCloudDictionary();
        Assert.assertEquals(makeCloudObjects(), Structs.getListOfMaps(o, "multipleObjectsKey", null));
        try {
            Structs.getListOfMaps(o, "singletonLongKey", null);
            Assert.fail("should have thrown an exception");
        } catch (Exception exn) {
            Assert.assertThat(exn.toString(), Matchers.containsString("not a list"));
        }
    }
}


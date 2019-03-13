/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.decisiontable.parser;


import DataListener.NON_MERGED;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesSheetListenerTest {
    @Test
    public void testProperties() {
        final PropertiesSheetListener listener = new PropertiesSheetListener();
        listener.startSheet("test");
        listener.newRow(0, 4);
        listener.newCell(0, 0, "", NON_MERGED);
        listener.newCell(0, 1, "key1", NON_MERGED);
        listener.newCell(0, 2, "value1", NON_MERGED);
        listener.newRow(1, 4);
        listener.newCell(1, 1, "key2", NON_MERGED);
        listener.newCell(1, 3, "value2", NON_MERGED);
        final CaseInsensitiveMap props = listener.getProperties();
        listener.newRow(2, 4);
        listener.newCell(1, 1, "key3", NON_MERGED);
        Assert.assertEquals("value1", props.getSingleProperty("Key1"));
        Assert.assertEquals("value2", props.getSingleProperty("key2"));
    }

    @Test
    public void testCaseInsensitive() {
        CaseInsensitiveMap map = new PropertiesSheetListener.CaseInsensitiveMap();
        map.addProperty("x3", new String[]{ "hey", "B2" });
        map.addProperty("x4", new String[]{ "wHee", "C3" });
        map.addProperty("XXx", new String[]{ "hey2", "D4" });
        Assert.assertNull(map.getProperty("x"));
        Assert.assertEquals("hey", map.getSingleProperty("x3"));
        Assert.assertEquals("hey", map.getSingleProperty("X3"));
        Assert.assertEquals("wHee", map.getSingleProperty("x4"));
        Assert.assertEquals("hey2", map.getSingleProperty("xxx"));
        Assert.assertEquals("hey2", map.getSingleProperty("XXX"));
        Assert.assertEquals("hey2", map.getSingleProperty("XXx"));
        Assert.assertEquals("Whee2", map.getSingleProperty("x", "Whee2"));
    }
}


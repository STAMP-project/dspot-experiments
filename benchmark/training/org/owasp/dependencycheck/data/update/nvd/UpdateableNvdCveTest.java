/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.data.update.nvd;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author Jeremy Long
 */
public class UpdateableNvdCveTest extends BaseTest {
    /**
     * Test of isUpdateNeeded method, of class UpdateableNvdCve.
     */
    @Test
    public void testIsUpdateNeeded() {
        String id = "key";
        String url = new File("target/test-classes/nvdcve-2.0-2012.xml").toURI().toString();
        long timestamp = 42;
        UpdateableNvdCve instance = new UpdateableNvdCve();
        instance.add(id, url, timestamp, false);
        boolean expResult = false;
        boolean result = instance.isUpdateNeeded();
        Assert.assertEquals(expResult, result);
        instance.add("nextId", url, 23, true);
        expResult = true;
        result = instance.isUpdateNeeded();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class UpdateableNvdCve.
     */
    @Test
    public void testAdd() throws Exception {
        String id = "key";
        String url = new File("target/test-classes/nvdcve-1.0-2012.json.gz").toURI().toString();
        long timestamp = 42;
        UpdateableNvdCve instance = new UpdateableNvdCve();
        instance.add(id, url, timestamp, false);
        boolean expResult = false;
        boolean result = instance.isUpdateNeeded();
        Assert.assertEquals(expResult, result);
        instance.add("nextId", url, 23, false);
        NvdCveInfo results = instance.get(id);
        Assert.assertEquals(id, results.getId());
        Assert.assertEquals(url, results.getUrl());
        Assert.assertEquals(timestamp, results.getTimestamp());
    }

    /**
     * Test of clear method, of class UpdateableNvdCve.
     */
    @Test
    public void testClear() {
        String id = "key";
        String url = new File("target/test-classes/nvdcve-1.0-2012.json.gz").toURI().toString();
        long timestamp = 42;
        UpdateableNvdCve instance = new UpdateableNvdCve();
        instance.add(id, url, timestamp, false);
        Assert.assertFalse(instance.getCollection().isEmpty());
        instance.clear();
        Assert.assertTrue(instance.getCollection().isEmpty());
    }

    /**
     * Test of iterator method, of class UpdatableNvdCve.
     */
    @Test
    public void testIterator() {
        String url = new File("target/test-classes/nvdcve-1.0-2012.json.gz").toURI().toString();
        UpdateableNvdCve instance = new UpdateableNvdCve();
        instance.add("one", url, 42, false);
        instance.add("two", url, 23, false);
        instance.add("three", url, 17, false);
        int itemsProcessed = 0;
        for (NvdCveInfo item : instance) {
            if ("one".equals(item.getId())) {
                instance.remove();
            }
            itemsProcessed += 1;
        }
        Assert.assertEquals(3, itemsProcessed);
        Assert.assertEquals(2, instance.getCollection().size());
    }
}


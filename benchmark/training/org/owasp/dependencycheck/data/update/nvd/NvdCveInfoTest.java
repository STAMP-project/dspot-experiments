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


import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 * Rigorous test of setters/getters.
 *
 * @author Jeremy Long
 */
public class NvdCveInfoTest extends BaseTest {
    /**
     * Test of setId and getId method, of class NvdCveInfo.
     */
    @Test
    public void testSetGetId() {
        NvdCveInfo instance = new NvdCveInfo();
        String expResult = "id";
        instance.setId(expResult);
        String result = instance.getId();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getUrl method, of class NvdCveInfo.
     */
    @Test
    public void testSetGetUrl() {
        NvdCveInfo instance = new NvdCveInfo();
        String expResult = "http://www.someurl.com/something";
        instance.setUrl(expResult);
        String result = instance.getUrl();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class NvdCveInfo.
     */
    @Test
    public void testSetGetTimestamp() {
        NvdCveInfo instance = new NvdCveInfo();
        long expResult = 1337L;
        instance.setTimestamp(expResult);
        long result = instance.getTimestamp();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getNeedsUpdate method, of class NvdCveInfo.
     */
    @Test
    public void testSetGetNeedsUpdate() {
        NvdCveInfo instance = new NvdCveInfo();
        boolean expResult = true;
        instance.setNeedsUpdate(expResult);
        boolean result = instance.getNeedsUpdate();
        Assert.assertEquals(expResult, result);
    }
}


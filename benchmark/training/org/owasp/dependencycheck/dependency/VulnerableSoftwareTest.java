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
package org.owasp.dependencycheck.dependency;


import LogicalValue.ANY;
import LogicalValue.NA;
import Part.APPLICATION;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import us.springett.parsers.cpe.exceptions.CpeValidationException;


/**
 *
 *
 * @author Jeremy Long
 */
public class VulnerableSoftwareTest extends BaseTest {
    /**
     * Test of equals method, of class VulnerableSoftware.
     *
     * @throws CpeValidationException
     * 		
     */
    @Test
    public void testEquals() throws CpeValidationException {
        VulnerableSoftwareBuilder builder = new VulnerableSoftwareBuilder();
        VulnerableSoftware obj = null;
        VulnerableSoftware instance = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1").build();
        Assert.assertFalse(instance.equals(obj));
        obj = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1.0").build();
        instance = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1").build();
        Assert.assertFalse(instance.equals(obj));
        obj = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1.0").build();
        instance = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1.0").build();
        Assert.assertTrue(instance.equals(obj));
    }

    /**
     * Test of compareTo method, of class VulnerableSoftware.
     *
     * @throws CpeValidationException
     * 		
     */
    @Test
    public void testCompareTo() throws CpeValidationException {
        VulnerableSoftwareBuilder builder = new VulnerableSoftwareBuilder();
        VulnerableSoftware obj = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1.0").build();
        VulnerableSoftware instance = builder.part(APPLICATION).vendor("mortbay").product("jetty").version("6.1").build();
        int result = instance.compareTo(obj);
        Assert.assertTrue((result < 0));
        obj = builder.part(APPLICATION).vendor("yahoo").product("toolbar").version("3.1.0.20130813024103").build();
        instance = builder.part(APPLICATION).vendor("yahoo").product("toolbar").version("3.1.0.20130813024104").build();
        result = instance.compareTo(obj);
        Assert.assertTrue((result > 0));
    }

    @Test
    public void testCompareVersionRange() throws CpeValidationException {
        VulnerableSoftwareBuilder builder = new VulnerableSoftwareBuilder();
        VulnerableSoftware instance = builder.version("2.0.0").build();
        Assert.assertTrue(instance.compareVersionRange("2.0.0"));
        Assert.assertFalse(instance.compareVersionRange("2.0.1"));
        instance = builder.version(ANY).build();
        Assert.assertTrue(instance.compareVersionRange("2.0.1"));
        instance = builder.version(NA).build();
        Assert.assertFalse(instance.compareVersionRange("2.0.1"));
        instance = builder.version(ANY).versionEndIncluding("2.0.1").build();
        Assert.assertTrue(instance.compareVersionRange("2.0.1"));
        Assert.assertFalse(instance.compareVersionRange("2.0.2"));
        instance = builder.version(ANY).versionEndExcluding("2.0.2").build();
        Assert.assertTrue(instance.compareVersionRange("2.0.1"));
        Assert.assertFalse(instance.compareVersionRange("2.0.2"));
        instance = builder.version(ANY).versionStartIncluding("1.0.1").build();
        Assert.assertTrue(instance.compareVersionRange("1.0.1"));
        Assert.assertFalse(instance.compareVersionRange("1.0.0"));
        instance = builder.version(ANY).versionStartExcluding("1.0.0").build();
        Assert.assertTrue(instance.compareVersionRange("1.0.1"));
        Assert.assertFalse(instance.compareVersionRange("1.0.0"));
    }
}


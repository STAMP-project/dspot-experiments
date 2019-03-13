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
 * Copyright (c) 2015 The OWASP Foundatio. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.pom;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 * Test the PomUtils object.
 *
 * @author Jeremy Long
 */
public class PomUtilsTest extends BaseTest {
    /**
     * Test of readPom method, of class PomUtils.
     *
     * @throws java.lang.Exception
     * 		thrown when the test fails due to an
     * 		exception
     */
    @Test
    public void testReadPom_File() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "dwr-pom.xml");
        String expResult = "Direct Web Remoting";
        Model result = PomUtils.readPom(file);
        Assert.assertEquals(expResult, result.getName());
        expResult = "get ahead";
        Assert.assertEquals(expResult, result.getOrganization());
        expResult = "http://getahead.ltd.uk/dwr";
        Assert.assertEquals(expResult, result.getOrganizationUrl());
        file = BaseTest.getResourceAsFile(this, "jmockit-1.26.pom");
        expResult = "Main ? modified to test issue #710 and #801 (&amps;)";
        result = PomUtils.readPom(file);
        Assert.assertEquals(expResult, result.getName());
    }
}


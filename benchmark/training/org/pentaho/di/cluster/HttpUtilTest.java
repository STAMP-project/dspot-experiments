/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.cluster;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import junit.framework.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


public class HttpUtilTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String STANDART = "(\u256e\u00b0-\u00b0)\u256e\u2533\u2501\u2501\u2533\u30c6\u30fc\u30d6" + "\u30eb(\u256f\u00b0\u25a1\u00b0)\u256f\u253b\u2501\u2501\u253b\u30aa\u30d5";

    /**
     * [PDI-4325] Test that we can decode/encode Strings without loss of data.
     *
     * @throws IOException
     * 		
     * @throws NoSuchAlgorithmException
     * 		
     */
    @Test
    public final void testDecodeBase64ZippedString() throws IOException, NoSuchAlgorithmException {
        String enc64 = this.canonicalBase64Encode(HttpUtilTest.STANDART);
        // decode string
        String decoded = HttpUtil.decodeBase64ZippedString(enc64);
        Assert.assertEquals("Strings are the same after transformation", HttpUtilTest.STANDART, decoded);
    }

    @Test
    public void testConstructUrl() throws Exception {
        Variables variables = new Variables();
        String expected = "hostname:1234/webAppName?param=value";
        Assert.assertEquals(("http://" + expected), HttpUtil.constructUrl(variables, "hostname", String.valueOf(1234), "webAppName", "?param=value"));
        Assert.assertEquals(("http://" + expected), HttpUtil.constructUrl(variables, "hostname", String.valueOf(1234), "webAppName", "?param=value", false));
        Assert.assertEquals(("https://" + expected), HttpUtil.constructUrl(variables, "hostname", String.valueOf(1234), "webAppName", "?param=value", true));
    }

    /**
     * Test that we can encode and decode String using only static class-under-test methods.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEncodeBase64ZippedString() throws IOException {
        String enc64 = HttpUtil.encodeBase64ZippedString(HttpUtilTest.STANDART);
        String decoded = HttpUtil.decodeBase64ZippedString(enc64);
        Assert.assertEquals("Strings are the same after transformation", HttpUtilTest.STANDART, decoded);
    }
}


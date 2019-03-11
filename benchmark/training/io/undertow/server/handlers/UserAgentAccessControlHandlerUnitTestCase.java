/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.handlers;


import io.undertow.testutils.category.UnitTest;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Unit tests for peer security handler
 *
 * @author Andre Dietisheim
 */
@Category(UnitTest.class)
public class UserAgentAccessControlHandlerUnitTestCase {
    private static final String PATTERN_IE_ALL = "Mozilla.+\\(compatible; MSIE .+";

    private static final String PATTERN_IE_ALL_ABOVE_6 = "Mozilla.+\\(compatible; MSIE ([7-9]|1[0-9]).+";

    private static final String PATTERN_FF_ALL = "Mozilla.+\\(.+ Gecko.* Firefox.+";

    private static final String IE_6 = "Mozilla/4.0 (compatible; MSIE 6.1; Windows XP; .NET CLR 1.1.4322; .NET CLR 2.0.50727)";

    private static final String IE_10 = "Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0";

    private static final String FF_25 = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";

    private static final String SAFARI = "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25";

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPattern() {
        addAllow("[bogus");
    }

    @Test
    public void testFalseDefault() {
        Assert.assertFalse(setDefaultAllow(false).isAllowed("some useragent"));
    }

    @Test
    public void testTrueDefault() throws UnknownHostException {
        Assert.assertTrue(setDefaultAllow(true).isAllowed("some useragent"));
    }

    @Test
    public void testNullUserAgent() {
        Assert.assertTrue(setDefaultAllow(true).isAllowed(null));
    }

    @Test
    public void testAllowAllButOne() throws UnknownHostException {
        AccessControlListHandler handler = setDefaultAllow(true).addDeny(UserAgentAccessControlHandlerUnitTestCase.PATTERN_IE_ALL);
        Assert.assertFalse(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.IE_6));
        Assert.assertTrue(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.FF_25));
    }

    @Test
    public void testDenyAllButOne() throws UnknownHostException {
        AccessControlListHandler handler = setDefaultAllow(false).addAllow(UserAgentAccessControlHandlerUnitTestCase.PATTERN_FF_ALL);
        Assert.assertTrue(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.FF_25));
        Assert.assertFalse(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.IE_10));
    }

    @Test
    public void testAllowIE6AndAboveAndAllOthers() throws UnknownHostException {
        AccessControlListHandler handler = addAllow(UserAgentAccessControlHandlerUnitTestCase.PATTERN_IE_ALL_ABOVE_6).addDeny(UserAgentAccessControlHandlerUnitTestCase.PATTERN_IE_ALL);
        Assert.assertFalse(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.IE_6));
        Assert.assertTrue(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.IE_10));
        Assert.assertTrue(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.FF_25));
        Assert.assertTrue(handler.isAllowed(UserAgentAccessControlHandlerUnitTestCase.SAFARI));
    }
}


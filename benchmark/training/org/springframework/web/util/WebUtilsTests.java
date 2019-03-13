/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.MultiValueMap;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class WebUtilsTests {
    @Test
    public void findParameterValue() {
        Map<String, Object> params = new HashMap<>();
        params.put("myKey1", "myValue1");
        params.put("myKey2_myValue2", "xxx");
        params.put("myKey3_myValue3.x", "xxx");
        params.put("myKey4_myValue4.y", new String[]{ "yyy" });
        Assert.assertNull(WebUtils.findParameterValue(params, "myKey0"));
        Assert.assertEquals("myValue1", WebUtils.findParameterValue(params, "myKey1"));
        Assert.assertEquals("myValue2", WebUtils.findParameterValue(params, "myKey2"));
        Assert.assertEquals("myValue3", WebUtils.findParameterValue(params, "myKey3"));
        Assert.assertEquals("myValue4", WebUtils.findParameterValue(params, "myKey4"));
    }

    @Test
    public void parseMatrixVariablesString() {
        MultiValueMap<String, String> variables;
        variables = WebUtils.parseMatrixVariables(null);
        Assert.assertEquals(0, variables.size());
        variables = WebUtils.parseMatrixVariables("year");
        Assert.assertEquals(1, variables.size());
        Assert.assertEquals("", variables.getFirst("year"));
        variables = WebUtils.parseMatrixVariables("year=2012");
        Assert.assertEquals(1, variables.size());
        Assert.assertEquals("2012", variables.getFirst("year"));
        variables = WebUtils.parseMatrixVariables("year=2012;colors=red,blue,green");
        Assert.assertEquals(2, variables.size());
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), variables.get("colors"));
        Assert.assertEquals("2012", variables.getFirst("year"));
        variables = WebUtils.parseMatrixVariables(";year=2012;colors=red,blue,green;");
        Assert.assertEquals(2, variables.size());
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), variables.get("colors"));
        Assert.assertEquals("2012", variables.getFirst("year"));
        variables = WebUtils.parseMatrixVariables("colors=red;colors=blue;colors=green");
        Assert.assertEquals(1, variables.size());
        Assert.assertEquals(Arrays.asList("red", "blue", "green"), variables.get("colors"));
    }

    @Test
    public void isValidOrigin() {
        List<String> allowed = Collections.emptyList();
        Assert.assertTrue(checkValidOrigin("mydomain1.com", (-1), "http://mydomain1.com", allowed));
        Assert.assertFalse(checkValidOrigin("mydomain1.com", (-1), "http://mydomain2.com", allowed));
        allowed = Collections.singletonList("*");
        Assert.assertTrue(checkValidOrigin("mydomain1.com", (-1), "http://mydomain2.com", allowed));
        allowed = Collections.singletonList("http://mydomain1.com");
        Assert.assertTrue(checkValidOrigin("mydomain2.com", (-1), "http://mydomain1.com", allowed));
        Assert.assertFalse(checkValidOrigin("mydomain2.com", (-1), "http://mydomain3.com", allowed));
    }

    @Test
    public void isSameOrigin() {
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com"));
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com:80"));
        Assert.assertTrue(checkSameOrigin("https", "mydomain1.com", 443, "https://mydomain1.com"));
        Assert.assertTrue(checkSameOrigin("https", "mydomain1.com", 443, "https://mydomain1.com:443"));
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", 123, "http://mydomain1.com:123"));
        Assert.assertTrue(checkSameOrigin("ws", "mydomain1.com", (-1), "ws://mydomain1.com"));
        Assert.assertTrue(checkSameOrigin("wss", "mydomain1.com", 443, "wss://mydomain1.com"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain2.com"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain1.com", (-1), "https://mydomain1.com"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain1.com", (-1), "invalid-origin"));
        Assert.assertFalse(checkSameOrigin("https", "mydomain1.com", (-1), "http://mydomain1.com"));
        // Handling of invalid origins as described in SPR-13478
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com/"));
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com:80/"));
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com/path"));
        Assert.assertTrue(checkSameOrigin("http", "mydomain1.com", (-1), "http://mydomain1.com:80/path"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain2.com", (-1), "http://mydomain1.com/"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain2.com", (-1), "http://mydomain1.com:80/"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain2.com", (-1), "http://mydomain1.com/path"));
        Assert.assertFalse(checkSameOrigin("http", "mydomain2.com", (-1), "http://mydomain1.com:80/path"));
        // Handling of IPv6 hosts as described in SPR-13525
        Assert.assertTrue(checkSameOrigin("http", "[::1]", (-1), "http://[::1]"));
        Assert.assertTrue(checkSameOrigin("http", "[::1]", 8080, "http://[::1]:8080"));
        Assert.assertTrue(checkSameOrigin("http", "[2001:0db8:0000:85a3:0000:0000:ac1f:8001]", (-1), "http://[2001:0db8:0000:85a3:0000:0000:ac1f:8001]"));
        Assert.assertTrue(checkSameOrigin("http", "[2001:0db8:0000:85a3:0000:0000:ac1f:8001]", 8080, "http://[2001:0db8:0000:85a3:0000:0000:ac1f:8001]:8080"));
        Assert.assertFalse(checkSameOrigin("http", "[::1]", (-1), "http://[::1]:8080"));
        Assert.assertFalse(checkSameOrigin("http", "[::1]", 8080, "http://[2001:0db8:0000:85a3:0000:0000:ac1f:8001]:8080"));
    }

    // SPR-16262
    @Test
    public void isSameOriginWithXForwardedHeaders() throws Exception {
        String server = "mydomain1.com";
        testWithXForwardedHeaders(server, (-1), "https", null, (-1), "https://mydomain1.com");
        testWithXForwardedHeaders(server, 123, "https", null, (-1), "https://mydomain1.com");
        testWithXForwardedHeaders(server, (-1), "https", "mydomain2.com", (-1), "https://mydomain2.com");
        testWithXForwardedHeaders(server, 123, "https", "mydomain2.com", (-1), "https://mydomain2.com");
        testWithXForwardedHeaders(server, (-1), "https", "mydomain2.com", 456, "https://mydomain2.com:456");
        testWithXForwardedHeaders(server, 123, "https", "mydomain2.com", 456, "https://mydomain2.com:456");
    }

    // SPR-16262
    @Test
    public void isSameOriginWithForwardedHeader() throws Exception {
        String server = "mydomain1.com";
        testWithForwardedHeader(server, (-1), "proto=https", "https://mydomain1.com");
        testWithForwardedHeader(server, 123, "proto=https", "https://mydomain1.com");
        testWithForwardedHeader(server, (-1), "proto=https; host=mydomain2.com", "https://mydomain2.com");
        testWithForwardedHeader(server, 123, "proto=https; host=mydomain2.com", "https://mydomain2.com");
        testWithForwardedHeader(server, (-1), "proto=https; host=mydomain2.com:456", "https://mydomain2.com:456");
        testWithForwardedHeader(server, 123, "proto=https; host=mydomain2.com:456", "https://mydomain2.com:456");
    }
}


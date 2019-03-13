/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.endpoint;


import org.junit.Assert;
import org.junit.Test;


public class EndpointGroupUtilTest {
    private static final String endpointGroupMark = "group:";

    @Test
    public void testGetEndpointGroupName() throws Exception {
        Assert.assertNull(EndpointGroupUtil.getEndpointGroupName("http://myGroupName/"));
        Assert.assertNull(EndpointGroupUtil.getEndpointGroupName("http://myGroupName:8080/xxx"));
        Assert.assertNull(EndpointGroupUtil.getEndpointGroupName("http://group1:myGroupName:8080/"));
        Assert.assertNull(EndpointGroupUtil.getEndpointGroupName("http://username:password@myGroupName:8080/"));
        Assert.assertEquals("myGroupName", EndpointGroupUtil.getEndpointGroupName((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName/")));
        Assert.assertEquals("myGroupName", EndpointGroupUtil.getEndpointGroupName((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/")));
        Assert.assertEquals("myGroupName", EndpointGroupUtil.getEndpointGroupName((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/")));
        Assert.assertEquals("myGroupName", EndpointGroupUtil.getEndpointGroupName((("http://username:password@" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/")));
    }

    @Test
    public void testReplace() throws Exception {
        final String replacement = "127.0.0.1:1234";
        Assert.assertEquals("http://myGroupName/", EndpointGroupUtil.replaceEndpointGroup("http://myGroupName/", replacement));
        Assert.assertEquals("http://myGroupName:8080/xxx", EndpointGroupUtil.replaceEndpointGroup("http://myGroupName:8080/xxx", replacement));
        Assert.assertEquals("http://group1:myGroupName:8080/", EndpointGroupUtil.replaceEndpointGroup("http://group1:myGroupName:8080/", replacement));
        Assert.assertEquals("http://username:password@myGroupName:8080/", EndpointGroupUtil.replaceEndpointGroup("http://username:password@myGroupName:8080/", replacement));
        Assert.assertEquals("http://127.0.0.1:1234/", EndpointGroupUtil.replaceEndpointGroup((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName/"), replacement));
        Assert.assertEquals("http://127.0.0.1:1234/", EndpointGroupUtil.replaceEndpointGroup((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/"), replacement));
        Assert.assertEquals("http://127.0.0.1:1234/xxx", EndpointGroupUtil.replaceEndpointGroup((("http://" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/xxx"), replacement));
        Assert.assertEquals("http://username:password@127.0.0.1:1234/xxx", EndpointGroupUtil.replaceEndpointGroup((("http://username:password@" + (EndpointGroupUtilTest.endpointGroupMark)) + "myGroupName:8080/xxx"), replacement));
    }
}


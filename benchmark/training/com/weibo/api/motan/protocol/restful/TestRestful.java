/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.protocol.restful;


import Status.OK;
import com.weibo.api.motan.config.RefererConfig;
import com.weibo.api.motan.config.ServiceConfig;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public class TestRestful {
    private ServiceConfig<HelloResource> serviceConfig;

    private RefererConfig<HelloResource> refererConfig;

    private HelloResource resource;

    @Test
    public void testPrimitiveType() {
        Assert.assertEquals("helloworld", resource.testPrimitiveType());
    }

    @Test
    public void testCookie() {
        List<HelloResource.User> users = resource.hello(23);
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.get(0).getId(), 23);
        Assert.assertEquals(users.get(0).getName(), "de");
    }

    @Test
    public void testReturnResponse() {
        Response resp = resource.add(2, "de");
        Assert.assertEquals(resp.getStatus(), OK.getStatusCode());
        Assert.assertEquals(resp.getCookies().size(), 1);
        Assert.assertEquals(resp.getCookies().get("ck").getName(), "ck");
        Assert.assertEquals(resp.getCookies().get("ck").getValue(), "2");
        HelloResource.User user = resp.readEntity(HelloResource.User.class);
        resp.close();
        Assert.assertEquals(user.getId(), 2);
        Assert.assertEquals(user.getName(), "de");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testException() {
        resource.testException();
    }
}


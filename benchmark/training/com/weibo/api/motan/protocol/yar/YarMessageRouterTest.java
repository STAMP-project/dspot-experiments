/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.weibo.api.motan.protocol.yar;


import com.weibo.api.motan.protocol.yar.annotation.YarConfig;
import com.weibo.api.motan.rpc.DefaultProvider;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;
import com.weibo.yar.YarRequest;
import com.weibo.yar.YarResponse;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown YarMessageRouterTest
 * @author zhanglei
 * @unknown 2016?7?27?
 */
public class YarMessageRouterTest {
    YarMessageRouterTest.TestYarMessageRouter router = new YarMessageRouterTest.TestYarMessageRouter();

    DefaultResponse response;

    String requestPath = "/test/anno_path";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testHandle() {
        response = new DefaultResponse();
        response.setValue("test");
        response.setProcessTime(1);
        Provider provider = new DefaultProvider(null, null, YarMessageRouterTest.AnnoService.class);
        router.addProvider(provider);
        YarRequest yarRequest = new YarRequest(1, "JSON", "hello", new Object[]{ "params" });
        yarRequest.setRequestPath(requestPath);
        YarResponse yarResponse = ((YarResponse) (router.handle(null, yarRequest)));
        Assert.assertEquals(YarProtocolUtil.convert(response, "JSON"), yarResponse);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testAddProvider() {
        Provider provider = new DefaultProvider(null, null, YarMessageRouterTest.AnnoService.class);
        router.addProvider(provider);
        Assert.assertTrue(router.checkProvider(requestPath));
        router.removeProvider(provider);
        Assert.assertFalse(router.checkProvider(requestPath));
        URL url = new URL("motan", "localhost", 8002, "urlpath");
        provider = new DefaultProvider(null, url, YarMessageRouterTest.normalService.class);
        router.addProvider(provider);
        Assert.assertTrue(router.checkProvider(YarProtocolUtil.getYarPath(YarMessageRouterTest.normalService.class, url)));
        router.removeProvider(provider);
        Assert.assertFalse(router.checkProvider(YarProtocolUtil.getYarPath(YarMessageRouterTest.normalService.class, url)));
    }

    class TestYarMessageRouter extends YarMessageRouter {
        public boolean checkProvider(String path) {
            return providerMap.containsKey(path);
        }

        @Override
        protected Response call(Request request, Provider<?> provider) {
            return response;
        }
    }

    @YarConfig(path = "/test/anno_path")
    interface AnnoService {
        String hello(String name);
    }

    interface normalService {
        String hello(String name);
    }
}


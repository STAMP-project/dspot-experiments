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


import com.weibo.api.motan.rpc.DefaultRequest;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.util.ReflectUtil;
import com.weibo.yar.YarRequest;
import com.weibo.yar.YarResponse;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @unknown YarProtocolUtilTest
 * @author zhanglei
 * @unknown 2016?7?27?
 */
public class YarProtocolUtilTest {
    @Test
    public void testGetYarPath() {
        String path = YarProtocolUtil.getYarPath(YarMessageRouterTest.AnnoService.class, null);
        Assert.assertEquals("/test/anno_path", path);
        URL url = new URL("motan", "localhost", 8002, "testpath");
        path = YarProtocolUtil.getYarPath(null, url);
        Assert.assertEquals(((("/" + (url.getGroup())) + "/") + (url.getPath())), path);
    }

    @Test
    public void testConvertYarRequest() throws NoSuchMethodException, SecurityException {
        DefaultRequest request = new DefaultRequest();
        request.setRequestId(123);
        request.setMethodName("hello");
        request.setArguments(new Object[]{ "param1" });
        request.setInterfaceName(YarMessageRouterTest.AnnoService.class.getName());
        request.setParamtersDesc(ReflectUtil.getMethodParamDesc(YarMessageRouterTest.AnnoService.class.getMethod("hello", String.class)));
        YarRequest yarRequest = YarProtocolUtil.convert(request, "JSON");
        Assert.assertNotNull(yarRequest);
        Request newRequest = YarProtocolUtil.convert(yarRequest, YarMessageRouterTest.AnnoService.class);
        Assert.assertNotNull(newRequest);
        Assert.assertEquals(request.toString(), newRequest.toString());
    }

    // test string cast primitive value
    @Test
    public void testConvertRequest() throws Exception {
        String methodName = "testParam";
        Class[] paramClazz = new Class[]{ int.class, long.class, boolean.class, float.class, double.class };
        Method method = YarProtocolUtilTest.MethodTestService.class.getDeclaredMethod(methodName, paramClazz);
        final String result = "succ";
        YarProtocolUtilTest.MethodTestService service = new YarProtocolUtilTest.MethodTestService() {
            @Override
            public String testParam(int intParam, long longParam, boolean booleanParam, float floatParam, double doubleParam) {
                return result;
            }
        };
        // string
        Object[] params = new Object[]{ "234", "567", "true", "789.12", "678.12" };
        verifyMethodParam(YarProtocolUtilTest.MethodTestService.class, service, method, params, result);
        // number
        params = new Object[]{ 234L, 567, false, 789.12, 678.12F };
        verifyMethodParam(YarProtocolUtilTest.MethodTestService.class, service, method, params, result);
    }

    @Test
    public void testConvertYarResponse() {
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(456);
        response.setValue("stringValue");
        YarResponse yarResponse = YarProtocolUtil.convert(response, "JSON");
        Assert.assertNotNull(yarResponse);
        Response newResponse = YarProtocolUtil.convert(yarResponse);
        Assert.assertEquals(response.getRequestId(), newResponse.getRequestId());
        Assert.assertEquals(response.getValue(), newResponse.getValue());
        response.setException(new RuntimeException("test exception"));
        yarResponse = YarProtocolUtil.convert(response, "JSON");
        Assert.assertNotNull(yarResponse);
        newResponse = YarProtocolUtil.convert(yarResponse);
        Assert.assertEquals(response.getRequestId(), newResponse.getRequestId());
        // yarresponse??????motan????
        Assert.assertEquals(getMessage(), getMessage());
    }

    @Test
    public void testBuildDefaultErrorResponse() {
        String errMsg = "test err";
        String packagerName = "MSGPACK";
        YarResponse response = YarProtocolUtil.buildDefaultErrorResponse(errMsg, packagerName);
        Assert.assertNotNull(response);
        Assert.assertEquals(errMsg, response.getError());
        Assert.assertEquals(packagerName, response.getPackagerName());
    }

    interface MethodTestService {
        String testParam(int intParam, long longParam, boolean booleanParam, float floatParam, double doubleParam);
    }
}


/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.api.services;


import Request.Type;
import Request.Type.DELETE;
import Request.Type.GET;
import Request.Type.POST;
import Request.Type.PUT;
import Request.Type.QUERY_POST;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.ambari.server.api.resources.ResourceInstance;
import org.apache.ambari.server.api.services.parsers.RequestBodyParser;
import org.apache.ambari.server.api.services.serializers.ResultSerializer;
import org.apache.ambari.server.audit.request.RequestAuditLogger;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Base class for service unit tests.
 */
@RunWith(EasyMockRunner.class)
public abstract class BaseServiceTest {
    protected ResourceInstance resourceInstance = createNiceMock(ResourceInstance.class);

    protected RequestFactory requestFactory = createStrictMock(RequestFactory.class);

    protected Request request = createNiceMock(Request.class);

    protected HttpHeaders httpHeaders = createNiceMock(HttpHeaders.class);

    protected UriInfo uriInfo = createNiceMock(UriInfo.class);

    protected Result result = createMock(Result.class);

    protected RequestBody requestBody = createNiceMock(RequestBody.class);

    protected RequestBodyParser bodyParser = createStrictMock(RequestBodyParser.class);

    protected ResultStatus status = createNiceMock(ResultStatus.class);

    protected ResultSerializer serializer = createStrictMock(ResultSerializer.class);

    protected Object serializedResult = new Object();

    @Mock(type = MockType.NICE)
    public RequestAuditLogger requestAuditLogger;

    @Test
    public void testService() throws Exception {
        List<BaseServiceTest.ServiceTestInvocation> listTestInvocations = getTestInvocations();
        for (BaseServiceTest.ServiceTestInvocation testInvocation : listTestInvocations) {
            testMethod(testInvocation);
            testMethod_bodyParseException(testInvocation);
            testMethod_resultInErrorState(testInvocation);
        }
    }

    public static class ServiceTestInvocation {
        private Type m_type;

        private BaseService m_instance;

        private Method m_method;

        private Object[] m_args;

        private String m_body;

        private static final Map<Request.Type, Integer> mapStatusCodes = new HashMap<>();

        static {
            BaseServiceTest.ServiceTestInvocation.mapStatusCodes.put(GET, 200);
            BaseServiceTest.ServiceTestInvocation.mapStatusCodes.put(POST, 201);
            BaseServiceTest.ServiceTestInvocation.mapStatusCodes.put(PUT, 200);
            BaseServiceTest.ServiceTestInvocation.mapStatusCodes.put(DELETE, 200);
            BaseServiceTest.ServiceTestInvocation.mapStatusCodes.put(QUERY_POST, 201);
        }

        public ServiceTestInvocation(Request.Type requestType, BaseService instance, Method method, Object[] args, String body) {
            m_type = requestType;
            m_instance = instance;
            m_method = method;
            m_args = args;
            m_body = body;
        }

        public int getStatusCode() {
            return BaseServiceTest.ServiceTestInvocation.mapStatusCodes.get(m_type);
        }

        public Type getRequestType() {
            return m_type;
        }

        public String getBody() {
            return m_body;
        }

        public Response invoke() throws IllegalAccessException, InvocationTargetException {
            return ((Response) (m_method.invoke(m_instance, m_args)));
        }
    }
}


/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.dubbo.provider;


import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.proxy.AbstractProxyInvoker;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import com.navercorp.pinpoint.test.plugin.TraceObjectManagable;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jinkai.Ma
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.alibaba:dubbo:2.5.3", "org.mockito:mockito-all:1.8.4" })
@TraceObjectManagable
public class DubboProviderIT {
    public static final String META_DO_NOT_TRACE = "_DUBBO_DO_NOT_TRACE";

    public static final String META_TRANSACTION_ID = "_DUBBO_TRASACTION_ID";

    public static final String META_SPAN_ID = "_DUBBO_SPAN_ID";

    public static final String META_PARENT_SPAN_ID = "_DUBBO_PARENT_SPAN_ID";

    public static final String META_PARENT_APPLICATION_NAME = "_DUBBO_PARENT_APPLICATION_NAME";

    public static final String META_PARENT_APPLICATION_TYPE = "_DUBBO_PARENT_APPLICATION_TYPE";

    public static final String META_FLAGS = "_DUBBO_FLAGS";

    @Mock
    private RpcInvocation rpcInvocation;

    private URL url;

    @Mock
    private Directory directory;

    @Mock
    private Invoker invoker;

    @Test
    public void testProvider() throws NoSuchMethodException {
        AbstractProxyInvoker abstractProxyInvoker = new AbstractProxyInvoker(new String(), String.class, url) {
            @Override
            protected Object doInvoke(Object proxy, String methodName, Class[] parameterTypes, Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
        try {
            abstractProxyInvoker.invoke(rpcInvocation);
        } catch (RpcException ignore) {
            ignore.printStackTrace();
        }
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTraceCount(2);
    }

    @Test
    public void testDoNotTrace() throws Exception {
        Mockito.when(rpcInvocation.getAttachment(DubboProviderIT.META_DO_NOT_TRACE)).thenReturn("1");
        AbstractProxyInvoker abstractProxyInvoker = new AbstractProxyInvoker(new String(), String.class, url) {
            @Override
            protected Object doInvoke(Object proxy, String methodName, Class[] parameterTypes, Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
        try {
            abstractProxyInvoker.invoke(rpcInvocation);
        } catch (RpcException ignore) {
            ignore.printStackTrace();
        }
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTraceCount(0);
    }
}


/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.server.bolt;


import RpcErrorType.SERVER_UNDECLARED_ERROR;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.EchoService;
import com.alipay.sofa.rpc.test.HelloService;
import com.alipay.sofa.rpc.test.HelloServiceImpl;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * ???????? ??????? ??
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class NotFoundInvokerTest extends ActivelyDestroyTest {
    @Test
    public void testAll() {
        ServerConfig serverConfig = new ServerConfig().setStopTimeout(0).setPort(22222).setQueues(100).setCoreThreads(10).setMaxThreads(10);
        // ??????????????1?
        ProviderConfig<HelloService> providerConfig = new ProviderConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setRef(new HelloServiceImpl(1500)).setServer(serverConfig).setRegister(false);
        providerConfig.export();
        ConsumerConfig<HelloService> consumerConfig = new ConsumerConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setDirectUrl("bolt://127.0.0.1:22222").setTimeout(30000).setFilterRef(Collections.<Filter>singletonList(new Filter() {
            @Override
            public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {
                request.setMethodName(((request.getMethodName()) + "_unknown"));// ??????

                return invoker.invoke(request);
            }
        })).setRegister(false);
        HelloService helloService = consumerConfig.refer();
        boolean ok = true;
        try {
            helloService.sayHello("xxx", 22);// ?????

        } catch (Exception e) {
            if (e instanceof SofaRpcException) {
                Assert.assertEquals(getErrorType(), SERVER_UNDECLARED_ERROR);
            } else
                if (e instanceof UndeclaredThrowableException) {
                    Assert.assertEquals(getErrorType(), SERVER_UNDECLARED_ERROR);
                }

            ok = false;
        }
        Assert.assertFalse(ok);
        ConsumerConfig<EchoService> consumerConfig2 = new ConsumerConfig<EchoService>().setInterfaceId(EchoService.class.getName()).setDirectUrl("bolt://127.0.0.1:22222").setTimeout(30000).setRegister(false);
        EchoService echoService = consumerConfig2.refer();
        ok = true;
        try {
            echoService.echoStr("xx");
        } catch (Exception e) {
            if (e instanceof SofaRpcException) {
                Assert.assertEquals(getErrorType(), SERVER_UNDECLARED_ERROR);
            } else
                if (e instanceof UndeclaredThrowableException) {
                    Assert.assertEquals(getErrorType(), SERVER_UNDECLARED_ERROR);
                }

            ok = false;
        }
        Assert.assertFalse(ok);
    }
}


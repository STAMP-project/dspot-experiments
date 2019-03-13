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
package com.alipay.sofa.rpc.test.generic;


import com.alipay.hessian.generic.model.GenericObject;
import com.alipay.sofa.rpc.api.GenericService;
import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.MethodConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.generic.bean.Job;
import com.alipay.sofa.rpc.test.generic.bean.People;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author xuanbei
 * @since 2016/07/28
 */
public class GenericTest extends ActivelyDestroyTest {
    @Test
    public void testAll() throws SofaRpcException, InterruptedException {
        // ????
        ServerConfig serverConfig2 = new ServerConfig().setPort(22222).setDaemon(false);
        List<MethodConfig> methodConfigs = new ArrayList<MethodConfig>();
        MethodConfig config1 = new MethodConfig().setName("helloFuture").setInvokeType("future");
        methodConfigs.add(config1);
        MethodConfig config2 = new MethodConfig().setName("helloCallback").setInvokeType("callback").setOnReturn(new TestCallback());
        methodConfigs.add(config2);
        MethodConfig config21 = new MethodConfig().setName("helloCallbackException").setInvokeType("callback").setOnReturn(new TestCallback());
        methodConfigs.add(config21);
        MethodConfig config3 = new MethodConfig().setName("helloOneway").setInvokeType("oneway");
        methodConfigs.add(config3);
        // C??????
        ProviderConfig<TestInterface> CProvider = new ProviderConfig<TestInterface>().setInterfaceId(TestInterface.class.getName()).setRef(new TestClass()).setServer(serverConfig2);
        CProvider.export();
        // ????
        ConsumerConfig<GenericService> BConsumer = new ConsumerConfig<GenericService>().setInterfaceId(TestInterface.class.getName()).setGeneric(true).setMethods(methodConfigs).setDirectUrl("bolt://127.0.0.1:22222").setTimeout(3000);
        GenericService proxy = BConsumer.refer();
        GenericObject genericObject = new GenericObject("com.alipay.sofa.rpc.test.generic.bean.People");
        genericObject.putField("name", "Lilei");
        genericObject.putField("job", new Job("coder"));
        People people = new People();
        people.setName("Lilei");
        people.setJob(new Job("coder"));
        // sync ??
        Assert.assertEquals(proxy.$invoke("hello", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ people }), new TestClass().hello(people));
        People peopleResult = proxy.$genericInvoke("hello", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject }, People.class);
        Assert.assertEquals(peopleResult, new TestClass().hello(people));
        GenericObject result = ((GenericObject) (proxy.$genericInvoke("hello", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject })));
        isCorrect(result);
        // future ??
        proxy.$invoke("helloFuture", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ people });
        Assert.assertEquals(SofaResponseFuture.getResponse(1000, true), new TestClass().helloFuture(people));
        proxy.$genericInvoke("helloFuture", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject }, People.class);
        Assert.assertEquals(SofaResponseFuture.getResponse(1000, true), new TestClass().helloFuture(people));
        proxy.$genericInvoke("helloFuture", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject });
        result = ((GenericObject) (SofaResponseFuture.getResponse(1000, true)));
        isCorrect(result);
        // callback??
        proxy.$invoke("helloCallback", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ people });
        TestCallback.startLatach();
        Assert.assertEquals(TestCallback.result, new TestClass().helloCallback(people));
        proxy.$genericInvoke("helloCallback", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject }, People.class);
        TestCallback.startLatach();
        Assert.assertEquals(TestCallback.result, new TestClass().helloCallback(people));
        proxy.$genericInvoke("helloCallback", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject });
        TestCallback.startLatach();
        isCorrect(((GenericObject) (TestCallback.result)));
        TestCallback.result = null;
        // oneway??
        proxy.$invoke("helloOneway", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ people });
        proxy.$genericInvoke("helloOneway", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject }, People.class);
        proxy.$genericInvoke("helloOneway", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject });
        // callback????
        proxy.$invoke("helloCallbackException", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ people });
        TestCallback.startLatach();
        Assert.assertEquals(((Throwable) (TestCallback.result)).getMessage(), "Hello~");
        proxy.$genericInvoke("helloCallbackException", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject }, People.class);
        TestCallback.startLatach();
        Assert.assertEquals(((Throwable) (TestCallback.result)).getMessage(), "Hello~");
        proxy.$genericInvoke("helloCallbackException", new String[]{ "com.alipay.sofa.rpc.test.generic.bean.People" }, new Object[]{ genericObject });
        TestCallback.startLatach();
        Assert.assertEquals(((Throwable) (TestCallback.result)).getMessage(), "Hello~");
        testTimeout(proxy, genericObject, people);
        testComplexBean(proxy);
        testBasicBean(proxy);
    }
}


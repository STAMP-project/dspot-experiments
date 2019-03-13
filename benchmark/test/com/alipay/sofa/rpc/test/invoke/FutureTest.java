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
package com.alipay.sofa.rpc.test.invoke;


import RpcConstants.INVOKER_TYPE_FUTURE;
import com.alipay.remoting.rpc.exception.InvokeTimeoutException;
import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.test.ActivelyDestroyTest;
import com.alipay.sofa.rpc.test.HelloService;
import com.alipay.sofa.rpc.test.HelloServiceImpl;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public class FutureTest extends ActivelyDestroyTest {
    @Test
    public void testAll() {
        ServerConfig serverConfig2 = new ServerConfig().setPort(22222).setDaemon(false);
        // ???
        ProviderConfig<HelloService> CProvider = new ProviderConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setRef(new HelloServiceImpl(1000)).setServer(serverConfig2);
        CProvider.export();
        Filter filter = new TestAsyncFilter();
        // ???
        ConsumerConfig<HelloService> BConsumer = new ConsumerConfig<HelloService>().setInterfaceId(HelloService.class.getName()).setInvokeType(INVOKER_TYPE_FUTURE).setTimeout(5000).setFilterRef(Arrays.asList(filter)).setDirectUrl("bolt://127.0.0.1:22222");
        HelloService helloService = BConsumer.refer();
        // ??
        boolean error = false;
        try {
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Future future = SofaResponseFuture.getFuture();
            ret = ((String) (future.get()));
            Assert.assertNotNull(ret);
            // ?????
            Assert.assertTrue(ret.endsWith("append by async filter"));
        } catch (Exception e) {
            error = true;
        }
        Assert.assertFalse(error);
        // ???????? < ??
        error = false;
        long start = System.currentTimeMillis();
        long end;
        try {
            RpcInvokeContext.getContext().setTimeout(300);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Future future = SofaResponseFuture.getFuture(true);
            Assert.assertNull(RpcInvokeContext.getContext().getFuture());
            ret = ((String) (future.get(1000, TimeUnit.MILLISECONDS)));
            Assert.assertNotNull(ret);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ExecutionException));
            Assert.assertTrue(((e.getCause()) instanceof InvokeTimeoutException));
            error = true;
        } finally {
            end = System.currentTimeMillis();
            LOGGER.info((("elapsed time " + (end - start)) + "ms"));
            Assert.assertTrue(((end - start) < 400));
        }
        Assert.assertTrue(error);
        // ???????? > ??
        error = false;
        start = System.currentTimeMillis();
        try {
            RpcInvokeContext.getContext().setTimeout(1000);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Future future = SofaResponseFuture.getFuture(true);
            Assert.assertNull(RpcInvokeContext.getContext().getFuture());
            ret = ((String) (future.get(300, TimeUnit.MILLISECONDS)));
            Assert.assertNotNull(ret);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof TimeoutException));
            error = true;
        } finally {
            end = System.currentTimeMillis();
            LOGGER.info((("elapsed time " + (end - start)) + "ms"));
            Assert.assertTrue(((end - start) < 400));
        }
        Assert.assertTrue(error);
        // ????????  ???????
        error = false;
        try {
            RpcInvokeContext.getContext().setTimeout(500);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Future future = SofaResponseFuture.getFuture();
            ret = ((String) (future.get()));// 500ms ??? ?? 1s ???????????

            Assert.assertNotNull(ret);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ExecutionException));
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
            error = true;
        }
        Assert.assertTrue(error);
        // ???????? ????????????
        error = false;
        try {
            RpcInvokeContext.getContext().setTimeout(500);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Thread.sleep(1500);// 1s ????rpc?????

            Future future = SofaResponseFuture.getFuture();
            ret = ((String) (future.get()));
            Assert.assertNotNull(ret);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof ExecutionException));
            Assert.assertTrue(((e.getCause()) instanceof InvokeTimeoutException));
            error = true;
        }
        Assert.assertTrue(error);
        // ?????
        error = false;
        try {
            RpcInvokeContext.getContext().setTimeout(500);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Object ret1 = SofaResponseFuture.getResponse(300, true);
            Assert.assertNotNull(ret1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SofaRpcException));
            Assert.assertTrue(((e.getCause()) instanceof TimeoutException));
            error = true;
        }
        Assert.assertTrue(error);
        // ?????
        error = false;
        try {
            RpcInvokeContext.getContext().setTimeout(300);
            String ret = helloService.sayHello("xxx", 22);
            Assert.assertNull(ret);// ?????null

            Object ret1 = SofaResponseFuture.getResponse(500, true);
            Assert.assertNotNull(ret1);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SofaRpcException));
            Assert.assertTrue(((e.getCause()) instanceof InvokeTimeoutException));
            error = true;
        }
        Assert.assertTrue(error);
    }
}


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
package org.apache.dubbo.rpc.protocol.dubbo;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ImplicitCallBackTest {
    protected Exporter<ImplicitCallBackTest.IDemoService> exporter = null;

    protected Invoker<ImplicitCallBackTest.IDemoService> reference = null;

    protected URL serviceURL = null;

    protected URL consumerUrl = null;

    Method onReturnMethod;

    Method onThrowMethod;

    Method onInvokeMethod;

    ImplicitCallBackTest.NofifyImpl notify = new ImplicitCallBackTest.NofifyImpl();

    // ================================================================================================
    ImplicitCallBackTest.IDemoService demoProxy = null;

    @Test
    public void test_CloseCallback() throws Exception {
        initOrResetUrl(false);
        initOrResetService();
        ImplicitCallBackTest.Person ret = demoProxy.get(1);
        Assertions.assertEquals(1, ret.getId());
        destroyService();
    }

    @Test
    public void test_Sync_Onreturn() throws Exception {
        initOrResetUrl(false);
        initOrResetService();
        initImplicitCallBackURL_onlyOnreturn();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(requestId, ret.getId());
        for (int i = 0; i < 10; i++) {
            if (!(notify.ret.containsKey(requestId))) {
                Thread.sleep(200);
            } else {
                break;
            }
        }
        Assertions.assertEquals(requestId, notify.ret.get(requestId).getId());
        destroyService();
    }

    @Test
    public void test_Ex_OnReturn() throws Exception {
        initOrResetUrl(true);
        initOrResetExService();
        initImplicitCallBackURL_onlyOnreturn();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(null, ret);
        for (int i = 0; i < 10; i++) {
            if (!(notify.errors.containsKey(requestId))) {
                Thread.sleep(200);
            } else {
                break;
            }
        }
        Assertions.assertTrue((!(notify.errors.containsKey(requestId))));
        destroyService();
    }

    @Test
    public void test_Ex_OnInvoke() throws Exception {
        initOrResetUrl(true);
        initOrResetExService();
        initImplicitCallBackURL_onlyOninvoke();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(null, ret);
        for (int i = 0; i < 10; i++) {
            if (!(notify.inv.contains(requestId))) {
                Thread.sleep(200);
            } else {
                break;
            }
        }
        Assertions.assertTrue(notify.inv.contains(requestId));
        destroyService();
    }

    @Test
    public void test_Ex_Onthrow() throws Exception {
        initOrResetUrl(true);
        initOrResetExService();
        initImplicitCallBackURL_onlyOnthrow();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(null, ret);
        for (int i = 0; i < 10; i++) {
            if (!(notify.errors.containsKey(requestId))) {
                Thread.sleep(200);
            } else {
                break;
            }
        }
        Assertions.assertTrue(notify.errors.containsKey(requestId));
        Assertions.assertTrue(((notify.errors.get(requestId)) instanceof Throwable));
        destroyService();
    }

    @Test
    public void test_Sync_NoFuture() throws Exception {
        initOrResetUrl(false);
        initOrResetService();
        initImplicitCallBackURL_onlyOnreturn();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(requestId, ret.getId());
        Future<ImplicitCallBackTest.Person> pFuture = RpcContext.getContext().getFuture();
        Assertions.assertEquals(null, pFuture);
        destroyService();
    }

    @Test
    public void test_Async_Future() throws Exception {
        initOrResetUrl(true);
        initOrResetService();
        int requestId = 2;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
        Assertions.assertEquals(null, ret);
        Future<ImplicitCallBackTest.Person> pFuture = RpcContext.getContext().getFuture();
        ret = pFuture.get((1000 * 1000), TimeUnit.MICROSECONDS);
        Assertions.assertEquals(requestId, ret.getId());
        destroyService();
    }

    @Test
    public void test_Async_Future_Multi() throws Exception {
        initOrResetUrl(true);
        initOrResetService();
        int requestId1 = 1;
        ImplicitCallBackTest.Person ret = demoProxy.get(requestId1);
        Assertions.assertEquals(null, ret);
        Future<ImplicitCallBackTest.Person> p1Future = RpcContext.getContext().getFuture();
        int requestId2 = 1;
        ImplicitCallBackTest.Person ret2 = demoProxy.get(requestId2);
        Assertions.assertEquals(null, ret2);
        Future<ImplicitCallBackTest.Person> p2Future = RpcContext.getContext().getFuture();
        ret = p1Future.get((1000 * 1000), TimeUnit.MICROSECONDS);
        ret2 = p2Future.get((1000 * 1000), TimeUnit.MICROSECONDS);
        Assertions.assertEquals(requestId1, ret.getId());
        Assertions.assertEquals(requestId2, ret.getId());
        destroyService();
    }

    @Test
    public void test_Async_Future_Ex() throws Throwable {
        Assertions.assertThrows(RuntimeException.class, () -> {
            try {
                initOrResetUrl(true);
                initOrResetExService();
                int requestId = 2;
                ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
                Assertions.assertEquals(null, ret);
                Future<ImplicitCallBackTest.Person> pFuture = RpcContext.getContext().getFuture();
                ret = pFuture.get((1000 * 1000), TimeUnit.MICROSECONDS);
                Assertions.assertEquals(requestId, ret.getId());
            } catch (ExecutionException e) {
                throw e.getCause();
            } finally {
                destroyService();
            }
        });
    }

    @Test
    public void test_Normal_Ex() throws Exception {
        Assertions.assertThrows(RuntimeException.class, () -> {
            initOrResetUrl(false);
            initOrResetExService();
            int requestId = 2;
            ImplicitCallBackTest.Person ret = demoProxy.get(requestId);
            Assertions.assertEquals(requestId, ret.getId());
        });
    }

    interface Nofify {
        public void onreturn(ImplicitCallBackTest.Person msg, Integer id);

        public void onthrow(Throwable ex, Integer id);

        public void oninvoke(Integer id);
    }

    interface IDemoService {
        public ImplicitCallBackTest.Person get(int id);
    }

    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private int id;

        private String name;

        private int age;

        public Person(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return ((("Person [name=" + (name)) + ", age=") + (age)) + "]";
        }
    }

    class NofifyImpl implements ImplicitCallBackTest.Nofify {
        public List<Integer> inv = new ArrayList<Integer>();

        public Map<Integer, ImplicitCallBackTest.Person> ret = new HashMap<Integer, ImplicitCallBackTest.Person>();

        public Map<Integer, Throwable> errors = new HashMap<Integer, Throwable>();

        public boolean exd = false;

        public void onreturn(ImplicitCallBackTest.Person msg, Integer id) {
            System.out.println(("onNotify:" + msg));
            ret.put(id, msg);
        }

        public void onthrow(Throwable ex, Integer id) {
            errors.put(id, ex);
            // ex.printStackTrace();
        }

        public void oninvoke(Integer id) {
            inv.add(id);
        }
    }

    class NormalDemoService implements ImplicitCallBackTest.IDemoService {
        public ImplicitCallBackTest.Person get(int id) {
            return new ImplicitCallBackTest.Person(id, "charles", 4);
        }
    }

    class ExceptionDemoExService implements ImplicitCallBackTest.IDemoService {
        public ImplicitCallBackTest.Person get(int id) {
            throw new RuntimeException(("request persion id is :" + id));
        }
    }
}


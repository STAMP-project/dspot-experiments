/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.hazelcast;


import HazelcastConstants.OBJECT_POS;
import com.hazelcast.core.IList;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastListProducerTest extends HazelcastCamelTestSupport {
    @Mock
    private IList<String> list;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperation() {
        template.sendBody("direct:addInvalid", "bar");
    }

    @Test
    public void addValue() throws InterruptedException {
        template.sendBody("direct:add", "bar");
        Mockito.verify(list).add("bar");
    }

    @Test
    public void addValueWithOperationNumber() throws InterruptedException {
        template.sendBody("direct:addWithOperationNumber", "bar");
        Mockito.verify(list).add("bar");
    }

    @Test
    public void addValueWithOperationName() throws InterruptedException {
        template.sendBody("direct:addWithOperationName", "bar");
        Mockito.verify(list).add("bar");
    }

    @Test
    public void removeValue() throws InterruptedException {
        template.sendBody("direct:removeValue", "foo2");
        Mockito.verify(list).remove("foo2");
    }

    @Test
    public void getValueWithIdx() {
        Mockito.when(list.get(1)).thenReturn("foo2");
        template.sendBodyAndHeader("direct:get", "test", OBJECT_POS, 1);
        Mockito.verify(list).get(1);
        assertEquals("foo2", consumer.receiveBody("seda:out", 5000, String.class));
    }

    @Test
    public void setValueWithIdx() {
        template.sendBodyAndHeader("direct:set", "test", OBJECT_POS, 1);
        Mockito.verify(list).set(1, "test");
    }

    @Test
    public void removeValueWithIdx() {
        template.sendBodyAndHeader("direct:removeValue", null, OBJECT_POS, 1);
        Mockito.verify(list).remove(1);
    }

    @Test
    public void removeValueWithoutIdx() {
        template.sendBody("direct:removeValue", "foo1");
        Mockito.verify(list).remove("foo1");
    }

    @Test
    public void clearList() {
        template.sendBody("direct:clear", "");
        Mockito.verify(list).clear();
    }

    @Test
    public void addAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:addall", t);
        Mockito.verify(list).addAll(t);
    }

    @Test
    public void removeAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:removeAll", t);
        Mockito.verify(list).removeAll(t);
    }

    @Test
    public void retainAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:RETAIN_ALL", t);
        Mockito.verify(list).retainAll(t);
    }
}


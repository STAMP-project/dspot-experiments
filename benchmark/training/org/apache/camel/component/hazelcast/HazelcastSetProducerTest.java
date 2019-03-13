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


import com.hazelcast.core.ISet;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HazelcastSetProducerTest extends HazelcastCamelTestSupport {
    @Mock
    private ISet<String> set;

    @Test(expected = CamelExecutionException.class)
    public void testWithInvalidOperation() {
        template.sendBody("direct:addInvalid", "bar");
    }

    @Test
    public void addValue() throws InterruptedException {
        template.sendBody("direct:add", "bar");
        Mockito.verify(set).add("bar");
    }

    @Test
    public void addValueWithOperationNumber() throws InterruptedException {
        template.sendBody("direct:addWithOperationNumber", "bar");
        Mockito.verify(set).add("bar");
    }

    @Test
    public void addValueWithOperationName() throws InterruptedException {
        template.sendBody("direct:addWithOperationName", "bar");
        Mockito.verify(set).add("bar");
    }

    @Test
    public void removeValue() throws InterruptedException {
        template.sendBody("direct:removeValue", "foo2");
        Mockito.verify(set).remove("foo2");
    }

    @Test
    public void clearList() {
        template.sendBody("direct:clear", "");
        Mockito.verify(set).clear();
    }

    @Test
    public void addAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:addall", t);
        Mockito.verify(set).addAll(t);
    }

    @Test
    public void removeAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:removeAll", t);
        Mockito.verify(set).removeAll(t);
    }

    @Test
    public void retainAll() throws InterruptedException {
        Collection t = new ArrayList();
        t.add("test1");
        t.add("test2");
        template.sendBody("direct:RETAIN_ALL", t);
        Mockito.verify(set).retainAll(t);
    }
}


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
package org.apache.camel.impl;


import org.apache.camel.Exchange;
import org.apache.camel.support.TypeConverterSupport;
import org.junit.Assert;
import org.junit.Test;


public class TypeConverterRegistryMissesThenAddTest extends Assert {
    @Test
    public void testMissThenAddTypeConverter() {
        DefaultCamelContext context = new DefaultCamelContext();
        TypeConverterRegistryMissesThenAddTest.MyOrder order = context.getTypeConverter().convertTo(TypeConverterRegistryMissesThenAddTest.MyOrder.class, "123");
        Assert.assertNull(order);
        // add missing type converter
        context.getTypeConverterRegistry().addTypeConverter(TypeConverterRegistryMissesThenAddTest.MyOrder.class, String.class, new TypeConverterRegistryMissesThenAddTest.MyOrderTypeConverter());
        // this time it should work
        order = context.getTypeConverter().convertTo(TypeConverterRegistryMissesThenAddTest.MyOrder.class, "123");
        Assert.assertNotNull(order);
        Assert.assertEquals(123, order.getId());
    }

    private static class MyOrder {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    private static class MyOrderTypeConverter extends TypeConverterSupport {
        @SuppressWarnings("unchecked")
        public <T> T convertTo(Class<T> type, Exchange exchange, Object value) {
            // converter from value to the MyOrder bean
            TypeConverterRegistryMissesThenAddTest.MyOrder order = new TypeConverterRegistryMissesThenAddTest.MyOrder();
            order.setId(Integer.parseInt(value.toString()));
            return ((T) (order));
        }
    }
}


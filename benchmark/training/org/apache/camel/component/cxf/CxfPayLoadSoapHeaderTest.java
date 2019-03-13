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
package org.apache.camel.component.cxf;


import javax.xml.namespace.QName;
import org.apache.camel.pizza.Pizza;
import org.apache.camel.pizza.types.CallerIDHeaderType;
import org.apache.camel.pizza.types.OrderPizzaResponseType;
import org.apache.camel.pizza.types.OrderPizzaType;
import org.apache.camel.pizza.types.ToppingsListType;
import org.junit.Test;


public class CxfPayLoadSoapHeaderTest extends CxfPayLoadSoapHeaderTestAbstract {
    private final QName serviceName = new QName("http://camel.apache.org/pizza", "PizzaService");

    @Test
    public void testPizzaService() {
        Pizza port = getPort();
        OrderPizzaType req = new OrderPizzaType();
        ToppingsListType t = new ToppingsListType();
        t.getTopping().add("test");
        req.setToppings(t);
        CallerIDHeaderType header = new CallerIDHeaderType();
        header.setName("Willem");
        header.setPhoneNumber("108");
        OrderPizzaResponseType res = port.orderPizza(req, header);
        assertEquals(208, res.getMinutesUntilReady());
    }
}


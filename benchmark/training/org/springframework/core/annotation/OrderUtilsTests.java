/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.core.annotation;


import javax.annotation.Priority;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class OrderUtilsTests {
    @Test
    public void getSimpleOrder() {
        Assert.assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderUtilsTests.SimpleOrder.class, null));
        Assert.assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderUtilsTests.SimpleOrder.class, null));
    }

    @Test
    public void getPriorityOrder() {
        Assert.assertEquals(Integer.valueOf(55), OrderUtils.getOrder(OrderUtilsTests.SimplePriority.class, null));
        Assert.assertEquals(Integer.valueOf(55), OrderUtils.getOrder(OrderUtilsTests.SimplePriority.class, null));
    }

    @Test
    public void getOrderWithBoth() {
        Assert.assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderUtilsTests.OrderAndPriority.class, null));
        Assert.assertEquals(Integer.valueOf(50), OrderUtils.getOrder(OrderUtilsTests.OrderAndPriority.class, null));
    }

    @Test
    public void getDefaultOrder() {
        Assert.assertEquals(33, OrderUtils.getOrder(OrderUtilsTests.NoOrder.class, 33));
        Assert.assertEquals(33, OrderUtils.getOrder(OrderUtilsTests.NoOrder.class, 33));
    }

    @Test
    public void getPriorityValueNoAnnotation() {
        Assert.assertNull(OrderUtils.getPriority(OrderUtilsTests.SimpleOrder.class));
        Assert.assertNull(OrderUtils.getPriority(OrderUtilsTests.SimpleOrder.class));
    }

    @Test
    public void getPriorityValue() {
        Assert.assertEquals(Integer.valueOf(55), OrderUtils.getPriority(OrderUtilsTests.OrderAndPriority.class));
        Assert.assertEquals(Integer.valueOf(55), OrderUtils.getPriority(OrderUtilsTests.OrderAndPriority.class));
    }

    @Order(50)
    private static class SimpleOrder {}

    @Priority(55)
    private static class SimplePriority {}

    @Order(50)
    @Priority(55)
    private static class OrderAndPriority {}

    private static class NoOrder {}
}


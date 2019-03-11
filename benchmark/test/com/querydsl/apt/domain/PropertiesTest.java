/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import QPropertiesTest_Customer.customer.name;
import QPropertiesTest_Customer.customer.pizzas;
import QPropertiesTest_Pizza.pizza.customer;
import QPropertiesTest_Pizza.pizza.orderTime;
import QPropertiesTest_Pizza.pizza.toppings;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static TemporalType.TIMESTAMP;


public class PropertiesTest {
    @Entity
    public abstract static class AbstractEntity {}

    @Entity
    @Table(name = "Customer")
    public static class Customer extends PropertiesTest.AbstractEntity {
        private String name;

        private List<PropertiesTest.Pizza> pizzas = new ArrayList<PropertiesTest.Pizza>(0);

        @Column
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @OneToMany(mappedBy = "customer")
        public List<PropertiesTest.Pizza> getPizzas() {
            return pizzas;
        }

        public void setPizzas(List<PropertiesTest.Pizza> pizzas) {
            this.pizzas = pizzas;
        }
    }

    @Entity
    @Table(name = "Pizza")
    public static class Pizza extends PropertiesTest.AbstractEntity {
        private Date orderTime;

        private PropertiesTest.Customer customer;

        private List<PropertiesTest.Topping> toppings = new ArrayList<PropertiesTest.Topping>(0);

        @Column
        @Temporal(TIMESTAMP)
        public Date getOrderTime() {
            return new Date(orderTime.getTime());
        }

        public void setOrderTime(Date orderTime) {
            this.orderTime = new Date(orderTime.getTime());
        }

        @ManyToOne
        @JoinColumn(name = "customerId")
        public PropertiesTest.Customer getCustomer() {
            return customer;
        }

        public void setCustomer(PropertiesTest.Customer customer) {
            this.customer = customer;
        }

        @OneToMany(mappedBy = "pizza")
        public List<PropertiesTest.Topping> getToppings() {
            return toppings;
        }

        public void setToppings(List<PropertiesTest.Topping> toppings) {
            this.toppings = toppings;
        }
    }

    @Entity
    public static class Topping {}

    @Test
    public void customer() {
        Assert.assertNotNull(name);
        Assert.assertNotNull(pizzas);
    }

    @Test
    public void pizza() {
        Assert.assertNotNull(orderTime);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(toppings);
    }
}


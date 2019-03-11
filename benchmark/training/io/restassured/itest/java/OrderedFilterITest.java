/**
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.filter.OrderedFilter;
import io.restassured.itest.java.support.WithJetty;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OrderedFilterITest extends WithJetty {
    @Test
    public void high_precedence_are_sorted_before_low_precedence() {
        List<String> list = new CopyOnWriteArrayList<>();
        RestAssured.given().filter(( requestSpec, responseSpec, ctx) -> {
            list.add("Default");
            return ctx.next(requestSpec, responseSpec);
        }).filter(new OrderedFilter() {
            @Override
            public int getOrder() {
                return 1002;
            }

            @Override
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                list.add("Custom");
                return ctx.next(requestSpec, responseSpec);
            }
        }).filter(new OrderedFilter() {
            @Override
            public int getOrder() {
                return LOWEST_PRECEDENCE;
            }

            @Override
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                list.add("Lowest");
                return ctx.next(requestSpec, responseSpec);
            }
        }).filter(new OrderedFilter() {
            @Override
            public int getOrder() {
                return HIGHEST_PRECEDENCE;
            }

            @Override
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                list.add("Highest");
                return ctx.next(requestSpec, responseSpec);
            }
        }).when().get("/lotto");
        Assert.assertThat(list, Matchers.contains("Highest", "Default", "Custom", "Lowest"));
    }

    @Test
    public void filters_are_sorted_in_insertion_order_when_all_filters_have_the_same_precedence() {
        List<String> list = new CopyOnWriteArrayList<>();
        RestAssured.given().filter(( requestSpec, responseSpec, ctx) -> {
            list.add("Default1");
            return ctx.next(requestSpec, responseSpec);
        }).filter(( requestSpec, responseSpec, ctx) -> {
            list.add("Default2");
            return ctx.next(requestSpec, responseSpec);
        }).filter(( requestSpec, responseSpec, ctx) -> {
            list.add("Default3");
            return ctx.next(requestSpec, responseSpec);
        }).filter(( requestSpec, responseSpec, ctx) -> {
            list.add("Default4");
            return ctx.next(requestSpec, responseSpec);
        }).when().get("/lotto");
        Assert.assertThat(list, Matchers.contains("Default1", "Default2", "Default3", "Default4"));
    }
}


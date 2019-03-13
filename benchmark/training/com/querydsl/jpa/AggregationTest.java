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
package com.querydsl.jpa;


import org.junit.Test;


public class AggregationTest extends AbstractQueryTest {
    @Test
    public void max() {
        AbstractQueryTest.assertToString("max(cat.bodyWeight)", Constants.cat.bodyWeight.max());
    }

    @Test
    public void min() {
        AbstractQueryTest.assertToString("min(cat.bodyWeight)", Constants.cat.bodyWeight.min());
    }

    @Test
    public void avg() {
        AbstractQueryTest.assertToString("avg(cat.bodyWeight)", Constants.cat.bodyWeight.avg());
    }

    @Test
    public void count() {
        AbstractQueryTest.assertToString("count(cat)", Constants.cat.count());
    }

    @Test
    public void countDistinct() {
        AbstractQueryTest.assertToString("count(distinct cat)", Constants.cat.countDistinct());
    }
}


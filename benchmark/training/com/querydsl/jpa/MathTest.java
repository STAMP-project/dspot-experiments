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


import QCat.cat;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.domain.QCat;
import org.junit.Test;


public class MathTest extends AbstractQueryTest {
    @Test
    public void test() {
        NumberPath<Double> path = cat.bodyWeight;
        AbstractQueryTest.assertToString("(cat.bodyWeight - sum(cat.bodyWeight)) * cat.bodyWeight", path.subtract(path.sum()).multiply(path));
    }

    @Test
    public void add() {
        AbstractQueryTest.assertToString("cat.bodyWeight + ?1", Constants.cat.bodyWeight.add(10));
    }

    @Test
    public void subtract() {
        AbstractQueryTest.assertToString("cat.bodyWeight - ?1", Constants.cat.bodyWeight.subtract(10));
    }

    @Test
    public void multiply() {
        AbstractQueryTest.assertToString("cat.bodyWeight * ?1", Constants.cat.bodyWeight.multiply(10));
    }

    @Test
    public void divide() {
        AbstractQueryTest.assertToString("cat.bodyWeight / ?1", Constants.cat.bodyWeight.divide(10));
    }

    @Test
    public void add_and_compare() {
        AbstractQueryTest.assertToString("cat.bodyWeight + ?1 < ?1", Constants.cat.bodyWeight.add(10.0).lt(10.0));
    }

    @Test
    public void subtract_and_compare() {
        AbstractQueryTest.assertToString("cat.bodyWeight - ?1 < ?1", Constants.cat.bodyWeight.subtract(10.0).lt(10.0));
    }

    @Test
    public void multiply_and_compare() {
        AbstractQueryTest.assertToString("cat.bodyWeight * ?1 < ?1", Constants.cat.bodyWeight.multiply(10.0).lt(10.0));
    }

    @Test
    public void divide_and_compare() {
        AbstractQueryTest.assertToString("cat.bodyWeight / ?1 < ?2", Constants.cat.bodyWeight.divide(10.0).lt(20.0));
    }

    @Test
    public void add_and_multiply() {
        AbstractQueryTest.assertToString("(cat.bodyWeight + ?1) * ?2", Constants.cat.bodyWeight.add(10).multiply(20));
    }

    @Test
    public void subtract_and_multiply() {
        AbstractQueryTest.assertToString("(cat.bodyWeight - ?1) * ?2", Constants.cat.bodyWeight.subtract(10).multiply(20));
    }

    @Test
    public void multiply_and_add() {
        AbstractQueryTest.assertToString("cat.bodyWeight * ?1 + ?2", Constants.cat.bodyWeight.multiply(10).add(20));
    }

    @Test
    public void multiply_and_subtract() {
        AbstractQueryTest.assertToString("cat.bodyWeight * ?1 - ?2", Constants.cat.bodyWeight.multiply(10).subtract(20));
    }

    @Test
    public void arithmetic_and_arithmetic2() {
        QCat c1 = new QCat("c1");
        QCat c2 = new QCat("c2");
        QCat c3 = new QCat("c3");
        AbstractQueryTest.assertToString("c1.id + c2.id * c3.id", c1.id.add(c2.id.multiply(c3.id)));
        AbstractQueryTest.assertToString("c1.id * (c2.id + c3.id)", c1.id.multiply(c2.id.add(c3.id)));
        AbstractQueryTest.assertToString("(c1.id + c2.id) * c3.id", c1.id.add(c2.id).multiply(c3.id));
    }

    @Test
    public void mathematicalOperations() {
        // mathematical operators +, -, *, /
        Constants.cat.bodyWeight.add(Constants.kitten.bodyWeight);
        Constants.cat.bodyWeight.subtract(Constants.kitten.bodyWeight);
        Constants.cat.bodyWeight.multiply(Constants.kitten.bodyWeight);
        Constants.cat.bodyWeight.divide(Constants.kitten.bodyWeight);
    }
}


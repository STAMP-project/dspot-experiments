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


import com.querydsl.jpa.domain.QAccount;
import com.querydsl.jpa.domain.QInheritedProperties;
import org.junit.Assert;
import org.junit.Test;


public class FeaturesTest extends AbstractQueryTest {
    @Test
    public void domainConstruction() {
        QInheritedProperties i = new QInheritedProperties("i");
        Assert.assertNotNull(i.superclassProperty);
        Assert.assertNotNull(i.classProperty);
    }

    @Test
    public void domainConstruction2() {
        QAccount a = new QAccount("a");
        Assert.assertNotNull(a.embeddedData.someData);
    }

    @Test
    public void basicStructure() {
        Assert.assertNull(Constants.cat.getMetadata().getParent());
    }

    @Test
    public void basicStructure2() {
        Assert.assertEquals(Constants.cat, Constants.cat.alive.getMetadata().getParent());
    }

    @Test
    public void basicStructure3() {
        Assert.assertEquals("cat", Constants.cat.getMetadata().getName());
    }

    @Test
    public void argumentHandling() {
        // Kitty is reused, so it should be used via one named parameter
        AbstractQueryTest.assertToString("cat.name = ?1 or cust.name.firstName = ?2 or kitten.name = ?1", Constants.cat.name.eq("Kitty").or(Constants.cust.name.firstName.eq("Hans")).or(Constants.kitten.name.eq("Kitty")));
    }

    @Test
    public void basicOperations() {
        AbstractQueryTest.assertToString("cat.bodyWeight = kitten.bodyWeight", Constants.cat.bodyWeight.eq(Constants.kitten.bodyWeight));
    }

    @Test
    public void basicOperations2() {
        AbstractQueryTest.assertToString("cat.bodyWeight <> kitten.bodyWeight", Constants.cat.bodyWeight.ne(Constants.kitten.bodyWeight));
    }

    @Test
    public void basicOperations3() {
        AbstractQueryTest.assertToString("cat.bodyWeight + kitten.bodyWeight = kitten.bodyWeight", Constants.cat.bodyWeight.add(Constants.kitten.bodyWeight).eq(Constants.kitten.bodyWeight));
    }

    @Test
    public void equalsAndNotEqualsForAllExpressions() {
        AbstractQueryTest.assertToString("cat.name = cust.name.firstName", Constants.cat.name.eq(Constants.cust.name.firstName));
    }

    @Test
    public void equalsAndNotEqualsForAllExpressions2() {
        AbstractQueryTest.assertToString("cat.name <> cust.name.firstName", Constants.cat.name.ne(Constants.cust.name.firstName));
    }

    @Test
    public void groupingOperationsAndNullChecks() {
        // in, not in, between, is null, is not null, is empty, is not empty,
        // member of and not member of
        // in,
        // not in,
        // between,
        // is null,
        // is not null,
        // is empty,
        // is not empty,
        // member of
        // not member of
        Constants.kitten.in(Constants.cat.kittens);
        Constants.kitten.in(Constants.cat.kittens).not();
        Constants.kitten.bodyWeight.between(10, 20);
        Constants.kitten.bodyWeight.isNull();
        Constants.kitten.bodyWeight.isNotNull();
        Constants.cat.kittens.isEmpty();
        Constants.cat.kittens.isNotEmpty();
    }

    @Test
    public void toString_() {
        AbstractQueryTest.assertToString("cat", Constants.cat);
        AbstractQueryTest.assertToString("cat.alive", Constants.cat.alive);
        AbstractQueryTest.assertToString("cat.bodyWeight", Constants.cat.bodyWeight);
        AbstractQueryTest.assertToString("cat.name", Constants.cat.name);
        AbstractQueryTest.assertToString("cust.name", Constants.cust.name);
        AbstractQueryTest.assertToString("cust.name.firstName = ?1", Constants.cust.name.firstName.eq("Martin"));
        // toString("cat.kittens as kitten", cat.kittens.as(kitten));
        AbstractQueryTest.assertToString("cat.bodyWeight + ?1", Constants.cat.bodyWeight.add(10));
        AbstractQueryTest.assertToString("cat.bodyWeight - ?1", Constants.cat.bodyWeight.subtract(10));
        AbstractQueryTest.assertToString("cat.bodyWeight * ?1", Constants.cat.bodyWeight.multiply(10));
        AbstractQueryTest.assertToString("cat.bodyWeight / ?1", Constants.cat.bodyWeight.divide(10));
        // toString("cat.bodyWeight as bw", cat.bodyWeight.as("bw"));
        AbstractQueryTest.assertToString("kitten member of cat.kittens", Constants.kitten.in(Constants.cat.kittens));
        // toString("distinct cat.bodyWeight", distinct(cat.bodyWeight));
    }
}


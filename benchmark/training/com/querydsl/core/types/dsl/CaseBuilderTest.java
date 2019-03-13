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
package com.querydsl.core.types.dsl;


import Expressions.TRUE;
import com.querydsl.core.alias.Alias;
import org.junit.Assert;
import org.junit.Test;


public class CaseBuilderTest {
    public enum Gender {

        MALE,
        FEMALE;}

    public static class Customer {
        private long annualSpending;

        public long getAnnualSpending() {
            return annualSpending;
        }
    }

    @Test
    public void general() {
        SimpleExpression<Object> expr = new CaseBuilder().when(TRUE).then(new Object()).otherwise(null);
        Assert.assertNotNull(expr);
    }

    @Test
    public void booleanTyped() {
        CaseBuilderTest.Customer c = Alias.alias(CaseBuilderTest.Customer.class, "customer");
        BooleanExpression cases = new CaseBuilder().when(Alias.$(c.getAnnualSpending()).gt(10000)).then(true).otherwise(false);
        Assert.assertEquals(("case " + (("when customer.annualSpending > 10000 then true " + "else false ") + "end")), cases.toString());
    }

    @Test
    public void booleanTyped_predicate() {
        CaseBuilderTest.Customer c = Alias.alias(CaseBuilderTest.Customer.class, "customer");
        BooleanExpression cases = new CaseBuilder().when(Alias.$(c.getAnnualSpending()).gt(20000)).then(false).when(Alias.$(c.getAnnualSpending()).gt(10000)).then(true).otherwise(false);
        Assert.assertEquals(("case " + ((("when customer.annualSpending > 20000 then false " + "when customer.annualSpending > 10000 then true ") + "else false ") + "end")), cases.toString());
    }

    @Test
    public void enumTyped() {
        CaseBuilderTest.Customer c = Alias.alias(CaseBuilderTest.Customer.class, "customer");
        EnumExpression<CaseBuilderTest.Gender> cases = new CaseBuilder().when(Alias.$(c.getAnnualSpending()).gt(10000)).then(CaseBuilderTest.Gender.MALE).otherwise(CaseBuilderTest.Gender.FEMALE);
        Assert.assertEquals(("case " + (("when customer.annualSpending > 10000 then MALE " + "else FEMALE ") + "end")), cases.toString());
    }

    @Test
    public void numberTyped() {
        CaseBuilderTest.Customer c = Alias.alias(CaseBuilderTest.Customer.class, "customer");
        NumberExpression<Integer> cases = new CaseBuilder().when(Alias.$(c.getAnnualSpending()).gt(10000)).then(1).when(Alias.$(c.getAnnualSpending()).gt(5000)).then(2).when(Alias.$(c.getAnnualSpending()).gt(2000)).then(3).otherwise(4);
        Assert.assertEquals(("case " + (((("when customer.annualSpending > 10000 then 1 " + "when customer.annualSpending > 5000 then 2 ") + "when customer.annualSpending > 2000 then 3 ") + "else 4 ") + "end")), cases.toString());
    }

    @Test
    public void stringTyped() {
        // CASE
        // WHEN c.annualSpending > 10000 THEN 'Premier'
        // WHEN c.annualSpending >  5000 THEN 'Gold'
        // WHEN c.annualSpending >  2000 THEN 'Silver'
        // ELSE 'Bronze'
        // END
        CaseBuilderTest.Customer c = Alias.alias(CaseBuilderTest.Customer.class, "customer");
        StringExpression cases = new CaseBuilder().when(Alias.$(c.getAnnualSpending()).gt(10000)).then("Premier").when(Alias.$(c.getAnnualSpending()).gt(5000)).then("Gold").when(Alias.$(c.getAnnualSpending()).gt(2000)).then("Silver").otherwise("Bronze");
        // NOTE : this is just a test serialization, not the real one
        Assert.assertEquals(("case " + (((("when customer.annualSpending > 10000 then Premier " + "when customer.annualSpending > 5000 then Gold ") + "when customer.annualSpending > 2000 then Silver ") + "else Bronze ") + "end")), cases.toString());
    }
}


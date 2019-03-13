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


import com.querydsl.core.alias.Alias;
import java.sql.Date;
import java.sql.Time;
import org.junit.Assert;
import org.junit.Test;


public class CaseForEqBuilderTest {
    public enum EnumExample {

        A,
        B;}

    public static class Customer {
        private long annualSpending;

        public long getAnnualSpending() {
            return annualSpending;
        }
    }

    @Test
    public void numberTyped() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        NumberExpression<Integer> cases = Alias.$(c.getAnnualSpending()).when(1000L).then(1).when(2000L).then(2).when(5000L).then(3).otherwise(4);
        Assert.assertEquals(("case customer.annualSpending " + (((("when 1000 then 1 " + "when 2000 then 2 ") + "when 5000 then 3 ") + "else 4 ") + "end")), cases.toString());
    }

    @Test
    public void stringTyped() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        StringExpression cases = Alias.$(c.getAnnualSpending()).when(1000L).then("bronze").when(2000L).then("silver").when(5000L).then("gold").otherwise("platinum");
        Assert.assertNotNull(cases);
    }

    @Test
    public void booleanTyped() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        BooleanExpression cases = Alias.$(c.getAnnualSpending()).when(1000L).then(true).otherwise(false);
        Assert.assertNotNull(cases);
    }

    @Test
    public void dateType() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        DateExpression<Date> cases = Alias.$(c.getAnnualSpending()).when(1000L).then(new Date(0)).otherwise(new Date(0));
        Assert.assertNotNull(cases);
    }

    @Test
    public void dateTimeType() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        DateTimeExpression<java.util.Date> cases = Alias.$(c.getAnnualSpending()).when(1000L).then(new java.util.Date(0)).otherwise(new java.util.Date(0));
        Assert.assertNotNull(cases);
    }

    @Test
    public void timeType() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        TimeExpression<Time> cases = Alias.$(c.getAnnualSpending()).when(1000L).then(new Time(0)).otherwise(new Time(0));
        Assert.assertNotNull(cases);
    }

    @Test
    public void enumType() {
        CaseForEqBuilderTest.Customer c = Alias.alias(CaseForEqBuilderTest.Customer.class, "customer");
        EnumExpression<CaseForEqBuilderTest.EnumExample> cases = Alias.$(c.getAnnualSpending()).when(1000L).then(CaseForEqBuilderTest.EnumExample.A).otherwise(CaseForEqBuilderTest.EnumExample.B);
        Assert.assertNotNull(cases);
    }
}


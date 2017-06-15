/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.jdbc;


public class AmplSelectBuilderTest {
    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatement() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.ID like #id# AND P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2("a", "b", "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2(null, "b", "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstTwoParams() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2(null, null, "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingAllParams() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + ("FROM PERSON P\n" + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2(null, null, null));
    }

    @org.junit.Test
    public void shouldProduceExpectedComplexSelectStatement() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" + (((((((("FROM PERSON P, ACCOUNT A\n" + "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n") + "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n") + "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n") + "OR (P.LAST_NAME like ?)\n") + "GROUP BY P.ID\n") + "HAVING (P.LAST_NAME like ?) \n") + "OR (P.FIRST_NAME like ?)\n") + "ORDER BY P.ID, P.FULL_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSelectBuilderTest.example1());
    }

    private static java.lang.String example1() {
        org.apache.ibatis.jdbc.SelectBuilder.SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
        org.apache.ibatis.jdbc.SelectBuilder.SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
        org.apache.ibatis.jdbc.SelectBuilder.FROM("PERSON P");
        org.apache.ibatis.jdbc.SelectBuilder.FROM("ACCOUNT A");
        org.apache.ibatis.jdbc.SelectBuilder.INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
        org.apache.ibatis.jdbc.SelectBuilder.INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
        org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.ID = A.ID");
        org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.FIRST_NAME like ?");
        org.apache.ibatis.jdbc.SelectBuilder.OR();
        org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.LAST_NAME like ?");
        org.apache.ibatis.jdbc.SelectBuilder.GROUP_BY("P.ID");
        org.apache.ibatis.jdbc.SelectBuilder.HAVING("P.LAST_NAME like ?");
        org.apache.ibatis.jdbc.SelectBuilder.OR();
        org.apache.ibatis.jdbc.SelectBuilder.HAVING("P.FIRST_NAME like ?");
        org.apache.ibatis.jdbc.SelectBuilder.ORDER_BY("P.ID");
        org.apache.ibatis.jdbc.SelectBuilder.ORDER_BY("P.FULL_NAME");
        return org.apache.ibatis.jdbc.SelectBuilder.SQL();
    }

    private static java.lang.String example2(java.lang.String id, java.lang.String firstName, java.lang.String lastName) {
        org.apache.ibatis.jdbc.SelectBuilder.SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME");
        org.apache.ibatis.jdbc.SelectBuilder.FROM("PERSON P");
        if (id != null) {
            org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.ID like #id#");
        }
        if (firstName != null) {
            org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.FIRST_NAME like #firstName#");
        }
        if (lastName != null) {
            org.apache.ibatis.jdbc.SelectBuilder.WHERE("P.LAST_NAME like #lastName#");
        }
        org.apache.ibatis.jdbc.SelectBuilder.ORDER_BY("P.LAST_NAME");
        return org.apache.ibatis.jdbc.SelectBuilder.SQL();
    }

    /* amplification of org.apache.ibatis.jdbc.SelectBuilderTest#shouldProduceExpectedSimpleSelectStatementMissingAllParams */
    @org.junit.Test(timeout = 10000)
    public void shouldProduceExpectedSimpleSelectStatementMissingAllParams_cf20646_cf20899_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + ("FROM PERSON P\n" + "ORDER BY P.LAST_NAME");
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SelectBuilder vc_120 = (org.apache.ibatis.jdbc.SelectBuilder)null;
            // MethodAssertGenerator build local variable
            Object o_4_0 = vc_120;
            // StatementAdderMethod cloned existing statement
            vc_120.BEGIN();
            // MethodAssertGenerator build local variable
            Object o_8_0 = vc_120;
            // StatementAdderOnAssert create null value
            java.lang.String vc_226 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SelectBuilder vc_224 = (org.apache.ibatis.jdbc.SelectBuilder)null;
            // StatementAdderMethod cloned existing statement
            vc_224.SELECT_DISTINCT(vc_226);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2(null, null, null);
            org.junit.Assert.fail("shouldProduceExpectedSimpleSelectStatementMissingAllParams_cf20646_cf20899 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.jdbc.SelectBuilderTest#shouldProduceExpectedSimpleSelectStatementMissingFirstParam */
    @org.junit.Test(timeout = 10000)
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf22085_cf22467_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SelectBuilder vc_330 = (org.apache.ibatis.jdbc.SelectBuilder)null;
            // MethodAssertGenerator build local variable
            Object o_4_0 = vc_330;
            // StatementAdderMethod cloned existing statement
            vc_330.RESET();
            // MethodAssertGenerator build local variable
            Object o_8_0 = vc_330;
            // StatementAdderOnAssert create null value
            java.lang.String vc_458 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_330.SELECT_DISTINCT(vc_458);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.apache.ibatis.jdbc.AmplSelectBuilderTest.example2(null, "b", "c");
            org.junit.Assert.fail("shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf22085_cf22467 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


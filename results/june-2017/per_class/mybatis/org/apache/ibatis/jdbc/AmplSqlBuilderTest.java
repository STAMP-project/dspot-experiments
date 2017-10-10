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


public class AmplSqlBuilderTest {
    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatement() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.ID like #id# AND P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2("a", "b", "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, "b", "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstTwoParams() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, null, "c"));
    }

    @org.junit.Test
    public void shouldProduceExpectedSimpleSelectStatementMissingAllParams() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + ("FROM PERSON P\n" + "ORDER BY P.LAST_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, null, null));
    }

    @org.junit.Test
    public void shouldProduceExpectedComplexSelectStatement() {
        java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" + (((((((("FROM PERSON P, ACCOUNT A\n" + "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n") + "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n") + "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n") + "OR (P.LAST_NAME like ?)\n") + "GROUP BY P.ID\n") + "HAVING (P.LAST_NAME like ?) \n") + "OR (P.FIRST_NAME like ?)\n") + "ORDER BY P.ID, P.FULL_NAME");
        org.junit.Assert.assertEquals(expected, org.apache.ibatis.jdbc.AmplSqlBuilderTest.example1());
    }

    private static java.lang.String example1() {
        org.apache.ibatis.jdbc.SqlBuilder.SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
        org.apache.ibatis.jdbc.SqlBuilder.SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
        org.apache.ibatis.jdbc.SqlBuilder.FROM("PERSON P");
        org.apache.ibatis.jdbc.SqlBuilder.FROM("ACCOUNT A");
        org.apache.ibatis.jdbc.SqlBuilder.INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
        org.apache.ibatis.jdbc.SqlBuilder.INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
        org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.ID = A.ID");
        org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.FIRST_NAME like ?");
        org.apache.ibatis.jdbc.SqlBuilder.OR();
        org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.LAST_NAME like ?");
        org.apache.ibatis.jdbc.SqlBuilder.GROUP_BY("P.ID");
        org.apache.ibatis.jdbc.SqlBuilder.HAVING("P.LAST_NAME like ?");
        org.apache.ibatis.jdbc.SqlBuilder.OR();
        org.apache.ibatis.jdbc.SqlBuilder.HAVING("P.FIRST_NAME like ?");
        org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY("P.ID");
        org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY("P.FULL_NAME");
        return org.apache.ibatis.jdbc.SqlBuilder.SQL();
    }

    private static java.lang.String example2(java.lang.String id, java.lang.String firstName, java.lang.String lastName) {
        org.apache.ibatis.jdbc.SqlBuilder.SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME");
        org.apache.ibatis.jdbc.SqlBuilder.FROM("PERSON P");
        if (id != null) {
            org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.ID like #id#");
        }
        if (firstName != null) {
            org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.FIRST_NAME like #firstName#");
        }
        if (lastName != null) {
            org.apache.ibatis.jdbc.SqlBuilder.WHERE("P.LAST_NAME like #lastName#");
        }
        org.apache.ibatis.jdbc.SqlBuilder.ORDER_BY("P.LAST_NAME");
        return org.apache.ibatis.jdbc.SqlBuilder.SQL();
    }

    /* amplification of org.apache.ibatis.jdbc.SqlBuilderTest#shouldProduceExpectedSimpleSelectStatementMissingAllParams */
    @org.junit.Test(timeout = 10000)
    public void shouldProduceExpectedSimpleSelectStatementMissingAllParams_cf21576_cf22068_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + ("FROM PERSON P\n" + "ORDER BY P.LAST_NAME");
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SqlBuilder vc_204 = (org.apache.ibatis.jdbc.SqlBuilder)null;
            // MethodAssertGenerator build local variable
            Object o_4_0 = vc_204;
            // StatementAdderMethod cloned existing statement
            vc_204.RESET();
            // MethodAssertGenerator build local variable
            Object o_8_0 = vc_204;
            // StatementAdderOnAssert create null value
            java.lang.String vc_380 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SqlBuilder vc_378 = (org.apache.ibatis.jdbc.SqlBuilder)null;
            // StatementAdderMethod cloned existing statement
            vc_378.UPDATE(vc_380);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, null, null);
            org.junit.Assert.fail("shouldProduceExpectedSimpleSelectStatementMissingAllParams_cf21576_cf22068 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.jdbc.SqlBuilderTest#shouldProduceExpectedSimpleSelectStatementMissingFirstParam */
    @org.junit.Test(timeout = 10000)
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf23462_cf23642_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SqlBuilder vc_394 = (org.apache.ibatis.jdbc.SqlBuilder)null;
            // MethodAssertGenerator build local variable
            Object o_4_0 = vc_394;
            // StatementAdderMethod cloned existing statement
            vc_394.BEGIN();
            // MethodAssertGenerator build local variable
            Object o_8_0 = vc_394;
            // StatementAdderOnAssert create null value
            java.lang.String vc_476 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            vc_394.DELETE_FROM(vc_476);
            // MethodAssertGenerator build local variable
            Object o_14_0 = org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, "b", "c");
            org.junit.Assert.fail("shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf23462_cf23642 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.apache.ibatis.jdbc.SqlBuilderTest#shouldProduceExpectedSimpleSelectStatementMissingFirstParam */
    @org.junit.Test(timeout = 10000)
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf23546_cf24056_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SqlBuilder vc_438 = (org.apache.ibatis.jdbc.SqlBuilder)null;
            // MethodAssertGenerator build local variable
            Object o_4_0 = vc_438;
            // StatementAdderMethod cloned existing statement
            vc_438.RESET();
            // MethodAssertGenerator build local variable
            Object o_8_0 = vc_438;
            // StatementAdderOnAssert create null value
            java.lang.String vc_614 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.apache.ibatis.jdbc.SqlBuilder vc_612 = (org.apache.ibatis.jdbc.SqlBuilder)null;
            // StatementAdderMethod cloned existing statement
            vc_612.UPDATE(vc_614);
            // MethodAssertGenerator build local variable
            Object o_16_0 = org.apache.ibatis.jdbc.AmplSqlBuilderTest.example2(null, "b", "c");
            org.junit.Assert.fail("shouldProduceExpectedSimpleSelectStatementMissingFirstParam_cf23546_cf24056 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


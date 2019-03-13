package org.apache.ibatis.jdbc;


import org.junit.Assert;
import org.junit.Test;


public class SQLTest {
    @Test
    public void shouldDemonstrateProvidedStringBuilder() {
        // You can pass in your own StringBuilder
        final StringBuilder sb = new StringBuilder();
        // From the tutorial
        final String sql = usingAppender(sb).toString();
        Assert.assertEquals(("SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" + (((((((("FROM PERSON P, ACCOUNT A\n" + "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n") + "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n") + "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n") + "OR (P.LAST_NAME like ?)\n") + "GROUP BY P.ID\n") + "HAVING (P.LAST_NAME like ?) \n") + "OR (P.FIRST_NAME like ?)\n") + "ORDER BY P.ID, P.FULL_NAME")), sql);
    }

    @Test
    public void shouldDemonstrateMixedStyle() {
        // Mixed
        final String sql = new SQL() {
            {
                SELECT("id, name");
                FROM("PERSON A");
                WHERE("id = ?");
            }
        }.toString();
        Assert.assertEquals(("" + (("SELECT id, name\n" + "FROM PERSON A\n") + "WHERE (name like ? AND id = ?)")), sql);
    }

    @Test
    public void shouldDemonstrateFluentStyle() {
        // Fluent Style
        final String sql = WHERE("id = ?").toString();
        Assert.assertEquals(("" + (("SELECT id, name\n" + "FROM PERSON A\n") + "WHERE (name like ? AND id = ?)")), sql);
    }

    @Test
    public void shouldProduceExpectedSimpleSelectStatement() {
        final String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.ID like #id# AND P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        Assert.assertEquals(expected, SQLTest.example2("a", "b", "c"));
    }

    @Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstParam() {
        final String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.FIRST_NAME like #firstName# AND P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        Assert.assertEquals(expected, SQLTest.example2(null, "b", "c"));
    }

    @Test
    public void shouldProduceExpectedSimpleSelectStatementMissingFirstTwoParams() {
        final String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + (("FROM PERSON P\n" + "WHERE (P.LAST_NAME like #lastName#)\n") + "ORDER BY P.LAST_NAME");
        Assert.assertEquals(expected, SQLTest.example2(null, null, "c"));
    }

    @Test
    public void shouldProduceExpectedSimpleSelectStatementMissingAllParams() {
        final String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FIRST_NAME, P.LAST_NAME\n" + ("FROM PERSON P\n" + "ORDER BY P.LAST_NAME");
        Assert.assertEquals(expected, SQLTest.example2(null, null, null));
    }

    @Test
    public void shouldProduceExpectedComplexSelectStatement() {
        final String expected = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON\n" + (((((((("FROM PERSON P, ACCOUNT A\n" + "INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID\n") + "INNER JOIN COMPANY C on D.COMPANY_ID = C.ID\n") + "WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) \n") + "OR (P.LAST_NAME like ?)\n") + "GROUP BY P.ID\n") + "HAVING (P.LAST_NAME like ?) \n") + "OR (P.FIRST_NAME like ?)\n") + "ORDER BY P.ID, P.FULL_NAME");
        Assert.assertEquals(expected, SQLTest.example1());
    }
}


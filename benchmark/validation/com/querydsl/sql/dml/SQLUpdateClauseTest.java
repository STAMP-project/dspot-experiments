package com.querydsl.sql.dml;


import com.google.common.collect.ImmutableList;
import com.querydsl.sql.KeyAccessorsTest;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLTemplates;
import org.junit.Assert;
import org.junit.Test;


public class SQLUpdateClauseTest {
    @Test(expected = IllegalStateException.class)
    public void noConnection() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.id, 1);
        update.execute();
    }

    @Test
    public void getSQL() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.id, 1);
        SQLBindings sql = update.getSQL().get(0);
        Assert.assertEquals("update EMPLOYEE\nset ID = ?", sql.getSQL());
        Assert.assertEquals(ImmutableList.of(1), sql.getBindings());
    }

    @Test
    public void intertable() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        KeyAccessorsTest.QEmployee emp2 = new KeyAccessorsTest.QEmployee("emp2");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.id, 1).where(emp1.id.eq(SQLExpressions.select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull())));
        SQLBindings sql = update.getSQL().get(0);
        Assert.assertEquals(("update EMPLOYEE\n" + ((("set ID = ?\n" + "where EMPLOYEE.ID = (select emp2.ID\n") + "from EMPLOYEE emp2\n") + "where emp2.SUPERIOR_ID is not null)")), sql.getSQL());
    }

    @Test
    public void intertable2() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        KeyAccessorsTest.QEmployee emp2 = new KeyAccessorsTest.QEmployee("emp2");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.id, SQLExpressions.select(emp2.id).from(emp2).where(emp2.superiorId.isNotNull()));
        SQLBindings sql = update.getSQL().get(0);
        Assert.assertEquals(("update EMPLOYEE\n" + (("set ID = (select emp2.ID\n" + "from EMPLOYEE emp2\n") + "where emp2.SUPERIOR_ID is not null)")), sql.getSQL());
    }

    @Test
    public void intertable3() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        KeyAccessorsTest.QEmployee emp2 = new KeyAccessorsTest.QEmployee("emp2");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.superiorId, SQLExpressions.select(emp2.id).from(emp2).where(emp2.id.eq(emp1.id)));
        SQLBindings sql = update.getSQL().get(0);
        Assert.assertEquals(("update EMPLOYEE\n" + (("set SUPERIOR_ID = (select emp2.ID\n" + "from EMPLOYEE emp2\n") + "where emp2.ID = EMPLOYEE.ID)")), sql.getSQL());
    }

    @Test
    public void clear() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLUpdateClause update = new SQLUpdateClause(null, SQLTemplates.DEFAULT, emp1);
        update.set(emp1.id, 1);
        update.addBatch();
        Assert.assertEquals(1, update.getBatchCount());
        update.clear();
        Assert.assertEquals(0, update.getBatchCount());
    }
}


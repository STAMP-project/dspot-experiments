package com.querydsl.sql.dml;


import com.google.common.collect.ImmutableList;
import com.querydsl.sql.KeyAccessorsTest;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLTemplates;
import org.junit.Assert;
import org.junit.Test;


public class SQLDeleteClauseTest {
    @Test(expected = IllegalStateException.class)
    public void noConnection() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
        delete.where(emp1.id.eq(1));
        delete.execute();
    }

    @Test
    public void getSQL() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
        delete.where(emp1.id.eq(1));
        SQLBindings sql = delete.getSQL().get(0);
        Assert.assertEquals("delete from EMPLOYEE\nwhere EMPLOYEE.ID = ?", sql.getSQL());
        Assert.assertEquals(ImmutableList.of(1), sql.getBindings());
    }

    @Test
    public void clear() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLDeleteClause delete = new SQLDeleteClause(null, SQLTemplates.DEFAULT, emp1);
        delete.where(emp1.id.eq(1));
        delete.addBatch();
        Assert.assertEquals(1, delete.getBatchCount());
        delete.clear();
        Assert.assertEquals(0, delete.getBatchCount());
    }
}


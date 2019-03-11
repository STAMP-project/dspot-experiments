package com.querydsl.sql.dml;


import QueryFlag.Position.END;
import com.google.common.collect.ImmutableList;
import com.querydsl.sql.KeyAccessorsTest;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.domain.QEmployee;
import org.junit.Assert;
import org.junit.Test;


public class SQLInsertClauseTest {
    @Test(expected = IllegalStateException.class)
    public void noConnection() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLInsertClause insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
        insert.set(emp1.id, 1);
        insert.execute();
    }

    @Test
    public void getSQL() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLInsertClause insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
        insert.set(emp1.id, 1);
        SQLBindings sql = insert.getSQL().get(0);
        Assert.assertEquals("insert into EMPLOYEE (ID)\nvalues (?)", sql.getSQL());
        Assert.assertEquals(ImmutableList.of(1), sql.getBindings());
    }

    @Test
    public void bulk() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLInsertClause insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
        insert.set(emp1.id, 1);
        insert.addBatch();
        insert.set(emp1.id, 2);
        insert.addBatch();
        insert.addFlag(END, " on duplicate key ignore");
        insert.setBatchToBulk(true);
        Assert.assertEquals(("insert into EMPLOYEE (ID)\n" + "values (?), (?) on duplicate key ignore"), insert.getSQL().get(0).getSQL());
    }

    @Test
    public void getSQLWithPreservedColumnOrder() {
        QEmployee emp1 = new QEmployee("emp1");
        SQLInsertClause insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
        insert.populate(emp1);
        SQLBindings sql = insert.getSQL().get(0);
        Assert.assertEquals("The order of columns in generated sql should be predictable", ("insert into EMPLOYEE (ID, FIRSTNAME, LASTNAME, SALARY, DATEFIELD, TIMEFIELD, SUPERIOR_ID)\n" + "values (EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID)"), sql.getSQL());
    }

    @Test
    public void clear() {
        KeyAccessorsTest.QEmployee emp1 = new KeyAccessorsTest.QEmployee("emp1");
        SQLInsertClause insert = new SQLInsertClause(null, SQLTemplates.DEFAULT, emp1);
        insert.set(emp1.id, 1);
        insert.addBatch();
        Assert.assertEquals(1, insert.getBatchCount());
        insert.clear();
        Assert.assertEquals(0, insert.getBatchCount());
    }
}


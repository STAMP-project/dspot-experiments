package com.querydsl.sql;


import com.querydsl.sql.domain.QEmployee;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;


public class ColumnMetadataTest {
    @Test
    public void defaultColumn() {
        ColumnMetadata column = ColumnMetadata.named("Person");
        Assert.assertEquals("Person", column.getName());
        Assert.assertFalse(column.hasJdbcType());
        Assert.assertFalse(column.hasSize());
        Assert.assertTrue(column.isNullable());
    }

    @Test
    public void fullyConfigured() {
        ColumnMetadata column = ColumnMetadata.named("Person").withSize(10).notNull().ofType(Types.BIGINT);
        Assert.assertEquals("Person", column.getName());
        Assert.assertTrue(column.hasJdbcType());
        Assert.assertEquals(Types.BIGINT, column.getJdbcType());
        Assert.assertTrue(column.hasSize());
        Assert.assertEquals(10, column.getSize());
        Assert.assertFalse(column.isNullable());
    }

    @Test
    public void extractFromRelationalPath() {
        ColumnMetadata column = ColumnMetadata.getColumnMetadata(QEmployee.employee.id);
        Assert.assertEquals("ID", column.getName());
    }

    @Test
    public void fallBackToDefaultWhenMissing() {
        ColumnMetadata column = ColumnMetadata.getColumnMetadata(QEmployee.employee.salary);
        Assert.assertEquals("SALARY", column.getName());
    }
}


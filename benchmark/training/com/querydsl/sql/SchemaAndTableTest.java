package com.querydsl.sql;


import org.junit.Assert;
import org.junit.Test;


public class SchemaAndTableTest {
    @Test
    public void equalsAndHashCode() {
        SchemaAndTable st1 = new SchemaAndTable(null, "table");
        SchemaAndTable st2 = new SchemaAndTable(null, "table");
        Assert.assertEquals(st1, st2);
        Assert.assertEquals(st1.hashCode(), st2.hashCode());
    }
}


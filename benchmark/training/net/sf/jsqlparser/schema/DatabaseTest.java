/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.schema;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author schwitters
 */
public class DatabaseTest {
    public DatabaseTest() {
    }

    @Test
    public void testDatabaseSimple() {
        String databaseName = "db1";
        Database database = new Database(databaseName);
        Assert.assertEquals(databaseName, database.getFullyQualifiedName());
    }

    @Test
    public void testDatabaseAndServer() {
        final Server server = new Server("SERVER", "INSTANCE");
        String databaseName = "db1";
        Database database = new Database(server, databaseName);
        Assert.assertEquals("[SERVER\\INSTANCE].db1", database.getFullyQualifiedName());
        Assert.assertSame(server, database.getServer());
        Assert.assertEquals(databaseName, database.getDatabaseName());
        Assert.assertEquals("[SERVER\\INSTANCE].db1", database.toString());
    }

    @Test
    public void testNullDatabaseAndServer() {
        final Server server = new Server("SERVER", "INSTANCE");
        Database database = new Database(server, null);
        Assert.assertEquals("[SERVER\\INSTANCE].", database.getFullyQualifiedName());
        Assert.assertSame(server, database.getServer());
    }
}


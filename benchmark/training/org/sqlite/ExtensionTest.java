package org.sqlite;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.Assert;
import org.junit.Test;


public class ExtensionTest {
    Connection conn;

    Statement stat;

    @Test
    public void extFTS3() throws Exception {
        stat.execute("create virtual table recipe using fts3(name, ingredients)");
        stat.execute("insert into recipe (name, ingredients) values('broccoli stew', 'broccoli peppers cheese tomatoes')");
        stat.execute("insert into recipe (name, ingredients) values('pumpkin stew', 'pumpkin onions garlic celery')");
        ResultSet rs = stat.executeQuery("select rowid, name, ingredients from recipe where ingredients match 'onions'");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("pumpkin stew", rs.getString(2));
    }

    @Test
    public void extFTS5() throws Exception {
        stat.execute("create virtual table recipe using fts5(name, ingredients)");
        stat.execute("insert into recipe (name, ingredients) values('broccoli stew', 'broccoli peppers cheese tomatoes')");
        stat.execute("insert into recipe (name, ingredients) values('pumpkin stew', 'pumpkin onions garlic celery')");
        ResultSet rs = stat.executeQuery("select rowid, name, ingredients from recipe where recipe match 'onions'");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("pumpkin stew", rs.getString(2));
    }

    @Test
    public void extFunctions() throws Exception {
        {
            ResultSet rs = stat.executeQuery("select cos(radians(45))");
            Assert.assertTrue(rs.next());
            Assert.assertEquals(0.707106781186548, rs.getDouble(1), 1.0E-15);
            rs.close();
        }
        {
            ResultSet rs = stat.executeQuery("select reverse(\"ACGT\")");
            Assert.assertTrue(rs.next());
            Assert.assertEquals("TGCA", rs.getString(1));
            rs.close();
        }
    }
}


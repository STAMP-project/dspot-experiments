package org.sqlite;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA. User: david_donn Date: 19/01/2010 Time: 11:50:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class FetchSizeTest {
    private Connection conn;

    @Test
    public void testFetchSize() throws SQLException {
        Assert.assertEquals(conn.prepareStatement("create table s1 (c1)").executeUpdate(), 0);
        PreparedStatement insertPrep = conn.prepareStatement("insert into s1 values (?)");
        insertPrep.setInt(1, 1);
        Assert.assertEquals(insertPrep.executeUpdate(), 1);
        insertPrep.setInt(1, 2);
        Assert.assertEquals(insertPrep.executeUpdate(), 1);
        insertPrep.setInt(1, 3);
        Assert.assertEquals(insertPrep.executeUpdate(), 1);
        insertPrep.setInt(1, 4);
        Assert.assertEquals(insertPrep.executeUpdate(), 1);
        insertPrep.setInt(1, 5);
        Assert.assertEquals(insertPrep.executeUpdate(), 1);
        insertPrep.close();
        PreparedStatement selectPrep = conn.prepareStatement("select c1 from s1");
        ResultSet rs = selectPrep.executeQuery();
        rs.setFetchSize(2);
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        Assert.assertFalse(rs.next());
    }
}


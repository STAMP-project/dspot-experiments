package cn.hutool.db;


import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * HSQLDB???????
 *
 * @author looly
 */
public class HsqldbTest {
    private static final String DS_GROUP_NAME = "hsqldb";

    @Test
    public void connTest() throws SQLException {
        List<Entity> query = Db.use(HsqldbTest.DS_GROUP_NAME).query("select * from test");
        Assert.assertEquals(4, query.size());
    }
}


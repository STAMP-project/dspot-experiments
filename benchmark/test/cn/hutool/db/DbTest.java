package cn.hutool.db;


import cn.hutool.core.lang.Console;
import cn.hutool.db.sql.Condition;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Db??????
 *
 * @author looly
 */
public class DbTest {
    @Test
    public void findTest() throws SQLException {
        Db.use();
        List<Entity> find = Db.use().find(Entity.create("user").set("age", 18));
        Assert.assertEquals("??", find.get(0).get("name"));
    }

    @Test
    public void findByTest() throws SQLException {
        Db.use();
        List<Entity> find = Db.use().findBy("user", Condition.parse("age", "> 18"), Condition.parse("age", "< 100"));
        for (Entity entity : find) {
            Console.log(entity);
        }
        Assert.assertEquals("unitTestUser", find.get(0).get("name"));
    }
}


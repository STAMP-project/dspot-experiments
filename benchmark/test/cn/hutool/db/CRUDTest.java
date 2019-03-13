package cn.hutool.db;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Condition.LikeType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??????
 *
 * @author looly
 */
public class CRUDTest {
    private static Db db = Db.use("test");

    @Test
    public void findIsNullTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", "is null"));
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void findIsNullTest2() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", "= null"));
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void findIsNullTest3() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", null));
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void findBetweenTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", "between '18' and '40'"));
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void findByBigIntegerTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", new BigInteger("12")));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findByBigDecimalTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("age", new BigDecimal("12")));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findLikeTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("name", "like \"%\u4e09%\""));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findLikeTest2() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("name", new Condition("name", "?", LikeType.Contains)));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findLikeTest3() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("name", new Condition("name", null, LikeType.Contains)));
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void findInTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("id", "in 1,2,3"));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findInTest2() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll(Entity.create("user").set("id", new Condition("id", new long[]{ 1, 2, 3 })));
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void findAllTest() throws SQLException {
        List<Entity> results = CRUDTest.db.findAll("user");
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void findTest() throws SQLException {
        List<Entity> find = CRUDTest.db.find(CollUtil.newArrayList("name AS name2"), Entity.create("user"), new EntityListHandler());
        Assert.assertFalse(find.isEmpty());
    }

    @Test
    public void findActiveTest() throws SQLException {
        ActiveEntity entity = new ActiveEntity(CRUDTest.db, "user");
        entity.setFieldNames("name AS name2").load();
        Assert.assertEquals("user", entity.getTableName());
        Assert.assertFalse(entity.isEmpty());
    }
}


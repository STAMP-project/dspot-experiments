package cn.hutool.db;


import cn.hutool.core.map.MapUtil;
import cn.hutool.db.sql.NamedSql;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class NamedSqlTest {
    @Test
    public void parseTest() {
        String sql = "select * from table where id=@id and name = @name1 and nickName = :subName";
        Map<String, Object> paramMap = MapUtil.builder("name1", ((Object) ("\u5f20\u4e09"))).put("age", 12).put("subName", "???").build();
        NamedSql namedSql = new NamedSql(sql, paramMap);
        // ?????????
        Assert.assertEquals("select * from table where id=@id and name = ? and nickName = ?", namedSql.getSql());
        Assert.assertEquals("??", namedSql.getParams()[0]);
        Assert.assertEquals("???", namedSql.getParams()[1]);
    }

    @Test
    public void parseTest2() {
        String sql = "select * from table where id=@id and name = @name1 and nickName = :subName";
        Map<String, Object> paramMap = MapUtil.builder("name1", ((Object) ("\u5f20\u4e09"))).put("age", 12).put("subName", "???").put("id", null).build();
        NamedSql namedSql = new NamedSql(sql, paramMap);
        Assert.assertEquals("select * from table where id=? and name = ? and nickName = ?", namedSql.getSql());
        // ???null????????????null
        Assert.assertNull(namedSql.getParams()[0]);
        Assert.assertEquals("??", namedSql.getParams()[1]);
        Assert.assertEquals("???", namedSql.getParams()[2]);
    }
}


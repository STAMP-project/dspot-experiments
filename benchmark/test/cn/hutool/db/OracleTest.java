package cn.hutool.db;


import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Oracle??????
 *
 * @author looly
 */
public class OracleTest {
    @Test
    public void oraclePageSqlTest() {
        Page page = new Page(1, 10);
        Entity where = Entity.create("PMCPERFORMANCEINFO").set("yearPI", "2017");
        final Query query = new Query(SqlUtil.buildConditions(where), where.getTableName());
        query.setPage(page);
        SqlBuilder find = SqlBuilder.create(null).query(query).orderBy(page.getOrders());
        final int[] startEnd = page.getStartEnd();
        SqlBuilder builder = // 
        // 
        // 
        // 
        SqlBuilder.create(null).append("SELECT * FROM ( SELECT row_.*, rownum rownum_ from ( ").append(find).append(" ) row_ where rownum <= ").append(startEnd[1]).append(") table_alias").append(" where table_alias.rownum_ >= ").append(startEnd[0]);// 

        String ok = "SELECT * FROM "// 
         + ("( SELECT row_.*, rownum rownum_ from ( SELECT * FROM PMCPERFORMANCEINFO WHERE yearPI = ? ) row_ "// 
         + "where rownum <= 10) table_alias where table_alias.rownum_ >= 0");// 

        Assert.assertEquals(ok, builder.toString());
    }
}


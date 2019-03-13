package com.alibaba.druid.bvt.sql.mysql.create;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlCeateTableTest34 extends TestCase {
    public void test_for_parse() throws Exception {
        String sql = "CREATE TABLE \"item_extra\" (\n" + ((((((("  \"id\" int(10) unsigned NOT NULL AUTO_INCREMENT,\n" + "  \"item_id\" int(10) unsigned NOT NULL DEFAULT \'0\' COMMENT \'\u5546\u54c1id\',\n") + "  \"type\" tinyint(4) unsigned NOT NULL DEFAULT \'0\' COMMENT \'\u6269\u5c55\u5c5e\u6027\u7c7b\u578b\uff0c0\uff1a\u865a\u62df\u5546\u54c1\',\n") + "  \"attr_key\" varchar(50) NOT NULL COMMENT \'\u6269\u5c55\u5c5e\u6027key\',\n") + "  \"attr_value\" varchar(500) NOT NULL COMMENT \'\u6269\u5c55\u5c5e\u6027value\',\n") + "  PRIMARY KEY (\"id\"),\n") + "  KEY \"idx_item_type_kv\" (\"item_id\",\"type\",\"attr_key\",\"attr_value\"(191))\n") + ")");// 

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        // print(statementList);
        Assert.assertEquals(1, statementList.size());
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        // System.out.println("orderBy : " + visitor.getOrderByColumns());
        Assert.assertEquals(1, visitor.getTables().size());
        Assert.assertEquals(5, visitor.getColumns().size());
        Assert.assertEquals(0, visitor.getConditions().size());
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("item_extra")));
        String output = SQLUtils.toMySqlString(stmt);
        Assert.assertEquals(("CREATE TABLE \"item_extra\" (\n" + ((((((("\t\"id\" int(10) UNSIGNED NOT NULL AUTO_INCREMENT,\n" + "\t\"item_id\" int(10) UNSIGNED NOT NULL DEFAULT \'0\' COMMENT \'\u5546\u54c1id\',\n") + "\t\"type\" tinyint(4) UNSIGNED NOT NULL DEFAULT \'0\' COMMENT \'\u6269\u5c55\u5c5e\u6027\u7c7b\u578b\uff0c0\uff1a\u865a\u62df\u5546\u54c1\',\n") + "\t\"attr_key\" varchar(50) NOT NULL COMMENT \'\u6269\u5c55\u5c5e\u6027key\',\n") + "\t\"attr_value\" varchar(500) NOT NULL COMMENT \'\u6269\u5c55\u5c5e\u6027value\',\n") + "\tPRIMARY KEY (\"id\"),\n") + "\tKEY \"idx_item_type_kv\" (\"item_id\", \"type\", \"attr_key\", \"attr_value\"(191))\n") + ")")), output);
    }
}


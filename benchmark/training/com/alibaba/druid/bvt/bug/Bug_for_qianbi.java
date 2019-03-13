package com.alibaba.druid.bvt.bug;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsOutputVisitor;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_qianbi extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "insert into table lol select detail(sellerid,id) as count1,sum(sellerid) as sum1 from ctu_trade_paid_done.time('natural','1d','1h') " + "where match(auctionTitle,\"\u7537\u978b\\n\u4e2d\u77f3\u5316&\u52a0\u6cb9\u5361\\n\u4e2d\u77f3\u5316&\u5145\u503c\u5361\\n\u4e2d\u77f3\u5316&\u51b2\u503c\u5361\\n\u4e2d\u77f3\u5316&\u4ee3\u51b2\\n\u4e2d\u77f3\u5316&\u4ee3\u5145\\n\u4e2d\u56fd\u77f3\u5316&\u52a0\u6cb9\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u5145\u503c\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u51b2\u503c\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u4ee3\u51b2\\n\u4e2d\u56fd\u77f3\u5316&\u4ee3\u5145\",\"\\n\")";
        String expected = "INSERT INTO TABLE lol\n" + (("SELECT detail(sellerid, id) AS count1, SUM(sellerid) AS sum1\n" + "FROM ctu_trade_paid_done:time(\'natural\', \'1d\', \'1h\')\n") + "WHERE match(auctionTitle, \'\u7537\u978b\\n\u4e2d\u77f3\u5316&\u52a0\u6cb9\u5361\\n\u4e2d\u77f3\u5316&\u5145\u503c\u5361\\n\u4e2d\u77f3\u5316&\u51b2\u503c\u5361\\n\u4e2d\u77f3\u5316&\u4ee3\u51b2\\n\u4e2d\u77f3\u5316&\u4ee3\u5145\\n\u4e2d\u56fd\u77f3\u5316&\u52a0\u6cb9\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u5145\u503c\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u51b2\u503c\u5361\\n\u4e2d\u56fd\u77f3\u5316&\u4ee3\u51b2\\n\u4e2d\u56fd\u77f3\u5316&\u4ee3\u5145\', \'\\n\');\n");
        StringBuilder out = new StringBuilder();
        OdpsOutputVisitor visitor = new OdpsOutputVisitor(out);
        OdpsStatementParser parser = new OdpsStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
            visitor.print(";");
            visitor.println();
        }
        // System.out.println(out.toString());
        Assert.assertEquals(expected, out.toString());
    }
}


package com.alibaba.druid.bvt.sql.mysql;


import JdbcConstants.MYSQL;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import java.io.File;
import java.net.URL;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;


public class AntlrMySqlTest extends TestCase {
    public void test_for_antlr_examples() throws Exception {
        SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(MYSQL);
        MySqlWallProvider provider = new MySqlWallProvider();
        String path = "bvt/parser/antlr_grammers_v4_mysql/examples/";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            System.out.println(file);
            String sql = FileUtils.readFileToString(file);
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, MYSQL);
            for (SQLStatement stmt : stmtList) {
                stmt.toString();
                stmt.accept(schemaStatVisitor);
                provider.checkValid(sql);
            }
        }
    }
}


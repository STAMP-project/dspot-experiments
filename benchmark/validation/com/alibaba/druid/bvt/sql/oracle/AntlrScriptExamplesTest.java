package com.alibaba.druid.bvt.sql.oracle;


import JdbcConstants.ORACLE;
import com.alibaba.druid.sql.SQLUtils;
import java.io.File;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;


public class AntlrScriptExamplesTest extends TestCase {
    public void test_for_antlr_examples() throws Exception {
        String path = "bvt/parser/antlr_grammers_v4_plsql/examples-sql-script/";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            System.out.println(file);
            String sql = FileUtils.readFileToString(file);
            SQLUtils.parseStatements(sql, ORACLE);
        }
    }
}


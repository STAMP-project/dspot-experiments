package unitTest;


import genCode.utils.GenSqlUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


// v7.9??v8.0
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:springMVC.xml" })
public class UpdateProjectId {
    private Map<String, String> moduleProjectMap = new HashMap<String, String>();

    @Test
    public void addProjectIdForArticle() {
        getModuleProjectMap();
        Connection conn = GenSqlUtil.openConnection();// ???????

        PreparedStatement pstmt = null;
        String[] sqls = new String[]{ "update article set projectId='%s' where moduleId='%s'", "update interface set projectId='%s' where moduleId='%s'", "update source set projectId='%s' where moduleId='%s'" };
        try {
            Iterator<Map.Entry<String, String>> entries = moduleProjectMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                for (String sql : sqls) {
                    pstmt = conn.prepareStatement(String.format(sql, entry.getValue(), entry.getKey()));
                    pstmt.execute();
                    System.out.println(String.format(sql, entry.getValue(), entry.getKey()));
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            GenSqlUtil.closeDatabase(conn, pstmt);
        }
    }
}


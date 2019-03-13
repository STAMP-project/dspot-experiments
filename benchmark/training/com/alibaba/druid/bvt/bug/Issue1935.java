package com.alibaba.druid.bvt.bug;


import TableStat.Column;
import TableStat.Condition;
import TableStat.Name;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1935 extends TestCase {
    public void test_for_issue() throws Exception {
        String DBTYPE = JdbcConstants.MYSQL;
        // String sql = "select name, course ,scole from student inner join scole on student.id = scole.sd_id where course = '??' limit 10;";
        // sql = "select name,course,sum(scole) as total from student where student.id in (select sd_id from scole where name='aaa') and scole in (1,2,3) group by name HAVING total <60 order by scole desc limit 10 ,2 ";
        String sql = "select name from  student where id in (select sd_id from scole where scole < 60 order by scole asc) or id = 2 order by name desc";
        String format = SQLUtils.format(sql, DBTYPE);
        // System.out.println("formated sql :  " + format);
        List<SQLStatement> list = SQLUtils.parseStatements(sql, DBTYPE);
        for (int i = 0; i < (list.size()); i++) {
            SQLStatement stmt = list.get(i);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);
            // ????????,??????
            System.out.println(("??????? : " + (visitor.getTables())));
            Map<TableStat.Name, TableStat> table_map = visitor.getTables();
            for (Map.Entry<TableStat.Name, TableStat> entry : table_map.entrySet()) {
                TableStat.Name name = entry.getKey();
                name.getName();
                // ???????????select ?update?
                TableStat ts = entry.getValue();
            }
            // ??????
            System.out.println(visitor.getParameters());
            // ????
            System.out.println(("?????? : " + (visitor.getColumns())));
            Collection<TableStat.Column> cc = visitor.getColumns();
            // column ???????????????????where?select?groupby ?order
            for (TableStat.Column column : cc) {
            }
            System.out.println(("conditions : " + (visitor.getConditions())));
            List<TableStat.Condition> conditions = visitor.getConditions();
            System.out.println("----------------------------");
            for (TableStat.Condition cond : conditions) {
                System.out.println(("column : " + (cond.getColumn())));
                System.out.println(("operator : " + (cond.getOperator())));
                System.out.println(("values  : " + (cond.getValues())));
                System.out.println("----------------------------");
            }
            System.out.println(("group by : " + (visitor.getGroupByColumns())));
            System.out.println(("order by : " + (visitor.getOrderByColumns())));
            System.out.println(("relations ships  : " + (visitor.getRelationships())));
        }
    }
}


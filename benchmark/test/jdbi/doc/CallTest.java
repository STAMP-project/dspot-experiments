package jdbi.doc;


import java.sql.Types;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.OutParameters;
import org.jdbi.v3.postgres.PostgresDbRule;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Rule;
import org.junit.Test;


public class CallTest {
    @Rule
    public JdbiRule db = PostgresDbRule.rule();

    @Test
    public void testCall() {
        Handle handle = db.getHandle();
        handle.execute(findSqlOnClasspath("create_stored_proc_add"));
        // tag::invokeProcedure[]
        OutParameters result = // <3> <4>
        // <2>
        // <2>
        // <1>
        handle.createCall("{:sum = call add(:a, :b)}").bind("a", 13).bind("b", 9).registerOutParameter("sum", Types.INTEGER).invoke();// <5>

        // end::invokeProcedure[]
        // tag::getOutParameters[]
        int sum = result.getInt("sum");
        // end::getOutParameters[]
        assertThat(sum).isEqualTo(22);
    }
}


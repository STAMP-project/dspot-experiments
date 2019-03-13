package io.crate.analyze.relations;


import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import org.hamcrest.core.Is;
import org.junit.Test;


public class GroupByScalarAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private SQLExecutor executor;

    @Test
    public void testScalarFunctionArgumentsNotAllInGroupByThrowsException() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'(id * other_id)' must appear in the GROUP BY");
        executor.analyze("select id * other_id from users group by id");
    }

    @Test
    public void testValidGroupByWithScalarAndMultipleColumns() throws Exception {
        QueriedRelation relation = executor.analyze("select id * other_id from users group by id, other_id");
        assertThat(relation.fields().get(0).path().outputName(), Is.is("(id * other_id)"));
    }

    @Test
    public void testValidGroupByWithScalar() throws Exception {
        QueriedRelation relation = executor.analyze("select id * 2 from users group by id");
        assertThat(relation.fields().get(0).path().outputName(), Is.is("(id * 2)"));
    }

    @Test
    public void testValidGroupByWithMultipleScalarFunctions() throws Exception {
        QueriedRelation relation = executor.analyze("select abs(id * 2) from users group by id");
        assertThat(relation.fields().get(0).path().outputName(), Is.is("abs((id * 2))"));
    }
}


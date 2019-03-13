package liquibase.statement;


import org.junit.Assert;
import org.junit.Test;


public class AutoIncrementConstraintTest {
    @Test
    public void ctor() {
        AutoIncrementConstraint constraint = new AutoIncrementConstraint("COL_NAME");
        Assert.assertEquals("COL_NAME", constraint.getColumnName());
    }
}


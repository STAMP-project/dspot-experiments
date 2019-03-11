package aima.test.core.unit.search.csp;


import aima.core.search.csp.Assignment;
import aima.core.search.csp.Variable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
// @Test
// public void testAssignmentDefaultVariableSelection() {
// Assert.assertEquals(X, assignment.selectFirstUnassignedVariable(csp));
// assignment.add(X, "Ravi");
// Assert.assertEquals(Y, assignment.selectFirstUnassignedVariable(csp));
// assignment.add(Y, "AIMA");
// Assert.assertEquals(null, assignment.selectFirstUnassignedVariable(csp));
// }
public class AssignmentTest {
    private static final Variable X = new Variable("x");

    private static final Variable Y = new Variable("y");

    private List<Variable> variables;

    private Assignment<Variable, String> assignment;

    @Test
    public void testAssignmentCompletion() {
        Assert.assertFalse(assignment.isComplete(variables));
        assignment.add(AssignmentTest.X, "Ravi");
        Assert.assertFalse(assignment.isComplete(variables));
        assignment.add(AssignmentTest.Y, "AIMA");
        Assert.assertTrue(assignment.isComplete(variables));
        assignment.remove(AssignmentTest.X);
        Assert.assertFalse(assignment.isComplete(variables));
    }
}


package aima.test.core.unit.search.csp;


import aima.core.search.csp.Assignment;
import aima.core.search.csp.CSP;
import aima.core.search.csp.Constraint;
import aima.core.search.csp.Domain;
import aima.core.search.csp.TreeCspSolver;
import aima.core.search.csp.Variable;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class TreeCspSolverTest {
    private static final Variable WA = new Variable("wa");

    private static final Variable NT = new Variable("nt");

    private static final Variable Q = new Variable("q");

    private static final Variable NSW = new Variable("nsw");

    private static final Variable V = new Variable("v");

    private static final Constraint<Variable, String> C1 = new aima.core.search.csp.examples.NotEqualConstraint(TreeCspSolverTest.WA, TreeCspSolverTest.NT);

    private static final Constraint<Variable, String> C2 = new aima.core.search.csp.examples.NotEqualConstraint(TreeCspSolverTest.NT, TreeCspSolverTest.Q);

    private static final Constraint<Variable, String> C3 = new aima.core.search.csp.examples.NotEqualConstraint(TreeCspSolverTest.Q, TreeCspSolverTest.NSW);

    private static final Constraint<Variable, String> C4 = new aima.core.search.csp.examples.NotEqualConstraint(TreeCspSolverTest.NSW, TreeCspSolverTest.V);

    private Domain<String> colors;

    private List<Variable> variables;

    @Test
    public void testConstraintNetwork() {
        CSP<Variable, String> csp = new CSP(variables);
        csp.addConstraint(TreeCspSolverTest.C1);
        csp.addConstraint(TreeCspSolverTest.C2);
        csp.addConstraint(TreeCspSolverTest.C3);
        csp.addConstraint(TreeCspSolverTest.C4);
        Assert.assertNotNull(csp.getConstraints());
        Assert.assertEquals(4, csp.getConstraints().size());
        Assert.assertNotNull(csp.getConstraints(TreeCspSolverTest.WA));
        Assert.assertEquals(1, csp.getConstraints(TreeCspSolverTest.WA).size());
        Assert.assertNotNull(csp.getConstraints(TreeCspSolverTest.NT));
        Assert.assertEquals(2, csp.getConstraints(TreeCspSolverTest.NT).size());
        Assert.assertNotNull(csp.getConstraints(TreeCspSolverTest.Q));
        Assert.assertEquals(2, csp.getConstraints(TreeCspSolverTest.Q).size());
        Assert.assertNotNull(csp.getConstraints(TreeCspSolverTest.NSW));
        Assert.assertEquals(2, csp.getConstraints(TreeCspSolverTest.NSW).size());
    }

    @Test
    public void testDomainChanges() {
        Domain<String> colors2 = new Domain(colors.asList());
        Assert.assertEquals(colors, colors2);
        CSP<Variable, String> csp = new CSP(variables);
        csp.addConstraint(TreeCspSolverTest.C1);
        Assert.assertNotNull(csp.getDomain(TreeCspSolverTest.WA));
        Assert.assertEquals(0, csp.getDomain(TreeCspSolverTest.WA).size());
        Assert.assertNotNull(csp.getConstraints(TreeCspSolverTest.WA));
        csp.setDomain(TreeCspSolverTest.WA, colors);
        Assert.assertEquals(colors, csp.getDomain(TreeCspSolverTest.WA));
        Assert.assertEquals(3, csp.getDomain(TreeCspSolverTest.WA).size());
        Assert.assertEquals("red", csp.getDomain(TreeCspSolverTest.WA).get(0));
        CSP<Variable, String> cspCopy = csp.copyDomains();
        Assert.assertNotNull(cspCopy.getDomain(TreeCspSolverTest.WA));
        Assert.assertEquals(3, cspCopy.getDomain(TreeCspSolverTest.WA).size());
        Assert.assertEquals("red", cspCopy.getDomain(TreeCspSolverTest.WA).get(0));
        Assert.assertNotNull(cspCopy.getDomain(TreeCspSolverTest.NT));
        Assert.assertEquals(0, cspCopy.getDomain(TreeCspSolverTest.NT).size());
        Assert.assertNotNull(cspCopy.getConstraints(TreeCspSolverTest.NT));
        Assert.assertEquals(TreeCspSolverTest.C1, cspCopy.getConstraints(TreeCspSolverTest.NT).get(0));
        cspCopy.removeValueFromDomain(TreeCspSolverTest.WA, "red");
        Assert.assertEquals(2, cspCopy.getDomain(TreeCspSolverTest.WA).size());
        Assert.assertEquals("green", cspCopy.getDomain(TreeCspSolverTest.WA).get(0));
        Assert.assertEquals(3, csp.getDomain(TreeCspSolverTest.WA).size());
        Assert.assertEquals("red", csp.getDomain(TreeCspSolverTest.WA).get(0));
    }

    @Test
    public void testCSPSolver() {
        CSP<Variable, String> csp = new CSP(variables);
        csp.addConstraint(TreeCspSolverTest.C1);
        csp.addConstraint(TreeCspSolverTest.C2);
        csp.addConstraint(TreeCspSolverTest.C3);
        csp.addConstraint(TreeCspSolverTest.C4);
        csp.setDomain(TreeCspSolverTest.WA, colors);
        csp.setDomain(TreeCspSolverTest.NT, colors);
        csp.setDomain(TreeCspSolverTest.Q, colors);
        csp.setDomain(TreeCspSolverTest.NSW, colors);
        csp.setDomain(TreeCspSolverTest.V, colors);
        TreeCspSolver<Variable, String> treeCSPSolver = new TreeCspSolver();
        Optional<Assignment<Variable, String>> assignment = treeCSPSolver.solve(csp);
        Assert.assertTrue(assignment.isPresent());
        Assert.assertTrue(assignment.get().isComplete(csp.getVariables()));
        Assert.assertTrue(assignment.get().isSolution(csp));
    }
}


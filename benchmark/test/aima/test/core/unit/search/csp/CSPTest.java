package aima.test.core.unit.search.csp;


import aima.core.search.csp.CSP;
import aima.core.search.csp.Constraint;
import aima.core.search.csp.Domain;
import aima.core.search.csp.Variable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ruediger Lunde
 */
public class CSPTest {
    private static final Variable X = new Variable("x");

    private static final Variable Y = new Variable("y");

    private static final Variable Z = new Variable("z");

    private static final Constraint<Variable, String> C1 = new aima.core.search.csp.examples.NotEqualConstraint(CSPTest.X, CSPTest.Y);

    private static final Constraint<Variable, String> C2 = new aima.core.search.csp.examples.NotEqualConstraint(CSPTest.X, CSPTest.Y);

    private Domain<String> colors;

    private Domain<String> animals;

    private List<Variable> variables;

    @Test
    public void testConstraintNetwork() {
        CSP<Variable, String> csp = new CSP(variables);
        csp.addConstraint(CSPTest.C1);
        csp.addConstraint(CSPTest.C2);
        Assert.assertNotNull(csp.getConstraints());
        Assert.assertEquals(2, csp.getConstraints().size());
        Assert.assertNotNull(csp.getConstraints(CSPTest.X));
        Assert.assertEquals(2, csp.getConstraints(CSPTest.X).size());
        Assert.assertNotNull(csp.getConstraints(CSPTest.Y));
        Assert.assertEquals(2, csp.getConstraints(CSPTest.Y).size());
        Assert.assertNotNull(csp.getConstraints(CSPTest.Z));
        Assert.assertEquals(0, csp.getConstraints(CSPTest.Z).size());
    }

    @Test
    public void testDomainChanges() {
        Domain<String> colors2 = new Domain(colors.asList());
        Assert.assertEquals(colors, colors2);
        CSP<Variable, String> csp = new CSP(variables);
        csp.addConstraint(CSPTest.C1);
        Assert.assertNotNull(csp.getDomain(CSPTest.X));
        Assert.assertEquals(0, csp.getDomain(CSPTest.X).size());
        Assert.assertNotNull(csp.getConstraints(CSPTest.X));
        csp.setDomain(CSPTest.X, colors);
        Assert.assertEquals(colors, csp.getDomain(CSPTest.X));
        Assert.assertEquals(3, csp.getDomain(CSPTest.X).size());
        Assert.assertEquals("red", csp.getDomain(CSPTest.X).get(0));
        CSP<Variable, String> cspCopy = csp.copyDomains();
        Assert.assertNotNull(cspCopy.getDomain(CSPTest.X));
        Assert.assertEquals(3, cspCopy.getDomain(CSPTest.X).size());
        Assert.assertEquals("red", cspCopy.getDomain(CSPTest.X).get(0));
        Assert.assertNotNull(cspCopy.getDomain(CSPTest.Y));
        Assert.assertEquals(0, cspCopy.getDomain(CSPTest.Y).size());
        Assert.assertNotNull(cspCopy.getConstraints(CSPTest.X));
        Assert.assertEquals(CSPTest.C1, cspCopy.getConstraints(CSPTest.X).get(0));
        cspCopy.removeValueFromDomain(CSPTest.X, "red");
        Assert.assertEquals(2, cspCopy.getDomain(CSPTest.X).size());
        Assert.assertEquals("green", cspCopy.getDomain(CSPTest.X).get(0));
        Assert.assertEquals(3, csp.getDomain(CSPTest.X).size());
        Assert.assertEquals("red", csp.getDomain(CSPTest.X).get(0));
        cspCopy.setDomain(CSPTest.X, animals);
        Assert.assertEquals(2, cspCopy.getDomain(CSPTest.X).size());
        Assert.assertEquals("cat", cspCopy.getDomain(CSPTest.X).get(0));
        Assert.assertEquals(3, csp.getDomain(CSPTest.X).size());
        Assert.assertEquals("red", csp.getDomain(CSPTest.X).get(0));
    }
}


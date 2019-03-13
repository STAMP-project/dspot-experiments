package aima.test.core.unit.logic.fol;


import aima.core.logic.fol.VariableCollector;
import aima.core.logic.fol.parsing.FOLParser;
import aima.core.logic.fol.parsing.ast.Variable;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class VariableCollectorTest {
    FOLParser parser;

    VariableCollector vc;

    @Test
    public void testSimplepredicate() {
        Set<Variable> variables = vc.collectAllVariables(parser.parse("King(x)"));
        Assert.assertEquals(1, variables.size());
        Assert.assertTrue(variables.contains(new Variable("x")));
    }

    @Test
    public void testMultipleVariables() {
        Set<Variable> variables = vc.collectAllVariables(parser.parse("BrotherOf(x) = EnemyOf(y)"));
        Assert.assertEquals(2, variables.size());
        Assert.assertTrue(variables.contains(new Variable("x")));
        Assert.assertTrue(variables.contains(new Variable("y")));
    }

    @Test
    public void testQuantifiedVariables() {
        // Note: Should collect quantified variables
        // even if not mentioned in clause.
        Set<Variable> variables = vc.collectAllVariables(parser.parse("FORALL x,y,z (BrotherOf(x) = EnemyOf(y))"));
        Assert.assertEquals(3, variables.size());
        Assert.assertTrue(variables.contains(new Variable("x")));
        Assert.assertTrue(variables.contains(new Variable("y")));
        Assert.assertTrue(variables.contains(new Variable("z")));
    }
}


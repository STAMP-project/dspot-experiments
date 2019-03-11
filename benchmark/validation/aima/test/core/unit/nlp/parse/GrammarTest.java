package aima.test.core.unit.nlp.parse;


import aima.core.nlp.parsing.grammars.ProbUnrestrictedGrammar;
import aima.core.nlp.parsing.grammars.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class GrammarTest {
    ProbUnrestrictedGrammar g;

    ProbUnrestrictedGrammar g2;

    @Test
    public void testValidRule() {
        Rule invalidR = new Rule(null, new ArrayList<String>(Arrays.asList("W", "Z")), ((float) (0.5)));
        Rule validR = new Rule(new ArrayList<String>(Arrays.asList("W")), new ArrayList<String>(Arrays.asList("a", "s")), ((float) (0.5)));
        Rule validR2 = new Rule(new ArrayList<String>(Arrays.asList("W")), null, ((float) (0.5)));
        Assert.assertFalse(g.validRule(invalidR));
        Assert.assertTrue(g.validRule(validR));
        Assert.assertTrue(g.validRule(validR2));
    }

    /**
     * Grammar should not allow a rule of the form
     * null -> X, where X is a combo of variables and terminals
     */
    @Test
    public void testRejectNullLhs() {
        Rule r = new Rule(new ArrayList<String>(), new ArrayList<String>(), ((float) (0.5)));// test completely null rule

        // test only null lhs
        Rule r2 = new Rule(null, new ArrayList<String>(Arrays.asList("W", "Z")), ((float) (0.5)));
        Assert.assertFalse(g.addRule(r));
        Assert.assertFalse(g.addRule(r2));
    }

    /**
     * Grammar (unrestricted) should accept all the rules in the test,
     * as they have non-null left hand sides
     */
    @Test
    public void testAcceptValidRules() {
        Rule unrestrictedRule = new Rule(new ArrayList<String>(Arrays.asList("A", "a", "A", "B")), new ArrayList<String>(Arrays.asList("b", "b", "A", "C")), ((float) (0.5)));
        Rule contextSensRule = new Rule(new ArrayList<String>(Arrays.asList("A", "a", "A")), new ArrayList<String>(Arrays.asList("b", "b", "A", "C")), ((float) (0.5)));
        Rule contextFreeRule = new Rule(new ArrayList<String>(Arrays.asList("A")), new ArrayList<String>(Arrays.asList("b", "b", "A", "C")), ((float) (0.5)));
        Rule regularRule = new Rule(new ArrayList<String>(Arrays.asList("A")), new ArrayList<String>(Arrays.asList("b", "C")), ((float) (0.5)));
        Rule nullRHSRule = new Rule(new ArrayList<String>(Arrays.asList("A", "B")), null, ((float) (0.5)));
        // try adding these rules in turn
        Assert.assertTrue(g.addRule(unrestrictedRule));
        Assert.assertTrue(g.addRule(contextSensRule));
        Assert.assertTrue(g.addRule(contextFreeRule));
        Assert.assertTrue(g.addRule(regularRule));
        Assert.assertTrue(g.addRule(nullRHSRule));
    }

    /**
     * Test that Grammar class correctly updates its
     * list of variables and terminals when a new rule is added
     */
    @Test
    public void testUpdateVarsAndTerminals() {
        // add a rule that has variables and terminals not
        // already in the grammar
        g.addRule(new Rule(new ArrayList<String>(Arrays.asList("Z")), new ArrayList<String>(Arrays.asList("z", "Z")), ((float) (0.5))));
        Assert.assertTrue(((g.terminals.contains("z")) && (!(g.terminals.contains("Z")))));
        Assert.assertTrue(((g.vars.contains("Z")) && (!(g.vars.contains("z")))));
    }

    @Test
    public void testIsVariable() {
        Assert.assertTrue(ProbUnrestrictedGrammar.isVariable("S"));
        Assert.assertTrue(ProbUnrestrictedGrammar.isVariable("SSSSS"));
        Assert.assertFalse(ProbUnrestrictedGrammar.isVariable("s"));
        Assert.assertFalse(ProbUnrestrictedGrammar.isVariable("tttt"));
    }

    @Test
    public void testIsTerminal() {
        Assert.assertTrue(ProbUnrestrictedGrammar.isTerminal("x"));
        Assert.assertTrue(ProbUnrestrictedGrammar.isTerminal("xxxxx"));
        Assert.assertFalse(ProbUnrestrictedGrammar.isTerminal("X"));
        Assert.assertFalse(ProbUnrestrictedGrammar.isTerminal("XXXXXX"));
    }
}


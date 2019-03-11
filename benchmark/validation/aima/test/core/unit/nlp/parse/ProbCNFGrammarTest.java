package aima.test.core.unit.nlp.parse;


import aima.core.nlp.parsing.grammars.ProbCNFGrammar;
import aima.core.nlp.parsing.grammars.Rule;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the class representing the Chomsky Normal Form grammar
 *
 * @author Jonathon
 */
public class ProbCNFGrammarTest {
    ProbCNFGrammar gEmpty;

    Rule validR;

    Rule invalidR;

    @Test
    public void testAddValidRule() {
        Assert.assertTrue(gEmpty.addRule(validR));
    }

    @Test
    public void testAddInvalidRule() {
        Assert.assertFalse(gEmpty.addRule(invalidR));
    }

    @Test
    public void testValidRule() {
        Assert.assertTrue(gEmpty.validRule(validR));
        Assert.assertFalse(gEmpty.validRule(invalidR));
    }
}


package aima.test.core.unit.nlp.parse;


import aima.core.nlp.parsing.grammars.ProbUnrestrictedGrammar;
import aima.core.nlp.parsing.grammars.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class ProbContextFreeGrammarTest {
    ProbUnrestrictedGrammar g;

    ProbUnrestrictedGrammar cfG;

    @Test
    public void testValidRule() {
        // This rule is a valid Context-Free rule
        Rule validR = new Rule(new ArrayList<String>(Arrays.asList("W")), new ArrayList<String>(Arrays.asList("a", "s")), ((float) (0.5)));
        // This rule is of correct form but not a context-free rule
        Rule invalidR = new Rule(new ArrayList<String>(Arrays.asList("W", "A")), null, ((float) (0.5)));
        Assert.assertFalse(cfG.validRule(invalidR));
        Assert.assertTrue(cfG.validRule(validR));
    }
}


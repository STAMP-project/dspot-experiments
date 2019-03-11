package aima.test.core.unit.logic.propositional.kb.data;


import aima.core.logic.propositional.kb.data.Model;
import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import aima.core.logic.propositional.parsing.ast.Sentence;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class ModelTest {
    private Model m;

    private PLParser parser;

    Sentence trueSentence;

    Sentence falseSentence;

    Sentence andSentence;

    Sentence orSentence;

    Sentence impliedSentence;

    Sentence biConditionalSentence;

    @Test
    public void testEmptyModel() {
        Assert.assertEquals(null, m.getValue(new PropositionSymbol("P")));
        Assert.assertEquals(true, m.isUnknown(new PropositionSymbol("P")));
    }

    @Test
    public void testExtendModel() {
        String p = "P";
        m = m.union(new PropositionSymbol(p), true);
        Assert.assertEquals(Boolean.TRUE, m.getValue(new PropositionSymbol("P")));
    }

    @Test
    public void testTrueFalseEvaluation() {
        Assert.assertEquals(true, m.isTrue(trueSentence));
        Assert.assertEquals(false, m.isFalse(trueSentence));
        Assert.assertEquals(false, m.isTrue(falseSentence));
        Assert.assertEquals(true, m.isFalse(falseSentence));
    }

    @Test
    public void testSentenceStatusWhenPTrueAndQTrue() {
        String p = "P";
        String q = "Q";
        m = m.union(new PropositionSymbol(p), true);
        m = m.union(new PropositionSymbol(q), true);
        Assert.assertEquals(true, m.isTrue(andSentence));
        Assert.assertEquals(true, m.isTrue(orSentence));
        Assert.assertEquals(true, m.isTrue(impliedSentence));
        Assert.assertEquals(true, m.isTrue(biConditionalSentence));
    }

    @Test
    public void testSentenceStatusWhenPFalseAndQFalse() {
        String p = "P";
        String q = "Q";
        m = m.union(new PropositionSymbol(p), false);
        m = m.union(new PropositionSymbol(q), false);
        Assert.assertEquals(true, m.isFalse(andSentence));
        Assert.assertEquals(true, m.isFalse(orSentence));
        Assert.assertEquals(true, m.isTrue(impliedSentence));
        Assert.assertEquals(true, m.isTrue(biConditionalSentence));
    }

    @Test
    public void testSentenceStatusWhenPTrueAndQFalse() {
        String p = "P";
        String q = "Q";
        m = m.union(new PropositionSymbol(p), true);
        m = m.union(new PropositionSymbol(q), false);
        Assert.assertEquals(true, m.isFalse(andSentence));
        Assert.assertEquals(true, m.isTrue(orSentence));
        Assert.assertEquals(true, m.isFalse(impliedSentence));
        Assert.assertEquals(true, m.isFalse(biConditionalSentence));
    }

    @Test
    public void testSentenceStatusWhenPFalseAndQTrue() {
        String p = "P";
        String q = "Q";
        m = m.union(new PropositionSymbol(p), false);
        m = m.union(new PropositionSymbol(q), true);
        Assert.assertEquals(true, m.isFalse(andSentence));
        Assert.assertEquals(true, m.isTrue(orSentence));
        Assert.assertEquals(true, m.isTrue(impliedSentence));
        Assert.assertEquals(true, m.isFalse(biConditionalSentence));
    }

    @Test
    public void testComplexSentence() {
        String p = "P";
        String q = "Q";
        m = m.union(new PropositionSymbol(p), true);
        m = m.union(new PropositionSymbol(q), false);
        Sentence sent = ((Sentence) (parser.parse("((P | Q) &  (P => Q))")));
        Assert.assertFalse(m.isTrue(sent));
        Assert.assertTrue(m.isFalse(sent));
        Sentence sent2 = ((Sentence) (parser.parse("((P | Q) & (Q))")));
        Assert.assertFalse(m.isTrue(sent2));
        Assert.assertTrue(m.isFalse(sent2));
    }
}


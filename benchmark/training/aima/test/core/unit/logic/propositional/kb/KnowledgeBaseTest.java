package aima.test.core.unit.logic.propositional.kb;


import aima.core.logic.propositional.kb.KnowledgeBase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class KnowledgeBaseTest {
    private KnowledgeBase kb;

    @Test
    public void testTellInsertsSentence() {
        kb.tell("(A & B)");
        Assert.assertEquals(1, kb.size());
    }

    @Test
    public void testTellDoesNotInsertSameSentenceTwice() {
        kb.tell("(A & B)");
        Assert.assertEquals(1, kb.size());
        kb.tell("(A & B)");
        Assert.assertEquals(1, kb.size());
    }

    @Test
    public void testEmptyKnowledgeBaseIsAnEmptyString() {
        Assert.assertEquals("", kb.toString());
    }

    @Test
    public void testKnowledgeBaseWithOneSentenceToString() {
        kb.tell("(A & B)");
        Assert.assertEquals("A & B", kb.toString());
    }

    @Test
    public void testKnowledgeBaseWithTwoSentencesToString() {
        kb.tell("(A & B)");
        kb.tell("(C & D)");
        Assert.assertEquals("A & B & C & D", kb.toString());
    }

    @Test
    public void testKnowledgeBaseWithThreeSentencesToString() {
        kb.tell("(A & B)");
        kb.tell("(C & D)");
        kb.tell("(E & F)");
        Assert.assertEquals("A & B & C & D & E & F", kb.toString());
    }
}


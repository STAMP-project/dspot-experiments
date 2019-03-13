package aima.test.core.unit.logic.propositional.parsing;


import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.PropositionSymbol;
import aima.core.logic.propositional.parsing.ast.Sentence;
import org.junit.Test;


public class ComplexSentenceTest {
    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_1() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(null, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_2() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.NOT, ((Sentence[]) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_3() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.NOT, new Sentence[]{  });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_4() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.NOT, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_5() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.AND, new Sentence[]{ new PropositionSymbol("A") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_6() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.AND, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B"), new PropositionSymbol("C") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_7() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.OR, new Sentence[]{ new PropositionSymbol("A") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_8() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.OR, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B"), new PropositionSymbol("C") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_9() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.IMPLICATION, new Sentence[]{ new PropositionSymbol("A") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_10() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.IMPLICATION, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B"), new PropositionSymbol("C") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_11() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.BICONDITIONAL, new Sentence[]{ new PropositionSymbol("A") });
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_IllegalArgumentOnConstruction_12() {
        new aima.core.logic.propositional.parsing.ast.ComplexSentence(Connective.BICONDITIONAL, new Sentence[]{ new PropositionSymbol("A"), new PropositionSymbol("B"), new PropositionSymbol("C") });
    }
}


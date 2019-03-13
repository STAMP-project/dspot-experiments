package aima.test.core.unit.logic.fol.parsing;


import aima.core.logic.common.LogicTokenTypes;
import aima.core.logic.fol.parsing.FOLLexer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class FOLLexerTest {
    FOLLexer lexer;

    @Test
    public void testLexBasicExpression() {
        lexer.setInput("( P )");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 0), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.CONSTANT, "P", 2), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 4), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EOI, "EOI", 5), lexer.nextToken());
    }

    @Test
    public void testConnectors() {
        lexer.setInput(" p  AND q");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "p", 1), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.CONNECTIVE, "AND", 4), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "q", 8), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EOI, "EOI", 9), lexer.nextToken());
    }

    @Test
    public void testFunctions() {
        lexer.setInput(" LeftLeg(q)");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.FUNCTION, "LeftLeg", 1), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 8), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "q", 9), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 10), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EOI, "EOI", 11), lexer.nextToken());
    }

    @Test
    public void testPredicate() {
        lexer.setInput(" HasColor(r)");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.PREDICATE, "HasColor", 1), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 9), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "r", 10), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 11), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EOI, "EOI", 12), lexer.nextToken());
    }

    @Test
    public void testMultiArgPredicate() {
        lexer.setInput(" King(x,y)");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.PREDICATE, "King", 1), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 5), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "x", 6), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.COMMA, ",", 7), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "y", 8), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 9), lexer.nextToken());
    }

    @Test
    public void testQuantifier() {
        lexer.setInput("FORALL x,y");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.QUANTIFIER, "FORALL", 0), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "x", 7), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.COMMA, ",", 8), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.VARIABLE, "y", 9), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EOI, "EOI", 10), lexer.nextToken());
    }

    @Test
    public void testTermEquality() {
        lexer.setInput("BrotherOf(John) = EnemyOf(Saladin)");
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.FUNCTION, "BrotherOf", 0), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 9), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.CONSTANT, "John", 10), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 14), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.EQUALS, "=", 16), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.FUNCTION, "EnemyOf", 18), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.LPAREN, "(", 25), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.CONSTANT, "Saladin", 26), lexer.nextToken());
        Assert.assertEquals(new aima.core.logic.common.Token(LogicTokenTypes.RPAREN, ")", 33), lexer.nextToken());
    }
}


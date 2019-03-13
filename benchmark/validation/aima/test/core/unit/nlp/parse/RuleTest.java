package aima.test.core.unit.nlp.parse;


import aima.core.nlp.parsing.grammars.Rule;
import org.junit.Assert;
import org.junit.Test;


public class RuleTest {
    Rule testR;

    @Test
    public void testStringSplitConstructor() {
        testR = new Rule("A,B", "a,bb,c", ((float) (0.5)));
        Assert.assertEquals(testR.lhs.size(), 2);
        Assert.assertEquals(testR.rhs.size(), 3);
        Assert.assertEquals(testR.lhs.get(1), "B");
        Assert.assertEquals(testR.rhs.get(2), "c");
    }

    @Test
    public void testStringSplitConstructorOnEmptyStrings() {
        testR = new Rule("", "", ((float) (0.5)));
        Assert.assertEquals(testR.lhs.size(), 0);
        Assert.assertEquals(testR.rhs.size(), 0);
    }

    @Test
    public void testStringSplitConstructorOnCommas() {
        testR = new Rule(",", ",", ((float) (0.5)));
        Assert.assertEquals(testR.lhs.size(), 0);
        Assert.assertEquals(testR.rhs.size(), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testStringSplitConstructorElementAccess() {
        testR = new Rule(",", "", ((float) (0.5)));
        testR.lhs.get(0);
    }
}


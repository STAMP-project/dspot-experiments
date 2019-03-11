package de.westnordost.streetcomplete.data.osm.tql;


import org.junit.Assert;
import org.junit.Test;


public class BooleanExpressionTest {
    @Test
    public void expand00() {
        checkExpand("a", "a");
    }

    @Test
    public void expand01() {
        checkExpand("a*b*c", "a*b*c");
    }

    @Test
    public void expand02() {
        checkExpand("a+b+c", "a+b+c");
    }

    @Test
    public void expand03() {
        checkExpand("a+b*c+d", "a+b*c+d");
    }

    @Test
    public void expand04() {
        checkExpand("a*b+c*d", "a*b+c*d");
    }

    @Test
    public void expand10() {
        checkExpand("(a+b)*c", "a*c+b*c");
    }

    @Test
    public void expand11() {
        checkExpand("a*(b+c)", "a*b+a*c");
    }

    @Test
    public void expand12() {
        checkExpand("a*(b+c)*d", "a*b*d+a*c*d");
    }

    @Test
    public void expand20() {
        checkExpand("a*(b+c*d)", "a*b+a*c*d");
    }

    @Test
    public void expand21() {
        checkExpand("a+b*(c+d)", "a+b*c+b*d");
    }

    @Test
    public void expand22() {
        checkExpand("(a+b)*c+d", "a*c+b*c+d");
    }

    @Test
    public void expand30() {
        checkExpand("((a+b)*c+d)*e", "a*c*e+b*c*e+d*e");
    }

    @Test
    public void expand31() {
        checkExpand("a*(b+c*(d+e))", "a*b+a*c*d+a*c*e");
    }

    @Test
    public void expand32() {
        checkExpand("z*(y+x*(a+b)*c+d)*e", "z*y*e+z*x*a*c*e+z*x*b*c*e+z*d*e");
    }

    @Test
    public void expand40() {
        checkExpand("(x+y)*z*(a+b)", "x*z*a+x*z*b+y*z*a+y*z*b");
    }

    @Test
    public void matchLeaf() {
        Assert.assertTrue(evalExpression("1"));
        Assert.assertFalse(evalExpression("0"));
    }

    @Test
    public void matchOr() {
        Assert.assertTrue(evalExpression("1+1"));
        Assert.assertTrue(evalExpression("1+0"));
        Assert.assertTrue(evalExpression("0+1"));
        Assert.assertFalse(evalExpression("0+0"));
        Assert.assertTrue(evalExpression("0+0+1"));
    }

    @Test
    public void matchAnd() {
        Assert.assertTrue(evalExpression("1*1"));
        Assert.assertFalse(evalExpression("1*0"));
        Assert.assertFalse(evalExpression("0*1"));
        Assert.assertFalse(evalExpression("0*0"));
        Assert.assertTrue(evalExpression("1*1*1"));
        Assert.assertFalse(evalExpression("1*1*0"));
    }

    @Test
    public void matchAndInOr() {
        Assert.assertTrue(evalExpression("(1*0)+1"));
        Assert.assertFalse(evalExpression("(1*0)+0"));
        Assert.assertTrue(evalExpression("(1*1)+0"));
        Assert.assertTrue(evalExpression("(1*1)+1"));
    }

    @Test
    public void matchOrInAnd() {
        Assert.assertTrue(evalExpression("(1+0)*1"));
        Assert.assertFalse(evalExpression("(1+0)*0"));
        Assert.assertFalse(evalExpression("(0+0)*0"));
        Assert.assertFalse(evalExpression("(0+0)*1"));
    }

    @Test
    public void typeNotInitiallySet() {
        BooleanExpression x = new BooleanExpression();
        Assert.assertFalse(x.isAnd());
        Assert.assertFalse(x.isOr());
        Assert.assertFalse(x.isRoot());
        Assert.assertFalse(x.isValue());
    }

    @Test
    public void addAnd() {
        BooleanExpression x = new BooleanExpression();
        Assert.assertTrue(x.addAnd().isAnd());
    }

    @Test
    public void addOr() {
        BooleanExpression x = new BooleanExpression();
        Assert.assertTrue(x.addOr().isOr());
    }

    @Test
    public void setAsRoot() {
        BooleanExpression x = new BooleanExpression(true);
        Assert.assertTrue(x.isRoot());
    }

    @Test
    public void setAsValue() {
        BooleanExpression<BooleanExpressionValue> x = new BooleanExpression();
        x.addValue(new TestBooleanExpressionValue("jo"));
        Assert.assertTrue(x.getFirstChild().isValue());
        Assert.assertEquals("jo", ((TestBooleanExpressionValue) (x.getFirstChild().getValue())).getValue());
    }

    @Test
    public void getParent() {
        BooleanExpression parent = new BooleanExpression();
        Assert.assertNull(parent.getParent());
        Assert.assertEquals(parent, parent.addOpenBracket().getParent());
    }

    @Test
    public void copyStringEquals() {
        BooleanExpression tree = TestBooleanExpressionParser.parse("(a+b)*c");
        BooleanExpression treeCopy = tree.copy();
        Assert.assertEquals(treeCopy.toString(), tree.toString());
    }

    @Test
    public void copyIsDeep() {
        BooleanExpression<BooleanExpressionValue> tree = TestBooleanExpressionParser.parse("(a+b)*c");
        BooleanExpression<BooleanExpressionValue> treeCopy = tree.copy();
        checkRecursiveEqualsButNotSame(tree, treeCopy);
    }
}


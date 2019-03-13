package jadx.tests.functional;


import IfOp.NE;
import LiteralArg.FALSE;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.regions.conditions.Compare;
import jadx.core.dex.regions.conditions.IfCondition;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestIfCondition {
    @Test
    public void testNormalize() {
        // 'a != false' => 'a == true'
        InsnArg a = TestIfCondition.mockArg();
        IfCondition c = TestIfCondition.makeCondition(NE, a, FALSE);
        IfCondition simp = simplify(c);
        Assert.assertThat(simp.getMode(), Matchers.is(COMPARE));
        Compare compare = simp.getCompare();
        Assert.assertThat(compare.getA(), Matchers.is(a));
        Assert.assertThat(compare.getB(), Matchers.is(TRUE));
    }

    @Test
    public void testMerge() {
        IfCondition a = TestIfCondition.makeSimpleCondition();
        IfCondition b = TestIfCondition.makeSimpleCondition();
        IfCondition c = merge(Mode.OR, a, b);
        Assert.assertThat(c.getMode(), Matchers.is(OR));
        Assert.assertThat(c.first(), Matchers.is(a));
        Assert.assertThat(c.second(), Matchers.is(b));
    }

    @Test
    public void testSimplifyNot() {
        // !(!a) => a
        IfCondition a = not(not(TestIfCondition.makeSimpleCondition()));
        Assert.assertThat(simplify(a), Matchers.is(a));
    }

    @Test
    public void testSimplifyNot2() {
        // !(!a) => a
        IfCondition a = not(TestIfCondition.makeNegCondition());
        Assert.assertThat(simplify(a), Matchers.is(a));
    }

    @Test
    public void testSimplify() {
        // '!(!a || !b)' => 'a && b'
        IfCondition a = TestIfCondition.makeSimpleCondition();
        IfCondition b = TestIfCondition.makeSimpleCondition();
        IfCondition c = not(merge(Mode.OR, not(a), not(b)));
        IfCondition simp = simplify(c);
        Assert.assertThat(simp.getMode(), Matchers.is(AND));
        Assert.assertThat(simp.first(), Matchers.is(a));
        Assert.assertThat(simp.second(), Matchers.is(b));
    }

    @Test
    public void testSimplify2() {
        // '(!a || !b) && !c' => '!((a && b) || c)'
        IfCondition a = TestIfCondition.makeSimpleCondition();
        IfCondition b = TestIfCondition.makeSimpleCondition();
        IfCondition c = TestIfCondition.makeSimpleCondition();
        IfCondition cond = merge(Mode.AND, merge(Mode.OR, not(a), not(b)), not(c));
        IfCondition simp = simplify(cond);
        Assert.assertThat(simp.getMode(), Matchers.is(NOT));
        IfCondition f = simp.first();
        Assert.assertThat(f.getMode(), Matchers.is(OR));
        Assert.assertThat(f.first().getMode(), Matchers.is(AND));
        Assert.assertThat(f.first().first(), Matchers.is(a));
        Assert.assertThat(f.first().second(), Matchers.is(b));
        Assert.assertThat(f.second(), Matchers.is(c));
    }
}


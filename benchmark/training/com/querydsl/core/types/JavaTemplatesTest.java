package com.querydsl.core.types;


import JavaTemplates.DEFAULT;
import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.INSTANCE_OF;
import Ops.LOE;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import org.junit.Assert;
import org.junit.Test;

import static JavaTemplates.DEFAULT;


public class JavaTemplatesTest {
    private Templates templates = DEFAULT;

    @Test
    public void precedence() {
        // postfix    expr++ expr--
        // unary    ++expr --expr +expr -expr ~ !
        // multiplicative    * / %
        // additive    + -
        // shift    << >> >>>
        // relational    < > <= >= instanceof
        // equality    == !=
        // bitwise AND    &
        // bitwise exclusive OR    ^
        // bitwise inclusive OR    |
        // logical AND    &&
        // logical OR    ||
        // ternary    ? :
        // assignment    = += -= *= /= %= &= ^= |= <<= >>= >>>=
        int p1 = getPrecedence(NOT);
        int p2 = getPrecedence(MULT, DIV, MOD);
        int p3 = getPrecedence(ADD, SUB);
        int p4 = getPrecedence(LT, GT, GOE, LOE, BETWEEN, INSTANCE_OF);
        int p5 = getPrecedence(EQ, NE);
        int p6 = getPrecedence(AND);
        int p7 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
    }

    @Test
    public void generic_precedence() {
        TemplatesTestUtils.testPrecedence(DEFAULT);
    }
}


package com.querydsl.jdo;


import JDOQLTemplates.DEFAULT;
import Ops.ADD;
import Ops.AND;
import Ops.DIV;
import Ops.EQ;
import Ops.EQ_IGNORE_CASE;
import Ops.GOE;
import Ops.GT;
import Ops.INSTANCE_OF;
import Ops.LOE;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.TemplatesTestUtils;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Assert;
import org.junit.Test;

import static JDOQLTemplates.DEFAULT;


public class JDOQLTemplatesTest {
    @Test
    public void precedence() {
        // Cast
        // Unary ("~") ("!")
        int p1 = getPrecedence(NOT);
        // Unary ("+") ("-")
        int p2 = getPrecedence(NEGATE);
        // Multiplicative ("*") ("/") ("%")
        int p3 = getPrecedence(MULT, DIV, MOD);
        // Additive ("+") ("-")
        int p4 = getPrecedence(ADD, SUB);
        // Relational (">=") (">") ("<=") ("<") ("instanceof")
        int p5 = getPrecedence(GOE, GT, LOE, LT, INSTANCE_OF);
        // Equality ("==") ("!=")
        int p6 = getPrecedence(EQ, EQ_IGNORE_CASE, NE);
        // Boolean logical AND ("&")
        // Boolean logical OR ("|")
        // Conditional AND ("&&")
        int p7 = getPrecedence(AND);
        // Conditional OR ("||")
        int p8 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
    }

    @Test
    public void generic_precedence() {
        TemplatesTestUtils.testPrecedence(DEFAULT);
    }

    @Test
    public void concat() {
        StringPath a = Expressions.stringPath("a");
        StringPath b = Expressions.stringPath("b");
        StringPath c = Expressions.stringPath("c");
        Expression<?> expr = a.append(b).toLowerCase();
        String str = new JDOQLSerializer(DEFAULT, c).handle(expr).toString();
        Assert.assertEquals("(a + b).toLowerCase()", str);
    }
}


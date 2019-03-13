package com.querydsl.jpa;


import JPQLOps.MEMBER_OF;
import JPQLOps.NOT_MEMBER_OF;
import JPQLTemplates.DEFAULT;
import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LOE;
import Ops.LT;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.TemplatesTestUtils;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JPQLTemplatesTest {
    @Test
    public void escape() {
        List<Templates> templates = Arrays.<Templates>asList(new JPQLTemplates(), new HQLTemplates(), new EclipseLinkTemplates(), new OpenJPATemplates());
        for (Templates t : templates) {
            Assert.assertEquals("{0} like {1} escape '!'", t.getTemplate(LIKE).toString());
        }
    }

    @Test
    public void custom_escape() {
        List<Templates> templates = Arrays.<Templates>asList(new JPQLTemplates('X'), new HQLTemplates('X'), new EclipseLinkTemplates('X'), new OpenJPATemplates('X'));
        for (Templates t : templates) {
            Assert.assertEquals("{0} like {1} escape 'X'", t.getTemplate(LIKE).toString());
        }
    }

    @Test
    public void precedence() {
        // Navigation operator (.)
        // +, - unary *,
        int p1 = getPrecedence(NEGATE);
        // / multiplication and division
        int p2 = getPrecedence(MULT, DIV);
        // +, - addition and subtraction
        int p3 = getPrecedence(ADD, SUB);
        // Comparison operators : =, >, >=, <, <=, <> (not equal), [NOT] BETWEEN, [NOT] LIKE, [NOT] IN, IS [NOT] NULL, IS [NOT] EMPTY, [NOT] MEMBER [OF]
        int p4 = getPrecedence(EQ, GT, GOE, LT, LOE, NE, BETWEEN, LIKE, LIKE_ESCAPE, IN, IS_NULL, IS_NOT_NULL, MEMBER_OF, NOT_MEMBER_OF);
        // NOT
        int p5 = getPrecedence(NOT);
        // AND
        int p6 = getPrecedence(AND);
        // OR
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
        for (JPQLTemplates templates : ImmutableList.of(DEFAULT, HQLTemplates.DEFAULT, EclipseLinkTemplates.DEFAULT)) {
            TemplatesTestUtils.testPrecedence(templates);
        }
    }
}


package com.querydsl.sql;


import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.CONCAT;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LOE;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.NOT_IN;
import Ops.OR;
import Ops.SUB;
import org.junit.Assert;
import org.junit.Test;


public class TeradataTemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void limit() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5);
        Assert.assertEquals(("from SURVEY survey1 " + "qualify row_number() over (order by 1) <= ?"), query.toString());
    }

    @Test
    public void offset() {
        query.from(AbstractSQLTemplatesTest.survey1).offset(5);
        Assert.assertEquals(("from SURVEY survey1 " + "qualify row_number() over (order by 1) > ?"), query.toString());
    }

    @Test
    public void limit_offset() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(10);
        Assert.assertEquals(("from SURVEY survey1 " + "qualify row_number() over (order by 1) between ? and ?"), query.toString());
    }

    @Test
    public void orderBy_limit() {
        query.from(AbstractSQLTemplatesTest.survey1).orderBy(TeradataTemplatesTest.survey1.name.asc()).limit(5);
        Assert.assertEquals(("from SURVEY survey1 " + ("order by survey1.NAME asc " + "qualify row_number() over (order by survey1.NAME asc) <= ?")), query.toString());
    }

    @Test
    public void precedence() {
        // +, - (unary)
        int p1 = getPrecedence(NEGATE);
        // ** (exponentation)
        // * / MOD
        int p2 = getPrecedence(MULT, DIV, MOD);
        // +, - (binary)
        int p3 = getPrecedence(ADD, SUB);
        // concat
        int p4 = getPrecedence(CONCAT);
        // EQ, NE, GT, LE, LT, GE, IN, NOT IN, BEWEEN, LIKE
        int p5 = getPrecedence(EQ, NE, GT, LT, GOE, LOE, IN, NOT_IN, BETWEEN, LIKE, LIKE_ESCAPE);
        // NOT
        int p6 = getPrecedence(NOT);
        // AND
        int p7 = getPrecedence(AND);
        // OR
        int p8 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
    }
}


/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;


import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.CONCAT;
import Ops.DIV;
import Ops.EQ;
import Ops.EXISTS;
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
import Ops.NOT_IN;
import Ops.OR;
import Ops.SUB;
import org.junit.Assert;
import org.junit.Test;


public class DB2TemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void precedence() {
        // Expressions within parentheses are evaluated first. When the order of evaluation is not
        // specified by parentheses, prefix operators are applied before multiplication and division,
        // and multiplication, division, and concatenation are applied before addition and subtraction.
        // Operators at the same precedence level are applied from left to right.
        int p1 = getPrecedence(NEGATE);
        int p2 = getPrecedence(MULT, DIV, CONCAT);
        int p3 = getPrecedence(ADD, SUB);
        int p4 = getPrecedence(EQ, NE, LT, GT, LOE, GOE);
        int p5 = getPrecedence(IS_NULL, IS_NOT_NULL, LIKE, LIKE_ESCAPE, BETWEEN, IN, NOT_IN, EXISTS);
        int p6 = getPrecedence(NOT);
        int p7 = getPrecedence(AND);
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


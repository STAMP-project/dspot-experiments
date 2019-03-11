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
import Ops.CONCAT;
import Ops.DIV;
import Ops.EQ;
import Ops.GT;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import org.junit.Assert;
import org.junit.Test;


public class H2TemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void builder() {
        SQLTemplates templates = H2Templates.builder().quote().newLineToSingleSpace().build();
        Assert.assertNotNull(templates);
    }

    @Test
    public void precedence() {
        // unary
        // *, /, %
        // +, -
        // ||
        // comparison
        // NOT
        // AND
        // OR
        int p1 = getPrecedence(NEGATE);
        int p2 = getPrecedence(MULT, DIV, MOD);
        int p3 = getPrecedence(ADD, SUB);
        int p4 = getPrecedence(CONCAT);
        int p5 = getPrecedence(EQ, NE, LT, GT);// ...

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


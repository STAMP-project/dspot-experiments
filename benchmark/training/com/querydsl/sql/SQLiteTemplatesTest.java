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
import Ops.DIV;
import Ops.EQ;
import Ops.EQ_IGNORE_CASE;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LOE;
import Ops.LT;
import Ops.MATCHES;
import Ops.MOD;
import Ops.MULT;
import Ops.OR;
import Ops.SUB;
import org.junit.Assert;
import org.junit.Test;


public class SQLiteTemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void precedence() {
        // ||
        // *    /    %
        int p1 = getPrecedence(MULT, DIV, MOD);
        // +    -
        int p2 = getPrecedence(ADD, SUB);
        // <<   >>   &    |
        // <    <=   >    >=
        int p3 = getPrecedence(LT, GT, LOE, GOE);
        // =    ==   !=   <>   IS   IS NOT   IN   LIKE   GLOB   MATCH   REGEXP
        int p4 = getPrecedence(EQ, EQ_IGNORE_CASE, IS_NULL, IS_NOT_NULL, IN, LIKE, LIKE_ESCAPE, MATCHES);
        // AND
        int p5 = getPrecedence(AND);
        // OR
        int p6 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
    }
}


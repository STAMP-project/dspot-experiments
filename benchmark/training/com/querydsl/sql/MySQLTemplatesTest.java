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
import Ops.CASE;
import Ops.CASE_ELSE;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LT;
import Ops.MATCHES;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import Ops.XNOR;
import Ops.XOR;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class MySQLTemplatesTest extends AbstractSQLTemplatesTest {
    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        SQLTemplates templates = MySQLTemplates.builder().printSchema().build();
        Configuration conf = new Configuration(templates);
        System.out.println(from(AbstractSQLTemplatesTest.survey1).toString());
    }

    @Test
    public void order_nullsFirst() {
        query.from(AbstractSQLTemplatesTest.survey1).orderBy(MySQLTemplatesTest.survey1.name.asc().nullsFirst());
        Assert.assertEquals("from SURVEY survey1 order by (case when survey1.NAME is null then 0 else 1 end), survey1.NAME asc", query.toString());
    }

    @Test
    public void order_nullsLast() {
        query.from(AbstractSQLTemplatesTest.survey1).orderBy(MySQLTemplatesTest.survey1.name.asc().nullsLast());
        Assert.assertEquals("from SURVEY survey1 order by (case when survey1.NAME is null then 1 else 0 end), survey1.NAME asc", query.toString());
    }

    @Test
    public void precedence() {
        // INTERVAL
        // BINARY, COLLATE
        // !
        // - (unary minus), ~ (unary bit inversion)
        int p0 = getPrecedence(NEGATE);
        // ^
        // *, /, DIV, %, MOD
        int p1 = getPrecedence(MULT, DIV, MOD);
        // -, +
        int p2 = getPrecedence(SUB, ADD);
        // <<, >>
        // &
        // |
        // = (comparison), <=>, >=, >, <=, <, <>, !=, IS, LIKE, REGEXP, IN
        int p3 = getPrecedence(EQ, GOE, GT, LT, NE, IS_NULL, IS_NOT_NULL, MATCHES, IN, LIKE, LIKE_ESCAPE);
        // BETWEEN, CASE, WHEN, THEN, ELSE
        int p4 = getPrecedence(BETWEEN, CASE, CASE_ELSE);
        // NOT
        int p5 = getPrecedence(NOT);
        // &&, AND
        int p6 = getPrecedence(AND);
        // XOR
        int p7 = getPrecedence(XOR, XNOR);
        // ||, OR
        int p8 = getPrecedence(OR);
        // = (assignment), :=
        Assert.assertTrue((p0 < p1));
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
    }
}


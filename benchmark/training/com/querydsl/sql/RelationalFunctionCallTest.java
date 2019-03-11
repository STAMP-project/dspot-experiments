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


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class RelationalFunctionCallTest {
    private static class TokenizeFunction extends RelationalFunctionCall<String> {
        final PathBuilder<String> alias;

        final StringPath token;

        public TokenizeFunction(String alias, String... tokens) {
            super(String.class, "tokenize", RelationalFunctionCallTest.serializeCollection(tokens));
            this.alias = new PathBuilder<String>(String.class, alias);
            this.token = Expressions.stringPath(this.alias, "token");
        }
    }

    @Test
    public void validation() {
        QSurvey survey = QSurvey.survey;
        RelationalFunctionCallTest.TokenizeFunction func = new RelationalFunctionCallTest.TokenizeFunction("func", "a", "b");
        SQLQuery<?> sub = SQLExpressions.selectOne().from(func.as(func.alias)).where(survey.name.like(func.token));
        System.out.println(sub);
    }

    @Test
    public void noArgs() {
        RelationalFunctionCall<String> functionCall = SQLExpressions.relationalFunctionCall(String.class, "getElements");
        Assert.assertEquals("getElements()", functionCall.getTemplate().toString());
    }

    @Test
    public void twoArgs() {
        StringPath str = Expressions.stringPath("str");
        RelationalFunctionCall<String> functionCall = SQLExpressions.relationalFunctionCall(String.class, "getElements", "a", str);
        Assert.assertEquals("getElements({0}, {1})", functionCall.getTemplate().toString());
        Assert.assertEquals("a", functionCall.getArg(0));
        Assert.assertEquals(str, functionCall.getArg(1));
    }
}


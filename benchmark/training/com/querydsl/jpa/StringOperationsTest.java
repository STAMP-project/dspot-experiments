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
package com.querydsl.jpa;


import Ops.INDEX_OF;
import Ops.SUBSTR_2ARGS;
import QCat.cat;
import QCat.cat.name;
import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;


public class StringOperationsTest extends AbstractQueryTest {
    @Test
    public void stringConcatenations() {
        AbstractQueryTest.assertToString("concat(cat.name,kitten.name)", Constants.cat.name.concat(Constants.kitten.name));
    }

    @Test
    public void stringConversionOperations() {
        AbstractQueryTest.assertToString("str(cat.bodyWeight)", Constants.cat.bodyWeight.stringValue());
    }

    @Test
    public void stringOperationsInFunctionalWay() {
        AbstractQueryTest.assertToString("concat(cat.name,cust.name.firstName)", Constants.cat.name.concat(Constants.cust.name.firstName));
        AbstractQueryTest.assertToString("lower(cat.name)", Constants.cat.name.lower());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void indexOf() {
        Path path = cat.name;
        Expression startIndex = Expressions.constant(0);
        Expression endIndex = Expressions.numberOperation(Integer.class, INDEX_OF, path, Expressions.constant("x"));
        Expression substr = Expressions.stringOperation(SUBSTR_2ARGS, path, startIndex, endIndex);
        AbstractQueryTest.assertToString("substring(cat.name,1,locate(?1,cat.name)-1 - ?2)", substr);
    }

    @Test
    public void indexOf2() {
        StringPath str = cat.name;
        AbstractQueryTest.assertToString("substring(cat.name,1,locate(?1,cat.name)-1 - ?2)", str.substring(0, str.indexOf("x")));
    }

    @Test
    public void indexOf3() {
        AbstractQueryTest.assertToString("substring(cat.name,2,1)", name.substring(1, 2));
    }
}


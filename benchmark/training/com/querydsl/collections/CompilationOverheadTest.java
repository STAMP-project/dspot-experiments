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
package com.querydsl.collections;


import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static QCat.cat;


public class CompilationOverheadTest {
    private static final QCat cat = cat;

    @Test
    public void test() {
        List<BooleanExpression> conditions = Arrays.asList(CompilationOverheadTest.cat.mate.isNull(), CompilationOverheadTest.cat.mate.isNotNull(), CompilationOverheadTest.cat.mate.name.eq("Kitty"), CompilationOverheadTest.cat.mate.name.ne("Kitty"), CompilationOverheadTest.cat.mate.isNotNull().and(CompilationOverheadTest.cat.mate.name.eq("Kitty")), CompilationOverheadTest.cat.mate.isNotNull().and(CompilationOverheadTest.cat.mate.name.eq("Kitty")).and(CompilationOverheadTest.cat.kittens.isEmpty()));
        // 1st
        for (BooleanExpression condition : conditions) {
            query(condition);
        }
        // 2nd
        for (BooleanExpression condition : conditions) {
            query(condition);
        }
    }
}


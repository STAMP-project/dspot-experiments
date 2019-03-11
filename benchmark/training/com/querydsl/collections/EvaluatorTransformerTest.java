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


import com.mysema.codegen.Evaluator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

import static CollQueryTemplates.DEFAULT;
import static QCat.cat;


public class EvaluatorTransformerTest {
    private QueryMetadata metadata = new DefaultQueryMetadata();

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        DefaultEvaluatorFactory evaluatorFactory = new DefaultEvaluatorFactory(DEFAULT);
        QCat cat = cat;
        Evaluator projectionEvaluator = evaluatorFactory.create(metadata, Collections.singletonList(cat), cat.name);
        EvaluatorFunction transformer = new EvaluatorFunction(projectionEvaluator);
        Cat c = new Cat("Kitty");
        Assert.assertEquals("Kitty", transformer.apply(c));
    }
}


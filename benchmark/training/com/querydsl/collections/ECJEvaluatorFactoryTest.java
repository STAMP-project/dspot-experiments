package com.querydsl.collections;


import com.mysema.codegen.ECJEvaluatorFactory;
import org.junit.Test;

import static CollQueryTemplates.DEFAULT;


public class ECJEvaluatorFactoryTest extends AbstractQueryTest {
    @Test
    public void evaluator_factory() {
        DefaultEvaluatorFactory evaluatorFactory = new DefaultEvaluatorFactory(DEFAULT, new ECJEvaluatorFactory(getClass().getClassLoader()));
        QueryEngine queryEngine = new DefaultQueryEngine(evaluatorFactory);
        CollQuery<?> query = new CollQuery<Void>(queryEngine);
        query.from(cat, cats).select(cat.name).fetch();
    }
}


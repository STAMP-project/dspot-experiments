package com.querydsl.collections;


import com.querydsl.core.testutil.Benchmark;
import com.querydsl.core.testutil.Performance;
import com.querydsl.core.testutil.Runner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static QCat.cat;


@Ignore
@Category(Performance.class)
public class QueryPerformanceTest {
    private static final int size = 1000;

    private static List<Cat> cats = new ArrayList<Cat>(QueryPerformanceTest.size);

    @Test
    public void byId() throws Exception {
        // 15857
        Runner.run("by id", new Benchmark() {
            @Override
            public void run(int times) throws Exception {
                for (int i = 0; i < times; i++) {
                    QCat cat = cat;
                    CollQueryFactory.from(cat, QueryPerformanceTest.cats).where(cat.id.eq((i % (QueryPerformanceTest.size)))).select(cat).fetch();
                }
            }
        });
    }
}


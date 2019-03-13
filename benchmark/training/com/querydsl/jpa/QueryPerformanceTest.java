package com.querydsl.jpa;


import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.Performance;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.testutil.JPATestRunner;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(JPATestRunner.class)
@Ignore
@Category(Performance.class)
public class QueryPerformanceTest implements JPATest {
    private static final int iterations = 1000;

    private EntityManager entityManager;

    @Test
    public void byId_raw() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryPerformanceTest.iterations); i++) {
            Cat cat = ((Cat) (entityManager.createQuery("select cat from Cat cat where id = ?").setParameter(1, (i + 100)).getSingleResult()));
            Assert.assertNotNull(cat);
        }
        System.err.println(("by id - raw" + ((System.currentTimeMillis()) - start)));
    }

    @Test
    public void byId_qdsl() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryPerformanceTest.iterations); i++) {
            QCat cat = QCat.cat;
            Cat c = query().from(cat).where(cat.id.eq((i + 100))).select(cat).fetchOne();
            Assert.assertNotNull(c);
        }
        System.err.println(("by id - dsl" + ((System.currentTimeMillis()) - start)));
    }

    @Test
    public void byId_twoCols_raw() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryPerformanceTest.iterations); i++) {
            Object[] row = ((Object[]) (entityManager.createQuery("select cat.id, cat.name from Cat cat where id = ?").setParameter(1, (i + 100)).getSingleResult()));
            Assert.assertNotNull(row);
        }
        System.err.println(("by id - 2 cols - raw" + ((System.currentTimeMillis()) - start)));
    }

    @Test
    public void byId_twoCols_qdsl() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < (QueryPerformanceTest.iterations); i++) {
            QCat cat = QCat.cat;
            Tuple row = query().from(cat).where(cat.id.eq((i + 100))).select(cat.id, cat.name).fetchOne();
            Assert.assertNotNull(row);
        }
        System.err.println(("by id - 2 cols - dsl" + ((System.currentTimeMillis()) - start)));
    }
}


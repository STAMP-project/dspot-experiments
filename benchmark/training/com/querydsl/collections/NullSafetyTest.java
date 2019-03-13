package com.querydsl.collections;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static QCat.cat;


public class NullSafetyTest extends AbstractQueryTest {
    @Test
    public void filters() {
        QCat cat = cat;
        CollQuery<Cat> query = CollQueryFactory.from(cat, Arrays.asList(new Cat(), new Cat("Bob")));
        Assert.assertEquals(1L, query.where(cat.name.eq("Bob")).fetchCount());
    }

    @Test
    public void joins() {
        Cat kitten1 = new Cat();
        Cat kitten2 = new Cat("Bob");
        Cat cat1 = new Cat();
        cat1.setKittens(Arrays.asList(kitten1, kitten2));
        Cat cat2 = new Cat();
        QCat cat = cat;
        QCat kitten = new QCat("kitten");
        CollQuery<Cat> query = CollQueryFactory.from(cat, Arrays.asList(cat1, cat2)).innerJoin(cat.kittens, kitten);
        Assert.assertEquals(1, query.where(kitten.name.eq("Bob")).fetchCount());
    }
}


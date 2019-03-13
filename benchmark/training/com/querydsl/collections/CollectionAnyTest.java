package com.querydsl.collections;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static QCat.cat;


public class CollectionAnyTest extends AbstractQueryTest {
    @Test
    public void any_null() {
        Cat a = new Cat("a");
        a.setKittens(null);
        Assert.assertEquals(0, CollQueryFactory.from(cat, Arrays.asList(a)).where(cat.kittens.any().name.startsWith("a")).fetchCount());
    }

    @Test
    public void any_in_projection() {
        Cat a = new Cat("a");
        Cat aa = new Cat("aa");
        Cat ab = new Cat("ab");
        Cat ac = new Cat("ac");
        a.setKittens(Arrays.asList(aa, ab, ac));
        Cat b = new Cat("b");
        Cat ba = new Cat("ba");
        Cat bb = new Cat("bb");
        b.setKittens(Arrays.asList(ba, bb));
        QCat cat = cat;
        List<Cat> kittens = CollQueryFactory.from(cat, Arrays.asList(a, b)).select(cat.kittens.any()).fetch();
        Assert.assertEquals(Arrays.asList(aa, ab, ac, ba, bb), kittens);
    }

    @Test
    public void any_in_projection2() {
        Cat a = new Cat("a");
        Cat aa = new Cat("aa");
        Cat ab = new Cat("ab");
        Cat ac = new Cat("ac");
        a.setKittens(Arrays.asList(aa, ab, ac));
        Cat b = new Cat("b");
        Cat ba = new Cat("ba");
        Cat bb = new Cat("bb");
        b.setKittens(Arrays.asList(ba, bb));
        QCat cat = cat;
        List<String> kittens = CollQueryFactory.from(cat, Arrays.asList(a, b)).select(cat.kittens.any().name).fetch();
        Assert.assertEquals(Arrays.asList("aa", "ab", "ac", "ba", "bb"), kittens);
    }

    @Test
    public void any_in_where_and_projection() {
        Cat a = new Cat("a");
        Cat aa = new Cat("aa");
        Cat ab = new Cat("ab");
        Cat ac = new Cat("ac");
        a.setKittens(Arrays.asList(aa, ab, ac));
        Cat b = new Cat("b");
        Cat ba = new Cat("ba");
        Cat bb = new Cat("bb");
        b.setKittens(Arrays.asList(ba, bb));
        QCat cat = cat;
        List<Cat> kittens = CollQueryFactory.from(cat, Arrays.asList(a, b)).where(cat.kittens.any().name.startsWith("a")).select(cat.kittens.any()).fetch();
        Assert.assertEquals(Arrays.asList(aa, ab, ac), kittens);
    }

    @Test
    public void any_in_where_and_projection2() {
        Cat a = new Cat("a");
        Cat aa = new Cat("aa");
        Cat ab = new Cat("ab");
        Cat ac = new Cat("ac");
        a.setKittens(Arrays.asList(aa, ab, ac));
        Cat b = new Cat("b");
        Cat ba = new Cat("ba");
        Cat bb = new Cat("bb");
        b.setKittens(Arrays.asList(ba, bb));
        QCat cat = cat;
        List<String> kittens = CollQueryFactory.from(cat, Arrays.asList(a, b)).where(cat.kittens.any().name.startsWith("a")).select(cat.kittens.any().name).fetch();
        Assert.assertEquals(Arrays.asList("aa", "ab", "ac"), kittens);
    }
}


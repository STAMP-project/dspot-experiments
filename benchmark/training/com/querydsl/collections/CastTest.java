package com.querydsl.collections;


import QAnimal.animal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class CastTest extends AbstractQueryTest {
    @Test
    public void parents() {
        QCat cat = animal.as(QCat.class);
        Assert.assertEquals(animal, cat.getMetadata().getParent());
    }

    @Test
    public void cast() {
        Assert.assertEquals(Arrays.asList(c1, c2, c3, c4), query().from(animal, cats).where(animal.as(QCat.class).breed.eq(0)).select(animal).fetch());
    }

    @Test
    public void property_dereference() {
        Cat cat = new Cat();
        cat.setEyecolor(Color.TABBY);
        Assert.assertEquals(Color.TABBY, CollQueryFactory.from(animal, cat).where(animal.instanceOf(Cat.class)).select(animal.as(QCat.class).eyecolor).fetchFirst());
    }
}


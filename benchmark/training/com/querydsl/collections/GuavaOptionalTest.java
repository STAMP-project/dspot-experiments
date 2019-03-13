package com.querydsl.collections;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class GuavaOptionalTest {
    public static class Cat {
        private Optional<String> pedigree = Optional.absent();

        public Cat() {
        }

        public Cat(Optional<String> pedigree) {
            this.pedigree = pedigree;
        }

        public Optional<String> getPedigree() {
            return pedigree;
        }
    }

    @Test
    public void test() {
        Collection<GuavaOptionalTest.Cat> cats = Lists.newArrayList();
        cats.add(new GuavaOptionalTest.Cat(Optional.<String>absent()));
        cats.add(new GuavaOptionalTest.Cat(Optional.of("persian")));
        GuavaOptionalTest.Cat c = alias(GuavaOptionalTest.Cat.class);
        for (GuavaOptionalTest.Cat cat : CollQueryFactory.from(c, cats).where($(c.getPedigree()).eq(Optional.of("persian"))).select($(c)).fetch()) {
            Assert.assertTrue(cat.getPedigree().isPresent());
            Assert.assertEquals("persian", cat.getPedigree().get());
        }
    }
}


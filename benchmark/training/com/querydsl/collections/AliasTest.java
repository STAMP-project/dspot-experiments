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


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class AliasTest extends AbstractQueryTest {
    @Test
    public void aliasVariations1() {
        // 1st
        QCat cat = new QCat("cat");
        Assert.assertEquals(Arrays.asList("Kitty", "Bob", "Alex", "Francis"), CollQueryFactory.from(cat, cats).where(cat.kittens.size().gt(0)).select(cat.name).fetch());
        // 2nd
        Cat c = alias(Cat.class, "cat");
        Assert.assertEquals(Arrays.asList("Kitty", "Bob", "Alex", "Francis"), CollQueryFactory.from(c, cats).where($(c.getKittens()).size().gt(0)).select($(c.getName())).fetch());
    }

    @Test
    public void aliasVariations2() {
        // 1st
        QCat cat = new QCat("cat");
        Assert.assertEquals(Arrays.asList(), CollQueryFactory.from(cat, cats).where(cat.name.matches("fri.*")).select(cat.name).fetch());
        // 2nd
        Cat c = alias(Cat.class, "cat");
        Assert.assertEquals(Arrays.asList(), CollQueryFactory.from(c, cats).where($(c.getName()).matches("fri.*")).select($(c.getName())).fetch());
    }

    @Test
    public void alias3() {
        QCat cat = new QCat("cat");
        Cat other = new Cat();
        Cat c = alias(Cat.class, "cat");
        // 1
        CollQueryFactory.from(c, cats).where($(c.getBirthdate()).gt(new Date())).select($(c)).iterate();
        // 2
        try {
            CollQueryFactory.from(c, cats).where($(c.getMate().getName().toUpperCase()).eq("MOE"));
            Assert.fail("expected NPE");
        } catch (NullPointerException ne) {
            // expected
        }
        // 3
        Assert.assertEquals(cat.name, $(c.getName()));
        // 4
        CollQueryFactory.from(c, cats).where($(c.getKittens().get(0).getBodyWeight()).gt(12)).select($(c.getName())).iterate();
        // 5
        CollQueryFactory.from(c, cats).where($(c).eq(other)).select($(c)).iterate();
        // 6
        CollQueryFactory.from(c, cats).where($(c.getKittens()).contains(other)).select($(c)).iterate();
        // 7
        CollQueryFactory.from(c, cats).where($(c.getKittens().isEmpty())).select($(c)).iterate();
        // 8
        CollQueryFactory.from(c, cats).where($(c.getName()).startsWith("B")).select($(c)).iterate();
        // 9
        CollQueryFactory.from(c, cats).where($(c.getName()).upper().eq("MOE")).select($(c)).iterate();
        // 10
        Assert.assertNotNull($(c.getKittensByName()));
        Assert.assertNotNull($(c.getKittensByName().get("Kitty")));
        CollQueryFactory.from(c, cats).where($(c.getKittensByName().get("Kitty")).isNotNull()).select(cat).iterate();
        // 11
        // try {
        // from(cat, cats).where(cat.mate.alive).fetch(cat);
        // fail("expected RuntimeException");
        // } catch (RuntimeException e) {
        // System.out.println(e.getMessage());
        // assertEquals("null in cat.mate.alive", e.getMessage());
        // }
        // 12
        // TestQuery query = query().from(cat, c1, c2).from(cat, c1, c2);
        // assertEquals(1, query.getMetadata().getJoins().size());
    }

    @Test
    public void various1() {
        StringPath str = Expressions.stringPath("str");
        Assert.assertEquals(Arrays.asList("a", "ab"), CollQueryFactory.from(str, "a", "ab", "cd", "de").where(str.startsWith("a")).select(str).fetch());
    }

    @Test
    public void various2() {
        Assert.assertEquals(Arrays.asList(1, 2, 5, 3), CollQueryFactory.from(var(), 1, 2, "abc", 5, 3).where(var().ne("abc")).select(var()).fetch());
    }

    @Test
    public void various3() {
        NumberPath<Integer> num = Expressions.numberPath(Integer.class, "num");
        Assert.assertEquals(Arrays.asList(1, 2, 3), CollQueryFactory.from(num, 1, 2, 3, 4).where(num.lt(4)).select(num).fetch());
    }
}


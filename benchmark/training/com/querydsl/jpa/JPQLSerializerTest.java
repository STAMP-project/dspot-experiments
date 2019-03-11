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
package com.querydsl.jpa;


import JoinType.DEFAULT;
import JoinType.INNERJOIN;
import JoinType.JOIN;
import QDomesticCat.domesticCat;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.domain.JobFunction;
import com.querydsl.jpa.domain.Location;
import com.querydsl.jpa.domain.QEmployee;
import org.junit.Assert;
import org.junit.Test;

import static HQLTemplates.DEFAULT;
import static java.util.Arrays.asList;


public class JPQLSerializerTest {
    @Test
    public void and_or() {
        // A.a.id.eq(theId).and(B.b.on.eq(false).or(B.b.id.eq(otherId)));
        QCat cat = QCat.cat;
        Predicate pred = cat.id.eq(1).and(cat.name.eq("Kitty").or(cat.name.eq("Boris")));
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.handle(pred);
        Assert.assertEquals("cat.id = ?1 and (cat.name = ?2 or cat.name = ?3)", serializer.toString());
        Assert.assertEquals("cat.id = 1 && (cat.name = Kitty || cat.name = Boris)", pred.toString());
    }

    @Test
    public void case1() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
        Expression<?> expr = Expressions.cases().when(cat.toes.eq(2)).then(2).when(cat.toes.eq(3)).then(3).otherwise(4);
        serializer.handle(expr);
        Assert.assertEquals("case when (cat.toes = ?1) then ?1 when (cat.toes = ?2) then ?2 else ?3 end", serializer.toString());
    }

    @Test
    public void case1_hibernate() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        Expression<?> expr = Expressions.cases().when(cat.toes.eq(2)).then(2).when(cat.toes.eq(3)).then(3).otherwise(4);
        serializer.handle(expr);
        Assert.assertEquals("case when (cat.toes = ?1) then ?1 when (cat.toes = ?2) then ?2 else 4 end", serializer.toString());
    }

    @Test
    public void case2() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(JPQLTemplates.DEFAULT);
        Expression<?> expr = Expressions.cases().when(cat.toes.eq(2)).then(cat.id.multiply(2)).when(cat.toes.eq(3)).then(cat.id.multiply(3)).otherwise(4);
        serializer.handle(expr);
        Assert.assertEquals("case when (cat.toes = ?1) then (cat.id * ?1) when (cat.toes = ?2) then (cat.id * ?2) else ?3 end", serializer.toString());
    }

    @Test
    public void case2_hibernate() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        Expression<?> expr = Expressions.cases().when(cat.toes.eq(2)).then(cat.id.multiply(2)).when(cat.toes.eq(3)).then(cat.id.multiply(3)).otherwise(4);
        serializer.handle(expr);
        Assert.assertEquals("case when (cat.toes = ?1) then (cat.id * ?1) when (cat.toes = ?2) then (cat.id * ?2) else 4 end", serializer.toString());
    }

    @Test
    public void count() {
        QCat cat = QCat.cat;
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.setProjection(cat.mate.countDistinct());
        JPQLSerializer serializer1 = new JPQLSerializer(DEFAULT);
        serializer1.serialize(md, true, null);
        Assert.assertEquals(("select count(count(distinct cat.mate))\n" + "from Cat cat"), serializer1.toString());
        JPQLSerializer serializer2 = new JPQLSerializer(DEFAULT);
        serializer2.serialize(md, false, null);
        Assert.assertEquals(("select count(distinct cat.mate)\n" + "from Cat cat"), serializer2.toString());
    }

    @Test
    public void fromWithCustomEntityName() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        EntityPath<Location> entityPath = new com.querydsl.core.types.dsl.EntityPathBase<Location>(Location.class, "entity");
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, entityPath);
        serializer.serialize(md, false, null);
        Assert.assertEquals("select entity\nfrom Location2 entity", serializer.toString());
    }

    @Test
    public void join_with() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.addJoin(INNERJOIN, cat.mate);
        md.addJoinCondition(cat.mate.alive);
        serializer.serialize(md, false, null);
        Assert.assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate with cat.mate.alive", serializer.toString());
    }

    @Test
    public void normalizeNumericArgs() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        NumberPath<Double> doublePath = Expressions.numberPath(Double.class, "doublePath");
        serializer.handle(doublePath.add(1));
        serializer.handle(doublePath.between(((float) (1.0)), 1L));
        serializer.handle(doublePath.lt(((byte) (1))));
        for (Object constant : serializer.getConstantToLabel().keySet()) {
            Assert.assertEquals(Double.class, constant.getClass());
        }
    }

    @Test
    public void delete_clause_uses_dELETE_fROM() {
        QEmployee employee = QEmployee.employee;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, employee);
        md.addWhere(employee.lastName.isNull());
        serializer.serializeForDelete(md);
        Assert.assertEquals("delete from Employee employee\nwhere employee.lastName is null", serializer.toString());
    }

    @Test
    public void delete_with_subQuery() {
        QCat parent = QCat.cat;
        QCat child = new QCat("kitten");
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, child);
        md.addWhere(child.id.eq(1).and(JPAExpressions.selectOne().from(parent).where(parent.id.eq(2), child.in(parent.kittens)).exists()));
        serializer.serializeForDelete(md);
        Assert.assertEquals(("delete from Cat kitten\n" + ("where kitten.id = ?1 and exists (select 1\n" + "from Cat cat\nwhere cat.id = ?2 and kitten member of cat.kittens)")), serializer.toString());
    }

    @Test
    public void in() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.handle(Expressions.numberPath(Integer.class, "id").in(asList(1, 2)));
        Assert.assertEquals("id in (?1)", serializer.toString());
    }

    @Test
    public void not_in() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.handle(Expressions.numberPath(Integer.class, "id").notIn(asList(1, 2)));
        Assert.assertEquals("id not in (?1)", serializer.toString());
    }

    @Test
    public void like() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.handle(Expressions.stringPath("str").contains("abc!"));
        Assert.assertEquals("str like ?1 escape '!'", serializer.toString());
        Assert.assertEquals("%abc!!%", serializer.getConstantToLabel().keySet().iterator().next().toString());
    }

    @Test
    public void stringContainsIc() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.handle(Expressions.stringPath("str").containsIgnoreCase("ABc!"));
        Assert.assertEquals("lower(str) like ?1 escape '!'", serializer.toString());
        Assert.assertEquals("%abc!!%", serializer.getConstantToLabel().keySet().iterator().next().toString());
    }

    @Test
    public void substring() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QCat cat = QCat.cat;
        serializer.handle(cat.name.substring(cat.name.length().subtract(1), 1));
        Assert.assertEquals("substring(cat.name,length(cat.name) + ?1,?2 - (length(cat.name) - ?2))", serializer.toString());
    }

    @Test
    public void nullsFirst() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.addOrderBy(cat.name.asc().nullsFirst());
        serializer.serialize(md, false, null);
        Assert.assertEquals(("select cat\n" + ("from Cat cat\n" + "order by cat.name asc nulls first")), serializer.toString());
    }

    @Test
    public void nullsLast() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.addOrderBy(cat.name.asc().nullsLast());
        serializer.serialize(md, false, null);
        Assert.assertEquals(("select cat\n" + ("from Cat cat\n" + "order by cat.name asc nulls last")), serializer.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void treat() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.addJoin(JOIN, cat.mate.as(((Path) (domesticCat))));
        md.setProjection(domesticCat);
        serializer.serialize(md, false, null);
        Assert.assertEquals(("select domesticCat\n" + ("from Cat cat\n" + "  inner join treat(cat.mate as DomesticCat) as domesticCat")), serializer.toString());
    }

    @Test
    public void openJPA_variables() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(OpenJPATemplates.DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(DEFAULT, cat);
        md.addJoin(INNERJOIN, cat.mate);
        md.addJoinCondition(cat.mate.alive);
        serializer.serialize(md, false, null);
        Assert.assertEquals("select cat_\nfrom Cat cat_\n  inner join cat_.mate on cat_.mate.alive", serializer.toString());
    }

    @Test
    public void visitLiteral_boolean() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.visitLiteral(Boolean.TRUE);
        Assert.assertEquals("true", serializer.toString());
    }

    @Test
    public void visitLiteral_number() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.visitLiteral(1.543);
        Assert.assertEquals("1.543", serializer.toString());
    }

    @Test
    public void visitLiteral_string() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.visitLiteral("abc''def");
        Assert.assertEquals("'abc''''def'", serializer.toString());
    }

    @Test
    public void visitLiteral_enum() {
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        serializer.visitLiteral(JobFunction.MANAGER);
        Assert.assertEquals("com.querydsl.jpa.domain.JobFunction.MANAGER", serializer.toString());
    }

    @Test
    public void substring_indexOf() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(DEFAULT);
        cat.name.substring(cat.name.indexOf("")).accept(serializer, null);
        Assert.assertEquals("substring(cat.name,locate(?1,cat.name)-1 + ?2)", serializer.toString());
    }
}


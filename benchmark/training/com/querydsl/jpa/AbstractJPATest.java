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


import QAnimal.animal;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.Target;
import com.querydsl.core.group.MockTuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.jpa.domain.Author;
import com.querydsl.jpa.domain.Book;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Company;
import com.querydsl.jpa.domain.DomesticCat;
import com.querydsl.jpa.domain.DoubleProjection;
import com.querydsl.jpa.domain.Entity1;
import com.querydsl.jpa.domain.Entity2;
import com.querydsl.jpa.domain.FloatProjection;
import com.querydsl.jpa.domain.Group;
import com.querydsl.jpa.domain.JobFunction;
import com.querydsl.jpa.domain4.QBookMark;
import com.querydsl.jpa.domain4.QBookVersion;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import static Expressions.FALSE;
import static Module.JPA;
import static QAnimal.animal;
import static QAuthor.author;
import static QBook.book;
import static QCat.cat;
import static QCompany.company;
import static QDomesticCat.domesticCat;
import static QEmployee.employee;
import static QEntity1.entity1;
import static QFoo.foo;
import static QMammal.mammal;
import static QNumeric.numeric;
import static QShow.show;
import static QSimpleTypes.simpleTypes;
import static QUser.user;
import static QWorld.world;
import static com.querydsl.jpa.domain.Company.Rating.A;
import static com.querydsl.jpa.domain.Company.Rating.AA;


/**
 *
 *
 * @author tiwe
 */
public abstract class AbstractJPATest {
    private static final Expression<?>[] NO_EXPRESSIONS = new Expression<?>[0];

    private static final QCompany company = company;

    private static final QAnimal animal = animal;

    private static final QCat cat = cat;

    private static final QCat otherCat = new QCat("otherCat");

    private static final BooleanExpression cond1 = AbstractJPATest.cat.name.length().gt(0);

    private static final BooleanExpression cond2 = AbstractJPATest.otherCat.name.length().gt(0);

    private static final Predicate condition = ExpressionUtils.and(((Predicate) (ExpressionUtils.extract(AbstractJPATest.cond1))), ((Predicate) (ExpressionUtils.extract(AbstractJPATest.cond2))));

    private static final Date birthDate;

    private static final java.sql.Date date;

    private static final Time time;

    private final List<Cat> savedCats = new ArrayList<Cat>();

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 2, 3, 4);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        birthDate = cal.getTime();
        date = new java.sql.Date(cal.getTimeInMillis());
        time = new Time(cal.getTimeInMillis());
    }

    @Test
    @ExcludeIn(ORACLE)
    public void add_bigDecimal() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        NumberPath<BigDecimal> bigd1 = entity.bigDecimal;
        NumberPath<BigDecimal> bigd2 = entity2.bigDecimal;
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(bigd1.add(bigd2).loe(new BigDecimal("1.00"))).select(entity).fetch());
    }

    @Test
    public void aggregates_list_max() {
        Assert.assertEquals(Integer.valueOf(6), query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id.max()).fetchFirst());
    }

    @Test
    public void aggregates_list_min() {
        Assert.assertEquals(Integer.valueOf(1), query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id.min()).fetchFirst());
    }

    @Test
    public void aggregates_uniqueResult_max() {
        Assert.assertEquals(Integer.valueOf(6), query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id.max()).fetchFirst());
    }

    @Test
    public void aggregates_uniqueResult_min() {
        Assert.assertEquals(Integer.valueOf(1), query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id.min()).fetchFirst());
    }

    @Test
    public void alias() {
        Assert.assertEquals(6, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id.as(AbstractJPATest.cat.id)).fetch().size());
    }

    @Test
    public void any_and_gt() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().name.eq("Ruth123"), AbstractJPATest.cat.kittens.any().bodyWeight.gt(10.0)).fetchCount());
    }

    @Test
    public void any_and_lt() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().name.eq("Ruth123"), AbstractJPATest.cat.kittens.any().bodyWeight.lt(10.0)).fetchCount());
    }

    @Test
    public void any_in_order() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.kittens.any().name.asc()).select(AbstractJPATest.cat).fetch().isEmpty());
    }

    @Test
    public void any_in_projection() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.kittens.any()).fetch().isEmpty());
    }

    @Test
    public void any_in_projection2() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.kittens.any().name).fetch().isEmpty());
    }

    @Test
    public void any_in_projection3() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.kittens.any().name, AbstractJPATest.cat.kittens.any().bodyWeight).fetch().isEmpty());
    }

    @Test
    public void any_in1() {
        // select cat from Cat cat where exists (
        // select cat_kittens from Cat cat_kittens where cat_kittens member of cat.kittens and cat_kittens in ?1)
        Assert.assertFalse(query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().in(savedCats)).select(AbstractJPATest.cat).fetch().isEmpty());
    }

    @Test
    public void any_in11() {
        List<Integer> ids = Lists.newArrayList();
        for (Cat cat : savedCats) {
            ids.add(cat.getId());
        }
        Assert.assertFalse(query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().id.in(ids)).select(AbstractJPATest.cat).fetch().isEmpty());
    }

    @Test
    public void any_in2() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().in(savedCats), AbstractJPATest.cat.kittens.any().in(savedCats.subList(0, 1)).not()).select(AbstractJPATest.cat).fetch().isEmpty());
    }

    @Test
    @NoBatooJPA
    public void any_in3() {
        QEmployee employee = employee;
        Assert.assertFalse(query().from(employee).where(employee.jobFunctions.any().in(JobFunction.CODER, JobFunction.CONSULTANT)).select(employee).fetch().isEmpty());
    }

    @Test
    public void any_simple() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().name.eq("Ruth123")).fetchCount());
    }

    @Test
    public void any_any() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().kittens.any().name.eq("Ruth123")).fetchCount());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void arrayProjection() {
        List<String[]> results = query().from(AbstractJPATest.cat).select(new ArrayConstructorExpression<String>(String[].class, AbstractJPATest.cat.name)).fetch();
        Assert.assertFalse(results.isEmpty());
        for (String[] result : results) {
            Assert.assertNotNull(result[0]);
        }
    }

    @Test
    public void as() {
        Assert.assertTrue(((query().from(QAnimal.animal.as(QCat.class)).fetchCount()) > 0));
    }

    @Test
    public void between() {
        Assert.assertEquals(ImmutableList.of(2, 3, 4, 5), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.id.between(2, 5)).orderBy(AbstractJPATest.cat.id.asc()).select(AbstractJPATest.cat.id).fetch());
    }

    @Test
    @NoBatooJPA
    public void case1() {
        Assert.assertEquals(ImmutableList.of(1, 2, 2, 2, 2, 2), query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.asc()).select(AbstractJPATest.cat.name.when("Bob123").then(1).otherwise(2)).fetch());
    }

    @Test
    @NoBatooJPA
    public void case1_long() {
        Assert.assertEquals(ImmutableList.of(1L, 2L, 2L, 2L, 2L, 2L), query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.asc()).select(AbstractJPATest.cat.name.when("Bob123").then(1L).otherwise(2L)).fetch());
        List<Integer> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(1).otherwise(2)).fetch();
        AbstractJPATest.assertInstancesOf(Integer.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoEclipseLink
    @NoHibernate
    public void case1_date() {
        List<LocalDate> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new LocalDate()).otherwise(new LocalDate().plusDays(1))).fetch();
        AbstractJPATest.assertInstancesOf(LocalDate.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoHibernate
    @NoEclipseLink({ MYSQL, POSTGRESQL })
    public void case1_date2() {
        List<java.sql.Date> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new java.sql.Date(0)).otherwise(new java.sql.Date(0))).fetch();
        AbstractJPATest.assertInstancesOf(java.sql.Date.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoEclipseLink
    @NoHibernate
    public void case1_time() {
        List<LocalTime> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new LocalTime()).otherwise(new LocalTime().plusHours(1))).fetch();
        AbstractJPATest.assertInstancesOf(LocalTime.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoHibernate
    @NoEclipseLink({ MYSQL, POSTGRESQL })
    public void case1_time2() {
        List<Time> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new Time(0)).otherwise(new Time(0))).fetch();
        AbstractJPATest.assertInstancesOf(Time.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoEclipseLink
    @NoHibernate
    public void case1_timestamp() {
        List<DateTime> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new DateTime()).otherwise(new DateTime().plusHours(1))).fetch();
        AbstractJPATest.assertInstancesOf(DateTime.class, rv);
    }

    // https://hibernate.atlassian.net/browse/HHH-8653
    @Test
    @NoHibernate
    @NoEclipseLink({ MYSQL, POSTGRESQL })
    public void case1_timestamp2() {
        List<Timestamp> rv = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.when("Bob").then(new Timestamp(0)).otherwise(new Timestamp(0))).fetch();
        AbstractJPATest.assertInstancesOf(Timestamp.class, rv);
    }

    @Test
    public void case2() {
        Assert.assertEquals(ImmutableList.of(4, 4, 4, 4, 4, 4), query().from(AbstractJPATest.cat).select(Expressions.cases().when(AbstractJPATest.cat.toes.eq(2)).then(AbstractJPATest.cat.id.multiply(2)).when(AbstractJPATest.cat.toes.eq(3)).then(AbstractJPATest.cat.id.multiply(3)).otherwise(4)).fetch());
    }

    @Test
    public void case3() {
        Assert.assertEquals(ImmutableList.of(4, 4, 4, 4, 4, 4), query().from(AbstractJPATest.cat).select(Expressions.cases().when(AbstractJPATest.cat.toes.in(2, 3)).then(AbstractJPATest.cat.id.multiply(AbstractJPATest.cat.toes)).otherwise(4)).fetch());
    }

    // doesn't work in Eclipselink
    @Test
    @ExcludeIn(MYSQL)
    public void case4() {
        NumberExpression<Float> numExpression = AbstractJPATest.cat.bodyWeight.floatValue().divide(AbstractJPATest.otherCat.bodyWeight.floatValue()).multiply(100);
        NumberExpression<Float> numExpression2 = AbstractJPATest.cat.id.when(0).then(0.0F).otherwise(numExpression);
        Assert.assertEquals(ImmutableList.of(200, 150, 133, 125, 120), query().from(AbstractJPATest.cat, AbstractJPATest.otherCat).where(AbstractJPATest.cat.id.eq(AbstractJPATest.otherCat.id.add(1))).orderBy(AbstractJPATest.cat.id.asc(), AbstractJPATest.otherCat.id.asc()).select(numExpression2.intValue()).fetch());
    }

    // EclipseLink uses a left join for cat.mate
    @Test
    @NoEclipseLink
    public void case5() {
        Assert.assertEquals(ImmutableList.of(0, 1, 1, 1), query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.asc()).select(AbstractJPATest.cat.mate.when(savedCats.get(0)).then(0).otherwise(1)).fetch());
    }

    @Test
    public void caseBuilder() {
        QCat cat2 = new QCat("cat2");
        NumberExpression<Integer> casex = new CaseBuilder().when(AbstractJPATest.cat.weight.isNull().and(AbstractJPATest.cat.weight.isNull())).then(0).when(AbstractJPATest.cat.weight.isNull()).then(cat2.weight).when(cat2.weight.isNull()).then(AbstractJPATest.cat.weight).otherwise(AbstractJPATest.cat.weight.add(cat2.weight));
        query().from(AbstractJPATest.cat, cat2).orderBy(casex.asc()).select(AbstractJPATest.cat.id, cat2.id).fetch();
        query().from(AbstractJPATest.cat, cat2).orderBy(casex.desc()).select(AbstractJPATest.cat.id, cat2.id).fetch();
    }

    @Test
    public void cast() {
        List<Cat> cats = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat).fetch();
        List<Integer> weights = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.castToNum(Integer.class)).fetch();
        for (int i = 0; i < (cats.size()); i++) {
            Assert.assertEquals(Integer.valueOf(((int) (cats.get(i).getBodyWeight()))), weights.get(i));
        }
    }

    @Test
    @ExcludeIn(SQLSERVER)
    public void cast_toString() {
        for (Tuple tuple : query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.breed, AbstractJPATest.cat.breed.stringValue()).fetch()) {
            Assert.assertEquals(tuple.get(AbstractJPATest.cat.breed).toString(), tuple.get(AbstractJPATest.cat.breed.stringValue()));
        }
    }

    @Test
    @ExcludeIn(SQLSERVER)
    public void cast_toString_append() {
        for (Tuple tuple : query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.breed, AbstractJPATest.cat.breed.stringValue().append("test")).fetch()) {
            Assert.assertEquals(((tuple.get(AbstractJPATest.cat.breed).toString()) + "test"), tuple.get(AbstractJPATest.cat.breed.stringValue().append("test")));
        }
    }

    @Test
    public void collection_predicates() {
        ListPath<Cat, QCat> path = AbstractJPATest.cat.kittens;
        List<Predicate> predicates = // path.eq(savedCats),
        // path.in(savedCats),
        // path.isNotNull(),
        // path.isNull(),
        // path.ne(savedCats),
        // path.notIn(savedCats)
        // path.when(other)
        Arrays.asList();
        for (Predicate pred : predicates) {
            System.err.println(pred);
            query().from(AbstractJPATest.cat).where(pred).select(AbstractJPATest.cat).fetch();
        }
    }

    @Test
    public void collection_projections() {
        ListPath<Cat, QCat> path = AbstractJPATest.cat.kittens;
        List<Expression<?>> projections = // path.fetchCount(),
        // path.countDistinct()
        Arrays.asList();
        for (Expression<?> proj : projections) {
            System.err.println(proj);
            query().from(AbstractJPATest.cat).select(proj).fetch();
        }
    }

    @Test
    public void constant() {
        // select cat.id, ?1 as const from Cat cat
        List<Cat> cats = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat).fetch();
        Path<String> path = Expressions.stringPath("const");
        List<Tuple> tuples = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id, Expressions.constantAs("abc", path)).fetch();
        for (int i = 0; i < (cats.size()); i++) {
            Assert.assertEquals(Integer.valueOf(cats.get(i).getId()), tuples.get(i).get(AbstractJPATest.cat.id));
            Assert.assertEquals("abc", tuples.get(i).get(path));
        }
    }

    @Test
    public void constant2() {
        Assert.assertFalse(query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.id, Expressions.constant("name")).fetch().isEmpty());
    }

    @Test
    public void constructorProjection() {
        List<Projection> projections = query().from(AbstractJPATest.cat).select(Projections.constructor(Projection.class, AbstractJPATest.cat.name, AbstractJPATest.cat)).fetch();
        Assert.assertFalse(projections.isEmpty());
        for (Projection projection : projections) {
            Assert.assertNotNull(projection);
        }
    }

    @Test
    public void constructorProjection2() {
        List<Projection> projections = query().from(AbstractJPATest.cat).select(new QProjection(AbstractJPATest.cat.name, AbstractJPATest.cat)).fetch();
        Assert.assertFalse(projections.isEmpty());
        for (Projection projection : projections) {
            Assert.assertNotNull(projection);
        }
    }

    @Test
    public void constructorProjection3() {
        List<Projection> projections = query().from(AbstractJPATest.cat).select(new QProjection(AbstractJPATest.cat.id, FALSE)).fetch();
        Assert.assertFalse(projections.isEmpty());
        for (Projection projection : projections) {
            Assert.assertNotNull(projection);
        }
    }

    @Test
    public void contains_ic() {
        QFoo foo = foo;
        Assert.assertEquals(1, query().from(foo).where(foo.bar.containsIgnoreCase("M?nchen")).fetchCount());
    }

    @Test
    public void contains1() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.contains("eli")).fetchCount());
    }

    @Test
    public void contains2() {
        Assert.assertEquals(1L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.contains(savedCats.get(0))).fetchCount());
    }

    @Test
    public void contains3() {
        Assert.assertEquals(1L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.contains("_")).fetchCount());
    }

    @Test
    public void contains4() {
        QEmployee employee = employee;
        Assert.assertEquals(Arrays.asList(), query().from(employee).where(employee.jobFunctions.contains(JobFunction.CODER), employee.jobFunctions.contains(JobFunction.CONSULTANT), employee.jobFunctions.size().eq(2)).select(employee).fetch());
    }

    @Test
    public void count() {
        QShow show = show;
        Assert.assertTrue(((query().from(show).fetchCount()) > 0));
    }

    @Test
    public void count_distinct() {
        QCat cat = cat;
        query().from(cat).groupBy(cat.id).select(cat.id, cat.breed.countDistinct()).fetch();
    }

    @Test
    @NoBatooJPA
    @NoHibernate
    public void count_distinct2() {
        QCat cat = cat;
        query().from(cat).groupBy(cat.id).select(cat.id, cat.birthdate.dayOfMonth().countDistinct()).fetch();
    }

    @Test
    @NoEclipseLink
    public void distinct_orderBy() {
        QCat cat = cat;
        List<Tuple> result = query().select(cat.id, cat.mate.id).distinct().from(cat).orderBy(cat.mate.id.asc().nullsFirst(), cat.id.asc().nullsFirst()).fetch();
        Assert.assertThat(result, Matchers.<Tuple>contains(new MockTuple(new Object[]{ 1, null }), new MockTuple(new Object[]{ 6, null }), new MockTuple(new Object[]{ 2, 1 }), new MockTuple(new Object[]{ 3, 2 }), new MockTuple(new Object[]{ 4, 3 }), new MockTuple(new Object[]{ 5, 4 })));
    }

    @Test
    @NoHibernate
    @ExcludeIn(MYSQL)
    public void distinct_orderBy2() {
        QCat cat = cat;
        List<Tuple> result = query().select(cat.id, cat.mate.id).distinct().from(cat).orderBy(cat.mate.id.asc().nullsFirst()).fetch();
        Assert.assertThat(result, Matchers.<Tuple>contains(new MockTuple(new Object[]{ 2, 1 }), new MockTuple(new Object[]{ 3, 2 }), new MockTuple(new Object[]{ 4, 3 }), new MockTuple(new Object[]{ 5, 4 })));
    }

    @Test
    @NoEclipseLink(HSQLDB)
    public void count_distinct3() {
        QCat kitten = new QCat("kitten");
        Assert.assertEquals(4, query().from(AbstractJPATest.cat).leftJoin(AbstractJPATest.cat.kittens, kitten).select(kitten.countDistinct()).fetchOne().intValue());
        Assert.assertEquals(6, query().from(AbstractJPATest.cat).leftJoin(AbstractJPATest.cat.kittens, kitten).select(kitten.countDistinct()).fetchCount());
    }

    @Test
    public void distinctResults() {
        System.out.println("-- fetch results");
        QueryResults<Date> res = query().from(AbstractJPATest.cat).limit(2).select(AbstractJPATest.cat.birthdate).fetchResults();
        Assert.assertEquals(2, res.getResults().size());
        Assert.assertEquals(6L, res.getTotal());
        System.out.println();
        System.out.println("-- fetch distinct results");
        res = query().from(AbstractJPATest.cat).limit(2).distinct().select(AbstractJPATest.cat.birthdate).fetchResults();
        Assert.assertEquals(1, res.getResults().size());
        Assert.assertEquals(1L, res.getTotal());
        System.out.println();
        System.out.println("-- fetch distinct");
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).distinct().select(AbstractJPATest.cat.birthdate).fetch().size());
    }

    @Test
    public void date() {
        Assert.assertEquals(2000, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.year()).fetchFirst().intValue());
        Assert.assertEquals(200002, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.yearMonth()).fetchFirst().intValue());
        Assert.assertEquals(2, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.month()).fetchFirst().intValue());
        Assert.assertEquals(2, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.dayOfMonth()).fetchFirst().intValue());
        Assert.assertEquals(3, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.hour()).fetchFirst().intValue());
        Assert.assertEquals(4, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.minute()).fetchFirst().intValue());
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.second()).fetchFirst().intValue());
    }

    @Test
    @NoEclipseLink({ DERBY, HSQLDB })
    @NoHibernate({ DERBY, POSTGRESQL, SQLSERVER })
    public void date_yearWeek() {
        int value = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.yearWeek()).fetchFirst();
        Assert.assertTrue(((value == 200006) || (value == 200005)));
    }

    @Test
    @NoEclipseLink({ DERBY, HSQLDB })
    @NoHibernate({ DERBY, POSTGRESQL, SQLSERVER })
    public void date_week() {
        int value = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.birthdate.week()).fetchFirst();
        Assert.assertTrue(((value == 6) || (value == 5)));
    }

    @Test
    @ExcludeIn(ORACLE)
    public void divide() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.ddouble.divide(entity2.ddouble).loe(2.0)).select(entity).fetch());
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.ddouble.divide(entity2.iint).loe(2.0)).select(entity).fetch());
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.iint.divide(entity2.ddouble).loe(2.0)).select(entity).fetch());
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.iint.divide(entity2.iint).loe(2)).select(entity).fetch());
    }

    @Test
    @ExcludeIn(ORACLE)
    public void divide_bigDecimal() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        NumberPath<BigDecimal> bigd1 = entity.bigDecimal;
        NumberPath<BigDecimal> bigd2 = entity2.bigDecimal;
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(bigd1.divide(bigd2).loe(new BigDecimal("1.00"))).select(entity).fetch());
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.ddouble.divide(bigd2).loe(new BigDecimal("1.00"))).select(entity).fetch());
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(bigd1.divide(entity.ddouble).loe(new BigDecimal("1.00"))).select(entity).fetch());
    }

    @Test
    public void endsWith() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.endsWith("h123")).fetchCount());
    }

    @Test
    public void endsWith_ignoreCase() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.endsWithIgnoreCase("H123")).fetchCount());
    }

    @Test
    public void endsWith2() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.endsWith("X")).fetchCount());
    }

    @Test
    public void endsWith3() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.endsWith("_123")).fetchCount());
    }

    @Test
    @NoBatooJPA
    public void enum_eq() {
        Assert.assertEquals(1, query().from(AbstractJPATest.company).where(AbstractJPATest.company.ratingOrdinal.eq(A)).fetchCount());
        Assert.assertEquals(1, query().from(AbstractJPATest.company).where(AbstractJPATest.company.ratingString.eq(AA)).fetchCount());
    }

    @Test
    @NoBatooJPA
    public void enum_in() {
        Assert.assertEquals(1, query().from(AbstractJPATest.company).where(AbstractJPATest.company.ratingOrdinal.in(A, AA)).fetchCount());
        Assert.assertEquals(1, query().from(AbstractJPATest.company).where(AbstractJPATest.company.ratingString.in(A, AA)).fetchCount());
    }

    @Test
    @NoBatooJPA
    public void enum_in2() {
        QEmployee employee = employee;
        JPQLQuery<?> query = query();
        query.from(employee).where(employee.lastName.eq("Smith"), employee.jobFunctions.contains(JobFunction.CODER));
        Assert.assertEquals(1L, query.fetchCount());
    }

    @Test
    @ExcludeIn(SQLSERVER)
    public void enum_startsWith() {
        Assert.assertEquals(1, query().from(AbstractJPATest.company).where(AbstractJPATest.company.ratingString.stringValue().startsWith("A")).fetchCount());
    }

    @Test
    @NoEclipseLink(HSQLDB)
    public void factoryExpressions() {
        QCat cat = cat;
        QCat cat2 = new QCat("cat2");
        QCat kitten = new QCat("kitten");
        JPQLQuery<Tuple> query = query().from(cat).leftJoin(cat.mate, cat2).leftJoin(cat2.kittens, kitten).select(Projections.tuple(cat.id, skipNulls()));
        Assert.assertEquals(6, query.fetch().size());
        Assert.assertNotNull(query.limit(1).fetchOne());
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    public void fetch() {
        QMammal mammal = mammal;
        QHuman human = new QHuman("mammal");
        query().from(mammal).leftJoin(human.hairs).fetchJoin().select(mammal).fetch();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    public void fetch2() {
        QWorld world = world;
        QMammal mammal = mammal;
        QHuman human = new QHuman("mammal");
        query().from(world).leftJoin(world.mammals, mammal).fetchJoin().leftJoin(human.hairs).fetchJoin().select(world).fetch();
    }

    @Test
    @ExcludeIn({ MYSQL, DERBY })
    @NoBatooJPA
    public void groupBy() {
        QAuthor author = author;
        QBook book = book;
        for (int i = 0; i < 10; i++) {
            Author a = new Author();
            a.setName(String.valueOf(i));
            save(a);
            for (int j = 0; j < 2; j++) {
                Book b = new Book();
                b.setTitle((((String.valueOf(i)) + " ") + (String.valueOf(j))));
                b.setAuthor(a);
                save(b);
            }
        }
        Map<Long, List<Pair<Long, String>>> map = query().from(author).join(author.books, book).transform(com.querydsl.core.group.GroupBy.groupBy(author.id).as(com.querydsl.core.group.GroupBy.list(com.querydsl.core.group.QPair.create(book.id, book.title))));
        for (Map.Entry<Long, List<Pair<Long, String>>> entry : map.entrySet()) {
            System.out.println(("author = " + (entry.getKey())));
            for (Pair<Long, String> pair : entry.getValue()) {
                System.out.println(((("  book = " + (pair.getFirst())) + ",") + (pair.getSecond())));
            }
        }
    }

    @Test
    public void groupBy2() {
        // select cat0_.name as col_0_0_, cat0_.breed as col_1_0_, sum(cat0_.bodyWeight) as col_2_0_
        // from animal_ cat0_ where cat0_.DTYPE in ('C', 'DC') and cat0_.bodyWeight>?
        // group by cat0_.name , cat0_.breed
        query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.bodyWeight.gt(0)).groupBy(AbstractJPATest.cat.name, AbstractJPATest.cat.breed).select(AbstractJPATest.cat.name, AbstractJPATest.cat.breed, AbstractJPATest.cat.bodyWeight.sum()).fetch();
    }

    @Test
    @NoEclipseLink
    public void groupBy_yearMonth() {
        query().from(AbstractJPATest.cat).groupBy(AbstractJPATest.cat.birthdate.yearMonth()).orderBy(AbstractJPATest.cat.birthdate.yearMonth().asc()).select(AbstractJPATest.cat.id.count()).fetch();
    }

    // https://hibernate.atlassian.net/browse/HHH-1902
    @Test
    @NoHibernate
    public void groupBy_select() {
        // select length(my_column) as column_size from my_table group by column_size
        NumberPath<Integer> length = Expressions.numberPath(Integer.class, "len");
        Assert.assertEquals(ImmutableList.of(4, 6, 7, 8), query().select(AbstractJPATest.cat.name.length().as(length)).from(AbstractJPATest.cat).orderBy(length.asc()).groupBy(length).fetch());
    }

    @Test
    public void groupBy_results() {
        QueryResults<Integer> results = query().from(AbstractJPATest.cat).groupBy(AbstractJPATest.cat.id).select(AbstractJPATest.cat.id).fetchResults();
        Assert.assertEquals(6, results.getTotal());
        Assert.assertEquals(6, results.getResults().size());
    }

    @Test
    public void groupBy_results2() {
        QueryResults<Integer> results = query().from(AbstractJPATest.cat).groupBy(AbstractJPATest.cat.birthdate).select(AbstractJPATest.cat.id.max()).fetchResults();
        Assert.assertEquals(1, results.getTotal());
        Assert.assertEquals(1, results.getResults().size());
    }

    @Test
    public void in() {
        Assert.assertEquals(3L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.in("Bob123", "Ruth123", "Felix123")).fetchCount());
        Assert.assertEquals(3L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.id.in(Arrays.asList(1, 2, 3))).fetchCount());
        Assert.assertEquals(0L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.in(Arrays.asList("A", "B", "C"))).fetchCount());
    }

    @Test
    public void in2() {
        Assert.assertEquals(3L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.id.in(1, 2, 3)).fetchCount());
        Assert.assertEquals(0L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.in("A", "B", "C")).fetchCount());
    }

    @Test
    public void in3() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.in("A,B,C".split(","))).fetchCount());
    }

    @Test
    public void in4() {
        // $.parameterRelease.id.eq(releaseId).and($.parameterGroups.any().id.in(filter.getGroups()));
        Assert.assertEquals(Arrays.asList(), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.id.eq(1), AbstractJPATest.cat.kittens.any().id.in(1, 2, 3)).select(AbstractJPATest.cat).fetch());
    }

    @Test
    public void in5() {
        Assert.assertEquals(4L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.mate.in(savedCats)).fetchCount());
    }

    @Test
    public void in7() {
        Assert.assertEquals(4L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittens.any().in(savedCats)).fetchCount());
    }

    @Test
    public void in_empty() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.in(ImmutableList.<String>of())).fetchCount());
    }

    @Test
    @NoOpenJPA
    public void indexOf() {
        Assert.assertEquals(Integer.valueOf(0), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq("Bob123")).select(AbstractJPATest.cat.name.indexOf("B")).fetchFirst());
    }

    @Test
    @NoOpenJPA
    public void indexOf2() {
        Assert.assertEquals(Integer.valueOf(1), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq("Bob123")).select(AbstractJPATest.cat.name.indexOf("o")).fetchFirst());
    }

    @Test
    public void instanceOf_cat() {
        Assert.assertEquals(6L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.instanceOf(Cat.class)).fetchCount());
    }

    @Test
    public void instanceOf_domesticCat() {
        Assert.assertEquals(0L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.instanceOf(DomesticCat.class)).fetchCount());
    }

    @Test
    public void instanceOf_entity1() {
        QEntity1 entity1 = entity1;
        Assert.assertEquals(2L, query().from(entity1).where(entity1.instanceOf(Entity1.class)).fetchCount());
    }

    @Test
    public void instanceOf_entity2() {
        QEntity1 entity1 = entity1;
        Assert.assertEquals(1L, query().from(entity1).where(entity1.instanceOf(Entity2.class)).fetchCount());
    }

    // https://hibernate.atlassian.net/browse/HHH-6686
    @Test
    @NoHibernate
    public void isEmpty_elementCollection() {
        QEmployee employee = employee;
        Assert.assertEquals(0, query().from(employee).where(employee.jobFunctions.isEmpty()).fetchCount());
    }

    @Test
    public void isEmpty_relation() {
        Assert.assertEquals(6L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.kittensSet.isEmpty()).fetchCount());
    }

    @Test
    @NoEclipseLink
    @ExcludeIn({ ORACLE, TERADATA })
    public void joinEmbeddable() {
        QBookVersion bookVersion = QBookVersion.bookVersion;
        QBookMark bookMark = QBookMark.bookMark;
        Assert.assertEquals(Arrays.asList(), query().from(bookVersion).join(bookVersion.definition.bookMarks, bookMark).where(bookVersion.definition.bookMarks.size().eq(1), bookMark.page.eq(2357L).or(bookMark.page.eq(2356L))).select(bookVersion).fetch());
    }

    @Test
    public void length() {
        Assert.assertEquals(6, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.length().gt(0)).fetchCount());
    }

    @Test
    public void like() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.like("!")).fetchCount());
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.like("\\")).fetchCount());
    }

    @Test
    public void limit() {
        List<String> names1 = Arrays.asList("Allen123", "Bob123");
        Assert.assertEquals(names1, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.name.asc()).limit(2).select(AbstractJPATest.cat.name).fetch());
    }

    @Test
    public void limit_and_offset() {
        List<String> names3 = Arrays.asList("Felix123", "Mary_123");
        Assert.assertEquals(names3, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.name.asc()).limit(2).offset(2).select(AbstractJPATest.cat.name).fetch());
    }

    @Test
    public void limit2() {
        Assert.assertEquals(Collections.singletonList("Allen123"), query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.name.asc()).limit(1).select(AbstractJPATest.cat.name).fetch());
    }

    @Test
    public void limit3() {
        Assert.assertEquals(6, query().from(AbstractJPATest.cat).limit(Long.MAX_VALUE).select(AbstractJPATest.cat).fetch().size());
    }

    @Test
    public void list_elementCollection_of_enum() {
        QEmployee employee = employee;
        // QJobFunction jobFunction = QJobFunction.jobFunction;
        EnumPath<JobFunction> jobFunction = Expressions.enumPath(JobFunction.class, "jf");
        List<JobFunction> jobFunctions = query().from(employee).innerJoin(employee.jobFunctions, jobFunction).select(jobFunction).fetch();
        Assert.assertEquals(4, jobFunctions.size());
    }

    @Test
    @NoBatooJPA
    public void list_elementCollection_of_string() {
        QFoo foo = foo;
        StringPath str = Expressions.stringPath("str");
        List<String> strings = query().from(foo).innerJoin(foo.names, str).select(str).fetch();
        Assert.assertEquals(2, strings.size());
        Assert.assertTrue(strings.contains("a"));
        Assert.assertTrue(strings.contains("b"));
    }

    @Test
    @NoEclipseLink(HSQLDB)
    public void list_order_get() {
        QCat cat = cat;
        Assert.assertEquals(6, query().from(cat).orderBy(cat.kittens.get(0).name.asc()).fetch().size());
    }

    @Test
    @NoEclipseLink(HSQLDB)
    public void list_order_get2() {
        QCat cat = cat;
        Assert.assertEquals(6, query().from(cat).orderBy(cat.mate.kittens.get(0).name.asc()).fetch().size());
    }

    @Test
    public void map_get() {
        QShow show = show;
        Assert.assertEquals(Arrays.asList("A"), query().from(show).select(show.acts.get("a")).fetch());
    }

    @Test
    @NoHibernate
    public void map_get2() {
        QShow show = show;
        Assert.assertEquals(1, query().from(show).where(show.acts.get("a").eq("A")).fetchCount());
    }

    @Test
    @NoEclipseLink
    public void map_order_get() {
        QShow show = show;
        Assert.assertEquals(1, query().from(show).orderBy(show.parent.acts.get("A").asc()).fetch().size());
    }

    @Test
    @NoEclipseLink
    public void map_order_get2() {
        QShow show = show;
        QShow parent = new QShow("parent");
        Assert.assertEquals(1, query().from(show).leftJoin(show.parent, parent).orderBy(parent.acts.get("A").asc()).fetch().size());
    }

    @Test
    public void map_containsKey() {
        QShow show = show;
        Assert.assertEquals(1L, query().from(show).where(show.acts.containsKey("a")).fetchCount());
    }

    @Test
    public void map_containsKey2() {
        QShow show = show;
        Assert.assertEquals(1L, query().from(show).where(show.acts.containsKey("b")).fetchCount());
    }

    @Test
    public void map_containsKey3() {
        QShow show = show;
        Assert.assertEquals(0L, query().from(show).where(show.acts.containsKey("c")).fetchCount());
    }

    @Test
    public void map_containsValue() {
        QShow show = show;
        Assert.assertEquals(1L, query().from(show).where(show.acts.containsValue("A")).fetchCount());
    }

    @Test
    public void map_containsValue2() {
        QShow show = show;
        Assert.assertEquals(1L, query().from(show).where(show.acts.containsValue("B")).fetchCount());
    }

    @Test
    public void map_containsValue3() {
        QShow show = show;
        Assert.assertEquals(0L, query().from(show).where(show.acts.containsValue("C")).fetchCount());
    }

    @Test
    public void map_contains() {
        QShow show = show;
        Assert.assertEquals(1L, query().from(show).where(show.acts.contains("a", "A")).fetchCount());
        Assert.assertEquals(0L, query().from(show).where(show.acts.contains("X", "X")).fetchCount());
    }

    @Test
    public void map_groupBy() {
        QShow show = show;
        Assert.assertEquals(1, query().from(show).select(show.acts.get("X")).groupBy(show.acts.get("a")).fetchCount());
    }

    @Test
    public void max() {
        Assert.assertEquals(6.0, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.max()).fetchFirst().doubleValue(), 1.0E-4);
    }

    @Test
    public void min() {
        Assert.assertEquals(1.0, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.min()).fetchFirst().doubleValue(), 1.0E-4);
    }

    @Test
    @ExcludeIn(ORACLE)
    public void multiply() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(entity.ddouble.multiply(entity2.ddouble).loe(2.0)).select(entity).fetch());
    }

    @Test
    @ExcludeIn(ORACLE)
    public void multiply_bigDecimal() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        NumberPath<BigDecimal> bigd1 = entity.bigDecimal;
        NumberPath<BigDecimal> bigd2 = entity2.bigDecimal;
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(bigd1.multiply(bigd2).loe(new BigDecimal("1.00"))).select(entity).fetch());
    }

    @Test
    public void nestedProjection() {
        Concatenation concat = new Concatenation(AbstractJPATest.cat.name, AbstractJPATest.cat.name);
        List<Tuple> tuples = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name, concat).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            Assert.assertEquals(tuple.get(concat), ((tuple.get(AbstractJPATest.cat.name)) + (tuple.get(AbstractJPATest.cat.name))));
        }
    }

    @Test
    public void not_in() {
        long all = query().from(AbstractJPATest.cat).fetchCount();
        Assert.assertEquals((all - 3L), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.notIn("Bob123", "Ruth123", "Felix123")).fetchCount());
        Assert.assertEquals(3L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.id.notIn(1, 2, 3)).fetchCount());
        Assert.assertEquals(6L, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.notIn("A", "B", "C")).fetchCount());
    }

    @Test
    @NoBatooJPA
    public void not_in_empty() {
        long count = query().from(AbstractJPATest.cat).fetchCount();
        Assert.assertEquals(count, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.notIn(Collections.<String>emptyList())).fetchCount());
    }

    @Test
    public void null_as_uniqueResult() {
        Assert.assertNull(query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(UUID.randomUUID().toString())).select(AbstractJPATest.cat).fetchFirst());
    }

    @Test
    @NoEclipseLink
    public void numeric() {
        QNumeric numeric = numeric;
        BigDecimal singleResult = query().from(numeric).select(numeric.value).fetchFirst();
        Assert.assertEquals(26.9, singleResult.doubleValue(), 0.001);
    }

    // FIXME
    @Test
    @NoOpenJPA
    @NoBatooJPA
    public void offset1() {
        List<String> names2 = Arrays.asList("Bob123", "Felix123", "Mary_123", "Ruth123", "Some");
        Assert.assertEquals(names2, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.name.asc()).offset(1).select(AbstractJPATest.cat.name).fetch());
    }

    // FIXME
    @Test
    @NoOpenJPA
    @NoBatooJPA
    public void offset2() {
        List<String> names2 = Arrays.asList("Felix123", "Mary_123", "Ruth123", "Some");
        Assert.assertEquals(names2, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.name.asc()).offset(2).select(AbstractJPATest.cat.name).fetch());
    }

    @Test
    public void one_to_one() {
        QEmployee employee = employee;
        QUser user = user;
        JPQLQuery<?> query = query();
        query.from(employee);
        query.innerJoin(employee.user, user);
        query.select(employee).fetch();
    }

    @Test
    public void order() {
        NumberPath<Double> weight = Expressions.numberPath(Double.class, "weight");
        Assert.assertEquals(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0), query().from(AbstractJPATest.cat).orderBy(weight.asc()).select(AbstractJPATest.cat.bodyWeight.as(weight)).fetch());
    }

    @Test
    public void order_by_count() {
        NumberPath<Long> count = Expressions.numberPath(Long.class, "c");
        query().from(AbstractJPATest.cat).groupBy(AbstractJPATest.cat.id).orderBy(count.asc()).select(AbstractJPATest.cat.id, AbstractJPATest.cat.id.count().as(count)).fetch();
    }

    @Test
    public void order_stringValue() {
        int count = ((int) (query().from(AbstractJPATest.cat).fetchCount()));
        Assert.assertEquals(count, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.stringValue().asc()).select(AbstractJPATest.cat).fetch().size());
    }

    // can't be parsed
    @Test
    @NoBatooJPA
    public void order_stringValue_to_integer() {
        int count = ((int) (query().from(AbstractJPATest.cat).fetchCount()));
        Assert.assertEquals(count, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.stringValue().castToNum(Integer.class).asc()).select(AbstractJPATest.cat).fetch().size());
    }

    // can't be parsed
    @Test
    @NoBatooJPA
    public void order_stringValue_toLong() {
        int count = ((int) (query().from(AbstractJPATest.cat).fetchCount()));
        Assert.assertEquals(count, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.stringValue().castToNum(Long.class).asc()).select(AbstractJPATest.cat).fetch().size());
    }

    // can't be parsed
    @Test
    @NoBatooJPA
    public void order_stringValue_toBigInteger() {
        int count = ((int) (query().from(AbstractJPATest.cat).fetchCount()));
        Assert.assertEquals(count, query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.id.stringValue().castToNum(java.math.BigInteger.class).asc()).select(AbstractJPATest.cat).fetch().size());
    }

    @Test
    @NoBatooJPA
    @ExcludeIn(SQLSERVER)
    public void order_nullsFirst() {
        Assert.assertNull(query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.dateField.asc().nullsFirst()).select(AbstractJPATest.cat.dateField).fetchFirst());
    }

    @Test
    @NoBatooJPA
    @ExcludeIn(SQLSERVER)
    public void order_nullsLast() {
        Assert.assertNotNull(query().from(AbstractJPATest.cat).orderBy(AbstractJPATest.cat.dateField.asc().nullsLast()).select(AbstractJPATest.cat.dateField).fetchFirst());
    }

    @Test
    public void params() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("Bob123", query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(name)).set(name, "Bob123").select(AbstractJPATest.cat.name).fetchFirst());
    }

    @Test
    public void params_anon() {
        Param<String> name = new Param<String>(String.class);
        Assert.assertEquals("Bob123", query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(name)).set(name, "Bob123").select(AbstractJPATest.cat.name).fetchFirst());
    }

    @Test(expected = ParamNotSetException.class)
    public void params_not_set() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("Bob123", query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(name)).select(AbstractJPATest.cat.name).fetchFirst());
    }

    @Test
    public void precedence() {
        StringPath str = AbstractJPATest.cat.name;
        Predicate where = str.like("Bob%").and(str.like("%ob123")).or(str.like("Ruth%").and(str.like("%uth123")));
        Assert.assertEquals(2L, query().from(AbstractJPATest.cat).where(where).fetchCount());
    }

    @Test
    public void precedence2() {
        StringPath str = AbstractJPATest.cat.name;
        Predicate where = str.like("Bob%").and(str.like("%ob123").or(str.like("Ruth%"))).and(str.like("%uth123"));
        Assert.assertEquals(0L, query().from(AbstractJPATest.cat).where(where).fetchCount());
    }

    @Test
    public void precedence3() {
        Predicate where = AbstractJPATest.cat.name.eq("Bob123").and(AbstractJPATest.cat.id.eq(1)).or(AbstractJPATest.cat.name.eq("Ruth123").and(AbstractJPATest.cat.id.eq(2)));
        Assert.assertEquals(2L, query().from(AbstractJPATest.cat).where(where).fetchCount());
    }

    @Test
    public void factoryExpression_in_groupBy() {
        Expression<Cat> catBean = Projections.bean(Cat.class, AbstractJPATest.cat.id, AbstractJPATest.cat.name);
        Assert.assertFalse(query().from(AbstractJPATest.cat).groupBy(catBean).select(catBean).fetch().isEmpty());
    }

    @Test
    public void startsWith() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.startsWith("R")).fetchCount());
    }

    @Test
    public void startsWith_ignoreCase() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.startsWithIgnoreCase("r")).fetchCount());
    }

    @Test
    public void startsWith2() {
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.startsWith("X")).fetchCount());
    }

    @Test
    public void startsWith3() {
        Assert.assertEquals(1, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.startsWith("Mary_")).fetchCount());
    }

    @Test
    @ExcludeIn({ MYSQL, SQLSERVER, TERADATA })
    @NoOpenJPA
    public void stringOperations() {
        // NOTE : locate in MYSQL is case-insensitive
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.startsWith("r")).fetchCount());
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.endsWith("H123")).fetchCount());
        Assert.assertEquals(Integer.valueOf(2), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq("Bob123")).select(AbstractJPATest.cat.name.indexOf("b")).fetchFirst());
    }

    @Test
    public void subQuery() {
        QShow show = show;
        QShow show2 = new QShow("show2");
        Assert.assertEquals(0, query().from(show).where(JPAExpressions.select(show2.count()).from(show2).where(show2.id.ne(show.id)).gt(0L)).fetchCount());
    }

    @Test
    public void subQuery2() {
        QCat cat = cat;
        QCat other = new QCat("other");
        Assert.assertEquals(savedCats, query().from(cat).where(cat.name.in(JPAExpressions.select(other.name).from(other).groupBy(other.name))).orderBy(cat.id.asc()).select(cat).fetch());
    }

    @Test
    public void subQuery3() {
        QCat cat = cat;
        QCat other = new QCat("other");
        Assert.assertEquals(savedCats.subList(0, 1), query().from(cat).where(cat.name.eq(JPAExpressions.select(other.name).from(other).where(other.name.indexOf("B").eq(0)))).select(cat).fetch());
    }

    @Test
    public void subQuery4() {
        QCat cat = cat;
        QCat other = new QCat("other");
        query().from(cat).select(cat.name, JPAExpressions.select(other.count()).from(other).where(other.name.eq(cat.name))).fetch();
    }

    @Test
    public void subQuery5() {
        QEmployee employee = employee;
        QEmployee employee2 = new QEmployee("e2");
        Assert.assertEquals(2, query().from(employee).where(JPAExpressions.select(employee2.id.count()).from(employee2).gt(1L)).fetchCount());
    }

    @Test
    public void substring() {
        for (String str : query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name.substring(1, 2)).fetch()) {
            Assert.assertEquals(1, str.length());
        }
    }

    @Test
    @NoBatooJPA
    @ExcludeIn(ORACLE)
    public void substring2() {
        QCompany company = company;
        StringExpression name = company.name;
        Integer companyId = query().from(company).select(company.id).fetchFirst();
        JPQLQuery<?> query = query().from(company).where(company.id.eq(companyId));
        String str = query.select(company.name).fetchFirst();
        Assert.assertEquals(Integer.valueOf(29), query.select(name.length().subtract(11)).fetchFirst());
        Assert.assertEquals(str.substring(0, 7), query.select(name.substring(0, 7)).fetchFirst());
        Assert.assertEquals(str.substring(15), query.select(name.substring(15)).fetchFirst());
        Assert.assertEquals(str.substring(str.length()), query.select(name.substring(name.length())).fetchFirst());
        Assert.assertEquals(str.substring(((str.length()) - 11)), query.select(name.substring(name.length().subtract(11))).fetchFirst());
    }

    @Test
    @ExcludeIn({ HSQLDB, DERBY })
    public void substring_from_right2() {
        Assert.assertEquals(Arrays.asList(), query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.substring(AbstractJPATest.cat.name.length().subtract(1), AbstractJPATest.cat.name.length()).eq(AbstractJPATest.cat.name.substring(AbstractJPATest.cat.name.length().subtract(2), AbstractJPATest.cat.name.length().subtract(1)))).select(AbstractJPATest.cat).fetch());
    }

    @Test
    @ExcludeIn(ORACLE)
    public void subtract_bigDecimal() {
        QSimpleTypes entity = new QSimpleTypes("entity1");
        QSimpleTypes entity2 = new QSimpleTypes("entity2");
        NumberPath<BigDecimal> bigd1 = entity.bigDecimal;
        NumberPath<BigDecimal> bigd2 = entity2.bigDecimal;
        Assert.assertEquals(Arrays.asList(), query().from(entity, entity2).where(bigd1.subtract(bigd2).loe(new BigDecimal("1.00"))).select(entity).fetch());
    }

    @Test
    public void sum_3() {
        Assert.assertEquals(21.0, query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.sum()).fetchFirst().doubleValue(), 1.0E-4);
    }

    @Test
    public void sum_3_projected() {
        double val = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.sum()).fetchFirst();
        DoubleProjection projection = query().from(AbstractJPATest.cat).select(new QDoubleProjection(AbstractJPATest.cat.bodyWeight.sum())).fetchFirst();
        Assert.assertEquals(val, projection.val, 0.001);
    }

    @Test
    public void sum_4() {
        Double dbl = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.bodyWeight.sum().negate()).fetchFirst();
        Assert.assertNotNull(dbl);
    }

    @Test
    public void sum_5() {
        QShow show = show;
        Long lng = query().from(show).select(show.id.sum()).fetchFirst();
        Assert.assertNotNull(lng);
    }

    @Test
    public void sum_of_integer() {
        QCat cat2 = new QCat("cat2");
        Assert.assertEquals(Arrays.asList(), query().from(AbstractJPATest.cat).where(JPAExpressions.select(cat2.breed.sum()).from(cat2).where(cat2.eq(AbstractJPATest.cat.mate)).gt(0)).select(AbstractJPATest.cat).fetch());
    }

    @Test
    public void sum_of_float() {
        QCat cat2 = new QCat("cat2");
        query().from(AbstractJPATest.cat).where(JPAExpressions.select(cat2.floatProperty.sum()).from(cat2).where(cat2.eq(AbstractJPATest.cat.mate)).gt(0.0F)).select(AbstractJPATest.cat).fetch();
    }

    @Test
    public void sum_of_double() {
        QCat cat2 = new QCat("cat2");
        query().from(AbstractJPATest.cat).where(JPAExpressions.select(cat2.bodyWeight.sum()).from(cat2).where(cat2.eq(AbstractJPATest.cat.mate)).gt(0.0)).select(AbstractJPATest.cat).fetch();
    }

    @Test
    public void sum_as_float() {
        float val = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.floatProperty.sum()).fetchFirst();
        Assert.assertTrue((val > 0));
    }

    @Test
    public void sum_as_float_projected() {
        float val = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.floatProperty.sum()).fetchFirst();
        FloatProjection projection = query().from(AbstractJPATest.cat).select(new QFloatProjection(AbstractJPATest.cat.floatProperty.sum())).fetchFirst();
        Assert.assertEquals(val, projection.val, 0.001);
    }

    @Test
    public void sum_as_float2() {
        float val = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.floatProperty.sum().negate()).fetchFirst();
        Assert.assertTrue((val < 0));
    }

    @Test
    public void sum_coalesce() {
        int val = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.weight.sum().coalesce(0)).fetchFirst();
        Assert.assertTrue((val == 0));
    }

    @Test
    public void sum_noRows_double() {
        Assert.assertEquals(null, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(UUID.randomUUID().toString())).select(AbstractJPATest.cat.bodyWeight.sum()).fetchFirst());
    }

    @Test
    public void sum_noRows_float() {
        Assert.assertEquals(null, query().from(AbstractJPATest.cat).where(AbstractJPATest.cat.name.eq(UUID.randomUUID().toString())).select(AbstractJPATest.cat.floatProperty.sum()).fetchFirst());
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    public void test() {
        Cat kitten = savedCats.get(0);
        Cat noKitten = savedCats.get(((savedCats.size()) - 1));
        ProjectionsFactory projections = new ProjectionsFactory(JPA, getTarget()) {
            @Override
            public <A, Q extends SimpleExpression<A>> Collection<Expression<?>> list(ListPath<A, Q> expr, ListExpression<A, Q> other, A knownElement) {
                // NOTE : expr.get(0) is only supported in the where clause
                return Collections.<Expression<?>>singleton(expr.size());
            }
        };
        final EntityPath<?>[] sources = new EntityPath<?>[]{ AbstractJPATest.cat, AbstractJPATest.otherCat };
        final Predicate[] conditions = new Predicate[]{ AbstractJPATest.condition };
        final Expression<?>[] projection = new Expression<?>[]{ AbstractJPATest.cat.name, AbstractJPATest.otherCat.name };
        QueryExecution standardTest = new QueryExecution(projections, new FilterFactory(projections, JPA, getTarget()), new MatchingFiltersFactory(JPA, getTarget())) {
            @Override
            protected Fetchable<?> createQuery() {
                // NOTE : EclipseLink needs extra conditions cond1 and code2
                return testQuery().from(sources).where(conditions);
            }

            @Override
            protected Fetchable<?> createQuery(Predicate filter) {
                // NOTE : EclipseLink needs extra conditions cond1 and code2
                return testQuery().from(sources).where(AbstractJPATest.condition, filter).select(projection);
            }
        };
        // standardTest.runArrayTests(cat.kittensArray, otherCat.kittensArray, kitten, noKitten);
        standardTest.runBooleanTests(AbstractJPATest.cat.name.isNull(), AbstractJPATest.otherCat.kittens.isEmpty());
        standardTest.runCollectionTests(AbstractJPATest.cat.kittens, AbstractJPATest.otherCat.kittens, kitten, noKitten);
        standardTest.runDateTests(AbstractJPATest.cat.dateField, AbstractJPATest.otherCat.dateField, AbstractJPATest.date);
        standardTest.runDateTimeTests(AbstractJPATest.cat.birthdate, AbstractJPATest.otherCat.birthdate, AbstractJPATest.birthDate);
        standardTest.runListTests(AbstractJPATest.cat.kittens, AbstractJPATest.otherCat.kittens, kitten, noKitten);
        // standardTest.mapTests(cat.kittensByName, otherCat.kittensByName, "Kitty", kitten);
        // int
        standardTest.runNumericCasts(AbstractJPATest.cat.id, AbstractJPATest.otherCat.id, 1);
        standardTest.runNumericTests(AbstractJPATest.cat.id, AbstractJPATest.otherCat.id, 1);
        // double
        standardTest.runNumericCasts(AbstractJPATest.cat.bodyWeight, AbstractJPATest.otherCat.bodyWeight, 1.0);
        standardTest.runNumericTests(AbstractJPATest.cat.bodyWeight, AbstractJPATest.otherCat.bodyWeight, 1.0);
        standardTest.runStringTests(AbstractJPATest.cat.name, AbstractJPATest.otherCat.name, kitten.getName());
        standardTest.runTimeTests(AbstractJPATest.cat.timeField, AbstractJPATest.otherCat.timeField, AbstractJPATest.time);
        standardTest.report();
    }

    @Test
    public void tupleProjection() {
        List<Tuple> tuples = query().from(AbstractJPATest.cat).select(AbstractJPATest.cat.name, AbstractJPATest.cat).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            Assert.assertNotNull(tuple.get(AbstractJPATest.cat.name));
            Assert.assertNotNull(tuple.get(AbstractJPATest.cat));
        }
    }

    @Test
    public void tupleProjection_as_queryResults() {
        QueryResults<Tuple> tuples = query().from(AbstractJPATest.cat).limit(1).select(AbstractJPATest.cat.name, AbstractJPATest.cat).fetchResults();
        Assert.assertEquals(1, tuples.getResults().size());
        Assert.assertTrue(((tuples.getTotal()) > 0));
    }

    @Test
    @ExcludeIn(DERBY)
    public void transform_groupBy() {
        QCat kitten = new QCat("kitten");
        Map<Integer, Cat> result = query().from(AbstractJPATest.cat).innerJoin(AbstractJPATest.cat.kittens, kitten).transform(com.querydsl.core.group.GroupBy.groupBy(AbstractJPATest.cat.id).as(Projections.constructor(Cat.class, AbstractJPATest.cat.name, AbstractJPATest.cat.id, com.querydsl.core.group.GroupBy.list(Projections.constructor(Cat.class, kitten.name, kitten.id)))));
        for (Cat entry : result.values()) {
            Assert.assertEquals(1, entry.getKittens().size());
        }
    }

    @Test
    @ExcludeIn(DERBY)
    public void transform_groupBy2() {
        QCat kitten = new QCat("kitten");
        Map<List<?>, Group> result = query().from(AbstractJPATest.cat).innerJoin(AbstractJPATest.cat.kittens, kitten).transform(com.querydsl.core.group.GroupBy.groupBy(AbstractJPATest.cat.id, kitten.id).as(AbstractJPATest.cat, kitten));
        Assert.assertFalse(result.isEmpty());
        for (Tuple row : query().from(AbstractJPATest.cat).innerJoin(AbstractJPATest.cat.kittens, kitten).select(AbstractJPATest.cat, kitten).fetch()) {
            Assert.assertNotNull(result.get(Arrays.asList(row.get(AbstractJPATest.cat).getId(), row.get(kitten).getId())));
        }
    }

    @Test
    @ExcludeIn(DERBY)
    public void transform_groupBy_alias() {
        QCat kitten = new QCat("kitten");
        SimplePath<Cat> k = Expressions.path(Cat.class, "k");
        Map<Integer, Group> result = query().from(AbstractJPATest.cat).innerJoin(AbstractJPATest.cat.kittens, kitten).transform(com.querydsl.core.group.GroupBy.groupBy(AbstractJPATest.cat.id).as(AbstractJPATest.cat.name, AbstractJPATest.cat.id, com.querydsl.core.group.GroupBy.list(Projections.constructor(Cat.class, kitten.name, kitten.id).as(k))));
        for (Group entry : result.values()) {
            Assert.assertNotNull(entry.getOne(AbstractJPATest.cat.id));
            Assert.assertNotNull(entry.getOne(AbstractJPATest.cat.name));
            Assert.assertFalse(entry.getList(k).isEmpty());
        }
    }

    @Test
    @NoBatooJPA
    public void treat() {
        QDomesticCat domesticCat = domesticCat;
        Assert.assertEquals(0, query().from(AbstractJPATest.cat).innerJoin(AbstractJPATest.cat.mate, domesticCat._super).where(domesticCat.name.eq("Bobby")).fetchCount());
    }

    @Test
    @NoOpenJPA
    public void type_order() {
        Assert.assertEquals(Arrays.asList(10, 1, 2, 3, 4, 5, 6), query().from(AbstractJPATest.animal).orderBy(JPAExpressions.type(AbstractJPATest.animal).asc(), AbstractJPATest.animal.id.asc()).select(AbstractJPATest.animal.id).fetch());
    }

    @Test
    @ExcludeIn(DERBY)
    public void byte_array() {
        QSimpleTypes simpleTypes = simpleTypes;
        Assert.assertEquals(ImmutableList.of(), query().from(simpleTypes).where(simpleTypes.byteArray.eq(new byte[]{ 0, 1 })).select(simpleTypes).fetch());
    }
}


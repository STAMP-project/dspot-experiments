package com.querydsl.jpa;


import QCat.cat.color;
import Wildcard.all;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Color;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.sql.SAnimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSQLTest {
    protected static final SAnimal cat = new SAnimal("cat");

    public static class CatDTO {
        Cat cat;

        public CatDTO(Cat cat) {
            this.cat = cat;
        }
    }

    @Test
    public void count() {
        Assert.assertEquals(6L, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).fetchCount());
    }

    @Test
    public void count_via_unique() {
        Assert.assertEquals(Long.valueOf(6), query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).select(AbstractSQLTest.cat.id.count()).fetchFirst());
    }

    @Test
    public void countDistinct() {
        Assert.assertEquals(6L, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).distinct().fetchCount());
    }

    @Test
    public void enum_binding() {
        List<Cat> cats = query().from(AbstractSQLTest.cat).select(Projections.bean(Cat.class, color)).fetch();
        Assert.assertFalse(cats.isEmpty());
        for (Cat cat : cats) {
            Assert.assertEquals(Color.BLACK, cat.getColor());
        }
    }

    @Test
    public void entityQueries() {
        QCat catEntity = QCat.cat;
        List<Cat> cats = query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.name.asc()).select(catEntity).fetch();
        Assert.assertEquals(6, cats.size());
        for (Cat c : cats) {
            Assert.assertNotNull(c.getName());
        }
    }

    @Test
    public void entityQueries2() {
        SAnimal mate = new SAnimal("mate");
        QCat catEntity = QCat.cat;
        List<Cat> cats = query().from(AbstractSQLTest.cat).innerJoin(mate).on(AbstractSQLTest.cat.mateId.eq(mate.id)).where(AbstractSQLTest.cat.dtype.eq("C"), mate.dtype.eq("C")).select(catEntity).fetch();
        Assert.assertTrue(cats.isEmpty());
    }

    @Test
    public void entityQueries3() {
        QCat catEntity = new QCat("animal_");
        Assert.assertEquals(0, query().from(catEntity).select(catEntity.toes.max()).fetchFirst().intValue());
    }

    @Test
    @NoBatooJPA
    @NoEclipseLink
    public void entityQueries4() {
        QCat catEntity = QCat.cat;
        List<Tuple> cats = query().from(AbstractSQLTest.cat).select(catEntity, AbstractSQLTest.cat.name, AbstractSQLTest.cat.id).fetch();
        Assert.assertEquals(6, cats.size());
        for (Tuple tuple : cats) {
            Assert.assertTrue(((tuple.get(catEntity)) instanceof Cat));
            Assert.assertTrue(((tuple.get(AbstractSQLTest.cat.name)) instanceof String));
            Assert.assertTrue(((tuple.get(AbstractSQLTest.cat.id)) instanceof Integer));
        }
    }

    @Test
    @NoBatooJPA
    @NoEclipseLink
    public void entityQueries5() {
        QCat catEntity = QCat.cat;
        SAnimal otherCat = new SAnimal("otherCat");
        QCat otherCatEntity = new QCat("otherCat");
        List<Tuple> cats = query().from(AbstractSQLTest.cat, otherCat).select(catEntity, otherCatEntity).fetch();
        Assert.assertEquals(36, cats.size());
        for (Tuple tuple : cats) {
            Assert.assertTrue(((tuple.get(catEntity)) instanceof Cat));
            Assert.assertTrue(((tuple.get(otherCatEntity)) instanceof Cat));
        }
    }

    @Test
    @NoBatooJPA
    @NoEclipseLink
    public void entityQueries6() {
        QCat catEntity = QCat.cat;
        List<AbstractSQLTest.CatDTO> results = query().from(AbstractSQLTest.cat).select(Projections.constructor(AbstractSQLTest.CatDTO.class, catEntity)).fetch();
        Assert.assertEquals(6, results.size());
        for (AbstractSQLTest.CatDTO cat : results) {
            Assert.assertTrue(((cat.cat) instanceof Cat));
        }
    }

    @Test
    public void entityQueries7() {
        QCompany company = QCompany.company;
        Assert.assertEquals(Arrays.asList(), query().from(company).select(company.officialName).fetch());
    }

    @Test
    public void in() {
        Assert.assertEquals(6L, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.in("C", "CX")).fetchCount());
    }

    @Test
    public void limit_offset() {
        Assert.assertEquals(2, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).limit(2).offset(2).select(AbstractSQLTest.cat.id, AbstractSQLTest.cat.name).fetch().size());
    }

    @Test
    public void list() {
        Assert.assertEquals(6, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).select(AbstractSQLTest.cat.id).fetch().size());
    }

    @Test
    public void list_limit_and_offset() {
        Assert.assertEquals(3, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).offset(3).limit(3).select(AbstractSQLTest.cat.id).fetch().size());
    }

    @Test
    public void list_limit_and_offset2() {
        List<Tuple> tuples = query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).offset(3).limit(3).select(AbstractSQLTest.cat.id, AbstractSQLTest.cat.name).fetch();
        Assert.assertEquals(3, tuples.size());
        Assert.assertEquals(2, tuples.get(0).size());
    }

    @Test
    public void list_limit_and_offset3() {
        List<Tuple> tuples = query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).offset(3).limit(3).select(Projections.tuple(AbstractSQLTest.cat.id, AbstractSQLTest.cat.name)).fetch();
        Assert.assertEquals(3, tuples.size());
        Assert.assertEquals(2, tuples.get(0).size());
    }

    @Test
    public void list_multiple() {
        print(query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).select(AbstractSQLTest.cat.id, AbstractSQLTest.cat.name, AbstractSQLTest.cat.bodyWeight).fetch());
    }

    @Test
    public void list_non_path() {
        Assert.assertEquals(6, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).select(AbstractSQLTest.cat.birthdate.year(), AbstractSQLTest.cat.birthdate.month(), AbstractSQLTest.cat.birthdate.dayOfMonth()).fetch().size());
    }

    @Test
    public void list_results() {
        QueryResults<String> results = query().from(AbstractSQLTest.cat).limit(3).orderBy(AbstractSQLTest.cat.name.asc()).select(AbstractSQLTest.cat.name).fetchResults();
        Assert.assertEquals(Arrays.asList("Beck", "Bobby", "Harold"), results.getResults());
        Assert.assertEquals(6L, results.getTotal());
    }

    @Test
    @ExcludeIn(Target.H2)
    public void list_wildcard() {
        Assert.assertEquals(6, query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).select(all).fetch().size());
    }

    @Test
    public void list_with_count() {
        print(query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.dtype.eq("C")).groupBy(AbstractSQLTest.cat.name).select(AbstractSQLTest.cat.name, AbstractSQLTest.cat.id.count()).fetch());
    }

    @Test
    public void list_with_limit() {
        Assert.assertEquals(3, query().from(AbstractSQLTest.cat).limit(3).select(AbstractSQLTest.cat.id).fetch().size());
    }

    @Test
    @ExcludeIn({ Target.H2, Target.MYSQL })
    public void list_with_offset() {
        Assert.assertEquals(3, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).offset(3).select(AbstractSQLTest.cat.id).fetch().size());
    }

    @Test
    @ExcludeIn(Target.HSQLDB)
    public void no_from() {
        Assert.assertNotNull(query().select(DateExpression.currentDate()).fetchFirst());
    }

    @Test
    public void null_as_uniqueResult() {
        Assert.assertNull(query().from(AbstractSQLTest.cat).where(AbstractSQLTest.cat.name.eq(UUID.randomUUID().toString())).select(AbstractSQLTest.cat.name).fetchOne());
    }

    @Test
    public void projections_duplicateColumns() {
        SAnimal cat = new SAnimal("cat");
        Assert.assertEquals(1, query().from(cat).select(Projections.list(cat.count(), cat.count())).fetch().size());
    }

    @Test
    public void single_result() {
        query().from(AbstractSQLTest.cat).select(AbstractSQLTest.cat.id).fetchFirst();
    }

    @Test
    public void single_result_multiple() {
        Assert.assertEquals(1, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).select(new Expression<?>[]{ AbstractSQLTest.cat.id }).fetchFirst().get(AbstractSQLTest.cat.id).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union() throws SQLException {
        SubQueryExpression<Integer> sq1 = select(AbstractSQLTest.cat.id.max()).from(AbstractSQLTest.cat);
        SubQueryExpression<Integer> sq2 = select(AbstractSQLTest.cat.id.min()).from(AbstractSQLTest.cat);
        List<Integer> list = query().union(sq1, sq2).list();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union_all() {
        SubQueryExpression<Integer> sq1 = select(AbstractSQLTest.cat.id.max()).from(AbstractSQLTest.cat);
        SubQueryExpression<Integer> sq2 = select(AbstractSQLTest.cat.id.min()).from(AbstractSQLTest.cat);
        List<Integer> list = query().unionAll(sq1, sq2).list();
        Assert.assertFalse(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn({ Target.DERBY, Target.ORACLE })
    public void union5() {
        SAnimal cat2 = new SAnimal("cat2");
        List<Tuple> rows = query().union(select(AbstractSQLTest.cat.id, cat2.id).from(AbstractSQLTest.cat).join(cat2).on(cat2.id.eq(AbstractSQLTest.cat.id.add(1))), select(AbstractSQLTest.cat.id, cat2.id).from(AbstractSQLTest.cat).join(cat2).on(cat2.id.eq(AbstractSQLTest.cat.id.add(1)))).list();
        Assert.assertEquals(5, rows.size());
        for (Tuple row : rows) {
            int first = row.get(AbstractSQLTest.cat.id);
            int second = row.get(cat2.id);
            Assert.assertEquals((first + 1), second);
        }
    }

    @Test
    public void unique_result() {
        Assert.assertEquals(1, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).limit(1).select(AbstractSQLTest.cat.id).fetchOne().intValue());
    }

    @Test
    public void unique_result_multiple() {
        Assert.assertEquals(1, query().from(AbstractSQLTest.cat).orderBy(AbstractSQLTest.cat.id.asc()).limit(1).select(new Expression<?>[]{ AbstractSQLTest.cat.id }).fetchOne().get(AbstractSQLTest.cat.id).intValue());
    }

    @Test
    @ExcludeIn(Target.H2)
    public void wildcard() {
        List<Tuple> rows = query().from(AbstractSQLTest.cat).select(all()).fetch();
        Assert.assertEquals(6, rows.size());
        print(rows);
        // rows = query().from(cat).fetch(cat.id, cat.all());
        // assertEquals(6, rows.size());
        // print(rows);
    }
}


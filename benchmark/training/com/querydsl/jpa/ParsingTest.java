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


import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Catalog;
import com.querydsl.jpa.domain.Color;
import com.querydsl.jpa.domain.Customer;
import com.querydsl.jpa.domain.DomesticCat;
import com.querydsl.jpa.domain.Payment;
import com.querydsl.jpa.domain.Product;
import com.querydsl.jpa.domain.QAccount;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class ParsingTest extends AbstractQueryTest {
    @Test
    public void basic() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat, Constants.fatcat).select(Constants.cat.name, Constants.fatcat.name).parse();
    }

    @Test
    public void beforeAndAfter() throws RecognitionException, TokenStreamException {
        ComparableExpression<Date> ed = Constants.catalog.effectiveDate;
        query().from(Constants.catalog).where(ed.gt(DateExpression.currentDate()), ed.goe(DateExpression.currentDate()), ed.lt(DateExpression.currentDate()), ed.loe(DateExpression.currentDate())).select(Constants.catalog).parse();
    }

    @Test
    @ExcludeIn(ORACLE)
    public void complexConstructor() throws Exception {
        query().from(Constants.bar).select(new QFooDTO(Constants.bar.count())).parse();
    }

    @Test
    public void docoExamples910() throws Exception {
        query().from(Constants.cat).groupBy(Constants.cat.color).select(Constants.cat.color, Constants.cat.weight.sum(), Constants.cat.count()).parse();
    }

    @Test
    public void docoExamples910_2() throws Exception {
        query().from(Constants.cat).groupBy(Constants.cat.color).having(Constants.cat.color.in(Color.TABBY, Color.BLACK)).select(Constants.cat.color, Constants.cat.weight.sum(), Constants.cat.count()).parse();
    }

    @Test
    public void docoExamples911() throws Exception {
        query().from(Constants.fatcat).where(Constants.fatcat.weight.gt(JPAExpressions.select(Constants.cat.weight.avg()).from(Constants.cat))).parse();
    }

    @Test
    public void docoExamples911_2() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.eqAny(JPAExpressions.select(Constants.name.nickName).from(Constants.name))).parse();
    }

    @Test
    public void docoExamples911_3() throws Exception {
        query().from(Constants.cat).where(JPAExpressions.select(Constants.mate).from(Constants.mate).where(Constants.mate.mate.eq(Constants.cat)).notExists()).parse();
    }

    @Test
    public void docoExamples911_4() throws Exception {
        query().from(Constants.cat).where(JPAExpressions.selectFrom(Constants.mate).where(Constants.mate.mate.eq(Constants.cat)).exists()).parse();
    }

    @Test
    public void docoExamples911_5() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.notIn(JPAExpressions.select(Constants.name.nickName).from(Constants.name))).parse();
    }

    @Test
    public void docoExamples912() throws Exception {
        query().from(Constants.ord, Constants.cust).join(Constants.ord.lineItems, Constants.item).join(Constants.item.product, Constants.product).from(Constants.catalog).join(Constants.catalog.prices, Constants.price).where(Constants.ord.paid.not().and(Constants.ord.customer.eq(Constants.cust)).and(Constants.price.product.eq(Constants.product)).and(Constants.catalog.effectiveDate.gt(DateExpression.currentDate())).and(Constants.catalog.effectiveDate.gtAny(JPAExpressions.select(Constants.catalog.effectiveDate).from(Constants.catalog).where(Constants.catalog.effectiveDate.lt(DateExpression.currentDate()))))).groupBy(Constants.ord).having(Constants.price.amount.sum().gt(0L)).orderBy(Constants.price.amount.sum().desc()).select(Constants.ord.id, Constants.price.amount.sum(), Constants.item.count());
        Customer c1 = new Customer();
        Catalog c2 = new Catalog();
        query().from(Constants.ord).join(Constants.ord.lineItems, Constants.item).join(Constants.item.product, Constants.product).from(Constants.catalog).join(Constants.catalog.prices, Constants.price).where(Constants.ord.paid.not().and(Constants.ord.customer.eq(c1)).and(Constants.price.product.eq(Constants.product)).and(Constants.catalog.eq(c2))).groupBy(Constants.ord).having(Constants.price.amount.sum().gt(0L)).orderBy(Constants.price.amount.sum().desc()).select(Constants.ord.id, Constants.price.amount.sum(), Constants.item.count());
    }

    @Test
    public void docoExamples92() throws Exception {
        query().from(Constants.cat).parse();
    }

    @Test
    public void docoExamples92_2() throws Exception {
        query().from(Constants.cat).parse();
    }

    @Test
    public void docoExamples92_3() throws Exception {
        query().from(Constants.form, Constants.param).parse();
    }

    @Test
    public void docoExamples93() throws Exception {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).leftJoin(Constants.cat.kittens, Constants.kitten).parse();
    }

    @Test
    public void docoExamples93_2() throws Exception {
        query().from(Constants.cat).leftJoin(Constants.cat.mate.kittens, Constants.kitten).parse();
    }

    @Test
    public void docoExamples93_3() throws Exception {
        query().from(Constants.cat).join(Constants.cat.mate, Constants.mate).leftJoin(Constants.cat.kittens, Constants.kitten).parse();
    }

    @Test
    public void docoExamples93_4() throws Exception {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).leftJoin(Constants.cat.kittens, Constants.kitten).parse();
    }

    @Test
    public void docoExamples93_viaAlias() throws Exception {
        Cat c = alias(Cat.class, "cat");
        Cat k = alias(Cat.class, "kittens");
        Cat m = alias(Cat.class, "mate");
        query().from($(c)).innerJoin($(c.getMate()), $(m)).leftJoin($(c.getKittens()), $(k)).parse();
    }

    @Test
    public void docoExamples93_viaAlias2() throws Exception {
        Cat c = alias(Cat.class, "cat");
        Cat k = alias(Cat.class, "kittens");
        query().from($(c)).leftJoin($(c.getMate().getKittens()), $(k)).parse();
    }

    @Test
    public void docoExamples93_viaAlias3() throws Exception {
        Cat c = alias(Cat.class, "cat");
        Cat k = alias(Cat.class, "kittens");
        Cat m = alias(Cat.class, "mate");
        query().from($(c)).innerJoin($(c.getMate()), $(m)).leftJoin($(c.getKittens()), $(k)).parse();
    }

    @Test
    public void docoExamples93_viaAlias4() throws Exception {
        Cat c = alias(Cat.class, "cat");
        Cat k = alias(Cat.class, "kittens");
        Cat m = alias(Cat.class, "mate");
        query().from($(c)).innerJoin($(c.getMate()), $(m)).leftJoin($(c.getKittens()), $(k)).parse();
    }

    @Test
    public void docoExamples94() throws Exception {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).select(Constants.cat.mate).parse();
    }

    @Test
    public void docoExamples94_2() throws Exception {
        query().from(Constants.cat).select(Constants.cat.mate).parse();
    }

    @Test
    @NoOpenJPA
    @NoBatooJPA
    public void docoExamples94_3() throws Exception {
        query().from(Constants.cat).select(Constants.cat.kittens).parse();
    }

    @Test
    public void docoExamples94_4() throws Exception {
        query().from(Constants.cust).select(Constants.cust.name.firstName).parse();
    }

    @Test
    public void docoExamples94_5() throws Exception {
        query().from(Constants.mother).innerJoin(Constants.mother.mate, Constants.mate).leftJoin(Constants.mother.kittens, Constants.offspr).select(Constants.mother, Constants.offspr, Constants.mate).parse();
    }

    @Test
    public void docoExamples94_6() throws Exception {
        query().from(Constants.mother).innerJoin(Constants.mother.mate, Constants.mate).leftJoin(Constants.mother.kittens, Constants.kitten).select(new QFamily(Constants.mother, Constants.mate, Constants.kitten)).parse();
    }

    @Test
    public void docoExamples95() throws Exception {
        query().from(Constants.cat).select(Constants.cat.weight.avg(), Constants.cat.weight.sum(), Constants.cat.weight.max(), Constants.cat.count()).parse();
    }

    @Test
    public void docoExamples96() throws Exception {
        query().from(Constants.cat).parse();
    }

    @Test
    public void docoExamples96_2() throws Exception {
        query().from(Constants.m, Constants.n).where(Constants.n.name.eq(Constants.m.name)).parse();
    }

    @Test
    @ExcludeIn(ORACLE)
    public void docoExamples97() throws Exception {
        query().from(Constants.foo, Constants.bar).where(Constants.foo.startDate.eq(Constants.bar.date)).select(Constants.foo).parse();
    }

    @Test
    public void docoExamples97_2() throws Exception {
        query().from(Constants.cat).where(Constants.cat.mate.name.isNotNull()).parse();
    }

    @Test
    public void docoExamples97_3() throws Exception {
        query().from(Constants.cat, Constants.rival).where(Constants.cat.mate.eq(Constants.rival.mate)).parse();
    }

    @Test
    public void docoExamples97_4() throws Exception {
        query().from(Constants.cat, Constants.mate).where(Constants.cat.mate.eq(Constants.mate)).select(Constants.cat, Constants.mate).parse();
    }

    @Test
    public void docoExamples97_5() throws Exception {
        query().from(Constants.cat).where(Constants.cat.id.eq(123)).parse();
    }

    @Test
    public void docoExamples97_6() throws Exception {
        query().from(Constants.cat).where(Constants.cat.mate.id.eq(69)).parse();
    }

    @Test
    public void docoExamples97_7() throws Exception {
        query().from(Constants.person).where(Constants.person.pid.country.eq("AU"), Constants.person.pid.medicareNumber.eq(123456)).parse();
    }

    @Test
    public void docoExamples97_8() throws Exception {
        query().from(Constants.account).where(Constants.account.owner.pid.medicareNumber.eq(123456)).parse();
    }

    @Test
    public void docoExamples97_9() throws Exception {
        query().from(Constants.cat).where(Constants.cat.instanceOf(DomesticCat.class)).parse();
    }

    @Test
    public void docoExamples97_10_2() throws Exception {
        query().from(Constants.log, Constants.payment).innerJoin(Constants.log.item, Constants.item).where(Constants.item.instanceOf(Payment.class), Constants.item.id.eq(Constants.payment.id)).parse();
    }

    @Test
    public void docoExamples98_1() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.between("A", "B")).parse();
    }

    @Test
    public void docoExamples98_2() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.in("Foo", "Bar", "Baz")).parse();
    }

    @Test
    public void docoExamples98_3() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.notBetween("A", "B")).parse();
    }

    @Test
    public void docoExamples98_4() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.notIn("Foo", "Bar", "Baz")).parse();
    }

    @Test
    public void docoExamples98_5() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().gt(0)).parse();
    }

    @Test
    public void docoExamples98_6() throws Exception {
        query().from(Constants.mother, Constants.kit).select(Constants.mother).where(Constants.kit.in(Constants.mother.kittens)).parse();
    }

    @Test
    @NoEclipseLink
    public void docoExamples98_7() throws Exception {
        query().from(Constants.list, Constants.p).select(Constants.p).where(Constants.p.name.eqAny(Constants.list.names)).parse();
    }

    @Test
    public void docoExamples98_8() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.isNotEmpty()).parse();
    }

    @Test
    public void docoExamples98_9() throws Exception {
        query().from(Constants.person, Constants.calendar).select(Constants.person).where(Constants.calendar.holidays("national holiday").eq(Constants.person.birthDay), Constants.person.nationality.calendar.eq(Constants.calendar)).parse();
    }

    @Test
    @ExcludeIn({ DERBY, HSQLDB, ORACLE })
    public void docoExamples98_10() throws Exception {
        query().from(Constants.item, Constants.ord).select(Constants.item).where(Constants.ord.items(Constants.ord.deliveredItemIndices(0)).eq(Constants.item), Constants.ord.id.eq(1L)).parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    @ExcludeIn({ DERBY, HSQLDB, ORACLE })
    public void docoExamples98_12() throws Exception {
        query().from(Constants.prod, Constants.store).innerJoin(Constants.store.customers, Constants.cust).select(Constants.cust).where(Constants.prod.name.eq("widget"), Constants.store.location.name.in("Melbourne", "Sydney"), Constants.prod.eqAll(Constants.cust.currentOrder.lineItems)).parse();
    }

    @Test
    public void docoExamples98() throws Exception {
        Constants.prod.eq(new Product());
        Constants.prod.eq(new QProduct("p"));
        Constants.prod.eq(new QItem("p"));
    }

    @Test
    public void docoExamples99() throws Exception {
        query().from(Constants.cat).orderBy(Constants.cat.name.asc(), Constants.cat.weight.desc(), Constants.cat.birthdate.asc()).parse();
    }

    @Test
    public void doubleLiteral() throws Exception {
        query().from(Constants.cat).where(Constants.cat.weight.lt(((int) (3.1415)))).parse();
    }

    @Test
    public void doubleLiteral2() throws Exception {
        query().from(Constants.cat).where(Constants.cat.weight.gt(((int) (3141.5)))).parse();
    }

    @Test
    @NoOpenJPA
    public void fetch() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).fetchJoin().parse();
    }

    @Test
    @NoOpenJPA
    public void fetch2() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).fetchJoin().fetchJoin().parse();
    }

    @Test
    public void in() throws Exception {
        query().from(Constants.foo).where(Constants.foo.bar.in("a", "b", "c")).parse();
    }

    @Test
    public void notIn() throws Exception {
        query().from(Constants.foo).where(Constants.foo.bar.notIn("a", "b", "c")).parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    public void joinFlags1() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).fetchAll().parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    public void joinFlags2() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).fetchAll().from(Constants.cat1).fetchAll().parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @NoBatooJPA
    public void joinFlags3() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).fetchAll().from(Constants.cat1).fetchAll().parse();
    }

    @Test
    public void joins() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).join(Constants.cat.mate, Constants.mate).select(Constants.cat).parse();
    }

    @Test
    public void innerJoin() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).innerJoin(Constants.cat.mate, Constants.mate).select(Constants.cat).parse();
    }

    @Test
    public void leftJoin() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).leftJoin(Constants.cat.mate, Constants.mate).select(Constants.cat).parse();
    }

    @Test
    @NoOpenJPA
    @NoBatooJPA
    public void joins2() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).join(Constants.cat.mate, Constants.mate).on(Constants.mate.name.eq("Bob")).parse();
    }

    @Test
    public void multipleFromClasses() throws Exception {
        query().from(Constants.qat, Constants.foo).parse();
    }

    @Test
    public void serialization() {
        QueryHelper query = query();
        query.from(Constants.cat);
        Assert.assertEquals("select cat\nfrom Cat cat", query.toString());
        query.from(Constants.fatcat);
        Assert.assertEquals("select cat\nfrom Cat cat, Cat fatcat", query.toString());
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @ExcludeIn(MYSQL)
    public void casts_byte() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.byteValue()).parse();
    }

    @Test
    @NoOpenJPA
    public void casts_double() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.doubleValue()).parse();
    }

    @Test
    @NoOpenJPA
    @ExcludeIn(MYSQL)
    public void casts_float() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.floatValue()).parse();
    }

    @Test
    @NoOpenJPA
    @ExcludeIn(MYSQL)
    public void casts_int() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.intValue()).parse();
    }

    @Test
    @NoOpenJPA
    @ExcludeIn({ DERBY, HSQLDB, MYSQL })
    public void casts_long() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.longValue()).parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @ExcludeIn(MYSQL)
    public void casts_short() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.shortValue()).parse();
    }

    @Test
    @NoOpenJPA
    @ExcludeIn({ DERBY, HSQLDB, MYSQL })
    public void casts_string() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.stringValue()).parse();
    }

    @Test
    @NoEclipseLink
    @NoOpenJPA
    @ExcludeIn(MYSQL)
    public void casts_2() throws Exception {
        NumberExpression<Double> bw = Constants.cat.bodyWeight;
        query().from(Constants.cat).select(bw.castToNum(Byte.class)).parse();
    }

    @Test
    public void not() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().lt(1).not()).parse();
    }

    @Test
    public void not_2() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().gt(1).not()).parse();
    }

    @Test
    public void not_3() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().goe(1).not()).parse();
    }

    @Test
    public void not_4() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().loe(1).not()).parse();
    }

    @Test
    public void not_5() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.between("A", "B").not()).parse();
    }

    @Test
    public void not_6() throws Exception {
        query().from(Constants.cat).where(Constants.cat.name.notBetween("A", "B").not()).parse();
    }

    @Test
    public void not_7() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().loe(1).not().not()).parse();
    }

    @Test
    public void not_8() throws Exception {
        query().from(Constants.cat).where(Constants.cat.kittens.size().loe(1).not().not().not()).parse();
    }

    @Test
    @NoOpenJPA
    public void orderBy_2() throws Exception {
        query().from(Constants.an).orderBy(Constants.an.bodyWeight.sqrt().divide(2.0).asc()).parse();
    }

    @Test
    public void select1() throws Exception {
        // query().select(Ops.AggOps.COUNT_ALL_AGG_EXPR).from(qat).parse();
        query().from(Constants.qat).select(Constants.qat.weight.avg()).parse();
    }

    @Test
    public void sum_3() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).where(Constants.cat.kittens.isEmpty()).select(Constants.cat).parse();
    }

    @Test
    public void sum_4() throws RecognitionException, TokenStreamException {
        query().from(Constants.cat).where(Constants.cat.kittens.isNotEmpty()).select(Constants.cat).parse();
    }

    @Test
    public void where() throws Exception {
        query().from(Constants.qat).where(Constants.qat.name.in("crater", "bean", "fluffy")).parse();
    }

    @Test
    public void where_2() throws Exception {
        query().from(Constants.qat).where(Constants.qat.name.notIn("crater", "bean", "fluffy")).parse();
    }

    @Test
    public void where_3() throws Exception {
        query().from(Constants.an).where(Constants.an.bodyWeight.sqrt().gt(10.0)).parse();
    }

    @Test
    public void where_4() throws Exception {
        query().from(Constants.an).where(Constants.an.bodyWeight.sqrt().divide(2.0).gt(10.0)).parse();
    }

    @Test
    public void where_5() throws Exception {
        query().from(Constants.an).where(Constants.an.bodyWeight.gt(10), Constants.an.bodyWeight.lt(100).or(Constants.an.bodyWeight.isNull())).parse();
    }
}


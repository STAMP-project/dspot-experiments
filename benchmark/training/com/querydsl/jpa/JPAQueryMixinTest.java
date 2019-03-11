package com.querydsl.jpa;


import OrderSpecifier.NullHandling.NullsLast;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.QDepartment;
import com.querydsl.jpa.domain.QEmployee;
import com.querydsl.jpa.domain4.QBookMark;
import com.querydsl.jpa.domain4.QBookVersion;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class JPAQueryMixinTest {
    private JPAQueryMixin<?> mixin = new JPAQueryMixin<Object>();

    @Test
    public void where_null() {
        mixin.where(((Predicate) (null)));
    }

    @Test
    public void orderBy() {
        QCat cat = QCat.cat;
        QCat catMate = new QCat("cat_mate");
        mixin.from(cat);
        mixin.orderBy(cat.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate))), md.getJoins());
        Assert.assertEquals(Arrays.asList(catMate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_nonRoot_twice() {
        QDepartment department = QDepartment.department;
        QCompany departmentCompany = new QCompany("department_company");
        QEmployee departmentCompanyCeo = new QEmployee("department_company_ceo");
        mixin.from(department);
        mixin.orderBy(department.company.ceo.firstName.asc(), department.company.ceo.lastName.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, department), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, department.company.as(departmentCompany)), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, departmentCompany.ceo.as(departmentCompanyCeo))), md.getJoins());
        Assert.assertEquals(Arrays.asList(departmentCompanyCeo.firstName.asc(), departmentCompanyCeo.lastName.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_where() {
        QCat cat = QCat.cat;
        mixin.from(cat);
        mixin.where(cat.mate.name.isNotNull());
        mixin.orderBy(cat.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat)), md.getJoins());
        Assert.assertEquals(Arrays.asList(cat.mate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_groupBy() {
        QCat cat = QCat.cat;
        mixin.from(cat);
        mixin.groupBy(cat.mate.name);
        mixin.orderBy(cat.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat)), md.getJoins());
        Assert.assertEquals(Arrays.asList(cat.mate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_operation() {
        QCat cat = QCat.cat;
        QCat catMate = new QCat("cat_mate");
        mixin.from(cat);
        mixin.orderBy(cat.mate.name.lower().asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate))), md.getJoins());
        Assert.assertEquals(Arrays.asList(catMate.name.lower().asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_long() {
        QCat cat = QCat.cat;
        QCat catMate = new QCat("cat_mate");
        QCat catMateMate = new QCat("cat_mate_mate");
        mixin.from(cat);
        mixin.orderBy(cat.mate.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.mate.as(catMate)), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, catMate.mate.as(catMateMate))), md.getJoins());
        Assert.assertEquals(Arrays.asList(catMateMate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_reuse() {
        QCat cat = QCat.cat;
        QCat mate = new QCat("mate");
        mixin.from(cat);
        mixin.leftJoin(cat.mate, mate);
        mixin.orderBy(cat.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.mate.as(mate))), md.getJoins());
        Assert.assertEquals(Arrays.asList(mate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_long_reuse() {
        QCat cat = QCat.cat;
        QCat mate = new QCat("mate");
        QCat mateMate = new QCat("mate_mate");
        mixin.from(cat);
        mixin.leftJoin(cat.mate, mate);
        mixin.orderBy(cat.mate.mate.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.mate.as(mate)), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, mate.mate.as(mateMate))), md.getJoins());
        Assert.assertEquals(Arrays.asList(mateMate.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_any() {
        QCat cat = QCat.cat;
        QCat catKittens = new QCat("cat_kittens");
        mixin.from(cat);
        mixin.orderBy(cat.kittens.any().name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, cat), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, cat.kittens.as(catKittens))), md.getJoins());
        Assert.assertEquals(Arrays.asList(catKittens.name.asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_embeddable() {
        QBookVersion bookVersion = QBookVersion.bookVersion;
        mixin.from(bookVersion);
        mixin.orderBy(bookVersion.definition.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, bookVersion)), md.getJoins());
        Assert.assertEquals(Arrays.asList(bookVersion.definition.name.asc()), md.getOrderBy());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void orderBy_embeddable2() {
        QArticle article = QArticle.article;
        QArticle articleContentArticle = new QArticle("article_content_article");
        mixin.from(article);
        mixin.orderBy(article.content.article.name.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, article), new com.querydsl.core.JoinExpression(JoinType.LEFTJOIN, as(articleContentArticle))), md.getJoins());
        Assert.assertEquals(Arrays.asList(articleContentArticle.name.asc()), md.getOrderBy());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void orderBy_embeddable_collection() {
        QBookVersion bookVersion = QBookVersion.bookVersion;
        QBookMark bookMark = new QBookMark("bookVersion_definition_bookMarks");
        mixin.from(bookVersion);
        mixin.orderBy(bookVersion.definition.bookMarks.any().comment.asc());
        QueryMetadata md = mixin.getMetadata();
        Assert.assertEquals(Arrays.asList(new com.querydsl.core.JoinExpression(JoinType.DEFAULT, bookVersion)), md.getJoins());
        Assert.assertEquals(Arrays.asList(Expressions.stringPath(bookVersion.definition.bookMarks, "comment").asc()), md.getOrderBy());
    }

    @Test
    public void orderBy_nullsLast() {
        QCat cat = QCat.cat;
        mixin.from(cat);
        mixin.orderBy(cat.mate.name.asc().nullsLast());
        Assert.assertEquals(NullsLast, mixin.getMetadata().getOrderBy().get(0).getNullHandling());
    }
}


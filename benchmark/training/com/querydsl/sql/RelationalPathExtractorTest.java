package com.querydsl.sql;


import com.google.common.collect.ImmutableSet;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.domain.QEmployee;
import org.junit.Assert;
import org.junit.Test;


public class RelationalPathExtractorTest {
    @Test
    public void simpleQuery() {
        QEmployee employee2 = new QEmployee("employee2");
        SQLQuery<?> query = query().from(Constants.employee, employee2);
        Assert.assertEquals(ImmutableSet.of(Constants.employee, employee2), RelationalPathExtractor.extract(query.getMetadata()));
    }

    @Test
    public void joins() {
        QEmployee employee2 = new QEmployee("employee2");
        SQLQuery<?> query = query().from(Constants.employee).innerJoin(employee2).on(Constants.employee.superiorId.eq(employee2.id));
        Assert.assertEquals(ImmutableSet.of(Constants.employee, employee2), RelationalPathExtractor.extract(query.getMetadata()));
    }

    @Test
    public void subQuery() {
        SQLQuery<?> query = query().from(Constants.employee).where(Constants.employee.id.eq(query().from(Constants.employee).select(Constants.employee.id.max())));
        Assert.assertEquals(ImmutableSet.of(Constants.employee), RelationalPathExtractor.extract(query.getMetadata()));
    }

    @Test
    public void subQuery2() {
        QEmployee employee2 = new QEmployee("employee2");
        SQLQuery<?> query = query().from(Constants.employee).where(Expressions.list(Constants.employee.id, Constants.employee.lastname).in(query().from(employee2).select(employee2.id, employee2.lastname)));
        Assert.assertEquals(ImmutableSet.of(Constants.employee, employee2), RelationalPathExtractor.extract(query.getMetadata()));
    }
}


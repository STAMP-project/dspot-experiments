package com.querydsl.sql;


import com.querydsl.core.types.dsl.Coalesce;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CoalesceTest {
    @Test
    public void coalesce_supports_subquery() {
        Coalesce<String> coalesce = new Coalesce<String>(SQLExpressions.select(QCompanies.companies.name).from(QCompanies.companies), QCompanies.companies.name);
        Assert.assertThat(SQLExpressions.select(coalesce).toString(), Matchers.is(Matchers.equalTo("select coalesce((select COMPANIES.NAME\nfrom COMPANIES COMPANIES), COMPANIES.NAME)\nfrom dual")));
    }
}


package com.querydsl.sql;


import com.querydsl.core.types.dsl.Param;
import com.querydsl.sql.domain.QSurvey;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class SQLBindingsTest {
    private QSurvey survey = QSurvey.survey;

    private SQLQuery<?> query = new SQLQuery<Void>();

    @Test
    public void empty() {
        SQLBindings bindings = query.getSQL();
        Assert.assertEquals("\nfrom dual", bindings.getSQL());
        Assert.assertTrue(bindings.getBindings().isEmpty());
    }

    @Test
    public void singleArg() {
        query.from(survey).where(survey.name.eq("Bob")).select(survey.id);
        SQLBindings bindings = query.getSQL();
        Assert.assertEquals("select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ?", bindings.getSQL());
        Assert.assertEquals(Arrays.asList("Bob"), bindings.getBindings());
    }

    @Test
    public void twoArgs() {
        query.from(survey).where(survey.name.eq("Bob"), survey.name2.eq("A")).select(survey.id);
        SQLBindings bindings = query.getSQL();
        Assert.assertEquals("select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ? and SURVEY.NAME2 = ?", bindings.getSQL());
        Assert.assertEquals(Arrays.asList("Bob", "A"), bindings.getBindings());
    }

    @Test
    public void params() {
        Param<String> name = new Param<String>(String.class, "name");
        query.from(survey).where(survey.name.eq(name), survey.name2.eq("A")).select(survey.id);
        query.set(name, "Bob");
        SQLBindings bindings = query.getSQL();
        Assert.assertEquals("select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ? and SURVEY.NAME2 = ?", bindings.getSQL());
        Assert.assertEquals(Arrays.asList("Bob", "A"), bindings.getBindings());
    }
}


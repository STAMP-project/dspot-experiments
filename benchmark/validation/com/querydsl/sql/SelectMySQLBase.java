package com.querydsl.sql;


import com.querydsl.core.Target;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;


public class SelectMySQLBase extends AbstractBaseTest {
    @Test
    @IncludeIn(Target.MYSQL)
    public void mysql_extensions() {
        mysqlQuery().from(Constants.survey).bigResult().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).bufferResult().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).cache().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).calcFoundRows().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).noCache().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).highPriority().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).lockInShareMode().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).smallResult().select(Constants.survey.id).fetch();
        mysqlQuery().from(Constants.survey).straightJoin().select(Constants.survey.id).fetch();
    }
}


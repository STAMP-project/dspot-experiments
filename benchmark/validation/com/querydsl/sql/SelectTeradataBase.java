package com.querydsl.sql;


import com.querydsl.core.Target;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;


public class SelectTeradataBase extends AbstractBaseTest {
    @Test
    @IncludeIn(Target.TERADATA)
    public void setQueryBand_forSession() {
        setQueryBand().set("a", "bb").forSession().execute();
        query().from(Constants.survey).select(Constants.survey.id).fetch();
    }

    @Test
    @IncludeIn(Target.TERADATA)
    public void setQueryBand_forTransaction() {
        setQueryBand().set("a", "bb").forTransaction().execute();
        query().from(Constants.survey).select(Constants.survey.id).fetch();
    }
}


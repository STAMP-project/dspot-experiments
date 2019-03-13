package com.querydsl.sql;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.sql.domain.QSurvey;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class RelationalPathTest {
    @Test
    public void path() throws IOException, ClassNotFoundException {
        QSurvey survey = QSurvey.survey;
        QSurvey survey2 = serialize(survey);
        Assert.assertEquals(Arrays.asList(all()), Arrays.asList(all()));
        Assert.assertEquals(getMetadata(), getMetadata());
        Assert.assertEquals(survey.getMetadata(survey.id), survey2.getMetadata(survey.id));
    }

    @Test
    public void in_tuple() throws IOException, ClassNotFoundException {
        // (survey.id, survey.name)
        QSurvey survey = QSurvey.survey;
        QTuple tuple = Projections.tuple(survey.id, survey.name);
        serialize(tuple);
        serialize(tuple.newInstance(1, "a"));
    }
}


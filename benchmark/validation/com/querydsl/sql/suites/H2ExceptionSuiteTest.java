package com.querydsl.sql.suites;


import com.querydsl.core.QueryException;
import com.querydsl.core.testutil.H2;
import com.querydsl.sql.AbstractBaseTest;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static DefaultSQLExceptionTranslator.DEFAULT;


@Category(H2.class)
public class H2ExceptionSuiteTest extends AbstractBaseTest {
    private static final SQLExceptionTranslator exceptionTranslator = DEFAULT;

    @Test
    public void sQLExceptionCreationTranslated() {
        SQLException e1 = new SQLException("Exception #1", "42001", 181);
        SQLException e2 = new SQLException("Exception #2", "HY000", 1030);
        e1.setNextException(e2);
        SQLException sqlException = new SQLException("Batch operation failed");
        sqlException.setNextException(e1);
        RuntimeException result = H2ExceptionSuiteTest.exceptionTranslator.translate(sqlException);
        inspectExceptionResult(result);
    }

    @Test
    public void updateBatchFailed() {
        execute(insert(QSurvey.survey).columns(QSurvey.survey.name, QSurvey.survey.name2).values("New Survey", "New Survey"));
        Exception result = null;
        try {
            execute(update(QSurvey.survey).set(QSurvey.survey.id, 1).addBatch().set(QSurvey.survey.id, 2).addBatch());
        } catch (QueryException e) {
            result = e;
        }
        Assert.assertNotNull(result);
        inspectExceptionResult(result);
    }
}


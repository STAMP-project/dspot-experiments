package org.nlpcn.es4sql;


import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;


/**
 * Created by Eliran on 21/8/2016.
 */
public class MultiQueryTests {
    private static String MINUS_SCROLL_DEFAULT_HINT = " /*! MINUS_SCROLL_FETCH_AND_RESULT_LIMITS(1000,50,100) */ ";

    private static String MINUS_TERMS_OPTIMIZATION_HINT = " /*! MINUS_USE_TERMS_OPTIMIZATION(true)*/ ";

    @Test
    public void unionAllSameRequestOnlyOneRecordTwice() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format("SELECT firstname FROM %s/account WHERE firstname = 'Amber' limit 1 union all SELECT firstname FROM %s/account WHERE firstname = 'Amber'", TestsConstants.TEST_INDEX_ACCOUNT, TestsConstants.TEST_INDEX_ACCOUNT);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals(2, searchHits.length);
        for (SearchHit hit : searchHits) {
            Object firstname = hit.getSourceAsMap().get("firstname");
            Assert.assertEquals("Amber", firstname);
        }
    }

    @Test
    public void unionAllOnlyOneRecordEachWithAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("SELECT firstname FROM %s/account WHERE firstname = 'Amber' " + ("union all " + "SELECT dog_name as firstname FROM %s/dog WHERE dog_name = 'rex'")), TestsConstants.TEST_INDEX_ACCOUNT, TestsConstants.TEST_INDEX_DOG);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals(2, searchHits.length);
        Set<String> names = new HashSet<>();
        for (SearchHit hit : searchHits) {
            Object firstname = hit.getSourceAsMap().get("firstname");
            names.add(firstname.toString());
        }
        Assert.assertTrue("names should contain Amber", names.contains("Amber"));
        Assert.assertTrue("names should contain rex", names.contains("rex"));
    }

    @Test
    public void unionAllOnlyOneRecordEachWithComplexAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("SELECT firstname FROM %s/account WHERE firstname = 'Amber' " + ("union all " + "SELECT name.firstname as firstname FROM %s/gotCharacters WHERE name.firstname = 'Daenerys'")), TestsConstants.TEST_INDEX_ACCOUNT, TestsConstants.TEST_INDEX_GAME_OF_THRONES);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals(2, searchHits.length);
        Set<String> names = new HashSet<>();
        for (SearchHit hit : searchHits) {
            Object firstname = hit.getSourceAsMap().get("firstname");
            names.add(firstname.toString());
        }
        Assert.assertTrue("names should contain Amber", names.contains("Amber"));
        Assert.assertTrue("names should contain Daenerys", names.contains("Daenerys"));
    }

    @Test
    public void minusAMinusANoAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinusAMinusANoAlias("");
    }

    @Test
    public void minusAMinusANoAliasWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinusAMinusANoAlias(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusAMinusANoAliasWithScrollingAndTerms() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinusAMinusANoAlias(((MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT) + (MultiQueryTests.MINUS_TERMS_OPTIMIZATION_HINT)));
    }

    @Test
    public void minusAMinusBNoAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_AMinusBNoAlias("");
    }

    @Test
    public void minusAMinusBNoAliasWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_AMinusBNoAlias(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusAMinusBNoAliasWithScrollingAndTerms() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_AMinusBNoAlias(((MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT) + (MultiQueryTests.MINUS_TERMS_OPTIMIZATION_HINT)));
    }

    @Test
    public void minusCMinusDTwoFieldsNoAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusDTwoFieldsNoAlias("");
    }

    @Test
    public void minusCMinusDTwoFieldsNoAliasWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusDTwoFieldsNoAlias(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusCMinusDTwoFieldsAliasOnBothSecondTableFields() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("SELECT pk , letter  FROM %s/systems WHERE system_name = 'C' " + ("minus " + "SELECT myId as pk , myLetter as letter FROM %s/systems WHERE system_name = 'E' ")), TestsConstants.TEST_INDEX_SYSTEM, TestsConstants.TEST_INDEX_SYSTEM);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals("not exactly one hit returned", 1, searchHits.length);
        Map<String, Object> sourceAsMap = searchHits[0].getSourceAsMap();
        Assert.assertEquals("source map not contained exactly two fields", 2, sourceAsMap.size());
        Assert.assertTrue("source map should contain pk", sourceAsMap.containsKey("pk"));
        Assert.assertTrue("source map should contain letter", sourceAsMap.containsKey("letter"));
        Assert.assertEquals(1, sourceAsMap.get("pk"));
        Assert.assertEquals("e", sourceAsMap.get("letter"));
    }

    @Test
    public void minusCMinusDTwoFieldsAliasOnBothTables() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusDTwoFieldsAliasOnBothTables("");
    }

    @Test
    public void minusCMinusDTwoFieldsAliasOnBothTablesWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusDTwoFieldsAliasOnBothTables(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusCMinusCTwoFields_OneAlias() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("SELECT pk as myId , letter  FROM %s/systems WHERE system_name = 'C' " + ("minus " + "SELECT pk as myId , letter FROM %s/systems WHERE system_name = 'C' ")), TestsConstants.TEST_INDEX_SYSTEM, TestsConstants.TEST_INDEX_SYSTEM);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals("no hits should be returned", 0, searchHits.length);
    }

    @Test
    public void minusCMinusTNoExistsTwoFields() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("SELECT pk , letter  FROM %s/systems WHERE system_name = 'C' " + ("minus " + "SELECT pk  , letter FROM %s/systems WHERE system_name = 'T' ")), TestsConstants.TEST_INDEX_SYSTEM, TestsConstants.TEST_INDEX_SYSTEM);
        SearchHit[] searchHits = executeAndGetHits(query);
        Assert.assertEquals("all hits should be returned", 3, searchHits.length);
    }

    @Test
    public void minusCMinusTNoExistsOneField() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusTNoExistsOneField("");
    }

    @Test
    public void minusCMinusTNoExistsOneFieldWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusTNoExistsOneField(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusCMinusTNoExistsOneFieldWithScrollingAndOptimization() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_CMinusTNoExistsOneField(((MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT) + (MultiQueryTests.MINUS_TERMS_OPTIMIZATION_HINT)));
    }

    @Test
    public void minusTMinusCNoExistsFirstQuery() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_TMinusCNoExistsFirstQuery("");
    }

    @Test
    public void minusTMinusCNoExistsFirstQueryWithScrolling() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_TMinusCNoExistsFirstQuery(MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT);
    }

    @Test
    public void minusTMinusCNoExistsFirstQueryWithScrollingAndOptimization() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        innerMinus_TMinusCNoExistsFirstQuery(((MultiQueryTests.MINUS_SCROLL_DEFAULT_HINT) + (MultiQueryTests.MINUS_TERMS_OPTIMIZATION_HINT)));
    }
}


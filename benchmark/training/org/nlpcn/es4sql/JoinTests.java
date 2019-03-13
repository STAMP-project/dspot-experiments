package org.nlpcn.es4sql;


import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.List;
import org.elasticsearch.search.SearchHit;
import org.junit.Assert;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;


/**
 * Created by Eliran on 22/8/2015.
 */
public class JoinTests {
    @Test
    public void joinParseCheckSelectedFieldsSplitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinParseCheckSelectedFieldsSplit(false);
    }

    @Test
    public void joinParseCheckSelectedFieldsSplitNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinParseCheckSelectedFieldsSplit(true);
    }

    @Test
    public void joinParseWithHintsCheckSelectedFieldsSplitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = ((((((("SELECT /*! HASH_WITH_TERMS_FILTER*/ a.firstname ,a.lastname , a.gender ,d.dog_name  FROM " + (TestsConstants.TEST_INDEX_PEOPLE)) + "/people a ") + " JOIN ") + (TestsConstants.TEST_INDEX_DOG)) + "/dog d on d.holdersName = a.firstname ") + " WHERE ") + " (a.age > 10 OR a.balance > 2000)") + " AND d.age > 1";
        String explainedQuery = hashJoinRunAndExplain(query);
        boolean containTerms = explainedQuery.replaceAll("\\s+", "").contains("\"terms\":{\"holdersName\":[");
        List<String> holdersName = Arrays.asList("daenerys", "nanette", "virginia", "aurelia", "mcgee", "hattie", "elinor", "burton");
        for (String holderName : holdersName) {
            Assert.assertTrue(("should contain:" + holderName), explainedQuery.contains(holderName));
        }
        Assert.assertTrue(containTerms);
    }

    @Test
    public void joinWithNoWhereButWithConditionHash() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNoWhereButWithCondition(false);
    }

    @Test
    public void joinWithNoWhereButWithConditionNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNoWhereButWithCondition(true);
    }

    @Test
    public void joinWithStarASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithStar(false);
    }

    @Test
    public void joinNoConditionButWithWhereHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionButWithWhere(false);
    }

    @Test
    public void joinNoConditionButWithWhereNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionButWithWhere(true);
    }

    @Test
    public void joinNoConditionAndNoWhereHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionAndNoWhere(false);
    }

    @Test
    public void joinNoConditionAndNoWhereNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionAndNoWhere(true);
    }

    @Test
    public void joinNoConditionAndNoWhereWithTotalLimitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionAndNoWhereWithTotalLimit(false);
    }

    @Test
    public void joinNoConditionAndNoWhereWithTotalLimitNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinNoConditionAndNoWhereWithTotalLimit(true);
    }

    @Test
    public void joinWithNestedFieldsOnReturnHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNestedFieldsOnReturn(false);
    }

    @Test
    public void joinWithNestedFieldsOnReturnNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNestedFieldsOnReturn(true);
    }

    @Test
    public void joinWithAllAliasOnReturnHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllAliasOnReturn(false);
    }

    @Test
    public void joinWithAllAliasOnReturnNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllAliasOnReturn(true);
    }

    @Test
    public void joinWithSomeAliasOnReturnHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithSomeAliasOnReturn(false);
    }

    @Test
    public void joinWithSomeAliasOnReturnNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithSomeAliasOnReturn(true);
    }

    @Test
    public void joinWithNestedFieldsOnComparisonAndOnReturnHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNestedFieldsOnComparisonAndOnReturn(false);
    }

    @Test
    public void joinWithNestedFieldsOnComparisonAndOnReturnNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithNestedFieldsOnComparisonAndOnReturn(true);
    }

    @Test
    public void testLeftJoinHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        testLeftJoin(false);
    }

    @Test
    public void testLeftJoinNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        testLeftJoin(true);
    }

    @Test
    public void hintLimits_firstLimitSecondNullHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondNull(false);
    }

    @Test
    public void hintLimits_firstLimitSecondNullNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondNull(true);
    }

    @Test
    public void hintLimits_firstLimitSecondLimitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondLimit(false);
    }

    @Test
    public void hintLimits_firstLimitSecondLimitNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondLimit(true);
    }

    @Test
    public void hintLimits_firstLimitSecondLimitOnlyOneNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondLimitOnlyOne(true);
    }

    @Test
    public void hintLimits_firstLimitSecondLimitOnlyOneHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstLimitSecondLimitOnlyOne(false);
    }

    @Test
    public void hintLimits_firstNullSecondLimitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstNullSecondLimit(false);
    }

    @Test
    public void hintLimits_firstNullSecondLimitNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        hintLimits_firstNullSecondLimit(true);
    }

    @Test
    public void testLeftJoinWithLimitHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        testLeftJoinWithLimit(false);
    }

    @Test
    public void testLeftJoinWithLimitNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        testLeftJoinWithLimit(true);
    }

    @Test
    public void hintMultiSearchCanRunFewTimesNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("select /*! USE_NL*/ /*! NL_MULTISEARCH_SIZE(2)*/ c.name.firstname,c.parents.father , h.hname,h.words from %s/gotCharacters c " + "JOIN %s/gotCharacters h "), TestsConstants.TEST_INDEX_GAME_OF_THRONES, TestsConstants.TEST_INDEX_GAME_OF_THRONES);
        SearchHit[] hits = joinAndGetHits(query);
        Assert.assertEquals(42, hits.length);
    }

    @Test
    public void joinWithGeoIntersectNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("select p1.description,p2.description from %s/location p1 " + ("JOIN %s/location2 p2 " + "ON GEO_INTERSECTS(p2.place,p1.place)")), TestsConstants.TEST_INDEX_LOCATION, TestsConstants.TEST_INDEX_LOCATION2);
        SearchHit[] hits = joinAndGetHits(query);
        Assert.assertEquals(2, hits.length);
        Assert.assertEquals("squareRelated", hits[0].getSourceAsMap().get("p2.description"));
        Assert.assertEquals("squareRelated", hits[1].getSourceAsMap().get("p2.description"));
    }

    @Test
    public void joinWithInQuery() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("select c.gender ,c.name.firstname, h.hname,h.words from %s/gotCharacters c " + ("JOIN %s/gotCharacters h on h.hname = c.house" + " where c.name.firstname in (select holdersName from %s/dog)")), TestsConstants.TEST_INDEX_GAME_OF_THRONES, TestsConstants.TEST_INDEX_GAME_OF_THRONES, TestsConstants.TEST_INDEX_DOG);
        SearchHit[] hits = joinAndGetHits(query);
        Assert.assertEquals(1, hits.length);
        Assert.assertEquals("Daenerys", hits[0].getSourceAsMap().get("c.name.firstname"));
    }

    @Test
    public void joinWithOrHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithOr(false);
    }

    @Test
    public void joinWithOrNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithOr(true);
    }

    @Test
    public void joinWithOrWithTermsFilterOpt() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        String query = String.format(("select /*! HASH_WITH_TERMS_FILTER*/ d.dog_name , c.name.firstname from %s/gotCharacters c " + ("JOIN %s/dog d on d.holdersName = c.name.firstname" + " OR d.age = c.name.ofHisName")), TestsConstants.TEST_INDEX_GAME_OF_THRONES, TestsConstants.TEST_INDEX_DOG);
        String explainedQuery = hashJoinRunAndExplain(query);
        boolean containsHoldersNamesTerms = explainedQuery.replaceAll("\\s+", "").contains("\"terms\":{\"holdersName\":");
        Assert.assertTrue(containsHoldersNamesTerms);
        List<String> holdersName = Arrays.asList("daenerys", "brandon", "eddard", "jaime");
        for (String holderName : holdersName) {
            Assert.assertTrue(("should contain:" + holderName), explainedQuery.contains(holderName));
        }
        boolean containsAgesTerms = explainedQuery.replaceAll("\\s+", "").contains("\"terms\":{\"age\":");
        Assert.assertTrue(containsAgesTerms);
    }

    @Test
    public void joinWithOrderbyFirstTableHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithOrderFirstTable(false);
    }

    @Test
    public void joinWithOrderbyFirstTableNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithOrderFirstTable(true);
    }

    @Test
    public void joinWithAllFromSecondTableHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllFromSecondTable(false);
    }

    @Test
    public void joinWithAllFromSecondTableNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllFromSecondTable(true);
    }

    @Test
    public void joinWithAllFromFirstTableHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllFromFirstTable(false);
    }

    @Test
    public void joinWithAllFromFirstTableNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        joinWithAllFromFirstTable(true);
    }

    @Test
    public void leftJoinWithAllFromSecondTableHASH() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        leftJoinWithAllFromSecondTable(false);
    }

    @Test
    public void leftJoinWithAllFromSecondTableNL() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        leftJoinWithAllFromSecondTable(true);
    }
}


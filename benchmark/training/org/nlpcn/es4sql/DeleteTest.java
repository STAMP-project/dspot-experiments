package org.nlpcn.es4sql;


import java.sql.SQLFeatureNotSupportedException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;


public class DeleteTest {
    @Test
    public void deleteAllTest() throws SQLFeatureNotSupportedException, SqlParseException {
        delete(String.format("DELETE FROM %s/temp_account", TestsConstants.TEST_INDEX_ACCOUNT_TEMP), TestsConstants.TEST_INDEX_ACCOUNT_TEMP);
        // Assert no results exist for this type.
        SearchRequestBuilder request = MainTestSuite.getClient().prepareSearch(TestsConstants.TEST_INDEX_ACCOUNT_TEMP);
        request.setTypes("temp_account");
        SearchResponse response = request.setQuery(QueryBuilders.matchAllQuery()).get();
        MatcherAssert.assertThat(response.getHits().getTotalHits(), IsEqual.equalTo(0L));
    }

    @Test
    public void deleteWithConditionTest() throws SQLFeatureNotSupportedException, SqlParseException {
        delete(String.format("DELETE FROM %s/phrase WHERE phrase = 'quick fox here' ", TestsConstants.TEST_INDEX_PHRASE), TestsConstants.TEST_INDEX_PHRASE);
        // Assert no results exist for this type.
        SearchRequestBuilder request = MainTestSuite.getClient().prepareSearch(TestsConstants.TEST_INDEX_PHRASE);
        request.setTypes("phrase");
        SearchResponse response = request.setQuery(QueryBuilders.matchAllQuery()).get();
        MatcherAssert.assertThat(response.getHits().getTotalHits(), IsEqual.equalTo(5L));
    }
}


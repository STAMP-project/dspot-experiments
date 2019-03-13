package org.nlpcn.es4sql;


import java.io.IOException;
import java.sql.SQLFeatureNotSupportedException;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;


/**
 * ???????
 *
 * @author ansj
 */
public class MethodQueryTest {
    /**
     * query ???????lucene ??????? ?????????????????? "query" :
     * {query_string" : {"query" : "address:880 Holmes Lane"}
     *
     * @throws IOException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void queryTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SqlElasticSearchRequestBuilder select = ((SqlElasticSearchRequestBuilder) (MainTestSuite.getSearchDao().explain("select address from bank where q= query('address:880 Holmes Lane') limit 3").explain()));
        System.out.println(select);
    }

    /**
     * matchQuery ????????????????? "query" : { "match" : { "address" :
     * {"query":"880 Holmes Lane", "type" : "boolean" } } }
     *
     * @throws IOException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void matchQueryTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SqlElasticSearchRequestBuilder select = ((SqlElasticSearchRequestBuilder) (MainTestSuite.getSearchDao().explain("select address from bank where address= matchQuery('880 Holmes Lane') limit 3").explain()));
        System.out.println(select);
    }

    /**
     * matchQuery ????????????????? "query" : { "bool" : { "must" : { "bool" : {
     * "should" : [ { "constant_score" : { "query" : { "match" : { "address" : {
     * "query" : "Lane", "type" : "boolean" } } }, "boost" : 100.0 } }, {
     * "constant_score" : { "query" : { "match" : { "address" : { "query" :
     * "Street", "type" : "boolean" } } }, "boost" : 0.5 } } ] } } } }
     *
     * @throws IOException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void scoreQueryTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SqlElasticSearchRequestBuilder select = ((SqlElasticSearchRequestBuilder) (MainTestSuite.getSearchDao().explain("select address from bank where address= score(matchQuery('Lane'),100) or address= score(matchQuery('Street'),0.5)  order by _score desc limit 3").explain()));
        System.out.println(select);
    }

    /**
     * wildcardQuery ????????????term ?????? l*e means leae ltae ....
     * "wildcard": { "address" : { "wildcard" : "l*e" } }
     *
     * @throws IOException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void wildcardQueryTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SqlElasticSearchRequestBuilder select = ((SqlElasticSearchRequestBuilder) (MainTestSuite.getSearchDao().explain("select address from bank where address= wildcardQuery('l*e')  order by _score desc limit 3").explain()));
        System.out.println(select);
    }

    /**
     * matchPhraseQueryTest ?????????
     * "address" : {
     * "query" : "671 Bristol Street",
     * "type" : "phrase"
     * }
     *
     * @throws IOException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void matchPhraseQueryTest() throws IOException, SQLFeatureNotSupportedException, SqlParseException {
        SqlElasticSearchRequestBuilder select = ((SqlElasticSearchRequestBuilder) (MainTestSuite.getSearchDao().explain("select address from bank where address= matchPhrase('671 Bristol Street')  order by _score desc limit 3").explain()));
        System.out.println(select);
    }
}


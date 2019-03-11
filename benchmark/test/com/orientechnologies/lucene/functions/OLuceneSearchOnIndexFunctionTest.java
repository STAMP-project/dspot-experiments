package com.orientechnologies.lucene.functions;


import com.orientechnologies.lucene.test.BaseLuceneTest;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Test;


/**
 * Created by frank on 15/01/2017.
 */
public class OLuceneSearchOnIndexFunctionTest extends BaseLuceneTest {
    @Test
    public void shouldSearchOnSingleIndex() throws Exception {
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', 'BELIEVE') = true");
        // resultSet.getExecutionPlan().ifPresent(x -> System.out.println(x.prettyPrint(0, 2)));
        assertThat(resultSet).hasSize(2);
        resultSet.close();
        resultSet = db.query("SELECT from Song where SEARCH_INDEX(\'Song.title\', \"bel*\") = true");
        assertThat(resultSet).hasSize(3);
        resultSet.close();
        resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', 'bel*') = true");
        assertThat(resultSet).hasSize(3);
        resultSet.close();
    }

    @Test
    public void shouldFindNothingOnEmptyQuery() throws Exception {
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', '') = true");
        // resultSet.getExecutionPlan().ifPresent(x -> System.out.println(x.prettyPrint(0, 2)));
        assertThat(resultSet).hasSize(0);
        resultSet.close();
    }

    // @Ignore
    @Test
    public void shouldSearchOnSingleIndexWithLeadingWildcard() throws Exception {
        // TODO: metadata still not used
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', '*EVE*', {'allowLeadingWildcard': true}) = true");
        // resultSet.getExecutionPlan().ifPresent(x -> System.out.println(x.prettyPrint(0, 2)));
        assertThat(resultSet).hasSize(14);
        resultSet.close();
    }

    @Test
    public void shouldSearchOnTwoIndexesInOR() throws Exception {
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', 'BELIEVE') = true OR SEARCH_INDEX('Song.author', 'Bob') = true ");
        assertThat(resultSet).hasSize(41);
        resultSet.close();
    }

    @Test
    public void shouldSearhOnTwoIndexesInAND() throws Exception {
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', 'tambourine') = true AND SEARCH_INDEX('Song.author', 'Bob') = true ");
        assertThat(resultSet).hasSize(1);
        resultSet.close();
    }

    @Test
    public void shouldSearhOnTwoIndexesWithLeadingWildcardInAND() throws Exception {
        OResultSet resultSet = db.query("SELECT from Song where SEARCH_INDEX('Song.title', 'tambourine') = true AND SEARCH_INDEX('Song.author', 'Bob', {'allowLeadingWildcard': true}) = true ");
        assertThat(resultSet).hasSize(1);
        resultSet.close();
    }

    @Test(expected = OCommandExecutionException.class)
    public void shouldFailWithWrongIndexName() throws Exception {
        db.query("SELECT from Song where SEARCH_INDEX('Song.wrongName', 'tambourine') = true ").close();
    }
}


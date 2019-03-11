package com.orientechnologies.lucene.tests;


import com.orientechnologies.lucene.functions.OLuceneFunctionsUtils;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.Test;


/**
 * Created by frank on 09/05/2017.
 */
public class OLuceneMetadataFieldsTest extends OLuceneBaseTest {
    @Test
    public void shouldFetchOnlyFromACluster() throws Exception {
        assertThat(db.getMetadata().getIndexManager().getIndex("Song.title").getSize()).isEqualTo(586);
        int cluster = db.getMetadata().getSchema().getClass("Song").getClusterIds()[1];
        db.commit();
        OResultSet results = db.query((("SELECT FROM Song WHERE search_class('+_CLUSTER:" + cluster) + "')=true "));
        assertThat(results).hasSize(73);
        results.close();
    }

    @Test
    public void shouldFetchByRid() throws Exception {
        String ridQuery = OLuceneFunctionsUtils.doubleEscape("#26:4 #26:5");
        OResultSet results = db.query((("SELECT FROM Song WHERE search_class('RID:(" + ridQuery) + ") ')=true "));
        assertThat(results).hasSize(2);
        results.close();
    }
}


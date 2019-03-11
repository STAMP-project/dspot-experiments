package com.thinkaurelius.titan.diskstorage.solr;


import com.thinkaurelius.titan.graphdb.TitanIndexTest;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public abstract class SolrTitanIndexTest extends TitanIndexTest {
    protected SolrTitanIndexTest() {
        super(true, true, true);
    }

    @Test
    public void testRawQueries() {
        clopen(option(SolrIndex.DYNAMIC_FIELDS, TitanIndexTest.INDEX), false);
        super.testRawQueries();
    }
}


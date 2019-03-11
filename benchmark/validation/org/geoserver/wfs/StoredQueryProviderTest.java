/**
 * (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;


import java.io.File;
import java.io.IOException;
import java.util.List;
import net.opengis.wfs20.StoredQueryDescriptionType;
import org.geoserver.catalog.Catalog;
import org.geoserver.platform.GeoServerResourceLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class StoredQueryProviderTest {
    public static final String MY_STORED_QUERY = "MyStoredQuery";

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public static final String MY_STORED_QUERY_DEFINITION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + (((((((((((((((((((("<wfs:StoredQueryDescription id='MyStoredQuery'" + " xmlns:xlink=\"http://www.w3.org/1999/xlink\"") + " xmlns:ows=\"http://www.opengis.net/ows/1.1\"") + " xmlns:gml=\"http://www.opengis.net/gml/3.2\"") + " xmlns:wfs=\"http://www.opengis.net/wfs/2.0\"") + " xmlns:fes=\"http://www.opengis.net/fes/2.0\">>\n") + "  <wfs:Parameter name=\'AreaOfInterest\' type=\'gml:Polygon\'/>\n") + "  <wfs:QueryExpressionText\n") + "   returnFeatureTypes=\'topp:states\'\n") + "   language=\'urn:ogc:def:queryLanguage:OGC-WFS::WFS_QueryExpression\'\n") + "   isPrivate=\'false\'>\n") + "    <wfs:Query typeNames=\'topp:states\'>\n") + "      <fes:Filter>\n") + "        <fes:Within>\n") + "          <fes:ValueReference>the_geom</fes:ValueReference>\n") + "           ${AreaOfInterest}\n") + "        </fes:Within>\n") + "      </fes:Filter>\n") + "    </wfs:Query>\n") + "  </wfs:QueryExpressionText>\n") + "</wfs:StoredQueryDescription>");

    private StoredQueryProvider storedQueryProvider;

    private File baseDirectory;

    private Catalog catalog;

    private GeoServerResourceLoader loader;

    @Test
    public void whenNoStoredQueriesDefinedAGetFeatureByIdQueryIsReturned() {
        List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
        Assert.assertThat(queries, hasSize(1));
        Assert.assertThat(queries.get(0).getName(), is(equalTo("urn:ogc:def:query:OGC-WFS::GetFeatureById")));
        Assert.assertThat(storedQueryProvider.getStoredQuery("urn:ogc:def:query:OGC-WFS::GetFeatureById"), is(notNullValue()));
    }

    @Test
    public void whenBogusStoredQueryDefinitionCreatedItIsNotReturnedInTheListOfStoredQueries() throws IOException {
        createMyStoredQueryDefinitionFile(storedQueryProvider.storedQueryDir().dir());
        createMyBogusStoredQueryDefinition();
        List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
        Assert.assertThat(queries, hasSize(2));
        Assert.assertThat(storedQueryProvider.getStoredQuery("urn:ogc:def:query:OGC-WFS::GetFeatureById"), is(notNullValue()));
        Assert.assertThat(storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY), is(notNullValue()));
    }

    @Test
    public void whenStoredQueryDefinitionCreatedByFileItIsReturnedInTheListOfStoredQueries() throws IOException {
        createMyStoredQueryDefinitionFile(storedQueryProvider.storedQueryDir().dir());
        List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
        Assert.assertThat(queries, hasSize(2));
        Assert.assertThat(storedQueryProvider.getStoredQuery("urn:ogc:def:query:OGC-WFS::GetFeatureById"), is(notNullValue()));
        Assert.assertThat(storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY).getName(), is(StoredQueryProviderTest.MY_STORED_QUERY));
    }

    @Test
    public void whenStoredQueryDefinitionCreatedByDescriptionItIsReturnedInTheListOfStoredQueries() throws Exception {
        StoredQueryDescriptionType storedQueryDescriptionType = createMyStoredQueryDefinitionInStoredQueryDescriptionType();
        StoredQuery result = storedQueryProvider.createStoredQuery(storedQueryDescriptionType);
        Assert.assertThat(result.getName(), is(StoredQueryProviderTest.MY_STORED_QUERY));
        Assert.assertThat(storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY).getName(), is(StoredQueryProviderTest.MY_STORED_QUERY));
    }

    @Test
    public void storedQueryDefinitionIsNotRewrittenByListingTheQueries() throws IOException {
        // c.f. GEOS-7297
        File myStoredQueryDefinition = createMyStoredQueryDefinitionFile(storedQueryProvider.storedQueryDir().dir());
        try {
            myStoredQueryDefinition.setReadOnly();
            List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
            Assert.assertThat(queries, hasSize(2));
            Assert.assertThat(storedQueryProvider.getStoredQuery("urn:ogc:def:query:OGC-WFS::GetFeatureById"), is(notNullValue()));
            Assert.assertThat(storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY).getName(), is(StoredQueryProviderTest.MY_STORED_QUERY));
        } finally {
            myStoredQueryDefinition.setWritable(true);
        }
    }

    @Test
    public void canRemoveStoredQueryDefinition() throws IOException {
        File myStoredQueryDefinition = createMyStoredQueryDefinitionFile(storedQueryProvider.storedQueryDir().dir());
        List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
        Assert.assertThat(queries, hasSize(2));
        StoredQuery myStoredQuery = storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY);
        Assert.assertThat(myStoredQuery.getName(), is(StoredQueryProviderTest.MY_STORED_QUERY));
        storedQueryProvider.removeStoredQuery(myStoredQuery);
        Assert.assertThat(myStoredQueryDefinition.exists(), is(false));
        Assert.assertThat(storedQueryProvider.getStoredQuery(myStoredQuery.getName()), is(nullValue()));
    }

    @Test
    public void canRemoveAllStoredQueryDefinitions() throws IOException {
        File myStoredQueryDefinition = createMyStoredQueryDefinitionFile(storedQueryProvider.storedQueryDir().dir());
        List<StoredQuery> queries = storedQueryProvider.listStoredQueries();
        Assert.assertThat(queries, hasSize(2));
        storedQueryProvider.removeAll();
        Assert.assertThat(myStoredQueryDefinition.exists(), is(false));
        Assert.assertThat(storedQueryProvider.getStoredQuery(StoredQueryProviderTest.MY_STORED_QUERY), is(nullValue()));
    }

    @Test
    public void testGetLanguage() {
        Assert.assertThat(storedQueryProvider.getLanguage(), is(equalTo(StoredQueryProvider.LANGUAGE_20)));
    }
}


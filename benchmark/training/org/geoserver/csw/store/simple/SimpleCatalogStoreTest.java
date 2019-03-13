/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.store.simple;


import CSWRecordDescriptor.DEFAULT_CRS_NAME;
import CSWRecordDescriptor.NAMESPACES;
import CSWRecordDescriptor.RECORD_DESCRIPTOR;
import Query.ALL;
import Transaction.AUTO_COMMIT;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.csw.records.RecordDescriptor;
import org.geoserver.csw.store.CatalogStoreCapabilities;
import org.geoserver.csw.store.RepositoryItem;
import org.geoserver.platform.resource.Files;
import org.geotools.csw.CSW;
import org.geotools.csw.DC;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Attribute;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;


public class SimpleCatalogStoreTest {
    static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    File root = new File("./src/test/resources/org/geoserver/csw/store/simple");

    SimpleCatalogStore store = new SimpleCatalogStore(Files.asResource(root));

    @Test
    public void testCapabilities() throws Exception {
        CatalogStoreCapabilities capabilities = store.getCapabilities();
        Assert.assertFalse(capabilities.supportsTransactions());
        Name cswRecordName = RECORD_DESCRIPTOR.getName();
        Assert.assertTrue(capabilities.supportsGetRepositoryItem(cswRecordName));
        Assert.assertTrue(capabilities.getQueriables(cswRecordName).contains(new org.geotools.feature.NameImpl(CSW.NAMESPACE, "AnyText")));
        Assert.assertTrue(capabilities.getDomainQueriables(cswRecordName).contains(new org.geotools.feature.NameImpl(DC.NAMESPACE, "title")));
    }

    @Test
    public void testCreationExceptions() throws IOException {
        try {
            new SimpleCatalogStore(Files.asResource(new File("./pom.xml")));
            Assert.fail("Should have failed, the reference is not a directory");
        } catch (IllegalArgumentException e) {
            // fine
        }
    }

    @Test
    public void testFeatureTypes() throws IOException {
        RecordDescriptor[] fts = store.getRecordDescriptors();
        Assert.assertEquals(1, fts.length);
        Assert.assertEquals(RECORD_DESCRIPTOR, fts[0].getFeatureDescriptor());
    }

    @Test
    public void testReadAllRecords() throws IOException {
        FeatureCollection records = store.getRecords(ALL, AUTO_COMMIT);
        int fileCount = root.list(new RegexFileFilter("Record_.*\\.xml")).length;
        Assert.assertEquals(fileCount, records.size());
        FeatureIterator<Feature> fi = records.features();
        try {
            while (fi.hasNext()) {
                Feature f = fi.next();
                // check the id has be read and matches the expected format (given what we have in
                // the files)
                String id = getSimpleLiteralValue(f, "identifier");
                Assert.assertNotNull(id);
                Assert.assertTrue(id.matches("urn:uuid:[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
                // check the feature id is the same as the id attribute
                Assert.assertEquals(id, f.getIdentifier().getID());
                // the other thing we always have in these records is the type
                Attribute type = ((Attribute) (f.getProperty("type")));
                Assert.assertNotNull(type);
                Assert.assertNotNull(type.getValue());
            } 
        } finally {
            fi.close();
        }
    }

    @Test
    public void testElementValueFilter() throws IOException {
        Filter filter = SimpleCatalogStoreTest.FF.equals(SimpleCatalogStoreTest.FF.property("dc:identifier/dc:value", NAMESPACES), SimpleCatalogStoreTest.FF.literal("urn:uuid:1ef30a8b-876d-4828-9246-c37ab4510bbd"));
        FeatureCollection records = store.getRecords(new Query("Record", filter), AUTO_COMMIT);
        Assert.assertEquals(1, records.size());
        Feature record = ((Feature) (records.toArray()[0]));
        Assert.assertEquals("urn:uuid:1ef30a8b-876d-4828-9246-c37ab4510bbd", getSimpleLiteralValue(record, "identifier"));
        Assert.assertEquals("http://purl.org/dc/dcmitype/Service", getSimpleLiteralValue(record, "type"));
        Assert.assertEquals("Proin sit amet justo. In justo. Aenean adipiscing nulla id tellus.", getSimpleLiteralValue(record, "abstract"));
    }

    @Test
    public void testSpatialFilter() throws IOException {
        Filter filter = SimpleCatalogStoreTest.FF.bbox("", 60.042, 13.754, 68.41, 17.92, DEFAULT_CRS_NAME);
        FeatureCollection records = store.getRecords(new Query("Record", filter), AUTO_COMMIT);
        Assert.assertEquals(1, records.size());
        Feature record = ((Feature) (records.toArray()[0]));
        Assert.assertEquals("urn:uuid:1ef30a8b-876d-4828-9246-c37ab4510bbd", getSimpleLiteralValue(record, "identifier"));
    }

    @Test
    public void testScheme() throws IOException {
        Filter filter = SimpleCatalogStoreTest.FF.equals(SimpleCatalogStoreTest.FF.property("dc:identifier/dc:value", NAMESPACES), SimpleCatalogStoreTest.FF.literal("urn:uuid:6a3de50b-fa66-4b58-a0e6-ca146fdd18d4"));
        FeatureCollection records = store.getRecords(new Query("Record", filter), AUTO_COMMIT);
        Assert.assertEquals(1, records.size());
        Feature record = ((Feature) (records.toArray()[0]));
        Assert.assertEquals("http://www.digest.org/2.1", getSimpleLiteralScheme(record, "subject"));
    }

    @Test
    public void testSpatialFilterWorld() throws IOException {
        Filter filter = SimpleCatalogStoreTest.FF.bbox("", (-90), (-180), 90, 180, DEFAULT_CRS_NAME);
        FeatureCollection records = store.getRecords(new Query("Record", filter), AUTO_COMMIT);
        // there are only 3 records with a bbox
        Assert.assertEquals(3, records.size());
    }

    @Test
    public void testMaxFeatures() throws IOException {
        Query query = new Query("Record");
        query.setMaxFeatures(2);
        FeatureCollection records = store.getRecords(query, AUTO_COMMIT);
        Assert.assertEquals(2, records.size());
    }

    @Test
    public void testOffsetFeatures() throws IOException {
        Query queryAll = new Query("Record");
        FeatureCollection allRecords = store.getRecords(queryAll, AUTO_COMMIT);
        int size = allRecords.size();
        Assert.assertEquals(12, size);
        // with an offset
        Query queryOffset = new Query("Record");
        queryOffset.setStartIndex(1);
        FeatureCollection offsetRecords = store.getRecords(queryOffset, AUTO_COMMIT);
        Assert.assertEquals((size - 1), offsetRecords.size());
        // paged one, but towards the end so that we won't get a full page
        Query queryPaged = new Query("Record");
        queryPaged.setStartIndex(10);
        queryPaged.setMaxFeatures(3);
        FeatureCollection pagedRecords = store.getRecords(queryPaged, AUTO_COMMIT);
        Assert.assertEquals(2, pagedRecords.size());
    }

    @Test
    public void testSortAscend() throws IOException {
        Query queryImage = new Query("Record");
        queryImage.setFilter(SimpleCatalogStoreTest.FF.equals(SimpleCatalogStoreTest.FF.property("dc:type/dc:value", NAMESPACES), SimpleCatalogStoreTest.FF.literal("http://purl.org/dc/dcmitype/Image")));
        queryImage.setSortBy(new SortBy[]{ new org.geotools.filter.SortByImpl(SimpleCatalogStoreTest.FF.property("dc:title/dc:value", NAMESPACES), SortOrder.ASCENDING) });
        FeatureCollection records = store.getRecords(queryImage, AUTO_COMMIT);
        // there are only 3 records with Image type
        Assert.assertEquals(3, records.size());
        // check they were sorted
        final List<String> values = collectElement(records, "title");
        Assert.assertEquals(3, values.size());
        Assert.assertEquals("Lorem ipsum", values.get(0));
        Assert.assertEquals("Lorem ipsum dolor sit amet", values.get(1));
        Assert.assertEquals("Vestibulum massa purus", values.get(2));
    }

    @Test
    public void testSortDescend() throws IOException {
        Query queryImage = new Query("Record");
        queryImage.setFilter(SimpleCatalogStoreTest.FF.equals(SimpleCatalogStoreTest.FF.property("dc:type/dc:value", NAMESPACES), SimpleCatalogStoreTest.FF.literal("http://purl.org/dc/dcmitype/Image")));
        queryImage.setSortBy(new SortBy[]{ new org.geotools.filter.SortByImpl(SimpleCatalogStoreTest.FF.property("dc:title/dc:value", NAMESPACES), SortOrder.DESCENDING) });
        FeatureCollection records = store.getRecords(queryImage, AUTO_COMMIT);
        // there are only 3 records with Image type
        Assert.assertEquals(3, records.size());
        // check they were sorted
        final List<String> values = collectElement(records, "title");
        Assert.assertEquals(3, values.size());
        Assert.assertEquals("Vestibulum massa purus", values.get(0));
        Assert.assertEquals("Lorem ipsum dolor sit amet", values.get(1));
        Assert.assertEquals("Lorem ipsum", values.get(2));
    }

    @Test
    public void testSortNatural() throws IOException {
        Query queryImage = new Query("Record");
        queryImage.setSortBy(new SortBy[]{ SortBy.NATURAL_ORDER });
        FeatureCollection records = store.getRecords(queryImage, AUTO_COMMIT);
        Assert.assertEquals(12, records.size());
        // check they were sorted
        final List<String> values = collectElement(records, "identifier");
        List<String> sorted = new ArrayList<String>(values);
        Collections.sort(sorted);
        Assert.assertEquals(sorted, values);
    }

    @Test
    public void testLimitAttributes() throws IOException {
        Query query = new Query("Record");
        Filter typeDataset = SimpleCatalogStoreTest.FF.equals(SimpleCatalogStoreTest.FF.property("dc:type/dc:value", NAMESPACES), SimpleCatalogStoreTest.FF.literal("http://purl.org/dc/dcmitype/Dataset"));
        query.setFilter(typeDataset);
        query.setSortBy(new SortBy[]{ new org.geotools.filter.SortByImpl(SimpleCatalogStoreTest.FF.property("dc:subject/dc:value", NAMESPACES), SortOrder.ASCENDING) });
        // select some properties we did not use for filtering and sorting
        query.setProperties(Arrays.asList(SimpleCatalogStoreTest.FF.property("dc:identifier", NAMESPACES)));
        FeatureCollection records = store.getRecords(query, AUTO_COMMIT);
        Assert.assertEquals(3, records.size());
        // check the properties and collect their identifier
        final List<String> values = new ArrayList<String>();
        records.accepts(new FeatureVisitor() {
            @Override
            public void visit(Feature feature) {
                // has the id
                ComplexAttribute id = ((ComplexAttribute) (feature.getProperty("identifier")));
                Assert.assertNotNull(id);
                String value = ((String) (id.getProperty("value").getValue()));
                values.add(value);
                // only has the id
                Assert.assertEquals(1, feature.getProperties().size());
            }
        }, null);
        // if they were actually sorted by subject, here is the expected identifier order
        Assert.assertEquals("urn:uuid:9a669547-b69b-469f-a11f-2d875366bbdc", values.get(0));
        Assert.assertEquals("urn:uuid:88247b56-4cbc-4df9-9860-db3f8042e357", values.get(1));
        Assert.assertEquals("urn:uuid:94bc9c83-97f6-4b40-9eb8-a8e8787a5c63", values.get(2));
    }

    @Test
    public void testGetDomain() throws IOException {
        Name name = new org.geotools.feature.NameImpl(DC.NAMESPACE, "type");
        CloseableIterator<String> domain = store.getDomain(new org.geotools.feature.NameImpl(CSW.NAMESPACE, "Record"), name);
        Assert.assertTrue(domain.hasNext());
        Assert.assertEquals("http://purl.org/dc/dcmitype/Dataset", domain.next());
        Assert.assertEquals("http://purl.org/dc/dcmitype/Image", domain.next());
        Assert.assertEquals("http://purl.org/dc/dcmitype/Service", domain.next());
        Assert.assertEquals("http://purl.org/dc/dcmitype/Text", domain.next());
        Assert.assertFalse(domain.hasNext());
        domain.close();
    }

    @Test
    public void testGetRepositoryItem() throws IOException {
        RepositoryItem item = store.getRepositoryItem("foo");
        Assert.assertNull(item);
        item = store.getRepositoryItem("urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f");
        Assert.assertNotNull(item);
        Assert.assertEquals("application/xml", item.getMime());
        String contents = IOUtils.toString(item.getContents(), "UTF-8");
        String expected = "This is a random comment that will show up only when fetching the repository item";
        Assert.assertTrue(contents.contains(expected));
    }
}


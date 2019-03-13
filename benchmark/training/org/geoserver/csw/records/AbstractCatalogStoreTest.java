/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.csw.records;


import Transaction.AUTO_COMMIT;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.opengis.cat.csw20.ElementSetType;
import org.geoserver.csw.store.AbstractCatalogStore;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.FeatureTypeFactoryImpl;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.xml.sax.helpers.NamespaceSupport;


public class AbstractCatalogStoreTest {
    @Test
    public void testNamespaceSupport() throws IOException, URISyntaxException {
        AbstractCatalogStore store = new AbstractCatalogStore() {
            {
                support(CSWRecordDescriptor.getInstance());
                support(AbstractCatalogStoreTest.GSRecordDescriptor.getInstance());
            }

            @Override
            public FeatureCollection getRecordsInternal(RecordDescriptor rd, RecordDescriptor rdOutput, Query q, Transaction t) throws IOException {
                if (rd == (AbstractCatalogStoreTest.GSRecordDescriptor.getInstance())) {
                    return new org.geoserver.csw.feature.MemoryFeatureCollection(AbstractCatalogStoreTest.GSRecordDescriptor.getInstance().getFeatureType());
                } else {
                    throw new RuntimeException("Was expecting the geoserver record descriptor");
                }
            }
        };
        RecordDescriptor[] descriptors = store.getRecordDescriptors();
        Assert.assertEquals(2, descriptors.length);
        Assert.assertEquals(CSWRecordDescriptor.getInstance(), descriptors[0]);
        Assert.assertEquals(AbstractCatalogStoreTest.GSRecordDescriptor.getInstance(), descriptors[1]);
        Query query = new Query("Record");
        query.setNamespace(new URI(AbstractCatalogStoreTest.GSRecordDescriptor.GS_NAMESPACE));
        FeatureCollection records = store.getRecords(query, AUTO_COMMIT, null);
        Assert.assertEquals(AbstractCatalogStoreTest.GSRecordDescriptor.getInstance().getFeatureType(), records.getSchema());
    }

    static class GSRecordDescriptor extends AbstractRecordDescriptor {
        static final String GS_NAMESPACE = "http://www.geoserver.org/csw";

        CSWRecordDescriptor delegate = CSWRecordDescriptor.getInstance();

        static final AbstractCatalogStoreTest.GSRecordDescriptor INSTANCE = new AbstractCatalogStoreTest.GSRecordDescriptor();

        public static AbstractCatalogStoreTest.GSRecordDescriptor getInstance() {
            return AbstractCatalogStoreTest.GSRecordDescriptor.INSTANCE;
        }

        public FeatureType getFeatureType() {
            FeatureType ft = delegate.getFeatureType();
            FeatureTypeFactory factory = new FeatureTypeFactoryImpl();
            FeatureType gsft = factory.createFeatureType(new NameImpl(AbstractCatalogStoreTest.GSRecordDescriptor.GS_NAMESPACE, "Record"), ft.getDescriptors(), null, false, null, ft.getSuper(), null);
            return gsft;
        }

        public AttributeDescriptor getFeatureDescriptor() {
            AttributeTypeBuilder builder = new AttributeTypeBuilder();
            AttributeDescriptor descriptor = builder.buildDescriptor(new NameImpl(AbstractCatalogStoreTest.GSRecordDescriptor.GS_NAMESPACE, "Record"), delegate.getFeatureType());
            return descriptor;
        }

        public String getOutputSchema() {
            return delegate.getOutputSchema();
        }

        public List<Name> getPropertiesForElementSet(ElementSetType elementSet) {
            return delegate.getPropertiesForElementSet(elementSet);
        }

        public NamespaceSupport getNamespaceSupport() {
            return delegate.getNamespaceSupport();
        }

        public Query adaptQuery(Query query) {
            return delegate.adaptQuery(query);
        }

        public String getBoundingBoxPropertyName() {
            return delegate.getBoundingBoxPropertyName();
        }

        public List<Name> getQueryables() {
            return delegate.getQueryables();
        }

        public String getQueryablesDescription() {
            return delegate.getQueryablesDescription();
        }

        public PropertyName translateProperty(Name name) {
            return delegate.translateProperty(name);
        }

        public void verifySpatialFilters(Filter filter) {
            delegate.verifySpatialFilters(filter);
        }
    }
}


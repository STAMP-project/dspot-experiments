/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.script.wfs;


import TransactionEventType.PRE_DELETE;
import TransactionEventType.PRE_INSERT;
import TransactionEventType.PRE_UPDATE;
import WfsFactory.eINSTANCE;
import com.google.common.collect.Multimap;
import java.util.Iterator;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import net.opengis.wfs.TransactionType;
import org.geoserver.script.ScriptManager;
import org.geoserver.script.wfs.TransactionDetail.Entry;
import org.geoserver.wfs.TransactionEvent;
import org.geoserver.wfs.TransactionEventType;
import org.geoserver.wfs.request.TransactionRequest;
import org.geotools.data.simple.SimpleFeatureCollection;


public class ScriptTransctionPluginTest extends TestCase {
    ScriptManager scriptMgr;

    public void testTransactionDetails() throws Exception {
        SimpleFeatureCollection inserted = createNiceMock(SimpleFeatureCollection.class);
        SimpleFeatureCollection updated = createNiceMock(SimpleFeatureCollection.class);
        SimpleFeatureCollection deleted = createNiceMock(SimpleFeatureCollection.class);
        replay(inserted, updated, deleted);
        TransactionType t = eINSTANCE.createTransactionType();
        TransactionEvent e1 = new TransactionEvent(TransactionEventType.PRE_INSERT, TransactionRequest.adapt(t), PRIMITIVEGEOFEATURE, inserted);
        TransactionEvent e2 = new TransactionEvent(TransactionEventType.PRE_UPDATE, TransactionRequest.adapt(t), PRIMITIVEGEOFEATURE, updated);
        TransactionEvent e3 = new TransactionEvent(TransactionEventType.PRE_DELETE, TransactionRequest.adapt(t), PRIMITIVEGEOFEATURE, deleted);
        ScriptTransactionPlugin plugin = new ScriptTransactionPlugin(scriptMgr);
        plugin.dataStoreChange(e1);
        plugin.dataStoreChange(e2);
        plugin.dataStoreChange(e3);
        TransactionDetail detail = ((TransactionDetail) (t.getExtendedProperties().get(TransactionDetail.class)));
        TestCase.assertNotNull(detail);
        Multimap<QName, Entry> entries = detail.getEntries();
        TestCase.assertTrue(entries.containsKey(PRIMITIVEGEOFEATURE));
        Iterator<Entry> it = entries.get(PRIMITIVEGEOFEATURE).iterator();
        TestCase.assertTrue(it.hasNext());
        Entry e = it.next();
        TestCase.assertEquals(PRE_INSERT, e.type);
        TestCase.assertEquals(inserted, e.features);
        TestCase.assertTrue(it.hasNext());
        e = it.next();
        TestCase.assertEquals(PRE_UPDATE, e.type);
        TestCase.assertEquals(updated, e.features);
        TestCase.assertTrue(it.hasNext());
        e = it.next();
        TestCase.assertEquals(PRE_DELETE, e.type);
        TestCase.assertEquals(deleted, e.features);
        TestCase.assertFalse(it.hasNext());
    }
}


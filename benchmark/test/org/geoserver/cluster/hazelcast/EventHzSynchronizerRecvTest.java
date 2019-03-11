/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2014 Boundless
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.hazelcast;


import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.cluster.ConfigChangeEvent;
import org.geoserver.cluster.ConfigChangeEvent.Type;
import org.geoserver.config.ConfigurationListener;
import org.junit.Test;


/* protected IExpectationSetters<Object> expectCatalogFire(final CatalogInfo info, final String id, final ConfigChangeEvent.Type type){
switch(type) {
case ADD:
getCatalog().fireAdded((CatalogInfo)info(id));
break;
case MODIFY:
getCatalog().firePostModified((CatalogInfo)info(id));
break;
case REMOVE:
getCatalog().fireRemoved((CatalogInfo)info(id));
break;
}
return expectLastCall().andAnswer(new IAnswer<Object>() {
@Override
public Object answer() throws Throwable {
switch(type) {
case ADD:
CatalogAddEventImpl addEvt = new CatalogAddEventImpl();
addEvt.setSource(info);
sync.handleAddEvent(addEvt);
break;
case MODIFY:
CatalogPostModifyEventImpl modEvt = new CatalogPostModifyEventImpl();
modEvt.setSource(info);
sync.handlePostModifyEvent(modEvt);
break;
case REMOVE:
CatalogRemoveEventImpl remEvt = new CatalogRemoveEventImpl();
remEvt.setSource(info);
sync.handleRemoveEvent(remEvt);
break;
}
return null;
}});
}
 */
public class EventHzSynchronizerRecvTest extends HzSynchronizerRecvTest {
    ConfigurationListener configListener;

    CatalogListener catListener;

    @Test
    public void testPublishAck() throws Exception {
        // This is just testStoreDelete with an extra assert to test if the ack was sent
        DataStoreInfo info;
        WorkspaceInfo wsInfo;
        final String storeName = "testStore";
        final String storeId = "Store-TEST";
        final String storeWorkspace = "Workspace-TEST";
        {
            info = createMock(DataStoreInfo.class);
            wsInfo = createMock(WorkspaceInfo.class);
            expect(info.getName()).andStubReturn(storeName);
            expect(info.getId()).andStubReturn(storeId);
            expect(info.getWorkspace()).andStubReturn(wsInfo);
            expect(wsInfo.getId()).andStubReturn(storeWorkspace);
            expectationTestStoreDelete(info, storeName, storeId, DataStoreInfo.class);
        }
        replay(info, wsInfo);
        ConfigChangeEvent evt = new ConfigChangeEvent(storeId, storeName, DataStoreInfoImpl.class, Type.REMOVE);
        {
            sync = getSynchronizer();
            sync.initialize(configWatcher);
            evt.setWorkspaceId(storeWorkspace);
            // Mock a message coming in from the cluster
            mockMessage(evt);
        }
        waitForSync();
        // Did the ack get sent
        this.assertAcked(evt.getUUID());
        verify(info, wsInfo);
    }
}


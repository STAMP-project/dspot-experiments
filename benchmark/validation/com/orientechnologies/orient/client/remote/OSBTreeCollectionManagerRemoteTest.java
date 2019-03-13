package com.orientechnologies.orient.client.remote;


import com.orientechnologies.orient.client.binary.OChannelBinaryAsynchClient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.storage.index.sbtreebonsai.local.OBonsaiBucketPointer;
import com.orientechnologies.orient.core.storage.index.sbtreebonsai.local.OSBTreeBonsai;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Artem Orobets (enisher-at-gmail.com)
 */
public class OSBTreeCollectionManagerRemoteTest {
    private static final int EXPECTED_FILE_ID = 17;

    private static final OBonsaiBucketPointer EXPECTED_ROOT_POINTER = new OBonsaiBucketPointer(11, 118);

    private static final int EXPECTED_CLUSTER_ID = 3;

    @Mock
    private OCollectionNetworkSerializer networkSerializerMock;

    @Mock
    private ODatabaseDocumentInternal dbMock;

    @Mock
    private OStorageRemote storageMock;

    @Mock
    private OChannelBinaryAsynchClient clientMock;

    @Test
    public void testLoadTree() throws Exception {
        OSBTreeCollectionManagerRemote remoteManager = new OSBTreeCollectionManagerRemote(storageMock, networkSerializerMock);
        OSBTreeBonsai<OIdentifiable, Integer> tree = remoteManager.loadTree(new com.orientechnologies.orient.core.storage.ridbag.sbtree.OBonsaiCollectionPointer(OSBTreeCollectionManagerRemoteTest.EXPECTED_FILE_ID, OSBTreeCollectionManagerRemoteTest.EXPECTED_ROOT_POINTER));
        Assert.assertNotNull(tree);
        Assert.assertEquals(tree.getFileId(), OSBTreeCollectionManagerRemoteTest.EXPECTED_FILE_ID);
        Assert.assertEquals(tree.getRootBucketPointer(), OSBTreeCollectionManagerRemoteTest.EXPECTED_ROOT_POINTER);
    }
}


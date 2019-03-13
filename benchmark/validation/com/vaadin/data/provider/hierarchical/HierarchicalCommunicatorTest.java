package com.vaadin.data.provider.hierarchical;


import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchicalDataCommunicator;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.AbstractClientConnector;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class HierarchicalCommunicatorTest {
    private static final String ROOT = "ROOT";

    private static final String FOLDER = "FOLDER";

    private static final String LEAF = "LEAF";

    private TreeDataProvider<String> dataProvider;

    private HierarchicalCommunicatorTest.TestHierarchicalDataCommunicator<String> communicator;

    private TreeData<String> treeData;

    @Test
    public void testFolderRemoveRefreshAll() {
        testItemRemove(HierarchicalCommunicatorTest.FOLDER, true);
    }

    @Test
    public void testLeafRemoveRefreshAll() {
        testItemRemove(HierarchicalCommunicatorTest.LEAF, true);
    }

    @Test
    public void testFolderRemove() {
        testItemRemove(HierarchicalCommunicatorTest.FOLDER, false);
    }

    @Test
    public void testLeafRemove() {
        testItemRemove(HierarchicalCommunicatorTest.LEAF, false);
    }

    @Test
    public void testReplaceAll() {
        communicator.pushData(1, Arrays.asList(HierarchicalCommunicatorTest.ROOT, HierarchicalCommunicatorTest.FOLDER, HierarchicalCommunicatorTest.LEAF));
        // Some modifications
        communicator.expand(HierarchicalCommunicatorTest.ROOT);
        communicator.expand(HierarchicalCommunicatorTest.FOLDER);
        communicator.refresh(HierarchicalCommunicatorTest.LEAF);
        // Replace dataprovider
        communicator.setDataProvider(new TreeDataProvider(new TreeData()), null);
        dataProvider.refreshAll();
        communicator.beforeClientResponse(false);
        Assert.assertFalse("Stalled object in KeyMapper", communicator.getKeyMapper().has(HierarchicalCommunicatorTest.ROOT));
        Assert.assertEquals((-1), communicator.getParentIndex(HierarchicalCommunicatorTest.FOLDER).longValue());
    }

    private static class TestHierarchicalDataCommunicator<T> extends HierarchicalDataCommunicator<T> {
        @Override
        public void extend(AbstractClientConnector target) {
            super.extend(target);
        }

        @Override
        public void pushData(int firstIndex, List<T> data) {
            super.pushData(firstIndex, data);
        }
    }
}


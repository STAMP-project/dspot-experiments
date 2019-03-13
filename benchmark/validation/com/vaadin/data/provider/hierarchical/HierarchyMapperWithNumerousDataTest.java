package com.vaadin.data.provider.hierarchical;


import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchyMapper;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class HierarchyMapperWithNumerousDataTest {
    private static final int ROOT_COUNT = 1;

    private static final int PARENT_COUNT = 100000;

    private static TreeData<Node> data = new TreeData();

    private TreeDataProvider<Node> provider;

    private HierarchyMapper<Node, SerializablePredicate<Node>> mapper;

    private static List<Node> testData;

    private static List<Node> roots;

    private int mapSize = HierarchyMapperWithNumerousDataTest.ROOT_COUNT;

    /**
     * Test for non-logarithmic {@code getParentOfItem} implementations 100000
     * entries and 1 second should be enought to make it run even on slow
     * machines and weed out linear solutions
     */
    @Test(timeout = 1000)
    public void expandRootNode() {
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithNumerousDataTest.ROOT_COUNT, mapper.getTreeSize());
        expand(HierarchyMapperWithNumerousDataTest.testData.get(0));
        Assert.assertEquals("Should be root count + once parent count", ((HierarchyMapperWithNumerousDataTest.ROOT_COUNT) + (HierarchyMapperWithNumerousDataTest.PARENT_COUNT)), mapper.getTreeSize());
        checkMapSize();
    }
}


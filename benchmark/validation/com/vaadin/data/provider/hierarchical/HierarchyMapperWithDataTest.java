package com.vaadin.data.provider.hierarchical;


import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchyMapper;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class HierarchyMapperWithDataTest {
    private static final int ROOT_COUNT = 5;

    private static final int PARENT_COUNT = 4;

    private static final int LEAF_COUNT = 2;

    private static TreeData<Node> data = new TreeData();

    private TreeDataProvider<Node> provider;

    private HierarchyMapper<Node, SerializablePredicate<Node>> mapper;

    private static List<Node> testData;

    private static List<Node> roots;

    private int mapSize = HierarchyMapperWithDataTest.ROOT_COUNT;

    @Test
    public void expandRootNode() {
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        expand(HierarchyMapperWithDataTest.testData.get(0));
        Assert.assertEquals("Should be root count + once parent count", ((HierarchyMapperWithDataTest.ROOT_COUNT) + (HierarchyMapperWithDataTest.PARENT_COUNT)), mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandAndCollapseLastRootNode() {
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        expand(HierarchyMapperWithDataTest.roots.get(((HierarchyMapperWithDataTest.roots.size()) - 1)));
        Assert.assertEquals("Should be root count + once parent count", ((HierarchyMapperWithDataTest.ROOT_COUNT) + (HierarchyMapperWithDataTest.PARENT_COUNT)), mapper.getTreeSize());
        checkMapSize();
        collapse(HierarchyMapperWithDataTest.roots.get(((HierarchyMapperWithDataTest.roots.size()) - 1)));
        Assert.assertEquals("Map size should be equal to root node count again", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandHiddenNode() {
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        expand(HierarchyMapperWithDataTest.testData.get(1));
        Assert.assertEquals("Map size should not change when expanding a hidden node", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        checkMapSize();
        expand(HierarchyMapperWithDataTest.roots.get(0));
        Assert.assertEquals("Hidden node should now be expanded as well", (((HierarchyMapperWithDataTest.ROOT_COUNT) + (HierarchyMapperWithDataTest.PARENT_COUNT)) + (HierarchyMapperWithDataTest.LEAF_COUNT)), mapper.getTreeSize());
        checkMapSize();
        collapse(HierarchyMapperWithDataTest.roots.get(0));
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void expandLeafNode() {
        Assert.assertEquals("Map size should be equal to root node count", HierarchyMapperWithDataTest.ROOT_COUNT, mapper.getTreeSize());
        expand(HierarchyMapperWithDataTest.testData.get(0));
        expand(HierarchyMapperWithDataTest.testData.get(1));
        Assert.assertEquals("Root and parent node expanded", (((HierarchyMapperWithDataTest.ROOT_COUNT) + (HierarchyMapperWithDataTest.PARENT_COUNT)) + (HierarchyMapperWithDataTest.LEAF_COUNT)), mapper.getTreeSize());
        checkMapSize();
        expand(HierarchyMapperWithDataTest.testData.get(2));
        Assert.assertEquals("Expanding a leaf node should have no effect", (((HierarchyMapperWithDataTest.ROOT_COUNT) + (HierarchyMapperWithDataTest.PARENT_COUNT)) + (HierarchyMapperWithDataTest.LEAF_COUNT)), mapper.getTreeSize());
        checkMapSize();
    }

    @Test
    public void findParentIndexOfLeaf() {
        expand(HierarchyMapperWithDataTest.testData.get(0));
        Assert.assertEquals("Could not find the root node of a parent", Integer.valueOf(0), mapper.getParentIndex(HierarchyMapperWithDataTest.testData.get(1)));
        expand(HierarchyMapperWithDataTest.testData.get(1));
        Assert.assertEquals("Could not find the parent of a leaf", Integer.valueOf(1), mapper.getParentIndex(HierarchyMapperWithDataTest.testData.get(2)));
    }

    @Test
    public void fetchRangeOfRows() {
        expand(HierarchyMapperWithDataTest.testData.get(0));
        expand(HierarchyMapperWithDataTest.testData.get(1));
        List<Node> expectedResult = HierarchyMapperWithDataTest.testData.stream().filter(( n) -> ((HierarchyMapperWithDataTest.roots.contains(n)) || (n.getParent().equals(HierarchyMapperWithDataTest.testData.get(0)))) || (n.getParent().equals(HierarchyMapperWithDataTest.testData.get(1)))).collect(Collectors.toList());
        // Range containing deepest level of expanded nodes without their
        // parents in addition to root nodes at the end.
        Range range = Range.between(3, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
        // Only the expanded two nodes, nothing more.
        range = Range.between(0, 2);
        verifyFetchIsCorrect(expectedResult, range);
        // Fetch everything
        range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }

    @Test
    public void fetchRangeOfRowsWithSorting() {
        // Expand before sort
        expand(HierarchyMapperWithDataTest.testData.get(0));
        expand(HierarchyMapperWithDataTest.testData.get(1));
        // Construct a sorted version of test data with correct filters
        List<List<Node>> levels = new ArrayList<>();
        Comparator<Node> comparator = Comparator.comparing(Node::getNumber).reversed();
        levels.add(HierarchyMapperWithDataTest.testData.stream().filter(( n) -> (n.getParent()) == null).sorted(comparator).collect(Collectors.toList()));
        levels.add(HierarchyMapperWithDataTest.testData.stream().filter(( n) -> (n.getParent()) == (HierarchyMapperWithDataTest.testData.get(0))).sorted(comparator).collect(Collectors.toList()));
        levels.add(HierarchyMapperWithDataTest.testData.stream().filter(( n) -> (n.getParent()) == (HierarchyMapperWithDataTest.testData.get(1))).sorted(comparator).collect(Collectors.toList()));
        List<Node> expectedResult = levels.get(0).stream().flatMap(( root) -> {
            Stream<Node> nextLevel = levels.get(1).stream().filter(( n) -> (n.getParent()) == root).flatMap(( node) -> Stream.concat(Stream.of(node), levels.get(2).stream().filter(( n) -> (n.getParent()) == node)));
            return Stream.concat(Stream.of(root), nextLevel);
        }).collect(Collectors.toList());
        // Apply sorting
        mapper.setInMemorySorting(comparator::compare);
        // Range containing deepest level of expanded nodes without their
        // parents in addition to root nodes at the end.
        Range range = Range.between(8, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
        // Only the root nodes, nothing more.
        range = Range.between(0, HierarchyMapperWithDataTest.ROOT_COUNT);
        verifyFetchIsCorrect(expectedResult, range);
        // Fetch everything
        range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }

    @Test
    public void fetchWithFilter() {
        expand(HierarchyMapperWithDataTest.testData.get(0));
        Node expandedNode = HierarchyMapperWithDataTest.testData.get((2 + (HierarchyMapperWithDataTest.LEAF_COUNT)));// Expand second node

        expand(expandedNode);
        SerializablePredicate<Node> filter = ( n) -> ((n.getNumber()) % 2) == 0;
        List<Node> expectedResult = HierarchyMapperWithDataTest.testData.stream().filter(filter).filter(( n) -> ((HierarchyMapperWithDataTest.roots.contains(n)) || (n.getParent().equals(HierarchyMapperWithDataTest.testData.get(0)))) || (n.getParent().equals(expandedNode))).collect(Collectors.toList());
        mapper.setFilter(filter);
        // Fetch everything
        Range range = Range.between(0, mapper.getTreeSize());
        verifyFetchIsCorrect(expectedResult, range);
    }
}


package com.vaadin.data.provider;


import com.vaadin.data.TreeData;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class TreeDataProviderTest extends DataProviderTestBase<TreeDataProvider<StrBean>> {
    private TreeData<StrBean> data;

    private List<StrBean> flattenedData;

    private List<StrBean> rootData;

    @Test(expected = IllegalArgumentException.class)
    public void treeData_add_item_parent_not_in_hierarchy_throws() {
        new TreeData().addItem(new StrBean("", 0, 0), new StrBean("", 0, 0));
    }

    @Test(expected = NullPointerException.class)
    public void treeData_add_null_item_throws() {
        new TreeData().addItem(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void treeData_add_item_already_in_hierarchy_throws() {
        StrBean bean = new StrBean("", 0, 0);
        new TreeData().addItem(null, bean).addItem(null, bean);
    }

    @Test
    public void treeData_remove_root_item() {
        data.removeItem(null);
        Assert.assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void treeData_clear() {
        data.clear();
        Assert.assertTrue(data.getChildren(null).isEmpty());
    }

    @Test
    public void treeData_re_add_removed_item() {
        StrBean item = rootData.get(0);
        data.removeItem(item).addItem(null, item);
        Assert.assertTrue(data.getChildren(null).contains(item));
    }

    @Test
    public void treeData_get_parent() {
        StrBean root = rootData.get(0);
        StrBean firstChild = data.getChildren(root).get(0);
        Assert.assertNull(data.getParent(root));
        Assert.assertEquals(root, data.getParent(firstChild));
    }

    @Test
    public void treeData_set_parent() {
        StrBean item1 = rootData.get(0);
        StrBean item2 = rootData.get(1);
        Assert.assertEquals(0, data.getChildren(item2).size());
        Assert.assertEquals(10, data.getRootItems().size());
        // Move item1 as item2's child
        data.setParent(item1, item2);
        Assert.assertEquals(1, data.getChildren(item2).size());
        Assert.assertEquals(9, data.getRootItems().size());
        Assert.assertEquals(item1, data.getChildren(item2).get(0));
        // Move back to root
        data.setParent(item1, null);
        Assert.assertEquals(0, data.getChildren(item2).size());
        Assert.assertEquals(10, data.getRootItems().size());
    }

    @Test
    public void treeData_move_after_sibling() {
        StrBean root0 = rootData.get(0);
        StrBean root9 = rootData.get(9);
        Assert.assertEquals(root0, data.getRootItems().get(0));
        Assert.assertEquals(root9, data.getRootItems().get(9));
        // Move to last position
        data.moveAfterSibling(root0, root9);
        Assert.assertEquals(root0, data.getRootItems().get(9));
        Assert.assertEquals(root9, data.getRootItems().get(8));
        // Move back to first position
        data.moveAfterSibling(root0, null);
        Assert.assertEquals(root0, data.getRootItems().get(0));
        Assert.assertEquals(root9, data.getRootItems().get(9));
        StrBean child0 = data.getChildren(root0).get(0);
        StrBean child2 = data.getChildren(root0).get(2);
        // Move first child to different position
        data.moveAfterSibling(child0, child2);
        Assert.assertEquals(2, data.getChildren(root0).indexOf(child0));
        Assert.assertEquals(1, data.getChildren(root0).indexOf(child2));
        // Move child back to first position
        data.moveAfterSibling(child0, null);
        Assert.assertEquals(0, data.getChildren(root0).indexOf(child0));
        Assert.assertEquals(2, data.getChildren(root0).indexOf(child2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void treeData_move_after_sibling_different_parents() {
        StrBean root0 = rootData.get(0);
        StrBean wrongSibling = data.getChildren(root0).get(0);
        data.moveAfterSibling(root0, wrongSibling);
    }

    @Test
    public void treeData_root_items() {
        TreeData<String> data = new TreeData();
        TreeData<String> dataVarargs = new TreeData();
        TreeData<String> dataCollection = new TreeData();
        TreeData<String> dataStream = new TreeData();
        data.addItems(null, "a", "b", "c");
        dataVarargs.addRootItems("a", "b", "c");
        dataCollection.addRootItems(Arrays.asList("a", "b", "c"));
        dataStream.addRootItems(Arrays.asList("a", "b", "c").stream());
        Assert.assertEquals(data.getRootItems(), dataVarargs.getRootItems());
        Assert.assertEquals(data.getRootItems(), dataCollection.getRootItems());
        Assert.assertEquals(data.getRootItems(), dataStream.getRootItems());
    }

    @Test
    public void populate_treeData_with_child_item_provider() {
        TreeData<String> stringData = new TreeData();
        List<String> rootItems = Arrays.asList("a", "b", "c");
        stringData.addItems(rootItems, ( item) -> {
            if (((item.length()) >= 3) || (item.startsWith("c"))) {
                return Arrays.asList();
            }
            return Arrays.asList((item + "/a"), (item + "/b"), (item + "/c"));
        });
        Assert.assertEquals(stringData.getChildren("a"), Arrays.asList("a/a", "a/b", "a/c"));
        Assert.assertEquals(stringData.getChildren("b"), Arrays.asList("b/a", "b/b", "b/c"));
        Assert.assertEquals(stringData.getChildren("c"), Arrays.asList());
        Assert.assertEquals(stringData.getChildren("a/b"), Arrays.asList());
    }

    @Test
    public void populate_treeData_with_stream_child_item_provider() {
        TreeData<String> stringData = new TreeData();
        Stream<String> rootItems = Stream.of("a", "b", "c");
        stringData.addItems(rootItems, ( item) -> {
            if (((item.length()) >= 3) || (item.startsWith("c"))) {
                return Stream.empty();
            }
            return Stream.of((item + "/a"), (item + "/b"), (item + "/c"));
        });
        Assert.assertEquals(stringData.getChildren("a"), Arrays.asList("a/a", "a/b", "a/c"));
        Assert.assertEquals(stringData.getChildren("b"), Arrays.asList("b/a", "b/b", "b/c"));
        Assert.assertEquals(stringData.getChildren("c"), Arrays.asList());
        Assert.assertEquals(stringData.getChildren("a/b"), Arrays.asList());
    }

    @Test
    public void setFilter() {
        getDataProvider().setFilter(( item) -> (item.getValue().equals("Xyz")) || (item.getValue().equals("Baz")));
        Assert.assertEquals(10, sizeWithUnfilteredQuery());
        getDataProvider().setFilter(( item) -> (!(item.getValue().equals("Foo"))) && (!(item.getValue().equals("Xyz"))));
        Assert.assertEquals("Previous filter should be replaced when setting a new one", 6, sizeWithUnfilteredQuery());
        getDataProvider().setFilter(null);
        Assert.assertEquals("Setting filter to null should remove all filters", 20, sizeWithUnfilteredQuery());
    }

    @Test
    public void addFilter() {
        getDataProvider().addFilter(( item) -> (item.getId()) <= 10);
        getDataProvider().addFilter(( item) -> (item.getId()) >= 5);
        Assert.assertEquals(5, sizeWithUnfilteredQuery());
    }
}


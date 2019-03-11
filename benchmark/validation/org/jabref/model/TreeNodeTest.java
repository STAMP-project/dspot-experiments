package org.jabref.model;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


public class TreeNodeTest {
    Consumer<TreeNodeTestData.TreeNodeMock> subscriber;

    @Test
    public void constructorChecksThatClassImplementsCorrectInterface() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> new TreeNodeTest.WrongTreeNodeImplementation());
    }

    @Test
    public void constructorExceptsCorrectImplementation() {
        TreeNodeTestData.TreeNodeMock treeNode = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertNotNull(treeNode);
    }

    @Test
    public void newTreeNodeHasNoParentOrChildren() {
        TreeNodeTestData.TreeNodeMock treeNode = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Optional.empty(), getParent());
        Assertions.assertEquals(Collections.emptyList(), getChildren());
        Assertions.assertNotNull(treeNode);
    }

    @Test
    public void getIndexedPathFromRootReturnsEmptyListForRoot() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Collections.emptyList(), getIndexedPathFromRoot());
    }

    @Test
    public void getIndexedPathFromRootSimplePath() {
        Assertions.assertEquals(Arrays.asList(1, 0), getIndexedPathFromRoot());
    }

    @Test
    public void getIndexedPathFromRootComplexPath() {
        Assertions.assertEquals(Arrays.asList(2, 1, 0), getIndexedPathFromRoot());
    }

    @Test
    public void getDescendantSimplePath() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        Assertions.assertEquals(node, getDescendant(Arrays.asList(1, 0)).get());
    }

    @Test
    public void getDescendantComplexPath() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertEquals(node, getDescendant(Arrays.asList(2, 1, 0)).get());
    }

    @Test
    public void getDescendantNonExistentReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertEquals(Optional.empty(), getDescendant(Arrays.asList(1, 100, 0)));
    }

    @Test
    public void getPositionInParentForRootThrowsException() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> getPositionInParent());
    }

    @Test
    public void getPositionInParentSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertEquals(2, node.getPositionInParent());
    }

    @Test
    public void getIndexOfNonExistentChildReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Optional.empty(), root.getIndexOfChild(new TreeNodeTestData.TreeNodeMock()));
    }

    @Test
    public void getIndexOfChild() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertEquals(((Integer) (2)), root.getIndexOfChild(node).get());
    }

    @Test
    public void getLevelOfRoot() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(0, getLevel());
    }

    @Test
    public void getLevelInSimpleTree() {
        Assertions.assertEquals(2, getLevel());
    }

    @Test
    public void getLevelInComplexTree() {
        Assertions.assertEquals(3, getLevel());
    }

    @Test
    public void getChildCountInSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.getNodeInSimpleTree(root);
        Assertions.assertEquals(2, getNumberOfChildren());
    }

    @Test
    public void getChildCountInComplexTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertEquals(4, getNumberOfChildren());
    }

    @Test
    public void moveToAddsAsLastChildInSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        moveTo(root);
        Assertions.assertEquals(((Integer) (2)), root.getIndexOfChild(node).get());
    }

    @Test
    public void moveToAddsAsLastChildInComplexTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        moveTo(root);
        Assertions.assertEquals(((Integer) (4)), root.getIndexOfChild(node).get());
    }

    @Test
    public void moveToChangesParent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        moveTo(root);
        Assertions.assertEquals(root, getParent().get());
    }

    @Test
    public void moveToInSameLevelAddsAtEnd() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        moveTo(root);
        Assertions.assertEquals(Arrays.asList(child2, child1), getChildren());
    }

    @Test
    public void moveToInSameLevelWhenNodeWasBeforeTargetIndex() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child3 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        addChild(child3);
        child1.moveTo(root, 1);
        Assertions.assertEquals(Arrays.asList(child2, child1, child3), getChildren());
    }

    @Test
    public void moveToInSameLevelWhenNodeWasAfterTargetIndex() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child3 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        addChild(child3);
        child3.moveTo(root, 1);
        Assertions.assertEquals(Arrays.asList(child1, child3, child2), getChildren());
    }

    @Test
    public void getPathFromRootInSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        List<TreeNodeTestData.TreeNodeMock> path = getPathFromRoot();
        Assertions.assertEquals(3, path.size());
        Assertions.assertEquals(root, path.get(0));
        Assertions.assertEquals(node, path.get(2));
    }

    @Test
    public void getPathFromRootInComplexTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        List<TreeNodeTestData.TreeNodeMock> path = getPathFromRoot();
        Assertions.assertEquals(4, path.size());
        Assertions.assertEquals(root, path.get(0));
        Assertions.assertEquals(node, path.get(3));
    }

    @Test
    public void getPreviousSiblingReturnsCorrect() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        addChild(new TreeNodeTestData.TreeNodeMock());
        TreeNodeTestData.TreeNodeMock previous = new TreeNodeTestData.TreeNodeMock();
        addChild(previous);
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        addChild(new TreeNodeTestData.TreeNodeMock());
        Assertions.assertEquals(previous, getPreviousSibling().get());
    }

    @Test
    public void getPreviousSiblingForRootReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Optional.empty(), getPreviousSibling());
    }

    @Test
    public void getPreviousSiblingForNonexistentReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        Assertions.assertEquals(Optional.empty(), getPreviousSibling());
    }

    @Test
    public void getNextSiblingReturnsCorrect() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        addChild(new TreeNodeTestData.TreeNodeMock());
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        TreeNodeTestData.TreeNodeMock next = new TreeNodeTestData.TreeNodeMock();
        addChild(next);
        addChild(new TreeNodeTestData.TreeNodeMock());
        Assertions.assertEquals(next, getNextSibling().get());
    }

    @Test
    public void getNextSiblingForRootReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Optional.empty(), getNextSibling());
    }

    @Test
    public void getNextSiblingForNonexistentReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        Assertions.assertEquals(Optional.empty(), getPreviousSibling());
    }

    @Test
    public void getParentReturnsCorrect() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertEquals(root, getParent().get());
    }

    @Test
    public void getParentForRootReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(Optional.empty(), getParent());
    }

    @Test
    public void getChildAtReturnsCorrect() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertEquals(node, getChildAt(2).get());
    }

    @Test
    public void getChildAtInvalidIndexReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        addChild(new TreeNodeTestData.TreeNodeMock());
        addChild(new TreeNodeTestData.TreeNodeMock());
        Assertions.assertEquals(Optional.empty(), getChildAt(10));
    }

    @Test
    public void getRootReturnsTrueForRoot() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertTrue(isRoot());
    }

    @Test
    public void getRootReturnsFalseForChild() {
        Assertions.assertFalse(isRoot());
    }

    @Test
    public void nodeIsAncestorOfItself() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertTrue(isAncestorOf(root));
    }

    @Test
    public void isAncestorOfInSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        Assertions.assertTrue(isAncestorOf(node));
    }

    @Test
    public void isAncestorOfInComplexTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertTrue(isAncestorOf(node));
    }

    @Test
    public void getRootOfSingleNode() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertEquals(root, getRoot());
    }

    @Test
    public void getRootInSimpleTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        Assertions.assertEquals(root, getRoot());
    }

    @Test
    public void getRootInComplexTree() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertEquals(root, getRoot());
    }

    @Test
    public void isLeafIsCorrectForRootWithoutChildren() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        Assertions.assertTrue(isLeaf());
    }

    @Test
    public void removeFromParentSetsParentToEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        removeFromParent();
        Assertions.assertEquals(Optional.empty(), getParent());
    }

    @Test
    public void removeFromParentRemovesNodeFromChildrenCollection() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        removeFromParent();
        Assertions.assertFalse(getChildren().contains(node));
    }

    @Test
    public void removeAllChildrenSetsParentOfChildToEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        removeAllChildren();
        Assertions.assertEquals(Optional.empty(), getParent());
    }

    @Test
    public void removeAllChildrenRemovesAllNodesFromChildrenCollection() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.getNodeAsChild(root);
        removeAllChildren();
        Assertions.assertEquals(Collections.emptyList(), getChildren());
    }

    @Test
    public void getFirstChildAtReturnsCorrect() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        Assertions.assertEquals(node, getFirstChild().get());
    }

    @Test
    public void getFirstChildAtLeafReturnsEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock leaf = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertEquals(Optional.empty(), getFirstChild());
    }

    @Test
    public void isNodeDescendantInFirstLevel() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertTrue(isNodeDescendant(child));
    }

    @Test
    public void isNodeDescendantInComplex() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock descendant = TreeNodeTestData.getNodeInComplexTree(root);
        Assertions.assertTrue(isNodeDescendant(descendant));
    }

    @Test
    public void getChildrenReturnsAllChildren() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        Assertions.assertEquals(Arrays.asList(child1, child2), getChildren());
    }

    @Test
    public void removeChildSetsParentToEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        root.removeChild(node);
        Assertions.assertEquals(Optional.empty(), getParent());
    }

    @Test
    public void removeChildRemovesNodeFromChildrenCollection() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        root.removeChild(node);
        Assertions.assertFalse(getChildren().contains(node));
    }

    @Test
    public void removeChildIndexSetsParentToEmpty() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        removeChild(2);
        Assertions.assertEquals(Optional.empty(), getParent());
    }

    @Test
    public void removeChildIndexRemovesNodeFromChildrenCollection() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        removeChild(2);
        Assertions.assertFalse(getChildren().contains(node));
    }

    @Test
    public void addThrowsExceptionIfNodeHasParent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> root.addChild(node));
    }

    @Test
    public void moveAllChildrenToAddsAtSpecifiedPosition() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        moveAllChildrenTo(root, 0);
        Assertions.assertEquals(Arrays.asList(child1, child2, node), getChildren());
    }

    @Test
    public void moveAllChildrenToChangesParent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = new TreeNodeTestData.TreeNodeMock();
        addChild(node);
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock();
        addChild(child1);
        addChild(child2);
        moveAllChildrenTo(root, 0);
        Assertions.assertEquals(root, getParent().get());
        Assertions.assertEquals(root, getParent().get());
    }

    @Test
    public void moveAllChildrenToDescendantThrowsException() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> root.moveAllChildrenTo(node, 0));
    }

    @Test
    public void sortChildrenSortsInFirstLevel() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock("a");
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock("b");
        TreeNodeTestData.TreeNodeMock child3 = new TreeNodeTestData.TreeNodeMock("c");
        addChild(child2);
        addChild(child3);
        addChild(child1);
        sortChildren(( o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()), false);
        Assertions.assertEquals(Arrays.asList(child1, child2, child3), getChildren());
    }

    @Test
    public void sortChildrenRecursiveSortsInDeeperLevel() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInSimpleTree(root);
        TreeNodeTestData.TreeNodeMock child1 = new TreeNodeTestData.TreeNodeMock("a");
        TreeNodeTestData.TreeNodeMock child2 = new TreeNodeTestData.TreeNodeMock("b");
        TreeNodeTestData.TreeNodeMock child3 = new TreeNodeTestData.TreeNodeMock("c");
        addChild(child2);
        addChild(child3);
        addChild(child1);
        sortChildren(( o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()), true);
        Assertions.assertEquals(Arrays.asList(child1, child2, child3), getChildren());
    }

    @Test
    public void copySubtreeCopiesChildren() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeAsChild(root);
        TreeNodeTestData.TreeNodeMock copiedRoot = copySubtree();
        Assertions.assertEquals(Optional.empty(), getParent());
        Assertions.assertFalse(getChildren().contains(node));
        Assertions.assertEquals(getNumberOfChildren(), getNumberOfChildren());
    }

    @Test
    public void addChildSomewhereInTreeInvokesChangeEvent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        subscribeToDescendantChanged(subscriber);
        addChild(new TreeNodeTestData.TreeNodeMock());
        Mockito.verify(subscriber).accept(node);
    }

    @Test
    public void moveNodeSomewhereInTreeInvokesChangeEvent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        TreeNodeTestData.TreeNodeMock oldParent = getParent().get();
        subscribeToDescendantChanged(subscriber);
        moveTo(root);
        Mockito.verify(subscriber).accept(root);
        Mockito.verify(subscriber).accept(oldParent);
    }

    @Test
    public void removeChildSomewhereInTreeInvokesChangeEvent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        TreeNodeTestData.TreeNodeMock child = node.addChild(new TreeNodeTestData.TreeNodeMock());
        subscribeToDescendantChanged(subscriber);
        node.removeChild(child);
        Mockito.verify(subscriber).accept(node);
    }

    @Test
    public void removeChildIndexSomewhereInTreeInvokesChangeEvent() {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock();
        TreeNodeTestData.TreeNodeMock node = TreeNodeTestData.getNodeInComplexTree(root);
        addChild(new TreeNodeTestData.TreeNodeMock());
        subscribeToDescendantChanged(subscriber);
        removeChild(0);
        Mockito.verify(subscriber).accept(node);
    }

    @Test
    public void findChildrenWithSameName() throws Exception {
        TreeNodeTestData.TreeNodeMock root = new TreeNodeTestData.TreeNodeMock("A");
        TreeNodeTestData.TreeNodeMock childB = root.addChild(new TreeNodeTestData.TreeNodeMock("B"));
        TreeNodeTestData.TreeNodeMock node = childB.addChild(new TreeNodeTestData.TreeNodeMock("A"));
        TreeNodeTestData.TreeNodeMock childA = root.addChild(new TreeNodeTestData.TreeNodeMock("A"));
        Assertions.assertEquals(Arrays.asList(root, node, childA), findChildrenSatisfying(( treeNode) -> treeNode.getName().equals("A")));
    }

    private static class WrongTreeNodeImplementation extends TreeNode<TreeNodeTestData.TreeNodeMock> {
        // This class is a wrong derived class of TreeNode<T>
        // since it does not extends TreeNode<WrongTreeNodeImplementation>
        // See test constructorChecksThatClassImplementsCorrectInterface
        public WrongTreeNodeImplementation() {
            super(TreeNodeTestData.TreeNodeMock.class);
        }

        @Override
        public TreeNodeTestData.TreeNodeMock copyNode() {
            return null;
        }
    }
}


package com.airbnb.epoxy;


import ModelList.ModelListObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class ModelListTest {
    private final ModelListObserver observer = Mockito.mock(ModelListObserver.class);

    private final ModelList modelList = new ModelList();

    @Test
    public void testSet() {
        modelList.set(0, new TestModel());
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
        Mockito.verify(observer).onItemRangeInserted(0, 1);
    }

    @Test
    public void testSetSameIdDoesntNotify() {
        EpoxyModel<?> newModelWithSameId = new TestModel();
        newModelWithSameId.id(modelList.get(0).id());
        modelList.set(0, newModelWithSameId);
        Mockito.verifyNoMoreInteractions(observer);
        Assert.assertEquals(newModelWithSameId, modelList.get(0));
    }

    @Test
    public void testAdd() {
        modelList.add(new TestModel());
        modelList.add(new TestModel());
        Mockito.verify(observer).onItemRangeInserted(3, 1);
        Mockito.verify(observer).onItemRangeInserted(4, 1);
    }

    @Test
    public void testAddAtIndex() {
        modelList.add(0, new TestModel());
        modelList.add(2, new TestModel());
        Mockito.verify(observer).onItemRangeInserted(0, 1);
        Mockito.verify(observer).onItemRangeInserted(2, 1);
    }

    @Test
    public void testAddAll() {
        List<EpoxyModel<?>> newModels = new ArrayList<>();
        newModels.add(new TestModel());
        newModels.add(new TestModel());
        modelList.addAll(newModels);
        Mockito.verify(observer).onItemRangeInserted(3, 2);
    }

    @Test
    public void testAddAllAtIndex() {
        List<EpoxyModel<?>> newModels = new ArrayList<>();
        newModels.add(new TestModel());
        newModels.add(new TestModel());
        modelList.addAll(0, newModels);
        Mockito.verify(observer).onItemRangeInserted(0, 2);
    }

    @Test
    public void testRemoveIndex() {
        EpoxyModel<?> removedModel = modelList.remove(0);
        TestCase.assertFalse(modelList.contains(removedModel));
        Assert.assertEquals(2, modelList.size());
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
    }

    @Test
    public void testRemoveObject() {
        EpoxyModel<?> model = modelList.get(0);
        boolean model1Removed = modelList.remove(model);
        Assert.assertEquals(2, modelList.size());
        TestCase.assertTrue(model1Removed);
        TestCase.assertFalse(modelList.contains(model));
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
    }

    @Test
    public void testRemoveObjectNotAdded() {
        boolean removed = modelList.remove(new TestModel());
        TestCase.assertFalse(removed);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testClear() {
        modelList.clear();
        Mockito.verify(observer).onItemRangeRemoved(0, 3);
    }

    @Test
    public void testClearWhenAlreadyEmpty() {
        modelList.clear();
        modelList.clear();
        Mockito.verify(observer).onItemRangeRemoved(0, 3);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testSublistClear() {
        modelList.subList(0, 2).clear();
        Mockito.verify(observer).onItemRangeRemoved(0, 2);
    }

    @Test
    public void testNoClearWhenEmpty() {
        modelList.clear();
        modelList.clear();
        Mockito.verify(observer).onItemRangeRemoved(0, 3);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testRemoveRange() {
        modelList.removeRange(0, 2);
        Assert.assertEquals(1, modelList.size());
        Mockito.verify(observer).onItemRangeRemoved(0, 2);
    }

    @Test
    public void testRemoveEmptyRange() {
        modelList.removeRange(1, 1);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void testIteratorRemove() {
        Iterator<EpoxyModel<?>> iterator = modelList.iterator();
        iterator.next();
        iterator.remove();
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
    }

    @Test
    public void testRemoveAll() {
        List<EpoxyModel<?>> modelsToRemove = new ArrayList<>();
        modelsToRemove.add(modelList.get(0));
        modelsToRemove.add(modelList.get(1));
        modelList.removeAll(modelsToRemove);
        Mockito.verify(observer, Mockito.times(2)).onItemRangeRemoved(0, 1);
    }

    @Test
    public void testRetainAll() {
        List<EpoxyModel<?>> modelsToRetain = new ArrayList<>();
        modelsToRetain.add(modelList.get(0));
        modelList.retainAll(modelsToRetain);
        Mockito.verify(observer, Mockito.times(2)).onItemRangeRemoved(1, 1);
    }
}


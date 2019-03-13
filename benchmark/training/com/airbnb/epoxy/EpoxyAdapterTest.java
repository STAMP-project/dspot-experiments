package com.airbnb.epoxy;


import RecyclerView.AdapterDataObserver;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class EpoxyAdapterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TestAdapter testAdapter = new TestAdapter();

    private final TestObserver differObserver = new TestObserver();

    @Mock
    AdapterDataObserver observer;

    @Test
    public void testAddModel() {
        addModel(new TestModel());
        Mockito.verify(observer).onItemRangeInserted(0, 1);
        Assert.assertEquals(1, testAdapter.models.size());
        addModel(new TestModel());
        Mockito.verify(observer).onItemRangeInserted(1, 1);
        Assert.assertEquals(2, testAdapter.models.size());
        checkDifferState();
    }

    @Test
    public void testAddModels() {
        List<TestModel> list = new ArrayList<>();
        list.add(new TestModel());
        list.add(new TestModel());
        testAdapter.addModels(list);
        Mockito.verify(observer).onItemRangeInserted(0, 2);
        Assert.assertEquals(2, testAdapter.models.size());
        List<TestModel> list2 = new ArrayList<>();
        list2.add(new TestModel());
        list2.add(new TestModel());
        testAdapter.addModels(list2);
        Mockito.verify(observer).onItemRangeInserted(2, 2);
        Assert.assertEquals(4, testAdapter.models.size());
        checkDifferState();
    }

    @Test
    public void testAddModelsVarArgs() {
        testAdapter.addModels(new TestModel(), new TestModel());
        Mockito.verify(observer).onItemRangeInserted(0, 2);
        Assert.assertEquals(2, testAdapter.models.size());
        testAdapter.addModels(new TestModel(), new TestModel());
        Mockito.verify(observer).onItemRangeInserted(2, 2);
        Assert.assertEquals(4, testAdapter.models.size());
        checkDifferState();
    }

    @Test
    public void testNotifyModelChanged() {
        TestModel testModel = new TestModel();
        addModels(testModel);
        testAdapter.notifyModelChanged(testModel);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        checkDifferState();
    }

    @Test
    public void testNotifyModelChangedWithPayload() {
        Object payload = new Object();
        TestModel testModel = new TestModel();
        addModels(testModel);
        notifyModelChanged(testModel, payload);
        Mockito.verify(observer).onItemRangeChanged(0, 1, payload);
        checkDifferState();
    }

    @Test(expected = IllegalStateException.class)
    public void testInsertModelBeforeThrowsForInvalidModel() {
        insertModelBefore(new TestModel(), new TestModel());
    }

    @Test
    public void testInsertModelBefore() {
        TestModel firstModel = new TestModel();
        addModels(firstModel);
        insertModelBefore(new TestModel(), firstModel);
        Mockito.verify(observer, Mockito.times(2)).onItemRangeInserted(0, 1);
        Assert.assertEquals(2, testAdapter.models.size());
        Assert.assertEquals(firstModel, testAdapter.models.get(1));
        checkDifferState();
    }

    @Test(expected = IllegalStateException.class)
    public void testInsertModelAfterThrowsForInvalidModel() {
        insertModelAfter(new TestModel(), new TestModel());
    }

    @Test
    public void testInsertModelAfter() {
        TestModel firstModel = new TestModel();
        addModels(firstModel);
        insertModelAfter(new TestModel(), firstModel);
        Mockito.verify(observer).onItemRangeInserted(1, 1);
        Assert.assertEquals(2, testAdapter.models.size());
        Assert.assertEquals(firstModel, testAdapter.models.get(0));
        checkDifferState();
    }

    @Test
    public void testRemoveModels() {
        TestModel testModel = new TestModel();
        addModels(testModel);
        removeModel(testModel);
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
        Assert.assertEquals(0, testAdapter.models.size());
        checkDifferState();
    }

    @Test
    public void testRemoveAllModels() {
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            addModels(model);
        }
        removeAllModels();
        Mockito.verify(observer).onItemRangeRemoved(0, 10);
        Assert.assertEquals(0, testAdapter.models.size());
        checkDifferState();
    }

    @Test
    public void testRemoveAllAfterModels() {
        List<TestModel> models = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            models.add(model);
            addModels(model);
        }
        removeAllAfterModel(models.get(5));
        Mockito.verify(observer).onItemRangeRemoved(6, 4);
        Assert.assertEquals(models.subList(0, 6), testAdapter.models);
        checkDifferState();
    }

    @Test
    public void testShowModel() {
        TestModel testModel = new TestModel();
        hide();
        addModels(testModel);
        showModel(testModel);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        TestCase.assertTrue(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModels() {
        TestModel testModel1 = new TestModel();
        hide();
        TestModel testModel2 = new TestModel();
        hide();
        testAdapter.addModels(testModel1, testModel2);
        testAdapter.showModels(testAdapter.models);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertTrue(isShown());
        TestCase.assertTrue(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelsVarArgs() {
        TestModel testModel1 = new TestModel();
        hide();
        TestModel testModel2 = new TestModel();
        hide();
        testAdapter.addModels(testModel1, testModel2);
        testAdapter.showModels(testModel1, testModel2);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertTrue(isShown());
        TestCase.assertTrue(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelsConditionalTrue() {
        TestModel testModel1 = new TestModel();
        hide();
        TestModel testModel2 = new TestModel();
        hide();
        testAdapter.addModels(testModel1, testModel2);
        testAdapter.showModels(testAdapter.models, true);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertTrue(isShown());
        TestCase.assertTrue(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelsVarArgsConditionalTrue() {
        TestModel testModel1 = new TestModel();
        hide();
        TestModel testModel2 = new TestModel();
        hide();
        testAdapter.addModels(testModel1, testModel2);
        showModels(true, testModel1, testModel2);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertTrue(isShown());
        TestCase.assertTrue(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelsConditionalFalse() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        testAdapter.addModels(testModel1, testModel2);
        testAdapter.showModels(testAdapter.models, false);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertFalse(isShown());
        TestCase.assertFalse(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelsVarArgsConditionalFalse() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        testAdapter.addModels(testModel1, testModel2);
        showModels(false, testModel1, testModel2);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertFalse(isShown());
        TestCase.assertFalse(isShown());
        checkDifferState();
    }

    @Test
    public void testShowModelNoopIfAlreadyShown() {
        TestModel testModel = new TestModel();
        addModels(testModel);
        showModel(testModel);
        Mockito.verify(observer, Mockito.times(0)).onItemRangeChanged(0, 1, null);
        TestCase.assertTrue(isShown());
    }

    @Test
    public void testHideModel() {
        TestModel testModel = new TestModel();
        addModels(testModel);
        hideModel(testModel);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        TestCase.assertFalse(isShown());
        checkDifferState();
    }

    @Test
    public void testHideModels() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        testAdapter.addModels(testModel1, testModel2);
        testAdapter.hideModels(testAdapter.models);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertFalse(isShown());
        TestCase.assertFalse(isShown());
        checkDifferState();
    }

    @Test
    public void testHideModelsVarArgs() {
        TestModel testModel1 = new TestModel();
        TestModel testModel2 = new TestModel();
        testAdapter.addModels(testModel1, testModel2);
        hideModels(testModel1, testModel2);
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verify(observer).onItemRangeChanged(1, 1, null);
        TestCase.assertFalse(isShown());
        TestCase.assertFalse(isShown());
        checkDifferState();
    }

    @Test
    public void testHideAllAfterModel() {
        List<TestModel> models = new ArrayList<>();
        int modelCount = 10;
        for (int i = 0; i < modelCount; i++) {
            TestModel model = new TestModel();
            models.add(model);
            addModels(model);
        }
        int hideIndex = 5;
        hideAllAfterModel(models.get(hideIndex));
        for (int i = hideIndex + 1; i < modelCount; i++) {
            Mockito.verify(observer).onItemRangeChanged(i, 1, null);
        }
        for (int i = 0; i < modelCount; i++) {
            Assert.assertEquals((i <= hideIndex), isShown());
        }
        checkDifferState();
    }

    @Test
    public void testThrowIfChangeModelIdAfterNotify() {
        TestModel testModel = new TestModel();
        id(100);
        addModel(testModel);
        thrown.expect(IllegalEpoxyUsage.class);
        thrown.expectMessage("Cannot change a model's id after it has been added to the adapter");
        id(200);
    }

    @Test
    public void testAllowSetSameModelIdAfterNotify() {
        TestModel testModel = new TestModel();
        id(100);
        addModel(testModel);
        id(100);
    }

    @Test
    public void testThrowIfChangeModelIdAfterDiff() {
        TestModel testModel = new TestModel();
        id(100);
        testAdapter.models.add(testModel);
        notifyModelsChanged();
        thrown.expect(IllegalEpoxyUsage.class);
        thrown.expectMessage("Cannot change a model's id after it has been added to the adapter");
        id(200);
    }
}


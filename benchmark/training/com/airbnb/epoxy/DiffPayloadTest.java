package com.airbnb.epoxy;


import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DiffPayloadTest {
    private final List<EpoxyModel<?>> models = new ArrayList<>();

    private BaseEpoxyAdapter adapter;

    private AdapterDataObserver observer;

    @Test
    public void payloadsDisabled() {
        DiffHelper diffHelper = new DiffHelper(adapter, false);
        TestModel firstModel = new TestModel();
        models.add(firstModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeInserted(0, 1);
        TestModel updatedFirstModel = firstModel.clone().incrementValue();
        models.clear();
        models.add(updatedFirstModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeChanged(0, 1, null);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void noPayloadsForNoChanges() {
        DiffHelper diffHelper = new DiffHelper(adapter, true);
        TestModel firstModel = new TestModel();
        models.add(firstModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeInserted(0, 1);
        models.clear();
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeRemoved(0, 1);
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void singlePayload() {
        DiffHelper diffHelper = new DiffHelper(adapter, true);
        TestModel firstModel = new TestModel();
        models.add(firstModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeInserted(0, 1);
        models.clear();
        TestModel changedFirstModel = firstModel.clone().incrementValue();
        this.models.add(changedFirstModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.argThat(new DiffPayloadTest.DiffPayloadMatcher(firstModel)));
        Mockito.verifyNoMoreInteractions(observer);
    }

    @Test
    public void batchPayload() {
        DiffHelper diffHelper = new DiffHelper(adapter, true);
        TestModel firstModel = new TestModel();
        TestModel secondModel = new TestModel();
        models.add(firstModel);
        models.add(secondModel);
        diffHelper.notifyModelChanges();
        TestModel changedFirstModel = firstModel.clone().incrementValue();
        TestModel changedSecondModel = secondModel.clone().incrementValue();
        models.clear();
        models.add(changedFirstModel);
        models.add(changedSecondModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(2), ArgumentMatchers.argThat(new DiffPayloadTest.DiffPayloadMatcher(firstModel, secondModel)));
    }

    @Test
    public void multiplePayloads() {
        DiffHelper diffHelper = new DiffHelper(adapter, true);
        TestModel firstModel = new TestModel();
        TestModel secondModel = new TestModel();
        TestModel thirdModel = new TestModel();
        models.add(firstModel);
        models.add(thirdModel);
        diffHelper.notifyModelChanges();
        TestModel changedFirstModel = firstModel.clone().incrementValue();
        TestModel changedThirdModel = thirdModel.clone().incrementValue();
        models.clear();
        models.add(changedFirstModel);
        models.add(secondModel);
        models.add(changedThirdModel);
        diffHelper.notifyModelChanges();
        Mockito.verify(observer).onItemRangeChanged(ArgumentMatchers.eq(0), ArgumentMatchers.eq(1), ArgumentMatchers.argThat(new DiffPayloadTest.DiffPayloadMatcher(firstModel)));
        Mockito.verify(observer).onItemRangeChanged(ArgumentMatchers.eq(2), ArgumentMatchers.eq(1), ArgumentMatchers.argThat(new DiffPayloadTest.DiffPayloadMatcher(thirdModel)));
    }

    @Test
    public void getSingleModelFromPayload() {
        TestModel model = new TestModel();
        List<Object> payloads = DiffPayloadTest.payloadsWithChangedModels(model);
        EpoxyModel<?> modelFromPayload = DiffPayload.getModelFromPayload(payloads, id());
        Assert.assertEquals(model, modelFromPayload);
    }

    @Test
    public void returnsNullWhenNoModelFoundInPayload() {
        TestModel model = new TestModel();
        List<Object> payloads = DiffPayloadTest.payloadsWithChangedModels(model);
        EpoxyModel<?> modelFromPayload = DiffPayload.getModelFromPayload(payloads, ((id()) - 1));
        Assert.assertNull(modelFromPayload);
    }

    @Test
    public void returnsNullForEmptyPayload() {
        List<Object> payloads = new ArrayList<>();
        EpoxyModel<?> modelFromPayload = DiffPayload.getModelFromPayload(payloads, 2);
        Assert.assertNull(modelFromPayload);
    }

    @Test
    public void getMultipleModelsFromPayload() {
        TestModel model1 = new TestModel();
        TestModel model2 = new TestModel();
        List<Object> payloads = DiffPayloadTest.payloadsWithChangedModels(model1, model2);
        EpoxyModel<?> modelFromPayload1 = DiffPayload.getModelFromPayload(payloads, id());
        EpoxyModel<?> modelFromPayload2 = DiffPayload.getModelFromPayload(payloads, id());
        Assert.assertEquals(model1, modelFromPayload1);
        Assert.assertEquals(model2, modelFromPayload2);
    }

    @Test
    public void getSingleModelsFromMultipleDiffPayloads() {
        TestModel model1 = new TestModel();
        DiffPayload diffPayload1 = DiffPayloadTest.diffPayloadWithModels(model1);
        TestModel model2 = new TestModel();
        DiffPayload diffPayload2 = DiffPayloadTest.diffPayloadWithModels(model2);
        List<Object> payloads = DiffPayloadTest.payloadsWithDiffPayloads(diffPayload1, diffPayload2);
        EpoxyModel<?> modelFromPayload1 = DiffPayload.getModelFromPayload(payloads, id());
        EpoxyModel<?> modelFromPayload2 = DiffPayload.getModelFromPayload(payloads, id());
        Assert.assertEquals(model1, modelFromPayload1);
        Assert.assertEquals(model2, modelFromPayload2);
    }

    @Test
    public void getMultipleModelsFromMultipleDiffPayloads() {
        TestModel model1Payload1 = new TestModel(1);
        TestModel model2Payload1 = new TestModel(2);
        DiffPayload diffPayload1 = DiffPayloadTest.diffPayloadWithModels(model1Payload1, model2Payload1);
        TestModel model1Payload2 = new TestModel(3);
        TestModel model2Payload2 = new TestModel(4);
        DiffPayload diffPayload2 = DiffPayloadTest.diffPayloadWithModels(model1Payload2, model2Payload2);
        List<Object> payloads = DiffPayloadTest.payloadsWithDiffPayloads(diffPayload1, diffPayload2);
        EpoxyModel<?> model1FromPayload1 = DiffPayload.getModelFromPayload(payloads, id());
        EpoxyModel<?> model2FromPayload1 = DiffPayload.getModelFromPayload(payloads, id());
        EpoxyModel<?> model1FromPayload2 = DiffPayload.getModelFromPayload(payloads, id());
        EpoxyModel<?> model2FromPayload2 = DiffPayload.getModelFromPayload(payloads, id());
        Assert.assertEquals(model1Payload1, model1FromPayload1);
        Assert.assertEquals(model2Payload1, model2FromPayload1);
        Assert.assertEquals(model1Payload2, model1FromPayload2);
        Assert.assertEquals(model2Payload2, model2FromPayload2);
    }

    static class DiffPayloadMatcher implements ArgumentMatcher<DiffPayload> {
        private final DiffPayload expectedPayload;

        DiffPayloadMatcher(EpoxyModel<?>... changedModels) {
            List<EpoxyModel<?>> epoxyModels = Arrays.asList(changedModels);
            expectedPayload = new DiffPayload(epoxyModels);
        }

        @Override
        public boolean matches(DiffPayload argument) {
            return expectedPayload.equalsForTesting(argument);
        }
    }
}


package com.airbnb.epoxy;


import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


/**
 * Tests that changes to the models via notify calls besides
 * {@link EpoxyAdapter#notifyModelsChanged()}
 * will properly update the model state maintained by the differ.
 */
@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DifferNotifyTest {
    private static final int INITIAL_MODEL_COUNT = 20;

    private static final boolean SHOW_LOGS = false;

    private final TestObserver testObserver = new TestObserver(DifferNotifyTest.SHOW_LOGS);

    private final TestAdapter adapter = new TestAdapter();

    private final List<EpoxyModel<?>> models = adapter.models;

    @Test(expected = UnsupportedOperationException.class)
    public void notifyChange() {
        notifyDataSetChanged();
    }

    @Test
    public void notifyAddedToEmpty() {
        ModelTestUtils.addModels(models);
        adapter.notifyItemRangeInserted(0, models.size());
        assertCorrectness();
    }

    @Test
    public void notifyAddedToStart() {
        addInitialModels();
        ModelTestUtils.addModels(models, 0);
        adapter.notifyItemRangeInserted(0, ((models.size()) - (DifferNotifyTest.INITIAL_MODEL_COUNT)));
        assertCorrectness();
    }

    @Test
    public void notifyAddedToEnd() {
        addInitialModels();
        ModelTestUtils.addModels(models, DifferNotifyTest.INITIAL_MODEL_COUNT);
        adapter.notifyItemRangeInserted(DifferNotifyTest.INITIAL_MODEL_COUNT, ((models.size()) - (DifferNotifyTest.INITIAL_MODEL_COUNT)));
        assertCorrectness();
    }

    @Test
    public void notifyAddedToMiddle() {
        addInitialModels();
        ModelTestUtils.addModels(models, ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        adapter.notifyItemRangeInserted(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2), ((models.size()) - (DifferNotifyTest.INITIAL_MODEL_COUNT)));
        assertCorrectness();
    }

    @Test
    public void notifyRemoveAll() {
        addInitialModels();
        models.clear();
        notifyItemRangeRemoved(0, DifferNotifyTest.INITIAL_MODEL_COUNT);
        assertCorrectness();
    }

    @Test
    public void notifyRemoveStart() {
        addInitialModels();
        ModelTestUtils.remove(models, 0, ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        notifyItemRangeRemoved(0, ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        assertCorrectness();
    }

    @Test
    public void notifyRemoveMiddle() {
        addInitialModels();
        ModelTestUtils.remove(models, ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 3), ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 3));
        notifyItemRangeRemoved(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 3), ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 3));
        assertCorrectness();
    }

    @Test
    public void notifyRemoveEnd() {
        addInitialModels();
        ModelTestUtils.remove(models, ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2), ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        notifyItemRangeRemoved(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2), ((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        assertCorrectness();
    }

    @Test
    public void notifyFrontMovedToEnd() {
        addInitialModels();
        EpoxyModel<?> modelToMove = models.remove(0);
        models.add(modelToMove);
        notifyItemMoved(0, ((DifferNotifyTest.INITIAL_MODEL_COUNT) - 1));
        assertCorrectness();
    }

    @Test
    public void notifyEndMovedToFront() {
        addInitialModels();
        EpoxyModel<?> modelToMove = models.remove(((DifferNotifyTest.INITIAL_MODEL_COUNT) - 1));
        models.add(0, modelToMove);
        notifyItemMoved(((DifferNotifyTest.INITIAL_MODEL_COUNT) - 1), 0);
        assertCorrectness();
    }

    @Test
    public void notifyMiddleMovedToEnd() {
        addInitialModels();
        EpoxyModel<?> modelToMove = models.remove(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        models.add(modelToMove);
        notifyItemMoved(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2), ((DifferNotifyTest.INITIAL_MODEL_COUNT) - 1));
        assertCorrectness();
    }

    @Test
    public void notifyMiddleMovedToFront() {
        addInitialModels();
        EpoxyModel<?> modelToMove = models.remove(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2));
        models.add(0, modelToMove);
        notifyItemMoved(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 2), 0);
        assertCorrectness();
    }

    @Test
    public void notifyValuesUpdated() {
        addInitialModels();
        int numModelsUpdated = 0;
        for (int i = (DifferNotifyTest.INITIAL_MODEL_COUNT) / 3; i < (((DifferNotifyTest.INITIAL_MODEL_COUNT) * 2) / 3); i++) {
            ModelTestUtils.changeValue(models.get(i));
            numModelsUpdated++;
        }
        notifyItemRangeChanged(((DifferNotifyTest.INITIAL_MODEL_COUNT) / 3), numModelsUpdated);
        assertCorrectness();
    }
}


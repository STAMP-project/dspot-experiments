package com.airbnb.epoxy;


import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class DifferCorrectnessTest {
    private static final boolean SHOW_LOGS = false;

    /**
     * If true, will log the time taken on the diff and skip the validation since that takes a long
     * time for big change sets.
     */
    private static final boolean SPEED_RUN = false;

    private final TestObserver testObserver = new TestObserver(DifferCorrectnessTest.SHOW_LOGS);

    private final TestAdapter testAdapter = new TestAdapter();

    private final List<EpoxyModel<?>> models = testAdapter.models;

    private static long totalDiffMillis = 0;

    private static long totalDiffOperations = 0;

    private static long totalDiffs = 0;

    @Test
    public void noChange() {
        diffAndValidateWithOpCount(0);
    }

    @Test
    public void simpleUpdate() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.changeValues(models);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void updateStart() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.changeValues(models, 0, ((models.size()) / 2));
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void updateMiddle() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.changeValues(models, ((models.size()) / 3), (((models.size()) * 2) / 3));
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void updateEnd() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.changeValues(models, ((models.size()) / 2), models.size());
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void shuffle() {
        // Tries all permutations of item shuffles, with various list sizes. Also randomizes
        // item values so that the diff must deal with both item updates and movements
        for (int i = 0; i < 9; i++) {
            List<EpoxyModel<?>> originalModels = new ArrayList<>();
            ModelTestUtils.addModels(i, originalModels);
            int permutationNumber = 0;
            for (List<EpoxyModel<?>> permutedModels : Collections2.permutations(originalModels)) {
                permutationNumber++;
                // Resetting to the original models each time, otherwise each subsequent permutation is
                // only a small difference
                models.clear();
                models.addAll(originalModels);
                diffAndValidate();
                models.clear();
                models.addAll(permutedModels);
                ModelTestUtils.changeValues(models);
                log((((("\n\n***** Permutation " + permutationNumber) + " - List Size: ") + i) + " ****** \n"));
                log(("old models:\n" + (models)));
                log("\n");
                log(("new models:\n" + (models)));
                log("\n");
                diffAndValidate();
            }
        }
    }

    @Test
    public void swapEnds() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        EpoxyModel<?> firstModel = models.remove(0);
        EpoxyModel<?> lastModel = models.remove(((models.size()) - 1));
        models.add(0, lastModel);
        models.add(firstModel);
        diffAndValidateWithOpCount(2);
    }

    @Test
    public void moveFrontToEnd() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        EpoxyModel<?> firstModel = models.remove(0);
        models.add(firstModel);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void moveEndToFront() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        EpoxyModel<?> lastModel = models.remove(((models.size()) - 1));
        models.add(0, lastModel);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void moveEndToFrontAndChangeValues() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        EpoxyModel<?> lastModel = models.remove(((models.size()) - 1));
        models.add(0, lastModel);
        ModelTestUtils.changeValues(models);
        diffAndValidateWithOpCount(2);
    }

    @Test
    public void swapHalf() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        List<EpoxyModel<?>> firstHalf = models.subList(0, ((models.size()) / 2));
        ArrayList<EpoxyModel<?>> firstHalfCopy = new ArrayList(firstHalf);
        firstHalf.clear();
        models.addAll(firstHalfCopy);
        diffAndValidateWithOpCount(firstHalfCopy.size());
    }

    @Test
    public void reverse() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        Collections.reverse(models);
        diffAndValidate();
    }

    @Test
    public void removeAll() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        models.clear();
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void removeEnd() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        int half = (models.size()) / 2;
        ModelTestUtils.remove(models, half, half);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void removeMiddle() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        int third = (models.size()) / 3;
        ModelTestUtils.remove(models, third, third);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void removeStart() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        int half = (models.size()) / 2;
        ModelTestUtils.remove(models, 0, half);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void multipleRemovals() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        int size = models.size();
        int tenth = size / 10;
        // Remove a tenth of the models at the end, middle, and start
        ModelTestUtils.removeModelsAfterPosition(models, (size - tenth));
        ModelTestUtils.remove(models, (size / 2), tenth);
        ModelTestUtils.remove(models, 0, tenth);
        diffAndValidateWithOpCount(3);
    }

    @Test
    public void simpleAdd() {
        ModelTestUtils.addModels(models);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void addToStart() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.addModels(models, 0);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void addToMiddle() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.addModels(models, ((models.size()) / 2));
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void addToEnd() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.addModels(models);
        diffAndValidateWithOpCount(1);
    }

    @Test
    public void multipleInsertions() {
        ModelTestUtils.addModels(models);
        diffAndValidate();
        ModelTestUtils.addModels(models, 0);
        ModelTestUtils.addModels(models, (((models.size()) * 2) / 3));
        ModelTestUtils.addModels(models);
        diffAndValidateWithOpCount(3);
    }

    @Test
    public void moveTwoInFrontOfInsertion() {
        ModelTestUtils.addModels(4, models);
        diffAndValidate();
        ModelTestUtils.addModels(1, models, 0);
        EpoxyModel<?> lastModel = models.remove(((models.size()) - 1));
        models.add(0, lastModel);
        lastModel = models.remove(((models.size()) - 1));
        models.add(0, lastModel);
        diffAndValidate();
    }

    @Test
    public void randomCombinations() {
        int maxBatchSize = 3;
        int maxModelCount = 10;
        int maxSeed = 100000;
        // This modifies the models list in a random way many times, with different size lists.
        for (int modelCount = 1; modelCount < maxModelCount; modelCount++) {
            for (int randomSeed = 0; randomSeed < maxSeed; randomSeed++) {
                log((((("\n\n*** Combination seed " + randomSeed) + " Model Count: ") + modelCount) + " *** \n"));
                // We keep the list from the previous loop and keep modifying it. This allows us to test
                // that state is maintained properly between diffs. We just make sure the list size
                // says the same by adding or removing if necessary
                int currentModelCount = models.size();
                if (currentModelCount < modelCount) {
                    ModelTestUtils.addModels((modelCount - currentModelCount), models);
                } else
                    if (currentModelCount > modelCount) {
                        ModelTestUtils.removeModelsAfterPosition(models, modelCount);
                    }

                diffAndValidate();
                modifyModelsRandomly(models, maxBatchSize, new Random(randomSeed));
                log("\nResulting diff: \n");
                diffAndValidate();
            }
        }
    }
}


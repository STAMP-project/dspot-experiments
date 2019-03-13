package com.airbnb.epoxy;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class UpdateOpHelperTest {
    private final UpdateOpHelper helper = new UpdateOpHelper();

    @Test
    public void insertionBatch() {
        helper.add(0);// New batch

        helper.add(1);// Add at the end

        helper.add(0);// Add at the start

        helper.add(1);// Add in the middle

        Assert.assertEquals(1, helper.getNumInsertionBatches());
        Assert.assertEquals(4, helper.getNumInsertions());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(1, opList.size());
        Assert.assertEquals(0, opList.get(0).positionStart);
        Assert.assertEquals(4, opList.get(0).itemCount);
        Assert.assertEquals(0, helper.getNumRemovalBatches());
        Assert.assertEquals(0, helper.getNumRemovals());
        Assert.assertEquals(0, helper.getNumMoves());
    }

    @Test
    public void insertionMultipleBatches() {
        helper.add(1);// New batch

        helper.add(3);// New batch

        helper.add(1);// New batch

        helper.add(0);// New batch

        Assert.assertEquals(4, helper.getNumInsertionBatches());
        Assert.assertEquals(4, helper.getNumInsertions());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(4, opList.size());
        Assert.assertEquals(1, opList.get(0).positionStart);
        Assert.assertEquals(1, opList.get(0).itemCount);
        Assert.assertEquals(3, opList.get(1).positionStart);
        Assert.assertEquals(1, opList.get(1).itemCount);
        Assert.assertEquals(1, opList.get(2).positionStart);
        Assert.assertEquals(1, opList.get(2).itemCount);
        Assert.assertEquals(0, opList.get(3).positionStart);
        Assert.assertEquals(1, opList.get(3).itemCount);
    }

    @Test
    public void insertionBatchRanges() {
        helper.add(1, 2);
        helper.add(1, 1);
        helper.add(4, 1);
        Assert.assertEquals(1, helper.getNumInsertionBatches());
        Assert.assertEquals(4, helper.getNumInsertions());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(1, opList.size());
        Assert.assertEquals(1, opList.get(0).positionStart);
        Assert.assertEquals(4, opList.get(0).itemCount);
    }

    @Test
    public void removeBatch() {
        helper.remove(3);// New batch

        helper.remove(3);// Remove at the end

        helper.remove(2);// Remove at the start

        Assert.assertEquals(1, helper.getNumRemovalBatches());
        Assert.assertEquals(3, helper.getNumRemovals());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(1, opList.size());
        Assert.assertEquals(2, opList.get(0).positionStart);
        Assert.assertEquals(3, opList.get(0).itemCount);
        Assert.assertEquals(0, helper.getNumInsertionBatches());
        Assert.assertEquals(0, helper.getNumInsertions());
        Assert.assertEquals(0, helper.getNumMoves());
    }

    @Test
    public void removeMultipleBatches() {
        helper.remove(3);
        helper.remove(4);
        helper.remove(2);
        Assert.assertEquals(3, helper.getNumRemovalBatches());
        Assert.assertEquals(3, helper.getNumRemovals());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(3, opList.size());
        Assert.assertEquals(3, opList.get(0).positionStart);
        Assert.assertEquals(1, opList.get(0).itemCount);
        Assert.assertEquals(4, opList.get(1).positionStart);
        Assert.assertEquals(1, opList.get(1).itemCount);
        Assert.assertEquals(2, opList.get(2).positionStart);
        Assert.assertEquals(1, opList.get(2).itemCount);
    }

    @Test
    public void removeBatchRange() {
        helper.remove(3, 2);
        helper.remove(3, 2);
        helper.remove(0, 3);
        Assert.assertEquals(1, helper.getNumRemovalBatches());
        Assert.assertEquals(7, helper.getNumRemovals());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(1, opList.size());
        Assert.assertEquals(0, opList.get(0).positionStart);
        Assert.assertEquals(7, opList.get(0).itemCount);
    }

    @Test
    public void update() {
        helper.update(1);// New Batch

        helper.update(0);// Update at start of batch

        helper.update(2);// Update at end of batch

        helper.update(0);// Update same item as before (shouldn't be added to batch length)

        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(1, opList.size());
        Assert.assertEquals(0, opList.get(0).positionStart);
        Assert.assertEquals(3, opList.get(0).itemCount);
        Assert.assertEquals(0, helper.getNumInsertionBatches());
        Assert.assertEquals(0, helper.getNumInsertions());
        Assert.assertEquals(0, helper.getNumRemovalBatches());
        Assert.assertEquals(0, helper.getNumRemovals());
        Assert.assertEquals(0, helper.getNumMoves());
    }

    @Test
    public void updateMultipleBatches() {
        helper.update(3);
        helper.update(5);
        helper.update(3);
        helper.update(0);
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(4, opList.size());
        Assert.assertEquals(3, opList.get(0).positionStart);
        Assert.assertEquals(1, opList.get(0).itemCount);
        Assert.assertEquals(5, opList.get(1).positionStart);
        Assert.assertEquals(1, opList.get(1).itemCount);
        Assert.assertEquals(3, opList.get(2).positionStart);
        Assert.assertEquals(1, opList.get(2).itemCount);
        Assert.assertEquals(0, opList.get(3).positionStart);
        Assert.assertEquals(1, opList.get(3).itemCount);
        Assert.assertEquals(0, helper.getNumInsertionBatches());
        Assert.assertEquals(0, helper.getNumInsertions());
        Assert.assertEquals(0, helper.getNumRemovalBatches());
        Assert.assertEquals(0, helper.getNumRemovals());
        Assert.assertEquals(0, helper.getNumMoves());
    }

    @Test
    public void moves() {
        helper.move(0, 3);
        helper.move(0, 4);
        Assert.assertEquals(2, helper.getNumMoves());
        Assert.assertEquals(0, helper.getNumInsertionBatches());
        Assert.assertEquals(0, helper.getNumInsertions());
        Assert.assertEquals(0, helper.getNumRemovalBatches());
        Assert.assertEquals(0, helper.getNumRemovals());
        List<UpdateOp> opList = helper.opList;
        Assert.assertEquals(2, opList.size());
        Assert.assertEquals(0, opList.get(0).positionStart);
        Assert.assertEquals(3, opList.get(0).itemCount);
        Assert.assertEquals(0, opList.get(0).positionStart);
        Assert.assertEquals(3, opList.get(0).itemCount);
    }
}


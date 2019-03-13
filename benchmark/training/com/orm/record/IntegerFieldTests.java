package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.IntegerFieldAnnotatedModel;
import com.orm.model.IntegerFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class IntegerFieldTests {
    private Integer integer = 25;

    @Test
    public void nullIntegerExtendedTest() {
        SugarRecord.save(new IntegerFieldExtendedModel());
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        Assert.assertNull(model.getInteger());
    }

    @Test
    public void nullIntExtendedTest() {
        SugarRecord.save(new IntegerFieldExtendedModel());
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        Assert.assertEquals(0, model.getInt());
    }

    @Test
    public void nullIntegerAnnotatedTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel());
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getInteger());
    }

    @Test
    public void nullIntAnnotatedTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel());
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        Assert.assertEquals(0, model.getInt());
    }

    @Test
    public void integerExtendedTest() {
        SugarRecord.save(new IntegerFieldExtendedModel(integer));
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        Assert.assertEquals(integer, model.getInteger());
    }

    @Test
    public void intExtendedTest() {
        SugarRecord.save(new IntegerFieldExtendedModel(integer.intValue()));
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        Assert.assertEquals(integer.intValue(), model.getInt());
    }

    @Test
    public void integerAnnotatedTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer));
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        Assert.assertEquals(integer, model.getInteger());
    }

    @Test
    public void intAnnotatedTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        Assert.assertEquals(integer.intValue(), model.getInt());
    }

    @Test
    public void sumTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        Assert.assertEquals((2 * (integer)), SugarRecord.sum(IntegerFieldAnnotatedModel.class, "raw_integer"));
    }

    @Test
    public void whereSumTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        Assert.assertEquals(((long) (integer)), SugarRecord.sum(IntegerFieldAnnotatedModel.class, "raw_integer", "id = ?", "1"));
    }

    @Test
    public void noSumTest() {
        Assert.assertEquals(0, SugarRecord.sum(IntegerFieldAnnotatedModel.class, "raw_integer"));
    }

    @Test
    public void brokenSumTest() {
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        SugarRecord.save(new IntegerFieldAnnotatedModel(integer.intValue()));
        Assert.assertEquals((-1), SugarRecord.sum(IntegerFieldAnnotatedModel.class, "wrongfield"));
    }
}


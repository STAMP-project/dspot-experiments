package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.BooleanFieldAnnotatedModel;
import com.orm.model.BooleanFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class BooleanFieldTests {
    @Test
    public void nullBooleanExtendedTest() {
        SugarRecord.save(new BooleanFieldExtendedModel());
        BooleanFieldExtendedModel model = SugarRecord.findById(BooleanFieldExtendedModel.class, 1);
        Assert.assertNull(model.getBoolean());
    }

    @Test
    public void nullRawBooleanExtendedTest() {
        SugarRecord.save(new BooleanFieldExtendedModel());
        BooleanFieldExtendedModel model = SugarRecord.findById(BooleanFieldExtendedModel.class, 1);
        Assert.assertEquals(false, model.getRawBoolean());
    }

    @Test
    public void nullBooleanAnnotatedTest() {
        SugarRecord.save(new BooleanFieldAnnotatedModel());
        BooleanFieldAnnotatedModel model = SugarRecord.findById(BooleanFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getBoolean());
    }

    @Test
    public void nullRawBooleanAnnotatedTest() {
        SugarRecord.save(new BooleanFieldAnnotatedModel());
        BooleanFieldAnnotatedModel model = SugarRecord.findById(BooleanFieldAnnotatedModel.class, 1);
        Assert.assertEquals(false, model.getRawBoolean());
    }

    // //TODO check this method
    // @Test
    // public void objectBooleanExtendedTest() {
    // save(new BooleanFieldExtendedModel(true));
    // BooleanFieldExtendedModel model = SugarRecord.findById(BooleanFieldExtendedModel.class, 1);
    // assertEquals(true, model.getBoolean());
    // }
    @Test
    public void rawBooleanExtendedTest() {
        SugarRecord.save(new BooleanFieldExtendedModel(true));
        BooleanFieldExtendedModel model = SugarRecord.findById(BooleanFieldExtendedModel.class, 1);
        Assert.assertEquals(true, model.getRawBoolean());
    }

    // //TODO check this
    // @Test
    // public void objectBooleanAnnotatedTest() {
    // save(new BooleanFieldAnnotatedModel(true));
    // BooleanFieldAnnotatedModel model = SugarRecord.findById(BooleanFieldAnnotatedModel.class, 1);
    // 
    // if (null != model) {
    // assertEquals(true, model.getBoolean());
    // }
    // }
    @Test
    public void rawBooleanAnnotatedTest() {
        SugarRecord.save(new BooleanFieldAnnotatedModel(true));
        BooleanFieldAnnotatedModel model = SugarRecord.findById(BooleanFieldAnnotatedModel.class, 1);
        Assert.assertEquals(true, model.getRawBoolean());
    }
}


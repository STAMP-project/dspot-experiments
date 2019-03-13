package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.FloatFieldAnnotatedModel;
import com.orm.model.FloatFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@SuppressWarnings("all")
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class FloatFieldTests {
    Float aFloat = Float.valueOf(25.0F);

    @Test
    public void nullFloatExtendedTest() {
        SugarRecord.save(new FloatFieldExtendedModel());
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        Assert.assertNull(model.getFloat());
    }

    @Test
    public void nullRawFloatExtendedTest() {
        SugarRecord.save(new FloatFieldExtendedModel());
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        Assert.assertEquals(0.0F, model.getRawFloat(), 1.0E-10F);
    }

    @Test
    public void nullFloatAnnotatedTest() {
        SugarRecord.save(new FloatFieldAnnotatedModel());
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getFloat());
    }

    @Test
    public void nullRawFloatAnnotatedTest() {
        SugarRecord.save(new FloatFieldAnnotatedModel());
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        Assert.assertEquals(0.0F, model.getRawFloat(), 1.0E-10F);
    }

    @Test
    public void objectFloatExtendedTest() {
        SugarRecord.save(new FloatFieldExtendedModel(aFloat));
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        Assert.assertEquals(aFloat, model.getFloat());
    }

    @Test
    public void rawFloatExtendedTest() {
        SugarRecord.save(new FloatFieldExtendedModel(aFloat.floatValue()));
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        Assert.assertEquals(aFloat.floatValue(), model.getRawFloat(), 1.0E-10F);
    }

    @Test
    public void objectFloatAnnotatedTest() {
        SugarRecord.save(new FloatFieldAnnotatedModel(aFloat));
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        Assert.assertEquals(aFloat, model.getFloat());
    }

    @Test
    public void rawFloatAnnotatedTest() {
        SugarRecord.save(new FloatFieldAnnotatedModel(aFloat.floatValue()));
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        Assert.assertEquals(aFloat.floatValue(), model.getRawFloat(), 1.0E-10F);
    }
}


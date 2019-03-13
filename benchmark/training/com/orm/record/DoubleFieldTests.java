package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.DoubleFieldAnnotatedModel;
import com.orm.model.DoubleFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class DoubleFieldTests {
    @Test
    public void nullDoubleExtendedTest() {
        SugarRecord.save(new DoubleFieldExtendedModel());
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        Assert.assertNull(model.getDouble());
    }

    @Test
    public void nullRawDoubleExtendedTest() {
        SugarRecord.save(new DoubleFieldExtendedModel());
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        Assert.assertEquals(0.0, model.getRawDouble(), 1.0E-10);
    }

    @Test
    public void nullDoubleAnnotatedTest() {
        SugarRecord.save(new DoubleFieldAnnotatedModel());
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getDouble());
    }

    @Test
    public void nullRawDoubleAnnotatedTest() {
        SugarRecord.save(new DoubleFieldAnnotatedModel());
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        Assert.assertEquals(0.0, model.getRawDouble(), 1.0E-10);
    }

    @Test
    @SuppressWarnings("all")
    public void objectDoubleExtendedTest() {
        Double objectDouble = Double.valueOf(25.0);
        SugarRecord.save(new DoubleFieldExtendedModel(objectDouble));
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        Assert.assertEquals(objectDouble, model.getDouble());
    }

    @Test
    public void rawDoubleExtendedTest() {
        SugarRecord.save(new DoubleFieldExtendedModel(25.0));
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        Assert.assertEquals(25.0, model.getRawDouble(), 1.0E-10);
    }

    @Test
    @SuppressWarnings("all")
    public void objectDoubleAnnotatedTest() {
        Double objectDouble = Double.valueOf(25.0);
        SugarRecord.save(new DoubleFieldAnnotatedModel(objectDouble));
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        Assert.assertEquals(objectDouble, model.getDouble());
    }

    @Test
    public void rawDoubleAnnotatedTest() {
        SugarRecord.save(new DoubleFieldAnnotatedModel(25.0));
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        Assert.assertEquals(25.0, model.getRawDouble(), 1.0E-10);
    }
}


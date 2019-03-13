package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.StringFieldAnnotatedModel;
import com.orm.model.StringFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class StringFieldTests {
    private String string = "Test String";

    @Test
    public void nullStringExtendedTest() {
        SugarRecord.save(new StringFieldExtendedModel());
        StringFieldExtendedModel model = SugarRecord.findById(StringFieldExtendedModel.class, 1);
        Assert.assertNull(model.getString());
    }

    @Test
    public void nullStringAnnotatedTest() {
        SugarRecord.save(new StringFieldAnnotatedModel());
        StringFieldAnnotatedModel model = SugarRecord.findById(StringFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getString());
    }

    @Test
    public void stringExtendedTest() {
        SugarRecord.save(new StringFieldExtendedModel(string));
        StringFieldExtendedModel model = SugarRecord.findById(StringFieldExtendedModel.class, 1);
        Assert.assertEquals(string, model.getString());
    }

    @Test
    public void stringAnnotatedTest() {
        SugarRecord.save(new StringFieldAnnotatedModel(string));
        StringFieldAnnotatedModel model = SugarRecord.findById(StringFieldAnnotatedModel.class, 1);
        Assert.assertEquals(string, model.getString());
    }
}


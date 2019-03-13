package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.EnumFieldAnnotatedModel;
import com.orm.model.EnumFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.orm.model.EnumFieldExtendedModel.DefaultEnum.TWO;
import static com.orm.model.EnumFieldExtendedModel.OverrideEnum.ONE;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class EnumFieldTests {
    @Test
    public void nullDefaultEnumExtendedTest() {
        SugarRecord.save(new EnumFieldExtendedModel());
        EnumFieldExtendedModel model = SugarRecord.findById(EnumFieldExtendedModel.class, 1);
        Assert.assertNull(model.getDefaultEnum());
    }

    @Test
    public void nullOverriddenEnumExtendedTest() {
        SugarRecord.save(new EnumFieldExtendedModel());
        EnumFieldExtendedModel model = SugarRecord.findById(EnumFieldExtendedModel.class, 1);
        Assert.assertNull(model.getOverrideEnum());
    }

    @Test
    public void nullDefaultEnumAnnotatedTest() {
        SugarRecord.save(new EnumFieldAnnotatedModel());
        EnumFieldAnnotatedModel model = SugarRecord.findById(EnumFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getDefaultEnum());
    }

    @Test
    public void nullOverriddenEnumAnnotatedTest() {
        SugarRecord.save(new EnumFieldAnnotatedModel());
        EnumFieldAnnotatedModel model = SugarRecord.findById(EnumFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getOverrideEnum());
    }

    @Test
    public void defaultEnumExtendedTest() {
        SugarRecord.save(new EnumFieldExtendedModel(ONE, TWO));
        EnumFieldExtendedModel model = SugarRecord.findById(EnumFieldExtendedModel.class, 1);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getDefaultEnum(), TWO);
    }

    @Test
    public void overriddenEnumExtendedTest() {
        SugarRecord.save(new EnumFieldExtendedModel(ONE, TWO));
        EnumFieldExtendedModel model = SugarRecord.findById(EnumFieldExtendedModel.class, 1);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getOverrideEnum(), ONE);
    }

    @Test
    public void defaultEnumAnnotatedTest() {
        SugarRecord.save(new EnumFieldAnnotatedModel(EnumFieldAnnotatedModel.OverrideEnum.ONE, EnumFieldAnnotatedModel.DefaultEnum.TWO));
        EnumFieldAnnotatedModel model = SugarRecord.findById(EnumFieldAnnotatedModel.class, 1);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getDefaultEnum(), EnumFieldAnnotatedModel.DefaultEnum.TWO);
    }

    @Test
    public void overriddenEnumAnnotatedTest() {
        SugarRecord.save(new EnumFieldAnnotatedModel(EnumFieldAnnotatedModel.OverrideEnum.ONE, EnumFieldAnnotatedModel.DefaultEnum.TWO));
        EnumFieldAnnotatedModel model = SugarRecord.findById(EnumFieldAnnotatedModel.class, 1);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getOverrideEnum(), EnumFieldAnnotatedModel.OverrideEnum.ONE);
    }
}


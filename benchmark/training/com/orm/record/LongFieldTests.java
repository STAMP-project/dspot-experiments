package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.LongFieldAnnotatedModel;
import com.orm.model.LongFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@SuppressWarnings("all")
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class LongFieldTests {
    private Long aLong = Long.valueOf(25L);

    @Test
    public void nullLongExtendedTest() {
        SugarRecord.save(new LongFieldExtendedModel());
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        Assert.assertNull(model.getLong());
    }

    @Test
    public void nullRawLongExtendedTest() {
        SugarRecord.save(new LongFieldExtendedModel());
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        Assert.assertEquals(0L, model.getRawLong());
    }

    @Test
    public void nullLongAnnotatedTest() {
        SugarRecord.save(new LongFieldAnnotatedModel());
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getLong());
    }

    @Test
    public void nullRawLongAnnotatedTest() {
        SugarRecord.save(new LongFieldAnnotatedModel());
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        Assert.assertEquals(0L, model.getRawLong());
    }

    @Test
    public void objectLongExtendedTest() {
        SugarRecord.save(new LongFieldExtendedModel(aLong));
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        Assert.assertEquals(aLong, model.getLong());
    }

    @Test
    public void rawLongExtendedTest() {
        SugarRecord.save(new LongFieldExtendedModel(aLong.longValue()));
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        Assert.assertEquals(aLong.longValue(), model.getRawLong());
    }

    @Test
    public void objectLongAnnotatedTest() {
        SugarRecord.save(new LongFieldAnnotatedModel(aLong));
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        Assert.assertEquals(aLong, model.getLong());
    }

    @Test
    public void rawLongAnnotatedTest() {
        SugarRecord.save(new LongFieldAnnotatedModel(aLong.longValue()));
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        Assert.assertEquals(aLong.longValue(), model.getRawLong());
    }
}


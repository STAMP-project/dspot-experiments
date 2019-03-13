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


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public class FirstAndLastTests {
    @Test
    @SuppressWarnings("all")
    public void firstExtendedTest() {
        Float firstObjectFloat = 25.0F;
        Float lastObjectFloat = 50.0F;
        SugarRecord.save(new FloatFieldExtendedModel(firstObjectFloat));
        SugarRecord.save(new FloatFieldExtendedModel(lastObjectFloat));
        FloatFieldExtendedModel model = SugarRecord.first(FloatFieldExtendedModel.class);
        if (null != model) {
            Assert.assertEquals(firstObjectFloat, model.getFloat());
        }
    }

    @Test
    public void firstDeletedRecordExtendedTest() {
        Float second = 25.0F;
        SugarRecord.save(new FloatFieldExtendedModel(15.0F));
        SugarRecord.save(new FloatFieldExtendedModel(second));
        SugarRecord.save(new FloatFieldExtendedModel(35.0F));
        SugarRecord.save(new FloatFieldExtendedModel(45.0F));
        FloatFieldExtendedModel firstRecord = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        SugarRecord.delete(firstRecord);
        FloatFieldExtendedModel model = SugarRecord.first(FloatFieldExtendedModel.class);
        if (null != model) {
            Assert.assertEquals(second, model.getFloat());
        }
    }

    @Test
    public void lastExtendedTest() {
        Float last = 50.0F;
        SugarRecord.save(new FloatFieldExtendedModel(25.0F));
        SugarRecord.save(new FloatFieldExtendedModel(last));
        FloatFieldExtendedModel model = SugarRecord.last(FloatFieldExtendedModel.class);
        if (null != model) {
            Assert.assertEquals(last, model.getFloat());
        }
    }

    @Test
    public void lastDeletedRecordExtendedTest() {
        Float third = 35.0F;
        SugarRecord.save(new FloatFieldExtendedModel(15.0F));
        SugarRecord.save(new FloatFieldExtendedModel(25.0F));
        SugarRecord.save(new FloatFieldExtendedModel(third));
        SugarRecord.save(new FloatFieldExtendedModel(45.0F));
        FloatFieldExtendedModel lastRecord = SugarRecord.findById(FloatFieldExtendedModel.class, 4);
        SugarRecord.delete(lastRecord);
        FloatFieldExtendedModel model = SugarRecord.last(FloatFieldExtendedModel.class);
        if (null != model) {
            Assert.assertEquals(third, model.getFloat());
        }
    }

    @Test
    public void nullFirstExtendedTest() {
        Assert.assertNull(SugarRecord.first(FloatFieldExtendedModel.class));
    }

    @Test
    public void nullLastExtendedTest() {
        Assert.assertNull(SugarRecord.last(FloatFieldExtendedModel.class));
    }

    @Test
    public void oneItemExtendedTest() {
        SugarRecord.save(new FloatFieldExtendedModel(25.0F));
        FloatFieldExtendedModel firstModel = SugarRecord.first(FloatFieldExtendedModel.class);
        FloatFieldExtendedModel lastModel = SugarRecord.last(FloatFieldExtendedModel.class);
        if ((null != firstModel) && (null != lastModel)) {
            Assert.assertEquals(firstModel.getFloat(), lastModel.getFloat());
        }
    }

    @Test
    public void firstAnnotatedTest() {
        Float first = 25.0F;
        SugarRecord.save(new FloatFieldAnnotatedModel(first));
        SugarRecord.save(new FloatFieldAnnotatedModel(50.0F));
        FloatFieldAnnotatedModel model = SugarRecord.first(FloatFieldAnnotatedModel.class);
        if (null != model) {
            Assert.assertEquals(first, model.getFloat());
        }
    }

    @Test
    public void firstDeletedRecordAnnotatedTest() {
        Float second = 25.0F;
        SugarRecord.save(new FloatFieldAnnotatedModel(15.0F));
        SugarRecord.save(new FloatFieldAnnotatedModel(second));
        SugarRecord.save(new FloatFieldAnnotatedModel(35.0F));
        SugarRecord.save(new FloatFieldAnnotatedModel(45.0F));
        FloatFieldAnnotatedModel firstRecord = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        SugarRecord.delete(firstRecord);
        FloatFieldAnnotatedModel model = SugarRecord.first(FloatFieldAnnotatedModel.class);
        if (null != model) {
            Assert.assertEquals(second, model.getFloat());
        }
    }

    @Test
    public void lastAnnotatedTest() {
        Float last = 50.0F;
        SugarRecord.save(new FloatFieldAnnotatedModel(25.0F));
        SugarRecord.save(new FloatFieldAnnotatedModel(last));
        FloatFieldAnnotatedModel model = SugarRecord.last(FloatFieldAnnotatedModel.class);
        if (null != model) {
            Assert.assertEquals(last, model.getFloat());
        }
    }

    @Test
    public void lastDeletedRecordAnnotatedTest() {
        Float third = 35.0F;
        SugarRecord.save(new FloatFieldAnnotatedModel(15.0F));
        SugarRecord.save(new FloatFieldAnnotatedModel(25.0F));
        SugarRecord.save(new FloatFieldAnnotatedModel(third));
        SugarRecord.save(new FloatFieldAnnotatedModel(45.0F));
        FloatFieldAnnotatedModel lastRecord = SugarRecord.findById(FloatFieldAnnotatedModel.class, 4);
        SugarRecord.delete(lastRecord);
        FloatFieldAnnotatedModel model = SugarRecord.last(FloatFieldAnnotatedModel.class);
        if (null != model) {
            Assert.assertEquals(third, model.getFloat());
        }
    }

    @Test
    public void nullFirstAnnotatedTest() {
        Assert.assertNull(SugarRecord.first(FloatFieldAnnotatedModel.class));
    }

    @Test
    public void nullLastAnnotatedTest() {
        Assert.assertNull(SugarRecord.last(FloatFieldAnnotatedModel.class));
    }

    @Test
    public void oneItemAnnotatedTest() {
        SugarRecord.save(new FloatFieldAnnotatedModel(25.0F));
        FloatFieldAnnotatedModel first = SugarRecord.first(FloatFieldAnnotatedModel.class);
        FloatFieldAnnotatedModel last = SugarRecord.last(FloatFieldAnnotatedModel.class);
        if ((null != first) && (null != last)) {
            Assert.assertEquals(first.getFloat(), last.getFloat());
        }
    }
}


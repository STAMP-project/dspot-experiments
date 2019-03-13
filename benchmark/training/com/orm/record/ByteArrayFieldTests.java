package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.ByteArrayAnnotatedModel;
import com.orm.model.ByteArrayExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class ByteArrayFieldTests {
    @Test
    public void nullByteArrayExtendedTest() {
        byte[] array = "".getBytes();
        SugarRecord.save(new ByteArrayExtendedModel());
        ByteArrayExtendedModel model = SugarRecord.findById(ByteArrayExtendedModel.class, 1);
        Assert.assertEquals(new String(array), new String(model.getByteArray()));
        Assert.assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void nullByteArrayAnnotatedTest() {
        byte[] array = "".getBytes();
        SugarRecord.save(new ByteArrayAnnotatedModel());
        ByteArrayAnnotatedModel model = SugarRecord.findById(ByteArrayAnnotatedModel.class, 1);
        Assert.assertEquals(new String(array), new String(model.getByteArray()));
        Assert.assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void byteArrayExtendedTest() {
        byte[] array = "hello".getBytes();
        SugarRecord.save(new ByteArrayExtendedModel(array));
        ByteArrayExtendedModel model = SugarRecord.findById(ByteArrayExtendedModel.class, 1);
        Assert.assertEquals(new String(array), new String(model.getByteArray()));
        Assert.assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void byteArrayAnnotatedTest() {
        byte[] array = "hello".getBytes();
        SugarRecord.save(new ByteArrayAnnotatedModel(array));
        ByteArrayAnnotatedModel model = SugarRecord.findById(ByteArrayAnnotatedModel.class, 1);
        Assert.assertEquals(new String(array), new String(model.getByteArray()));
        Assert.assertArrayEquals(array, model.getByteArray());
    }
}


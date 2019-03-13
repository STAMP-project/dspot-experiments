package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.NoSugarModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class NoSugarModelTests {
    @Test
    public void deleteTest() throws Exception {
        NoSugarModel model = new NoSugarModel();
        Assert.assertFalse(SugarRecord.delete(model));
    }

    @Test
    public void saveInTransactionTest() throws Exception {
        SugarRecord.saveInTx(new NoSugarModel(), new NoSugarModel());
        Assert.assertEquals((-1L), SugarRecord.count(NoSugarModel.class));
    }
}


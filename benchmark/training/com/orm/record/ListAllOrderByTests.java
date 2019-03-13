package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.IntegerFieldExtendedModel;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class ListAllOrderByTests {
    @Test
    public void listAllOrderByEmptyTest() {
        final List<IntegerFieldExtendedModel> list = SugarRecord.listAll(IntegerFieldExtendedModel.class, "id");
        Assert.assertEquals(0L, list.size());
    }

    @Test
    public void listAllOrderByIdTest() {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new IntegerFieldExtendedModel(i));
        }
        List<IntegerFieldExtendedModel> models = SugarRecord.listAll(IntegerFieldExtendedModel.class, "id");
        Assert.assertEquals(100L, models.size());
        Long id = getId();
        for (int i = 1; i < 100; i++) {
            Assert.assertTrue((id < (models.get(i).getId())));
        }
    }

    @Test
    public void listAllOrderByFieldTest() {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new IntegerFieldExtendedModel(i));
        }
        List<IntegerFieldExtendedModel> models = SugarRecord.listAll(IntegerFieldExtendedModel.class, "raw_integer");
        Assert.assertEquals(100L, models.size());
        int raw = models.get(0).getInt();
        for (int i = 1; i < 100; i++) {
            Assert.assertTrue((raw < (models.get(i).getInt())));
        }
    }
}


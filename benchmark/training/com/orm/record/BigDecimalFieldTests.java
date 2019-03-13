package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.BigDecimalFieldAnnotatedModel;
import com.orm.model.BigDecimalFieldExtendedModel;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class BigDecimalFieldTests {
    private BigDecimal decimal = new BigDecimal(1234.567890123457);

    @Test
    public void nullBigDecimalExtendedTest() {
        SugarRecord.save(new BigDecimalFieldExtendedModel());
        BigDecimalFieldExtendedModel model = SugarRecord.findById(BigDecimalFieldExtendedModel.class, 1);
        Assert.assertNull(model.getBigDecimal());
    }

    @Test
    public void nullBigDecimalAnnotatedTest() {
        SugarRecord.save(new BigDecimalFieldAnnotatedModel());
        BigDecimalFieldAnnotatedModel model = SugarRecord.findById(BigDecimalFieldAnnotatedModel.class, 1);
        Assert.assertNull(model.getBigDecimal());
    }

    @Test
    public void bigDecimalExtendedTest() {
        SugarRecord.save(new BigDecimalFieldExtendedModel(decimal));
        BigDecimalFieldExtendedModel model = SugarRecord.findById(BigDecimalFieldExtendedModel.class, 1);
        Assert.assertEquals(decimal, model.getBigDecimal());
    }

    @Test
    public void bigDecimalAnnotatedTest() {
        SugarRecord.save(new BigDecimalFieldAnnotatedModel(decimal));
        BigDecimalFieldAnnotatedModel model = SugarRecord.findById(BigDecimalFieldAnnotatedModel.class, 1);
        Assert.assertEquals(decimal, model.getBigDecimal());
    }
}


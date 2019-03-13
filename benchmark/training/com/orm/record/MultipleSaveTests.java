package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.StringFieldAnnotatedModel;
import com.orm.model.StringFieldAnnotatedNoIdModel;
import com.orm.model.StringFieldExtendedModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class MultipleSaveTests {
    private String testString = "Test String";

    private String anotherString = "Another test";

    @Test
    public void stringMultipleSaveOriginalExtendedTest() {
        StringFieldExtendedModel model = new StringFieldExtendedModel(testString);
        long id = SugarRecord.save(model);
        StringFieldExtendedModel query = SugarRecord.findById(StringFieldExtendedModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
        }
        model.setString(anotherString);
        Assert.assertEquals(id, SugarRecord.save(model));
        Assert.assertNull(SugarRecord.findById(StringFieldExtendedModel.class, 2));
    }

    @Test
    public void stringMultipleSaveQueriedExtendedTest() {
        StringFieldExtendedModel model = new StringFieldExtendedModel(testString);
        long id = SugarRecord.save(model);
        StringFieldExtendedModel query = SugarRecord.findById(StringFieldExtendedModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
            query.setString(anotherString);
            Assert.assertEquals(id, SugarRecord.save(query));
            Assert.assertNull(SugarRecord.findById(StringFieldExtendedModel.class, 2));
        }
    }

    @Test
    public void stringMultipleSaveOriginalAnnotatedTest() {
        StringFieldAnnotatedModel model = new StringFieldAnnotatedModel(testString);
        long id = SugarRecord.save(model);
        StringFieldAnnotatedModel query = SugarRecord.findById(StringFieldAnnotatedModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
            model.setString(anotherString);
            Assert.assertEquals(id, SugarRecord.save(model));
            Assert.assertNull(SugarRecord.findById(StringFieldAnnotatedModel.class, 2));
        }
    }

    @Test
    public void stringMultipleSaveQueriedAnnotatedTest() {
        StringFieldAnnotatedModel model = new StringFieldAnnotatedModel(testString);
        long id = SugarRecord.save(model);
        StringFieldAnnotatedModel query = SugarRecord.findById(StringFieldAnnotatedModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
            query.setString(anotherString);
            Assert.assertEquals(id, SugarRecord.save(query));
            Assert.assertNull(SugarRecord.findById(StringFieldAnnotatedModel.class, 2));
        }
    }

    @Test
    public void stringMultipleSaveOriginalAnnotatedNoIdTest() {
        StringFieldAnnotatedNoIdModel model = new StringFieldAnnotatedNoIdModel(testString);
        long id = SugarRecord.save(model);
        StringFieldAnnotatedNoIdModel query = SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
            model.setString(anotherString);
            Assert.assertEquals(id, SugarRecord.save(model));
            Assert.assertNull(SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, 2));
        }
    }

    @Test
    public void stringMultipleSaveQueriedAnnotatedNoIdTest() {
        StringFieldAnnotatedNoIdModel model = new StringFieldAnnotatedNoIdModel(testString);
        long id = SugarRecord.save(model);
        StringFieldAnnotatedNoIdModel query = SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, id);
        if (null != query) {
            Assert.assertEquals(testString, query.getString());
            query.setString(anotherString);
            Assert.assertEquals(id, SugarRecord.save(query));
            Assert.assertNull(SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, 2));
        }
    }
}


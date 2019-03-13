package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.NestedAnnotatedModel;
import com.orm.model.RelationshipAnnotatedModel;
import com.orm.model.SimpleAnnotatedModel;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class NestedAnnotatedTests {
    @Test
    public void emptyDatabaseTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(NestedAnnotatedModel.class));
        Assert.assertEquals(0L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
        SugarRecord.save(nested);
        SugarRecord.save(new NestedAnnotatedModel(nested));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(NestedAnnotatedModel.class));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
        SugarRecord.save(nested);
        SugarRecord.save(new NestedAnnotatedModel(nested));
        SugarRecord.save(new NestedAnnotatedModel(nested));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(2L, SugarRecord.count(NestedAnnotatedModel.class));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        SimpleAnnotatedModel anotherSimple = new SimpleAnnotatedModel();
        SugarRecord.save(anotherSimple);
        RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
        SugarRecord.save(nested);
        RelationshipAnnotatedModel anotherNested = new RelationshipAnnotatedModel(anotherSimple);
        SugarRecord.save(anotherNested);
        SugarRecord.save(new NestedAnnotatedModel(nested));
        SugarRecord.save(new NestedAnnotatedModel(anotherNested));
        Assert.assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(2L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(2L, SugarRecord.count(NestedAnnotatedModel.class));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
        SugarRecord.save(nested);
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new NestedAnnotatedModel(nested));
        }
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(100L, SugarRecord.count(NestedAnnotatedModel.class));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            SugarRecord.save(simple);
            RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
            SugarRecord.save(nested);
            SugarRecord.save(new NestedAnnotatedModel(nested));
        }
        Assert.assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(100L, SugarRecord.count(RelationshipAnnotatedModel.class));
        Assert.assertEquals(100L, SugarRecord.count(NestedAnnotatedModel.class));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
        SugarRecord.save(nested);
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new NestedAnnotatedModel(nested));
        }
        List<NestedAnnotatedModel> models = SugarRecord.listAll(NestedAnnotatedModel.class);
        Assert.assertEquals(100, models.size());
        for (NestedAnnotatedModel model : models) {
            Assert.assertEquals(nested.getId(), model.getNested().getId());
            Assert.assertEquals(simple.getId(), model.getNested().getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            SugarRecord.save(simple);
            RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
            SugarRecord.save(nested);
            SugarRecord.save(new NestedAnnotatedModel(nested));
        }
        List<NestedAnnotatedModel> models = SugarRecord.listAll(NestedAnnotatedModel.class);
        Assert.assertEquals(100, models.size());
        for (NestedAnnotatedModel model : models) {
            Assert.assertEquals(model.getId(), model.getNested().getId());
            Assert.assertEquals(model.getId(), model.getNested().getSimple().getId());
        }
    }
}


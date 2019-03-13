package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.NestedMixedBAModel;
import com.orm.model.RelationshipMixedAModel;
import com.orm.model.SimpleAnnotatedModel;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class NestedMixedBATests {
    @Test
    public void emptyDatabaseTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(NestedMixedBAModel.class));
        Assert.assertEquals(0L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
        SugarRecord.save(nested);
        SugarRecord.save(new NestedMixedBAModel(nested));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(1L, SugarRecord.count(NestedMixedBAModel.class));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
        SugarRecord.save(nested);
        SugarRecord.save(new NestedMixedBAModel(nested));
        SugarRecord.save(new NestedMixedBAModel(nested));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(2L, SugarRecord.count(NestedMixedBAModel.class));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        SimpleAnnotatedModel anotherSimple = new SimpleAnnotatedModel();
        SugarRecord.save(anotherSimple);
        RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
        SugarRecord.save(nested);
        RelationshipMixedAModel anotherNested = new RelationshipMixedAModel(anotherSimple);
        SugarRecord.save(anotherNested);
        SugarRecord.save(new NestedMixedBAModel(nested));
        SugarRecord.save(new NestedMixedBAModel(anotherNested));
        Assert.assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(2L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(2L, SugarRecord.count(NestedMixedBAModel.class));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
        SugarRecord.save(nested);
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new NestedMixedBAModel(nested));
        }
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(1L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(100L, SugarRecord.count(NestedMixedBAModel.class));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            SugarRecord.save(simple);
            RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
            SugarRecord.save(nested);
            SugarRecord.save(new NestedMixedBAModel(nested));
        }
        Assert.assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(100L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(100L, SugarRecord.count(NestedMixedBAModel.class));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        SugarRecord.save(simple);
        RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
        SugarRecord.save(nested);
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new NestedMixedBAModel(nested));
        }
        List<NestedMixedBAModel> models = SugarRecord.listAll(NestedMixedBAModel.class);
        Assert.assertEquals(100, models.size());
        for (NestedMixedBAModel model : models) {
            Assert.assertEquals(getId(), getId());
            Assert.assertEquals(simple.getId(), model.getNested().getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            SugarRecord.save(simple);
            RelationshipMixedAModel nested = new RelationshipMixedAModel(simple);
            SugarRecord.save(nested);
            SugarRecord.save(new NestedMixedBAModel(nested));
        }
        List<NestedMixedBAModel> models = SugarRecord.listAll(NestedMixedBAModel.class);
        Assert.assertEquals(100, models.size());
        for (NestedMixedBAModel model : models) {
            Assert.assertEquals(model.getId(), getId());
            Assert.assertEquals(model.getId(), model.getNested().getSimple().getId());
        }
    }
}


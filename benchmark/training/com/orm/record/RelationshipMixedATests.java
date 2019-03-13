package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
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
public final class RelationshipMixedATests {
    @Test
    public void emptyDatabaseTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(RelationshipMixedAModel.class));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        RelationshipMixedAModel mixedAModel = new RelationshipMixedAModel(simple);
        SugarRecord.save(simple);
        SugarRecord.save(mixedAModel);
        Assert.assertEquals(1L, SugarRecord.count(simple.getClass()));
        Assert.assertEquals(1L, SugarRecord.count(mixedAModel.getClass()));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        RelationshipMixedAModel mixedAModel1 = new RelationshipMixedAModel(simple);
        RelationshipMixedAModel mixedAModel2 = new RelationshipMixedAModel(simple);
        SugarRecord.save(simple);
        SugarRecord.save(mixedAModel1);
        SugarRecord.save(mixedAModel2);
        Assert.assertEquals(1L, SugarRecord.count(simple.getClass()));
        Assert.assertEquals(2L, SugarRecord.count(mixedAModel1.getClass()));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleAnnotatedModel anotherSimple = new SimpleAnnotatedModel();
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        RelationshipMixedAModel mixedAModel = new RelationshipMixedAModel(simple);
        RelationshipMixedAModel anotherMixedAModel = new RelationshipMixedAModel(anotherSimple);
        SugarRecord.save(simple);
        SugarRecord.save(anotherSimple);
        SugarRecord.save(mixedAModel);
        SugarRecord.save(anotherMixedAModel);
        Assert.assertEquals(2L, SugarRecord.count(simple.getClass()));
        Assert.assertEquals(2L, SugarRecord.count(mixedAModel.getClass()));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        final SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        RelationshipMixedAModel mixedAModel = null;
        SugarRecord.save(simple);
        for (int i = 1; i <= 100; i++) {
            mixedAModel = new RelationshipMixedAModel(simple);
            SugarRecord.save(mixedAModel);
        }
        Assert.assertEquals(1L, SugarRecord.count(simple.getClass()));
        Assert.assertEquals(100L, SugarRecord.count(mixedAModel.getClass()));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        SimpleAnnotatedModel simple = null;
        RelationshipMixedAModel mixedAModel = null;
        for (int i = 1; i <= 100; i++) {
            simple = new SimpleAnnotatedModel();
            mixedAModel = new RelationshipMixedAModel(simple);
            SugarRecord.save(simple);
            SugarRecord.save(mixedAModel);
        }
        Assert.assertEquals(100L, SugarRecord.count(simple.getClass()));
        Assert.assertEquals(100L, SugarRecord.count(mixedAModel.getClass()));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        for (int i = 1; i <= 100; i++) {
            RelationshipMixedAModel mixedAModel = new RelationshipMixedAModel(simple);
            SugarRecord.save(simple);
            SugarRecord.save(mixedAModel);
        }
        List<RelationshipMixedAModel> models = SugarRecord.listAll(RelationshipMixedAModel.class);
        Assert.assertEquals(100, models.size());
        for (RelationshipMixedAModel model : models) {
            Assert.assertEquals(simple.getId(), model.getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            SugarRecord.save(simple);
            SugarRecord.save(new RelationshipMixedAModel(simple));
        }
        List<RelationshipMixedAModel> models = SugarRecord.listAll(RelationshipMixedAModel.class);
        Assert.assertEquals(100, models.size());
        for (RelationshipMixedAModel model : models) {
            Assert.assertEquals(getId(), model.getSimple().getId());
        }
    }
}


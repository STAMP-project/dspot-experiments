package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.onetomany.OneToManyModel;
import com.orm.model.onetomany.OneToManyRelationModel;
import com.orm.model.onetomany.WithoutOneToManyAnnotationModel;
import com.orm.model.onetomany.WithoutOneToManyAnnotationRelationModel;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by ?ukasz Weso?owski on 28.07.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public class OneToManyTest {
    @Test
    public void shouldSaveWithOneToManyRelation() {
        List<Long> relationIds = Arrays.asList(1L, 2L, 3L, 4L);
        OneToManyModel model = new OneToManyModel(1L);
        SugarRecord.save(model);
        for (long i : relationIds) {
            SugarRecord.save(new OneToManyRelationModel(i, model));
        }
        OneToManyModel result = SugarRecord.findById(OneToManyModel.class, 1L);
        Assert.assertEquals(4, result.getModels().size());
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertEquals(result, result.getModels().get(0).getModel());
        Assert.assertEquals(result, result.getModels().get(1).getModel());
        Assert.assertEquals(result, result.getModels().get(2).getModel());
        Assert.assertEquals(result, result.getModels().get(3).getModel());
    }

    @Test
    public void shouldRemoveOneOfManyToOneRelation() {
        OneToManyModel model = new OneToManyModel(1L);
        SugarRecord.save(model);
        for (long i : Arrays.asList(1L, 2L, 3L, 4L)) {
            SugarRecord.save(new OneToManyRelationModel(i, model));
        }
        OneToManyModel result = SugarRecord.findById(OneToManyModel.class, 1L);
        Assert.assertEquals(4, result.getModels().size());
        deleteAll(OneToManyRelationModel.class, "id = ?", String.valueOf(3L));
        result = SugarRecord.findById(OneToManyModel.class, 1L);
        Assert.assertEquals(3, result.getModels().size());
        Assert.assertTrue(((getId()) != 3L));
        Assert.assertTrue(((getId()) != 3L));
        Assert.assertTrue(((getId()) != 3L));
    }

    @Test
    public void shouldNotRemoveRelation() {
        OneToManyModel model = new OneToManyModel(1L);
        SugarRecord.save(model);
        for (long i : Arrays.asList(1L, 2L, 3L, 4L)) {
            SugarRecord.save(new OneToManyRelationModel(i, model));
        }
        OneToManyModel result = SugarRecord.findById(OneToManyModel.class, 1L);
        result.getModels().clear();
        SugarRecord.save(model);
        result = SugarRecord.findById(OneToManyModel.class, 1L);
        Assert.assertEquals(4, result.getModels().size());
    }

    @Test
    public void shouldNotAddRelation() {
        List<Long> relationIds = Arrays.asList(1L, 2L, 3L, 4L);
        OneToManyModel model = new OneToManyModel(1L);
        SugarRecord.save(model);
        for (long i : relationIds) {
            SugarRecord.save(new OneToManyRelationModel(i, model));
        }
        SugarRecord.save(new OneToManyRelationModel(5L, null));
        OneToManyModel result = SugarRecord.findById(OneToManyModel.class, 1L);
        Assert.assertEquals(4, result.getModels().size());
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertTrue(relationIds.contains(getId()));
        Assert.assertEquals(result, result.getModels().get(0).getModel());
        Assert.assertEquals(result, result.getModels().get(1).getModel());
        Assert.assertEquals(result, result.getModels().get(2).getModel());
        Assert.assertEquals(result, result.getModels().get(3).getModel());
    }

    @Test
    public void shouldNotInflateList() {
        List<Long> relationIds = Arrays.asList(1L, 2L, 3L, 4L);
        WithoutOneToManyAnnotationModel model = new WithoutOneToManyAnnotationModel(1L);
        SugarRecord.save(model);
        for (long i : relationIds) {
            SugarRecord.save(new WithoutOneToManyAnnotationRelationModel(i, model));
        }
        WithoutOneToManyAnnotationModel result = SugarRecord.findById(WithoutOneToManyAnnotationModel.class, 1L);
        Assert.assertEquals(null, result.getModels());
    }
}


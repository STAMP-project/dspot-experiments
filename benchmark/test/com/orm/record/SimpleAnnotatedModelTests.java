package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.helper.NamingHelper;
import com.orm.model.SimpleAnnotatedModel;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class SimpleAnnotatedModelTests {
    @Test
    public void emptyDatabaseTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void twoSaveTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void manySaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Assert.assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void defaultIdTest() throws Exception {
        Assert.assertEquals(1L, SugarRecord.save(new SimpleAnnotatedModel()));
    }

    @Test
    public void whereCountTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class, "id = ?", new String[]{ "1" }));
    }

    @Test
    public void whereNoCountTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class, "id = ?", new String[]{ "1" }));
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class, "id = ?", new String[]{ "3" }));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class, "id = ?", new String[]{ "a" }));
    }

    @Test
    public void whereBrokenCountTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals((-1L), SugarRecord.count(SimpleAnnotatedModel.class, "di = ?", new String[]{ "1" }));
    }

    @Test
    public void deleteTest() throws Exception {
        SimpleAnnotatedModel model = new SimpleAnnotatedModel();
        SugarRecord.save(model);
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertTrue(SugarRecord.delete(model));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void deleteUnsavedTest() throws Exception {
        SimpleAnnotatedModel model = new SimpleAnnotatedModel();
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertFalse(SugarRecord.delete(model));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void deleteWrongTest() throws Exception {
        SimpleAnnotatedModel model = new SimpleAnnotatedModel();
        SugarRecord.save(model);
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        Field idField = model.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(model, Long.MAX_VALUE);
        Assert.assertFalse(SugarRecord.delete(model));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void deleteAllTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Assert.assertEquals(100, SugarRecord.deleteAll(SimpleAnnotatedModel.class));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAllWhereTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Assert.assertEquals(99, SugarRecord.deleteAll(SimpleAnnotatedModel.class, "id > ?", new String[]{ "1" }));
        Assert.assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void deleteInTransactionFewTest() throws Exception {
        SimpleAnnotatedModel first = new SimpleAnnotatedModel();
        SimpleAnnotatedModel second = new SimpleAnnotatedModel();
        SimpleAnnotatedModel third = new SimpleAnnotatedModel();
        SugarRecord.save(first);
        SugarRecord.save(second);
        // Not saving last model
        Assert.assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(2, SugarRecord.deleteInTx(first, second, third));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void deleteInTransactionManyTest() throws Exception {
        List<SimpleAnnotatedModel> models = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel model = new SimpleAnnotatedModel();
            models.add(model);
            // Not saving last model
            if (i < 100) {
                SugarRecord.save(model);
            }
        }
        Assert.assertEquals(99, SugarRecord.count(SimpleAnnotatedModel.class));
        Assert.assertEquals(99, SugarRecord.deleteInTx(models));
        Assert.assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void saveInTransactionTest() throws Exception {
        SugarRecord.saveInTx(new SimpleAnnotatedModel(), new SimpleAnnotatedModel());
        Assert.assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void listAllTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        List<SimpleAnnotatedModel> models = SugarRecord.listAll(SimpleAnnotatedModel.class);
        Assert.assertEquals(100, models.size());
        for (long i = 1; i <= 100; i++) {
            Assert.assertEquals(Long.valueOf(i), models.get((((int) (i)) - 1)).getId());
        }
    }

    @Test
    public void findTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        List<SimpleAnnotatedModel> models = SugarRecord.find(SimpleAnnotatedModel.class, "id = ?", "2");
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(2L, models.get(0).getId().longValue());
    }

    @Test
    public void findWithQueryTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        List<SimpleAnnotatedModel> models = SugarRecord.findWithQuery(SimpleAnnotatedModel.class, (("Select * from " + (NamingHelper.toTableName(SimpleAnnotatedModel.class))) + " where id >= ? "), "50");
        for (SimpleAnnotatedModel model : models) {
            Assert.assertEquals(75L, model.getId(), 25L);
        }
    }

    @Test
    @SuppressWarnings("all")
    public void findByIdTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(1L, SugarRecord.findById(SimpleAnnotatedModel.class, 1L).getId().longValue());
    }

    @Test
    public void findByIdIntegerTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(1L, SugarRecord.findById(SimpleAnnotatedModel.class, 1).getId().longValue());
    }

    @Test
    public void findByIdStringsNullTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertEquals(0, SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{ "" }).size());
    }

    @Test
    public void findByIdStringsOneTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        List<SimpleAnnotatedModel> models = SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{ "1" });
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(1L, models.get(0).getId().longValue());
    }

    @Test
    public void findByIdStringsTwoTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        SugarRecord.save(new SimpleAnnotatedModel());
        List<SimpleAnnotatedModel> models = SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{ "1", "3" });
        Assert.assertEquals(2, models.size());
        Assert.assertEquals(Long.valueOf(1L), models.get(0).getId());
        Assert.assertEquals(Long.valueOf(3L), models.get(1).getId());
    }

    @Test
    public void findByIdStringsManyTest() throws Exception {
        for (int i = 1; i <= 10; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        List<SimpleAnnotatedModel> models = SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{ "1", "3", "6", "10" });
        Assert.assertEquals(4, models.size());
        Assert.assertEquals(Long.valueOf(1L), models.get(0).getId());
        Assert.assertEquals(Long.valueOf(3L), models.get(1).getId());
        Assert.assertEquals(Long.valueOf(6L), models.get(2).getId());
        Assert.assertEquals(Long.valueOf(10L), models.get(3).getId());
    }

    @Test
    public void findByIdStringsOrderTest() throws Exception {
        for (int i = 1; i <= 10; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        List<SimpleAnnotatedModel> models = SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{ "10", "6", "3", "1" });
        Assert.assertEquals(4, models.size());
        // The order of the query doesn't matter
        Assert.assertEquals(Long.valueOf(1L), models.get(0).getId());
        Assert.assertEquals(Long.valueOf(3L), models.get(1).getId());
        Assert.assertEquals(Long.valueOf(6L), models.get(2).getId());
        Assert.assertEquals(Long.valueOf(10L), models.get(3).getId());
    }

    @Test
    public void findByIdNullTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Assert.assertNull(SugarRecord.findById(SimpleAnnotatedModel.class, 2L));
    }

    @Test
    public void findAllTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findAll(SimpleAnnotatedModel.class);
        for (int i = 1; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleAnnotatedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), model.getId());
        }
    }

    @Test
    public void findAsIteratorTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findAsIterator(SimpleAnnotatedModel.class, "id >= ?", "50");
        for (int i = 50; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleAnnotatedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), model.getId());
        }
    }

    @Test
    public void findWithQueryAsIteratorTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleAnnotatedModel());
        }
        Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findWithQueryAsIterator(SimpleAnnotatedModel.class, (("Select * from " + (NamingHelper.toTableName(SimpleAnnotatedModel.class))) + " where id >= ? "), "50");
        for (int i = 50; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleAnnotatedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), model.getId());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void findAsIteratorOutOfBoundsTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findAsIterator(SimpleAnnotatedModel.class, "id = ?", "1");
        Assert.assertTrue(cursor.hasNext());
        SimpleAnnotatedModel model = cursor.next();
        Assert.assertNotNull(model);
        Assert.assertEquals(Long.valueOf(1), model.getId());
        // This should throw a NoSuchElementException
        cursor.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void disallowRemoveCursorTest() throws Exception {
        SugarRecord.save(new SimpleAnnotatedModel());
        Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findAsIterator(SimpleAnnotatedModel.class, "id = ?", "1");
        Assert.assertTrue(cursor.hasNext());
        SimpleAnnotatedModel model = cursor.next();
        Assert.assertNotNull(model);
        Assert.assertEquals(Long.valueOf(1), model.getId());
        // This should throw a UnsupportedOperationException
        cursor.remove();
    }

    @Test
    public void vacuumTest() throws Exception {
        SugarRecord.executeQuery("Vacuum");
    }
}


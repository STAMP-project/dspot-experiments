package com.orm.record;


import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.helper.NamingHelper;
import com.orm.model.SimpleExtendedModel;
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
public final class SimpleExtendedModelTests {
    private String id = "id = ?";

    @Test
    public void emptyDatabaseTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void twoSaveTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(2L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void manySaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Assert.assertEquals(100L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void defaultIdTest() throws Exception {
        Assert.assertEquals(1L, SugarRecord.save(new SimpleExtendedModel()));
    }

    @Test
    public void whereCountTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class, id, new String[]{ "1" }));
    }

    @Test
    public void whereNoCountTest() throws Exception {
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class, id, new String[]{ "1" }));
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class, id, new String[]{ "3" }));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class, id, new String[]{ "a" }));
    }

    @Test
    public void whereBrokenCountTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals((-1L), SugarRecord.count(SimpleExtendedModel.class, "di = ?", new String[]{ "1" }));
    }

    @Test
    public void saveMethodTest() throws Exception {
        SimpleExtendedModel model = new SimpleExtendedModel();
        model.save();
        Assert.assertEquals((-1L), SugarRecord.count(SimpleExtendedModel.class, "di = ?", new String[]{ "1" }));
    }

    @Test
    public void deleteTest() throws Exception {
        SimpleExtendedModel model = new SimpleExtendedModel();
        SugarRecord.save(model);
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        Assert.assertTrue(SugarRecord.delete(model));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void deleteUnsavedTest() throws Exception {
        SimpleExtendedModel model = new SimpleExtendedModel();
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
        Assert.assertFalse(SugarRecord.delete(model));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void deleteWrongTest() throws Exception {
        SimpleExtendedModel model = new SimpleExtendedModel();
        SugarRecord.save(model);
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        Field idField = model.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(model, Long.MAX_VALUE);
        Assert.assertFalse(SugarRecord.delete(model));
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void deleteAllTest() throws Exception {
        int elementNumber = 100;
        for (int i = 1; i <= elementNumber; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Assert.assertEquals(elementNumber, SugarRecord.deleteAll(SimpleExtendedModel.class));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    @SuppressWarnings("all")
    public void deleteAllWhereTest() throws Exception {
        int elementNumber = 100;
        for (int i = 1; i <= elementNumber; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Assert.assertEquals((elementNumber - 1), SugarRecord.deleteAll(SimpleExtendedModel.class, "id > ?", new String[]{ "1" }));
        Assert.assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void deleteInTransactionFewTest() throws Exception {
        SimpleExtendedModel first = new SimpleExtendedModel();
        SimpleExtendedModel second = new SimpleExtendedModel();
        SimpleExtendedModel third = new SimpleExtendedModel();
        SugarRecord.save(first);
        SugarRecord.save(second);
        // Not saving last model
        Assert.assertEquals(2L, SugarRecord.count(SimpleExtendedModel.class));
        Assert.assertEquals(2, SugarRecord.deleteInTx(first, second, third));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void deleteInTransactionManyTest() throws Exception {
        long elementNumber = 100;
        List<SimpleExtendedModel> models = new ArrayList<>();
        for (int i = 1; i <= elementNumber; i++) {
            SimpleExtendedModel model = new SimpleExtendedModel();
            models.add(model);
            // Not saving last model
            if (i < elementNumber) {
                SugarRecord.save(model);
            }
        }
        Assert.assertEquals((elementNumber - 1), SugarRecord.count(SimpleExtendedModel.class));
        Assert.assertEquals((elementNumber - 1), SugarRecord.deleteInTx(models));
        Assert.assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void saveInTransactionTest() throws Exception {
        SugarRecord.saveInTx(new SimpleExtendedModel(), new SimpleExtendedModel());
        Assert.assertEquals(2L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void listAllTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        List<SimpleExtendedModel> models = SugarRecord.listAll(SimpleExtendedModel.class);
        Assert.assertEquals(100, models.size());
        for (long i = 1; i <= 100; i++) {
            Assert.assertEquals(Long.valueOf(i), getId());
        }
    }

    @Test
    public void findTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        List<SimpleExtendedModel> models = SugarRecord.find(SimpleExtendedModel.class, "id = ?", "2");
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(Long.valueOf(2L), getId());
    }

    @Test
    public void findWithQueryTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        List<SimpleExtendedModel> models = SugarRecord.findWithQuery(SimpleExtendedModel.class, (("Select * from " + (NamingHelper.toTableName(SimpleExtendedModel.class))) + " where id >= ? "), "50");
        for (SimpleExtendedModel model : models) {
            Assert.assertEquals(75, getId(), 25L);
        }
    }

    @Test
    @SuppressWarnings("all")
    public void findByIdTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(Long.valueOf(1L), getId());
    }

    @Test
    public void findByIdIntegerTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(Long.valueOf(1L), getId());
    }

    @Test
    public void findByIdStringsNullTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertEquals(0, SugarRecord.findById(SimpleExtendedModel.class, new String[]{ "" }).size());
    }

    @Test
    public void findByIdStringsOneTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        List<SimpleExtendedModel> models = SugarRecord.findById(SimpleExtendedModel.class, new String[]{ "1" });
        Assert.assertEquals(1, models.size());
        Assert.assertEquals(Long.valueOf(1L), getId());
    }

    @Test
    public void findByIdStringsTwoTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        SugarRecord.save(new SimpleExtendedModel());
        List<SimpleExtendedModel> models = SugarRecord.findById(SimpleExtendedModel.class, new String[]{ "1", "3" });
        Assert.assertEquals(2, models.size());
        Assert.assertEquals(Long.valueOf(1L), getId());
        Assert.assertEquals(Long.valueOf(3L), getId());
    }

    @Test
    public void findByIdStringsManyTest() throws Exception {
        for (int i = 1; i <= 10; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        List<SimpleExtendedModel> models = SugarRecord.findById(SimpleExtendedModel.class, new String[]{ "1", "3", "6", "10" });
        Assert.assertEquals(4, models.size());
        Assert.assertEquals(Long.valueOf(1L), getId());
        Assert.assertEquals(Long.valueOf(3L), getId());
        Assert.assertEquals(Long.valueOf(6L), getId());
        Assert.assertEquals(Long.valueOf(10L), getId());
    }

    @Test
    public void findByIdStringsOrderTest() throws Exception {
        for (int i = 1; i <= 10; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        List<SimpleExtendedModel> models = SugarRecord.findById(SimpleExtendedModel.class, new String[]{ "10", "6", "3", "1" });
        Assert.assertEquals(4, models.size());
        // The order of the query doesn't matter
        Assert.assertEquals(Long.valueOf(1L), getId());
        Assert.assertEquals(Long.valueOf(3L), getId());
        Assert.assertEquals(Long.valueOf(6L), getId());
        Assert.assertEquals(Long.valueOf(10L), getId());
    }

    @Test
    public void findByIdNullTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Assert.assertNull(SugarRecord.findById(SimpleExtendedModel.class, 2L));
    }

    @Test
    public void findAllTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Iterator<SimpleExtendedModel> cursor = SugarRecord.findAll(SimpleExtendedModel.class);
        for (int i = 1; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleExtendedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), getId());
        }
    }

    @Test
    public void findAsIteratorTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Iterator<SimpleExtendedModel> cursor = SugarRecord.findAsIterator(SimpleExtendedModel.class, "id >= ?", "50");
        for (int i = 50; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleExtendedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), getId());
        }
    }

    @Test
    public void findWithQueryAsIteratorTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SugarRecord.save(new SimpleExtendedModel());
        }
        Iterator<SimpleExtendedModel> cursor = SugarRecord.findWithQueryAsIterator(SimpleExtendedModel.class, (("Select * from " + (NamingHelper.toTableName(SimpleExtendedModel.class))) + " where id >= ? "), "50");
        for (int i = 50; i <= 100; i++) {
            Assert.assertTrue(cursor.hasNext());
            SimpleExtendedModel model = cursor.next();
            Assert.assertNotNull(model);
            Assert.assertEquals(Long.valueOf(i), getId());
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void findAsIteratorOutOfBoundsTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Iterator<SimpleExtendedModel> cursor = SugarRecord.findAsIterator(SimpleExtendedModel.class, id, "1");
        Assert.assertTrue(cursor.hasNext());
        SimpleExtendedModel model = cursor.next();
        Assert.assertNotNull(model);
        Assert.assertEquals(Long.valueOf(1), getId());
        // This should throw a NoSuchElementException
        cursor.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void disallowRemoveCursorTest() throws Exception {
        SugarRecord.save(new SimpleExtendedModel());
        Iterator<SimpleExtendedModel> cursor = SugarRecord.findAsIterator(SimpleExtendedModel.class, id, "1");
        Assert.assertTrue(cursor.hasNext());
        SimpleExtendedModel model = cursor.next();
        Assert.assertNotNull(model);
        Assert.assertEquals(Long.valueOf(1), getId());
        // This should throw a UnsupportedOperationException
        cursor.remove();
    }

    @Test
    public void vacuumTest() throws Exception {
        SugarRecord.executeQuery("Vacuum");
    }
}


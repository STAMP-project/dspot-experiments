package com.mongodb.hadoop.pig;


import MongoStorageOptions.Index;
import MongoStorageOptions.Update;
import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;


public class MongoStorageOptionsTest {
    @Test
    public void testUpdate() {
        try {
            final String update1 = "update [string, stringTwo]";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ update1 });
            MongoStorageOptions[] indexes = m.getIndexes();
            MongoStorageOptions.Update update = m.getUpdate();
            Assert.assertTrue("No insert condition provided, array should be empty", ((indexes.length) == 0));
            Assert.assertNotNull("Update should not be null", update);
            Assert.assertFalse("Update multi option should be false, for 'update' string", update.multi);
            Assert.assertTrue("Query should contain field string but does not", update.keys[0].equals("string"));
            Assert.assertTrue("Query should contain field stringTwo but does not", update.keys[1].equals("stringTwo"));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testMultiUpdate() {
        try {
            final String multiUpdate = "multi [string, stringTwo]";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ multiUpdate });
            MongoStorageOptions[] indexes = m.getIndexes();
            MongoStorageOptions.Update update = m.getUpdate();
            Assert.assertTrue("No insert condition provided, array should be empty", ((indexes.length) == 0));
            Assert.assertNotNull("Update should not be null", update);
            Assert.assertTrue("Update multi option should be true, for 'multi' string", update.multi);
            Assert.assertTrue("Query should contain field string but does not", update.keys[0].equals("string"));
            Assert.assertTrue("Query should contain field stringTwo but does not", update.keys[1].equals("stringTwo"));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testEnsureIndex() {
        try {
            final String insert = "{string : 1, stringTwo : -1},{}";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ insert });
            MongoStorageOptions[] indexes = m.getIndexes();
            MongoStorageOptions.Update update = m.getUpdate();
            // Test proper result sizes returned
            Assert.assertTrue("Single insert provided, array should be length 1", ((indexes.length) == 1));
            Assert.assertNull("Update not provided, should be null", update);
            MongoStorageOptions.Index index = indexes[0];
            // Test returned index is properly formed
            Assert.assertTrue("Index should contain field 'string' but does not", index.index.containsField("string"));
            Assert.assertTrue("Index should contain field 'stringTwo' but does not", index.index.containsField("stringTwo"));
            Assert.assertTrue("Index at 'string' should equal 1 but does not", (((Integer) (index.index.get("string"))) == 1));
            Assert.assertTrue("Index at 'string' should equal 1 but does not", (((Integer) (index.index.get("stringTwo"))) == (-1)));
            // Test that default options are correctly set
            Assert.assertNotNull("Options object not created properly", index.options);
            Assert.assertFalse("Default of unique should be false", ((Boolean) (index.options.get("unique"))));
            Assert.assertFalse("Default of spare should be false", ((Boolean) (index.options.get("sparse"))));
            Assert.assertFalse("Default of dropDups should be false", ((Boolean) (index.options.get("dropDups"))));
            Assert.assertFalse("Default of background should be false", ((Boolean) (index.options.get("background"))));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testEnsureIndexUnique() {
        try {
            final String insertUnique = "{string : 1, stringTwo : 1},{unique : true}";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ insertUnique });
            MongoStorageOptions.Index index = m.getIndexes()[0];
            // Test that default options are correctly set
            Assert.assertNotNull("Options object not created properly", index.options);
            Assert.assertTrue("Unique should be true", ((Boolean) (index.options.get("unique"))));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testEnsureIndexSpare() {
        try {
            final String insertSparse = "{string : 1, stringTwo : 1},{sparse : true}";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ insertSparse });
            MongoStorageOptions.Index index = m.getIndexes()[0];
            // Test that default options are correctly set
            Assert.assertNotNull("Options object not created properly", index.options);
            Assert.assertTrue("spare should be true", ((Boolean) (index.options.get("sparse"))));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testEnsureIndexDropDups() {
        try {
            final String insertDropDups = "{string : 1, stringTwo : 1},{dropDups : true}";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ insertDropDups });
            MongoStorageOptions.Index index = m.getIndexes()[0];
            // Test that default options are correctly set
            Assert.assertNotNull("Options object not created properly", index.options);
            Assert.assertTrue("dropDups should be true", ((Boolean) (index.options.get("dropDups"))));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }

    @Test
    public void testEnsureIndexBackground() {
        try {
            final String insertBackground = "{string : 1, stringTwo : 1},{background : true}";
            MongoStorageOptions m = MongoStorageOptions.parseArguments(new String[]{ insertBackground });
            MongoStorageOptions.Index index = m.getIndexes()[0];
            // Test that default options are correctly set
            Assert.assertNotNull("Options object not created properly", index.options);
            Assert.assertTrue("Background should be true", ((Boolean) (index.options.get("background"))));
        } catch (ParseException e) {
            Assert.fail(("Threw parse exception on valid string: " + (e.getMessage())));
        }
    }
}


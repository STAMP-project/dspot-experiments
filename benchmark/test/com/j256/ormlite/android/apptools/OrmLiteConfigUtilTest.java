package com.j256.ormlite.android.apptools;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class OrmLiteConfigUtilTest {
    private static final String lineSeparator = System.getProperty("line.separator");

    @Test
    public void testBasic() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new Class[]{ OrmLiteConfigUtilTest.Foo.class });
        String result = output.toString();
        Assert.assertTrue(result, result.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=id")// 
         + (OrmLiteConfigUtilTest.lineSeparator)) + "id=true") + (OrmLiteConfigUtilTest.lineSeparator))));
    }

    @Test
    public void testBasicSorted() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new Class[]{ OrmLiteConfigUtilTest.Foo.class, OrmLiteConfigUtilTest.Bar.class }, false);
        String result1 = output.toString();
        Assert.assertTrue(result1, result1.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=id")// 
         + (OrmLiteConfigUtilTest.lineSeparator)) + "id=true") + (OrmLiteConfigUtilTest.lineSeparator))));
        output.reset();
        OrmLiteConfigUtil.writeConfigFile(output, new Class[]{ OrmLiteConfigUtilTest.Foo.class, OrmLiteConfigUtilTest.Bar.class }, true);
        String result2 = output.toString();
        Assert.assertFalse(result2.equals(result1));
    }

    @Test
    public void testCurrentDir() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new File("src/test/java/com/j256/ormlite/android/apptools/"));
        String result = output.toString();
        Assert.assertTrue(result, result.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=id") + (OrmLiteConfigUtilTest.lineSeparator))// 
         + "id=true") + (OrmLiteConfigUtilTest.lineSeparator))));
    }

    @Test
    public void testCurrentDirSorted() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new File("src/test/java/com/j256/ormlite/android/apptools/"), true);
        String result = output.toString();
        Assert.assertTrue(result, result.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=id") + (OrmLiteConfigUtilTest.lineSeparator))// 
         + "id=true") + (OrmLiteConfigUtilTest.lineSeparator))));
    }

    @Test
    public void testForeignCollection() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new Class[]{ OrmLiteConfigUtilTest.ForeignCollectionTest.class });
        String result = output.toString();
        Assert.assertTrue(result, result.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=collection") + (OrmLiteConfigUtilTest.lineSeparator))// 
         + "foreignCollection=true") + (OrmLiteConfigUtilTest.lineSeparator))));
    }

    @Test
    public void testForeign() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OrmLiteConfigUtil.writeConfigFile(output, new Class[]{ OrmLiteConfigUtilTest.Foo.class });
        String result = output.toString();
        Assert.assertTrue(result, result.contains((((((OrmLiteConfigUtilTest.lineSeparator)// 
         + "fieldName=foreign") + (OrmLiteConfigUtilTest.lineSeparator))// 
         + "foreign=true") + (OrmLiteConfigUtilTest.lineSeparator))));
    }

    protected static class Foo {
        @DatabaseField(id = true)
        int id;

        @DatabaseField(foreign = true)
        OrmLiteConfigUtilTest.Foreign foreign;
    }

    protected static class Bar {
        @DatabaseField(id = true)
        int id;

        @DatabaseField(foreign = true)
        String zipper;
    }

    protected static class Foreign {
        @DatabaseField(id = true)
        int id;
    }

    protected static class ForeignCollectionTest {
        @ForeignCollectionField
        Collection<OrmLiteConfigUtilTest.Foo> collection;
    }
}


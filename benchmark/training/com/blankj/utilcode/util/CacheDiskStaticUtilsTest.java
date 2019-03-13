package com.blankj.utilcode.util;


import Bitmap.Config.RGB_565;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.File;
import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2019/01/04
 *     desc  : test CacheDiskStaticUtils
 * </pre>
 */
public class CacheDiskStaticUtilsTest extends BaseTest {
    private static final String DISK1_PATH = ((TestConfig.PATH_CACHE) + "disk1") + (TestConfig.FILE_SEP);

    private static final String DISK2_PATH = ((TestConfig.PATH_CACHE) + "disk2") + (TestConfig.FILE_SEP);

    private static final File DISK1_FILE = new File(CacheDiskStaticUtilsTest.DISK1_PATH);

    private static final File DISK2_FILE = new File(CacheDiskStaticUtilsTest.DISK2_PATH);

    private static final CacheDiskUtils CACHE_DISK_UTILS1 = CacheDiskUtils.getInstance(CacheDiskStaticUtilsTest.DISK1_FILE);

    private static final CacheDiskUtils CACHE_DISK_UTILS2 = CacheDiskUtils.getInstance(CacheDiskStaticUtilsTest.DISK2_FILE);

    private static final byte[] BYTES = "CacheDiskUtils".getBytes();

    private static final String STRING = "CacheDiskUtils";

    private static final JSONObject JSON_OBJECT = new JSONObject();

    private static final JSONArray JSON_ARRAY = new JSONArray();

    private static final CacheDiskStaticUtilsTest.ParcelableTest PARCELABLE_TEST = new CacheDiskStaticUtilsTest.ParcelableTest("Blankj", "CacheDiskUtils");

    private static final CacheDiskStaticUtilsTest.SerializableTest SERIALIZABLE_TEST = new CacheDiskStaticUtilsTest.SerializableTest("Blankj", "CacheDiskUtils");

    private static final Bitmap BITMAP = Bitmap.createBitmap(100, 100, RGB_565);

    private static final Drawable DRAWABLE = new android.graphics.drawable.BitmapDrawable(Utils.getApp().getResources(), CacheDiskStaticUtilsTest.BITMAP);

    static {
        try {
            CacheDiskStaticUtilsTest.JSON_OBJECT.put("class", "CacheDiskUtils");
            CacheDiskStaticUtilsTest.JSON_OBJECT.put("author", "Blankj");
            CacheDiskStaticUtilsTest.JSON_ARRAY.put(0, CacheDiskStaticUtilsTest.JSON_OBJECT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBytes() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, new String(CacheDiskStaticUtils.getBytes("bytes1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1)));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, new String(CacheDiskStaticUtils.getBytes("bytes1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1)));
        Assert.assertNull(CacheDiskStaticUtils.getBytes("bytes2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, new String(CacheDiskStaticUtils.getBytes("bytes2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2)));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, new String(CacheDiskStaticUtils.getBytes("bytes2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2)));
        Assert.assertNull(CacheDiskStaticUtils.getBytes("bytes1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getString() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, CacheDiskStaticUtils.getString("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, CacheDiskStaticUtils.getString("string1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("string2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, CacheDiskStaticUtils.getString("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(CacheDiskStaticUtilsTest.STRING, CacheDiskStaticUtils.getString("string2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("string1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getJSONObject() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_OBJECT.toString(), CacheDiskStaticUtils.getJSONObject("jsonObject1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1).toString());
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_OBJECT.toString(), CacheDiskStaticUtils.getJSONObject("jsonObject1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1).toString());
        Assert.assertNull(CacheDiskStaticUtils.getJSONObject("jsonObject2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_OBJECT.toString(), CacheDiskStaticUtils.getJSONObject("jsonObject2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2).toString());
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_OBJECT.toString(), CacheDiskStaticUtils.getJSONObject("jsonObject2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2).toString());
        Assert.assertNull(CacheDiskStaticUtils.getJSONObject("jsonObject1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getJSONArray() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_ARRAY.toString(), CacheDiskStaticUtils.getJSONArray("jsonArray1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1).toString());
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_ARRAY.toString(), CacheDiskStaticUtils.getJSONArray("jsonArray1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1).toString());
        Assert.assertNull(CacheDiskStaticUtils.getJSONArray("jsonArray2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_ARRAY.toString(), CacheDiskStaticUtils.getJSONArray("jsonArray2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2).toString());
        Assert.assertEquals(CacheDiskStaticUtilsTest.JSON_ARRAY.toString(), CacheDiskStaticUtils.getJSONArray("jsonArray2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2).toString());
        Assert.assertNull(CacheDiskStaticUtils.getJSONArray("jsonArray1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getBitmap() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("bitmap1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("bitmap1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("bitmap2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("bitmap2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("bitmap2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("bitmap1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getDrawable() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("drawable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("drawable1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("drawable2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("drawable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(bitmapString, CacheDiskStaticUtils.getString("drawable2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("drawable1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getParcel() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.PARCELABLE_TEST, CacheDiskStaticUtils.getParcelable("parcelable1", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.PARCELABLE_TEST, CacheDiskStaticUtils.getParcelable("parcelable1", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getParcelable("parcelable2", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.PARCELABLE_TEST, CacheDiskStaticUtils.getParcelable("parcelable2", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(CacheDiskStaticUtilsTest.PARCELABLE_TEST, CacheDiskStaticUtils.getParcelable("parcelable2", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getParcelable("parcelable1", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getSerializable() {
        Assert.assertEquals(CacheDiskStaticUtilsTest.SERIALIZABLE_TEST, CacheDiskStaticUtils.getSerializable("serializable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.SERIALIZABLE_TEST, CacheDiskStaticUtils.getSerializable("serializable1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getSerializable("parcelable2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(CacheDiskStaticUtilsTest.SERIALIZABLE_TEST, CacheDiskStaticUtils.getSerializable("serializable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(CacheDiskStaticUtilsTest.SERIALIZABLE_TEST, CacheDiskStaticUtils.getSerializable("serializable2", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getSerializable("parcelable1", null, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getCacheSize() {
        Assert.assertEquals(FileUtils.getDirLength(CacheDiskStaticUtilsTest.DISK1_FILE), CacheDiskStaticUtils.getCacheSize(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(FileUtils.getDirLength(CacheDiskStaticUtilsTest.DISK2_FILE), CacheDiskStaticUtils.getCacheSize(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void getCacheCount() {
        Assert.assertEquals(8, CacheDiskStaticUtils.getCacheCount(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(8, CacheDiskStaticUtils.getCacheCount(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void remove() {
        Assert.assertNotNull(CacheDiskStaticUtils.getString("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        CacheDiskStaticUtils.remove("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1);
        Assert.assertNull(CacheDiskStaticUtils.getString("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        CacheDiskStaticUtils.remove("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2);
        Assert.assertNull(CacheDiskStaticUtils.getString("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    @Test
    public void clear() {
        Assert.assertNotNull(CacheDiskStaticUtils.getBytes("bytes1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getJSONObject("jsonObject1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getJSONArray("jsonArray1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("bitmap1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("drawable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getParcelable("parcelable1", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getSerializable("serializable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        CacheDiskStaticUtils.clear(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1);
        Assert.assertNull(CacheDiskStaticUtils.getBytes("bytes1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("string1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getJSONObject("jsonObject1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getJSONArray("jsonArray1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("bitmap1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getString("drawable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getParcelable("parcelable1", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNull(CacheDiskStaticUtils.getSerializable("serializable1", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(0, CacheDiskStaticUtils.getCacheSize(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertEquals(0, CacheDiskStaticUtils.getCacheCount(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS1));
        Assert.assertNotNull(CacheDiskStaticUtils.getBytes("bytes2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getJSONObject("jsonObject2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getJSONArray("jsonArray2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("bitmap2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getString("drawable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getParcelable("parcelable2", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNotNull(CacheDiskStaticUtils.getSerializable("serializable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        CacheDiskStaticUtils.clear(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2);
        Assert.assertNull(CacheDiskStaticUtils.getBytes("bytes2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("string2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getJSONObject("jsonObject2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getJSONArray("jsonArray2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("bitmap2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getString("drawable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getParcelable("parcelable2", CacheDiskStaticUtilsTest.ParcelableTest.CREATOR, CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertNull(CacheDiskStaticUtils.getSerializable("serializable2", CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(0, CacheDiskStaticUtils.getCacheSize(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
        Assert.assertEquals(0, CacheDiskStaticUtils.getCacheCount(CacheDiskStaticUtilsTest.CACHE_DISK_UTILS2));
    }

    static class ParcelableTest implements Parcelable {
        String author;

        String className;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        ParcelableTest(String author, String className) {
            this.author = author;
            this.className = className;
        }

        ParcelableTest(Parcel in) {
            author = in.readString();
            className = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(author);
            dest.writeString(className);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CacheDiskStaticUtilsTest.ParcelableTest> CREATOR = new Creator<CacheDiskStaticUtilsTest.ParcelableTest>() {
            @Override
            public CacheDiskStaticUtilsTest.ParcelableTest createFromParcel(Parcel in) {
                return new CacheDiskStaticUtilsTest.ParcelableTest(in);
            }

            @Override
            public CacheDiskStaticUtilsTest.ParcelableTest[] newArray(int size) {
                return new CacheDiskStaticUtilsTest.ParcelableTest[size];
            }
        };

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof CacheDiskStaticUtilsTest.ParcelableTest) && (((CacheDiskStaticUtilsTest.ParcelableTest) (obj)).author.equals(author))) && (((CacheDiskStaticUtilsTest.ParcelableTest) (obj)).className.equals(className));
        }
    }

    static class SerializableTest implements Serializable {
        private static final long serialVersionUID = -5806706668736895024L;

        String author;

        String className;

        SerializableTest(String author, String className) {
            this.author = author;
            this.className = className;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof CacheDiskStaticUtilsTest.SerializableTest) && (((CacheDiskStaticUtilsTest.SerializableTest) (obj)).author.equals(author))) && (((CacheDiskStaticUtilsTest.SerializableTest) (obj)).className.equals(className));
        }
    }
}


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
 *     time  : 2018/06/13
 *     desc  : test CacheDoubleUtils
 * </pre>
 */
public class CacheDoubleUtilsTest extends BaseTest {
    private static final String CACHE_PATH = ((TestConfig.PATH_CACHE) + "double") + (TestConfig.FILE_SEP);

    private static final File CACHE_FILE = new File(CacheDoubleUtilsTest.CACHE_PATH);

    private static final byte[] BYTES = "CacheDoubleUtils".getBytes();

    private static final String STRING = "CacheDoubleUtils";

    private static final JSONObject JSON_OBJECT = new JSONObject();

    private static final JSONArray JSON_ARRAY = new JSONArray();

    private static final CacheDoubleUtilsTest.ParcelableTest PARCELABLE_TEST = new CacheDoubleUtilsTest.ParcelableTest("Blankj", "CacheDoubleUtils");

    private static final CacheDoubleUtilsTest.SerializableTest SERIALIZABLE_TEST = new CacheDoubleUtilsTest.SerializableTest("Blankj", "CacheDoubleUtils");

    private static final Bitmap BITMAP = Bitmap.createBitmap(100, 100, RGB_565);

    private static final Drawable DRAWABLE = new android.graphics.drawable.BitmapDrawable(Utils.getApp().getResources(), CacheDoubleUtilsTest.BITMAP);

    private static final CacheMemoryUtils CACHE_MEMORY_UTILS = CacheMemoryUtils.getInstance();

    private static final CacheDiskUtils CACHE_DISK_UTILS = CacheDiskUtils.getInstance(CacheDoubleUtilsTest.CACHE_FILE);

    private static final CacheDoubleUtils CACHE_DOUBLE_UTILS = CacheDoubleUtils.getInstance(CacheDoubleUtilsTest.CACHE_MEMORY_UTILS, CacheDoubleUtilsTest.CACHE_DISK_UTILS);

    static {
        try {
            CacheDoubleUtilsTest.JSON_OBJECT.put("class", "CacheDiskUtils");
            CacheDoubleUtilsTest.JSON_OBJECT.put("author", "Blankj");
            CacheDoubleUtilsTest.JSON_ARRAY.put(0, CacheDoubleUtilsTest.JSON_OBJECT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBytes() {
        Assert.assertEquals(CacheDoubleUtilsTest.STRING, new String(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes")));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("bytes");
        Assert.assertEquals(CacheDoubleUtilsTest.STRING, new String(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes")));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("bytes");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes"));
    }

    @Test
    public void getString() {
        Assert.assertEquals(CacheDoubleUtilsTest.STRING, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("string");
        Assert.assertEquals(CacheDoubleUtilsTest.STRING, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("string");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
    }

    @Test
    public void getJSONObject() {
        Assert.assertEquals(CacheDoubleUtilsTest.JSON_OBJECT.toString(), CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject").toString());
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("jsonObject");
        Assert.assertEquals(CacheDoubleUtilsTest.JSON_OBJECT.toString(), CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject").toString());
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("jsonObject");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("jsonObject"));
    }

    @Test
    public void getJSONArray() {
        Assert.assertEquals(CacheDoubleUtilsTest.JSON_ARRAY.toString(), CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray").toString());
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("jsonArray");
        Assert.assertEquals(CacheDoubleUtilsTest.JSON_ARRAY.toString(), CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray").toString());
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("jsonArray");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("jsonArray"));
    }

    @Test
    public void getBitmap() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(CacheDoubleUtilsTest.BITMAP, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("bitmap");
        Assert.assertEquals(bitmapString, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("bitmap"));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("bitmap");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("bitmap"));
    }

    @Test
    public void getDrawable() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(CacheDoubleUtilsTest.DRAWABLE, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("drawable");
        Assert.assertEquals(bitmapString, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("drawable"));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("drawable");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("drawable"));
    }

    @Test
    public void getParcel() {
        Assert.assertEquals(CacheDoubleUtilsTest.PARCELABLE_TEST, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleUtilsTest.ParcelableTest.CREATOR));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("parcelable");
        Assert.assertEquals(CacheDoubleUtilsTest.PARCELABLE_TEST, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleUtilsTest.ParcelableTest.CREATOR));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("parcelable");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("parcelable"));
    }

    @Test
    public void getSerializable() {
        Assert.assertEquals(CacheDoubleUtilsTest.SERIALIZABLE_TEST, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleUtilsTest.CACHE_MEMORY_UTILS.remove("serializable");
        Assert.assertEquals(CacheDoubleUtilsTest.SERIALIZABLE_TEST, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleUtilsTest.CACHE_DISK_UTILS.remove("serializable");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("serializable"));
    }

    @Test
    public void getCacheDiskSize() {
        Assert.assertEquals(FileUtils.getDirLength(CacheDoubleUtilsTest.CACHE_FILE), CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskSize());
    }

    @Test
    public void getCacheDiskCount() {
        Assert.assertEquals(8, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskCount());
    }

    @Test
    public void getCacheMemoryCount() {
        Assert.assertEquals(8, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheMemoryCount());
    }

    @Test
    public void remove() {
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.remove("string");
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
    }

    @Test
    public void clear() {
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNotNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.clear();
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNull(CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        Assert.assertEquals(0, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskSize());
        Assert.assertEquals(0, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskCount());
        Assert.assertEquals(0, CacheDoubleUtilsTest.CACHE_DOUBLE_UTILS.getCacheMemoryCount());
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

        public static final Creator<CacheDoubleUtilsTest.ParcelableTest> CREATOR = new Creator<CacheDoubleUtilsTest.ParcelableTest>() {
            @Override
            public CacheDoubleUtilsTest.ParcelableTest createFromParcel(Parcel in) {
                return new CacheDoubleUtilsTest.ParcelableTest(in);
            }

            @Override
            public CacheDoubleUtilsTest.ParcelableTest[] newArray(int size) {
                return new CacheDoubleUtilsTest.ParcelableTest[size];
            }
        };

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof CacheDoubleUtilsTest.ParcelableTest) && (((CacheDoubleUtilsTest.ParcelableTest) (obj)).author.equals(author))) && (((CacheDoubleUtilsTest.ParcelableTest) (obj)).className.equals(className));
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
            return ((obj instanceof CacheDoubleUtilsTest.SerializableTest) && (((CacheDoubleUtilsTest.SerializableTest) (obj)).author.equals(author))) && (((CacheDoubleUtilsTest.SerializableTest) (obj)).className.equals(className));
        }
    }
}


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
 *     desc  : test CacheDoubleStaticUtils
 * </pre>
 */
public class CacheDoubleStaticUtilsTest extends BaseTest {
    private static final String CACHE_PATH = ((TestConfig.PATH_CACHE) + "double") + (TestConfig.FILE_SEP);

    private static final File CACHE_FILE = new File(CacheDoubleStaticUtilsTest.CACHE_PATH);

    private static final byte[] BYTES = "CacheDoubleUtils".getBytes();

    private static final String STRING = "CacheDoubleUtils";

    private static final JSONObject JSON_OBJECT = new JSONObject();

    private static final JSONArray JSON_ARRAY = new JSONArray();

    private static final CacheDoubleStaticUtilsTest.ParcelableTest PARCELABLE_TEST = new CacheDoubleStaticUtilsTest.ParcelableTest("Blankj", "CacheDoubleUtils");

    private static final CacheDoubleStaticUtilsTest.SerializableTest SERIALIZABLE_TEST = new CacheDoubleStaticUtilsTest.SerializableTest("Blankj", "CacheDoubleUtils");

    private static final Bitmap BITMAP = Bitmap.createBitmap(100, 100, RGB_565);

    private static final Drawable DRAWABLE = new android.graphics.drawable.BitmapDrawable(Utils.getApp().getResources(), CacheDoubleStaticUtilsTest.BITMAP);

    private static final CacheMemoryUtils CACHE_MEMORY_UTILS = CacheMemoryUtils.getInstance();

    private static final CacheDiskUtils CACHE_DISK_UTILS = CacheDiskUtils.getInstance(CacheDoubleStaticUtilsTest.CACHE_FILE);

    private static final CacheDoubleUtils CACHE_DOUBLE_UTILS = CacheDoubleUtils.getInstance(CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS, CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS);

    static {
        try {
            CacheDoubleStaticUtilsTest.JSON_OBJECT.put("class", "CacheDoubleUtils");
            CacheDoubleStaticUtilsTest.JSON_OBJECT.put("author", "Blankj");
            CacheDoubleStaticUtilsTest.JSON_ARRAY.put(0, CacheDoubleStaticUtilsTest.JSON_OBJECT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBytes() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.STRING, new String(CacheDoubleStaticUtils.getBytes("bytes", CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS)));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("bytes");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.STRING, new String(CacheDoubleStaticUtils.getBytes("bytes", CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS)));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("bytes");
        Assert.assertNull(CacheDoubleStaticUtils.getBytes("bytes"));
    }

    @Test
    public void getString() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.STRING, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("string");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.STRING, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("string");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
    }

    @Test
    public void getJSONObject() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.JSON_OBJECT.toString(), CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject").toString());
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("jsonObject");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.JSON_OBJECT.toString(), CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject").toString());
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("jsonObject");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("jsonObject"));
    }

    @Test
    public void getJSONArray() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.JSON_ARRAY.toString(), CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray").toString());
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("jsonArray");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.JSON_ARRAY.toString(), CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray").toString());
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("jsonArray");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("jsonArray"));
    }

    @Test
    public void getBitmap() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(CacheDoubleStaticUtilsTest.BITMAP, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("bitmap");
        Assert.assertEquals(bitmapString, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("bitmap"));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("bitmap");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("bitmap"));
    }

    @Test
    public void getDrawable() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(CacheDoubleStaticUtilsTest.DRAWABLE, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("drawable");
        Assert.assertEquals(bitmapString, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("drawable"));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("drawable");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("drawable"));
    }

    @Test
    public void getParcel() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.PARCELABLE_TEST, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleStaticUtilsTest.ParcelableTest.CREATOR));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("parcelable");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.PARCELABLE_TEST, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleStaticUtilsTest.ParcelableTest.CREATOR));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("parcelable");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("parcelable"));
    }

    @Test
    public void getSerializable() {
        Assert.assertEquals(CacheDoubleStaticUtilsTest.SERIALIZABLE_TEST, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleStaticUtilsTest.CACHE_MEMORY_UTILS.remove("serializable");
        Assert.assertEquals(CacheDoubleStaticUtilsTest.SERIALIZABLE_TEST, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleStaticUtilsTest.CACHE_DISK_UTILS.remove("serializable");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("serializable"));
    }

    @Test
    public void getCacheDiskSize() {
        Assert.assertEquals(FileUtils.getDirLength(CacheDoubleStaticUtilsTest.CACHE_FILE), CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskSize());
    }

    @Test
    public void getCacheDiskCount() {
        Assert.assertEquals(8, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskCount());
    }

    @Test
    public void getCacheMemoryCount() {
        Assert.assertEquals(8, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheMemoryCount());
    }

    @Test
    public void remove() {
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.remove("string");
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
    }

    @Test
    public void clear() {
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleStaticUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNotNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.clear();
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getBytes("bytes"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getString("string"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONObject("jsonObject"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getJSONArray("jsonArray"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getBitmap("bitmap"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getDrawable("drawable"));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getParcelable("parcelable", CacheDoubleStaticUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNull(CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getSerializable("serializable"));
        Assert.assertEquals(0, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskSize());
        Assert.assertEquals(0, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheDiskCount());
        Assert.assertEquals(0, CacheDoubleStaticUtilsTest.CACHE_DOUBLE_UTILS.getCacheMemoryCount());
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

        public static final Creator<CacheDoubleStaticUtilsTest.ParcelableTest> CREATOR = new Creator<CacheDoubleStaticUtilsTest.ParcelableTest>() {
            @Override
            public CacheDoubleStaticUtilsTest.ParcelableTest createFromParcel(Parcel in) {
                return new CacheDoubleStaticUtilsTest.ParcelableTest(in);
            }

            @Override
            public CacheDoubleStaticUtilsTest.ParcelableTest[] newArray(int size) {
                return new CacheDoubleStaticUtilsTest.ParcelableTest[size];
            }
        };

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof CacheDoubleStaticUtilsTest.ParcelableTest) && (((CacheDoubleStaticUtilsTest.ParcelableTest) (obj)).author.equals(author))) && (((CacheDoubleStaticUtilsTest.ParcelableTest) (obj)).className.equals(className));
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
            return ((obj instanceof CacheDoubleStaticUtilsTest.SerializableTest) && (((CacheDoubleStaticUtilsTest.SerializableTest) (obj)).author.equals(author))) && (((CacheDoubleStaticUtilsTest.SerializableTest) (obj)).className.equals(className));
        }
    }
}


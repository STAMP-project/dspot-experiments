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
 *     time  : 2017/05/26
 *     desc  : test CacheDiskUtils
 * </pre>
 */
public class CacheDiskUtilsTest extends BaseTest {
    private static final String DISK1_PATH = ((TestConfig.PATH_CACHE) + "disk1") + (TestConfig.FILE_SEP);

    private static final String DISK2_PATH = ((TestConfig.PATH_CACHE) + "disk2") + (TestConfig.FILE_SEP);

    private static final File DISK1_FILE = new File(CacheDiskUtilsTest.DISK1_PATH);

    private static final File DISK2_FILE = new File(CacheDiskUtilsTest.DISK2_PATH);

    private static final CacheDiskUtils CACHE_DISK_UTILS1 = CacheDiskUtils.getInstance(CacheDiskUtilsTest.DISK1_FILE);

    private static final CacheDiskUtils CACHE_DISK_UTILS2 = CacheDiskUtils.getInstance(CacheDiskUtilsTest.DISK2_FILE);

    private static final byte[] BYTES = "CacheDiskUtils".getBytes();

    private static final String STRING = "CacheDiskUtils";

    private static final JSONObject JSON_OBJECT = new JSONObject();

    private static final JSONArray JSON_ARRAY = new JSONArray();

    private static final CacheDiskUtilsTest.ParcelableTest PARCELABLE_TEST = new CacheDiskUtilsTest.ParcelableTest("Blankj", "CacheDiskUtils");

    private static final CacheDiskUtilsTest.SerializableTest SERIALIZABLE_TEST = new CacheDiskUtilsTest.SerializableTest("Blankj", "CacheDiskUtils");

    private static final Bitmap BITMAP = Bitmap.createBitmap(100, 100, RGB_565);

    private static final Drawable DRAWABLE = new android.graphics.drawable.BitmapDrawable(Utils.getApp().getResources(), CacheDiskUtilsTest.BITMAP);

    static {
        try {
            CacheDiskUtilsTest.JSON_OBJECT.put("class", "CacheDiskUtils");
            CacheDiskUtilsTest.JSON_OBJECT.put("author", "Blankj");
            CacheDiskUtilsTest.JSON_ARRAY.put(0, CacheDiskUtilsTest.JSON_OBJECT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getBytes() {
        Assert.assertEquals(CacheDiskUtilsTest.STRING, new String(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getBytes("bytes1")));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, new String(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getBytes("bytes1", null)));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getBytes("bytes2", null));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, new String(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getBytes("bytes2")));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, new String(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getBytes("bytes2", null)));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getBytes("bytes1", null));
    }

    @Test
    public void getString() {
        Assert.assertEquals(CacheDiskUtilsTest.STRING, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1"));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string2", null));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2"));
        Assert.assertEquals(CacheDiskUtilsTest.STRING, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string1", null));
    }

    @Test
    public void getJSONObject() {
        Assert.assertEquals(CacheDiskUtilsTest.JSON_OBJECT.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONObject("jsonObject1").toString());
        Assert.assertEquals(CacheDiskUtilsTest.JSON_OBJECT.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONObject("jsonObject1", null).toString());
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONObject("jsonObject2", null));
        Assert.assertEquals(CacheDiskUtilsTest.JSON_OBJECT.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONObject("jsonObject2").toString());
        Assert.assertEquals(CacheDiskUtilsTest.JSON_OBJECT.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONObject("jsonObject2", null).toString());
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONObject("jsonObject1", null));
    }

    @Test
    public void getJSONArray() {
        Assert.assertEquals(CacheDiskUtilsTest.JSON_ARRAY.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONArray("jsonArray1").toString());
        Assert.assertEquals(CacheDiskUtilsTest.JSON_ARRAY.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONArray("jsonArray1", null).toString());
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONArray("jsonArray2", null));
        Assert.assertEquals(CacheDiskUtilsTest.JSON_ARRAY.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONArray("jsonArray2").toString());
        Assert.assertEquals(CacheDiskUtilsTest.JSON_ARRAY.toString(), CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONArray("jsonArray2", null).toString());
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONArray("jsonArray1", null));
    }

    @Test
    public void getBitmap() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("bitmap1"));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("bitmap1", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("bitmap2", null));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("bitmap2"));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("bitmap2", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("bitmap1", null));
    }

    @Test
    public void getDrawable() {
        String bitmapString = "Bitmap (100 x 100) compressed as PNG with quality 100";
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("drawable1"));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("drawable1", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("drawable2", null));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("drawable2"));
        Assert.assertEquals(bitmapString, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("drawable2", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("drawable1", null));
    }

    @Test
    public void getParcel() {
        Assert.assertEquals(CacheDiskUtilsTest.PARCELABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getParcelable("parcelable1", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertEquals(CacheDiskUtilsTest.PARCELABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getParcelable("parcelable1", CacheDiskUtilsTest.ParcelableTest.CREATOR, null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getParcelable("parcelable2", CacheDiskUtilsTest.ParcelableTest.CREATOR, null));
        Assert.assertEquals(CacheDiskUtilsTest.PARCELABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getParcelable("parcelable2", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertEquals(CacheDiskUtilsTest.PARCELABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getParcelable("parcelable2", CacheDiskUtilsTest.ParcelableTest.CREATOR, null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getParcelable("parcelable1", CacheDiskUtilsTest.ParcelableTest.CREATOR, null));
    }

    @Test
    public void getSerializable() {
        Assert.assertEquals(CacheDiskUtilsTest.SERIALIZABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getSerializable("serializable1"));
        Assert.assertEquals(CacheDiskUtilsTest.SERIALIZABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getSerializable("serializable1", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getSerializable("parcelable2", null));
        Assert.assertEquals(CacheDiskUtilsTest.SERIALIZABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getSerializable("serializable2"));
        Assert.assertEquals(CacheDiskUtilsTest.SERIALIZABLE_TEST, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getSerializable("serializable2", null));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getSerializable("parcelable1", null));
    }

    @Test
    public void getCacheSize() {
        Assert.assertEquals(FileUtils.getDirLength(CacheDiskUtilsTest.DISK1_FILE), CacheDiskUtilsTest.CACHE_DISK_UTILS1.getCacheSize());
        Assert.assertEquals(FileUtils.getDirLength(CacheDiskUtilsTest.DISK2_FILE), CacheDiskUtilsTest.CACHE_DISK_UTILS2.getCacheSize());
    }

    @Test
    public void getCacheCount() {
        Assert.assertEquals(8, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getCacheCount());
        Assert.assertEquals(8, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getCacheCount());
    }

    @Test
    public void remove() {
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1"));
        CacheDiskUtilsTest.CACHE_DISK_UTILS1.remove("string1");
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2"));
        CacheDiskUtilsTest.CACHE_DISK_UTILS2.remove("string2");
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2"));
    }

    @Test
    public void clear() {
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getBytes("bytes1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONObject("jsonObject1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONArray("jsonArray1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("bitmap1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("drawable1"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getParcelable("parcelable1", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getSerializable("serializable1"));
        CacheDiskUtilsTest.CACHE_DISK_UTILS1.clear();
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getBytes("bytes1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("string1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONObject("jsonObject1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getJSONArray("jsonArray1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("bitmap1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getString("drawable1"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getParcelable("parcelable1", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS1.getSerializable("serializable1"));
        Assert.assertEquals(0, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getCacheSize());
        Assert.assertEquals(0, CacheDiskUtilsTest.CACHE_DISK_UTILS1.getCacheCount());
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getBytes("bytes2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONObject("jsonObject2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONArray("jsonArray2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("bitmap2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("drawable2"));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getParcelable("parcelable2", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNotNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getSerializable("serializable2"));
        CacheDiskUtilsTest.CACHE_DISK_UTILS2.clear();
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getBytes("bytes2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("string2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONObject("jsonObject2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getJSONArray("jsonArray2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("bitmap2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getString("drawable2"));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getParcelable("parcelable2", CacheDiskUtilsTest.ParcelableTest.CREATOR));
        Assert.assertNull(CacheDiskUtilsTest.CACHE_DISK_UTILS2.getSerializable("serializable2"));
        Assert.assertEquals(0, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getCacheSize());
        Assert.assertEquals(0, CacheDiskUtilsTest.CACHE_DISK_UTILS2.getCacheCount());
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

        public static final Creator<CacheDiskUtilsTest.ParcelableTest> CREATOR = new Creator<CacheDiskUtilsTest.ParcelableTest>() {
            @Override
            public CacheDiskUtilsTest.ParcelableTest createFromParcel(Parcel in) {
                return new CacheDiskUtilsTest.ParcelableTest(in);
            }

            @Override
            public CacheDiskUtilsTest.ParcelableTest[] newArray(int size) {
                return new CacheDiskUtilsTest.ParcelableTest[size];
            }
        };

        @Override
        public boolean equals(Object obj) {
            return ((obj instanceof CacheDiskUtilsTest.ParcelableTest) && (((CacheDiskUtilsTest.ParcelableTest) (obj)).author.equals(author))) && (((CacheDiskUtilsTest.ParcelableTest) (obj)).className.equals(className));
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
            return ((obj instanceof CacheDiskUtilsTest.SerializableTest) && (((CacheDiskUtilsTest.SerializableTest) (obj)).author.equals(author))) && (((CacheDiskUtilsTest.SerializableTest) (obj)).className.equals(className));
        }
    }
}


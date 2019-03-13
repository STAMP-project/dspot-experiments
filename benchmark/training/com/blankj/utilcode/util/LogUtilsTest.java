package com.blankj.utilcode.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/26
 *     desc  : test LogUtils
 * </pre>
 */
public class LogUtilsTest extends BaseTest {
    private static final String JSON = "\r\n{\"tools\": [{ \"name\":\"css format\" , \"site\":\"http://tools.w3cschool.cn/code/css\" },{ \"name\":\"JSON format\" , \"site\":\"http://tools.w3cschool.cn/code/JSON\" },{ \"name\":\"pwd check\" , \"site\":\"http://tools.w3cschool.cn/password/my_password_safe\" }]}";

    private static final String XML = "<books><book><author>Jack Herrington</author><title>PHP Hacks</title><publisher>O'Reilly</publisher></book><book><author>Jack Herrington</author><title>Podcasting Hacks</title><publisher>O'Reilly</publisher></book></books>";

    private static final int[] ONE_D_ARRAY = new int[]{ 1, 2, 3 };

    private static final int[][] TWO_D_ARRAY = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };

    private static final ArrayList<String> LIST = new ArrayList<>();

    private static final Map<String, Object> MAP = new HashMap<>();

    @Test
    public void testV() {
        LogUtils.v();
        LogUtils.v("");
        LogUtils.v(null);
        LogUtils.v("hello");
        LogUtils.v("hello\nworld");
        LogUtils.v("hello", "world");
    }

    @Test
    public void testVTag() {
        LogUtils.vTag("");
        LogUtils.vTag("", "");
        LogUtils.vTag("TAG", null);
        LogUtils.vTag("TAG", "hello");
        LogUtils.vTag("TAG", "hello\nworld");
        LogUtils.vTag("TAG", "hello", "world");
    }

    @Test
    public void testD() {
        LogUtils.d();
        LogUtils.d("");
        LogUtils.d(null);
        LogUtils.d("hello");
        LogUtils.d("hello\nworld");
        LogUtils.d("hello", "world");
    }

    @Test
    public void testDTag() {
        LogUtils.dTag("");
        LogUtils.dTag("", "");
        LogUtils.dTag("TAG", null);
        LogUtils.dTag("TAG", "hello");
        LogUtils.dTag("TAG", "hello\nworld");
        LogUtils.dTag("TAG", "hello", "world");
    }

    @Test
    public void testI() {
        LogUtils.i();
        LogUtils.i("");
        LogUtils.i(null);
        LogUtils.i("hello");
        LogUtils.i("hello\nworld");
        LogUtils.i("hello", "world");
    }

    @Test
    public void testITag() {
        LogUtils.iTag("");
        LogUtils.iTag("", "");
        LogUtils.iTag("TAG", null);
        LogUtils.iTag("TAG", "hello");
        LogUtils.iTag("TAG", "hello\nworld");
        LogUtils.iTag("TAG", "hello", "world");
    }

    @Test
    public void testW() {
        LogUtils.w();
        LogUtils.w("");
        LogUtils.w(null);
        LogUtils.w("hello");
        LogUtils.w("hello\nworld");
        LogUtils.w("hello", "world");
    }

    @Test
    public void testWTag() {
        LogUtils.wTag("");
        LogUtils.wTag("", "");
        LogUtils.wTag("TAG", null);
        LogUtils.wTag("TAG", "hello");
        LogUtils.wTag("TAG", "hello\nworld");
        LogUtils.wTag("TAG", "hello", "world");
    }

    @Test
    public void testE() {
        LogUtils.e();
        LogUtils.e("");
        LogUtils.e(null);
        LogUtils.e("hello");
        LogUtils.e("hello\nworld");
        LogUtils.e("hello", "world");
    }

    @Test
    public void testETag() {
        LogUtils.eTag("");
        LogUtils.eTag("", "");
        LogUtils.eTag("TAG", null);
        LogUtils.eTag("TAG", "hello");
        LogUtils.eTag("TAG", "hello\nworld");
        LogUtils.eTag("TAG", "hello", "world");
    }

    @Test
    public void testA() {
        LogUtils.a();
        LogUtils.a("");
        LogUtils.a(null);
        LogUtils.a("hello");
        LogUtils.a("hello\nworld");
        LogUtils.a("hello", "world");
    }

    @Test
    public void testATag() {
        LogUtils.aTag("");
        LogUtils.aTag("", "");
        LogUtils.aTag("TAG", null);
        LogUtils.aTag("TAG", "hello");
        LogUtils.aTag("TAG", "hello\nworld");
        LogUtils.aTag("TAG", "hello", "world");
    }

    @Test
    public void testJson() {
        LogUtils.json(LogUtilsTest.JSON);
        LogUtils.json(new LogUtilsTest.Person("Blankj"));
    }

    @Test
    public void testXml() {
        LogUtils.xml(LogUtilsTest.XML);
    }

    @Test
    public void testObject() {
        LogUtilsTest.LIST.add("hello");
        LogUtilsTest.LIST.add("log");
        LogUtilsTest.LIST.add("utils");
        LogUtilsTest.MAP.put("name", "AndroidUtilCode");
        LogUtilsTest.MAP.put("class", "LogUtils");
        LogUtils.d(((Object) (LogUtilsTest.ONE_D_ARRAY)));
        LogUtils.d(((Object) (LogUtilsTest.TWO_D_ARRAY)));
        LogUtils.d(LogUtilsTest.LIST);
        LogUtils.d(LogUtilsTest.MAP);
    }

    static class Person {
        String name;

        int gender;

        String address;

        public Person(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == (this))
                return true;

            if (!(obj instanceof LogUtilsTest.Person))
                return false;

            LogUtilsTest.Person p = ((LogUtilsTest.Person) (obj));
            return ((LogUtilsTest.Person.equals(name, p.name)) && ((p.gender) == (gender))) && (LogUtilsTest.Person.equals(address, p.address));
        }

        private static boolean equals(final Object o1, final Object o2) {
            return (o1 == o2) || ((o1 != null) && (o1.equals(o2)));
        }

        @Override
        public String toString() {
            return ((((("{\"name\":" + (LogUtilsTest.primitive2String(name))) + ",\"gender\":") + (LogUtilsTest.primitive2String(gender))) + ",\"address\":") + (LogUtilsTest.primitive2String(address))) + "}";
        }
    }
}


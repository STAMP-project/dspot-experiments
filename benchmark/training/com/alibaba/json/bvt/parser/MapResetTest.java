package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class MapResetTest extends TestCase {
    public void test_0() throws Exception {
        MapResetTest.Book book = new MapResetTest.Book();
        // book.setMetadata(new MetaData());
        String text = JSON.toJSONString(book);
        System.out.println(text);
        MapResetTest.Book book2 = JSON.parseObject(text, MapResetTest.Book.class);
        System.out.println(JSON.toJSONString(book2));
    }

    public static class Book {
        private int id;

        private int pageCountNum;

        private MapResetTest.MetaData metadata;

        public int getPageCountNum() {
            return pageCountNum;
        }

        public void setPageCountNum(int pageCountNum) {
            this.pageCountNum = pageCountNum;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public MapResetTest.MetaData getMetadata() {
            return metadata;
        }

        public void setMetadata(MapResetTest.MetaData metadata) {
            this.metadata = metadata;
        }
    }

    public static class MetaData {}
}


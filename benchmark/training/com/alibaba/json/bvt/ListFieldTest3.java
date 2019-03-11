package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import data.media.MediaContent;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class ListFieldTest3 extends TestCase {
    public void test_typeRef() throws Exception {
        String text = "{\"images\":[],\"media\":{\"width\":640}}";
        MediaContent object = JSON.parseObject(text, MediaContent.class);
    }

    public static class Root {
        private List<ListFieldTest3.Image> images = new ArrayList<ListFieldTest3.Image>();

        private ListFieldTest3.Entity media;

        public List<ListFieldTest3.Image> getImages() {
            return images;
        }

        public void setImages(List<ListFieldTest3.Image> images) {
            this.images = images;
        }

        public ListFieldTest3.Entity getMedia() {
            return media;
        }

        public void setMedia(ListFieldTest3.Entity media) {
            this.media = media;
        }
    }

    public static class Image {
        public int width;
    }

    public static class Entity {
        public String title;// Can be null


        public int width;

        public int height;

        public ListFieldTest3.Size size;
    }

    public enum Size {

        SMALL,
        LARGE;}
}


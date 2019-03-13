package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericTest2 extends TestCase {
    public void test_for_bingyang() throws Exception {
        String text = "{\"count\":123,\"index\":7,\"items\":[{\"id\":234,\"latitude\":2.5,\"longtitude\":3.7}]}";
        GenericTest2.PageBean<GenericTest2.ActiveBase> pageBean = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<GenericTest2.PageBean<GenericTest2.ActiveBase>>() {});
        Assert.assertNotNull(pageBean);
        Assert.assertEquals(123, pageBean.getCount());
        Assert.assertEquals(7, pageBean.getIndex());
        Assert.assertNotNull(pageBean.getItems());
        Assert.assertEquals(1, pageBean.getItems().size());
        GenericTest2.ActiveBase active = pageBean.getItems().get(0);
        Assert.assertEquals(new Integer(234), active.getId());
        Assert.assertTrue((3.7 == (active.getLongtitude())));
        Assert.assertTrue((2.5 == (active.getLatitude())));
    }

    public static class ActiveBase extends GenericTest2.BaseModel {
        private double latitude;

        private double longtitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongtitude() {
            return longtitude;
        }

        public void setLongtitude(double longtitude) {
            this.longtitude = longtitude;
        }
    }

    public static class BaseModel {
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class PageBean<T> {
        private int count;

        private int index;

        private List<T> items;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }
    }
}


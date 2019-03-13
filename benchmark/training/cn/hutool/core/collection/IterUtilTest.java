package cn.hutool.core.collection;


import java.util.ArrayList;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link IterUtil} ????
 *
 * @author looly
 */
public class IterUtilTest {
    @Test
    public void countMapTest() {
        ArrayList<String> list = CollUtil.newArrayList("a", "b", "c", "c", "a", "b", "d");
        Map<String, Integer> countMap = IterUtil.countMap(list);
        Assert.assertEquals(Integer.valueOf(2), countMap.get("a"));
        Assert.assertEquals(Integer.valueOf(2), countMap.get("b"));
        Assert.assertEquals(Integer.valueOf(2), countMap.get("c"));
        Assert.assertEquals(Integer.valueOf(1), countMap.get("d"));
    }

    @Test
    public void fieldValueMapTest() {
        ArrayList<IterUtilTest.Car> carList = CollUtil.newArrayList(new IterUtilTest.Car("123", "??"), new IterUtilTest.Car("345", "??"), new IterUtilTest.Car("567", "??"));
        Map<String, IterUtilTest.Car> carNameMap = IterUtil.fieldValueMap(carList, "carNumber");
        Assert.assertEquals("??", carNameMap.get("123").getCarName());
        Assert.assertEquals("??", carNameMap.get("345").getCarName());
        Assert.assertEquals("??", carNameMap.get("567").getCarName());
    }

    @Test
    public void joinTest() {
        ArrayList<String> list = CollUtil.newArrayList("1", "2", "3", "4");
        String join = IterUtil.join(list, ":");
        Assert.assertEquals("1:2:3:4", join);
        ArrayList<Integer> list1 = CollUtil.newArrayList(1, 2, 3, 4);
        String join1 = IterUtil.join(list1, ":");
        Assert.assertEquals("1:2:3:4", join1);
        ArrayList<String> list2 = CollUtil.newArrayList("1", "2", "3", "4");
        String join2 = IterUtil.join(list2, ":", "\"", "\"");
        Assert.assertEquals("\"1\":\"2\":\"3\":\"4\"", join2);
    }

    public static class Car {
        private String carNumber;

        private String carName;

        public Car(String carNumber, String carName) {
            this.carNumber = carNumber;
            this.carName = carName;
        }

        public String getCarNumber() {
            return carNumber;
        }

        public void setCarNumber(String carNumber) {
            this.carNumber = carNumber;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }
    }
}


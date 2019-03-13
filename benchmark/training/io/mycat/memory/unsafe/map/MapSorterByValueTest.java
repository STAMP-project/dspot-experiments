package io.mycat.memory.unsafe.map;


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Created by znix on 2016/7/4.
 */
public class MapSorterByValueTest {
    @Test
    public void testMapSorterByValue() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("q", 23);
        map.put("b", 4);
        map.put("c", 5);
        map.put("d", 6);
        Map<String, Integer> resultMap = mapSorterByValue(map);// ?Value????

        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            System.out.println((((entry.getKey()) + " ") + (entry.getValue())));
        }
    }
}


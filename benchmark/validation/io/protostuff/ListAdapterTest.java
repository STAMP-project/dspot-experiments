package io.protostuff;


import ListAdapter.Converter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class ListAdapterTest {
    @Test
    public void checkConvertedValues() throws Exception {
        List<Integer> a = new ArrayList<Integer>();
        ListAdapter<Integer, String> adapter = new ListAdapter<Integer, String>(a, new Converter<Integer, String>() {
            @Override
            public String convert(Integer from) {
                return from.toString();
            }
        });
        a.add(5);
        a.add(10);
        Assert.assertEquals(2, adapter.size());
        Assert.assertEquals("5", adapter.get(0));
        Assert.assertEquals("10", adapter.get(1));
    }
}


package com.sankuai.waimai.router.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PriorityListTest {
    @Test
    public void testItr() {
        List<Object> list = new ArrayList<>();
        list.add("5");
        ListIterator<Object> iterator = list.listIterator();
        Object next = iterator.next();
        System.out.println(next);
        Object previous = iterator.previous();
        System.out.println(previous);
    }

    @Test
    public void testSort() throws Exception {
        testSort(new int[]{  }, new int[]{  });
        testSort(new int[]{ 1 }, new int[]{ 1 });
        testSort(new int[]{ 1, 3 }, new int[]{ 3, 1 });
        testSort(new int[]{ 1, 3, 5 }, new int[]{ 5, 3, 1 });
        testSort(new int[]{ 5, 4 }, new int[]{ 5, 4 });
        testSort(new int[]{ 5, 4, 7 }, new int[]{ 7, 5, 4 });
        testSort(new int[]{ 1, 3, 5, 4, 7, 6 }, new int[]{ 7, 6, 5, 4, 3, 1 });
        testSort(new int[]{ 1, 3, 5, 4, 7, 6, 0 }, new int[]{ 7, 6, 5, 4, 3, 1, 0 });
    }

    @Test
    public void testStable() throws Exception {
        int[] data = new int[]{ 1, 2, 3, 4, 5, 6, 7 };
        testStable(data);
    }
}


package org.tron.common;


import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.testng.collections.Lists;


@Slf4j
public class ComparatorTest {
    @Test
    public void intComparator() {
        List<Integer> list1 = Lists.newArrayList(ImmutableList.of(1, 8, 4, 6, 2));
        List<Integer> list2 = Lists.newArrayList(ImmutableList.of(1, 8, 4, 6, 2));
        list1.sort(( o1, o2) -> o1 - o2);
        list2.sort(Comparator.comparingInt(( o) -> o));
        logger.info(("list1:" + list1));
        logger.info(("list2:" + list2));
        Assert.assertEquals(list1, list2);
        List<Integer> list3 = Lists.newArrayList(ImmutableList.of(1, 8, 4, 6, 2));
        List<Integer> list4 = Lists.newArrayList(ImmutableList.of(1, 8, 4, 6, 2));
        List<Integer> list5 = Lists.newArrayList(ImmutableList.of(1, 8, 4, 6, 2));
        list3.sort(( o1, o2) -> o2 - o1);
        list4.sort(Comparator.comparingInt((Integer o) -> o).reversed());
        list5.sort(( o1, o2) -> -(o1 - o2));
        logger.info(("list3:" + list3));
        logger.info(("list4:" + list4));
        logger.info(("list5:" + list5));
        Assert.assertEquals(list3, list4);
        Assert.assertEquals(list3, list5);
    }

    @Test
    public void thenComparing() {
        ComparatorTest.C c1 = new ComparatorTest.C(1, new ComparatorTest.B(20));
        ComparatorTest.C c2 = new ComparatorTest.C(2, new ComparatorTest.B(40));
        ComparatorTest.C c3 = new ComparatorTest.C(3, new ComparatorTest.B(60));
        ComparatorTest.C c4 = new ComparatorTest.C(4, new ComparatorTest.B(10));
        ComparatorTest.C c5 = new ComparatorTest.C(5, new ComparatorTest.B(30));
        ComparatorTest.C c6 = new ComparatorTest.C(5, new ComparatorTest.B(0));
        List<ComparatorTest.C> cs1 = Lists.newArrayList(c4, c5, c1, c3, c2, c6);
        List<ComparatorTest.C> cs2 = Lists.newArrayList(c4, c5, c1, c3, c2, c6);
        logger.info(("cs1:" + cs1));
        logger.info(("cs2:" + cs2));
        cs1.sort(( a, b) -> {
            int age1 = getAge();
            int age2 = getAge();
            if (age1 != age2) {
                return age2 - age1;
            } else {
                return Long.compare(getB().getHigh(), getB().getHigh());
            }
        });
        cs2.sort(Comparator.comparingInt((ComparatorTest.C c) -> c.getAge()).reversed().thenComparing(Comparator.comparingInt((ComparatorTest.C c) -> c.getB().getHigh()).reversed()));
        logger.info(("cs1:" + cs1));
        logger.info(("cs2:" + cs2));
        Assert.assertEquals(cs1, cs2);
    }

    @Data
    @AllArgsConstructor
    static class B {
        int high;
    }

    @Data
    @AllArgsConstructor
    static class C {
        int age;

        ComparatorTest.B b;
    }
}


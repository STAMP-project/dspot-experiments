package org.gridkit.jvmtool.event;


import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class MergeExcludeIteratorTest {
    Iterable<String> NO_STR = seq(new String[0]);

    Iterable<Integer> NO_INT = seq(new Integer[0]);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Comparator<String> SCMP = ((Comparator) (new MergeExcludeIteratorTest.ReverseComparator()));

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Comparator<Integer> ICMP = ((Comparator) (new MergeExcludeIteratorTest.ReverseComparator()));

    @Test
    public void simple_string_merge() {
        Assertions.assertThat(MergeIterator.merge(seq("A", "B", "C"), NO_STR)).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(NO_STR, seq("A", "B", "C"))).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(NO_STR, NO_STR)).containsExactly();
        Assertions.assertThat(MergeIterator.merge(seq("A"), seq("B"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("B"), seq("A"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B"), seq("A"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B"), seq("A", "B"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("B"), seq("A", "B"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B"), seq("B"))).containsExactly("A", "B");
        Assertions.assertThat(MergeIterator.merge(seq("A"), seq("B", "C"))).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(seq("B"), seq("A", "C"))).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B"), seq("B", "C"))).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B", "C"), seq("B", "C"))).containsExactly("A", "B", "C");
        Assertions.assertThat(MergeIterator.merge(seq("A", "B", "C"), seq("A", "B", "C"))).containsExactly("A", "B", "C");
    }

    @Test
    public void simple_int_merge() {
        Assertions.assertThat(MergeIterator.merge(seq(1, 2, 3), NO_INT)).containsExactly(1, 2, 3);
        Assertions.assertThat(MergeIterator.merge(NO_INT, seq(1, 2, 3))).containsExactly(1, 2, 3);
        Assertions.assertThat(MergeIterator.merge(NO_INT, NO_INT)).containsExactly();
        Assertions.assertThat(MergeIterator.merge(seq(1), seq(2))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(2), seq(1))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2), seq(1))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2), seq(1, 2))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(2), seq(1, 2))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2), seq(2))).containsExactly(1, 2);
        Assertions.assertThat(MergeIterator.merge(seq(1), seq(2, 3))).containsExactly(1, 2, 3);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2), seq(2, 3))).containsExactly(1, 2, 3);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2, 3), seq(2, 3))).containsExactly(1, 2, 3);
        Assertions.assertThat(MergeIterator.merge(seq(1, 2, 3), seq(1, 2, 3))).containsExactly(1, 2, 3);
    }

    @Test
    public void simple_int_merge_with_comparator() {
        Assertions.assertThat(MergeIterator.merge(seq(3, 2, 1), NO_INT, ICMP)).containsExactly(3, 2, 1);
        Assertions.assertThat(MergeIterator.merge(NO_INT, seq(3, 2, 1), ICMP)).containsExactly(3, 2, 1);
        Assertions.assertThat(MergeIterator.merge(NO_INT, NO_INT, ICMP)).containsExactly();
        Assertions.assertThat(MergeIterator.merge(seq(1), seq(2), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2), seq(1), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2, 1), seq(1), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2, 1), seq(2, 1), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2), seq(2, 1), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2, 1), seq(2), ICMP)).containsExactly(2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(1), seq(3, 2), ICMP)).containsExactly(3, 2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(2, 1), seq(3, 2), ICMP)).containsExactly(3, 2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(3, 2, 1), seq(3, 2), ICMP)).containsExactly(3, 2, 1);
        Assertions.assertThat(MergeIterator.merge(seq(3, 2, 1), seq(3, 2, 1), ICMP)).containsExactly(3, 2, 1);
    }

    @Test
    public void simple_string_merge_with_comparator() {
        Assertions.assertThat(MergeIterator.merge(seq("C", "B", "A"), NO_STR, SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(MergeIterator.merge(NO_STR, seq("C", "B", "A"), SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(MergeIterator.merge(NO_STR, NO_STR, SCMP)).containsExactly();
        Assertions.assertThat(MergeIterator.merge(seq("A"), seq("B"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B"), seq("A"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B", "A"), seq("A"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B", "A"), seq("B", "A"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B"), seq("B", "A"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B", "A"), seq("B"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("A"), seq("C", "B"), SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("B", "A"), seq("C", "B"), SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("C", "B", "A"), seq("C", "B"), SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(MergeIterator.merge(seq("C", "B", "A"), seq("C", "B", "A"), SCMP)).containsExactly("C", "B", "A");
    }

    @Test
    public void simple_string_exclude() {
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), NO_STR)).containsExactly("A", "B", "C");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("D"))).containsExactly("A", "B", "C");
        Assertions.assertThat(ExcludeIterator.exclude(NO_STR, seq("A", "B", "C"))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(NO_STR, NO_STR)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("A"), seq("B"))).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("A"))).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B"), seq("A"))).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B"), seq("A", "B"))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("A", "B"))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B"), seq("B"))).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A"), seq("B", "C"))).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("A", "C"))).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B"), seq("B", "C"))).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("B", "C"))).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("A"))).containsExactly("B", "C");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("B"))).containsExactly("A", "C");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("C"))).containsExactly("A", "B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A", "B", "C"), seq("A", "C"))).containsExactly("B");
    }

    @Test
    public void simple_int_exclude() {
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), NO_INT)).containsExactly(1, 2, 3);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(4))).containsExactly(1, 2, 3);
        Assertions.assertThat(ExcludeIterator.exclude(NO_INT, seq(1, 2, 3))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(NO_INT, NO_INT)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq(1), seq(2))).containsExactly(1);
        Assertions.assertThat(ExcludeIterator.exclude(seq(2), seq(1))).containsExactly(2);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2), seq(1))).containsExactly(2);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2), seq(1, 2))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq(2), seq(1, 2))).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2), seq(2))).containsExactly(1);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1), seq(2, 3))).containsExactly(1);
        Assertions.assertThat(ExcludeIterator.exclude(seq(2), seq(1, 3))).containsExactly(2);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2), seq(2, 3))).containsExactly(1);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(2, 3))).containsExactly(1);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(1))).containsExactly(2, 3);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(2))).containsExactly(1, 3);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(3))).containsExactly(1, 2);
        Assertions.assertThat(ExcludeIterator.exclude(seq(1, 2, 3), seq(1, 3))).containsExactly(2);
    }

    @Test
    public void simple_string_exclude_with_comparator() {
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), NO_STR, SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("D"), SCMP)).containsExactly("C", "B", "A");
        Assertions.assertThat(ExcludeIterator.exclude(NO_STR, seq("C", "B", "A"), SCMP)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(NO_STR, NO_STR, SCMP)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("A"), seq("B"), SCMP)).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("A"), SCMP)).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B", "A"), seq("A"), SCMP)).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B", "A"), seq("B", "A"), SCMP)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("B", "A"), SCMP)).containsExactly();
        Assertions.assertThat(ExcludeIterator.exclude(seq("B", "A"), seq("B"), SCMP)).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("A"), seq("C", "B"), SCMP)).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B"), seq("C", "A"), SCMP)).containsExactly("B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("B", "A"), seq("C", "B"), SCMP)).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("C", "B"), SCMP)).containsExactly("A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("A"), SCMP)).containsExactly("C", "B");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("B"), SCMP)).containsExactly("C", "A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("C"), SCMP)).containsExactly("B", "A");
        Assertions.assertThat(ExcludeIterator.exclude(seq("C", "B", "A"), seq("C", "A"), SCMP)).containsExactly("B");
    }

    public static class ReverseComparator implements Comparator<Comparable<Object>> {
        @Override
        public int compare(Comparable<Object> o1, Comparable<Object> o2) {
            return o2.compareTo(o1);
        }
    }
}


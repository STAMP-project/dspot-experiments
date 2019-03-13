package com.annimon.stream;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ComparatorCompatTest {
    @Test
    public void testNaturalOrder() {
        int[] expected = new int[]{ 1, 2, 3, 5, 8, 13 };
        IntStream stream = IntStream.of(1, 2, 3, 5, 8, 13).sorted(ComparatorCompat.<Integer>naturalOrder());
        Assert.assertThat(stream.toArray(), Matchers.is(expected));
    }

    @Test
    public void testReverseOrder() {
        int[] expected = new int[]{ 13, 8, 5, 3, 2, 1 };
        IntStream stream = IntStream.of(1, 2, 3, 5, 8, 13).sorted(ComparatorCompat.<Integer>reverseOrder());
        Assert.assertThat(stream.toArray(), Matchers.is(expected));
    }

    @Test
    public void testReversedComparator() {
        int[] expected = new int[]{ 1, -2, 4, -8, 16 };
        IntStream stream = IntStream.of((-8), (-2), 1, 4, 16).sorted(ComparatorCompat.reversed(Functions.descendingAbsoluteOrder()));
        Assert.assertThat(stream.toArray(), Matchers.is(expected));
    }

    @Test
    public void testThenComparing() {
        int[] expected = new int[]{ 16, -16, 4, -4, -2, 1 };
        IntStream stream = IntStream.of((-16), (-4), (-2), 1, 4, 16).sorted(ComparatorCompat.thenComparing(Functions.descendingAbsoluteOrder(), ComparatorCompat.<Integer>reverseOrder()));
        Assert.assertThat(stream.toArray(), Matchers.is(expected));
    }

    @Test
    public void testComparing() {
        Stream.of("abc", "ab", "abcd", "a").sorted(ComparatorCompat.comparing(new com.annimon.stream.function.Function<String, Integer>() {
            @Override
            public Integer apply(String str) {
                return str.length();
            }
        }, ComparatorCompat.<Integer>reverseOrder())).custom(assertElements(Matchers.contains("abcd", "abc", "ab", "a")));
    }

    @Test
    public void testComparingComparable() {
        Stream.of("abc", "ab", "abcd", "a").sorted(ComparatorCompat.comparing(new com.annimon.stream.function.Function<String, Integer>() {
            @Override
            public Integer apply(String str) {
                return str.length();
            }
        })).custom(assertElements(Matchers.contains("a", "ab", "abc", "abcd")));
    }

    @Test
    public void testComparingInt() {
        Stream.of("abc", "ab", "abcd", "a").sorted(ComparatorCompat.comparingInt(new com.annimon.stream.function.ToIntFunction<String>() {
            @Override
            public int applyAsInt(String str) {
                return str.length();
            }
        })).custom(assertElements(Matchers.contains("a", "ab", "abc", "abcd")));
    }

    @Test
    public void testComparingLong() {
        Stream.of("abc", "ab", "abcd", "a").sorted(ComparatorCompat.comparingLong(new com.annimon.stream.function.ToLongFunction<String>() {
            @Override
            public long applyAsLong(String str) {
                return (str.length()) * 10000000L;
            }
        })).custom(assertElements(Matchers.contains("a", "ab", "abc", "abcd")));
    }

    @Test
    public void testComparingDouble() {
        Stream.of("abc", "ab", "abcd", "a").sorted(ComparatorCompat.comparingDouble(new com.annimon.stream.function.ToDoubleFunction<String>() {
            @Override
            public double applyAsDouble(String str) {
                return (str.length()) / 0.01;
            }
        })).custom(assertElements(Matchers.contains("a", "ab", "abc", "abcd")));
    }

    @Test
    public void testNullsFirst() {
        Stream.of("abc", "ab", null, "abcd", null, "a").sorted(ComparatorCompat.nullsFirst()).custom(assertElements(Matchers.contains(null, null, "abc", "ab", "abcd", "a")));
    }

    @Test
    public void testNullsFirstComparator() {
        Stream.of("abc", "ab", null, "abcd", null, "a").sorted(ComparatorCompat.nullsFirst(String.CASE_INSENSITIVE_ORDER)).custom(assertElements(Matchers.contains(null, null, "a", "ab", "abc", "abcd")));
    }

    @Test
    public void testNullsLast() {
        Stream.of("abc", "ab", null, "abcd", null, "a").sorted(ComparatorCompat.nullsLast()).custom(assertElements(Matchers.contains("abc", "ab", "abcd", "a", null, null)));
    }

    @Test
    public void testNullsLastComparator() {
        Stream.of("abc", "ab", null, "abcd", null, "a").sorted(ComparatorCompat.nullsLast(String.CASE_INSENSITIVE_ORDER)).custom(assertElements(Matchers.contains("a", "ab", "abc", "abcd", null, null)));
    }

    @Test
    public void testChain_CourseReversed() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.course).reversed();
        List<Student> input = Arrays.asList(Students.MARIA_CS_1, Students.STEVE_CS_4, Students.VICTORIA_CS_3);
        Stream.of(input).sorted(comparator).custom(assertElements(Matchers.contains(Students.STEVE_CS_4, Students.VICTORIA_CS_3, Students.MARIA_CS_1)));
    }

    @Test
    public void testChain_CourseThenName() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.course).thenComparing(Students.studentName);
        List<Student> input = Arrays.asList(Students.MARIA_CS_1, Students.STEVE_CS_4, Students.VICTORIA_CS_3, Students.SERGEY_LAW_1);
        Stream.of(input).sorted(comparator).custom(assertElements(Matchers.contains(Students.MARIA_CS_1, Students.SERGEY_LAW_1, Students.VICTORIA_CS_3, Students.STEVE_CS_4)));
    }

    @Test
    public void testChain_SpecialityThenCourseThenName() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.speciality).thenComparingInt(new com.annimon.stream.function.ToIntFunction<Student>() {
            @Override
            public int applyAsInt(Student student) {
                return student.getCourse();
            }
        }).thenComparing(Students.studentName);
        List<Student> input = Arrays.asList(Students.STEVE_CS_4, Students.SERGEY_LAW_1, Students.MARIA_CS_1, Students.SOPHIA_ECONOMICS_2, Students.GEORGE_LAW_3, Students.VICTORIA_CS_3);
        Stream.of(input).sorted(comparator).custom(assertElements(Matchers.contains(Students.MARIA_CS_1, Students.VICTORIA_CS_3, Students.STEVE_CS_4, Students.SOPHIA_ECONOMICS_2, Students.SERGEY_LAW_1, Students.GEORGE_LAW_3)));
    }

    @Test
    public void testChain_NameReversedThenCourseThenSpeciality() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.studentName).reversed().thenComparing(Students.course).thenComparing(Students.speciality);
        testStudentComparator(comparator);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testChain_NameReversedThenCourseThenSpeciality_Deprecation() {
        Comparator<Student> comparator = ComparatorCompat.chain(ComparatorCompat.comparing(Students.studentName)).reversed().thenComparing(Students.course).thenComparing(Students.speciality).comparator();
        testStudentComparator(comparator);
    }

    @Test
    public void testChain_NameReversedThenCourseThenSpecialityDoubleReversed() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.studentName).thenComparing(ComparatorCompat.<Student>reverseOrder()).thenComparingLong(new com.annimon.stream.function.ToLongFunction<Student>() {
            @Override
            public long applyAsLong(Student student) {
                return (student.getCourse()) * 100000L;
            }
        }).thenComparing(Students.speciality, ComparatorCompat.<String>reverseOrder()).reversed();
        testStudentComparator(comparator);
    }

    @Test
    public void testChain_NameReversedThenCourseThenSpeciality2() {
        Comparator<Student> comparator = ComparatorCompat.comparing(Students.studentName).reversed().thenComparingDouble(new com.annimon.stream.function.ToDoubleFunction<Student>() {
            @Override
            public double applyAsDouble(Student student) {
                return (student.getCourse()) / 0.001;
            }
        }).thenComparing(Students.speciality);
        testStudentComparator(comparator);
    }
}


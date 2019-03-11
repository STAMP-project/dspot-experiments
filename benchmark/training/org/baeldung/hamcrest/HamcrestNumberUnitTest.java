package org.baeldung.hamcrest;


import java.math.BigDecimal;
import java.time.LocalDate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HamcrestNumberUnitTest {
    @Test
    public void givenADouble_whenCloseTo_thenCorrect() {
        double actual = 1.3;
        double operand = 1;
        double error = 0.5;
        MatcherAssert.assertThat(actual, Matchers.is(Matchers.closeTo(operand, error)));
    }

    @Test
    public void givenADouble_whenNotCloseTo_thenCorrect() {
        double actual = 1.6;
        double operand = 1;
        double error = 0.5;
        MatcherAssert.assertThat(actual, Matchers.is(Matchers.not(Matchers.closeTo(operand, error))));
    }

    @Test
    public void givenABigDecimal_whenCloseTo_thenCorrect() {
        BigDecimal actual = new BigDecimal("1.0003");
        BigDecimal operand = new BigDecimal("1");
        BigDecimal error = new BigDecimal("0.0005");
        MatcherAssert.assertThat(actual, Matchers.is(Matchers.closeTo(operand, error)));
    }

    @Test
    public void givenABigDecimal_whenNotCloseTo_thenCorrect() {
        BigDecimal actual = new BigDecimal("1.0006");
        BigDecimal operand = new BigDecimal("1");
        BigDecimal error = new BigDecimal("0.0005");
        MatcherAssert.assertThat(actual, Matchers.is(Matchers.not(Matchers.closeTo(operand, error))));
    }

    @Test
    public void given5_whenComparesEqualTo5_thenCorrect() {
        Integer five = 5;
        MatcherAssert.assertThat(five, Matchers.comparesEqualTo(five));
    }

    @Test
    public void given5_whenNotComparesEqualTo7_thenCorrect() {
        Integer seven = 7;
        Integer five = 5;
        MatcherAssert.assertThat(five, Matchers.not(Matchers.comparesEqualTo(seven)));
    }

    @Test
    public void given7_whenGreaterThan5_thenCorrect() {
        Integer seven = 7;
        Integer five = 5;
        MatcherAssert.assertThat(seven, Matchers.is(Matchers.greaterThan(five)));
    }

    @Test
    public void given7_whenGreaterThanOrEqualTo5_thenCorrect() {
        Integer seven = 7;
        Integer five = 5;
        MatcherAssert.assertThat(seven, Matchers.is(Matchers.greaterThanOrEqualTo(five)));
    }

    @Test
    public void given5_whenGreaterThanOrEqualTo5_thenCorrect() {
        Integer five = 5;
        MatcherAssert.assertThat(five, Matchers.is(Matchers.greaterThanOrEqualTo(five)));
    }

    @Test
    public void given3_whenLessThan5_thenCorrect() {
        Integer three = 3;
        Integer five = 5;
        MatcherAssert.assertThat(three, Matchers.is(Matchers.lessThan(five)));
    }

    @Test
    public void given3_whenLessThanOrEqualTo5_thenCorrect() {
        Integer three = 3;
        Integer five = 5;
        MatcherAssert.assertThat(three, Matchers.is(Matchers.lessThanOrEqualTo(five)));
    }

    @Test
    public void given5_whenLessThanOrEqualTo5_thenCorrect() {
        Integer five = 5;
        MatcherAssert.assertThat(five, Matchers.is(Matchers.lessThanOrEqualTo(five)));
    }

    @Test
    public void givenBenjamin_whenGreaterThanAmanda_thenCorrect() {
        String amanda = "Amanda";
        String benjamin = "Benjamin";
        MatcherAssert.assertThat(benjamin, Matchers.is(Matchers.greaterThan(amanda)));
    }

    @Test
    public void givenAmanda_whenLessThanBenajmin_thenCorrect() {
        String amanda = "Amanda";
        String benjamin = "Benjamin";
        MatcherAssert.assertThat(amanda, Matchers.is(Matchers.lessThan(benjamin)));
    }

    @Test
    public void givenToday_whenGreaterThanYesterday_thenCorrect() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        MatcherAssert.assertThat(today, Matchers.is(Matchers.greaterThan(yesterday)));
    }

    @Test
    public void givenToday_whenLessThanTomorrow_thenCorrect() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        MatcherAssert.assertThat(today, Matchers.is(Matchers.lessThan(tomorrow)));
    }

    @Test
    public void givenAmanda_whenOlderThanBenjamin_thenCorrect() {
        HamcrestNumberUnitTest.Person amanda = new HamcrestNumberUnitTest.Person("Amanda", 20);
        HamcrestNumberUnitTest.Person benjamin = new HamcrestNumberUnitTest.Person("Benjamin", 18);
        MatcherAssert.assertThat(amanda, Matchers.is(Matchers.greaterThan(benjamin)));
    }

    @Test
    public void givenBenjamin_whenYoungerThanAmanda_thenCorrect() {
        HamcrestNumberUnitTest.Person amanda = new HamcrestNumberUnitTest.Person("Amanda", 20);
        HamcrestNumberUnitTest.Person benjamin = new HamcrestNumberUnitTest.Person("Benjamin", 18);
        MatcherAssert.assertThat(benjamin, Matchers.is(Matchers.lessThan(amanda)));
    }

    class Person implements Comparable<HamcrestNumberUnitTest.Person> {
        String name;

        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public int compareTo(HamcrestNumberUnitTest.Person o) {
            if ((this.age) == (o.getAge()))
                return 0;

            if ((this.age) > (o.age))
                return 1;
            else
                return -1;

        }
    }

    @Test
    public void givenNaN_whenIsNotANumber_thenCorrect() {
        double zero = 0.0;
        MatcherAssert.assertThat((zero / zero), Matchers.is(Matchers.notANumber()));
    }
}


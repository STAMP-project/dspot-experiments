package org.baeldung.guava;


import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GuavaOrderingUnitTest {
    @Test
    public void givenListOfIntegers_whenCreateNaturalOrderOrdering_shouldSortProperly() {
        // given
        List<Integer> integers = Arrays.asList(3, 2, 1);
        // when
        integers.sort(Ordering.natural());
        // then
        Assert.assertEquals(Arrays.asList(1, 2, 3), integers);
    }

    @Test
    public void givenListOfPersonObject_whenSortedUsingCustomOrdering_shouldSortProperly() {
        // given
        List<GuavaOrderingUnitTest.Person> persons = Arrays.asList(new GuavaOrderingUnitTest.Person("Michael", 10), new GuavaOrderingUnitTest.Person("Alice", 3));
        Ordering<GuavaOrderingUnitTest.Person> orderingByAge = new Ordering<GuavaOrderingUnitTest.Person>() {
            @Override
            public int compare(GuavaOrderingUnitTest.Person p1, GuavaOrderingUnitTest.Person p2) {
                return Ints.compare(p1.age, p2.age);
            }
        };
        // when
        persons.sort(orderingByAge);
        // then
        Assert.assertEquals(Arrays.asList(new GuavaOrderingUnitTest.Person("Alice", 3), new GuavaOrderingUnitTest.Person("Michael", 10)), persons);
    }

    @Test
    public void givenListOfPersonObject_whenSortedUsingChainedOrdering_shouldSortPropely() {
        // given
        List<GuavaOrderingUnitTest.Person> persons = Arrays.asList(new GuavaOrderingUnitTest.Person("Michael", 10), new GuavaOrderingUnitTest.Person("Alice", 3), new GuavaOrderingUnitTest.Person("Thomas", null));
        Ordering<GuavaOrderingUnitTest.Person> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<GuavaOrderingUnitTest.Person, Comparable>() {
            @Override
            public Comparable apply(GuavaOrderingUnitTest.Person person) {
                return person.age;
            }
        });
        // when
        persons.sort(ordering);
        // then
        Assert.assertEquals(Arrays.asList(new GuavaOrderingUnitTest.Person("Thomas", null), new GuavaOrderingUnitTest.Person("Alice", 3), new GuavaOrderingUnitTest.Person("Michael", 10)), persons);
    }

    class Person {
        private final String name;

        private final Integer age;

        private Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            GuavaOrderingUnitTest.Person person = ((GuavaOrderingUnitTest.Person) (o));
            if ((name) != null ? !(name.equals(person.name)) : (person.name) != null)
                return false;

            return (age) != null ? age.equals(person.age) : (person.age) == null;
        }

        @Override
        public int hashCode() {
            int result = ((name) != null) ? name.hashCode() : 0;
            result = (31 * result) + ((age) != null ? age.hashCode() : 0);
            return result;
        }
    }
}


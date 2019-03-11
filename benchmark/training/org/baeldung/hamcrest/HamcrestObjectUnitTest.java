package org.baeldung.hamcrest;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HamcrestObjectUnitTest {
    @Test
    public void givenACity_whenHasToString_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.hasToString("[Name: San Francisco, State: CA]"));
    }

    @Test
    public void givenACity_whenHasToStringEqualToIgnoringCase_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.hasToString(Matchers.equalToIgnoringCase("[NAME: SAN FRANCISCO, STATE: CA]")));
    }

    @Test
    public void givenACity_whenHasToStringEmptyOrNullString_thenCorrect() {
        City city = new City(null, null);
        MatcherAssert.assertThat(city, Matchers.hasToString(Matchers.emptyOrNullString()));
    }

    @Test
    public void givenACity_whenTypeCompatibleWithLocation_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city.getClass(), Matchers.is(Matchers.typeCompatibleWith(Location.class)));
    }

    @Test
    public void givenACity_whenTypeNotCompatibleWithString_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city.getClass(), Matchers.is(Matchers.not(Matchers.typeCompatibleWith(String.class))));
    }

    @Test
    public void givenACity_whenTypeCompatibleWithObject_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city.getClass(), Matchers.is(Matchers.typeCompatibleWith(Object.class)));
    }
}


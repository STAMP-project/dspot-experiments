package org.baeldung.hamcrest;


import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HamcrestBeansUnitTest {
    @Test
    public void givenACity_whenHasProperty_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.hasProperty("name"));
    }

    @Test
    public void givenACity_whenNotHasProperty_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.not(Matchers.hasProperty("country")));
    }

    @Test
    public void givenACity_whenHasPropertyWithValueEqualTo_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.hasProperty("name", Matchers.equalTo("San Francisco")));
    }

    @Test
    public void givenACity_whenHasPropertyWithValueEqualToIgnoringCase_thenCorrect() {
        City city = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.hasProperty("state", Matchers.equalToIgnoringCase("ca")));
    }

    @Test
    public void givenACity_whenSamePropertyValuesAs_thenCorrect() {
        City city = new City("San Francisco", "CA");
        City city2 = new City("San Francisco", "CA");
        MatcherAssert.assertThat(city, Matchers.samePropertyValuesAs(city2));
    }

    @Test
    public void givenACity_whenNotSamePropertyValuesAs_thenCorrect() {
        City city = new City("San Francisco", "CA");
        City city2 = new City("Los Angeles", "CA");
        MatcherAssert.assertThat(city, Matchers.not(Matchers.samePropertyValuesAs(city2)));
    }

    @Test
    public void givenACity_whenGetPropertyDescriptor_thenCorrect() {
        City city = new City("San Francisco", "CA");
        PropertyDescriptor descriptor = getPropertyDescriptor("state", city);
        MatcherAssert.assertThat(descriptor.getReadMethod().getName(), Matchers.is(Matchers.equalTo("getState")));
    }

    @Test
    public void givenACity_whenGetPropertyDescriptorsFor_thenCorrect() {
        City city = new City("San Francisco", "CA");
        PropertyDescriptor[] descriptors = propertyDescriptorsFor(city, Object.class);
        List<String> getters = Arrays.stream(descriptors).map(( x) -> x.getReadMethod().getName()).collect(Collectors.toList());
        MatcherAssert.assertThat(getters, Matchers.containsInAnyOrder("getName", "getState"));
    }
}


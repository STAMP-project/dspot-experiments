package com.baeldung.jackson.inheritance;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IgnoranceUnitTest {
    private abstract static class CarMixIn {
        @JsonIgnore
        public String make;

        @JsonIgnore
        public String topSpeed;
    }

    private static class IgnoranceIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1422295680188892323L;

        public boolean hasIgnoreMarker(AnnotatedMember m) {
            return (((((m.getDeclaringClass()) == (IgnoranceMixinOrIntrospection.Vehicle.class)) && ((m.getName()) == "model")) || ((m.getDeclaringClass()) == (IgnoranceMixinOrIntrospection.Car.class))) || ((m.getName()) == "towingCapacity")) || (super.hasIgnoreMarker(m));
        }
    }

    @Test
    public void givenAnnotations_whenIgnoringProperties_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        IgnoranceAnnotationStructure.Sedan sedan = new IgnoranceAnnotationStructure.Sedan("Mercedes-Benz", "S500", 5, 250.0);
        IgnoranceAnnotationStructure.Crossover crossover = new IgnoranceAnnotationStructure.Crossover("BMW", "X6", 5, 250.0, 6000.0);
        List<IgnoranceAnnotationStructure.Vehicle> vehicles = new ArrayList<>();
        vehicles.add(sedan);
        vehicles.add(crossover);
        String jsonDataString = mapper.writeValueAsString(vehicles);
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("make"));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("model")));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("seatingCapacity")));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("topSpeed")));
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("towingCapacity"));
    }

    @Test
    public void givenMixIns_whenIgnoringProperties_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(IgnoranceMixinOrIntrospection.Car.class, IgnoranceUnitTest.CarMixIn.class);
        String jsonDataString = instantiateAndSerializeObjects(mapper);
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("make")));
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("model"));
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("seatingCapacity"));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("topSpeed")));
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("towingCapacity"));
    }

    @Test
    public void givenIntrospection_whenIgnoringProperties_thenCorrect() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new IgnoranceUnitTest.IgnoranceIntrospector());
        String jsonDataString = instantiateAndSerializeObjects(mapper);
        Assert.assertThat(jsonDataString, CoreMatchers.containsString("make"));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("model")));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("seatingCapacity")));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("topSpeed")));
        Assert.assertThat(jsonDataString, CoreMatchers.not(CoreMatchers.containsString("towingCapacity")));
    }
}


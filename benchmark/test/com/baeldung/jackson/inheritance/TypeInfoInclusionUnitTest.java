package com.baeldung.jackson.inheritance;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TypeInfoInclusionUnitTest {
    @Test
    public void givenTypeInfo_whenAnnotatingGlobally_thenTypesAreCorrectlyRecovered() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        TypeInfoStructure.Car car = new TypeInfoStructure.Car("Mercedes-Benz", "S500", 5, 250.0);
        TypeInfoStructure.Truck truck = new TypeInfoStructure.Truck("Isuzu", "NQR", 7500.0);
        List<TypeInfoStructure.Vehicle> vehicles = new ArrayList<>();
        vehicles.add(car);
        vehicles.add(truck);
        TypeInfoStructure.Fleet serializedFleet = new TypeInfoStructure.Fleet();
        serializedFleet.setVehicles(vehicles);
        String jsonDataString = mapper.writeValueAsString(serializedFleet);
        TypeInfoStructure.Fleet deserializedFleet = mapper.readValue(jsonDataString, TypeInfoStructure.Fleet.class);
        Assert.assertThat(deserializedFleet.getVehicles().get(0), CoreMatchers.instanceOf(TypeInfoStructure.Car.class));
        Assert.assertThat(deserializedFleet.getVehicles().get(1), CoreMatchers.instanceOf(TypeInfoStructure.Truck.class));
    }

    @Test
    public void givenTypeInfo_whenAnnotatingPerClass_thenTypesAreCorrectlyRecovered() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeInfoAnnotatedStructure.Car car = new TypeInfoAnnotatedStructure.Car("Mercedes-Benz", "S500", 5, 250.0);
        TypeInfoAnnotatedStructure.Truck truck = new TypeInfoAnnotatedStructure.Truck("Isuzu", "NQR", 7500.0);
        List<TypeInfoAnnotatedStructure.Vehicle> vehicles = new ArrayList<>();
        vehicles.add(car);
        vehicles.add(truck);
        TypeInfoAnnotatedStructure.Fleet serializedFleet = new TypeInfoAnnotatedStructure.Fleet();
        serializedFleet.setVehicles(vehicles);
        String jsonDataString = mapper.writeValueAsString(serializedFleet);
        TypeInfoAnnotatedStructure.Fleet deserializedFleet = mapper.readValue(jsonDataString, TypeInfoAnnotatedStructure.Fleet.class);
        Assert.assertThat(deserializedFleet.getVehicles().get(0), CoreMatchers.instanceOf(TypeInfoAnnotatedStructure.Car.class));
        Assert.assertThat(deserializedFleet.getVehicles().get(1), CoreMatchers.instanceOf(TypeInfoAnnotatedStructure.Truck.class));
    }
}


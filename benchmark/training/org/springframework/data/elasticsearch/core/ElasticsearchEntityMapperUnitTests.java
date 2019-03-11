/**
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.entities.Car;
import org.springframework.data.elasticsearch.entities.GeoEntity;
import org.springframework.data.geo.Point;


/**
 *
 *
 * @author Christoph Strobl
 */
public class ElasticsearchEntityMapperUnitTests {
    static final String JSON_STRING = "{\"_class\":\"org.springframework.data.elasticsearch.entities.Car\",\"name\":\"Grat\",\"model\":\"Ford\"}";

    static final String CAR_MODEL = "Ford";

    static final String CAR_NAME = "Grat";

    ElasticsearchEntityMapper entityMapper;

    ElasticsearchEntityMapperUnitTests.Person sarahConnor;

    ElasticsearchEntityMapperUnitTests.Person kyleReese;

    ElasticsearchEntityMapperUnitTests.Person t800;

    ElasticsearchEntityMapperUnitTests.Inventory gun = new ElasticsearchEntityMapperUnitTests.Gun("Glock 19", 33);

    ElasticsearchEntityMapperUnitTests.Inventory grenade = new ElasticsearchEntityMapperUnitTests.Grenade("40 mm");

    ElasticsearchEntityMapperUnitTests.Inventory rifle = new ElasticsearchEntityMapperUnitTests.Rifle("AR-18 Assault Rifle", 3.17, 40);

    ElasticsearchEntityMapperUnitTests.Inventory shotGun = new ElasticsearchEntityMapperUnitTests.ShotGun("Ithaca 37 Pump Shotgun");

    ElasticsearchEntityMapperUnitTests.Address observatoryRoad;

    ElasticsearchEntityMapperUnitTests.Place bigBunsCafe;

    Map<String, Object> sarahAsMap;

    Map<String, Object> t800AsMap;

    Map<String, Object> kyleAsMap;

    Map<String, Object> gratiotAveAsMap;

    Map<String, Object> locationAsMap;

    Map<String, Object> gunAsMap;

    Map<String, Object> grenadeAsMap;

    Map<String, Object> rifleAsMap;

    Map<String, Object> shotGunAsMap;

    Map<String, Object> bigBunsCafeAsMap;

    // DATAES-530
    @Test
    public void shouldMapObjectToJsonString() throws IOException {
        // Given
        // When
        String jsonResult = entityMapper.mapToString(builder().model(ElasticsearchEntityMapperUnitTests.CAR_MODEL).name(ElasticsearchEntityMapperUnitTests.CAR_NAME).build());
        // Then
        assertThat(jsonResult).isEqualTo(ElasticsearchEntityMapperUnitTests.JSON_STRING);
    }

    // DATAES-530
    @Test
    public void shouldMapJsonStringToObject() throws IOException {
        // Given
        // When
        Car result = entityMapper.mapToObject(ElasticsearchEntityMapperUnitTests.JSON_STRING, Car.class);
        // Then
        assertThat(result.getName()).isEqualTo(ElasticsearchEntityMapperUnitTests.CAR_NAME);
        assertThat(result.getModel()).isEqualTo(ElasticsearchEntityMapperUnitTests.CAR_MODEL);
    }

    // DATAES-530
    @Test
    public void shouldMapGeoPointElasticsearchNames() throws IOException {
        // given
        final Point point = new Point(10, 20);
        final String pointAsString = ((point.getX()) + ",") + (point.getY());
        final double[] pointAsArray = new double[]{ point.getX(), point.getY() };
        final GeoEntity geoEntity = builder().pointA(point).pointB(GeoPoint.fromPoint(point)).pointC(pointAsString).pointD(pointAsArray).build();
        // when
        String jsonResult = entityMapper.mapToString(geoEntity);
        // then
        assertThat(jsonResult).contains(pointTemplate("pointA", point));
        assertThat(jsonResult).contains(pointTemplate("pointB", point));
        assertThat(jsonResult).contains(String.format(Locale.ENGLISH, "\"%s\":\"%s\"", "pointC", pointAsString));
        assertThat(jsonResult).contains(String.format(Locale.ENGLISH, "\"%s\":[%.1f,%.1f]", "pointD", pointAsArray[0], pointAsArray[1]));
    }

    // DATAES-530
    @Test
    public void ignoresReadOnlyProperties() throws IOException {
        // given
        ElasticsearchEntityMapperUnitTests.Sample sample = new ElasticsearchEntityMapperUnitTests.Sample();
        sample.readOnly = "readOnly";
        sample.property = "property";
        sample.javaTransientProperty = "javaTransient";
        sample.annotatedTransientProperty = "transient";
        // when
        String result = entityMapper.mapToString(sample);
        // then
        assertThat(result).contains("\"property\"");
        assertThat(result).contains("\"javaTransient\"");
        assertThat(result).doesNotContain("readOnly");
        assertThat(result).doesNotContain("annotatedTransientProperty");
    }

    // DATAES-530
    @Test
    public void writesNestedEntity() {
        ElasticsearchEntityMapperUnitTests.Person person = new ElasticsearchEntityMapperUnitTests.Person();
        person.birthdate = new Date();
        person.gender = ElasticsearchEntityMapperUnitTests.Gender.MAN;
        person.address = observatoryRoad;
        LinkedHashMap<String, Object> sink = writeToMap(person);
        assertThat(sink.get("address")).isEqualTo(gratiotAveAsMap);
    }

    // DATAES-530
    @Test
    public void writesConcreteList() throws IOException {
        ElasticsearchEntityMapperUnitTests.Person ginger = new ElasticsearchEntityMapperUnitTests.Person();
        ginger.id = "ginger";
        ginger.gender = ElasticsearchEntityMapperUnitTests.Gender.MAN;
        sarahConnor.coWorkers = Arrays.asList(kyleReese, ginger);
        LinkedHashMap<String, Object> target = writeToMap(sarahConnor);
        assertThat(((List) (target.get("coWorkers")))).hasSize(2).contains(kyleAsMap);
    }

    // DATAES-530
    @Test
    public void writesInterfaceList() throws IOException {
        ElasticsearchEntityMapperUnitTests.Inventory gun = new ElasticsearchEntityMapperUnitTests.Gun("Glock 19", 33);
        ElasticsearchEntityMapperUnitTests.Inventory grenade = new ElasticsearchEntityMapperUnitTests.Grenade("40 mm");
        sarahConnor.inventoryList = Arrays.asList(gun, grenade);
        LinkedHashMap<String, Object> target = writeToMap(sarahConnor);
        assertThat(((List) (target.get("inventoryList")))).containsExactly(gunAsMap, grenadeAsMap);
    }

    // DATAES-530
    @Test
    public void readTypeCorrectly() {
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(target).isEqualTo(sarahConnor);
    }

    // DATAES-530
    @Test
    public void readListOfConcreteTypesCorrectly() {
        sarahAsMap.put("coWorkers", Arrays.asList(kyleAsMap));
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(getCoWorkers()).contains(kyleReese);
    }

    // DATAES-530
    @Test
    public void readListOfInterfacesTypesCorrectly() {
        sarahAsMap.put("inventoryList", Arrays.asList(gunAsMap, grenadeAsMap));
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(getInventoryList()).containsExactly(gun, grenade);
    }

    // DATAES-530
    @Test
    public void writeMapOfConcreteType() {
        sarahConnor.shippingAddresses = new LinkedHashMap<>();
        sarahConnor.shippingAddresses.put("home", observatoryRoad);
        LinkedHashMap<String, Object> target = writeToMap(sarahConnor);
        assertThat(target.get("shippingAddresses")).isInstanceOf(Map.class);
        assertThat(target.get("shippingAddresses")).isEqualTo(Collections.singletonMap("home", gratiotAveAsMap));
    }

    // DATAES-530
    @Test
    public void writeMapOfInterfaceType() {
        sarahConnor.inventoryMap = new LinkedHashMap<>();
        sarahConnor.inventoryMap.put("glock19", gun);
        sarahConnor.inventoryMap.put("40 mm grenade", grenade);
        LinkedHashMap<String, Object> target = writeToMap(sarahConnor);
        assertThat(target.get("inventoryMap")).isInstanceOf(Map.class);
        assertThat(((Map) (target.get("inventoryMap")))).containsEntry("glock19", gunAsMap).containsEntry("40 mm grenade", grenadeAsMap);
    }

    // DATAES-530
    @Test
    public void readConcreteMapCorrectly() {
        sarahAsMap.put("shippingAddresses", Collections.singletonMap("home", gratiotAveAsMap));
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(getShippingAddresses()).hasSize(1).containsEntry("home", observatoryRoad);
    }

    // DATAES-530
    @Test
    public void readInterfaceMapCorrectly() {
        sarahAsMap.put("inventoryMap", Collections.singletonMap("glock19", gunAsMap));
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(getInventoryMap()).hasSize(1).containsEntry("glock19", gun);
    }

    // DATAES-530
    @Test
    public void genericWriteList() {
        ElasticsearchEntityMapperUnitTests.Skynet skynet = new ElasticsearchEntityMapperUnitTests.Skynet();
        skynet.objectList = new ArrayList<>();
        skynet.objectList.add(t800);
        skynet.objectList.add(gun);
        LinkedHashMap<String, Object> target = writeToMap(skynet);
        assertThat(((List<Object>) (target.get("objectList")))).containsExactly(t800AsMap, gunAsMap);
    }

    // DATAES-530
    @Test
    public void readGenericList() {
        LinkedHashMap<String, Object> source = new LinkedHashMap<>();
        source.put("objectList", Arrays.asList(t800AsMap, gunAsMap));
        ElasticsearchEntityMapperUnitTests.Skynet target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Skynet.class, source);
        assertThat(getObjectList()).containsExactly(t800, gun);
    }

    // DATAES-530
    @Test
    public void genericWriteListWithList() {
        ElasticsearchEntityMapperUnitTests.Skynet skynet = new ElasticsearchEntityMapperUnitTests.Skynet();
        skynet.objectList = new ArrayList<>();
        skynet.objectList.add(Arrays.asList(t800, gun));
        LinkedHashMap<String, Object> target = writeToMap(skynet);
        assertThat(((List<Object>) (target.get("objectList")))).containsExactly(Arrays.asList(t800AsMap, gunAsMap));
    }

    // DATAES-530
    @Test
    public void readGenericListList() {
        LinkedHashMap<String, Object> source = new LinkedHashMap<>();
        source.put("objectList", Arrays.asList(Arrays.asList(t800AsMap, gunAsMap)));
        ElasticsearchEntityMapperUnitTests.Skynet target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Skynet.class, source);
        assertThat(getObjectList()).containsExactly(Arrays.asList(t800, gun));
    }

    // DATAES-530
    @Test
    public void writeGenericMap() {
        ElasticsearchEntityMapperUnitTests.Skynet skynet = new ElasticsearchEntityMapperUnitTests.Skynet();
        skynet.objectMap = new LinkedHashMap<>();
        skynet.objectMap.put("gun", gun);
        skynet.objectMap.put("grenade", grenade);
        LinkedHashMap<String, Object> target = writeToMap(skynet);
        assertThat(((Map<String, Object>) (target.get("objectMap")))).containsEntry("gun", gunAsMap).containsEntry("grenade", grenadeAsMap);
    }

    // DATAES-530
    @Test
    public void readGenericMap() {
        LinkedHashMap<String, Object> source = new LinkedHashMap<>();
        source.put("objectMap", Collections.singletonMap("glock19", gunAsMap));
        ElasticsearchEntityMapperUnitTests.Skynet target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Skynet.class, source);
        assertThat(getObjectMap()).containsEntry("glock19", gun);
    }

    // DATAES-530
    @Test
    public void writeGenericMapMap() {
        ElasticsearchEntityMapperUnitTests.Skynet skynet = new ElasticsearchEntityMapperUnitTests.Skynet();
        skynet.objectMap = new LinkedHashMap<>();
        skynet.objectMap.put("inventory", Collections.singletonMap("glock19", gun));
        LinkedHashMap<String, Object> target = writeToMap(skynet);
        assertThat(((Map<String, Object>) (target.get("objectMap")))).containsEntry("inventory", Collections.singletonMap("glock19", gunAsMap));
    }

    // DATAES-530
    @Test
    public void readGenericMapMap() {
        LinkedHashMap<String, Object> source = new LinkedHashMap<>();
        source.put("objectMap", Collections.singletonMap("inventory", Collections.singletonMap("glock19", gunAsMap)));
        ElasticsearchEntityMapperUnitTests.Skynet target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Skynet.class, source);
        assertThat(getObjectMap()).containsEntry("inventory", Collections.singletonMap("glock19", gun));
    }

    // DATAES-530
    @Test
    public void readsNestedEntity() {
        sarahAsMap.put("address", gratiotAveAsMap);
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(getAddress()).isEqualTo(observatoryRoad);
    }

    // DATAES-530
    @Test
    public void readsNestedObjectEntity() {
        LinkedHashMap<String, Object> source = new LinkedHashMap<>();
        source.put("object", t800AsMap);
        ElasticsearchEntityMapperUnitTests.Skynet target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Skynet.class, source);
        assertThat(getObject()).isEqualTo(t800);
    }

    // DATAES-530
    @Test
    public void writesAliased() {
        assertThat(writeToMap(rifle)).containsEntry("_class", "rifle").doesNotContainValue(ElasticsearchEntityMapperUnitTests.Rifle.class.getName());
    }

    // DATAES-530
    @Test
    public void writesNestedAliased() {
        t800.inventoryList = Collections.singletonList(rifle);
        LinkedHashMap<String, Object> target = writeToMap(t800);
        assertThat(((List) (target.get("inventoryList")))).contains(rifleAsMap);
    }

    // DATAES-530
    @Test
    public void readsAliased() {
        assertThat(entityMapper.read(ElasticsearchEntityMapperUnitTests.Inventory.class, rifleAsMap)).isEqualTo(rifle);
    }

    // DATAES-530
    @Test
    public void readsNestedAliased() {
        t800AsMap.put("inventoryList", Collections.singletonList(rifleAsMap));
        assertThat(getInventoryList()).containsExactly(rifle);
    }

    // DATAES-530
    @Test
    public void appliesCustomConverterForWrite() {
        assertThat(writeToMap(shotGun)).isEqualTo(shotGunAsMap);
    }

    // DATAES-530
    @Test
    public void appliesCustomConverterForRead() {
        assertThat(entityMapper.read(ElasticsearchEntityMapperUnitTests.Inventory.class, shotGunAsMap)).isEqualTo(shotGun);
    }

    // DATAES-530
    @Test
    public void writeSubTypeCorrectly() {
        sarahConnor.address = bigBunsCafe;
        LinkedHashMap<String, Object> target = writeToMap(sarahConnor);
        assertThat(target.get("address")).isEqualTo(bigBunsCafeAsMap);
    }

    // DATAES-530
    @Test
    public void readSubTypeCorrectly() {
        sarahAsMap.put("address", bigBunsCafeAsMap);
        ElasticsearchEntityMapperUnitTests.Person target = entityMapper.read(ElasticsearchEntityMapperUnitTests.Person.class, sarahAsMap);
        assertThat(target.address).isEqualTo(bigBunsCafe);
    }

    public static class Sample {
        @ReadOnlyProperty
        public String readOnly;

        @Transient
        public String annotatedTransientProperty;

        public transient String javaTransientProperty;

        public String property;
    }

    @Data
    static class Person {
        @Id
        String id;

        String name;

        Date birthdate;

        ElasticsearchEntityMapperUnitTests.Gender gender;

        ElasticsearchEntityMapperUnitTests.Address address;

        List<ElasticsearchEntityMapperUnitTests.Person> coWorkers;

        List<ElasticsearchEntityMapperUnitTests.Inventory> inventoryList;

        Map<String, ElasticsearchEntityMapperUnitTests.Address> shippingAddresses;

        Map<String, ElasticsearchEntityMapperUnitTests.Inventory> inventoryMap;
    }

    enum Gender {

        MAN("1"),
        MACHINE("0");
        String theValue;

        Gender(String theValue) {
            this.theValue = theValue;
        }

        public String getTheValue() {
            return theValue;
        }
    }

    interface Inventory {
        String getLabel();
    }

    @Getter
    @RequiredArgsConstructor
    @EqualsAndHashCode
    static class Gun implements ElasticsearchEntityMapperUnitTests.Inventory {
        final String label;

        final int shotsPerMagazine;

        @Override
        public String getLabel() {
            return label;
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    static class Grenade implements ElasticsearchEntityMapperUnitTests.Inventory {
        final String label;

        @Override
        public String getLabel() {
            return label;
        }
    }

    @TypeAlias("rifle")
    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Rifle implements ElasticsearchEntityMapperUnitTests.Inventory {
        final String label;

        final double weight;

        final int maxShotsPerMagazine;

        @Override
        public String getLabel() {
            return label;
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class ShotGun implements ElasticsearchEntityMapperUnitTests.Inventory {
        final String label;

        @Override
        public String getLabel() {
            return label;
        }
    }

    @Data
    static class Address {
        Point location;

        String street;

        String city;
    }

    @Data
    static class Place extends ElasticsearchEntityMapperUnitTests.Address {
        String name;
    }

    @Data
    static class Skynet {
        Object object;

        List<Object> objectList;

        Map<String, Object> objectMap;
    }

    @WritingConverter
    static class ShotGunToMapConverter implements Converter<ElasticsearchEntityMapperUnitTests.ShotGun, Map<String, Object>> {
        @Override
        public Map<String, Object> convert(ElasticsearchEntityMapperUnitTests.ShotGun source) {
            LinkedHashMap<String, Object> target = new LinkedHashMap<>();
            target.put("model", source.getLabel());
            target.put("_class", ElasticsearchEntityMapperUnitTests.ShotGun.class.getName());
            return target;
        }
    }

    @ReadingConverter
    static class MapToShotGunConverter implements Converter<Map<String, Object>, ElasticsearchEntityMapperUnitTests.ShotGun> {
        @Override
        public ElasticsearchEntityMapperUnitTests.ShotGun convert(Map<String, Object> source) {
            return new ElasticsearchEntityMapperUnitTests.ShotGun(source.get("model").toString());
        }
    }
}


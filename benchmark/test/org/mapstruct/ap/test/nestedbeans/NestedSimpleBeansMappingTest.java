/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.nestedbeans;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;


@WithClasses({ User.class, UserDto.class, Car.class, CarDto.class, House.class, HouseDto.class, Wheel.class, WheelDto.class, Roof.class, RoofDto.class, RoofType.class, ExternalRoofType.class, org.mapstruct.ap.test.nestedbeans.other.CarDto.class, org.mapstruct.ap.test.nestedbeans.other.UserDto.class, org.mapstruct.ap.test.nestedbeans.other.HouseDto.class, org.mapstruct.ap.test.nestedbeans.other.RoofDto.class, org.mapstruct.ap.test.nestedbeans.other.WheelDto.class, UserDtoMapperClassic.class, UserDtoMapperSmart.class, UserDtoUpdateMapperSmart.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class NestedSimpleBeansMappingTest {
    @Rule
    public final GeneratedSource generatedSource = new GeneratedSource().addComparisonToFixtureFor(UserDtoMapperClassic.class, UserDtoMapperSmart.class, UserDtoUpdateMapperSmart.class);

    @Test
    public void shouldHaveAllFields() throws Exception {
        // If this test fails that means something new was added to the structure of the User/UserDto.
        // Make sure that the other tests are also updated (the new field is asserted)
        String[] userFields = new String[]{ "name", "car", "secondCar", "house" };
        assertThat(User.class).hasOnlyDeclaredFields(userFields);
        assertThat(UserDto.class).hasOnlyDeclaredFields(userFields);
        String[] carFields = new String[]{ "name", "year", "wheels" };
        assertThat(Car.class).hasOnlyDeclaredFields(carFields);
        assertThat(CarDto.class).hasOnlyDeclaredFields(carFields);
        String[] wheelFields = new String[]{ "front", "right" };
        assertThat(Wheel.class).hasOnlyDeclaredFields(wheelFields);
        assertThat(WheelDto.class).hasOnlyDeclaredFields(wheelFields);
        String[] houseFields = new String[]{ "name", "year", "roof" };
        assertThat(House.class).hasOnlyDeclaredFields(houseFields);
        assertThat(HouseDto.class).hasOnlyDeclaredFields(houseFields);
        String[] roofFields = new String[]{ "color", "type" };
        assertThat(Roof.class).hasOnlyDeclaredFields(roofFields);
        assertThat(RoofDto.class).hasOnlyDeclaredFields(roofFields);
    }

    @Test
    public void shouldMapNestedBeans() {
        User user = TestData.createUser();
        UserDto classicMapping = UserDtoMapperClassic.INSTANCE.userToUserDto(user);
        UserDto smartMapping = UserDtoMapperSmart.INSTANCE.userToUserDto(user);
        NestedSimpleBeansMappingTest.assertUserDto(classicMapping, user);
        NestedSimpleBeansMappingTest.assertUserDto(smartMapping, user);
    }

    @Test
    public void shouldMapUpdateNestedBeans() {
        User user = TestData.createUser();
        user.getCar().setName(null);
        // create a pre-exsiting smartMapping
        UserDto smartMapping = new UserDto();
        smartMapping.setCar(new CarDto());
        smartMapping.getCar().setName("Toyota");
        // action
        UserDtoUpdateMapperSmart.INSTANCE.userToUserDto(smartMapping, user);
        // result
        assertThat(smartMapping.getName()).isEqualTo(user.getName());
        assertThat(smartMapping.getCar().getYear()).isEqualTo(user.getCar().getYear());
        assertThat(smartMapping.getCar().getName()).isNull();
        assertThat(user.getCar().getName()).isNull();
        NestedSimpleBeansMappingTest.assertWheels(smartMapping.getCar().getWheels(), user.getCar().getWheels());
        NestedSimpleBeansMappingTest.assertCar(smartMapping.getSecondCar(), user.getSecondCar());
        NestedSimpleBeansMappingTest.assertHouse(smartMapping.getHouse(), user.getHouse());
    }
}


/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.inheritfromconfig;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 *
 *
 * @author Andreas Gudian
 */
@RunWith(AnnotationProcessorTestRunner.class)
@WithClasses({ BaseVehicleDto.class, BaseVehicleEntity.class, CarDto.class, CarEntity.class, CarMapperWithAutoInheritance.class, CarMapperWithExplicitInheritance.class, AutoInheritedConfig.class, NotToBeUsedMapper.class })
@IssueKey("168")
public class InheritFromConfigTest {
    @Test
    public void autoInheritedMappingIsApplied() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = CarMapperWithAutoInheritance.INSTANCE.toCarEntity(carDto);
        assertEntity(carEntity);
    }

    @Test
    public void autoInheritedMappingIsAppliedForMappingTarget() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = new CarEntity();
        CarMapperWithAutoInheritance.INSTANCE.intoCarEntityOnItsOwn(carDto, carEntity);
        assertEntity(carEntity);
    }

    @Test
    public void autoInheritedMappingIsAppliedForMappingTargetWithTwoStepInheritance() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = new CarEntity();
        CarMapperWithAutoInheritance.INSTANCE.intoCarEntity(carDto, carEntity);
        assertEntity(carEntity);
    }

    @Test
    public void autoInheritedMappingIsOverriddenAtMethodLevel() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = CarMapperWithAutoInheritance.INSTANCE.toCarEntityWithFixedAuditTrail(carDto);
        assertThat(carEntity.getColor()).isEqualTo("red");
        assertThat(carEntity.getPrimaryKey()).isEqualTo(42L);
        assertThat(carEntity.getAuditTrail()).isEqualTo("fixed");
    }

    @Test
    public void autoInheritedMappingIsAppliedInReverse() {
        CarEntity carEntity = new CarEntity();
        carEntity.setColor("red");
        carEntity.setPrimaryKey(42L);
        CarDto carDto = CarMapperWithAutoInheritance.INSTANCE.toCarDto(carEntity);
        assertThat(carDto.getColour()).isEqualTo("red");
        assertThat(carDto.getId()).isEqualTo(42L);
    }

    @Test
    public void explicitInheritedMappingIsAppliedInReverse() {
        CarEntity carEntity = new CarEntity();
        carEntity.setColor("red");
        carEntity.setPrimaryKey(42L);
        CarDto carDto = CarMapperWithExplicitInheritance.INSTANCE.toCarDto(carEntity);
        assertThat(carDto.getColour()).isEqualTo("red");
        assertThat(carDto.getId()).isEqualTo(42L);
    }

    @Test
    @IssueKey("1065")
    @WithClasses({ CarMapperReverseWithExplicitInheritance.class })
    public void explicitInheritedMappingIsAppliedInReverseDirectlyFromConfig() {
        CarEntity carEntity = new CarEntity();
        carEntity.setColor("red");
        carEntity.setPrimaryKey(42L);
        CarDto carDto = CarMapperReverseWithExplicitInheritance.INSTANCE.toCarDto(carEntity);
        assertThat(carDto.getColour()).isEqualTo("red");
        assertThat(carDto.getId()).isEqualTo(42L);
    }

    @Test
    @IssueKey("1255")
    @WithClasses({ CarMapperReverseWithAutoInheritance.class, AutoInheritedReverseConfig.class })
    public void autoInheritedMappingIsAppliedInReverseDirectlyFromConfig() {
        CarEntity carEntity = new CarEntity();
        carEntity.setColor("red");
        carEntity.setPrimaryKey(42L);
        CarDto carDto = CarMapperReverseWithAutoInheritance.INSTANCE.toCarDto(carEntity);
        assertThat(carDto.getColour()).isEqualTo("red");
        assertThat(carDto.getId()).isEqualTo(42L);
    }

    @Test
    @IssueKey("1255")
    @WithClasses({ CarMapperAllWithAutoInheritance.class, AutoInheritedAllConfig.class })
    public void autoInheritedMappingIsAppliedInForwardAndReverseDirectlyFromConfig() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = CarMapperAllWithAutoInheritance.INSTANCE.toCarEntity(carDto);
        CarDto carDto2 = CarMapperAllWithAutoInheritance.INSTANCE.toCarDto(carEntity);
        assertThat(carDto.getColour()).isEqualTo(carDto2.getColour());
        assertThat(carDto.getId()).isEqualTo(carDto2.getId());
    }

    @Test
    public void explicitInheritedMappingWithTwoLevelsIsOverriddenAtMethodLevel() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = CarMapperWithExplicitInheritance.INSTANCE.toCarEntityWithFixedAuditTrail(carDto);
        assertThat(carEntity.getColor()).isEqualTo("red");
        assertThat(carEntity.getPrimaryKey()).isEqualTo(42L);
        assertThat(carEntity.getAuditTrail()).isEqualTo("fixed");
    }

    @Test
    public void explicitInheritedMappingIsApplied() {
        CarDto carDto = newTestDto();
        CarEntity carEntity = CarMapperWithExplicitInheritance.INSTANCE.toCarEntity(carDto);
        assertEntity(carEntity);
    }

    @Test
    @WithClasses({ DriverDto.class, CarWithDriverEntity.class, CarWithDriverMapperWithAutoInheritance.class, AutoInheritedDriverConfig.class })
    public void autoInheritedFromMultipleSources() {
        CarDto carDto = newTestDto();
        DriverDto driverDto = new DriverDto();
        driverDto.setName("Malcroft");
        CarWithDriverEntity carWithDriverEntity = CarWithDriverMapperWithAutoInheritance.INSTANCE.toCarWithDriverEntity(carDto, driverDto);
        assertEntity(carWithDriverEntity);
        assertThat(carWithDriverEntity.getDriverName()).isEqualTo("Malcroft");
    }
}


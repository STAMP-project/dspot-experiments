/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.ignore;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for ignoring properties during the mapping.
 *
 * @author Gunnar Morling
 */
@WithClasses({ Animal.class, AnimalDto.class, AnimalMapper.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class IgnorePropertyTest {
    @Test
    @IssueKey("72")
    public void shouldNotPropagateIgnoredPropertyGivenViaTargetAttribute() {
        Animal animal = new Animal("Bruno", 100, 23, "black");
        AnimalDto animalDto = AnimalMapper.INSTANCE.animalToDto(animal);
        assertThat(animalDto).isNotNull();
        assertThat(animalDto.getName()).isEqualTo("Bruno");
        assertThat(animalDto.getSize()).isEqualTo(100);
        assertThat(animalDto.getAge()).isNull();
        assertThat(animalDto.publicAge).isNull();
        assertThat(animalDto.getColor()).isNull();
        assertThat(animalDto.publicColor).isNull();
    }

    @Test
    @IssueKey("1392")
    public void shouldIgnoreAllTargetPropertiesWithNoUnmappedTargetWarnings() {
        Animal animal = new Animal("Bruno", 100, 23, "black");
        AnimalDto animalDto = AnimalMapper.INSTANCE.animalToDtoIgnoreAll(animal);
        assertThat(animalDto).isNotNull();
        assertThat(animalDto.getName()).isNull();
        assertThat(animalDto.getSize()).isNull();
        assertThat(animalDto.getAge()).isNull();
        assertThat(animalDto.publicAge).isNull();
        assertThat(animalDto.getColor()).isNull();
        assertThat(animalDto.publicColor).isNull();
    }

    @Test
    @IssueKey("337")
    public void propertyIsIgnoredInReverseMappingWhenSourceIsAlsoSpecifiedICWIgnore() {
        AnimalDto animalDto = new AnimalDto("Bruno", 100, 23, "black");
        Animal animal = AnimalMapper.INSTANCE.animalDtoToAnimal(animalDto);
        assertThat(animal).isNotNull();
        assertThat(animalDto.getName()).isEqualTo("Bruno");
        assertThat(animalDto.getSize()).isEqualTo(100);
        assertThat(animal.getColour()).isNull();
        assertThat(animal.publicColour).isNull();
    }
}


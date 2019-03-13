/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.dependency;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for ordering mapped attributes by means of {@link Mapping#dependsOn()}.
 *
 * @author Gunnar Morling
 */
@WithClasses({ Person.class, PersonDto.class, Address.class, AddressDto.class, AddressMapper.class })
@RunWith(AnnotationProcessorTestRunner.class)
public class OrderingTest {
    @Test
    @IssueKey("304")
    public void shouldApplyChainOfDependencies() {
        Address source = new Address();
        source.setFirstName("Bob");
        source.setMiddleName("J.");
        source.setLastName("McRobb");
        AddressDto target = AddressMapper.INSTANCE.addressToDto(source);
        assertThat(target).isNotNull();
        assertThat(target.getFullName()).isEqualTo("Bob J. McRobb");
    }

    @Test
    @IssueKey("304")
    public void shouldApplySeveralDependenciesConfiguredForOneProperty() {
        Person source = new Person();
        source.setFirstName("Bob");
        source.setMiddleName("J.");
        source.setLastName("McRobb");
        PersonDto target = AddressMapper.INSTANCE.personToDto(source);
        assertThat(target).isNotNull();
        assertThat(target.getFullName()).isEqualTo("Bob J. McRobb");
    }
}


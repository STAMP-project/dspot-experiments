/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.source.manysourcearguments;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;


/**
 * Test for propagation of attribute without setter in source and getter in
 * target.
 *
 * @author Gunnar Morling
 */
@IssueKey("31")
@RunWith(AnnotationProcessorTestRunner.class)
public class ManySourceArgumentsTest {
    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapper.class, ReferencedMapper.class })
    public void shouldMapSeveralSourceAttributesToCombinedTarget() {
        Person person = new Person("Bob", "Garner", 181, "An actor");
        Address address = new Address("Main street", 12345, 42, "His address");
        DeliveryAddress deliveryAddress = SourceTargetMapper.INSTANCE.personAndAddressToDeliveryAddress(person, address);
        assertThat(deliveryAddress).isNotNull();
        assertThat(deliveryAddress.getLastName()).isEqualTo("Garner");
        assertThat(deliveryAddress.getZipCode()).isEqualTo(12345);
        assertThat(deliveryAddress.getHouseNumber()).isEqualTo(42);
        assertThat(deliveryAddress.getDescription()).isEqualTo("An actor");
        assertThat(ReferencedMapper.isBeforeMappingCalled()).isTrue();
    }

    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapper.class, ReferencedMapper.class })
    public void shouldMapSeveralSourceAttributesToCombinedTargetWithTargetParameter() {
        Person person = new Person("Bob", "Garner", 181, "An actor");
        Address address = new Address("Main street", 12345, 42, "His address");
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        SourceTargetMapper.INSTANCE.personAndAddressToDeliveryAddress(person, address, deliveryAddress);
        assertThat(deliveryAddress.getLastName()).isEqualTo("Garner");
        assertThat(deliveryAddress.getZipCode()).isEqualTo(12345);
        assertThat(deliveryAddress.getHouseNumber()).isEqualTo(42);
        assertThat(deliveryAddress.getDescription()).isEqualTo("An actor");
        assertThat(ReferencedMapper.isBeforeMappingCalled()).isTrue();
    }

    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapper.class, ReferencedMapper.class })
    public void shouldSetAttributesFromNonNullParameters() {
        Person person = new Person("Bob", "Garner", 181, "An actor");
        DeliveryAddress deliveryAddress = SourceTargetMapper.INSTANCE.personAndAddressToDeliveryAddress(person, null);
        assertThat(deliveryAddress).isNotNull();
        assertThat(deliveryAddress.getLastName()).isEqualTo("Garner");
        assertThat(deliveryAddress.getDescription()).isEqualTo("An actor");
        assertThat(deliveryAddress.getHouseNumber()).isEqualTo(0);
        assertThat(deliveryAddress.getStreet()).isNull();
    }

    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapper.class, ReferencedMapper.class })
    public void shouldReturnNullIfAllParametersAreNull() {
        DeliveryAddress deliveryAddress = SourceTargetMapper.INSTANCE.personAndAddressToDeliveryAddress(null, null);
        assertThat(deliveryAddress).isNull();
    }

    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapper.class, ReferencedMapper.class })
    public void shouldMapSeveralSourceAttributesAndParameters() {
        Person person = new Person("Bob", "Garner", 181, "An actor");
        DeliveryAddress deliveryAddress = SourceTargetMapper.INSTANCE.personAndAddressToDeliveryAddress(person, 42, 12345, "Main street");
        assertThat(deliveryAddress.getLastName()).isEqualTo("Garner");
        assertThat(deliveryAddress.getZipCode()).isEqualTo(12345);
        assertThat(deliveryAddress.getHouseNumber()).isEqualTo(42);
        assertThat(deliveryAddress.getDescription()).isEqualTo("An actor");
        assertThat(deliveryAddress.getStreet()).isEqualTo("Main street");
    }

    @IssueKey("1593")
    @Test
    @WithClasses({ Person.class, Address.class, DeliveryAddress.class, SourceTargetMapperWithConfig.class, SourceTargetConfig.class })
    public void shouldUseConfig() {
        Person person = new Person("Bob", "Garner", 181, "An actor");
        Address address = new Address("Main street", 12345, 42, "His address");
        DeliveryAddress deliveryAddress = SourceTargetMapperWithConfig.INSTANCE.personAndAddressToDeliveryAddress(person, address);
        assertThat(deliveryAddress).isNotNull();
        assertThat(deliveryAddress.getLastName()).isEqualTo("Garner");
        assertThat(deliveryAddress.getZipCode()).isEqualTo(12345);
        assertThat(deliveryAddress.getHouseNumber()).isEqualTo(42);
        assertThat(deliveryAddress.getDescription()).isEqualTo("An actor");
    }
}


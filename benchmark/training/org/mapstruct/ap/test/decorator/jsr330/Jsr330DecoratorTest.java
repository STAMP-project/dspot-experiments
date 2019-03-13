/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.decorator.jsr330;


import java.util.Calendar;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.test.decorator.Address;
import org.mapstruct.ap.test.decorator.AddressDto;
import org.mapstruct.ap.test.decorator.Person;
import org.mapstruct.ap.test.decorator.PersonDto;
import org.mapstruct.ap.testutil.IssueKey;
import org.mapstruct.ap.testutil.WithClasses;
import org.mapstruct.ap.testutil.runner.AnnotationProcessorTestRunner;
import org.mapstruct.ap.testutil.runner.GeneratedSource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Test for the application of decorators using component model jsr330.
 *
 * @author Andreas Gudian
 */
@WithClasses({ Person.class, PersonDto.class, Address.class, AddressDto.class, PersonMapper.class, PersonMapperDecorator.class })
@IssueKey("592")
@RunWith(AnnotationProcessorTestRunner.class)
@ComponentScan(basePackageClasses = Jsr330DecoratorTest.class)
@Configuration
public class Jsr330DecoratorTest {
    private final GeneratedSource generatedSource = new GeneratedSource();

    @Inject
    @Named
    private PersonMapper personMapper;

    private ConfigurableApplicationContext context;

    @Test
    public void shouldInvokeDecoratorMethods() {
        Calendar birthday = Calendar.getInstance();
        birthday.set(1928, 4, 23);
        Person person = new Person("Gary", "Crant", birthday.getTime(), new Address("42 Ocean View Drive"));
        PersonDto personDto = personMapper.personToPersonDto(person);
        assertThat(personDto).isNotNull();
        assertThat(personDto.getName()).isEqualTo("Gary Crant");
        assertThat(personDto.getAddress()).isNotNull();
        assertThat(personDto.getAddress().getAddressLine()).isEqualTo("42 Ocean View Drive");
    }

    @Test
    public void shouldDelegateNonDecoratedMethodsToDefaultImplementation() {
        Address address = new Address("42 Ocean View Drive");
        AddressDto addressDto = personMapper.addressToAddressDto(address);
        assertThat(addressDto).isNotNull();
        assertThat(addressDto.getAddressLine()).isEqualTo("42 Ocean View Drive");
    }

    @IssueKey("664")
    @Test
    public void hasSingletonAnnotation() {
        // check the decorator
        generatedSource.forMapper(PersonMapper.class).content().contains((("@Singleton" + (System.lineSeparator())) + "@Named"));
        // check the plain mapper
        generatedSource.forDecoratedMapper(PersonMapper.class).content().contains((("@Singleton" + (System.lineSeparator())) + "@Named"));
    }
}


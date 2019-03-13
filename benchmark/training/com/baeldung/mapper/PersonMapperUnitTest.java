package com.baeldung.mapper;


import PersonMapper.INSTANCE;
import com.baeldung.dto.PersonDTO;
import com.baeldung.entity.Person;
import org.junit.Assert;
import org.junit.Test;


public class PersonMapperUnitTest {
    @Test
    public void givenPersonEntitytoPersonWithExpression_whenMaps_thenCorrect() {
        Person entity = new Person();
        entity.setName("Micheal");
        PersonDTO personDto = INSTANCE.personToPersonDTO(entity);
        Assert.assertNull(entity.getId());
        Assert.assertNotNull(personDto.getId());
        Assert.assertEquals(personDto.getName(), entity.getName());
    }
}


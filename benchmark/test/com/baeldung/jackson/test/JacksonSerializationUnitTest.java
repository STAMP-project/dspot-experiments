package com.baeldung.jackson.test;


import com.baeldung.jackson.dtos.Item;
import com.baeldung.jackson.dtos.ItemWithSerializer;
import com.baeldung.jackson.dtos.MyDto;
import com.baeldung.jackson.dtos.MyDtoFieldNameChanged;
import com.baeldung.jackson.dtos.MyDtoNoAccessors;
import com.baeldung.jackson.dtos.MyDtoNoAccessorsAndFieldVisibility;
import com.baeldung.jackson.dtos.User;
import com.baeldung.jackson.serialization.ItemSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;


public class JacksonSerializationUnitTest {
    // tests - single entity to json
    @Test
    public final void whenSerializing_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String dtoAsString = mapper.writeValueAsString(new MyDto());
        Assert.assertThat(dtoAsString, Matchers.containsString("intValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("stringValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("booleanValue"));
    }

    @Test
    public final void givenNameOfFieldIsChangedViaAnnotationOnGetter_whenSerializing_thenCorrect() throws JsonParseException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final MyDtoFieldNameChanged dtoObject = new MyDtoFieldNameChanged();
        dtoObject.setStringValue("a");
        final String dtoAsString = mapper.writeValueAsString(dtoObject);
        Assert.assertThat(dtoAsString, Matchers.not(Matchers.containsString("stringValue")));
        Assert.assertThat(dtoAsString, Matchers.containsString("strVal"));
        System.out.println(dtoAsString);
    }

    // tests - serialize via accessors/fields
    @Test(expected = JsonMappingException.class)
    public final void givenObjectHasNoAccessors_whenSerializing_thenException() throws JsonParseException, IOException {
        final String dtoAsString = new ObjectMapper().writeValueAsString(new MyDtoNoAccessors());
        Assert.assertThat(dtoAsString, Matchers.notNullValue());
    }

    @Test
    public final void givenObjectHasNoAccessors_whenSerializingWithPrivateFieldsVisibility_thenNoException() throws JsonParseException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, ANY);
        final String dtoAsString = objectMapper.writeValueAsString(new MyDtoNoAccessors());
        Assert.assertThat(dtoAsString, Matchers.containsString("intValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("stringValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("booleanValue"));
    }

    @Test
    public final void givenObjectHasNoAccessorsButHasVisibleFields_whenSerializing_thenNoException() throws JsonParseException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String dtoAsString = objectMapper.writeValueAsString(new MyDtoNoAccessorsAndFieldVisibility());
        Assert.assertThat(dtoAsString, Matchers.containsString("intValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("stringValue"));
        Assert.assertThat(dtoAsString, Matchers.containsString("booleanValue"));
    }

    // tests - multiple entities to json
    @Test
    public final void whenDtoIsSerialized_thenCorrect() throws JsonParseException, IOException {
        final List<MyDto> listOfDtos = Lists.newArrayList(new MyDto("a", 1, true), new MyDto("bc", 3, false));
        final ObjectMapper mapper = new ObjectMapper();
        final String dtosAsString = mapper.writeValueAsString(listOfDtos);
        System.out.println(dtosAsString);
    }

    // tests - custom serializer
    @Test
    public final void whenSerializing_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
        final Item myItem = new Item(1, "theItem", new User(2, "theUser"));
        final String serialized = new ObjectMapper().writeValueAsString(myItem);
        System.out.println(serialized);
    }

    @Test
    public final void whenSerializingWithCustomSerializer_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
        final Item myItem = new Item(1, "theItem", new User(2, "theUser"));
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Item.class, new ItemSerializer());
        mapper.registerModule(simpleModule);
        final String serialized = mapper.writeValueAsString(myItem);
        System.out.println(serialized);
    }

    @Test
    public final void givenSerializerRegisteredOnClass_whenSerializingWithCustomSerializer_thenNoExceptions() throws JsonGenerationException, JsonMappingException, IOException {
        final ItemWithSerializer myItem = new ItemWithSerializer(1, "theItem", new User(2, "theUser"));
        final String serialized = new ObjectMapper().writeValueAsString(myItem);
        System.out.println(serialized);
    }
}


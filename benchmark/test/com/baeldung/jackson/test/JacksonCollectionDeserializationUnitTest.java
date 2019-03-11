package com.baeldung.jackson.test;


import com.baeldung.jackson.dtos.MyDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JacksonCollectionDeserializationUnitTest {
    // tests - json to multiple entity
    @Test
    public final void givenJsonArray_whenDeserializingAsArray_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<MyDto> listOfDtos = Lists.newArrayList(new MyDto("a", 1, true), new MyDto("bc", 3, false));
        final String jsonArray = mapper.writeValueAsString(listOfDtos);
        // [{"stringValue":"a","intValue":1,"booleanValue":true},{"stringValue":"bc","intValue":3,"booleanValue":false}]
        final MyDto[] asArray = mapper.readValue(jsonArray, MyDto[].class);
        Assert.assertThat(asArray[0], Matchers.instanceOf(MyDto.class));
    }

    @Test
    public final void givenJsonArray_whenDeserializingAsListWithNoTypeInfo_thenNotCorrect() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<MyDto> listOfDtos = Lists.newArrayList(new MyDto("a", 1, true), new MyDto("bc", 3, false));
        final String jsonArray = mapper.writeValueAsString(listOfDtos);
        // [{"stringValue":"a","intValue":1,"booleanValue":true},{"stringValue":"bc","intValue":3,"booleanValue":false}]
        final List<MyDto> asList = mapper.readValue(jsonArray, List.class);
        Assert.assertThat(((Object) (asList.get(0))), Matchers.instanceOf(LinkedHashMap.class));
    }

    @Test
    public final void givenJsonArray_whenDeserializingAsListWithTypeReferenceHelp_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<MyDto> listOfDtos = Lists.newArrayList(new MyDto("a", 1, true), new MyDto("bc", 3, false));
        final String jsonArray = mapper.writeValueAsString(listOfDtos);
        // [{"stringValue":"a","intValue":1,"booleanValue":true},{"stringValue":"bc","intValue":3,"booleanValue":false}]
        final List<MyDto> asList = mapper.readValue(jsonArray, new TypeReference<List<MyDto>>() {});
        Assert.assertThat(asList.get(0), Matchers.instanceOf(MyDto.class));
    }

    @Test
    public final void givenJsonArray_whenDeserializingAsListWithJavaTypeHelp_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<MyDto> listOfDtos = Lists.newArrayList(new MyDto("a", 1, true), new MyDto("bc", 3, false));
        final String jsonArray = mapper.writeValueAsString(listOfDtos);
        // [{"stringValue":"a","intValue":1,"booleanValue":true},{"stringValue":"bc","intValue":3,"booleanValue":false}]
        final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, MyDto.class);
        final List<MyDto> asList = mapper.readValue(jsonArray, javaType);
        Assert.assertThat(asList.get(0), Matchers.instanceOf(MyDto.class));
    }
}

/**
 * a (private) no-args constructor is required (simulate without)
 */

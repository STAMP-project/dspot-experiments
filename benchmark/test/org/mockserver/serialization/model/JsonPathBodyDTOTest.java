package org.mockserver.serialization.model;


import Body.Type.JSON_PATH;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.JsonPathBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonPathBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        JsonPathBodyDTO xpathBody = new JsonPathBodyDTO(new JsonPathBody("some_body"));
        // then
        MatcherAssert.assertThat(xpathBody.getJsonPath(), Is.is("some_body"));
        MatcherAssert.assertThat(xpathBody.getType(), Is.is(JSON_PATH));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        JsonPathBody jsonPathBody = buildObject();
        // then
        MatcherAssert.assertThat(jsonPathBody.getValue(), Is.is("some_body"));
        MatcherAssert.assertThat(jsonPathBody.getType(), Is.is(JSON_PATH));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(JsonPathBody.jsonPath("some_body"), Is.is(new JsonPathBody("some_body")));
    }
}


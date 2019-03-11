package org.mockserver.serialization.model;


import Body.Type.BINARY;
import javax.xml.bind.DatatypeConverter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class BinaryBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBodyDTO binaryBody = new BinaryBodyDTO(new BinaryBody(body));
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(DatatypeConverter.printBase64Binary(body)));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // given
        byte[] body = DatatypeConverter.parseBase64Binary("some_body");
        // when
        BinaryBody binaryBody = buildObject();
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(body));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
    }

    @Test
    public void shouldHandleNull() {
        // given
        byte[] body = null;
        // when
        BinaryBody binaryBody = buildObject();
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(new byte[0]));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
    }

    @Test
    public void shouldHandleEmptyByteArray() {
        // given
        byte[] body = new byte[0];
        // when
        BinaryBody binaryBody = buildObject();
        // then
        MatcherAssert.assertThat(binaryBody.getValue(), Is.is(new byte[0]));
        MatcherAssert.assertThat(binaryBody.getType(), Is.is(BINARY));
    }
}


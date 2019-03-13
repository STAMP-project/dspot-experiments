/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign;


import feign.codec.Decoder;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void removesEmptyStrings() {
        String[] values = new String[]{ "", null };
        assertThat(Util.removeValues(values, ( value) -> (emptyToNull(value)) == null, String.class)).isEmpty();
    }

    @Test
    public void removesEvenNumbers() {
        Integer[] values = new Integer[]{ 22, 23 };
        assertThat(Util.removeValues(values, ( number) -> (number % 2) == 0, Integer.class)).containsExactly(23);
    }

    @Test
    public void emptyValueOf() throws Exception {
        Assert.assertEquals(false, Util.emptyValueOf(boolean.class));
        Assert.assertEquals(false, Util.emptyValueOf(Boolean.class));
        assertThat(((byte[]) (Util.emptyValueOf(byte[].class)))).isEmpty();
        Assert.assertEquals(Collections.emptyList(), Util.emptyValueOf(Collection.class));
        assertThat(((Iterator<?>) (Util.emptyValueOf(Iterator.class)))).isEmpty();
        Assert.assertEquals(Collections.emptyList(), Util.emptyValueOf(List.class));
        Assert.assertEquals(Collections.emptyMap(), Util.emptyValueOf(Map.class));
        Assert.assertEquals(Collections.emptySet(), Util.emptyValueOf(Set.class));
        Assert.assertEquals(Optional.empty(), Util.emptyValueOf(Optional.class));
    }

    /**
     * In other words, {@code List<String>} is as empty as {@code List<?>}.
     */
    @Test
    public void emptyValueOf_considersRawType() throws Exception {
        Type listStringType = UtilTest.LastTypeParameter.class.getDeclaredField("LIST_STRING").getGenericType();
        assertThat(((List<?>) (Util.emptyValueOf(listStringType)))).isEmpty();
    }

    /**
     * Ex. your {@code Foo} object would be null, but so would things like Number.
     */
    @Test
    public void emptyValueOf_nullForUndefined() throws Exception {
        assertThat(Util.emptyValueOf(Number.class)).isNull();
        assertThat(Util.emptyValueOf(UtilTest.Parameterized.class)).isNull();
    }

    @Test
    public void resolveLastTypeParameterWhenNotSubtype() throws Exception {
        Type context = UtilTest.LastTypeParameter.class.getDeclaredField("PARAMETERIZED_LIST_STRING").getGenericType();
        Type listStringType = UtilTest.LastTypeParameter.class.getDeclaredField("LIST_STRING").getGenericType();
        Type last = Util.resolveLastTypeParameter(context, UtilTest.Parameterized.class);
        Assert.assertEquals(listStringType, last);
    }

    @Test
    public void lastTypeFromInstance() throws Exception {
        UtilTest.Parameterized<?> instance = new UtilTest.ParameterizedSubtype();
        Type last = Util.resolveLastTypeParameter(instance.getClass(), UtilTest.Parameterized.class);
        Assert.assertEquals(String.class, last);
    }

    @Test
    public void lastTypeFromAnonymous() throws Exception {
        UtilTest.Parameterized<?> instance = new UtilTest.Parameterized<Reader>() {};
        Type last = Util.resolveLastTypeParameter(instance.getClass(), UtilTest.Parameterized.class);
        Assert.assertEquals(Reader.class, last);
    }

    @Test
    public void resolveLastTypeParameterWhenWildcard() throws Exception {
        Type context = UtilTest.LastTypeParameter.class.getDeclaredField("PARAMETERIZED_WILDCARD_LIST_STRING").getGenericType();
        Type listStringType = UtilTest.LastTypeParameter.class.getDeclaredField("LIST_STRING").getGenericType();
        Type last = Util.resolveLastTypeParameter(context, UtilTest.Parameterized.class);
        Assert.assertEquals(listStringType, last);
    }

    @Test
    public void resolveLastTypeParameterWhenParameterizedSubtype() throws Exception {
        Type context = UtilTest.LastTypeParameter.class.getDeclaredField("PARAMETERIZED_DECODER_LIST_STRING").getGenericType();
        Type listStringType = UtilTest.LastTypeParameter.class.getDeclaredField("LIST_STRING").getGenericType();
        Type last = Util.resolveLastTypeParameter(context, UtilTest.ParameterizedDecoder.class);
        Assert.assertEquals(listStringType, last);
    }

    @Test
    public void unboundWildcardIsObject() throws Exception {
        Type context = UtilTest.LastTypeParameter.class.getDeclaredField("PARAMETERIZED_DECODER_UNBOUND").getGenericType();
        Type last = Util.resolveLastTypeParameter(context, UtilTest.ParameterizedDecoder.class);
        Assert.assertEquals(Object.class, last);
    }

    @Test
    public void checkArgumentInputFalseNotNullNullOutputIllegalArgumentException() {
        // Arrange
        final boolean expression = false;
        final String errorMessageTemplate = "";
        final Object[] errorMessageArgs = null;
        // Act
        thrown.expect(IllegalArgumentException.class);
        Util.checkArgument(expression, errorMessageTemplate, errorMessageArgs);
        // Method is not expected to return due to exception thrown
    }

    @Test
    public void checkNotNullInputNullNotNullNullOutputNullPointerException() {
        // Arrange
        final Object reference = null;
        final String errorMessageTemplate = "";
        final Object[] errorMessageArgs = null;
        // Act
        thrown.expect(NullPointerException.class);
        Util.checkNotNull(reference, errorMessageTemplate, errorMessageArgs);
        // Method is not expected to return due to exception thrown
    }

    @Test
    public void checkNotNullInputZeroNotNull0OutputZero() {
        // Arrange
        final Object reference = 0;
        final String errorMessageTemplate = "   ";
        final Object[] errorMessageArgs = new Object[]{  };
        // Act
        final Object retval = Util.checkNotNull(reference, errorMessageTemplate, errorMessageArgs);
        // Assert result
        Assert.assertEquals(new Integer(0), retval);
    }

    @Test
    public void checkStateInputFalseNotNullNullOutputIllegalStateException() {
        // Arrange
        final boolean expression = false;
        final String errorMessageTemplate = "";
        final Object[] errorMessageArgs = null;
        // Act
        thrown.expect(IllegalStateException.class);
        Util.checkState(expression, errorMessageTemplate, errorMessageArgs);
        // Method is not expected to return due to exception thrown
    }

    @Test
    public void emptyToNullInputNotNullOutputNotNull() {
        // Arrange
        final String string = "AAAAAAAA";
        // Act
        final String retval = Util.emptyToNull(string);
        // Assert result
        Assert.assertEquals("AAAAAAAA", retval);
    }

    @Test
    public void emptyToNullInputNullOutputNull() {
        // Arrange
        final String string = null;
        // Act
        final String retval = Util.emptyToNull(string);
        // Assert result
        Assert.assertNull(retval);
    }

    @Test
    public void isBlankInputNotNullOutputFalse() {
        // Arrange
        final String value = "AAAAAAAA";
        // Act
        final boolean retval = Util.isBlank(value);
        // Assert result
        Assert.assertEquals(false, retval);
    }

    @Test
    public void isBlankInputNullOutputTrue() {
        // Arrange
        final String value = null;
        // Act
        final boolean retval = Util.isBlank(value);
        // Assert result
        Assert.assertEquals(true, retval);
    }

    @Test
    public void isNotBlankInputNotNullOutputFalse() {
        // Arrange
        final String value = "";
        // Act
        final boolean retval = Util.isNotBlank(value);
        // Assert result
        Assert.assertEquals(false, retval);
    }

    @Test
    public void isNotBlankInputNotNullOutputTrue() {
        // Arrange
        final String value = "AAAAAAAA";
        // Act
        final boolean retval = Util.isNotBlank(value);
        // Assert result
        Assert.assertEquals(true, retval);
    }

    interface LastTypeParameter {
        final List<String> LIST_STRING = null;

        final UtilTest.Parameterized<List<String>> PARAMETERIZED_LIST_STRING = null;

        final UtilTest.Parameterized<? extends List<String>> PARAMETERIZED_WILDCARD_LIST_STRING = null;

        final UtilTest.ParameterizedDecoder<List<String>> PARAMETERIZED_DECODER_LIST_STRING = null;

        final UtilTest.ParameterizedDecoder<?> PARAMETERIZED_DECODER_UNBOUND = null;
    }

    interface ParameterizedDecoder<T extends List<String>> extends Decoder {}

    interface Parameterized<T> {}

    static class ParameterizedSubtype implements UtilTest.Parameterized<String> {}
}


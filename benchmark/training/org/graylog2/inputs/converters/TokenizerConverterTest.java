/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.converters;


import java.util.HashMap;
import java.util.Map;
import org.graylog2.plugin.inputs.Converter;
import org.junit.Assert;
import org.junit.Test;


public class TokenizerConverterTest {
    @Test
    public void testConvert() throws Exception {
        Converter hc = new TokenizerConverter(new HashMap<String, Object>());
        Assert.assertNull(hc.convert(null));
        Assert.assertEquals("", hc.convert(""));
    }

    @Test
    public void testBasic() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("Ohai I am a message k1=v1 k2=v2 Awesome!")));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("v1", result.get("k1"));
        Assert.assertEquals("v2", result.get("k2"));
    }

    @Test
    public void testFilterWithKVAtBeginning() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("k1=v1 k2=v2 Awesome!")));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("v1", result.get("k1"));
        Assert.assertEquals("v2", result.get("k2"));
    }

    @Test
    public void testFilterWithKVAtEnd() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("lolwat Awesome! k1=v1")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("v1", result.get("k1"));
    }

    @Test
    public void testFilterWithStringInBetween() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("foo k2=v2 lolwat Awesome! k1=v1")));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("v1", result.get("k1"));
        Assert.assertEquals("v2", result.get("k2"));
    }

    @Test
    public void testFilterWithKVOnly() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("k1=v1")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("v1", result.get("k1"));
    }

    @Test
    public void testFilterWithInvalidKVPairs() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("Ohai I am a message and this is a URL: index.php?foo=bar&baz=bar")));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFilterWithoutKVPairs() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("trolololololol")));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFilterWithOneInvalidKVPair() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("Ohai I am a message and this is a URL: index.php?foo=bar")));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFilterWithWhitespaceAroundKVNoException() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("k1 = ")));
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFilterWithWhitespaceAroundKV() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1 = v1 k2= v2 k3 =v3 k4=v4 more otters")));
        Assert.assertEquals(4, result.size());
        Assert.assertEquals("v1", result.get("k1"));
        Assert.assertEquals("v2", result.get("k2"));
        Assert.assertEquals("v3", result.get("k3"));
        Assert.assertEquals("v4", result.get("k4"));
    }

    @Test
    public void testFilterWithNewlineBetweenKV() {
        final TokenizerConverter f = new TokenizerConverter(new HashMap());
        @SuppressWarnings("unchecked")
        final Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1 = v1\nk2= v2 more otters")));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("v1", result.get("k1"));
        Assert.assertEquals("v2", result.get("k2"));
    }

    @Test
    public void testFilterWithQuotedValue() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1=\"v1\" more otters")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("v1", result.get("k1"));
    }

    @Test
    public void testFilterWithSingleQuotedValue() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1='v1' more otters")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("v1", result.get("k1"));
    }

    @Test
    public void testFilterWithIDAdditionalField() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters _id=123 more otters")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("123", result.get("_id"));
    }

    @Test
    public void testFilterWithMixedQuotedAndPlainValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1=\"v1\" k2=v2 more otters")));
        assertThat(result).hasSize(2).containsEntry("k1", "v1").containsEntry("k2", "v2");
    }

    @Test
    public void testFilterWithMixedSingleQuotedAndPlainValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1='v1' k2=v2 more otters")));
        assertThat(result).hasSize(2).containsEntry("k1", "v1").containsEntry("k2", "v2");
    }

    @Test
    public void testFilterWithKeysIncludingDashOrUnderscore() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k-1=v1 k_2=v2 _k3=v3 more otters")));
        assertThat(result).hasSize(3).containsEntry("k-1", "v1").containsEntry("k_2", "v2").containsEntry("_k3", "v3");
    }

    @Test
    public void testFilterRetainsWhitespaceInQuotedValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1= v1  k2=\" v2\" k3=\" v3 \" more otters")));
        assertThat(result).hasSize(3).containsEntry("k1", "v1").containsEntry("k2", " v2").containsEntry("k3", " v3 ");
    }

    @Test
    public void testFilterRetainsWhitespaceInSingleQuotedValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1= v1  k2=' v2' k3=' v3 ' more otters")));
        assertThat(result).hasSize(3).containsEntry("k1", "v1").containsEntry("k2", " v2").containsEntry("k3", " v3 ");
    }

    @Test
    public void testFilterRetainsNestedSingleQuotesInDoubleQuotedValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1= v1  k2=\" \'v2\'\" k3=\" \'v3\' \" more otters")));
        assertThat(result).hasSize(3).containsEntry("k1", "v1").containsEntry("k2", " 'v2'").containsEntry("k3", " 'v3' ");
    }

    @Test
    public void testFilterRetainsNestedDoubleQuotesInSingleQuotedValues() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("otters in k1= v1  k2=\' \"v2\"\' k3=\' \"v3\" \' more otters")));
        assertThat(result).hasSize(3).containsEntry("k1", "v1").containsEntry("k2", " \"v2\"").containsEntry("k3", " \"v3\" ");
    }

    @Test
    public void testFilterSupportsMultipleIdenticalKeys() {
        TokenizerConverter f = new TokenizerConverter(new HashMap<String, Object>());
        @SuppressWarnings("unchecked")
        Map<String, String> result = ((Map<String, String>) (f.convert("Ohai I am a message k1=v1 k1=v2 Awesome!")));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("v2", result.get("k1"));
    }
}


package org.wordpress.android.editor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UtilsTest {
    @Test
    public void testEscapeHtml() {
        // Test null
        Assert.assertEquals(null, Utils.escapeHtml(null));
    }

    @Test
    public void testDecodeHtml() {
        // Test null
        Assert.assertEquals(null, Utils.decodeHtml(null));
        // Test normal usage
        Assert.assertEquals("http://www.wordpress.com/", Utils.decodeHtml("http%3A%2F%2Fwww.wordpress.com%2F"));
    }

    @Test
    public void testSplitDelimitedString() {
        Set<String> splitString = new HashSet<>();
        // Test normal usage
        splitString.add("p");
        splitString.add("bold");
        splitString.add("justifyLeft");
        Assert.assertEquals(splitString, Utils.splitDelimitedString("p~bold~justifyLeft", "~"));
        // Test empty string
        Assert.assertEquals(Collections.emptySet(), Utils.splitDelimitedString("", "~"));
    }

    @Test
    public void testSplitValuePairDelimitedString() {
        // Test usage with a URL containing the delimiter
        Set<String> keyValueSet = new HashSet<>();
        keyValueSet.add("url=http://www.wordpress.com/~user");
        keyValueSet.add("title=I'm a link!");
        List<String> identifiers = new ArrayList<>();
        identifiers.add("url");
        identifiers.add("title");
        Assert.assertEquals(keyValueSet, Utils.splitValuePairDelimitedString("url=http://www.wordpress.com/~user~title=I'm a link!", "~", identifiers));
        // Test usage with a matching identifier but no delimiters
        keyValueSet.clear();
        keyValueSet.add("url=http://www.wordpress.com/");
        Assert.assertEquals(keyValueSet, Utils.splitValuePairDelimitedString("url=http://www.wordpress.com/", "~", identifiers));
        // Test usage with no matching identifier and no delimiters
        keyValueSet.clear();
        keyValueSet.add("something=something else");
        Assert.assertEquals(keyValueSet, Utils.splitValuePairDelimitedString("something=something else", "~", identifiers));
    }

    @Test
    public void testBuildMapFromKeyValuePairs() {
        Set<String> keyValueSet = new HashSet<>();
        Map<String, String> expectedMap = new HashMap<>();
        // Test normal usage
        keyValueSet.add("id=test");
        keyValueSet.add("name=example");
        expectedMap.put("id", "test");
        expectedMap.put("name", "example");
        Assert.assertEquals(expectedMap, Utils.buildMapFromKeyValuePairs(keyValueSet));
        // Test mixed valid and invalid entries
        keyValueSet.clear();
        keyValueSet.add("test");
        keyValueSet.add("name=example");
        expectedMap.clear();
        expectedMap.put("name", "example");
        Assert.assertEquals(expectedMap, Utils.buildMapFromKeyValuePairs(keyValueSet));
        // Test multiple '=' (should split at the first `=` and treat the rest of them as part of the string)
        keyValueSet.clear();
        keyValueSet.add("id=test");
        keyValueSet.add("contents=some text\n<a href=\"http://wordpress.com\">WordPress</a>");
        expectedMap.clear();
        expectedMap.put("id", "test");
        expectedMap.put("contents", "some text\n<a href=\"http://wordpress.com\">WordPress</a>");
        Assert.assertEquals(expectedMap, Utils.buildMapFromKeyValuePairs(keyValueSet));
        // Test invalid entry
        keyValueSet.clear();
        keyValueSet.add("test");
        Assert.assertEquals(Collections.emptyMap(), Utils.buildMapFromKeyValuePairs(keyValueSet));
        // Test empty sets
        Assert.assertEquals(Collections.emptyMap(), Utils.buildMapFromKeyValuePairs(Collections.<String>emptySet()));
    }

    @Test
    public void testGetChangeMapFromSets() {
        Set<String> oldSet = new HashSet<>();
        Set<String> newSet = new HashSet<>();
        Map<String, Boolean> expectedMap = new HashMap<>();
        // Test normal usage
        oldSet.add("p");
        oldSet.add("bold");
        oldSet.add("justifyLeft");
        newSet.add("p");
        newSet.add("justifyRight");
        expectedMap.put("bold", false);
        expectedMap.put("justifyLeft", false);
        expectedMap.put("justifyRight", true);
        Assert.assertEquals(expectedMap, Utils.getChangeMapFromSets(oldSet, newSet));
        // Test no changes
        oldSet.clear();
        oldSet.add("p");
        oldSet.add("bold");
        newSet.clear();
        newSet.add("p");
        newSet.add("bold");
        Assert.assertEquals(Collections.emptyMap(), Utils.getChangeMapFromSets(oldSet, newSet));
        // Test empty sets
        Assert.assertEquals(Collections.emptyMap(), Utils.getChangeMapFromSets(Collections.emptySet(), Collections.emptySet()));
    }

    @Test
    public void testClipboardUrlWithNullContext() {
        Assert.assertNull(Utils.getUrlFromClipboard(null));
    }

    @Test
    public void testClipboardUrlWithNoClipData() {
        Assert.assertNull(getClipboardUrlHelper(0, null));
    }

    @Test
    public void testClipboardUrlWithNonUriData() {
        Assert.assertNull(getClipboardUrlHelper(1, "not a URL"));
    }

    @Test
    public void testClipboardUrlWithLocalUriData() {
        Assert.assertNull(getClipboardUrlHelper(1, "file://test.png"));
    }

    @Test
    public void testClipboardWithUrlData() {
        String testUrl = "google.com";
        Assert.assertEquals(testUrl, getClipboardUrlHelper(1, testUrl));
    }
}


package org.mockserver.model;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HeadersTest {
    @Test
    public void shouldBuildHeader() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        Header header = headers.build(NottableString.string("name"), Arrays.asList(NottableString.string("value_one"), NottableString.string("value_two")));
        // then
        Assert.assertThat(header, Is.is(new Header(NottableString.string("name"), NottableString.string("value_one"), NottableString.string("value_two"))));
    }

    @Test
    public void shouldAddEntriesAsHeaderVarargs() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        headers.withEntries(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two"));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldAddEntriesAsHeaderList() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        headers.withEntries(Arrays.asList(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldAddEntriesAsMap() throws Exception {
        // given
        Headers headers = new Headers();
        Map<String, List<String>> entries = new LinkedHashMap<>();
        entries.put("name_one", Arrays.asList("value_one_one", "value_one_two"));
        entries.put("name_two", Arrays.asList("value_two_one", "value_two_two"));
        entries.put("name_three", Arrays.asList("value_three_one", "value_three_two"));
        // when
        headers.withEntries(entries);
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldAddEntryAsCookie() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldAddEntryAsNameAndValueString() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        headers.withEntry("name_one", "value_one_one", "value_one_two");
        headers.withEntry("name_two", "value_two_one", "value_two_two");
        headers.withEntry("name_three", "value_three_one", "value_three_two");
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "value_two_one", "value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldAddEntryAsNameAndValueNottableString() throws Exception {
        // given
        Headers headers = new Headers();
        // when
        headers.withEntry(NottableString.string("name_one"), NottableString.not("value_one_one"), NottableString.not("value_one_two"));
        headers.withEntry(NottableString.not("name_two"), NottableString.string("value_two_one"), NottableString.string("value_two_two"));
        headers.withEntry(NottableString.string("name_three"), NottableString.string("value_three_one"), NottableString.string("value_three_two"));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header(NottableString.string("name_one"), NottableString.not("value_one_one"), NottableString.not("value_one_two")), Header.header(NottableString.not("name_two"), NottableString.string("value_two_one"), NottableString.string("value_two_two")), Header.header(NottableString.string("name_three"), NottableString.string("value_three_one"), NottableString.string("value_three_two"))));
    }

    @Test
    public void shouldReplaceEntryWithHeader() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        headers.replaceEntry(Header.header("name_two", "new_value_two_one", "new_value_two_two"));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "new_value_two_one", "new_value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldReplaceEntryWithStrings() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        headers.replaceEntry("name_two", "new_value_two_one", "new_value_two_two");
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("name_two", "new_value_two_one", "new_value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldReplaceEntryWithHeaderIgnoringCase() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        headers.replaceEntry(Header.header("Name_Two", "new_value_two_one", "new_value_two_two"));
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("Name_Two", "new_value_two_one", "new_value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldReplaceEntryWithStringsIgnoringCase() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        headers.replaceEntry("Name_Two", "new_value_two_one", "new_value_two_two");
        // then
        Assert.assertThat(headers.getEntries().size(), Is.is(3));
        Assert.assertThat(headers.getEntries(), IsCollectionContaining.hasItems(Header.header("name_one", "value_one_one", "value_one_two"), Header.header("Name_Two", "new_value_two_one", "new_value_two_two"), Header.header("name_three", "value_three_one", "value_three_two")));
    }

    @Test
    public void shouldRetrieveEntryValues() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        List<String> values = headers.getValues("name_two");
        // then
        Assert.assertThat(values.size(), Is.is(2));
        Assert.assertThat(values, IsCollectionContaining.hasItems("value_two_one", "value_two_two"));
    }

    @Test
    public void shouldGetFirstEntry() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // when
        String value = headers.getFirstValue("name_two");
        // then
        Assert.assertThat(value, Is.is("value_two_one"));
    }

    @Test
    public void shouldContainEntryByKey() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // then
        Assert.assertTrue(headers.containsEntry("name_two"));
        TestCase.assertFalse(headers.containsEntry("name_other"));
    }

    @Test
    public void shouldContainEntryByKeyAndValueString() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(Header.header("name_one", "value_one_one", "value_one_two"));
        headers.withEntry(Header.header("name_two", "value_two_one", "value_two_two"));
        headers.withEntry(Header.header("name_three", "value_three_one", "value_three_two"));
        // then
        Assert.assertTrue(headers.containsEntry("name_two", "value_two_one"));
        Assert.assertTrue(headers.containsEntry("name_two", "value_two_two"));
        TestCase.assertFalse(headers.containsEntry("name_other", "value_three_one"));
        TestCase.assertFalse(headers.containsEntry("name_three", "value_three_other"));
    }

    @Test
    public void shouldContainEntryByKeyAndValueNottableString() throws Exception {
        // given
        Headers headers = new Headers();
        headers.withEntry(NottableString.string("name_one"), NottableString.not("value_one_one"), NottableString.not("value_one_two"));
        headers.withEntry(NottableString.not("name_two"), NottableString.string("value_two_one"), NottableString.string("value_two_two"));
        headers.withEntry(NottableString.string("name_three"), NottableString.string("value_three_one"), NottableString.string("value_three_two"));
        headers.withEntry(NottableString.string("name_four"), NottableString.string("value_four_one"));
        // then
        // exact match, not key, string first value
        Assert.assertTrue(headers.containsEntry(NottableString.not("name_two"), NottableString.string("value_two_one")));
        // exact match, not key, string second value
        Assert.assertTrue(headers.containsEntry(NottableString.not("name_two"), NottableString.string("value_two_two")));
        // exact match, string key, not first value
        Assert.assertTrue(headers.containsEntry(NottableString.string("name_one"), NottableString.not("value_one_one")));
        // exact match, string key, string second value
        Assert.assertTrue(headers.containsEntry(NottableString.string("name_three"), NottableString.string("value_three_two")));
        // not value
        Assert.assertTrue(headers.containsEntry(NottableString.string("name_three"), NottableString.not("value_other")));
        // not key
        Assert.assertTrue(headers.containsEntry(NottableString.not("name_other"), NottableString.string("value_three_one")));
        // matches (matched "name_one" -> "!value_one_one")
        Assert.assertTrue(headers.containsEntry(NottableString.not("name_three"), NottableString.string("value_three_one")));
        // matches ("!name_two" -> "value_two_one")
        Assert.assertTrue(headers.containsEntry(NottableString.string("name_four"), NottableString.not("value_four_one")));
        // non-match name
        TestCase.assertFalse(headers.containsEntry(NottableString.string("name_other"), NottableString.string("value_three_one")));
        // non-match value
        TestCase.assertFalse(headers.containsEntry(NottableString.string("name_three"), NottableString.string("value_three_other")));
    }
}


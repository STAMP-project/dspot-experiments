/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.search.web.internal.suggestions.display.context;


import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Http;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;


/**
 *
 *
 * @author Adam Brandizzi
 * @author Andr? de Oliveira
 */
public class SuggestionsPortletDisplayBuilderTest {
    @Test
    public void testGetRelatedQueriesSuggestions() {
        List<SuggestionDisplayContext> suggestionDisplayContexts = buildRelatedQueriesSuggestions(Arrays.asList("alpha"));
        assertSuggestion("[alpha] | q=X(q<<alpha)", suggestionDisplayContexts.get(0));
    }

    @Test
    public void testGetRelatedQueriesSuggestionsEmptyByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        List<SuggestionDisplayContext> suggestionDisplayContexts = suggestionsPortletDisplayContext.getRelatedQueriesSuggestions();
        Assert.assertTrue(suggestionDisplayContexts.isEmpty());
    }

    @Test
    public void testGetRelatedQueriesSuggestionsMultiple() {
        setUpSearchedKeywords("q", "a b");
        List<SuggestionDisplayContext> suggestionDisplayContexts = buildRelatedQueriesSuggestions(Arrays.asList("a C", "a b C"));
        Assert.assertEquals(suggestionDisplayContexts.toString(), 2, suggestionDisplayContexts.size());
        assertSuggestion("a [C] | q=a b(q<<a C)", suggestionDisplayContexts.get(0));
        assertSuggestion("a b [C] | q=a b(q<<a b C)", suggestionDisplayContexts.get(1));
    }

    @Test
    public void testGetRelatedSuggestionsWithEmptyList() {
        setUpSearchedKeywords("q", "a b");
        List<SuggestionDisplayContext> suggestionDisplayContexts = buildRelatedQueriesSuggestions(Collections.emptyList());
        Assert.assertTrue(suggestionDisplayContexts.isEmpty());
    }

    @Test
    public void testGetRelatedSuggestionsWithKeywordsAsSuggestions() {
        setUpSearchedKeywords("q", "a b");
        List<SuggestionDisplayContext> suggestionDisplayContexts = buildRelatedQueriesSuggestions(Arrays.asList("a b", "a b C"));
        Assert.assertEquals(suggestionDisplayContexts.toString(), 1, suggestionDisplayContexts.size());
        assertSuggestion("a b [C] | q=a b(q<<a b C)", suggestionDisplayContexts.get(0));
    }

    @Test
    public void testGetRelatedSuggestionsWithNullSuggestions() {
        setUpSearchedKeywords("q", "a b");
        List<SuggestionDisplayContext> suggestionDisplayContexts = buildRelatedQueriesSuggestions(Arrays.asList(null, "", "a b C"));
        Assert.assertEquals(suggestionDisplayContexts.toString(), 1, suggestionDisplayContexts.size());
        assertSuggestion("a b [C] | q=a b(q<<a b C)", suggestionDisplayContexts.get(0));
    }

    @Test
    public void testGetSpellCheckSuggestion() {
        SuggestionDisplayContext suggestionDisplayContext = buildSpellCheckSuggestion("alpha");
        assertSuggestion("[alpha] | q=X(q<<alpha)", suggestionDisplayContext);
    }

    @Test
    public void testGetSpellCheckSuggestionEqualsToKeywords() {
        setUpSearchedKeywords("q", "a b");
        Assert.assertNull(buildSpellCheckSuggestion("a b"));
    }

    @Test
    public void testGetSpellCheckSuggestionFormatted() {
        setUpSearchedKeywords("q", "a b");
        SuggestionDisplayContext suggestionDisplayContext = buildSpellCheckSuggestion("a C");
        assertSuggestion("a [C] | q=a b(q<<a C)", suggestionDisplayContext);
    }

    @Test
    public void testGetSpellCheckSuggestionsNullByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertNull(suggestionsPortletDisplayContext.getSpellCheckSuggestion());
    }

    @Test
    public void testGetSpellCheckSuggestionWithEmptySuggestion() {
        setUpSearchedKeywords("q", "a b");
        Assert.assertNull(buildSpellCheckSuggestion(""));
    }

    @Test
    public void testGetSpellCheckSuggestionWithNullSuggestion() {
        setUpSearchedKeywords("q", "a b");
        Assert.assertNull(buildSpellCheckSuggestion(null));
    }

    @Test
    public void testHasRelatedQueriesSuggestionsFalseByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
    }

    @Test
    public void testHasRelatedSuggestionsFalseWithDisabledAndNonemptyList() {
        _displayBuilder.setRelatedQueriesSuggestions(Arrays.asList(RandomTestUtil.randomString()));
        _displayBuilder.setRelatedQueriesSuggestionsEnabled(false);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
    }

    @Test
    public void testHasRelatedSuggestionsFalseWithEnabledAndEmptyList() {
        _displayBuilder.setRelatedQueriesSuggestions(Collections.emptyList());
        _displayBuilder.setRelatedQueriesSuggestionsEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
    }

    @Test
    public void testHasRelatedSuggestionsTrueWithEnabledAndNonemptyList() {
        _displayBuilder.setRelatedQueriesSuggestions(Arrays.asList(RandomTestUtil.randomString()));
        _displayBuilder.setRelatedQueriesSuggestionsEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertTrue(suggestionsPortletDisplayContext.hasRelatedQueriesSuggestions());
    }

    @Test
    public void testHasSpellCheckSuggestionFalseByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
    }

    @Test
    public void testHasSpellCheckSuggestionsFalseWithDisabledAndSuggestion() {
        _displayBuilder.setSpellCheckSuggestion(RandomTestUtil.randomString());
        _displayBuilder.setSpellCheckSuggestionEnabled(false);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
    }

    @Test
    public void testHasSpellCheckSuggestionsFalseWithEnabledAndNull() {
        _displayBuilder.setSpellCheckSuggestion(null);
        _displayBuilder.setSpellCheckSuggestionEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
    }

    @Test
    public void testHasSpellCheckSuggestionsTrueWithEnabledAndNonemptyList() {
        _displayBuilder.setSpellCheckSuggestion(RandomTestUtil.randomString());
        _displayBuilder.setSpellCheckSuggestionEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertTrue(suggestionsPortletDisplayContext.hasSpellCheckSuggestion());
    }

    @Test
    public void testIsRelatedSuggestions() {
        _displayBuilder.setRelatedQueriesSuggestions(Arrays.asList(RandomTestUtil.randomString()));
        _displayBuilder.setRelatedQueriesSuggestionsEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertTrue(suggestionsPortletDisplayContext.isRelatedQueriesSuggestionsEnabled());
    }

    @Test
    public void testIsRelatedSuggestionsFalseByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.isRelatedQueriesSuggestionsEnabled());
    }

    @Test
    public void testIsSpellCheckSuggestionEnabled() {
        _displayBuilder.setSpellCheckSuggestionEnabled(true);
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertTrue(suggestionsPortletDisplayContext.isSpellCheckSuggestionEnabled());
    }

    @Test
    public void testIsSpellCheckSuggestionEnabledFalseByDefault() {
        SuggestionsPortletDisplayContext suggestionsPortletDisplayContext = _displayBuilder.build();
        Assert.assertFalse(suggestionsPortletDisplayContext.isSpellCheckSuggestionEnabled());
    }

    @Mock
    protected Html html;

    @Mock
    protected Http http;

    private static final String _URL_PREFIX = "http://localhost:8080/?";

    private SuggestionsPortletDisplayBuilder _displayBuilder;
}


/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.core.completion;


import CompletionSuggestion.Entry.Option;
import Fuzziness.AUTO;
import java.util.List;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Artur Konczak
 * @author Mewes Kochheim
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:elasticsearch-template-test.xml")
public class ElasticsearchTemplateCompletionTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldPutMappingForGivenEntity() throws Exception {
        // given
        Class entity = CompletionEntity.class;
        elasticsearchTemplate.createIndex(entity);
        // when
        MatcherAssert.assertThat(elasticsearchTemplate.putMapping(entity), is(true));
    }

    @Test
    public void shouldFindSuggestionsForGivenCriteriaQueryUsingCompletionEntity() {
        // given
        loadCompletionObjectEntities();
        SuggestionBuilder completionSuggestionFuzzyBuilder = SuggestBuilders.completionSuggestion("suggest").prefix("m", AUTO);
        // when
        final SearchResponse suggestResponse = elasticsearchTemplate.suggest(new SuggestBuilder().addSuggestion("test-suggest", completionSuggestionFuzzyBuilder), CompletionEntity.class);
        CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("test-suggest");
        List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getEntries().get(0).getOptions();
        // then
        MatcherAssert.assertThat(options.size(), is(2));
        MatcherAssert.assertThat(options.get(0).getText().string(), isOneOf("Marchand", "Mohsin"));
        MatcherAssert.assertThat(options.get(1).getText().string(), isOneOf("Marchand", "Mohsin"));
    }

    @Test
    public void shouldFindSuggestionsForGivenCriteriaQueryUsingAnnotatedCompletionEntity() {
        // given
        loadAnnotatedCompletionObjectEntities();
        SuggestionBuilder completionSuggestionFuzzyBuilder = SuggestBuilders.completionSuggestion("suggest").prefix("m", AUTO);
        // when
        final SearchResponse suggestResponse = elasticsearchTemplate.suggest(new SuggestBuilder().addSuggestion("test-suggest", completionSuggestionFuzzyBuilder), CompletionEntity.class);
        CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("test-suggest");
        List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getEntries().get(0).getOptions();
        // then
        MatcherAssert.assertThat(options.size(), is(2));
        MatcherAssert.assertThat(options.get(0).getText().string(), isOneOf("Marchand", "Mohsin"));
        MatcherAssert.assertThat(options.get(1).getText().string(), isOneOf("Marchand", "Mohsin"));
    }

    @Test
    public void shouldFindSuggestionsWithWeightsForGivenCriteriaQueryUsingAnnotatedCompletionEntity() {
        // given
        loadAnnotatedCompletionObjectEntitiesWithWeights();
        SuggestionBuilder completionSuggestionFuzzyBuilder = SuggestBuilders.completionSuggestion("suggest").prefix("m", AUTO);
        // when
        final SearchResponse suggestResponse = elasticsearchTemplate.suggest(new SuggestBuilder().addSuggestion("test-suggest", completionSuggestionFuzzyBuilder), AnnotatedCompletionEntity.class);
        CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("test-suggest");
        List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getEntries().get(0).getOptions();
        // then
        MatcherAssert.assertThat(options.size(), is(4));
        for (CompletionSuggestion.Entry.Option option : options) {
            if (option.getText().string().equals("Mewes Kochheim1")) {
                Assert.assertEquals(4, option.getScore(), 0);
            } else
                if (option.getText().string().equals("Mewes Kochheim2")) {
                    Assert.assertEquals(1, option.getScore(), 0);
                } else
                    if (option.getText().string().equals("Mewes Kochheim3")) {
                        Assert.assertEquals(2, option.getScore(), 0);
                    } else
                        if (option.getText().string().equals("Mewes Kochheim4")) {
                            Assert.assertEquals(Integer.MAX_VALUE, option.getScore(), 0);
                        } else {
                            Assert.fail("Unexpected option");
                        }



        }
    }
}


package sagan.questions.support;


import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import sagan.support.cache.CachedRestClient;


/**
 * Tests for {@link StackOverflowClient}.
 */
public class StackOverflowClientTests {
    @Test
    public void getQuestionsTagged() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        Questions expected = new Questions();
        Question question = new Question();
        question.id = "12";
        expected.items = Collections.singletonList(question);
        BDDMockito.given(mockRestTemplate.getForObject(ArgumentMatchers.anyString(), ArgumentMatchers.any())).willReturn(expected);
        StackOverflowClient client = new StackOverflowClient(new CachedRestClient(), mockRestTemplate);
        List<Question> actual = client.searchForQuestionsTagged("spring-data-mongodb", "spring-data-neo4j");
        Assert.assertThat(actual, Matchers.contains(question));
        BDDMockito.then(mockRestTemplate).should(Mockito.times(1)).getForObject("https://api.stackexchange.com/2.2/search?filter=withbody&site=stackoverflow&tagged=spring-data-mongodb;spring-data-neo4j", Questions.class);
    }
}


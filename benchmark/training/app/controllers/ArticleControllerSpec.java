package app.controllers;


import org.javalite.activeweb.ControllerSpec;
import org.junit.Test;


public class ArticleControllerSpec extends ControllerSpec {
    @Test
    public void whenReturnedArticlesThenCorrect() {
        request().get("index");
        a(responseContent()).shouldContain("<td>Introduction to Mule</td>");
    }

    @Test
    public void givenKeywordWhenFoundArticleThenCorrect() {
        request().param("key", "Java").get("search");
        a(responseContent()).shouldContain("<td>Article with Java</td>");
    }
}


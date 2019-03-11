package io.github.izzyleung;


import io.github.izzyleung.ZhihuDailyPurify.News;
import io.reactivex.subscribers.TestSubscriber;
import java.io.IOException;
import org.junit.Test;


public class ZhihuDailyOfficial_NewsFromTest {
    private static final String date = "Date";

    private static final String storyTitle = "Story Title";

    private static final String thumbnailUrl = "Thumbnail URL";

    private ZhihuDailyOfficial official;

    private Story story;

    private TestSubscriber<News> newsObserver;

    @Test
    public void testNoQuestions() throws IOException {
        story = setUpStory("html/no_questions.html");
        official.newsFrom(story).subscribe(newsObserver);
        newsObserver.assertNoValues();
        newsObserver.assertComplete();
    }

    @Test
    public void testNoQuestionTitle() throws IOException {
        story = setUpStory("html/no_title.html");
        official.newsFrom(story).subscribe(newsObserver);
        // When question has no title in details page, default to title of the story it associates with.
        newsObserver.assertValue(( news) -> news.getQuestions(0).getTitle().equals(storyTitle));
        newsObserver.assertComplete();
    }

    @Test
    public void testEmptyQuestionTitle() throws IOException {
        story = setUpStory("html/empty_question_title.html");
        official.newsFrom(story).subscribe(newsObserver);
        // When question has empty title in details page, default to title of the story it associates with.
        newsObserver.assertValue(( news) -> news.getQuestions(0).getTitle().equals(storyTitle));
        newsObserver.assertComplete();
    }

    @Test
    public void testNoQuestionUrl() throws IOException {
        story = setUpStory("html/no_question_url.html");
        official.newsFrom(story).subscribe(newsObserver);
        newsObserver.assertNoValues();
        newsObserver.assertComplete();
    }

    @Test
    public void testInvalidQuestionUrl() throws IOException {
        story = setUpStory("html/invalid_question_url.html");
        official.newsFrom(story).subscribe(newsObserver);
        newsObserver.assertNoValues();
        newsObserver.assertComplete();
    }

    @Test
    public void testNormalScenario() throws IOException {
        story = setUpStory("html/normal.html");
        official.newsFrom(story).subscribe(newsObserver);
        newsObserver.assertValue(( news) -> {
            boolean sizeMatch = (news.getQuestionsCount()) == 2;
            ZhihuDailyPurify.Question first = news.getQuestions(0);
            boolean firstQuestionMatch = (first.getTitle().equals("First")) && (first.getUrl().endsWith("1234567"));
            ZhihuDailyPurify.Question second = news.getQuestions(1);
            boolean secondQuestionMatch = (second.getTitle().equals("Second")) && (second.getUrl().endsWith("2345678"));
            return (sizeMatch && firstQuestionMatch) && secondQuestionMatch;
        });
        newsObserver.assertComplete();
    }
}


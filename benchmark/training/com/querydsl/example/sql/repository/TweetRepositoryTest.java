package com.querydsl.example.sql.repository;


import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.User;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import tweet.content;


public class TweetRepositoryTest extends AbstractPersistenceTest {
    @Inject
    private TweetRepository repository;

    @Inject
    private UserRepository userRepository;

    private Long posterId;

    @Test
    public void save_and_find_by_id() {
        String content = "I am alive! #YOLO";
        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setPosterId(posterId);
        Long id = repository.save(tweet);
        Assert.assertEquals(content, repository.findById(id).getContent());
    }

    @Test
    public void save_and_find_by_username() {
        String content = "I am alive! #YOLO";
        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setPosterId(posterId);
        repository.save(tweet);
        Assert.assertFalse(repository.findOfUser("dr_frank").isEmpty());
    }

    @Test
    public void save_with_mentions() {
        User other = new User();
        other.setUsername("dexter");
        Long otherId = userRepository.save(other);
        String content = "I am alive! #YOLO";
        Tweet tweet = new Tweet();
        tweet.setContent(content);
        tweet.setPosterId(posterId);
        Long tweetId = repository.save(tweet, otherId);
        Assert.assertEquals(tweetId, repository.findWithMentioned(otherId).get(0).getId());
    }

    @Test
    public void find_list_by_predicate() {
        Tweet tw1 = new Tweet();
        tw1.setPosterId(posterId);
        tw1.setContent("It is a alive! #YOLO");
        repository.save(tw1);
        Tweet tw2 = new Tweet();
        tw2.setPosterId(posterId);
        tw2.setContent("Oh the humanity!");
        repository.save(tw2);
        Tweet tw3 = new Tweet();
        tw3.setPosterId(posterId);
        tw3.setContent("#EpicFail");
        repository.save(tw3);
        Assert.assertEquals(1, repository.findAll(content.contains("#YOLO")).size());
    }
}


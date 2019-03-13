package com.querydsl.example.jpa.repository;


import com.google.common.collect.Lists;
import com.querydsl.example.jpa.model.Tweet;
import com.querydsl.example.jpa.model.User;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import tweet.content;
import tweet.mentions;


public class TweetRepositoryTest extends AbstractPersistenceTest {
    @Inject
    private TweetRepository repository;

    @Inject
    private UserRepository userRepository;

    @Test
    public void save_and_find_by_id() {
        User poster = new User("dr_frank");
        userRepository.save(poster);
        String content = "I am alive! #YOLO";
        Tweet tweet = new Tweet(poster, content, Collections.<User>emptyList(), null);
        repository.save(tweet);
        Assert.assertEquals(content, repository.findById(tweet.getId()).getContent());
    }

    @Test
    public void find_list_by_predicate() {
        User poster = new User("dr_frank");
        userRepository.save(poster);
        repository.save(new Tweet(poster, "It is a alive! #YOLO", Collections.<User>emptyList(), null));
        repository.save(new Tweet(poster, "Oh the humanity!", Collections.<User>emptyList(), null));
        repository.save(new Tweet(poster, "#EpicFail", Collections.<User>emptyList(), null));
        Assert.assertEquals(1, repository.findAll(content.contains("#YOLO")).size());
    }

    @Test
    public void find_list_by_complex_predicate() {
        List<String> usernames = Lists.newArrayList("dr_frank", "mike", "maggie", "liza");
        List<User> users = Lists.newArrayList();
        for (String username : usernames) {
            users.add(userRepository.save(new User(username)));
        }
        User poster = new User("duplo");
        userRepository.save(poster);
        for (int i = 0; i < 100; ++i) {
            repository.save(new Tweet(poster, ("spamming @dr_frank " + i), users.subList(0, 1), null));
        }
        Assert.assertTrue(repository.findAll(mentions.contains(users.get(1))).isEmpty());
        Assert.assertEquals(100, repository.findAll(mentions.contains(users.get(0))).size());
        Assert.assertTrue(repository.findAll(mentions.any().username.eq("duplo")).isEmpty());
        Assert.assertEquals(100, repository.findAll(mentions.any().username.eq("dr_frank")).size());
    }
}


package com.fishercoder;


import _355.Solution1.Twitter;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/10/17.
 */
public class _355Test {
    private static Twitter solution1Twitter;

    private static _355.Solution2.Twitter solution2Twitter;

    @Test
    public void test1() {
        _355Test.solution1Twitter.postTweet(1, 5);
        List<Integer> user1newsFeed = _355Test.solution1Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(5, ((int) (user1newsFeed.get(0))));
        _355Test.solution1Twitter.follow(1, 2);
        _355Test.solution1Twitter.postTweet(2, 6);
        user1newsFeed = _355Test.solution1Twitter.getNewsFeed(1);
        Assert.assertEquals(2, user1newsFeed.size());
        Assert.assertEquals(6, ((int) (user1newsFeed.get(0))));
        Assert.assertEquals(5, ((int) (user1newsFeed.get(1))));
        _355Test.solution1Twitter.unfollow(1, 2);
        user1newsFeed = _355Test.solution1Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
    }

    @Test
    public void test2() {
        _355Test.solution2Twitter.postTweet(1, 5);
        List<Integer> user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(5, ((int) (user1newsFeed.get(0))));
        _355Test.solution2Twitter.follow(1, 2);
        _355Test.solution2Twitter.postTweet(2, 6);
        user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(2, user1newsFeed.size());
        Assert.assertEquals(6, ((int) (user1newsFeed.get(0))));
        Assert.assertEquals(5, ((int) (user1newsFeed.get(1))));
        _355Test.solution2Twitter.unfollow(1, 2);
        user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
    }

    @Test
    public void test3() {
        _355Test.solution2Twitter.postTweet(1, 1);
        List<Integer> user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(1, ((int) (user1newsFeed.get(0))));
        _355Test.solution2Twitter.follow(2, 1);
        user1newsFeed = _355Test.solution2Twitter.getNewsFeed(2);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(1, ((int) (user1newsFeed.get(0))));
        _355Test.solution2Twitter.unfollow(2, 1);
        user1newsFeed = _355Test.solution2Twitter.getNewsFeed(2);
        Assert.assertEquals(0, user1newsFeed.size());
    }

    @Test
    public void test4() {
        _355Test.solution1Twitter.postTweet(1, 1);
        List<Integer> user1newsFeed = _355Test.solution1Twitter.getNewsFeed(1);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(1, ((int) (user1newsFeed.get(0))));
        _355Test.solution1Twitter.follow(2, 1);
        user1newsFeed = _355Test.solution1Twitter.getNewsFeed(2);
        Assert.assertEquals(1, user1newsFeed.size());
        Assert.assertEquals(1, ((int) (user1newsFeed.get(0))));
        _355Test.solution1Twitter.unfollow(2, 1);
        user1newsFeed = _355Test.solution1Twitter.getNewsFeed(2);
        Assert.assertEquals(0, user1newsFeed.size());
    }

    @Test
    public void test5() {
        List<Integer> user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(0, user1newsFeed.size());
    }

    @Test
    public void test6() {
        _355Test.solution2Twitter.follow(1, 5);
        List<Integer> user1newsFeed = _355Test.solution2Twitter.getNewsFeed(1);
        Assert.assertEquals(0, user1newsFeed.size());
    }
}


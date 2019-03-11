package com.baeldung.cloud.openfeign;


import com.baeldung.cloud.openfeign.model.Post;
import com.baeldung.cloud.openfeign.service.JSONPlaceHolderService;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenfeignUnitTest {
    @Autowired
    private JSONPlaceHolderService jsonPlaceHolderService;

    @Test
    public void whenGetPosts_thenListPostSizeGreaterThanZero() {
        List<Post> posts = jsonPlaceHolderService.getPosts();
        Assert.assertFalse(posts.isEmpty());
    }

    @Test
    public void whenGetPostWithId_thenPostExist() {
        Post post = jsonPlaceHolderService.getPostById(1L);
        Assert.assertNotNull(post);
    }
}


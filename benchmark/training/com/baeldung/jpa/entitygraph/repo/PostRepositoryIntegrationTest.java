package com.baeldung.jpa.entitygraph.repo;


import com.baeldung.jpa.entitygraph.model.Comment;
import com.baeldung.jpa.entitygraph.model.Post;
import com.baeldung.jpa.entitygraph.model.User;
import org.hibernate.LazyInitializationException;
import org.junit.Assert;
import org.junit.Test;


public class PostRepositoryIntegrationTest {
    private static PostRepository postRepository = null;

    @Test(expected = LazyInitializationException.class)
    public void find() {
        Post post = PostRepositoryIntegrationTest.postRepository.find(1L);
        Assert.assertNotNull(post.getUser());
        String email = post.getUser().getEmail();
        Assert.assertNull(email);
    }

    @Test
    public void findWithEntityGraph() {
        Post post = PostRepositoryIntegrationTest.postRepository.findWithEntityGraph(1L);
        Assert.assertNotNull(post.getUser());
        String email = post.getUser().getEmail();
        Assert.assertNotNull(email);
    }

    @Test(expected = LazyInitializationException.class)
    public void findWithEntityGraph_Comment_Without_User() {
        Post post = PostRepositoryIntegrationTest.postRepository.findWithEntityGraph(1L);
        Assert.assertNotNull(post.getUser());
        String email = post.getUser().getEmail();
        Assert.assertNotNull(email);
        Assert.assertNotNull(post.getComments());
        Assert.assertEquals(post.getComments().size(), 2);
        Comment comment = post.getComments().get(0);
        Assert.assertNotNull(comment);
        User user = comment.getUser();
        user.getEmail();
    }

    @Test
    public void findWithEntityGraph2_Comment_With_User() {
        Post post = PostRepositoryIntegrationTest.postRepository.findWithEntityGraph2(1L);
        Assert.assertNotNull(post.getComments());
        Assert.assertEquals(post.getComments().size(), 2);
        Comment comment = post.getComments().get(0);
        Assert.assertNotNull(comment);
        User user = comment.getUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getEmail(), "user2@test.com");
    }
}


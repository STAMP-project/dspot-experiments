/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.collections;


import com.querydsl.core.group.Group;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static QComment.comment;
import static QPost.post;
import static QUser.user;


public class GroupByTest {
    private static final List<User> users = Arrays.asList(new User("Bob"), new User("Jane"), new User("Jack"));

    private static final List<Post> posts = Arrays.asList(new Post(1, "Post 1", GroupByTest.users.get(0)), new Post(2, "Post 2", GroupByTest.users.get(0)), new Post(3, "Post 3", GroupByTest.users.get(1)));

    private static final List<Comment> comments = Arrays.asList(new Comment(1, "Comment 1", GroupByTest.users.get(0), GroupByTest.posts.get(0)), new Comment(2, "Comment 2", GroupByTest.users.get(1), GroupByTest.posts.get(1)), new Comment(3, "Comment 3", GroupByTest.users.get(2), GroupByTest.posts.get(1)), new Comment(4, "Comment 4", GroupByTest.users.get(0), GroupByTest.posts.get(2)), new Comment(5, "Comment 5", GroupByTest.users.get(1), GroupByTest.posts.get(2)), new Comment(6, "Comment 6", GroupByTest.users.get(2), GroupByTest.posts.get(2)));

    private static final QUser user = user;

    private static final QComment comment = comment;

    private static final QPost post = post;

    private static final ConstructorExpression<Comment> qComment = QComment.create(GroupByTest.comment.id, GroupByTest.comment.text);

    @Test
    public void group_min() {
        Map<Integer, String> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(min(GroupByTest.comment.text)));
        Assert.assertEquals("Comment 1", results.get(1));
        Assert.assertEquals("Comment 2", results.get(2));
        Assert.assertEquals("Comment 4", results.get(3));
    }

    @Test
    public void group_max() {
        Map<Integer, String> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(max(GroupByTest.comment.text)));
        Assert.assertEquals("Comment 1", results.get(1));
        Assert.assertEquals("Comment 3", results.get(2));
        Assert.assertEquals("Comment 6", results.get(3));
    }

    @Test
    public void group_sum() {
        Map<Integer, Integer> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(sum(GroupByTest.comment.id)));
        Assert.assertEquals(1, results.get(1).intValue());
        Assert.assertEquals(5, results.get(2).intValue());
        Assert.assertEquals(15, results.get(3).intValue());
    }

    @Test
    public void group_avg() {
        Map<Integer, Integer> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(avg(GroupByTest.comment.id)));
        Assert.assertEquals(1, results.get(1).intValue());
        Assert.assertEquals(2, results.get(2).intValue());
        Assert.assertEquals(5, results.get(3).intValue());
    }

    @Test
    public void group_order() {
        Map<Integer, Group> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(GroupByTest.post.name, set(GroupByTest.comment.id)));
        Assert.assertEquals(3, results.size());
    }

    @Test
    public void first_set_and_list() {
        Map<Integer, Group> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(GroupByTest.post.name, set(GroupByTest.comment.id), list(GroupByTest.comment.text)));
        Group group = results.get(1);
        Assert.assertEquals(toInt(1), group.getOne(GroupByTest.post.id));
        Assert.assertEquals("Post 1", group.getOne(GroupByTest.post.name));
        Assert.assertEquals(toSet(1), group.getSet(GroupByTest.comment.id));
        Assert.assertEquals(Arrays.asList("Comment 1"), group.getList(GroupByTest.comment.text));
    }

    // @Test(expected=NoSuchElementException.class)
    // public void noSuchElementException() {
    // Map<Integer, Group> results = BASIC_RESULTS.transform(
    // groupBy(postId, postName, set(commentId), list(commentText)));
    // 
    // Group group = results.get(1);
    // group.getSet(qComment);
    // }
    @Test(expected = ClassCastException.class)
    public void classCastException() {
        Map<Integer, Group> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(GroupByTest.post.name, set(GroupByTest.comment.id), list(GroupByTest.comment.text)));
        Group group = results.get(1);
        group.getList(GroupByTest.comment.id);
    }

    @Test
    public void array_access() {
        Map<Integer, Group> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(GroupByTest.post.name, set(GroupByTest.comment.id), list(GroupByTest.comment.text)));
        Group group = results.get(1);
        Object[] array = group.toArray();
        Assert.assertEquals(toInt(1), array[0]);
        Assert.assertEquals("Post 1", array[1]);
        Assert.assertEquals(toSet(1), array[2]);
        Assert.assertEquals(Arrays.asList("Comment 1"), array[3]);
    }

    @Test
    public void transform_results() {
        Map<Integer, Post> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(QPost.create(GroupByTest.post.id, GroupByTest.post.name, set(GroupByTest.qComment))));
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(1, post.getId());
        Assert.assertEquals("Post 1", post.getName());
        Assert.assertEquals(1, post.getComments().size());
    }

    @Test
    public void transform_as_bean() {
        Map<Integer, Post> results = CollQueryFactory.from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.comment.post.id.eq(GroupByTest.post.id)).transform(groupBy(GroupByTest.post.id).as(Projections.bean(Post.class, GroupByTest.post.id, GroupByTest.post.name, set(GroupByTest.qComment).as("comments"))));
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(1, post.getId());
        Assert.assertEquals("Post 1", post.getName());
        Assert.assertEquals(1, post.getComments().size());
    }

    @Test
    public void oneToOneToMany_projection() {
        Map<String, User> results = CollQueryFactory.from(GroupByTest.user, GroupByTest.users).from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.user.name.eq(GroupByTest.post.user.name), GroupByTest.post.id.eq(GroupByTest.comment.post.id)).transform(groupBy(GroupByTest.user.name).as(Projections.constructor(User.class, GroupByTest.user.name, QPost.create(GroupByTest.post.id, GroupByTest.post.name, set(GroupByTest.qComment)))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(3, post.getId());
        Assert.assertEquals("Post 3", post.getName());
        Assert.assertEquals(3, post.getComments().size());
    }

    @Test
    public void oneToOneToMany_projection_as_bean() {
        Map<String, User> results = CollQueryFactory.from(GroupByTest.user, GroupByTest.users).from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.user.name.eq(GroupByTest.post.user.name), GroupByTest.post.id.eq(GroupByTest.comment.post.id)).transform(groupBy(GroupByTest.user.name).as(Projections.bean(User.class, GroupByTest.user.name, Projections.bean(Post.class, GroupByTest.post.id, GroupByTest.post.name, set(GroupByTest.qComment).as("comments")).as("latestPost"))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(3, post.getId());
        Assert.assertEquals("Post 3", post.getName());
        Assert.assertEquals(3, post.getComments().size());
    }

    @Test
    public void oneToOneToMany_projection_as_bean_and_constructor() {
        Map<String, User> results = CollQueryFactory.from(GroupByTest.user, GroupByTest.users).from(GroupByTest.post, GroupByTest.posts).from(GroupByTest.comment, GroupByTest.comments).where(GroupByTest.user.name.eq(GroupByTest.post.user.name), GroupByTest.post.id.eq(GroupByTest.comment.post.id)).transform(groupBy(GroupByTest.user.name).as(Projections.bean(User.class, GroupByTest.user.name, QPost.create(GroupByTest.post.id, GroupByTest.post.name, set(GroupByTest.qComment)).as("latestPost"))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(3, post.getId());
        Assert.assertEquals("Post 3", post.getName());
        Assert.assertEquals(3, post.getComments().size());
    }
}


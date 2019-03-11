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
package com.querydsl.core.group;


import com.google.common.collect.Ordering;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;


public class GroupByMapTest extends AbstractGroupByTest {
    @Test
    public void compile() {
        StringExpression str = Expressions.stringPath("str");
        GroupExpression<String, String> strGroup = new GOne<String>(str);
        GroupBy.GroupBy.sortedMap(strGroup, str, null);
        GroupBy.GroupBy.sortedMap(str, strGroup, null);
    }

    @Test
    public void group_order() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId)));
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void set_by_sorted() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS_UNORDERED.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, sortedSet(AbstractGroupByTest.commentId)));
        Group group = results.get(1);
        Iterator<Integer> it = group.getSet(AbstractGroupByTest.commentId).iterator();
        Assert.assertEquals(1, it.next().intValue());
        Assert.assertEquals(2, it.next().intValue());
        Assert.assertEquals(3, it.next().intValue());
    }

    @Test
    public void set_by_sorted_reverse() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS_UNORDERED.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, sortedSet(AbstractGroupByTest.commentId, Ordering.natural().reverse())));
        Group group = results.get(1);
        Iterator<Integer> it = group.getSet(AbstractGroupByTest.commentId).iterator();
        Assert.assertEquals(3, it.next().intValue());
        Assert.assertEquals(2, it.next().intValue());
        Assert.assertEquals(1, it.next().intValue());
    }

    @Test
    public void first_set_and_list() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        Assert.assertEquals(toInt(1), group.getOne(AbstractGroupByTest.postId));
        Assert.assertEquals("post 1", group.getOne(AbstractGroupByTest.postName));
        Assert.assertEquals(toSet(1, 2, 3), group.getSet(AbstractGroupByTest.commentId));
        Assert.assertEquals(Arrays.asList("comment 1", "comment 2", "comment 3"), group.getList(AbstractGroupByTest.commentText));
    }

    @Test
    public void group_by_null() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        Group group = results.get(null);
        Assert.assertNull(group.getOne(AbstractGroupByTest.postId));
        Assert.assertEquals("null post", group.getOne(AbstractGroupByTest.postName));
        Assert.assertEquals(toSet(7, 8), group.getSet(AbstractGroupByTest.commentId));
        Assert.assertEquals(Arrays.asList("comment 7", "comment 8"), group.getList(AbstractGroupByTest.commentText));
    }

    @Test(expected = NoSuchElementException.class)
    public void noSuchElementException() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        group.getSet(AbstractGroupByTest.qComment);
    }

    @Test(expected = ClassCastException.class)
    public void classCastException() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        group.getList(AbstractGroupByTest.commentId);
    }

    @Test
    public void map1() {
        Map<Integer, Group> results = AbstractGroupByTest.MAP_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        Map<Integer, String> comments = group.getMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText);
        Assert.assertEquals(3, comments.size());
        Assert.assertEquals("comment 2", comments.get(2));
    }

    @Test
    public void map_sorted() {
        Map<Integer, Group> results = AbstractGroupByTest.MAP_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, sortedMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        Iterator<Map.Entry<Integer, String>> it = group.getMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText).entrySet().iterator();
        Assert.assertEquals(1, it.next().getKey().intValue());
        Assert.assertEquals(2, it.next().getKey().intValue());
        Assert.assertEquals(3, it.next().getKey().intValue());
    }

    @Test
    public void map_sorted_reverse() {
        Map<Integer, Group> results = AbstractGroupByTest.MAP_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, sortedMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText, Ordering.natural().reverse())));
        Group group = results.get(1);
        Iterator<Map.Entry<Integer, String>> it = group.getMap(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText).entrySet().iterator();
        Assert.assertEquals(3, it.next().getKey().intValue());
        Assert.assertEquals(2, it.next().getKey().intValue());
        Assert.assertEquals(1, it.next().getKey().intValue());
    }

    @Test
    public void map2() {
        Map<Integer, Map<Integer, String>> results = AbstractGroupByTest.MAP2_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText)));
        Map<Integer, String> comments = results.get(1);
        Assert.assertEquals(3, comments.size());
        Assert.assertEquals("comment 2", comments.get(2));
    }

    @Test
    public void map3() {
        Map<Integer, Map<Integer, Map<Integer, String>>> actual = AbstractGroupByTest.MAP3_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(map(AbstractGroupByTest.postId, map(AbstractGroupByTest.commentId, AbstractGroupByTest.commentText))));
        Map<Integer, Map<Integer, Map<Integer, String>>> expected = new LinkedHashMap<Integer, Map<Integer, Map<Integer, String>>>();
        for (Iterator<Tuple> iterator = AbstractGroupByTest.MAP3_RESULTS.iterate(); iterator.hasNext();) {
            Tuple tuple = iterator.next();
            Object[] array = tuple.toArray();
            Map<Integer, Map<Integer, String>> posts = expected.get(array[0]);
            if (posts == null) {
                posts = new LinkedHashMap<Integer, Map<Integer, String>>();
                expected.put(((Integer) (array[0])), posts);
            }
            @SuppressWarnings("unchecked")
            Pair<Integer, Pair<Integer, String>> pair = ((Pair<Integer, Pair<Integer, String>>) (array[1]));
            Integer first = pair.getFirst();
            Map<Integer, String> comments = posts.get(first);
            if (comments == null) {
                comments = new LinkedHashMap<Integer, String>();
                posts.put(first, comments);
            }
            Pair<Integer, String> second = pair.getSecond();
            comments.put(second.getFirst(), second.getSecond());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void map4() {
        Map<Integer, Map<Map<Integer, String>, String>> actual = AbstractGroupByTest.MAP4_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(map(map(AbstractGroupByTest.postId, AbstractGroupByTest.commentText), AbstractGroupByTest.postName)));
        Map<Integer, Map<Map<Integer, String>, String>> expected = new LinkedHashMap<Integer, Map<Map<Integer, String>, String>>();
        for (Iterator<Tuple> iterator = AbstractGroupByTest.MAP4_RESULTS.iterate(); iterator.hasNext();) {
            Tuple tuple = iterator.next();
            Object[] array = tuple.toArray();
            Map<Map<Integer, String>, String> comments = expected.get(array[0]);
            if (comments == null) {
                comments = new LinkedHashMap<Map<Integer, String>, String>();
                expected.put(((Integer) (array[0])), comments);
            }
            @SuppressWarnings("unchecked")
            Pair<Pair<Integer, String>, String> pair = ((Pair<Pair<Integer, String>, String>) (array[1]));
            Pair<Integer, String> first = pair.getFirst();
            Map<Integer, String> posts = Collections.singletonMap(first.getFirst(), first.getSecond());
            comments.put(posts, pair.getSecond());
        }
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void array_access() {
        Map<Integer, Group> results = AbstractGroupByTest.BASIC_RESULTS.transform(groupBy(AbstractGroupByTest.postId).as(AbstractGroupByTest.postName, set(AbstractGroupByTest.commentId), list(AbstractGroupByTest.commentText)));
        Group group = results.get(1);
        Object[] array = group.toArray();
        Assert.assertEquals(toInt(1), array[0]);
        Assert.assertEquals("post 1", array[1]);
        Assert.assertEquals(toSet(1, 2, 3), array[2]);
        Assert.assertEquals(Arrays.asList("comment 1", "comment 2", "comment 3"), array[3]);
    }

    @Test
    public void transform_results() {
        Map<Integer, Post> results = AbstractGroupByTest.POST_W_COMMENTS.transform(groupBy(AbstractGroupByTest.postId).as(Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment))));
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(toInt(1), post.getId());
        Assert.assertEquals("post 1", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(1), AbstractGroupByTest.comment(2), AbstractGroupByTest.comment(3)), post.getComments());
    }

    @Test
    public void transform_via_groupByProjection() {
        Map<Integer, Post> results = AbstractGroupByTest.POST_W_COMMENTS2.transform(new GroupByProjection<Integer, Post>(AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment)) {
            @Override
            protected Post transform(Group group) {
                return new Post(group.getOne(AbstractGroupByTest.postId), group.getOne(AbstractGroupByTest.postName), group.getSet(AbstractGroupByTest.qComment));
            }
        });
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(toInt(1), post.getId());
        Assert.assertEquals("post 1", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(1), AbstractGroupByTest.comment(2), AbstractGroupByTest.comment(3)), post.getComments());
    }

    @Test
    public void transform_as_bean() {
        Map<Integer, Post> results = AbstractGroupByTest.POST_W_COMMENTS.transform(groupBy(AbstractGroupByTest.postId).as(Projections.bean(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment).as("comments"))));
        Post post = results.get(1);
        Assert.assertNotNull(post);
        Assert.assertEquals(toInt(1), post.getId());
        Assert.assertEquals("post 1", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(1), AbstractGroupByTest.comment(2), AbstractGroupByTest.comment(3)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection() {
        Map<String, User> results = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).as(Projections.constructor(User.class, AbstractGroupByTest.userName, Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment)))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection_as_bean() {
        Map<String, User> results = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).as(Projections.bean(User.class, AbstractGroupByTest.userName, Projections.bean(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment).as("comments")).as("latestPost"))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }

    @Test
    public void oneToOneToMany_projection_as_bean_and_constructor() {
        Map<String, User> results = AbstractGroupByTest.USERS_W_LATEST_POST_AND_COMMENTS.transform(groupBy(AbstractGroupByTest.userName).as(Projections.bean(User.class, AbstractGroupByTest.userName, Projections.constructor(Post.class, AbstractGroupByTest.postId, AbstractGroupByTest.postName, set(AbstractGroupByTest.qComment)).as("latestPost"))));
        Assert.assertEquals(2, results.size());
        User user = results.get("Jane");
        Post post = user.getLatestPost();
        Assert.assertEquals(toInt(2), post.getId());
        Assert.assertEquals("post 2", post.getName());
        Assert.assertEquals(toSet(AbstractGroupByTest.comment(4), AbstractGroupByTest.comment(5)), post.getComments());
    }

    @Test
    public void signature() {
        StringPath str = Expressions.stringPath("str");
        NumberPath<BigDecimal> bigd = Expressions.numberPath(BigDecimal.class, "bigd");
        ResultTransformer<Map<String, SortedMap<BigDecimal, SortedMap<BigDecimal, Map<String, String>>>>> resultTransformer = GroupBy.GroupBy.groupBy(str).as(GroupBy.GroupBy.sortedMap(bigd, GroupBy.GroupBy.sortedMap(bigd, GroupBy.GroupBy.map(str, str), Ordering.natural().nullsLast()), Ordering.natural().nullsFirst()));
        Assert.assertNotNull(resultTransformer);
    }
}


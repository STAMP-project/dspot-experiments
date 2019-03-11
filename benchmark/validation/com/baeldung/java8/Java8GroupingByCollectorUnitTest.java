package com.baeldung.java8;


import BlogPostType.GUIDE;
import BlogPostType.NEWS;
import BlogPostType.REVIEW;
import com.baeldung.java_8_features.groupingby.BlogPost;
import com.baeldung.java_8_features.groupingby.BlogPostType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class Java8GroupingByCollectorUnitTest {
    private static final List<BlogPost> posts = Arrays.asList(new BlogPost("News item 1", "Author 1", BlogPostType.NEWS, 15), new BlogPost("Tech review 1", "Author 2", BlogPostType.REVIEW, 5), new BlogPost("Programming guide", "Author 1", BlogPostType.GUIDE, 20), new BlogPost("News item 2", "Author 2", BlogPostType.NEWS, 35), new BlogPost("Tech review 2", "Author 1", BlogPostType.REVIEW, 15));

    @Test
    public void givenAListOfPosts_whenGroupedByType_thenGetAMapBetweenTypeAndPosts() {
        Map<BlogPostType, List<BlogPost>> postsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType));
        Assert.assertEquals(2, postsPerType.get(NEWS).size());
        Assert.assertEquals(1, postsPerType.get(GUIDE).size());
        Assert.assertEquals(2, postsPerType.get(REVIEW).size());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndTheirTitlesAreJoinedInAString_thenGetAMapBetweenTypeAndCsvTitles() {
        Map<BlogPostType, String> postsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.mapping(BlogPost::getTitle, Collectors.joining(", ", "Post titles: [", "]"))));
        Assert.assertEquals("Post titles: [News item 1, News item 2]", postsPerType.get(NEWS));
        Assert.assertEquals("Post titles: [Programming guide]", postsPerType.get(GUIDE));
        Assert.assertEquals("Post titles: [Tech review 1, Tech review 2]", postsPerType.get(REVIEW));
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndSumTheLikes_thenGetAMapBetweenTypeAndPostLikes() {
        Map<BlogPostType, Integer> likesPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.summingInt(BlogPost::getLikes)));
        Assert.assertEquals(50, likesPerType.get(NEWS).intValue());
        Assert.assertEquals(20, likesPerType.get(REVIEW).intValue());
        Assert.assertEquals(20, likesPerType.get(GUIDE).intValue());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeInAnEnumMap_thenGetAnEnumMapBetweenTypeAndPosts() {
        EnumMap<BlogPostType, List<BlogPost>> postsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, () -> new EnumMap<>(.class), Collectors.toList()));
        Assert.assertEquals(2, postsPerType.get(NEWS).size());
        Assert.assertEquals(1, postsPerType.get(GUIDE).size());
        Assert.assertEquals(2, postsPerType.get(REVIEW).size());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeInSets_thenGetAMapBetweenTypesAndSetsOfPosts() {
        Map<BlogPostType, Set<BlogPost>> postsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.toSet()));
        Assert.assertEquals(2, postsPerType.get(NEWS).size());
        Assert.assertEquals(1, postsPerType.get(GUIDE).size());
        Assert.assertEquals(2, postsPerType.get(REVIEW).size());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeConcurrently_thenGetAMapBetweenTypeAndPosts() {
        ConcurrentMap<BlogPostType, List<BlogPost>> postsPerType = Java8GroupingByCollectorUnitTest.posts.parallelStream().collect(Collectors.groupingByConcurrent(BlogPost::getType));
        Assert.assertEquals(2, postsPerType.get(NEWS).size());
        Assert.assertEquals(1, postsPerType.get(GUIDE).size());
        Assert.assertEquals(2, postsPerType.get(REVIEW).size());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndAveragingLikes_thenGetAMapBetweenTypeAndAverageNumberOfLikes() {
        Map<BlogPostType, Double> averageLikesPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.averagingInt(BlogPost::getLikes)));
        Assert.assertEquals(25, averageLikesPerType.get(NEWS).intValue());
        Assert.assertEquals(20, averageLikesPerType.get(GUIDE).intValue());
        Assert.assertEquals(10, averageLikesPerType.get(REVIEW).intValue());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndCounted_thenGetAMapBetweenTypeAndNumberOfPosts() {
        Map<BlogPostType, Long> numberOfPostsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.counting()));
        Assert.assertEquals(2, numberOfPostsPerType.get(NEWS).intValue());
        Assert.assertEquals(1, numberOfPostsPerType.get(GUIDE).intValue());
        Assert.assertEquals(2, numberOfPostsPerType.get(REVIEW).intValue());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndMaxingLikes_thenGetAMapBetweenTypeAndMaximumNumberOfLikes() {
        Map<BlogPostType, Optional<BlogPost>> maxLikesPerPostType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.maxBy(Comparator.comparingInt(BlogPost::getLikes))));
        Assert.assertTrue(maxLikesPerPostType.get(NEWS).isPresent());
        Assert.assertEquals(35, maxLikesPerPostType.get(NEWS).get().getLikes());
        Assert.assertTrue(maxLikesPerPostType.get(GUIDE).isPresent());
        Assert.assertEquals(20, maxLikesPerPostType.get(GUIDE).get().getLikes());
        Assert.assertTrue(maxLikesPerPostType.get(REVIEW).isPresent());
        Assert.assertEquals(15, maxLikesPerPostType.get(REVIEW).get().getLikes());
    }

    @Test
    public void givenAListOfPosts_whenGroupedByAuthorAndThenByType_thenGetAMapBetweenAuthorAndMapsBetweenTypeAndBlogPosts() {
        Map<String, Map<BlogPostType, List<BlogPost>>> map = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getAuthor, Collectors.groupingBy(BlogPost::getType)));
        Assert.assertEquals(1, map.get("Author 1").get(NEWS).size());
        Assert.assertEquals(1, map.get("Author 1").get(GUIDE).size());
        Assert.assertEquals(1, map.get("Author 1").get(REVIEW).size());
        Assert.assertEquals(1, map.get("Author 2").get(NEWS).size());
        Assert.assertEquals(1, map.get("Author 2").get(REVIEW).size());
        Assert.assertNull(map.get("Author 2").get(GUIDE));
    }

    @Test
    public void givenAListOfPosts_whenGroupedByTypeAndSummarizingLikes_thenGetAMapBetweenTypeAndSummary() {
        Map<BlogPostType, IntSummaryStatistics> likeStatisticsPerType = Java8GroupingByCollectorUnitTest.posts.stream().collect(Collectors.groupingBy(BlogPost::getType, Collectors.summarizingInt(BlogPost::getLikes)));
        IntSummaryStatistics newsLikeStatistics = likeStatisticsPerType.get(NEWS);
        Assert.assertEquals(2, newsLikeStatistics.getCount());
        Assert.assertEquals(50, newsLikeStatistics.getSum());
        Assert.assertEquals(25.0, newsLikeStatistics.getAverage(), 0.001);
        Assert.assertEquals(35, newsLikeStatistics.getMax());
        Assert.assertEquals(15, newsLikeStatistics.getMin());
    }
}


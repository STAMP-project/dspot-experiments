package sagan.blog.support;


import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.search.support.SearchService;
import sagan.search.types.SearchEntry;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;


@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {
    private BlogService service;

    private Post post;

    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private DateFactory dateFactory;

    @Mock
    private SearchService searchService;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private PostForm postForm;

    @Test
    public void postIsUpdated() {
        Mockito.verify(postFormAdapter).updatePostFromPostForm(post, postForm);
    }

    @Test
    public void postIsPersisted() {
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
        Mockito.verify(searchService).saveToIndex(((SearchEntry) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
        Mockito.reset(searchService);
        long postId = 123L;
        Post post = PostBuilder.post().id(postId).draft().build();
        service.updatePost(post, new PostForm(post));
        Mockito.verifyZeroInteractions(searchService);
    }
}


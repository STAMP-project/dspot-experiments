package sagan.blog.support;


import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.blog.Post;
import sagan.search.SearchException;
import sagan.search.support.SearchService;
import sagan.search.types.SearchEntry;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;


@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {
    private static final String AUTHOR_USERNAME = "username";

    private Post post;

    private PostForm postForm = new PostForm();

    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Mock
    private DateFactory dateFactory;

    @Mock
    private SearchService searchService;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private BlogService service;

    @Test
    public void createsAPost() {
        Mockito.verify(postFormAdapter).createPostFromPostForm(postForm, BlogService_ValidPostTests.AUTHOR_USERNAME);
    }

    @Test
    public void postIsPersisted() {
        Mockito.verify(postRepository).save(((Post) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void creatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
        Mockito.verify(searchService).saveToIndex(((SearchEntry) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void blogIsSavedWhenSearchServiceIsDown() {
        Mockito.reset(postRepository);
        BDDMockito.willThrow(SearchException.class).given(searchService).saveToIndex(((SearchEntry) (ArgumentMatchers.anyObject())));
        post = service.addPost(postForm, BlogService_ValidPostTests.AUTHOR_USERNAME);
        Mockito.verify(postRepository).save(post);
    }

    @Test
    public void creatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
        Mockito.reset(searchService);
        PostForm draftPostForm = new PostForm();
        draftPostForm.setDraft(true);
        Post draft = sagan.blog.PostBuilder.post().draft().build();
        BDDMockito.given(postFormAdapter.createPostFromPostForm(draftPostForm, BlogService_ValidPostTests.AUTHOR_USERNAME)).willReturn(draft);
        service.addPost(draftPostForm, BlogService_ValidPostTests.AUTHOR_USERNAME);
        Mockito.verifyZeroInteractions(searchService);
    }
}


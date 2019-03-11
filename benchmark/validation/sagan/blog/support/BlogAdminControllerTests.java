package sagan.blog.support;


import PostCategory.ENGINEERING;
import Sort.Direction;
import java.security.Principal;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.MapBindingResult;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostCategory;
import sagan.blog.PostFormat;
import sagan.support.DateFactory;
import sagan.support.nav.PageableFactory;
import sagan.team.MemberProfile;
import sagan.team.support.TeamRepository;


@RunWith(MockitoJUnitRunner.class)
public class BlogAdminControllerTests {
    private static final Post TEST_POST = PostBuilder.post().id(123L).publishYesterday().build();

    private BlogAdminController controller;

    @Mock
    private BlogService blogService;

    private DateFactory dateFactory = new DateFactory();

    private ExtendedModelMap model = new ExtendedModelMap();

    private Principal principal;

    private MapBindingResult bindingResult;

    @Mock
    private TeamRepository teamRepository;

    @Test
    public void dashboardShowsUsersPosts() {
        controller = new BlogAdminController(blogService, teamRepository, dateFactory);
        Page<Post> drafts = new org.springframework.data.domain.PageImpl(Arrays.asList(new Post("draft post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)), PageableFactory.forDashboard(1), 1);
        Page<Post> scheduled = new org.springframework.data.domain.PageImpl(Arrays.asList(new Post("scheduled post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)), PageableFactory.forDashboard(1), 1);
        Page<Post> published = new org.springframework.data.domain.PageImpl(Arrays.asList(new Post("published post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)), PageableFactory.forDashboard(1), 1);
        BDDMockito.given(blogService.getPublishedPosts(ArgumentMatchers.anyObject())).willReturn(published);
        BDDMockito.given(blogService.getDraftPosts(ArgumentMatchers.anyObject())).willReturn(drafts);
        BDDMockito.given(blogService.getScheduledPosts(ArgumentMatchers.anyObject())).willReturn(scheduled);
        ExtendedModelMap model = new ExtendedModelMap();
        controller.dashboard(model, 1);
        Assert.assertThat(((Page<PostView>) (model.get("drafts"))).getContent().get(0).getTitle(), equalTo("draft post"));
        Assert.assertThat(((Page<PostView>) (model.get("scheduled"))).getContent().get(0).getTitle(), equalTo("scheduled post"));
        Assert.assertThat(((Page<PostView>) (model.get("posts"))).getContent().get(0).getTitle(), equalTo("published post"));
    }

    @Test
    public void showPostModel() {
        Post post = PostBuilder.post().build();
        BDDMockito.given(blogService.getPost(post.getId())).willReturn(post);
        controller.showPost(post.getId(), "1-post-title", model);
        PostView view = ((PostView) (model.get("post")));
        Assert.assertThat(view, is(notNullValue()));
    }

    @Test
    public void showPostView() {
        Assert.assertThat(controller.showPost(1L, "not important", model), is("admin/blog/show"));
    }

    @Test
    public void creatingABlogPostRecordsTheUser() {
        String username = "username";
        MemberProfile member = new MemberProfile();
        member.setUsername(username);
        BDDMockito.given(teamRepository.findById(12345L)).willReturn(member);
        PostForm postForm = new PostForm();
        BDDMockito.given(blogService.addPost(ArgumentMatchers.eq(postForm), ArgumentMatchers.anyString())).willReturn(BlogAdminControllerTests.TEST_POST);
        controller.createPost(principal, postForm, new org.springframework.validation.BindException(postForm, "postForm"), null);
        Mockito.verify(blogService).addPost(postForm, username);
    }

    @Test
    public void redirectToEditPostAfterCreation() throws Exception {
        String username = "username";
        MemberProfile member = new MemberProfile();
        member.setUsername(username);
        BDDMockito.given(teamRepository.findById(12345L)).willReturn(member);
        PostForm postForm = new PostForm();
        postForm.setTitle("title");
        postForm.setContent("content");
        postForm.setCategory(ENGINEERING);
        Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();
        BDDMockito.given(blogService.addPost(postForm, username)).willReturn(post);
        String result = controller.createPost(principal, postForm, bindingResult, null);
        Assert.assertThat(result, equalTo("redirect:/blog/2013/05/06/post-title/edit"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void attemptingToCreateADuplicatePostReturnsToEditForm() throws Exception {
        String username = "username";
        MemberProfile member = new MemberProfile();
        member.setUsername(username);
        BDDMockito.given(teamRepository.findById(12345L)).willReturn(member);
        PostForm postForm = new PostForm();
        postForm.setTitle("title");
        postForm.setContent("content");
        postForm.setCategory(ENGINEERING);
        Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();
        BDDMockito.given(blogService.addPost(postForm, username)).willReturn(post);
        String result1 = controller.createPost(principal, postForm, bindingResult, null);
        Assert.assertThat(result1, equalTo("redirect:/blog/2013/05/06/post-title/edit"));
        BDDMockito.given(blogService.addPost(postForm, username)).willThrow(DataIntegrityViolationException.class);
        String result2 = controller.createPost(principal, postForm, bindingResult, new ExtendedModelMap());
        Assert.assertThat(result2, equalTo("admin/blog/new"));
    }

    @Test
    public void reRenderPosts() throws Exception {
        int page = 0;
        int pageSize = 20;
        Page<Post> posts = new org.springframework.data.domain.PageImpl(Arrays.asList(new Post("published post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN), new Post("another published post", "other body", PostCategory.NEWS_AND_EVENTS, PostFormat.MARKDOWN)), new org.springframework.data.domain.PageRequest(page, pageSize, Direction.DESC, "id"), 2);
        BDDMockito.given(blogService.refreshPosts(page, pageSize)).willReturn(posts);
        String result = controller.refreshBlogPosts(page, pageSize);
        Assert.assertThat(result, equalTo("{page: 0, pageSize: 20, totalPages: 1, totalElements: 2}"));
        Mockito.verify(blogService, Mockito.times(1)).refreshPosts(page, pageSize);
    }
}


package com.baeldung.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.baeldung.BaeldungApp;
import com.baeldung.domain.Post;
import com.baeldung.repository.PostRepository;
import com.baeldung.web.rest.errors.ExceptionTranslator;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaeldungApp.class)
public class PostResourceIntegrationTest {
    private static final String DEFAULT_TITLE = "AAAAAAAAAA";

    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";

    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);

    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPostMockMvc;

    private Post post;

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();
        // Create the Post
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isCreated());
        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize((databaseSizeBeforeCreate + 1));
        Post testPost = postList.get(((postList.size()) - 1));
        assertThat(testPost.getTitle()).isEqualTo(PostResourceIntegrationTest.DEFAULT_TITLE);
        assertThat(testPost.getContent()).isEqualTo(PostResourceIntegrationTest.DEFAULT_CONTENT);
        assertThat(testPost.getCreationDate()).isEqualTo(PostResourceIntegrationTest.DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();
        // Create the Post with an existing ID
        post.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isBadRequest());
        // Validate the Alice in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setTitle(null);
        // Create the Post, which fails.
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isBadRequest());
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setContent(null);
        // Create the Post, which fails.
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isBadRequest());
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setCreationDate(null);
        // Create the Post, which fails.
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isBadRequest());
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(post.getId().intValue()))).andExpect(jsonPath("$.[*].title").value(Matchers.hasItem(PostResourceIntegrationTest.DEFAULT_TITLE.toString()))).andExpect(jsonPath("$.[*].content").value(Matchers.hasItem(PostResourceIntegrationTest.DEFAULT_CONTENT.toString()))).andExpect(jsonPath("$.[*].creationDate").value(Matchers.hasItem(PostResourceIntegrationTest.DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(post.getId().intValue())).andExpect(jsonPath("$.title").value(PostResourceIntegrationTest.DEFAULT_TITLE.toString())).andExpect(jsonPath("$.content").value(PostResourceIntegrationTest.DEFAULT_CONTENT.toString())).andExpect(jsonPath("$.creationDate").value(PostResourceIntegrationTest.DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        // Update the post
        Post updatedPost = postRepository.findOne(post.getId());
        updatedPost.title(PostResourceIntegrationTest.UPDATED_TITLE).content(PostResourceIntegrationTest.UPDATED_CONTENT).creationDate(PostResourceIntegrationTest.UPDATED_CREATION_DATE);
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedPost))).andExpect(status().isOk());
        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(((postList.size()) - 1));
        assertThat(testPost.getTitle()).isEqualTo(PostResourceIntegrationTest.UPDATED_TITLE);
        assertThat(testPost.getContent()).isEqualTo(PostResourceIntegrationTest.UPDATED_CONTENT);
        assertThat(testPost.getCreationDate()).isEqualTo(PostResourceIntegrationTest.UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();
        // Create the Post
        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPostMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(post))).andExpect(status().isCreated());
        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize((databaseSizeBeforeUpdate + 1));
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);
        int databaseSizeBeforeDelete = postRepository.findAll().size();
        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
    }
}


package com.baeldung.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.baeldung.BaeldungApp;
import com.baeldung.domain.Comment;
import com.baeldung.repository.CommentRepository;
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
 * Test class for the CommentResource REST controller.
 *
 * @see CommentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaeldungApp.class)
public class CommentResourceIntegrationTest {
    private static final String DEFAULT_TEXT = "AAAAAAAAAA";

    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);

    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCommentMockMvc;

    private Comment comment;

    @Test
    @Transactional
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        // Create the Comment
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(comment))).andExpect(status().isCreated());
        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize((databaseSizeBeforeCreate + 1));
        Comment testComment = commentList.get(((commentList.size()) - 1));
        assertThat(testComment.getText()).isEqualTo(CommentResourceIntegrationTest.DEFAULT_TEXT);
        assertThat(testComment.getCreationDate()).isEqualTo(CommentResourceIntegrationTest.DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    public void createCommentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        // Create the Comment with an existing ID
        comment.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(comment))).andExpect(status().isBadRequest());
        // Validate the Alice in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setText(null);
        // Create the Comment, which fails.
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(comment))).andExpect(status().isBadRequest());
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setCreationDate(null);
        // Create the Comment, which fails.
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(comment))).andExpect(status().isBadRequest());
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        // Get all the commentList
        restCommentMockMvc.perform(get("/api/comments?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(comment.getId().intValue()))).andExpect(jsonPath("$.[*].text").value(Matchers.hasItem(CommentResourceIntegrationTest.DEFAULT_TEXT.toString()))).andExpect(jsonPath("$.[*].creationDate").value(Matchers.hasItem(CommentResourceIntegrationTest.DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(comment.getId().intValue())).andExpect(jsonPath("$.text").value(CommentResourceIntegrationTest.DEFAULT_TEXT.toString())).andExpect(jsonPath("$.creationDate").value(CommentResourceIntegrationTest.DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();
        // Update the comment
        Comment updatedComment = commentRepository.findOne(comment.getId());
        updatedComment.text(CommentResourceIntegrationTest.UPDATED_TEXT).creationDate(CommentResourceIntegrationTest.UPDATED_CREATION_DATE);
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedComment))).andExpect(status().isOk());
        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = commentList.get(((commentList.size()) - 1));
        assertThat(testComment.getText()).isEqualTo(CommentResourceIntegrationTest.UPDATED_TEXT);
        assertThat(testComment.getCreationDate()).isEqualTo(CommentResourceIntegrationTest.UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingComment() throws Exception {
        int databaseSizeBeforeUpdate = commentRepository.findAll().size();
        // Create the Comment
        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCommentMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(comment))).andExpect(status().isCreated());
        // Validate the Comment in the database
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize((databaseSizeBeforeUpdate + 1));
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);
        int databaseSizeBeforeDelete = commentRepository.findAll().size();
        // Get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
    }
}


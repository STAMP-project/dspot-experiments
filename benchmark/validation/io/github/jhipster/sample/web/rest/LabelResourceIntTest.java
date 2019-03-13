package io.github.jhipster.sample.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import io.github.jhipster.sample.JhipsterSampleApplicationApp;
import io.github.jhipster.sample.domain.Label;
import io.github.jhipster.sample.repository.LabelRepository;
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator;
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
import org.springframework.validation.Validator;


/**
 * Test class for the LabelResource REST controller.
 *
 * @see LabelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class LabelResourceIntTest {
    private static final String DEFAULT_LABEL = "AAAAAAAAAA";

    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restLabelMockMvc;

    private Label label;

    @Test
    @Transactional
    public void createLabel() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();
        // Create the Label
        restLabelMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(label))).andExpect(status().isCreated());
        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize((databaseSizeBeforeCreate + 1));
        Label testLabel = labelList.get(((labelList.size()) - 1));
        assertThat(testLabel.getLabel()).isEqualTo(LabelResourceIntTest.DEFAULT_LABEL);
    }

    @Test
    @Transactional
    public void createLabelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = labelRepository.findAll().size();
        // Create the Label with an existing ID
        label.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restLabelMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(label))).andExpect(status().isBadRequest());
        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = labelRepository.findAll().size();
        // set the field null
        label.setLabel(null);
        // Create the Label, which fails.
        restLabelMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(label))).andExpect(status().isBadRequest());
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLabels() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        // Get all the labelList
        restLabelMockMvc.perform(get("/api/labels?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(label.getId().intValue()))).andExpect(jsonPath("$.[*].label").value(Matchers.hasItem(LabelResourceIntTest.DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void getLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", label.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(label.getId().intValue())).andExpect(jsonPath("$.label").value(LabelResourceIntTest.DEFAULT_LABEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLabel() throws Exception {
        // Get the label
        restLabelMockMvc.perform(get("/api/labels/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        int databaseSizeBeforeUpdate = labelRepository.findAll().size();
        // Update the label
        Label updatedLabel = labelRepository.findById(label.getId()).get();
        // Disconnect from session so that the updates on updatedLabel are not directly saved in db
        em.detach(updatedLabel);
        updatedLabel.setLabel(LabelResourceIntTest.UPDATED_LABEL);
        restLabelMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedLabel))).andExpect(status().isOk());
        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
        Label testLabel = labelList.get(((labelList.size()) - 1));
        assertThat(testLabel.getLabel()).isEqualTo(LabelResourceIntTest.UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void updateNonExistingLabel() throws Exception {
        int databaseSizeBeforeUpdate = labelRepository.findAll().size();
        // Create the Label
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLabelMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(label))).andExpect(status().isBadRequest());
        // Validate the Label in the database
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteLabel() throws Exception {
        // Initialize the database
        labelRepository.saveAndFlush(label);
        int databaseSizeBeforeDelete = labelRepository.findAll().size();
        // Delete the label
        restLabelMockMvc.perform(delete("/api/labels/{id}", label.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Label> labelList = labelRepository.findAll();
        assertThat(labelList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Label.class);
        Label label1 = new Label();
        label1.setId(1L);
        Label label2 = new Label();
        label2.setId(label1.getId());
        assertThat(label1).isEqualTo(label2);
        label2.setId(2L);
        assertThat(label1).isNotEqualTo(label2);
        label1.setId(null);
        assertThat(label1).isNotEqualTo(label2);
    }
}


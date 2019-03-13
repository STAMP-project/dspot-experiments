package io.github.jhipster.sample.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import io.github.jhipster.sample.JhipsterSampleApplicationApp;
import io.github.jhipster.sample.domain.Operation;
import io.github.jhipster.sample.repository.OperationRepository;
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;


/**
 * Test class for the OperationResource REST controller.
 *
 * @see OperationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class OperationResourceIntTest {
    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);

    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";

    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);

    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    @Autowired
    private OperationRepository operationRepository;

    @Mock
    private OperationRepository operationRepositoryMock;

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

    private MockMvc restOperationMockMvc;

    private Operation operation;

    @Test
    @Transactional
    public void createOperation() throws Exception {
        int databaseSizeBeforeCreate = operationRepository.findAll().size();
        // Create the Operation
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(operation))).andExpect(status().isCreated());
        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize((databaseSizeBeforeCreate + 1));
        Operation testOperation = operationList.get(((operationList.size()) - 1));
        assertThat(testOperation.getDate()).isEqualTo(OperationResourceIntTest.DEFAULT_DATE);
        assertThat(testOperation.getDescription()).isEqualTo(OperationResourceIntTest.DEFAULT_DESCRIPTION);
        assertThat(testOperation.getAmount()).isEqualTo(OperationResourceIntTest.DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void createOperationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = operationRepository.findAll().size();
        // Create the Operation with an existing ID
        operation.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(operation))).andExpect(status().isBadRequest());
        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        // set the field null
        operation.setDate(null);
        // Create the Operation, which fails.
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(operation))).andExpect(status().isBadRequest());
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = operationRepository.findAll().size();
        // set the field null
        operation.setAmount(null);
        // Create the Operation, which fails.
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(operation))).andExpect(status().isBadRequest());
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOperations() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        // Get all the operationList
        restOperationMockMvc.perform(get("/api/operations?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(operation.getId().intValue()))).andExpect(jsonPath("$.[*].date").value(Matchers.hasItem(OperationResourceIntTest.DEFAULT_DATE.toString()))).andExpect(jsonPath("$.[*].description").value(Matchers.hasItem(OperationResourceIntTest.DEFAULT_DESCRIPTION.toString()))).andExpect(jsonPath("$.[*].amount").value(Matchers.hasItem(OperationResourceIntTest.DEFAULT_AMOUNT.intValue())));
    }

    @Test
    @Transactional
    public void getOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", operation.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(operation.getId().intValue())).andExpect(jsonPath("$.date").value(OperationResourceIntTest.DEFAULT_DATE.toString())).andExpect(jsonPath("$.description").value(OperationResourceIntTest.DEFAULT_DESCRIPTION.toString())).andExpect(jsonPath("$.amount").value(OperationResourceIntTest.DEFAULT_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingOperation() throws Exception {
        // Get the operation
        restOperationMockMvc.perform(get("/api/operations/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        // Update the operation
        Operation updatedOperation = operationRepository.findById(operation.getId()).get();
        // Disconnect from session so that the updates on updatedOperation are not directly saved in db
        em.detach(updatedOperation);
        updatedOperation.setDate(OperationResourceIntTest.UPDATED_DATE);
        updatedOperation.setDescription(OperationResourceIntTest.UPDATED_DESCRIPTION);
        updatedOperation.setAmount(OperationResourceIntTest.UPDATED_AMOUNT);
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedOperation))).andExpect(status().isOk());
        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
        Operation testOperation = operationList.get(((operationList.size()) - 1));
        assertThat(testOperation.getDate()).isEqualTo(OperationResourceIntTest.UPDATED_DATE);
        assertThat(testOperation.getDescription()).isEqualTo(OperationResourceIntTest.UPDATED_DESCRIPTION);
        assertThat(testOperation.getAmount()).isEqualTo(OperationResourceIntTest.UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void updateNonExistingOperation() throws Exception {
        int databaseSizeBeforeUpdate = operationRepository.findAll().size();
        // Create the Operation
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(operation))).andExpect(status().isBadRequest());
        // Validate the Operation in the database
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOperation() throws Exception {
        // Initialize the database
        operationRepository.saveAndFlush(operation);
        int databaseSizeBeforeDelete = operationRepository.findAll().size();
        // Delete the operation
        restOperationMockMvc.perform(delete("/api/operations/{id}", operation.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Operation> operationList = operationRepository.findAll();
        assertThat(operationList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Operation.class);
        Operation operation1 = new Operation();
        operation1.setId(1L);
        Operation operation2 = new Operation();
        operation2.setId(operation1.getId());
        assertThat(operation1).isEqualTo(operation2);
        operation2.setId(2L);
        assertThat(operation1).isNotEqualTo(operation2);
        operation1.setId(null);
        assertThat(operation1).isNotEqualTo(operation2);
    }
}


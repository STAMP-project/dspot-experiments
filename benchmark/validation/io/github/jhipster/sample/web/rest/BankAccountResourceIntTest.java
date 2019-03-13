package io.github.jhipster.sample.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import io.github.jhipster.sample.JhipsterSampleApplicationApp;
import io.github.jhipster.sample.domain.BankAccount;
import io.github.jhipster.sample.repository.BankAccountRepository;
import io.github.jhipster.sample.web.rest.errors.ExceptionTranslator;
import java.math.BigDecimal;
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
 * Test class for the BankAccountResource REST controller.
 *
 * @see BankAccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class BankAccountResourceIntTest {
    private static final String DEFAULT_NAME = "AAAAAAAAAA";

    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);

    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    @Autowired
    private BankAccountRepository bankAccountRepository;

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

    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;

    @Test
    @Transactional
    public void createBankAccount() throws Exception {
        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();
        // Create the BankAccount
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(bankAccount))).andExpect(status().isCreated());
        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize((databaseSizeBeforeCreate + 1));
        BankAccount testBankAccount = bankAccountList.get(((bankAccountList.size()) - 1));
        assertThat(testBankAccount.getName()).isEqualTo(BankAccountResourceIntTest.DEFAULT_NAME);
        assertThat(testBankAccount.getBalance()).isEqualTo(BankAccountResourceIntTest.DEFAULT_BALANCE);
    }

    @Test
    @Transactional
    public void createBankAccountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();
        // Create the BankAccount with an existing ID
        bankAccount.setId(1L);
        // An entity with an existing ID cannot be created, so this API call must fail
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(bankAccount))).andExpect(status().isBadRequest());
        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setName(null);
        // Create the BankAccount, which fails.
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(bankAccount))).andExpect(status().isBadRequest());
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBalanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = bankAccountRepository.findAll().size();
        // set the field null
        bankAccount.setBalance(null);
        // Create the BankAccount, which fails.
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(bankAccount))).andExpect(status().isBadRequest());
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBankAccounts() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        // Get all the bankAccountList
        restBankAccountMockMvc.perform(get("/api/bank-accounts?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(bankAccount.getId().intValue()))).andExpect(jsonPath("$.[*].name").value(Matchers.hasItem(BankAccountResourceIntTest.DEFAULT_NAME.toString()))).andExpect(jsonPath("$.[*].balance").value(Matchers.hasItem(BankAccountResourceIntTest.DEFAULT_BALANCE.intValue())));
    }

    @Test
    @Transactional
    public void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", bankAccount.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(bankAccount.getId().intValue())).andExpect(jsonPath("$.name").value(BankAccountResourceIntTest.DEFAULT_NAME.toString())).andExpect(jsonPath("$.balance").value(BankAccountResourceIntTest.DEFAULT_BALANCE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bank-accounts/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        // Update the bankAccount
        BankAccount updatedBankAccount = bankAccountRepository.findById(bankAccount.getId()).get();
        // Disconnect from session so that the updates on updatedBankAccount are not directly saved in db
        em.detach(updatedBankAccount);
        updatedBankAccount.setName(BankAccountResourceIntTest.UPDATED_NAME);
        updatedBankAccount.setBalance(BankAccountResourceIntTest.UPDATED_BALANCE);
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(updatedBankAccount))).andExpect(status().isOk());
        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccountList.get(((bankAccountList.size()) - 1));
        assertThat(testBankAccount.getName()).isEqualTo(BankAccountResourceIntTest.UPDATED_NAME);
        assertThat(testBankAccount.getBalance()).isEqualTo(BankAccountResourceIntTest.UPDATED_BALANCE);
    }

    @Test
    @Transactional
    public void updateNonExistingBankAccount() throws Exception {
        int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();
        // Create the BankAccount
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBankAccountMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(bankAccount))).andExpect(status().isBadRequest());
        // Validate the BankAccount in the database
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.saveAndFlush(bankAccount);
        int databaseSizeBeforeDelete = bankAccountRepository.findAll().size();
        // Delete the bankAccount
        restBankAccountMockMvc.perform(delete("/api/bank-accounts/{id}", bankAccount.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<BankAccount> bankAccountList = bankAccountRepository.findAll();
        assertThat(bankAccountList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BankAccount.class);
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setId(1L);
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setId(bankAccount1.getId());
        assertThat(bankAccount1).isEqualTo(bankAccount2);
        bankAccount2.setId(2L);
        assertThat(bankAccount1).isNotEqualTo(bankAccount2);
        bankAccount1.setId(null);
        assertThat(bankAccount1).isNotEqualTo(bankAccount2);
    }
}


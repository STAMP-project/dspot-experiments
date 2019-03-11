package com.baeldung.jhipster.quotes.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.baeldung.jhipster.quotes.QuotesApp;
import com.baeldung.jhipster.quotes.config.SecurityBeanOverrideConfiguration;
import com.baeldung.jhipster.quotes.domain.Quote;
import com.baeldung.jhipster.quotes.repository.QuoteRepository;
import com.baeldung.jhipster.quotes.service.QuoteQueryService;
import com.baeldung.jhipster.quotes.service.QuoteService;
import com.baeldung.jhipster.quotes.service.dto.QuoteDTO;
import com.baeldung.jhipster.quotes.service.mapper.QuoteMapper;
import com.baeldung.jhipster.quotes.web.rest.errors.ExceptionTranslator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Test class for the QuoteResource REST controller.
 *
 * @see QuoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, QuotesApp.class })
public class QuoteResourceIntTest {
    private static final String DEFAULT_SYMBOL = "AAAAAAAAAA";

    private static final String UPDATED_SYMBOL = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);

    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_LAST_TRADE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);

    private static final ZonedDateTime UPDATED_LAST_TRADE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private QuoteMapper quoteMapper;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private QuoteQueryService quoteQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuoteMockMvc;

    private Quote quote;

    @Test
    @Transactional
    public void createQuote() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();
        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isCreated());
        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize((databaseSizeBeforeCreate + 1));
        Quote testQuote = quoteList.get(((quoteList.size()) - 1));
        assertThat(testQuote.getSymbol()).isEqualTo(QuoteResourceIntTest.DEFAULT_SYMBOL);
        assertThat(testQuote.getPrice()).isEqualTo(QuoteResourceIntTest.DEFAULT_PRICE);
        assertThat(testQuote.getLastTrade()).isEqualTo(QuoteResourceIntTest.DEFAULT_LAST_TRADE);
    }

    @Test
    @Transactional
    public void createQuoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();
        // Create the Quote with an existing ID
        quote.setId(1L);
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        // An entity with an existing ID cannot be created, so this API call must fail
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isBadRequest());
        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSymbolIsRequired() throws Exception {
        int databaseSizeBeforeTest = quoteRepository.findAll().size();
        // set the field null
        quote.setSymbol(null);
        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isBadRequest());
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = quoteRepository.findAll().size();
        // set the field null
        quote.setPrice(null);
        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isBadRequest());
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastTradeIsRequired() throws Exception {
        int databaseSizeBeforeTest = quoteRepository.findAll().size();
        // set the field null
        quote.setLastTrade(null);
        // Create the Quote, which fails.
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isBadRequest());
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllQuotes() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList
        restQuoteMockMvc.perform(get("/api/quotes?sort=id,desc")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(quote.getId().intValue()))).andExpect(jsonPath("$.[*].symbol").value(Matchers.hasItem(QuoteResourceIntTest.DEFAULT_SYMBOL.toString()))).andExpect(jsonPath("$.[*].price").value(Matchers.hasItem(QuoteResourceIntTest.DEFAULT_PRICE.intValue()))).andExpect(jsonPath("$.[*].lastTrade").value(Matchers.hasItem(TestUtil.sameInstant(QuoteResourceIntTest.DEFAULT_LAST_TRADE))));
    }

    @Test
    @Transactional
    public void getQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", quote.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(quote.getId().intValue())).andExpect(jsonPath("$.symbol").value(QuoteResourceIntTest.DEFAULT_SYMBOL.toString())).andExpect(jsonPath("$.price").value(QuoteResourceIntTest.DEFAULT_PRICE.intValue())).andExpect(jsonPath("$.lastTrade").value(TestUtil.sameInstant(QuoteResourceIntTest.DEFAULT_LAST_TRADE)));
    }

    @Test
    @Transactional
    public void getAllQuotesBySymbolIsEqualToSomething() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where symbol equals to DEFAULT_SYMBOL
        defaultQuoteShouldBeFound(("symbol.equals=" + (QuoteResourceIntTest.DEFAULT_SYMBOL)));
        // Get all the quoteList where symbol equals to UPDATED_SYMBOL
        defaultQuoteShouldNotBeFound(("symbol.equals=" + (QuoteResourceIntTest.UPDATED_SYMBOL)));
    }

    @Test
    @Transactional
    public void getAllQuotesBySymbolIsInShouldWork() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where symbol in DEFAULT_SYMBOL or UPDATED_SYMBOL
        defaultQuoteShouldBeFound(((("symbol.in=" + (QuoteResourceIntTest.DEFAULT_SYMBOL)) + ",") + (QuoteResourceIntTest.UPDATED_SYMBOL)));
        // Get all the quoteList where symbol equals to UPDATED_SYMBOL
        defaultQuoteShouldNotBeFound(("symbol.in=" + (QuoteResourceIntTest.UPDATED_SYMBOL)));
    }

    @Test
    @Transactional
    public void getAllQuotesBySymbolIsNullOrNotNull() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where symbol is not null
        defaultQuoteShouldBeFound("symbol.specified=true");
        // Get all the quoteList where symbol is null
        defaultQuoteShouldNotBeFound("symbol.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuotesByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where price equals to DEFAULT_PRICE
        defaultQuoteShouldBeFound(("price.equals=" + (QuoteResourceIntTest.DEFAULT_PRICE)));
        // Get all the quoteList where price equals to UPDATED_PRICE
        defaultQuoteShouldNotBeFound(("price.equals=" + (QuoteResourceIntTest.UPDATED_PRICE)));
    }

    @Test
    @Transactional
    public void getAllQuotesByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultQuoteShouldBeFound(((("price.in=" + (QuoteResourceIntTest.DEFAULT_PRICE)) + ",") + (QuoteResourceIntTest.UPDATED_PRICE)));
        // Get all the quoteList where price equals to UPDATED_PRICE
        defaultQuoteShouldNotBeFound(("price.in=" + (QuoteResourceIntTest.UPDATED_PRICE)));
    }

    @Test
    @Transactional
    public void getAllQuotesByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where price is not null
        defaultQuoteShouldBeFound("price.specified=true");
        // Get all the quoteList where price is null
        defaultQuoteShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuotesByLastTradeIsEqualToSomething() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where lastTrade equals to DEFAULT_LAST_TRADE
        defaultQuoteShouldBeFound(("lastTrade.equals=" + (QuoteResourceIntTest.DEFAULT_LAST_TRADE)));
        // Get all the quoteList where lastTrade equals to UPDATED_LAST_TRADE
        defaultQuoteShouldNotBeFound(("lastTrade.equals=" + (QuoteResourceIntTest.UPDATED_LAST_TRADE)));
    }

    @Test
    @Transactional
    public void getAllQuotesByLastTradeIsInShouldWork() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where lastTrade in DEFAULT_LAST_TRADE or UPDATED_LAST_TRADE
        defaultQuoteShouldBeFound(((("lastTrade.in=" + (QuoteResourceIntTest.DEFAULT_LAST_TRADE)) + ",") + (QuoteResourceIntTest.UPDATED_LAST_TRADE)));
        // Get all the quoteList where lastTrade equals to UPDATED_LAST_TRADE
        defaultQuoteShouldNotBeFound(("lastTrade.in=" + (QuoteResourceIntTest.UPDATED_LAST_TRADE)));
    }

    @Test
    @Transactional
    public void getAllQuotesByLastTradeIsNullOrNotNull() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where lastTrade is not null
        defaultQuoteShouldBeFound("lastTrade.specified=true");
        // Get all the quoteList where lastTrade is null
        defaultQuoteShouldNotBeFound("lastTrade.specified=false");
    }

    @Test
    @Transactional
    public void getAllQuotesByLastTradeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where lastTrade greater than or equals to DEFAULT_LAST_TRADE
        defaultQuoteShouldBeFound(("lastTrade.greaterOrEqualThan=" + (QuoteResourceIntTest.DEFAULT_LAST_TRADE)));
        // Get all the quoteList where lastTrade greater than or equals to UPDATED_LAST_TRADE
        defaultQuoteShouldNotBeFound(("lastTrade.greaterOrEqualThan=" + (QuoteResourceIntTest.UPDATED_LAST_TRADE)));
    }

    @Test
    @Transactional
    public void getAllQuotesByLastTradeIsLessThanSomething() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        // Get all the quoteList where lastTrade less than or equals to DEFAULT_LAST_TRADE
        defaultQuoteShouldNotBeFound(("lastTrade.lessThan=" + (QuoteResourceIntTest.DEFAULT_LAST_TRADE)));
        // Get all the quoteList where lastTrade less than or equals to UPDATED_LAST_TRADE
        defaultQuoteShouldBeFound(("lastTrade.lessThan=" + (QuoteResourceIntTest.UPDATED_LAST_TRADE)));
    }

    @Test
    @Transactional
    public void getNonExistingQuote() throws Exception {
        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();
        // Update the quote
        Quote updatedQuote = quoteRepository.findById(quote.getId()).get();
        // Disconnect from session so that the updates on updatedQuote are not directly saved in db
        em.detach(updatedQuote);
        updatedQuote.symbol(QuoteResourceIntTest.UPDATED_SYMBOL).price(QuoteResourceIntTest.UPDATED_PRICE).lastTrade(QuoteResourceIntTest.UPDATED_LAST_TRADE);
        QuoteDTO quoteDTO = quoteMapper.toDto(updatedQuote);
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isOk());
        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate);
        Quote testQuote = quoteList.get(((quoteList.size()) - 1));
        assertThat(testQuote.getSymbol()).isEqualTo(QuoteResourceIntTest.UPDATED_SYMBOL);
        assertThat(testQuote.getPrice()).isEqualTo(QuoteResourceIntTest.UPDATED_PRICE);
        assertThat(testQuote.getLastTrade()).isEqualTo(QuoteResourceIntTest.UPDATED_LAST_TRADE);
    }

    @Test
    @Transactional
    public void updateNonExistingQuote() throws Exception {
        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();
        // Create the Quote
        QuoteDTO quoteDTO = quoteMapper.toDto(quote);
        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuoteMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(quoteDTO))).andExpect(status().isBadRequest());
        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        int databaseSizeBeforeDelete = quoteRepository.findAll().size();
        // Get the quote
        restQuoteMockMvc.perform(delete("/api/quotes/{id}", quote.getId()).accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());
        // Validate the database is empty
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize((databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quote.class);
        Quote quote1 = new Quote();
        quote1.setId(1L);
        Quote quote2 = new Quote();
        quote2.setId(quote1.getId());
        assertThat(quote1).isEqualTo(quote2);
        quote2.setId(2L);
        assertThat(quote1).isNotEqualTo(quote2);
        quote1.setId(null);
        assertThat(quote1).isNotEqualTo(quote2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuoteDTO.class);
        QuoteDTO quoteDTO1 = new QuoteDTO();
        quoteDTO1.setId(1L);
        QuoteDTO quoteDTO2 = new QuoteDTO();
        assertThat(quoteDTO1).isNotEqualTo(quoteDTO2);
        quoteDTO2.setId(quoteDTO1.getId());
        assertThat(quoteDTO1).isEqualTo(quoteDTO2);
        quoteDTO2.setId(2L);
        assertThat(quoteDTO1).isNotEqualTo(quoteDTO2);
        quoteDTO1.setId(null);
        assertThat(quoteDTO1).isNotEqualTo(quoteDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(quoteMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(quoteMapper.fromId(null)).isNull();
    }
}


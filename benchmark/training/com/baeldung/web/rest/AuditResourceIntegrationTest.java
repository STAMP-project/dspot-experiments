package com.baeldung.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.baeldung.BaeldungApp;
import com.baeldung.config.audit.AuditEventConverter;
import com.baeldung.domain.PersistentAuditEvent;
import com.baeldung.repository.PersistenceAuditEventRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the AuditResource REST controller.
 *
 * @see AuditResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaeldungApp.class)
@Transactional
public class AuditResourceIntegrationTest {
    private static final String SAMPLE_PRINCIPAL = "SAMPLE_PRINCIPAL";

    private static final String SAMPLE_TYPE = "SAMPLE_TYPE";

    private static final LocalDateTime SAMPLE_TIMESTAMP = LocalDateTime.parse("2015-08-04T10:11:30");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private PersistenceAuditEventRepository auditEventRepository;

    @Autowired
    private AuditEventConverter auditEventConverter;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private FormattingConversionService formattingConversionService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private PersistentAuditEvent auditEvent;

    private MockMvc restAuditMockMvc;

    @Test
    public void getAllAudits() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);
        // Get all the audits
        restAuditMockMvc.perform(get("/management/audits")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].principal").value(Matchers.hasItem(AuditResourceIntegrationTest.SAMPLE_PRINCIPAL)));
    }

    @Test
    public void getAudit() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);
        // Get the audit
        restAuditMockMvc.perform(get("/management/audits/{id}", auditEvent.getId())).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.principal").value(AuditResourceIntegrationTest.SAMPLE_PRINCIPAL));
    }

    @Test
    public void getAuditsByDate() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);
        // Generate dates for selecting audits by date, making sure the period will contain the audit
        String fromDate = AuditResourceIntegrationTest.SAMPLE_TIMESTAMP.minusDays(1).format(AuditResourceIntegrationTest.FORMATTER);
        String toDate = AuditResourceIntegrationTest.SAMPLE_TIMESTAMP.plusDays(1).format(AuditResourceIntegrationTest.FORMATTER);
        // Get the audit
        restAuditMockMvc.perform(get(((("/management/audits?fromDate=" + fromDate) + "&toDate=") + toDate))).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.[*].principal").value(Matchers.hasItem(AuditResourceIntegrationTest.SAMPLE_PRINCIPAL)));
    }

    @Test
    public void getNonExistingAuditsByDate() throws Exception {
        // Initialize the database
        auditEventRepository.save(auditEvent);
        // Generate dates for selecting audits by date, making sure the period will not contain the sample audit
        String fromDate = AuditResourceIntegrationTest.SAMPLE_TIMESTAMP.minusDays(2).format(AuditResourceIntegrationTest.FORMATTER);
        String toDate = AuditResourceIntegrationTest.SAMPLE_TIMESTAMP.minusDays(1).format(AuditResourceIntegrationTest.FORMATTER);
        // Query audits but expect no results
        restAuditMockMvc.perform(get(((("/management/audits?fromDate=" + fromDate) + "&toDate=") + toDate))).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE)).andExpect(header().string("X-Total-Count", "0"));
    }

    @Test
    public void getNonExistingAudit() throws Exception {
        // Get the audit
        restAuditMockMvc.perform(get("/management/audits/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}


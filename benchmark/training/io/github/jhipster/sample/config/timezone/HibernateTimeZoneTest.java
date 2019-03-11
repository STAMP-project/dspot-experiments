package io.github.jhipster.sample.config.timezone;


import io.github.jhipster.sample.JhipsterSampleApplicationApp;
import io.github.jhipster.sample.repository.timezone.DateTimeWrapper;
import io.github.jhipster.sample.repository.timezone.DateTimeWrapperRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Unit tests for the UTC Hibernate configuration.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class HibernateTimeZoneTest {
    @Autowired
    private DateTimeWrapperRepository dateTimeWrapperRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DateTimeWrapper dateTimeWrapper;

    private DateTimeFormatter dateTimeFormatter;

    private DateTimeFormatter timeFormatter;

    private DateTimeFormatter dateFormatter;

    @Test
    @Transactional
    public void storeInstantWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("instant", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeFormatter.format(dateTimeWrapper.getInstant());
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeLocalDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("local_date_time", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getLocalDateTime().atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeOffsetDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("offset_date_time", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getOffsetDateTime().format(dateTimeFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeZoneDateTimeWithUtcConfigShouldBeStoredOnGMTTimeZone() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("zoned_date_time", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getZonedDateTime().format(dateTimeFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeLocalTimeWithUtcConfigShouldBeStoredOnGMTTimeZoneAccordingToHis1stJan1970Value() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("local_time", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getLocalTime().atDate(LocalDate.of(1970, Month.JANUARY, 1)).atZone(ZoneId.systemDefault()).format(timeFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeOffsetTimeWithUtcConfigShouldBeStoredOnGMTTimeZoneAccordingToHis1stJan1970Value() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("offset_time", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getOffsetTime().toLocalTime().atDate(LocalDate.of(1970, Month.JANUARY, 1)).atZone(ZoneId.systemDefault()).format(timeFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }

    @Test
    @Transactional
    public void storeLocalDateWithUtcConfigShouldBeStoredWithoutTransformation() {
        saveAndFlush(dateTimeWrapper);
        String request = generateSqlRequest("local_date", dateTimeWrapper.getId());
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(request);
        String expectedValue = dateTimeWrapper.getLocalDate().format(dateFormatter);
        assertThatDateStoredValueIsEqualToInsertDateValueOnGMTTimeZone(resultSet, expectedValue);
    }
}


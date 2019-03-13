/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.service.daos;


import SECRETS.CREATEDAT;
import SECRETS.CREATEDBY;
import SECRETS.CURRENT;
import SECRETS.DESCRIPTION;
import SECRETS.ID;
import SECRETS.NAME;
import SECRETS.UPDATEDAT;
import SECRETS.UPDATEDBY;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import keywhiz.KeywhizTestRunner;
import keywhiz.api.ApiDate;
import keywhiz.api.automation.v2.PartialUpdateSecretRequestV2;
import keywhiz.api.model.SecretContent;
import keywhiz.api.model.SecretSeries;
import keywhiz.api.model.SecretSeriesAndContent;
import keywhiz.service.crypto.ContentCryptographer;
import keywhiz.service.crypto.CryptoFixtures;
import keywhiz.service.daos.SecretDAO.SecretDAOFactory;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(KeywhizTestRunner.class)
public class SecretDAOTest {
    @Inject
    private DSLContext jooqContext;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private SecretDAOFactory secretDAOFactory;

    private static final ContentCryptographer cryptographer = CryptoFixtures.contentCryptographer();

    private static final ApiDate date = ApiDate.now();

    private ImmutableMap<String, String> emptyMetadata = ImmutableMap.of();

    private SecretSeries series1 = SecretSeries.of(1, "secret1", "desc1", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", null, null, 101L);

    private String content = "c2VjcmV0MQ==";

    private String encryptedContent = SecretDAOTest.cryptographer.encryptionKeyDerivedFrom(series1.name()).encrypt(content);

    private SecretContent content1 = SecretContent.of(101, 1, encryptedContent, "checksum", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", emptyMetadata, 0);

    private SecretSeriesAndContent secret1 = SecretSeriesAndContent.of(series1, content1);

    private SecretSeries series2 = SecretSeries.of(2, "secret2", "desc2", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", null, null, 103L);

    private SecretContent content2a = SecretContent.of(102, 2, encryptedContent, "checksum", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", emptyMetadata, 0);

    private SecretSeriesAndContent secret2a = SecretSeriesAndContent.of(series2, content2a);

    private SecretContent content2b = SecretContent.of(103, 2, "some other content", "checksum", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", emptyMetadata, 0);

    private SecretSeriesAndContent secret2b = SecretSeriesAndContent.of(series2, content2b);

    private SecretSeries series3 = SecretSeries.of(3, "secret3", "desc3", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", null, null, null);

    private SecretContent content3 = SecretContent.of(104, 3, encryptedContent, "checksum", SecretDAOTest.date, "creator", SecretDAOTest.date, "updater", emptyMetadata, 0);

    private SecretSeriesAndContent secret3 = SecretSeriesAndContent.of(series3, content3);

    private SecretDAO secretDAO;

    // ---------------------------------------------------------------------------------------
    // createSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void createSecret() {
        int secretsBefore = tableSize(SECRETS);
        int secretContentsBefore = tableSize(SECRETS_CONTENT);
        String name = "newSecret";
        String content = "c2VjcmV0MQ==";
        String hmac = SecretDAOTest.cryptographer.computeHmac(content.getBytes(StandardCharsets.UTF_8));
        String encryptedContent = SecretDAOTest.cryptographer.encryptionKeyDerivedFrom(name).encrypt(content);
        long newId = secretDAO.createSecret(name, encryptedContent, hmac, "creator", ImmutableMap.of(), 0, "", null, ImmutableMap.of());
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(newId).get();
        assertThat(tableSize(SECRETS)).isEqualTo((secretsBefore + 1));
        assertThat(tableSize(SECRETS_CONTENT)).isEqualTo((secretContentsBefore + 1));
        newSecret = secretDAO.getSecretByName(newSecret.series().name()).get();
        assertThat(secretDAO.getSecrets(null, null)).containsOnly(secret1, secret2b, newSecret);
    }

    @Test(expected = DataAccessException.class)
    public void createSecretFailsIfSecretExists() {
        String name = "newSecret";
        secretDAO.createSecret(name, "some secret", "checksum", "creator", ImmutableMap.of(), 0, "", null, ImmutableMap.of());
        secretDAO.createSecret(name, "some secret", "checksum", "creator", ImmutableMap.of(), 0, "", null, ImmutableMap.of());
    }

    @Test
    public void createSecretSucceedsIfCurrentVersionIsNull() {
        String name = "newSecret";
        long firstId = secretDAO.createSecret(name, "content1", SecretDAOTest.cryptographer.computeHmac("content1".getBytes(StandardCharsets.UTF_8)), "creator1", ImmutableMap.of("foo", "bar"), 1000, "description1", "type1", ImmutableMap.of());
        jooqContext.update(SECRETS).set(CURRENT, ((Long) (null))).where(ID.eq(firstId)).execute();
        long secondId = secretDAO.createSecret(name, "content2", SecretDAOTest.cryptographer.computeHmac("content2".getBytes(StandardCharsets.UTF_8)), "creator2", ImmutableMap.of("foo2", "bar2"), 2000, "description2", "type2", ImmutableMap.of());
        assertThat(secondId).isGreaterThan(firstId);
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(secondId).get();
        assertThat(newSecret.series().createdBy()).isEqualTo("creator2");
        assertThat(newSecret.series().updatedBy()).isEqualTo("creator2");
        assertThat(newSecret.series().description()).isEqualTo("description2");
        assertThat(newSecret.series().type().get()).isEqualTo("type2");
        assertThat(newSecret.content().createdBy()).isEqualTo("creator2");
        assertThat(newSecret.content().encryptedContent()).isEqualTo("content2");
        assertThat(newSecret.content().metadata()).isEqualTo(ImmutableMap.of("foo2", "bar2"));
    }

    // ---------------------------------------------------------------------------------------
    // createOrUpdateSecret
    // ---------------------------------------------------------------------------------------
    @Test
    public void createOrUpdateSecretWhenSecretDoesNotExist() {
        int secretsBefore = tableSize(SECRETS);
        int secretContentsBefore = tableSize(SECRETS_CONTENT);
        String name = "newSecret";
        String content = "c2VjcmV0MQ==";
        String hmac = SecretDAOTest.cryptographer.computeHmac(content.getBytes(StandardCharsets.UTF_8));
        String encryptedContent = SecretDAOTest.cryptographer.encryptionKeyDerivedFrom(name).encrypt(content);
        long newId = secretDAO.createOrUpdateSecret(name, encryptedContent, hmac, "creator", ImmutableMap.of(), 0, "", null, ImmutableMap.of());
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(newId).get();
        assertThat(tableSize(SECRETS)).isEqualTo((secretsBefore + 1));
        assertThat(tableSize(SECRETS_CONTENT)).isEqualTo((secretContentsBefore + 1));
        newSecret = secretDAO.getSecretByName(newSecret.series().name()).get();
        assertThat(secretDAO.getSecrets(null, null)).containsOnly(secret1, secret2b, newSecret);
    }

    @Test
    public void createOrUpdateSecretWhenSecretExists() {
        String name = "newSecret";
        long firstId = secretDAO.createSecret(name, "content1", SecretDAOTest.cryptographer.computeHmac("content1".getBytes(StandardCharsets.UTF_8)), "creator1", ImmutableMap.of("foo", "bar"), 1000, "description1", "type1", ImmutableMap.of());
        long secondId = secretDAO.createOrUpdateSecret(name, "content2", SecretDAOTest.cryptographer.computeHmac("content2".getBytes(StandardCharsets.UTF_8)), "creator2", ImmutableMap.of("foo2", "bar2"), 2000, "description2", "type2", ImmutableMap.of());
        assertThat(secondId).isEqualTo(firstId);
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(firstId).get();
        assertThat(newSecret.series().createdBy()).isEqualTo("creator1");
        assertThat(newSecret.series().updatedBy()).isEqualTo("creator2");
        assertThat(newSecret.series().description()).isEqualTo("description2");
        assertThat(newSecret.series().type().get()).isEqualTo("type2");
        assertThat(newSecret.content().createdBy()).isEqualTo("creator2");
        assertThat(newSecret.content().encryptedContent()).isEqualTo("content2");
        assertThat(newSecret.content().metadata()).isEqualTo(ImmutableMap.of("foo2", "bar2"));
    }

    // ---------------------------------------------------------------------------------------
    // updateSecret
    // ---------------------------------------------------------------------------------------
    @Test(expected = NotFoundException.class)
    public void partialUpdateSecretWhenSecretSeriesDoesNotExist() {
        String name = "newSecret";
        String content = "c2VjcmV0MQ==";
        PartialUpdateSecretRequestV2 request = PartialUpdateSecretRequestV2.builder().contentPresent(true).content(content).build();
        secretDAO.partialUpdateSecret(name, "test", request);
    }

    @Test(expected = NotFoundException.class)
    public void partialUpdateSecretWhenSecretContentDoesNotExist() {
        String name = "newSecret";
        String content = "c2VjcmV0MQ==";
        PartialUpdateSecretRequestV2 request = PartialUpdateSecretRequestV2.builder().contentPresent(true).content(content).build();
        jooqContext.insertInto(SECRETS).set(ID, 12L).set(NAME, name).set(DESCRIPTION, series1.description()).set(CREATEDBY, series1.createdBy()).set(CREATEDAT, series1.createdAt().toEpochSecond()).set(UPDATEDBY, series1.updatedBy()).set(UPDATEDAT, series1.updatedAt().toEpochSecond()).set(CURRENT, 12L).execute();
        secretDAO.partialUpdateSecret(name, "test", request);
    }

    @Test(expected = NotFoundException.class)
    public void partialUpdateSecretWhenSecretCurrentIsNotSet() {
        String content = "c2VjcmV0MQ==";
        PartialUpdateSecretRequestV2 request = PartialUpdateSecretRequestV2.builder().contentPresent(true).content(content).build();
        secretDAO.partialUpdateSecret(series3.name(), "test", request);
    }

    @Test
    public void partialUpdateSecretWhenSecretExists() {
        // Update the content and set the type for series1
        long id = secretDAO.partialUpdateSecret(series1.name(), "creator1", PartialUpdateSecretRequestV2.builder().contentPresent(true).content("content1").typePresent(true).type("type1").build());
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(id).orElseThrow(IllegalStateException::new);
        assertThat(newSecret.series().createdBy()).isEqualTo("creator");
        assertThat(newSecret.series().updatedBy()).isEqualTo("creator1");
        assertThat(newSecret.series().description()).isEqualTo(series1.description());
        assertThat(newSecret.series().type().get()).isEqualTo("type1");
        assertThat(newSecret.content().createdBy()).isEqualTo("creator1");
        assertThat(newSecret.content().hmac()).isEqualTo(SecretDAOTest.cryptographer.computeHmac("content1".getBytes(StandardCharsets.UTF_8)));
        assertThat(newSecret.content().metadata()).isEqualTo(secret1.content().metadata());
        assertThat(newSecret.content().expiry()).isEqualTo(secret1.content().expiry());
        // Update the expiry and metadata for series2
        id = secretDAO.partialUpdateSecret(series2.name(), "creator2", PartialUpdateSecretRequestV2.builder().expiryPresent(true).expiry(12345L).metadataPresent(true).metadata(ImmutableMap.of("owner", "keywhiz-test")).build());
        newSecret = secretDAO.getSecretById(id).orElseThrow(IllegalStateException::new);
        assertThat(newSecret.series().createdBy()).isEqualTo("creator");
        assertThat(newSecret.series().updatedBy()).isEqualTo("creator2");
        assertThat(newSecret.series().description()).isEqualTo(series2.description());
        assertThat(newSecret.content().createdBy()).isEqualTo("creator2");
        assertThat(newSecret.content().hmac()).isEqualTo("checksum");
        assertThat(newSecret.content().metadata()).isEqualTo(ImmutableMap.of("owner", "keywhiz-test"));
        assertThat(newSecret.content().expiry()).isEqualTo(12345L);
    }

    // ---------------------------------------------------------------------------------------
    @Test
    public void getSecretByName() {
        String name = secret2b.series().name();
        assertThat(secretDAO.getSecretByName(name)).contains(secret2b);
    }

    @Test
    public void getSecretByNameOneReturnsEmptyWhenCurrentVersionIsNull() {
        String name = secret1.series().name();
        jooqContext.update(SECRETS).set(CURRENT, ((Long) (null))).where(ID.eq(series1.id())).execute();
        assertThat(secretDAO.getSecretByName(name)).isEmpty();
    }

    @Test
    public void getSecretByNameOneReturnsEmptyWhenRowIsMissing() {
        String name = "nonExistantSecret";
        assertThat(secretDAO.getSecretByName(name).isPresent()).isFalse();
        long newId = secretDAO.createSecret(name, "content", SecretDAOTest.cryptographer.computeHmac("content".getBytes(StandardCharsets.UTF_8)), "creator", ImmutableMap.of(), 0, "", null, ImmutableMap.of());
        SecretSeriesAndContent newSecret = secretDAO.getSecretById(newId).get();
        assertThat(secretDAO.getSecretByName(name).isPresent()).isTrue();
        jooqContext.deleteFrom(SECRETS_CONTENT).where(SECRETS_CONTENT.ID.eq(newSecret.content().id())).execute();
        assertThat(secretDAO.getSecretByName(name).isPresent()).isFalse();
    }

    @Test
    public void getSecretById() {
        assertThat(secretDAO.getSecretById(series2.id())).isEqualTo(Optional.of(secret2b));
    }

    @Test
    public void getSecretByIdOneReturnsEmptyWhenCurrentVersionIsNull() {
        jooqContext.update(SECRETS).set(CURRENT, ((Long) (null))).where(ID.eq(series2.id())).execute();
        assertThat(secretDAO.getSecretById(series2.id())).isEmpty();
    }

    @Test(expected = IllegalStateException.class)
    public void getSecretByIdOneThrowsExceptionIfCurrentVersionIsInvalid() {
        jooqContext.update(SECRETS).set(CURRENT, (-1234L)).where(ID.eq(series2.id())).execute();
        secretDAO.getSecretById(series2.id());
    }

    @Test
    public void getNonExistentSecret() {
        assertThat(secretDAO.getSecretByName("non-existent")).isEmpty();
        assertThat(secretDAO.getSecretById((-1231))).isEmpty();
    }

    @Test
    public void getSecrets() {
        assertThat(secretDAO.getSecrets(null, null)).containsOnly(secret1, secret2b);
    }

    @Test
    public void getSecretsByNameOnly() {
        assertThat(secretDAO.getSecretsNameOnly()).containsOnly(new java.util.AbstractMap.SimpleEntry(series1.id(), series1.name()), new java.util.AbstractMap.SimpleEntry(series2.id(), series2.name()));
    }

    @Test
    public void deleteSecretsByName() {
        secretDAO.createSecret("toBeDeleted_deleteSecretsByName", "encryptedShhh", SecretDAOTest.cryptographer.computeHmac("encryptedShhh".getBytes(StandardCharsets.UTF_8)), "creator", ImmutableMap.of(), 0, "", null, null);
        secretDAO.deleteSecretsByName("toBeDeleted_deleteSecretsByName");
        Optional<SecretSeriesAndContent> secret = secretDAO.getSecretByName("toBeDeleted_deleteSecretsByName");
        assertThat(secret.isPresent()).isFalse();
    }
}


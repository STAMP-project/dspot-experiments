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
import SECRETS.CURRENT;
import SECRETS.ID;
import SECRETS.NAME;
import SECRETS.UPDATEDAT;
import com.google.common.collect.ImmutableMap;
import java.time.OffsetDateTime;
import javax.inject.Inject;
import keywhiz.KeywhizTestRunner;
import keywhiz.api.ApiDate;
import keywhiz.api.model.SecretContent;
import keywhiz.service.daos.SecretContentDAO.SecretContentDAOFactory;
import org.jooq.DSLContext;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(KeywhizTestRunner.class)
public class SecretContentDAOTest {
    @Inject
    DSLContext jooqContext;

    @Inject
    SecretContentDAOFactory secretContentDAOFactory;

    static final ApiDate date = ApiDate.now();

    ImmutableMap<String, String> metadata = ImmutableMap.of("foo", "bar");

    SecretContent secretContent1 = SecretContent.of(11, 22, "[crypted]", "checksum", SecretContentDAOTest.date, "creator", SecretContentDAOTest.date, "creator", metadata, 1136214245);

    SecretContentDAO secretContentDAO;

    @Test
    public void createSecretContent() {
        int before = tableSize();
        secretContentDAO.createSecretContent(((secretContent1.secretSeriesId()) + 1), "encrypted", "checksum", "creator", metadata, 1136214245, OffsetDateTime.now().toEpochSecond());
        assertThat(tableSize()).isEqualTo((before + 1));
    }

    @Test
    public void pruneOldContents() throws Exception {
        long now = OffsetDateTime.now().toEpochSecond();
        long secretSeriesId = 666;
        jooqContext.insertInto(SECRETS, ID, NAME, CREATEDAT, UPDATEDAT).values(secretSeriesId, "secretForPruneTest1", now, now).execute();
        int before = tableSize();
        // Create contents
        long[] ids = new long[15];
        for (int i = 0; i < (ids.length); i++) {
            long id = secretContentDAO.createSecretContent(secretSeriesId, "encrypted", "checksum", "creator", metadata, 1136214245, now);
            ids[i] = id;
        }
        assertThat(tableSize()).isEqualTo((before + (ids.length)));
        // Update created_at to make all secrets older than cutoff
        jooqContext.update(SECRETS_CONTENT).set(SECRETS_CONTENT.CREATEDAT, 1L).execute();
        // Make most recent id be the current version for the secret series and prune
        jooqContext.update(SECRETS).set(CURRENT, ids[((ids.length) - 1)]).where(ID.eq(secretSeriesId)).execute();
        secretContentDAO.pruneOldContents(secretSeriesId);
        // Last ten secrets in series should have survived (plus the current one)
        assertThat(tableSize()).isEqualTo(((before + (SecretContentDAO.PRUNE_CUTOFF_ITEMS)) + 1));
        for (int i = 0; i < (((ids.length) - (SecretContentDAO.PRUNE_CUTOFF_ITEMS)) - 1); i++) {
            assertThat(secretContentDAO.getSecretContentById(ids[i]).isPresent()).isFalse();
        }
        for (int i = ((ids.length) - (SecretContentDAO.PRUNE_CUTOFF_ITEMS)) - 1; i < (ids.length); i++) {
            assertThat(secretContentDAO.getSecretContentById(ids[i]).isPresent()).isTrue();
        }
        // Other secrets contents left intact
        assertThat(secretContentDAO.getSecretContentById(secretContent1.id()).isPresent()).isTrue();
    }

    @Test
    public void pruneIgnores45DaysOrLess() throws Exception {
        long now = OffsetDateTime.now().toEpochSecond();
        long secretSeriesId = 666;
        jooqContext.insertInto(SECRETS, ID, NAME, CREATEDAT, UPDATEDAT).values(secretSeriesId, "secretForPruneTest2", now, now).execute();
        int before = tableSize();
        // Create contents
        long[] ids = new long[15];
        for (int i = 0; i < (ids.length); i++) {
            long id = secretContentDAO.createSecretContent(secretSeriesId, "encrypted", "checksum", "creator", metadata, 1136214245, now);
            ids[i] = id;
        }
        assertThat(tableSize()).isEqualTo((before + (ids.length)));
        // Make most recent id be the current version for the secret series and prune
        jooqContext.update(SECRETS).set(CURRENT, ids[((ids.length) - 1)]).where(ID.eq(secretSeriesId)).execute();
        secretContentDAO.pruneOldContents(secretSeriesId);
        // Nothing pruned
        for (int i = 0; i < (ids.length); i++) {
            assertThat(secretContentDAO.getSecretContentById(ids[i]).isPresent()).isTrue();
        }
    }

    @Test
    public void getSecretContentById() {
        assertThat(secretContentDAO.getSecretContentById(secretContent1.id())).contains(secretContent1);
    }
}


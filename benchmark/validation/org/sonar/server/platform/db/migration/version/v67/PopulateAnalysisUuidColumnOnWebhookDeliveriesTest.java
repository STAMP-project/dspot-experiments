/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v67;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.CoreDbTester;


public class PopulateAnalysisUuidColumnOnWebhookDeliveriesTest {
    private static final String TABLE_WEBHOOK_DELIVERY = "webhook_deliveries";

    private static final String TABLE_CE_ACTIVITIY = "ce_activity";

    @Rule
    public final CoreDbTester db = CoreDbTester.createForSchema(PopulateAnalysisUuidColumnOnWebhookDeliveriesTest.class, "initial.sql");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PopulateAnalysisUuidColumnOnWebhookDeliveries underTest = new PopulateAnalysisUuidColumnOnWebhookDeliveries(db.database());

    @Test
    public void migration_must_set_analysis_uuid() throws SQLException {
        String ceTaskUuid = randomAlphanumeric(40);
        String analysisUuid = randomAlphanumeric(40);
        String webhookDeliveryUuid = randomAlphanumeric(40);
        insertWebhookDelivery(webhookDeliveryUuid, null, ceTaskUuid);
        insertCeActivity(ceTaskUuid, analysisUuid);
        assertThat(db.countRowsOfTable(PopulateAnalysisUuidColumnOnWebhookDeliveriesTest.TABLE_WEBHOOK_DELIVERY)).isEqualTo(1);
        assertThat(db.countRowsOfTable(PopulateAnalysisUuidColumnOnWebhookDeliveriesTest.TABLE_CE_ACTIVITIY)).isEqualTo(1);
        underTest.execute();
        List<Map<String, Object>> maps = selectAllWebhookDeliveries();
        assertThat(maps).hasSize(1);
        assertThat(maps.get(0)).containsExactly(entry("ANALYSIS_UUID", analysisUuid), entry("UUID", webhookDeliveryUuid), entry("CE_TASK_UUID", ceTaskUuid));
    }

    @Test
    public void migration_should_be_reentrant() throws SQLException {
        for (int i = 0; i < 10; i++) {
            insertWebhookDelivery(randomAlphanumeric(40), null, randomAlphanumeric(40));
            insertCeActivity(randomAlphanumeric(40), randomAlphanumeric(40));
        }
        underTest.execute();
        List<Map<String, Object>> firstExecutionResult = selectAllWebhookDeliveries();
        underTest.execute();
        assertThat(selectAllWebhookDeliveries()).isEqualTo(firstExecutionResult);
    }
}


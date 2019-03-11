/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction;


import TransactionCoordinatorBuilderInitiator.LEGACY_SETTING_NAME;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.internal.TransactionCoordinatorBuilderInitiator;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class LegacySettingInitiatorTest extends BaseUnitTestCase {
    private BootstrapServiceRegistryImpl bsr;

    @Test
    public void testLegacySettingSelection() {
        final TransactionCoordinatorBuilderInitiator initiator = new TransactionCoordinatorBuilderInitiator();
        TransactionCoordinatorBuilder builder = initiator.initiateService(Collections.singletonMap(LEGACY_SETTING_NAME, "org.hibernate.transaction.JDBCTransactionFactory"), bsr);
        Assert.assertThat(builder, CoreMatchers.instanceOf(JdbcResourceLocalTransactionCoordinatorBuilderImpl.class));
        builder = initiator.initiateService(Collections.singletonMap(LEGACY_SETTING_NAME, "org.hibernate.transaction.JTATransactionFactory"), bsr);
        Assert.assertThat(builder, CoreMatchers.instanceOf(JtaTransactionCoordinatorBuilderImpl.class));
        builder = initiator.initiateService(Collections.singletonMap(LEGACY_SETTING_NAME, "org.hibernate.transaction.CMTTransactionFactory"), bsr);
        Assert.assertThat(builder, CoreMatchers.instanceOf(JtaTransactionCoordinatorBuilderImpl.class));
    }
}


/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.namingstrategy.complete;


import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public abstract class BaseNamingTests extends BaseUnitTestCase {
    @Test
    public void doTest() {
        final MetadataSources metadataSources = new MetadataSources();
        try {
            applySources(metadataSources);
            final Metadata metadata = metadataSources.getMetadataBuilder().applyImplicitNamingStrategy(getImplicitNamingStrategyToUse()).build();
            validateCustomer(metadata);
            validateOrder(metadata);
            validateZipCode(metadata);
            validateCustomerRegisteredTrademarks(metadata);
            validateCustomerAddresses(metadata);
            validateCustomerOrders(metadata);
            validateCustomerIndustries(metadata);
        } finally {
            ServiceRegistry metaServiceRegistry = metadataSources.getServiceRegistry();
            if (metaServiceRegistry instanceof BootstrapServiceRegistry) {
                BootstrapServiceRegistryBuilder.destroy(metaServiceRegistry);
            }
        }
    }
}


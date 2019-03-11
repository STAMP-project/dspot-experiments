/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.namingstrategy.collectionJoinTableNaming;


import ImplicitNamingStrategyLegacyJpaImpl.INSTANCE;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.mapping.Collection;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Alessandro Polverini
 */
public class CollectionJoinTableNamingTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9908")
    public void testCollectionJoinTableNamingBase() {
        // really the same as the JPA compliant tests; here we just pick up the default ImplicitNamingStrategy
        final MetadataSources metadataSources = new MetadataSources();
        try {
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Input.class);
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Ptx.class);
            final Metadata metadata = metadataSources.getMetadataBuilder().build();
            assertSameTableUsed(metadata);
        } finally {
            ServiceRegistry metaServiceRegistry = metadataSources.getServiceRegistry();
            if (metaServiceRegistry instanceof BootstrapServiceRegistry) {
                BootstrapServiceRegistryBuilder.destroy(metaServiceRegistry);
            }
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9908")
    public void testCollectionJoinTableNamingLegacyJpaStrategy() {
        final MetadataSources metadataSources = new MetadataSources();
        try {
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Input.class);
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Ptx.class);
            final Metadata metadata = metadataSources.getMetadataBuilder().applyImplicitNamingStrategy(INSTANCE).build();
            assertSameTableUsed(metadata);
        } finally {
            ServiceRegistry metaServiceRegistry = metadataSources.getServiceRegistry();
            if (metaServiceRegistry instanceof BootstrapServiceRegistry) {
                BootstrapServiceRegistryBuilder.destroy(metaServiceRegistry);
            }
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9908")
    public void testCollectionJoinTableNamingLegacyHbmStrategy() {
        final MetadataSources metadataSources = new MetadataSources();
        try {
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Input.class);
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Ptx.class);
            final Metadata metadata = metadataSources.getMetadataBuilder().applyImplicitNamingStrategy(ImplicitNamingStrategyLegacyHbmImpl.INSTANCE).build();
            Collection inputs1Mapping = metadata.getCollectionBinding(((CollectionJoinTableNamingTest.Ptx.class.getName()) + ".inputs1"));
            Assert.assertEquals("ptx_inputs1", inputs1Mapping.getCollectionTable().getName());
            Collection inputs2Mapping = metadata.getCollectionBinding(((CollectionJoinTableNamingTest.Ptx.class.getName()) + ".inputs2"));
            Assert.assertEquals("ptx_inputs2", inputs2Mapping.getCollectionTable().getName());
        } finally {
            ServiceRegistry metaServiceRegistry = metadataSources.getServiceRegistry();
            if (metaServiceRegistry instanceof BootstrapServiceRegistry) {
                BootstrapServiceRegistryBuilder.destroy(metaServiceRegistry);
            }
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9908")
    public void testCollectionJoinTableNamingJpaCompliantStrategy() {
        // Even in 4.3, with JPA compliant naming, Hibernate creates an unusable table...
        final MetadataSources metadataSources = new MetadataSources();
        try {
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Input.class);
            metadataSources.addAnnotatedClass(CollectionJoinTableNamingTest.Ptx.class);
            final Metadata metadata = metadataSources.getMetadataBuilder().applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE).build();
            assertSameTableUsed(metadata);
        } finally {
            ServiceRegistry metaServiceRegistry = metadataSources.getServiceRegistry();
            if (metaServiceRegistry instanceof BootstrapServiceRegistry) {
                BootstrapServiceRegistryBuilder.destroy(metaServiceRegistry);
            }
        }
    }

    @Entity
    @Table(name = "ptx")
    public static class Ptx {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        private Integer id;

        @OrderColumn
        @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
        private List<CollectionJoinTableNamingTest.Input> inputs1;

        @OrderColumn
        @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
        private List<CollectionJoinTableNamingTest.Input> inputs2;
    }

    @Entity
    @Table(name = "input")
    public class Input implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        private Integer id;
    }
}


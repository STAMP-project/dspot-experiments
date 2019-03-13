/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.converter.custom;


import Action.CREATE_DROP;
import AvailableSettings.HBM2DDL_AUTO;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry.INSTANCE;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CustomTypeConverterTest extends BaseUnitTestCase {
    @Test
    public void testConverterAppliedStaticRegistration() {
        // this is how we told users to do it previously using the static reference -
        // make sure it still works for now
        INSTANCE.addDescriptor(MyCustomJavaTypeDescriptor.INSTANCE);
        org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry.INSTANCE.addDescriptor(MyCustomSqlTypeDescriptor.INSTANCE);
        try (final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, CREATE_DROP).build()) {
            final MetadataSources metadataSources = addAnnotatedClass(MyCustomConverter.class).addAnnotatedClass(MyEntity.class);
            final MetadataBuilderImplementor metadataBuilder = ((MetadataBuilderImplementor) (metadataSources.getMetadataBuilder()));
            final TypeConfiguration bootTypeConfiguration = metadataBuilder.getBootstrapContext().getTypeConfiguration();
            performAssertions(metadataBuilder, bootTypeConfiguration);
        }
    }

    @Test
    public void testConverterAppliedScopedRegistration() {
        try (final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, CREATE_DROP).build()) {
            final MetadataSources metadataSources = addAnnotatedClass(MyCustomConverter.class).addAnnotatedClass(MyEntity.class);
            final MetadataBuilderImplementor metadataBuilder = ((MetadataBuilderImplementor) (metadataSources.getMetadataBuilder()));
            // now the new scoped way
            final TypeConfiguration bootTypeConfiguration = metadataBuilder.getBootstrapContext().getTypeConfiguration();
            bootTypeConfiguration.getJavaTypeDescriptorRegistry().addDescriptor(MyCustomJavaTypeDescriptor.INSTANCE);
            bootTypeConfiguration.getSqlTypeDescriptorRegistry().addDescriptor(MyCustomSqlTypeDescriptor.INSTANCE);
            performAssertions(metadataBuilder, bootTypeConfiguration);
        }
    }
}


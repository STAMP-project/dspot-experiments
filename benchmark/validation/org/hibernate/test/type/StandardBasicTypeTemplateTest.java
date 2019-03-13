/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.type;


import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypeTemplate;
import org.hibernate.type.descriptor.java.UrlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.Test;


/**
 * Test making sure StandardBasicTypeTemplate works
 *
 * @author Steve Ebersole
 */
public class StandardBasicTypeTemplateTest extends BaseUnitTestCase {
    public static final String REG_KEY = "validating-url";

    @Test
    public void testContributedBasicType() {
        TypeConfiguration typeConfiguration = new TypeConfiguration();
        typeConfiguration.getJavaTypeDescriptorRegistry().addDescriptor(StandardBasicTypeTemplateTest.ValidatingUrlJavaTypeDescriptor.INSTANCE);
        typeConfiguration.getBasicTypeRegistry().register(new StandardBasicTypeTemplate(VarcharTypeDescriptor.INSTANCE, StandardBasicTypeTemplateTest.ValidatingUrlJavaTypeDescriptor.INSTANCE, StandardBasicTypeTemplateTest.REG_KEY));
        final BasicType registeredType = typeConfiguration.getBasicTypeRegistry().getRegisteredType(StandardBasicTypeTemplateTest.REG_KEY);
        MatcherAssert.assertThat(registeredType, CoreMatchers.notNullValue());
        ExtraAssertions.assertTyping(StandardBasicTypeTemplate.class, registeredType);
    }

    private static class ValidatingUrlJavaTypeDescriptor extends UrlTypeDescriptor {
        /**
         * Singleton access
         */
        public static final StandardBasicTypeTemplateTest.ValidatingUrlJavaTypeDescriptor INSTANCE = new StandardBasicTypeTemplateTest.ValidatingUrlJavaTypeDescriptor();

        @Override
        public URL fromString(String string) {
            if ("invalid".equals(string)) {
                throw new IllegalStateException("Invalid url");
            }
            return super.fromString(string);
        }
    }
}


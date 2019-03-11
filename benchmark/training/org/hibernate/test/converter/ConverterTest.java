/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.Type;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-attribute-converter-query-parameter-entity-example[]
@TestForIssue(jiraKey = "HHH-12662")
public class ConverterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testJPQLUpperDbValueBindParameter() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::basic-attribute-converter-query-parameter-converter-dbdata-example[]
            org.hibernate.test.converter.Photo photo = entityManager.createQuery(("select p " + ("from Photo p " + "where upper(caption) = upper(:caption) ")), .class).setParameter("caption", "Nicolae Grigorescu").getSingleResult();
            // end::basic-attribute-converter-query-parameter-converter-dbdata-example[]
            assertEquals("Dorobantul", photo.getName());
        });
    }

    @Test
    public void testJPQLUpperAttributeValueBindParameterType() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::basic-attribute-converter-query-parameter-converter-object-example[]
            SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(.class);
            MetamodelImplementor metamodelImplementor = ((MetamodelImplementor) (sessionFactory.getMetamodel()));
            Type captionType = metamodelImplementor.entityPersister(.class.getName()).getPropertyType("caption");
            org.hibernate.test.converter.Photo photo = ((org.hibernate.test.converter.Photo) (entityManager.createQuery(("select p " + ("from Photo p " + "where upper(caption) = upper(:caption) ")), .class).unwrap(.class).setParameter("caption", new org.hibernate.test.converter.Caption("Nicolae Grigorescu"), captionType).getSingleResult()));
            // end::basic-attribute-converter-query-parameter-converter-object-example[]
            assertEquals("Dorobantul", photo.getName());
        });
    }

    // tag::basic-attribute-converter-query-parameter-object-example[]
    public static class Caption {
        private String text;

        public Caption(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConverterTest.Caption caption = ((ConverterTest.Caption) (o));
            return (text) != null ? text.equals(caption.text) : (caption.text) == null;
        }

        @Override
        public int hashCode() {
            return (text) != null ? text.hashCode() : 0;
        }
    }

    // end::basic-attribute-converter-query-parameter-object-example[]
    // tag::basic-attribute-converter-query-parameter-converter-example[]
    public static class CaptionConverter implements AttributeConverter<ConverterTest.Caption, String> {
        @Override
        public String convertToDatabaseColumn(ConverterTest.Caption attribute) {
            return attribute.getText();
        }

        @Override
        public ConverterTest.Caption convertToEntityAttribute(String dbData) {
            return new ConverterTest.Caption(dbData);
        }
    }

    // end::basic-attribute-converter-query-parameter-converter-example[]
    // tag::basic-attribute-converter-query-parameter-entity-example[]
    // tag::basic-attribute-converter-query-parameter-entity-example[]
    @Entity(name = "Photo")
    public static class Photo {
        @Id
        private Integer id;

        private String name;

        @Convert(converter = ConverterTest.CaptionConverter.class)
        private ConverterTest.Caption caption;

        // Getters and setters are omitted for brevity
        // end::basic-attribute-converter-query-parameter-entity-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ConverterTest.Caption getCaption() {
            return caption;
        }

        public void setCaption(ConverterTest.Caption caption) {
            this.caption = caption;
        }
    }
}


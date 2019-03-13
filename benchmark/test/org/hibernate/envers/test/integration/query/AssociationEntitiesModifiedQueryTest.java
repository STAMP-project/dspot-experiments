/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11981")
public class AssociationEntitiesModifiedQueryTest extends BaseEnversJPAFunctionalTestCase {
    @Entity(name = "TemplateType")
    @Audited(withModifiedFlag = true)
    public static class TemplateType {
        @Id
        private Integer id;

        private String name;

        TemplateType() {
            this(null, null);
        }

        TemplateType(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

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
    }

    @Entity(name = "Template")
    @Audited(withModifiedFlag = true)
    public static class Template {
        @Id
        private Integer id;

        private String name;

        @ManyToOne
        private AssociationEntitiesModifiedQueryTest.TemplateType templateType;

        Template() {
            this(null, null, null);
        }

        Template(Integer id, String name, AssociationEntitiesModifiedQueryTest.TemplateType type) {
            this.id = id;
            this.name = name;
            this.templateType = type;
        }

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

        public AssociationEntitiesModifiedQueryTest.TemplateType getTemplateType() {
            return templateType;
        }

        public void setTemplateType(AssociationEntitiesModifiedQueryTest.TemplateType templateType) {
            this.templateType = templateType;
        }
    }

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.TemplateType type1 = new org.hibernate.envers.test.integration.query.TemplateType(1, "Type1");
            final org.hibernate.envers.test.integration.query.TemplateType type2 = new org.hibernate.envers.test.integration.query.TemplateType(2, "Type2");
            final org.hibernate.envers.test.integration.query.Template template = new org.hibernate.envers.test.integration.query.Template(1, "Template1", type1);
            entityManager.persist(type1);
            entityManager.persist(type2);
            entityManager.persist(template);
        });
        // Revision 2
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.TemplateType type = entityManager.find(.class, 2);
            final org.hibernate.envers.test.integration.query.Template template = entityManager.find(.class, 1);
            template.setTemplateType(type);
            entityManager.merge(template);
        });
        // Revision 3
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.Template template = entityManager.find(.class, 1);
            entityManager.remove(template);
        });
    }

    @Test
    public void testEntitiesModifiedAtRevision1WithAssociationQueries() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getEntitiesModifiedAtRevisionUsingAssociationQueryResults(1);
            assertEquals(1, results.size());
            assertEquals("Type1", ((org.hibernate.envers.test.integration.query.TemplateType) (results.get(0))).getName());
        });
    }

    @Test
    public void testEntitiesModifiedAtRevision2WithAssociationQueries() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getEntitiesModifiedAtRevisionUsingAssociationQueryResults(2);
            assertEquals(1, results.size());
            assertEquals("Type2", ((org.hibernate.envers.test.integration.query.TemplateType) (results.get(0))).getName());
        });
    }

    @Test
    public void testEntitiesModifiedAtRevision3WithAssociationQueries() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List results = getEntitiesModifiedAtRevisionUsingAssociationQueryResults(3);
            assertEquals(0, results.size());
        });
    }
}


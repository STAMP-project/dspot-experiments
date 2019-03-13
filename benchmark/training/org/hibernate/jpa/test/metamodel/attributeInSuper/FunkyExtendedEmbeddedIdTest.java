/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel.attributeInSuper;


import javax.persistence.metamodel.EmbeddableType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 * An attempt at defining a test based on the HHH-8712 bug report
 */
public class FunkyExtendedEmbeddedIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8712")
    public void ensureAttributeForEmbeddableIsGeneratedInMappedSuperClass() {
        EmbeddableType<WorkOrderComponentId> woci = entityManagerFactory().getMetamodel().embeddable(WorkOrderComponentId.class);
        MatcherAssert.assertThat(woci, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(woci.getAttribute("workOrder"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(woci.getAttribute("plantId"), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(woci.getAttribute("lineNumber"), CoreMatchers.notNullValue());
    }
}


/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.modifiedflags.entities.Professor;
import org.hibernate.envers.test.integration.modifiedflags.entities.Student;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7510")
public class HasChangedAuditedManyToManyRemovalTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        EntityManager entityManager = getEntityManager();
        try {
            // Revision 1 - insertion
            Professor professor = new Professor();
            Student student = new Student();
            professor.getStudents().add(student);
            student.getProfessors().add(professor);
            entityManager.getTransaction().begin();
            entityManager.persist(professor);
            entityManager.persist(student);
            entityManager.getTransaction().commit();
            entityManager.clear();
            // Revision 2 - deletion
            entityManager.getTransaction().begin();
            professor = entityManager.find(Professor.class, professor.getId());
            student = entityManager.find(Student.class, student.getId());
            entityManager.remove(professor);
            entityManager.remove(student);
            // the issue is student.getProfessors() throws a LazyInitializationException.
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }
}


/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.loadplans.walking;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.MetamodelGraphWalker;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicWalkingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIt() {
        EntityPersister ep = ((EntityPersister) (sessionFactory().getClassMetadata(BasicWalkingTest.Message.class)));
        MetamodelGraphWalker.visitEntity(new LoggingAssociationVisitationStrategy(), ep);
    }

    @Entity(name = "Message")
    public static class Message {
        @Id
        private Integer id;

        private String name;

        @ManyToOne
        @JoinColumn
        private BasicWalkingTest.Poster poster;
    }

    @Entity(name = "Poster")
    public static class Poster {
        @Id
        private Integer id;

        private String name;

        @OneToMany(mappedBy = "poster")
        private List<BasicWalkingTest.Message> messages;
    }
}


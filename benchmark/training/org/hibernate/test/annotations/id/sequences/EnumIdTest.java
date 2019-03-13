/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.id.sequences;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.id.sequences.entities.Planet;
import org.hibernate.test.annotations.id.sequences.entities.PlanetCheatSheet;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for enum type as id.
 *
 * @author Hardy Ferentschik
 */
@SuppressWarnings("unchecked")
@TestForIssue(jiraKey = "ANN-744")
public class EnumIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEnumAsId() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        PlanetCheatSheet mercury = new PlanetCheatSheet();
        mercury.setPlanet(Planet.MERCURY);
        mercury.setMass(3.303E23);
        mercury.setRadius(2439700.0);
        mercury.setNumberOfInhabitants(0);
        s.persist(mercury);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        PlanetCheatSheet mercuryFromDb = ((PlanetCheatSheet) (s.get(PlanetCheatSheet.class, mercury.getPlanet())));
        Assert.assertNotNull(mercuryFromDb);
        log.debug(mercuryFromDb.toString());
        s.delete(mercuryFromDb);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        mercury = ((PlanetCheatSheet) (s.get(PlanetCheatSheet.class, Planet.MERCURY)));
        Assert.assertNull(mercury);
        tx.commit();
        s.close();
    }
}


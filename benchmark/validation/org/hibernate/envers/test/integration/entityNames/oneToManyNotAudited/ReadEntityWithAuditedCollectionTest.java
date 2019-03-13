/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.entityNames.oneToManyNotAudited;


import java.util.ArrayList;
import java.util.List;
import org.hibernate.envers.test.AbstractOneSessionTest;
import org.hibernate.envers.test.Priority;
import org.junit.Test;


/**
 *
 *
 * @author Hern&aacute;n Chanfreau
 */
public class ReadEntityWithAuditedCollectionTest extends AbstractOneSessionTest {
    private long id_car1;

    private long id_car2;

    private Car currentCar1;

    private Person currentPerson1;

    private long id_pers1;

    private Car car1_1;

    @Test
    @Priority(10)
    public void initData() {
        initializeSession();
        Person pers1 = new Person("Hernan", 28);
        Person pers2 = new Person("Leandro", 29);
        Person pers4 = new Person("Camomo", 15);
        List<Person> owners = new ArrayList<Person>();
        owners.add(pers1);
        owners.add(pers2);
        Car car1 = new Car(5, owners);
        // REV 1
        getSession().getTransaction().begin();
        getSession().persist(car1);
        getSession().getTransaction().commit();
        id_pers1 = pers1.getId();
        id_car1 = car1.getId();
        owners = new ArrayList<Person>();
        owners.add(pers2);
        owners.add(pers4);
        Car car2 = new Car(27, owners);
        // REV 2
        getSession().getTransaction().begin();
        Person person1 = ((Person) (getSession().get("Personaje", id_pers1)));
        person1.setName("Hernan David");
        person1.setAge(40);
        getSession().persist(car1);
        getSession().persist(car2);
        getSession().getTransaction().commit();
        id_car2 = car2.getId();
    }

    @Test
    public void testObtainEntityNameCollectionWithEntityNameAndNotAuditedMode() {
        loadDataOnSessionAndAuditReader();
        checkEntityNames();
    }

    @Test
    public void testObtainEntityNameCollectionWithEntityNameAndNotAuditedModeInNewSession() {
        // force new session and AR
        forceNewSession();
        loadDataOnSessionAndAuditReader();
        checkEntityNames();
    }
}


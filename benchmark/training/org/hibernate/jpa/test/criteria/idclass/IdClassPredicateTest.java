/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.idclass;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import junit.framework.Assert;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.junit.Test;


/**
 *
 *
 * @author Erich Heard
 */
public class IdClassPredicateTest extends AbstractMetamodelSpecificTest {
    @Test
    public void testCountIdClassAttributes() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Widget> path = cq.from(Widget.class);
        Expression<Long> countSelection = cb.count(path);
        cq.select(countSelection);
        Long count = em.createQuery(cq).getSingleResult();
        // // Packaging arguments for use in query.
        // List<String> divisions = new ArrayList<String>( );
        // divisions.add( "NA" );
        // divisions.add( "EU" );
        // 
        // // Building the query.
        // CriteriaBuilder criteria = em.getCriteriaBuilder( );
        // CriteriaQuery<Widget> query = criteria.createQuery( Widget.class );
        // Root<Widget> root = query.from( Widget.class );
        // 
        // Predicate predicate = root.get( "division" ).in( divisions );
        // query.where( predicate );
        // 
        // // Retrieving query.;
        // List<Widget> widgets = em.createQuery( query ).getResultList( );
        // Assert.assertEquals( 4, widgets.size() );
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testDeclaredIdClassAttributes() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        // Packaging arguments for use in query.
        List<String> divisions = new ArrayList<String>();
        divisions.add("NA");
        divisions.add("EU");
        // Building the query.
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<Widget> query = criteria.createQuery(Widget.class);
        Root<Widget> root = query.from(Widget.class);
        Predicate predicate = root.get("division").in(divisions);
        query.where(predicate);
        // Retrieving query.;
        List<Widget> widgets = em.createQuery(query).getResultList();
        Assert.assertEquals(4, widgets.size());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testSupertypeIdClassAttributes() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        // Packaging arguments for use in query.
        List<String> types = new ArrayList<String>();
        types.add("NA");
        types.add("EU");
        // Building the query.
        CriteriaBuilder criteria = em.getCriteriaBuilder();
        CriteriaQuery<Tool> query = criteria.createQuery(Tool.class);
        Root<Tool> root = query.from(Tool.class);
        Predicate predicate = root.get("type").in(types);
        query.where(predicate);
        // Retrieving query.
        List<Tool> tools = em.createQuery(query).getResultList();
        Assert.assertEquals(4, tools.size());
        em.getTransaction().commit();
        em.close();
    }
}


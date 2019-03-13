/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.mapjoin;


import MapEntityLocal_.shortName;
import MapEntity_.localized;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.MapEntity;
import org.hibernate.jpa.test.metamodel.MapEntityLocal;
import org.junit.Test;


public class MapJoinTest extends AbstractMetamodelSpecificTest {
    @Test
    public void allEntities() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MapEntity> query = cb.createQuery(MapEntity.class);
        Root<MapEntity> entity = query.from(MapEntity.class);
        MapJoin<MapEntity, String, MapEntityLocal> cname = entity.join(localized);
        query = query.select(entity).where(cb.equal(cname.key(), "en")).orderBy(cb.asc(cb.upper(cname.value().get(shortName))));
        em.createQuery(query).getResultList();
        em.getTransaction().commit();
        em.close();
    }
}


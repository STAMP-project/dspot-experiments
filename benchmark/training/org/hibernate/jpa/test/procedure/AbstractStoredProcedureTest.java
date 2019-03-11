/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import IntegerType.INSTANCE;
import ParameterStrategy.NAMED;
import ParameterStrategy.POSITIONAL;
import ProcedureCallMementoImpl.ParameterMemento;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode.IN;
import javax.persistence.ParameterMode.INOUT;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.procedure.internal.ProcedureCallMementoImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Strong Liu <stliu@hibernate.org>
 */
public abstract class AbstractStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testNamedStoredProcedureBinding() {
        EntityManager em = getOrCreateEntityManager();
        SessionFactoryImplementor sf = em.getEntityManagerFactory().unwrap(SessionFactoryImplementor.class);
        final ProcedureCallMementoImpl m1 = ((ProcedureCallMementoImpl) (sf.getNamedQueryRepository().getNamedProcedureCallMemento("s1")));
        Assert.assertNotNull(m1);
        Assert.assertEquals("p1", m1.getProcedureName());
        Assert.assertEquals(NAMED, m1.getParameterStrategy());
        List<ProcedureCallMementoImpl.ParameterMemento> list = m1.getParameterDeclarations();
        Assert.assertEquals(2, list.size());
        ProcedureCallMementoImpl.ParameterMemento memento = list.get(0);
        Assert.assertEquals("p11", memento.getName());
        Assert.assertEquals(IN, memento.getMode());
        Assert.assertEquals(INSTANCE, memento.getHibernateType());
        Assert.assertEquals(Integer.class, memento.getType());
        memento = list.get(1);
        Assert.assertEquals("p12", memento.getName());
        Assert.assertEquals(IN, memento.getMode());
        Assert.assertEquals(INSTANCE, memento.getHibernateType());
        Assert.assertEquals(Integer.class, memento.getType());
        final ProcedureCallMementoImpl m2 = ((ProcedureCallMementoImpl) (sf.getNamedQueryRepository().getNamedProcedureCallMemento("s2")));
        Assert.assertNotNull(m2);
        Assert.assertEquals("p2", m2.getProcedureName());
        Assert.assertEquals(POSITIONAL, m2.getParameterStrategy());
        list = m2.getParameterDeclarations();
        memento = list.get(0);
        Assert.assertEquals(Integer.valueOf(1), memento.getPosition());
        Assert.assertEquals(INOUT, memento.getMode());
        Assert.assertEquals(StringType.INSTANCE, memento.getHibernateType());
        Assert.assertEquals(String.class, memento.getType());
        memento = list.get(1);
        Assert.assertEquals(Integer.valueOf(2), memento.getPosition());
        Assert.assertEquals(INOUT, memento.getMode());
        Assert.assertEquals(LongType.INSTANCE, memento.getHibernateType());
        Assert.assertEquals(Long.class, memento.getType());
    }
}


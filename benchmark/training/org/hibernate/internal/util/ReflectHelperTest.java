/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal.util;


import FetchType.LAZY;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.FetchType;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.hib3rnat3.C0nst4nts?;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ReflectHelperTest {
    public enum Status {

        ON,
        OFF;}

    interface A {
        Integer getId();

        void setId(Integer id);

        default ReflectHelperTest.Status getStatus() {
            return ReflectHelperTest.Status.ON;
        }

        default void setId(String id) {
            this.setId(Integer.valueOf(id));
        }
    }

    interface B extends ReflectHelperTest.A {
        String getName();
    }

    interface C extends ReflectHelperTest.B {
        String getData();
    }

    class D implements ReflectHelperTest.C {
        @Override
        public Integer getId() {
            return null;
        }

        @Override
        public void setId(Integer id) {
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getData() {
            return null;
        }
    }

    class E extends ReflectHelperTest.D {}

    class F extends ReflectHelperTest.D {
        public ReflectHelperTest.Status getStatus() {
            return ReflectHelperTest.Status.OFF;
        }
    }

    private SessionFactoryImplementor sessionFactoryImplementorMock;

    private SessionFactoryOptions sessionFactoryOptionsMock;

    private ServiceRegistryImplementor serviceRegistryMock;

    private ClassLoaderService classLoaderServiceMock;

    @Test
    public void test_getConstantValue_simpleAlias() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Object value = ReflectHelper.getConstantValue("alias.b", sessionFactoryImplementorMock);
        Assert.assertNull(value);
        Mockito.verify(classLoaderServiceMock, Mockito.never()).classForName(ArgumentMatchers.anyString());
    }

    @Test
    public void test_getConstantValue_simpleAlias_non_conventional() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(false);
        Object value = ReflectHelper.getConstantValue("alias.b", sessionFactoryImplementorMock);
        Assert.assertNull(value);
        Mockito.verify(classLoaderServiceMock, Mockito.times(1)).classForName(ArgumentMatchers.eq("alias"));
    }

    @Test
    public void test_getConstantValue_nestedAlias() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Object value = ReflectHelper.getConstantValue("alias.b.c", sessionFactoryImplementorMock);
        Assert.assertNull(value);
        Mockito.verify(classLoaderServiceMock, Mockito.never()).classForName(ArgumentMatchers.anyString());
    }

    @Test
    public void test_getConstantValue_nestedAlias_non_conventional() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(false);
        Object value = ReflectHelper.getConstantValue("alias.b.c", sessionFactoryImplementorMock);
        Assert.assertNull(value);
        Mockito.verify(classLoaderServiceMock, Mockito.times(1)).classForName(ArgumentMatchers.eq("alias.b"));
    }

    @Test
    public void test_getConstantValue_outerEnum() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Mockito.when(classLoaderServiceMock.classForName("javax.persistence.FetchType")).thenReturn(((Class) (FetchType.class)));
        Object value = ReflectHelper.getConstantValue("javax.persistence.FetchType.LAZY", sessionFactoryImplementorMock);
        Assert.assertEquals(LAZY, value);
        Mockito.verify(classLoaderServiceMock, Mockito.times(1)).classForName(ArgumentMatchers.eq("javax.persistence.FetchType"));
    }

    @Test
    public void test_getConstantValue_enumClass() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Mockito.when(classLoaderServiceMock.classForName("org.hibernate.internal.util.ReflectHelperTest$Status")).thenReturn(((Class) (ReflectHelperTest.Status.class)));
        Object value = ReflectHelper.getConstantValue("org.hibernate.internal.util.ReflectHelperTest$Status", sessionFactoryImplementorMock);
        Assert.assertNull(value);
        Mockito.verify(classLoaderServiceMock, Mockito.never()).classForName(ArgumentMatchers.eq("org.hibernate.internal.util"));
    }

    @Test
    public void test_getConstantValue_nestedEnum() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Mockito.when(classLoaderServiceMock.classForName("org.hibernate.internal.util.ReflectHelperTest$Status")).thenReturn(((Class) (ReflectHelperTest.Status.class)));
        Object value = ReflectHelper.getConstantValue("org.hibernate.internal.util.ReflectHelperTest$Status.ON", sessionFactoryImplementorMock);
        Assert.assertEquals(ReflectHelperTest.Status.ON, value);
        Mockito.verify(classLoaderServiceMock, Mockito.times(1)).classForName(ArgumentMatchers.eq("org.hibernate.internal.util.ReflectHelperTest$Status"));
    }

    @Test
    public void test_getConstantValue_constant_digits() {
        Mockito.when(sessionFactoryOptionsMock.isConventionalJavaConstants()).thenReturn(true);
        Mockito.when(classLoaderServiceMock.classForName("org.hibernate.internal.util.hib3rnat3.C0nst4nts?")).thenReturn(((Class) (C0nst4nts?.class)));
        Object value = ReflectHelper.getConstantValue("org.hibernate.internal.util.hib3rnat3.C0nst4nts?.ABC_DEF", sessionFactoryImplementorMock);
        Assert.assertEquals(C0nst4nts?.ABC_DEF, value);
        Mockito.verify(classLoaderServiceMock, Mockito.times(1)).classForName(ArgumentMatchers.eq("org.hibernate.internal.util.hib3rnat3.C0nst4nts?"));
    }

    @Test
    public void test_getMethod_nestedInterfaces() {
        Assert.assertNotNull(ReflectHelper.findGetterMethod(ReflectHelperTest.C.class, "id"));
    }

    @Test
    public void test_getMethod_superclass() {
        Assert.assertNotNull(ReflectHelper.findGetterMethod(ReflectHelperTest.E.class, "id"));
    }

    @Test
    public void test_setMethod_nestedInterfaces() {
        Assert.assertNotNull(ReflectHelper.findSetterMethod(ReflectHelperTest.C.class, "id", Integer.class));
    }

    @TestForIssue(jiraKey = "HHH-12090")
    @Test
    public void test_getMethod_nestedInterfaces_on_superclasses() throws IllegalAccessException, InvocationTargetException {
        Method statusMethodEClass = ReflectHelper.findGetterMethod(ReflectHelperTest.E.class, "status");
        Assert.assertNotNull(statusMethodEClass);
        Assert.assertEquals(ReflectHelperTest.Status.ON, statusMethodEClass.invoke(new ReflectHelperTest.E()));
        Method statusMethodFClass = ReflectHelper.findGetterMethod(ReflectHelperTest.F.class, "status");
        Assert.assertNotNull(statusMethodFClass);
        Assert.assertEquals(ReflectHelperTest.Status.OFF, statusMethodFClass.invoke(new ReflectHelperTest.F()));
    }

    @TestForIssue(jiraKey = "HHH-12090")
    @Test
    public void test_setMethod_nestedInterfaces_on_superclasses() {
        Assert.assertNotNull(ReflectHelper.findSetterMethod(ReflectHelperTest.E.class, "id", String.class));
    }
}


package org.jboss.as.test.integration.web.handlestypes;


import java.util.Arrays;
import java.util.HashSet;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class HandlesTypesEarTestCase {
    @Test
    public void testParentClass() {
        Class<?>[] expeccted = new Class<?>[]{ HandlesTypesChild.class, HandlesTypesImplementor.class, HandlesTypesGandchild.class, HandlesTypesImplementorChild.class };
        Assert.assertEquals(new HashSet<>(Arrays.asList(expeccted)), ParentServletContainerInitializer.HANDLES_TYPES);
    }

    @Test
    public void testChildClass() {
        Class<?>[] expeccted = new Class<?>[]{ HandlesTypesGandchild.class, HandlesTypesImplementorChild.class };
        Assert.assertEquals(new HashSet<>(Arrays.asList(expeccted)), ChildServletContainerInitializer.HANDLES_TYPES);
    }

    @Test
    public void testAnnotatedClass() {
        Class<?>[] expeccted = new Class<?>[]{ AnnotatedParent.class, AnnotatedChild.class };
        Assert.assertEquals(new HashSet<>(Arrays.asList(expeccted)), AnnotationServletContainerInitializer.HANDLES_TYPES);
    }
}


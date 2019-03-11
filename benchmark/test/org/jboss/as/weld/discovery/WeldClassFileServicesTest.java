/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.weld.discovery;


import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.as.weld.discovery.vetoed.Bravo;
import org.jboss.weld.resources.spi.ClassFileInfo;
import org.junit.Assert;
import org.junit.Test;


public class WeldClassFileServicesTest {
    private static ClassFileInfo alpha;

    private static ClassFileInfo abstractAlpha;

    private static ClassFileInfo alphaImpl;

    private static ClassFileInfo innerInterface;

    private static ClassFileInfo bravo;

    private static ClassFileInfo charlie;

    @Test
    public void testModifiers() throws IOException {
        Assert.assertTrue(Modifier.isAbstract(WeldClassFileServicesTest.alpha.getModifiers()));
        Assert.assertTrue(Modifier.isAbstract(WeldClassFileServicesTest.abstractAlpha.getModifiers()));
        Assert.assertFalse(Modifier.isAbstract(WeldClassFileServicesTest.alphaImpl.getModifiers()));
        Assert.assertFalse(Modifier.isStatic(WeldClassFileServicesTest.alpha.getModifiers()));
        Assert.assertFalse(Modifier.isStatic(WeldClassFileServicesTest.abstractAlpha.getModifiers()));
        Assert.assertFalse(Modifier.isStatic(WeldClassFileServicesTest.alphaImpl.getModifiers()));
    }

    @Test
    public void testVeto() throws IOException {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isVetoed());
        Assert.assertFalse(WeldClassFileServicesTest.abstractAlpha.isVetoed());
        Assert.assertFalse(WeldClassFileServicesTest.alphaImpl.isVetoed());
        Assert.assertTrue(WeldClassFileServicesTest.bravo.isVetoed());
    }

    @Test
    public void testSuperclassName() {
        Assert.assertEquals(Object.class.getName(), WeldClassFileServicesTest.alpha.getSuperclassName());
        Assert.assertEquals(Object.class.getName(), WeldClassFileServicesTest.abstractAlpha.getSuperclassName());
        Assert.assertEquals(AbstractAlpha.class.getName(), WeldClassFileServicesTest.alphaImpl.getSuperclassName());
    }

    @Test
    public void testTopLevelClass() {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isTopLevelClass());
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isTopLevelClass());
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isTopLevelClass());
        Assert.assertFalse(WeldClassFileServicesTest.innerInterface.isTopLevelClass());
    }

    @Test
    public void testIsAssignableFrom() {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isAssignableFrom(AlphaImpl.class));
        Assert.assertTrue(WeldClassFileServicesTest.abstractAlpha.isAssignableFrom(AlphaImpl.class));
        Assert.assertFalse(WeldClassFileServicesTest.abstractAlpha.isAssignableFrom(Alpha.class));
        Assert.assertTrue(WeldClassFileServicesTest.innerInterface.isAssignableFrom(Bravo.class));
        Assert.assertTrue(WeldClassFileServicesTest.alphaImpl.isAssignableFrom(Bravo.class));
    }

    @Test
    public void testIsAssignableTo() {
        Assert.assertTrue(WeldClassFileServicesTest.alphaImpl.isAssignableTo(Alpha.class));
        Assert.assertTrue(WeldClassFileServicesTest.abstractAlpha.isAssignableTo(Alpha.class));
        Assert.assertFalse(WeldClassFileServicesTest.abstractAlpha.isAssignableTo(AlphaImpl.class));
        Assert.assertTrue(WeldClassFileServicesTest.bravo.isAssignableTo(InnerClasses.InnerInterface.class));
        Assert.assertTrue(WeldClassFileServicesTest.bravo.isAssignableTo(AbstractAlpha.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.isAssignableTo(InnerClasses.class));
    }

    @Test
    public void testIsAssignableToObject() {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isAssignableTo(Object.class));
        Assert.assertTrue(WeldClassFileServicesTest.abstractAlpha.isAssignableTo(Object.class));
        Assert.assertTrue(WeldClassFileServicesTest.alphaImpl.isAssignableTo(Object.class));
        Assert.assertTrue(WeldClassFileServicesTest.bravo.isAssignableTo(Object.class));
    }

    @Test
    public void testIsAssignableFromObject() {
        Assert.assertFalse(WeldClassFileServicesTest.alpha.isAssignableFrom(Object.class));
        Assert.assertFalse(WeldClassFileServicesTest.abstractAlpha.isAssignableFrom(Object.class));
        Assert.assertFalse(WeldClassFileServicesTest.alphaImpl.isAssignableFrom(Object.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.isAssignableFrom(Object.class));
    }

    @Test
    public void testIsAnnotationDeclared() {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.isAnnotationDeclared(Vetoed.class));
        Assert.assertTrue(WeldClassFileServicesTest.innerInterface.isAnnotationDeclared(Named.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.isAnnotationDeclared(Vetoed.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.isAnnotationDeclared(Named.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.isAnnotationDeclared(Inject.class));
    }

    @Test
    public void testContainsAnnotation() {
        Assert.assertTrue(WeldClassFileServicesTest.alpha.containsAnnotation(Vetoed.class));
        Assert.assertTrue(WeldClassFileServicesTest.innerInterface.containsAnnotation(Named.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.containsAnnotation(Vetoed.class));
        Assert.assertFalse(WeldClassFileServicesTest.bravo.containsAnnotation(Named.class));
        Assert.assertTrue(WeldClassFileServicesTest.bravo.containsAnnotation(Inject.class));
    }

    @Test
    public void testContainsAnnotationReflectionFallback() {
        Assert.assertTrue(WeldClassFileServicesTest.charlie.containsAnnotation(Target.class));
        Assert.assertTrue(WeldClassFileServicesTest.bravo.containsAnnotation(Target.class));
    }
}


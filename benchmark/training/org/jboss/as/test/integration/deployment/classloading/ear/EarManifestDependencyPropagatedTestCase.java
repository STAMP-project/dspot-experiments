/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
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
package org.jboss.as.test.integration.deployment.classloading.ear;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * tests that dependencies added to items in the ear are propagated to sub deployments.
 */
@RunWith(Arquillian.class)
public class EarManifestDependencyPropagatedTestCase {
    public static final String CLASS_FILE_WRITER_CLASS = "org.jboss.classfilewriter.ClassFile";

    public static final String JANDEX_CLASS = "org.jboss.jandex.Index";

    @Test
    public void testClassFileWriterAccessible() throws ClassNotFoundException {
        EarManifestDependencyPropagatedTestCase.loadClass(EarManifestDependencyPropagatedTestCase.CLASS_FILE_WRITER_CLASS);
        EarLibClassLoadingClass.loadClass(EarManifestDependencyPropagatedTestCase.CLASS_FILE_WRITER_CLASS);
    }

    @Test
    public void testJandexAccessibility() throws ClassNotFoundException {
        EarManifestDependencyPropagatedTestCase.loadClass(EarManifestDependencyPropagatedTestCase.JANDEX_CLASS);
        try {
            EarLibClassLoadingClass.loadClass(EarManifestDependencyPropagatedTestCase.JANDEX_CLASS);
            Assert.fail("Expected class loading to fail");
        } catch (ClassNotFoundException e) {
        }
    }
}


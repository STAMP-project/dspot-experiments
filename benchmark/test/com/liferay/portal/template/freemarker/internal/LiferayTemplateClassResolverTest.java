/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.template.freemarker.internal;


import freemarker.core.TemplateClassResolver;
import freemarker.template.TemplateException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomas Polesovsky
 * @author Manuel de la Pe?a
 */
public class LiferayTemplateClassResolverTest {
    @Test
    public void testResolveAllowedClassByClassName() throws Exception {
        _updateProperties("freemarker.template.utility.ClassUtil", "");
        _liferayTemplateClassResolver.resolve("freemarker.template.utility.ClassUtil", null, null);
    }

    @Test
    public void testResolveAllowedClassByStar() throws Exception {
        _updateProperties("freemarker.template.utility.*", "");
        _liferayTemplateClassResolver.resolve("freemarker.template.utility.ClassUtil", null, null);
    }

    @Test
    public void testResolveAllowedExecuteClass() throws Exception {
        _updateProperties("freemarker.template.utility.*", "");
        _testResolveNotAllowedClass("freemarker.template.utility.Execute");
    }

    @Test
    public void testResolveAllowedInvalidClass() throws Exception {
        _updateProperties("invalidClass", "");
        try {
            _liferayTemplateClassResolver.resolve("invalidClass", null, null);
            Assert.fail();
        } catch (TemplateException te) {
            ClassNotFoundException cnfe = ((ClassNotFoundException) (te.getCause()));
            Assert.assertEquals("Unable to load class invalidClass", cnfe.getMessage());
        }
    }

    @Test
    public void testResolveAllowedObjectConstructorClass() throws Exception {
        _updateProperties("freemarker.template.utility.*", "");
        _testResolveNotAllowedClass("freemarker.template.utility.ObjectConstructor");
    }

    @Test
    public void testResolveAllowedPortalClass() throws Exception {
        _updateProperties("com.liferay.portal.kernel.model.User", null);
        _liferayTemplateClassResolver.resolve("com.liferay.portal.kernel.model.User", null, null);
    }

    @Test
    public void testResolveAllowedPortalClassExplicitlyRestricted() throws Exception {
        _updateProperties("com.liferay.portal.kernel.model.User", "com.liferay.portal.kernel.model.*");
        _testResolveNotAllowedClass("com.liferay.portal.kernel.model.User");
    }

    @Test
    public void testResolveClassClass() {
        _testResolveNotAllowedClass("java.lang.Class");
    }

    @Test
    public void testResolveClassLoaderClass() {
        _testResolveNotAllowedClass("java.lang.ClassLoader");
    }

    @Test
    public void testResolveExecuteClass() {
        _testResolveNotAllowedClass("freemarker.template.utility.Execute");
    }

    @Test
    public void testResolveNotAllowedPortalClass() {
        _testResolveNotAllowedClass("com.liferay.portal.kernel.model.User");
    }

    @Test
    public void testResolveObjectConstructorClass() {
        _testResolveNotAllowedClass("freemarker.template.utility.ObjectConstructor");
    }

    @Test
    public void testResolveThreadClass() {
        _testResolveNotAllowedClass("java.lang.Thread");
    }

    private TemplateClassResolver _liferayTemplateClassResolver;
}


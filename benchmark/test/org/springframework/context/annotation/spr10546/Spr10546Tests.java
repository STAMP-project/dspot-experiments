/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.context.annotation.spr10546;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.spr10546.scanpackage.AEnclosingConfig;


/**
 *
 *
 * @author Rob Winch
 */
public class Spr10546Tests {
    private ConfigurableApplicationContext context;

    // These fail prior to fixing SPR-10546
    @Test
    public void enclosingConfigFirstParentDefinesBean() {
        assertLoadsMyBean(AEnclosingConfig.class, AEnclosingConfig.ChildConfig.class);
    }

    /**
     * Prior to fixing SPR-10546 this might have succeeded depending on the ordering the
     * classes were picked up. If they are picked up in the same order as
     * {@link #enclosingConfigFirstParentDefinesBean()} then it would fail. This test is
     * mostly for illustration purposes, but doesn't hurt to continue using it.
     *
     * <p>We purposely use the {@link AEnclosingConfig} to make it alphabetically prior to the
     * {@link AEnclosingConfig.ChildConfig} which encourages this to occur with the
     * classpath scanning implementation being used by the author of this test.
     */
    @Test
    public void enclosingConfigFirstParentDefinesBeanWithScanning() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        context = ctx;
        ctx.scan(AEnclosingConfig.class.getPackage().getName());
        ctx.refresh();
        Assert.assertThat(context.getBean("myBean", String.class), CoreMatchers.equalTo("myBean"));
    }

    @Test
    public void enclosingConfigFirstParentDefinesBeanWithImportResource() {
        assertLoadsMyBean(Spr10546Tests.AEnclosingWithImportResourceConfig.class, Spr10546Tests.AEnclosingWithImportResourceConfig.ChildConfig.class);
    }

    @Configuration
    static class AEnclosingWithImportResourceConfig {
        @Configuration
        public static class ChildConfig extends ParentWithImportResourceConfig {}
    }

    @Test
    public void enclosingConfigFirstParentDefinesBeanWithComponentScan() {
        assertLoadsMyBean(Spr10546Tests.AEnclosingWithComponentScanConfig.class, Spr10546Tests.AEnclosingWithComponentScanConfig.ChildConfig.class);
    }

    @Configuration
    static class AEnclosingWithComponentScanConfig {
        @Configuration
        public static class ChildConfig extends ParentWithComponentScanConfig {}
    }

    @Test
    public void enclosingConfigFirstParentWithParentDefinesBean() {
        assertLoadsMyBean(Spr10546Tests.AEnclosingWithGrandparentConfig.class, Spr10546Tests.AEnclosingWithGrandparentConfig.ChildConfig.class);
    }

    @Configuration
    static class AEnclosingWithGrandparentConfig {
        @Configuration
        public static class ChildConfig extends ParentWithParentConfig {}
    }

    @Test
    public void importChildConfigThenChildConfig() {
        assertLoadsMyBean(Spr10546Tests.ImportChildConfig.class, Spr10546Tests.ChildConfig.class);
    }

    @Configuration
    static class ChildConfig extends ParentConfig {}

    @Configuration
    @Import(Spr10546Tests.ChildConfig.class)
    static class ImportChildConfig {}

    // These worked prior, but validating they continue to work
    @Test
    public void enclosingConfigFirstParentDefinesBeanWithImport() {
        assertLoadsMyBean(Spr10546Tests.AEnclosingWithImportConfig.class, Spr10546Tests.AEnclosingWithImportConfig.ChildConfig.class);
    }

    @Configuration
    static class AEnclosingWithImportConfig {
        @Configuration
        public static class ChildConfig extends ParentWithImportConfig {}
    }

    @Test
    public void childConfigFirst() {
        assertLoadsMyBean(AEnclosingConfig.ChildConfig.class, AEnclosingConfig.class);
    }

    @Test
    public void enclosingConfigOnly() {
        assertLoadsMyBean(AEnclosingConfig.class);
    }

    @Test
    public void childConfigOnly() {
        assertLoadsMyBean(AEnclosingConfig.ChildConfig.class);
    }
}


/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.java9.module;


import JarPackageAnalyzer.JarEntryFilter;
import com.navercorp.pinpoint.common.util.CodeSourceUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class JarPackageAnalyzerTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Set<String> SLF4J_API_PACKAGE = Set.of(Set, "org.slf4j", "org.slf4j.event", "org.slf4j.helpers", "org.slf4j.spi");

    @Test
    public void packageAnalyzer() throws IOException {
        URL url = CodeSourceUtils.getCodeLocation(Logger.class);
        JarFile jarFile = new JarFile(url.getFile());
        logger.debug("jarFile:{}", jarFile.getName());
        PackageAnalyzer packageAnalyzer = new JarPackageAnalyzer(jarFile);
        Set<String> packageSet = packageAnalyzer.getPackage();
        logger.debug("package:{}", packageSet);
        Assert.assertEquals(packageSet, SLF4J_API_PACKAGE);
    }

    @Test
    public void jarFileToURI() throws IOException {
        URL url = CodeSourceUtils.getCodeLocation(Logger.class);
        logger.debug("url:{}", url);
        JarFile jarFile = new JarFile(url.getFile());
        logger.debug("jarFile:{}", jarFile.getName());
        File file = new File(jarFile.getName());
        logger.debug("url1:{}", file.toURI());
    }

    @Test
    public void filter_emptyPackage() {
        JarPackageAnalyzer.JarEntryFilter filter = new JarPackageAnalyzer.PackageFilter();
        JarEntry jarEntry = Mockito.mock(JarEntry.class);
        Mockito.when(jarEntry.getName()).thenReturn("test.class");
        String empty = filter.filter(jarEntry);
        Assert.assertNull(empty);
    }
}


/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.domain;


import java.util.Set;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import org.junit.Test;
import org.springframework.boot.autoconfigure.domain.scan.a.EmbeddableA;
import org.springframework.boot.autoconfigure.domain.scan.a.EntityA;
import org.springframework.boot.autoconfigure.domain.scan.b.EmbeddableB;
import org.springframework.boot.autoconfigure.domain.scan.b.EntityB;
import org.springframework.boot.autoconfigure.domain.scan.c.EmbeddableC;
import org.springframework.boot.autoconfigure.domain.scan.c.EntityC;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link EntityScanner}.
 *
 * @author Phillip Webb
 */
public class EntityScannerTests {
    @Test
    public void createWhenContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new EntityScanner(null)).withMessageContaining("Context must not be null");
    }

    @Test
    public void scanShouldScanFromSinglePackage() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EntityScannerTests.ScanConfig.class);
        EntityScanner scanner = new EntityScanner(context);
        Set<Class<?>> scanned = scanner.scan(Entity.class);
        assertThat(scanned).containsOnly(EntityA.class, EntityB.class, EntityC.class);
        context.close();
    }

    @Test
    public void scanShouldScanFromMultiplePackages() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EntityScannerTests.ScanAConfig.class, EntityScannerTests.ScanBConfig.class);
        EntityScanner scanner = new EntityScanner(context);
        Set<Class<?>> scanned = scanner.scan(Entity.class);
        assertThat(scanned).containsOnly(EntityA.class, EntityB.class);
        context.close();
    }

    @Test
    public void scanShouldFilterOnAnnotation() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(EntityScannerTests.ScanConfig.class);
        EntityScanner scanner = new EntityScanner(context);
        assertThat(scanner.scan(Entity.class)).containsOnly(EntityA.class, EntityB.class, EntityC.class);
        assertThat(scanner.scan(Embeddable.class)).containsOnly(EmbeddableA.class, EmbeddableB.class, EmbeddableC.class);
        assertThat(scanner.scan(Entity.class, Embeddable.class)).containsOnly(EntityA.class, EntityB.class, EntityC.class, EmbeddableA.class, EmbeddableB.class, EmbeddableC.class);
        context.close();
    }

    @Configuration
    @EntityScan("org.springframework.boot.autoconfigure.domain.scan")
    static class ScanConfig {}

    @Configuration
    @EntityScan(basePackageClasses = EntityA.class)
    static class ScanAConfig {}

    @Configuration
    @EntityScan(basePackageClasses = EntityB.class)
    static class ScanBConfig {}
}


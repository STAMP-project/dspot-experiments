/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.cli.compiler;


import groovy.lang.Grab;
import java.util.List;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.SourceUnit;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.cli.compiler.dependencies.ArtifactCoordinatesResolver;


/**
 * Tests for {@link DependencyCustomizer}
 *
 * @author Andy Wilkinson
 */
public class DependencyCustomizerTests {
    private final ModuleNode moduleNode = new ModuleNode(((SourceUnit) (null)));

    private final ClassNode classNode = new ClassNode(DependencyCustomizerTests.class);

    @Mock
    private ArtifactCoordinatesResolver resolver;

    private DependencyCustomizer dependencyCustomizer;

    @Test
    public void basicAdd() {
        this.dependencyCustomizer.add("spring-boot-starter-logging");
        List<AnnotationNode> grabAnnotations = this.classNode.getAnnotations(new ClassNode(Grab.class));
        assertThat(grabAnnotations).hasSize(1);
        AnnotationNode annotationNode = grabAnnotations.get(0);
        assertGrabAnnotation(annotationNode, "org.springframework.boot", "spring-boot-starter-logging", "1.2.3", null, null, true);
    }

    @Test
    public void nonTransitiveAdd() {
        this.dependencyCustomizer.add("spring-boot-starter-logging", false);
        List<AnnotationNode> grabAnnotations = this.classNode.getAnnotations(new ClassNode(Grab.class));
        assertThat(grabAnnotations).hasSize(1);
        AnnotationNode annotationNode = grabAnnotations.get(0);
        assertGrabAnnotation(annotationNode, "org.springframework.boot", "spring-boot-starter-logging", "1.2.3", null, null, false);
    }

    @Test
    public void fullyCustomized() {
        this.dependencyCustomizer.add("spring-boot-starter-logging", "my-classifier", "my-type", false);
        List<AnnotationNode> grabAnnotations = this.classNode.getAnnotations(new ClassNode(Grab.class));
        assertThat(grabAnnotations).hasSize(1);
        AnnotationNode annotationNode = grabAnnotations.get(0);
        assertGrabAnnotation(annotationNode, "org.springframework.boot", "spring-boot-starter-logging", "1.2.3", "my-classifier", "my-type", false);
    }

    @Test
    public void anyMissingClassesWithMissingClassesPerformsAdd() {
        this.dependencyCustomizer.ifAnyMissingClasses("does.not.Exist").add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).hasSize(1);
    }

    @Test
    public void anyMissingClassesWithMixtureOfClassesPerformsAdd() {
        this.dependencyCustomizer.ifAnyMissingClasses(getClass().getName(), "does.not.Exist").add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).hasSize(1);
    }

    @Test
    public void anyMissingClassesWithNoMissingClassesDoesNotPerformAdd() {
        this.dependencyCustomizer.ifAnyMissingClasses(getClass().getName()).add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).isEmpty();
    }

    @Test
    public void allMissingClassesWithNoMissingClassesDoesNotPerformAdd() {
        this.dependencyCustomizer.ifAllMissingClasses(getClass().getName()).add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).isEmpty();
    }

    @Test
    public void allMissingClassesWithMixtureOfClassesDoesNotPerformAdd() {
        this.dependencyCustomizer.ifAllMissingClasses(getClass().getName(), "does.not.Exist").add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).isEmpty();
    }

    @Test
    public void allMissingClassesWithAllClassesMissingPerformsAdd() {
        this.dependencyCustomizer.ifAllMissingClasses("does.not.Exist", "does.not.exist.Either").add("spring-boot-starter-logging");
        assertThat(this.classNode.getAnnotations(new ClassNode(Grab.class))).hasSize(1);
    }
}


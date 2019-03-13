/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.web.method;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 *
 * @author Brian Clozel
 */
public class ControllerAdviceBeanTests {
    @Test
    public void shouldMatchAll() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.SimpleControllerAdvice());
        assertApplicable("should match all", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertApplicable("should match all", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("should match all", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertApplicable("should match all", bean, String.class);
    }

    @Test
    public void basePackageSupport() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.BasePackageSupport());
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertNotApplicable("bean not in package", bean, String.class);
    }

    @Test
    public void basePackageValueSupport() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.BasePackageValueSupport());
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("base package support", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertNotApplicable("bean not in package", bean, String.class);
    }

    @Test
    public void annotationSupport() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.AnnotationSupport());
        assertApplicable("annotation support", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertNotApplicable("this bean is not annotated", bean, ControllerAdviceBeanTests.InheritanceController.class);
    }

    @Test
    public void markerClassSupport() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.MarkerClassSupport());
        assertApplicable("base package class support", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertApplicable("base package class support", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("base package class support", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertNotApplicable("bean not in package", bean, String.class);
    }

    @Test
    public void shouldNotMatch() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.ShouldNotMatch());
        assertNotApplicable("should not match", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertNotApplicable("should not match", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertNotApplicable("should not match", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertNotApplicable("should not match", bean, String.class);
    }

    @Test
    public void assignableTypesSupport() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.AssignableTypesSupport());
        assertApplicable("controller implements assignable", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("controller inherits assignable", bean, ControllerAdviceBeanTests.InheritanceController.class);
        assertNotApplicable("not assignable", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertNotApplicable("not assignable", bean, String.class);
    }

    @Test
    public void multipleMatch() {
        ControllerAdviceBean bean = new ControllerAdviceBean(new ControllerAdviceBeanTests.MultipleSelectorsSupport());
        assertApplicable("controller implements assignable", bean, ControllerAdviceBeanTests.ImplementationController.class);
        assertApplicable("controller is annotated", bean, ControllerAdviceBeanTests.AnnotatedController.class);
        assertNotApplicable("should not match", bean, ControllerAdviceBeanTests.InheritanceController.class);
    }

    // ControllerAdvice classes
    @ControllerAdvice
    static class SimpleControllerAdvice {}

    @ControllerAdvice(annotations = ControllerAdviceBeanTests.ControllerAnnotation.class)
    static class AnnotationSupport {}

    @ControllerAdvice(basePackageClasses = ControllerAdviceBeanTests.MarkerClass.class)
    static class MarkerClassSupport {}

    @ControllerAdvice(assignableTypes = { ControllerAdviceBeanTests.ControllerInterface.class, ControllerAdviceBeanTests.AbstractController.class })
    static class AssignableTypesSupport {}

    @ControllerAdvice(basePackages = "org.springframework.web.method")
    static class BasePackageSupport {}

    @ControllerAdvice("org.springframework.web.method")
    static class BasePackageValueSupport {}

    @ControllerAdvice(annotations = ControllerAdviceBeanTests.ControllerAnnotation.class, assignableTypes = ControllerAdviceBeanTests.ControllerInterface.class)
    static class MultipleSelectorsSupport {}

    @ControllerAdvice(basePackages = "java.util", annotations = { RestController.class })
    static class ShouldNotMatch {}

    // Support classes
    static class MarkerClass {}

    @Retention(RetentionPolicy.RUNTIME)
    static @interface ControllerAnnotation {}

    @ControllerAdviceBeanTests.ControllerAnnotation
    public static class AnnotatedController {}

    static interface ControllerInterface {}

    static class ImplementationController implements ControllerAdviceBeanTests.ControllerInterface {}

    abstract static class AbstractController {}

    static class InheritanceController extends ControllerAdviceBeanTests.AbstractController {}
}


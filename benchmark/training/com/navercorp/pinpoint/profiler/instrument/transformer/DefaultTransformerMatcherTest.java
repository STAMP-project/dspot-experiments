/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.instrument.transformer;


import com.navercorp.pinpoint.bootstrap.config.InstrumentMatcherCacheConfig;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.AnnotationInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.ClassInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.InterfaceInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.MatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.PackageInternalNameMatcherOperand;
import com.navercorp.pinpoint.bootstrap.instrument.matcher.operand.SuperClassInternalNameMatcherOperand;
import com.navercorp.pinpoint.profiler.instrument.classreading.InternalClassMetadata;
import java.io.InputStream;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class DefaultTransformerMatcherTest {
    @Test
    public void matchClass() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InstrumentMatcherCacheConfig config = new InstrumentMatcherCacheConfig();
        TransformerMatcher matcher = new DefaultTransformerMatcher(config);
        InternalClassMetadata stringClassMetadata = readClassMetadata(classLoader, String.class.getName());
        InternalClassMetadata threadClassMetadata = readClassMetadata(classLoader, Thread.class.getName());
        InternalClassMetadata inputStreamClassMetadata = readClassMetadata(classLoader, InputStream.class.getName());
        MatcherOperand operand = null;
        // single operand.
        operand = new ClassInternalNameMatcherOperand("java/lang/String");
        boolean result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertTrue(result);
        // not matched.
        operand = new ClassInternalNameMatcherOperand("java/io/InputStream");
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertFalse(result);
        // and operator
        // package AND interface
        operand = new PackageInternalNameMatcherOperand("java/lang").and(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false));
        // java.lang.Thread
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertTrue(result);
        // java.lang.String
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertFalse(result);
        // or operator
        // package OR interface
        operand = new PackageInternalNameMatcherOperand("java/lang").or(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false));
        // java.lang.Thread
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertTrue(result);
        // java.lang.String
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertTrue(result);
        // not operator
        // NOT interface.
        operand = new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false);
        operand = operand.not();
        // java.lang.Thread
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertFalse(result);
        // java.lang.String
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertTrue(result);
        // complex operator
        // (class or interface) AND (class or interface) ==> class, interface
        operand = new ClassInternalNameMatcherOperand("java/lang/String").or(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false));
        operand = operand.and(new ClassInternalNameMatcherOperand("java/lang/Thread").or(new InterfaceInternalNameMatcherOperand("java/lang/Comparable", false)));
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertTrue(result);
        // (class AND interface) OR (class AND interface) ==> class, class
        operand = new ClassInternalNameMatcherOperand("java/lang/String").and(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false));
        operand = operand.or(new ClassInternalNameMatcherOperand("java/lang/Thread").and(new InterfaceInternalNameMatcherOperand("java/lang/Comparable", false)));
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertFalse(result);
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertFalse(result);
        // package AND (interface OR annotation) ==> package
        operand = new PackageInternalNameMatcherOperand("java/lang");
        operand = operand.and(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false).or(new AnnotationInternalNameMatcherOperand("java/lang/Override", false)));
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertFalse(result);
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertTrue(result);
        // class OR (interface AND NOT annotation)
        operand = new ClassInternalNameMatcherOperand("java/lang/String");
        operand = operand.or(new InterfaceInternalNameMatcherOperand("java/lang/Runnable", false).and(new AnnotationInternalNameMatcherOperand("java/lang/Override", false).not()));
        result = matcher.match(classLoader, operand, stringClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, operand, threadClassMetadata);
        Assert.assertTrue(result);
    }

    @Test
    public void considerHierarchy() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InstrumentMatcherCacheConfig config = new InstrumentMatcherCacheConfig();
        TransformerMatcher matcher = new DefaultTransformerMatcher(config);
        boolean result = false;
        InternalClassMetadata stringClassMetadata = readClassMetadata(classLoader, String.class.getName());
        InternalClassMetadata threadClassMetadata = readClassMetadata(classLoader, Thread.class.getName());
        InternalClassMetadata extendsThreadClassMetadata = readClassMetadata(classLoader, DefaultTransformerMatcherTest.ExtendsThread.class.getName());
        InternalClassMetadata extendsExtendsThreadClassMetadata = readClassMetadata(classLoader, DefaultTransformerMatcherTest.ExtendsExtendsThread.class.getName());
        InternalClassMetadata hasMetaAnnotationClassMetadata = readClassMetadata(classLoader, DefaultTransformerMatcherTest.HasMetaAnnotation.class.getName());
        InternalClassMetadata hasMetaMetaAnnotationClassMetadata = readClassMetadata(classLoader, DefaultTransformerMatcherTest.HasMetaMetaAnnotation.class.getName());
        // interface
        InterfaceInternalNameMatcherOperand interfaceMatcherOperand = new InterfaceInternalNameMatcherOperand("java/lang/Runnable", true);
        result = matcher.match(classLoader, interfaceMatcherOperand, extendsThreadClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, interfaceMatcherOperand, threadClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, interfaceMatcherOperand, stringClassMetadata);
        Assert.assertFalse(result);
        result = matcher.match(classLoader, interfaceMatcherOperand, extendsExtendsThreadClassMetadata);
        Assert.assertTrue(result);
        // super
        SuperClassInternalNameMatcherOperand superMatcherOperand = new SuperClassInternalNameMatcherOperand("java/lang/Thread", true);
        result = matcher.match(classLoader, superMatcherOperand, extendsExtendsThreadClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, superMatcherOperand, extendsThreadClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, superMatcherOperand, threadClassMetadata);
        Assert.assertFalse(result);
        result = matcher.match(classLoader, superMatcherOperand, stringClassMetadata);
        Assert.assertFalse(result);
        // annotation
        AnnotationInternalNameMatcherOperand annotationMatcherOperand = new AnnotationInternalNameMatcherOperand("javax/annotation/Resource", true);
        result = matcher.match(classLoader, annotationMatcherOperand, hasMetaAnnotationClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, annotationMatcherOperand, hasMetaMetaAnnotationClassMetadata);
        Assert.assertTrue(result);
        result = matcher.match(classLoader, annotationMatcherOperand, stringClassMetadata);
        Assert.assertFalse(result);
        result = matcher.match(classLoader, annotationMatcherOperand, threadClassMetadata);
        Assert.assertFalse(result);
    }

    class ExtendsThread extends Thread {}

    class ExtendsExtendsThread extends DefaultTransformerMatcherTest.ExtendsThread {}

    @Resource
    @interface ResourceMetaAnnotation {}

    @DefaultTransformerMatcherTest.ResourceMetaAnnotation
    @interface ResourceMetaMetaAnnotation {}

    @DefaultTransformerMatcherTest.ResourceMetaAnnotation
    class HasMetaAnnotation {}

    @DefaultTransformerMatcherTest.ResourceMetaMetaAnnotation
    class HasMetaMetaAnnotation {}
}


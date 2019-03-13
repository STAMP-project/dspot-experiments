/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * [java-8]
 */
package org.joor.test;


import java.util.Collections;
import java.util.Set;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.joor.CompileOptions;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukas Eder
 */
public class CompileOptionsTest {
    @Test
    public void testCompileWithAnnotationProcessors() throws Exception {
        CompileOptionsTest.AProcessor p = new CompileOptionsTest.AProcessor();
        try {
            Reflect.compile("org.joor.test.FailAnnotationProcessing", ("package org.joor.test; " + (("@A " + "public class FailAnnotationProcessing {") + "}")), new CompileOptions().processors(p)).create().get();
            Assert.fail();
        } catch (ReflectException expected) {
            Assert.assertFalse(p.processed);
        }
        Reflect.compile("org.joor.test.SucceedAnnotationProcessing", ("package org.joor.test; " + (("@A @B " + "public class SucceedAnnotationProcessing {") + "}")), new CompileOptions().processors(p)).create().get();
        Assert.assertTrue(p.processed);
    }

    static class AProcessor implements Processor {
        boolean processed;

        @Override
        public Set<String> getSupportedOptions() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return Collections.singleton("*");
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.RELEASE_8;
        }

        @Override
        public void init(ProcessingEnvironment processingEnv) {
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            for (TypeElement e1 : annotations)
                if (e1.getQualifiedName().contentEquals(A.class.getName()))
                    for (Element e2 : roundEnv.getElementsAnnotatedWith(e1))
                        if ((e2.getAnnotation(B.class)) == null)
                            throw new RuntimeException("Annotation A must be accompanied by annotation B");




            this.processed = true;
            return false;
        }

        @Override
        public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
            return Collections.emptyList();
        }
    }
}


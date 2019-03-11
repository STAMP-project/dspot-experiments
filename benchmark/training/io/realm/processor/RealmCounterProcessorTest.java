/**
 * Copyright 2014-2017 Realm Inc.
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
package io.realm.processor;


import java.io.IOException;
import java.util.Arrays;
import javax.lang.model.element.Modifier;
import org.junit.Test;


public class RealmCounterProcessorTest {
    @Test
    public void compileMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void compileIgnoredMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().annotation("Ignore").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void compileIndexedMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().annotation("Index").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void compileRequiredMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().annotation("Required").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void compileStaticMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().modifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC).builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void failOnPKMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().annotation("PrimaryKey").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("cannot be used as primary key");
    }

    @Test
    public void failUnlessFinalMutableRealmInteger() throws IOException {
        RealmSyntheticTestClass javaFileObject = createCounterTestClass().modifiers(Modifier.PRIVATE).builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(javaFileObject)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("must be final");
    }
}


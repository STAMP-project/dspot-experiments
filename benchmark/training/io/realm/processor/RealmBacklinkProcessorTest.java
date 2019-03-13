/**
 * Copyright 2017 Realm Inc.
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


import com.google.testing.compile.JavaFileObjects;
import java.io.IOException;
import java.util.Arrays;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;
import org.junit.Test;


public class RealmBacklinkProcessorTest {
    private final JavaFileObject sourceClass = JavaFileObjects.forResource("some/test/BacklinkSource.java");

    private final JavaFileObject targetClass = JavaFileObjects.forResource("some/test/BacklinkTarget.java");

    private final JavaFileObject invalidResultsValueType = JavaFileObjects.forResource("some/test/InvalidResultsElementType.java");

    @Test
    public void compileBacklinks() {
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void compileSyntheticBacklinks() throws IOException {
        RealmSyntheticTestClass targetClass = createBacklinkTestClass().builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void failOnLinkingObjectsWithInvalidFieldType() throws IOException {
        RealmSyntheticTestClass targetClass = createBacklinkTestClass().type("BacklinkTarget").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("Fields annotated with @LinkingObjects must be RealmResults");
    }

    @Test
    public void failOnLinkingObjectsWithNonFinalField() throws IOException {
        RealmSyntheticTestClass targetClass = // A field with a @LinkingObjects annotation must be final
        createBacklinkTestClass().modifiers(Modifier.PUBLIC).builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("must be final");
    }

    @Test
    public void failsOnLinkingObjectsWithLinkedFields() throws IOException {
        RealmSyntheticTestClass targetClass = // Defining a backlink more than one levels back is not supported.
        // It can be queried though: `equalTo("selectedFieldParents.selectedFieldParents")
        createBacklinkTestClass().clearAnnotations().annotation("LinkingObjects(\"child.id\")").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("The use of '.' to specify fields in referenced classes is not supported");
    }

    @Test
    public void failsOnLinkingObjectsMissingFieldName() throws IOException {
        RealmSyntheticTestClass targetClass = // No backlinked field specified
        createBacklinkTestClass().clearAnnotations().annotation("LinkingObjects").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("must have a parameter identifying the link target");
    }

    @Test
    public void failsOnLinkingObjectsMissingGeneric() throws IOException {
        RealmSyntheticTestClass targetClass = // No backlink generic param specified
        createBacklinkTestClass().type("RealmResults").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("must specify a generic type");
    }

    @Test
    public void failsOnLinkingObjectsWithRequiredFields() throws IOException {
        RealmSyntheticTestClass targetClass = // A backlinked field may not be @Required
        createBacklinkTestClass().annotation("Required").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("The @LinkingObjects field ");
    }

    @Test
    public void failsOnLinkingObjectsWithIgnoreFields() throws IOException {
        RealmSyntheticTestClass targetClass = // An  @Ignored, backlinked field is completely ignored
        createBacklinkTestClass().annotation("Ignore").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    // TODO: This seems like a "gottcha".  We should warn.
    @Test
    public void ignoreStaticLinkingObjects() throws IOException {
        RealmSyntheticTestClass targetClass = createBacklinkTestClass().modifiers(Modifier.PUBLIC, Modifier.STATIC).type("RealmResults").clearAnnotations().annotation("LinkingObjects(\"xxx\")").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).compilesWithoutError();
    }

    @Test
    public void failsOnLinkingObjectsFieldNotFound() throws IOException {
        RealmSyntheticTestClass targetClass = // The argument to the @LinkingObjects annotation must name a field in the source class
        createBacklinkTestClass().clearAnnotations().annotation("LinkingObjects(\"xxx\")").builder().build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("does not exist in class");
    }

    @Test
    public void failsOnLinkingObjectsWithFieldWrongType() throws IOException {
        RealmSyntheticTestClass targetClass = // The type of the field named in the @LinkingObjects annotation must match
        // the generic type of the annotated field.  BacklinkSource.child is a Backlink,
        // not a Backlinks_WrongType.
        createBacklinkTestClass().builder().name("BacklinkTarget_WrongType").build();
        ASSERT.about(javaSources()).that(Arrays.asList(sourceClass, targetClass)).processedWith(new RealmProcessor()).failsToCompile().withErrorContaining("instead of");
    }

    @Test
    public void failToCompileInvalidResultsElementType() {
        ASSERT.about(javaSource()).that(invalidResultsValueType).processedWith(new RealmProcessor()).failsToCompile();
    }

    @Test
    public void compileBacklinkClassesWithSimpleNameConflicts() {
        ASSERT.about(javaSources()).that(Arrays.asList(JavaFileObjects.forResource("some/test/BacklinkSelfReference.java"), JavaFileObjects.forResource("some/test/conflict/BacklinkSelfReference.java"))).processedWith(new RealmProcessor()).compilesWithoutError();
    }
}


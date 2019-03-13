/**
 * Copyright 2013 Jake Wharton
 * Copyright 2014 Prateek Srivastava (@f2prateek)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dart.processor;


import com.google.common.base.Joiner;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ExtraBinderProcessor}. For tests related to Parceler and Parceler is available.
 */
public class BindExtraWithParcelerTest {
    @Test
    public void serializableCollection() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestSerializableCollectionNavigationModel", // 
        Joiner.on('\n').join("package test;", "import dart.BindExtra;", "import java.lang.Object;", "import java.lang.String;", "import android.util.SparseArray;", "public class TestSerializableCollectionNavigationModel {", "  @BindExtra(\"key\") SparseArray<String> extra;", "}"));
        String extraBinderQualifiedName = "test.TestSerializableCollectionNavigationModel__ExtraBinder";
        JavaFileObject expectedSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestSerializableCollectionNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestSerializableCollectionNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = org.parceler.Parcels.unwrap((android.os.Parcelable) object);", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(expectedSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestSerializableCollectionNavigationModel"));
    }

    @Test
    public void nonSerializableNonParcelableCollection_withoutParceler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel", // 
        Joiner.on('\n').join("package test;", "import dart.BindExtra;", "import java.lang.Object;", "import java.lang.String;", "import java.util.List;", "public class TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel {", "  @BindExtra(\"key\") List<String> extra;", "}"));
        String extraBinderQualifiedName = "test.TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel__ExtraBinder";
        JavaFileObject expectedSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = org.parceler.Parcels.unwrap((android.os.Parcelable) object);", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(expectedSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestNonSerializableNonParcelableCollection_withoutParcelerNavigationModel"));
    }

    @Test
    public void parcelAnnotatedType() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestParcelAnnotatedNavigationModel", // 
        Joiner.on('\n').join("package test;", "import dart.BindExtra;", "import java.lang.Object;", "import java.lang.String;", "import org.parceler.Parcel;", "public class TestParcelAnnotatedNavigationModel {", "  @BindExtra(\"key\") Foo extra;", "  @Parcel static class Foo {}", "}"));
        String extraBinderQualifiedName = "test.TestParcelAnnotatedNavigationModel__ExtraBinder";
        JavaFileObject expectedSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestParcelAnnotatedNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestParcelAnnotatedNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = org.parceler.Parcels.unwrap((android.os.Parcelable) object);", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(expectedSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestParcelAnnotatedNavigationModel"));
    }

    @Test
    public void collectionOfParcelAnnotatedType() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestCollectionParcelNavigationModel", // 
        Joiner.on('\n').join("package test;", "import dart.BindExtra;", "import java.lang.Object;", "import java.lang.String;", "import java.util.List;", "import org.parceler.Parcel;", "public class TestCollectionParcelNavigationModel {", "  @BindExtra(\"key\") List<Foo> extra;", "  @Parcel static class Foo {}", "}"));
        String extraBinderQualifiedName = "test.TestCollectionParcelNavigationModel__ExtraBinder";
        JavaFileObject expectedSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestCollectionParcelNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestCollectionParcelNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = org.parceler.Parcels.unwrap((android.os.Parcelable) object);", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(expectedSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestCollectionParcelNavigationModel"));
    }

    @Test
    public void bindingParcelThatExtendsParcelableExtra() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestParcelExtendsParcelableNavigationModel", // 
        Joiner.on('\n').join("package test;", "import android.os.Parcelable;", "import org.parceler.Parcel;", "import dart.BindExtra;", "class ExtraParent implements Parcelable {", "  public void writeToParcel(android.os.Parcel out, int flags) {", "  }", "  public int describeContents() {", "    return 0;", "  }", "}", "@Parcel class Extra extends ExtraParent {}", "public class TestParcelExtendsParcelableNavigationModel {", "    @BindExtra(\"key\") Extra extra;", "}"));
        String extraBinderQualifiedName = "test.TestParcelExtendsParcelableNavigationModel__ExtraBinder";
        JavaFileObject builderSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestParcelExtendsParcelableNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestParcelExtendsParcelableNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = org.parceler.Parcels.unwrap((android.os.Parcelable) object);", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(builderSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestParcelExtendsParcelableNavigationModel"));
    }

    @Test
    public void bindingParcelableThatExtendsParcelableExtra() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.TestParcelableExtendsParcelableNavigationModel", // 
        Joiner.on('\n').join("package test;", "import android.os.Parcelable;", "import dart.BindExtra;", "class ExtraParent implements Parcelable {", "  public void writeToParcel(android.os.Parcel out, int flags) {", "  }", "  public int describeContents() {", "    return 0;", "  }", "}", "class Extra extends ExtraParent implements Parcelable {", "  public void writeToParcel(android.os.Parcel out, int flags) {", "  }", "  public int describeContents() {", "    return 0;", "  }", "}", "public class TestParcelableExtendsParcelableNavigationModel {", "    @BindExtra(\"key\") Extra extra;", "}"));
        String extraBinderQualifiedName = "test.TestParcelableExtendsParcelableNavigationModel__ExtraBinder";
        JavaFileObject builderSource = JavaFileObjects.forSourceString(extraBinderQualifiedName, // 
        Joiner.on('\n').join("package test;", "import dart.Dart;", "import java.lang.Object;", "public class TestParcelableExtendsParcelableNavigationModel__ExtraBinder {", "  public static void bind(Dart.Finder finder, TestParcelableExtendsParcelableNavigationModel target, Object source) {", "    Object object;", "    object = finder.getExtra(source, \"key\");", "    if (object == null) {", "      throw new IllegalStateException(\"Required extra with key \'key\' for field \'extra\' was not found. If this extra is optional add \'@Nullable\' annotation.\");", "    }", "    target.extra = (Extra) object;", "  }", "}"));
        ExtraBinderProcessor processor = ProcessorTestUtilities.extraBinderProcessors();
        Compilation compilation = javac().withProcessors(processor).compile(source);
        assertThat(compilation).generatedSourceFile(extraBinderQualifiedName).hasSourceEquivalentTo(builderSource);
        TypeElement originatingElement = processor.getOriginatingElement(extraBinderQualifiedName);
        TypeElement mostEnclosingElement = ProcessorTestUtilities.getMostEnclosingElement(originatingElement);
        Assert.assertTrue(mostEnclosingElement.getQualifiedName().contentEquals("test.TestParcelableExtendsParcelableNavigationModel"));
    }
}


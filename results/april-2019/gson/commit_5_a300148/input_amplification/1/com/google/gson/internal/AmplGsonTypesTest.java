package com.google.gson.internal;


import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import junit.framework.TestCase;


public final class AmplGsonTypesTest extends TestCase {
    public void testGetFirstTypeArgument_add36() throws Exception {
        Type o_testGetFirstTypeArgument_add36__1 = AmplGsonTypesTest.getFirstTypeArgument(AmplGsonTypesTest.A.class);
        TestCase.assertNull(o_testGetFirstTypeArgument_add36__1);
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, AmplGsonTypesTest.A.class, AmplGsonTypesTest.B.class, AmplGsonTypesTest.C.class);
        Type o_testGetFirstTypeArgument_add36__4 = AmplGsonTypesTest.getFirstTypeArgument(type);
        TestCase.assertEquals("class com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).toString());
        TestCase.assertEquals("(file:/tmp/dspot-experiments/dataset/april-2019/gson_parent/gson/target/test-classes/ <no signer certificates>)", ((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).toString());
        TestCase.assertEquals(922580336, ((int) (((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).hashCode())));
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).getCertificates());
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        TestCase.assertTrue(((PermissionCollection) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getPermissions())).isReadOnly());
        TestCase.assertEquals(26, ((int) (((Class) (o_testGetFirstTypeArgument_add36__4)).getModifiers())));
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isInterface());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isArray());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isPrimitive());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isAnnotation());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isSynthetic());
        TestCase.assertNull(((ClassLoader) (((ClassLoader) (((Class) (o_testGetFirstTypeArgument_add36__4)).getClassLoader())).getParent())).getParent());
        TestCase.assertEquals("java.lang.Object", ((Type) (((Class) (o_testGetFirstTypeArgument_add36__4)).getGenericSuperclass())).getTypeName());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationVendor());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationVendor());
        TestCase.assertEquals("package com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).toString());
        TestCase.assertEquals(614253784, ((int) (((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).hashCode())));
        TestCase.assertEquals("com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getName());
        TestCase.assertFalse(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).isSealed());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getSigners());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnclosingMethod());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnclosingConstructor());
        TestCase.assertEquals("B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getSimpleName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getTypeName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest.B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getCanonicalName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isAnonymousClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isLocalClass());
        TestCase.assertTrue(((Class) (o_testGetFirstTypeArgument_add36__4)).isMemberClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isEnum());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnumConstants());
        TestCase.assertEquals("java.lang.Object", ((Type) (((AnnotatedType) (((Class) (o_testGetFirstTypeArgument_add36__4)).getAnnotatedSuperclass())).getType())).getTypeName());
        Type o_testGetFirstTypeArgument_add36__5 = AmplGsonTypesTest.getFirstTypeArgument(type);
        TestCase.assertEquals("class com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__5)).toString());
        TestCase.assertEquals("(file:/tmp/dspot-experiments/dataset/april-2019/gson_parent/gson/target/test-classes/ <no signer certificates>)", ((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__5)).getProtectionDomain())).getCodeSource())).toString());
        TestCase.assertEquals(922580336, ((int) (((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__5)).getProtectionDomain())).getCodeSource())).hashCode())));
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__5)).getProtectionDomain())).getCodeSource())).getCertificates());
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__5)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        TestCase.assertTrue(((PermissionCollection) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__5)).getProtectionDomain())).getPermissions())).isReadOnly());
        TestCase.assertEquals(26, ((int) (((Class) (o_testGetFirstTypeArgument_add36__5)).getModifiers())));
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isInterface());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isArray());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isPrimitive());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__5)).getName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isAnnotation());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isSynthetic());
        TestCase.assertNull(((ClassLoader) (((ClassLoader) (((Class) (o_testGetFirstTypeArgument_add36__5)).getClassLoader())).getParent())).getParent());
        TestCase.assertEquals("java.lang.Object", ((Type) (((Class) (o_testGetFirstTypeArgument_add36__5)).getGenericSuperclass())).getTypeName());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getSpecificationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getSpecificationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getSpecificationVendor());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getImplementationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getImplementationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getImplementationVendor());
        TestCase.assertEquals("package com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).toString());
        TestCase.assertEquals(614253784, ((int) (((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).hashCode())));
        TestCase.assertEquals("com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).getName());
        TestCase.assertFalse(((Package) (((Class) (o_testGetFirstTypeArgument_add36__5)).getPackage())).isSealed());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__5)).getSigners());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__5)).getEnclosingMethod());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__5)).getEnclosingConstructor());
        TestCase.assertEquals("B", ((Class) (o_testGetFirstTypeArgument_add36__5)).getSimpleName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__5)).getTypeName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest.B", ((Class) (o_testGetFirstTypeArgument_add36__5)).getCanonicalName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isAnonymousClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isLocalClass());
        TestCase.assertTrue(((Class) (o_testGetFirstTypeArgument_add36__5)).isMemberClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__5)).isEnum());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__5)).getEnumConstants());
        TestCase.assertEquals("java.lang.Object", ((Type) (((AnnotatedType) (((Class) (o_testGetFirstTypeArgument_add36__5)).getAnnotatedSuperclass())).getType())).getTypeName());
        TestCase.assertNull(o_testGetFirstTypeArgument_add36__1);
        TestCase.assertEquals("class com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).toString());
        TestCase.assertEquals("(file:/tmp/dspot-experiments/dataset/april-2019/gson_parent/gson/target/test-classes/ <no signer certificates>)", ((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).toString());
        TestCase.assertEquals(922580336, ((int) (((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).hashCode())));
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).getCertificates());
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        TestCase.assertTrue(((PermissionCollection) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add36__4)).getProtectionDomain())).getPermissions())).isReadOnly());
        TestCase.assertEquals(26, ((int) (((Class) (o_testGetFirstTypeArgument_add36__4)).getModifiers())));
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isInterface());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isArray());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isPrimitive());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isAnnotation());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isSynthetic());
        TestCase.assertNull(((ClassLoader) (((ClassLoader) (((Class) (o_testGetFirstTypeArgument_add36__4)).getClassLoader())).getParent())).getParent());
        TestCase.assertEquals("java.lang.Object", ((Type) (((Class) (o_testGetFirstTypeArgument_add36__4)).getGenericSuperclass())).getTypeName());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getSpecificationVendor());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getImplementationVendor());
        TestCase.assertEquals("package com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).toString());
        TestCase.assertEquals(614253784, ((int) (((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).hashCode())));
        TestCase.assertEquals("com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).getName());
        TestCase.assertFalse(((Package) (((Class) (o_testGetFirstTypeArgument_add36__4)).getPackage())).isSealed());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getSigners());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnclosingMethod());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnclosingConstructor());
        TestCase.assertEquals("B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getSimpleName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getTypeName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest.B", ((Class) (o_testGetFirstTypeArgument_add36__4)).getCanonicalName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isAnonymousClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isLocalClass());
        TestCase.assertTrue(((Class) (o_testGetFirstTypeArgument_add36__4)).isMemberClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add36__4)).isEnum());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add36__4)).getEnumConstants());
        TestCase.assertEquals("java.lang.Object", ((Type) (((AnnotatedType) (((Class) (o_testGetFirstTypeArgument_add36__4)).getAnnotatedSuperclass())).getType())).getTypeName());
    }

    public void testGetFirstTypeArgument() throws Exception {
        Type o_testGetFirstTypeArgument__1 = AmplGsonTypesTest.getFirstTypeArgument(AmplGsonTypesTest.A.class);
        TestCase.assertNull(o_testGetFirstTypeArgument__1);
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, AmplGsonTypesTest.A.class, AmplGsonTypesTest.B.class, AmplGsonTypesTest.C.class);
        Type o_testGetFirstTypeArgument__4 = AmplGsonTypesTest.getFirstTypeArgument(type);
        TestCase.assertEquals("class com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument__4)).toString());
        TestCase.assertEquals("(file:/tmp/dspot-experiments/dataset/april-2019/gson_parent/gson/target/test-classes/ <no signer certificates>)", ((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument__4)).getProtectionDomain())).getCodeSource())).toString());
        TestCase.assertEquals(922580336, ((int) (((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument__4)).getProtectionDomain())).getCodeSource())).hashCode())));
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument__4)).getProtectionDomain())).getCodeSource())).getCertificates());
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument__4)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        TestCase.assertTrue(((PermissionCollection) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument__4)).getProtectionDomain())).getPermissions())).isReadOnly());
        TestCase.assertEquals(26, ((int) (((Class) (o_testGetFirstTypeArgument__4)).getModifiers())));
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isInterface());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isArray());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isPrimitive());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument__4)).getName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isAnnotation());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isSynthetic());
        TestCase.assertNull(((ClassLoader) (((ClassLoader) (((Class) (o_testGetFirstTypeArgument__4)).getClassLoader())).getParent())).getParent());
        TestCase.assertEquals("java.lang.Object", ((Type) (((Class) (o_testGetFirstTypeArgument__4)).getGenericSuperclass())).getTypeName());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getSpecificationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getSpecificationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getSpecificationVendor());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getImplementationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getImplementationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getImplementationVendor());
        TestCase.assertEquals("package com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).toString());
        TestCase.assertEquals(614253784, ((int) (((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).hashCode())));
        TestCase.assertEquals("com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).getName());
        TestCase.assertFalse(((Package) (((Class) (o_testGetFirstTypeArgument__4)).getPackage())).isSealed());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument__4)).getSigners());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument__4)).getEnclosingMethod());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument__4)).getEnclosingConstructor());
        TestCase.assertEquals("B", ((Class) (o_testGetFirstTypeArgument__4)).getSimpleName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument__4)).getTypeName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest.B", ((Class) (o_testGetFirstTypeArgument__4)).getCanonicalName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isAnonymousClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isLocalClass());
        TestCase.assertTrue(((Class) (o_testGetFirstTypeArgument__4)).isMemberClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument__4)).isEnum());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument__4)).getEnumConstants());
        TestCase.assertEquals("java.lang.Object", ((Type) (((AnnotatedType) (((Class) (o_testGetFirstTypeArgument__4)).getAnnotatedSuperclass())).getType())).getTypeName());
        TestCase.assertNull(o_testGetFirstTypeArgument__1);
    }

    public void testGetFirstTypeArgument_add34() throws Exception {
        Type o_testGetFirstTypeArgument_add34__1 = AmplGsonTypesTest.getFirstTypeArgument(AmplGsonTypesTest.A.class);
        TestCase.assertNull(o_testGetFirstTypeArgument_add34__1);
        Type o_testGetFirstTypeArgument_add34__2 = AmplGsonTypesTest.getFirstTypeArgument(AmplGsonTypesTest.A.class);
        TestCase.assertNull(o_testGetFirstTypeArgument_add34__2);
        Type type = $Gson$Types.newParameterizedTypeWithOwner(null, AmplGsonTypesTest.A.class, AmplGsonTypesTest.B.class, AmplGsonTypesTest.C.class);
        Type o_testGetFirstTypeArgument_add34__5 = AmplGsonTypesTest.getFirstTypeArgument(type);
        TestCase.assertEquals("class com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add34__5)).toString());
        TestCase.assertEquals("(file:/tmp/dspot-experiments/dataset/april-2019/gson_parent/gson/target/test-classes/ <no signer certificates>)", ((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add34__5)).getProtectionDomain())).getCodeSource())).toString());
        TestCase.assertEquals(922580336, ((int) (((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add34__5)).getProtectionDomain())).getCodeSource())).hashCode())));
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add34__5)).getProtectionDomain())).getCodeSource())).getCertificates());
        TestCase.assertNull(((CodeSource) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add34__5)).getProtectionDomain())).getCodeSource())).getCodeSigners());
        TestCase.assertTrue(((PermissionCollection) (((ProtectionDomain) (((Class) (o_testGetFirstTypeArgument_add34__5)).getProtectionDomain())).getPermissions())).isReadOnly());
        TestCase.assertEquals(26, ((int) (((Class) (o_testGetFirstTypeArgument_add34__5)).getModifiers())));
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isInterface());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isArray());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isPrimitive());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add34__5)).getName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isAnnotation());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isSynthetic());
        TestCase.assertNull(((ClassLoader) (((ClassLoader) (((Class) (o_testGetFirstTypeArgument_add34__5)).getClassLoader())).getParent())).getParent());
        TestCase.assertEquals("java.lang.Object", ((Type) (((Class) (o_testGetFirstTypeArgument_add34__5)).getGenericSuperclass())).getTypeName());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getSpecificationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getSpecificationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getSpecificationVendor());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getImplementationTitle());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getImplementationVersion());
        TestCase.assertNull(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getImplementationVendor());
        TestCase.assertEquals("package com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).toString());
        TestCase.assertEquals(614253784, ((int) (((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).hashCode())));
        TestCase.assertEquals("com.google.gson.internal", ((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).getName());
        TestCase.assertFalse(((Package) (((Class) (o_testGetFirstTypeArgument_add34__5)).getPackage())).isSealed());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add34__5)).getSigners());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add34__5)).getEnclosingMethod());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add34__5)).getEnclosingConstructor());
        TestCase.assertEquals("B", ((Class) (o_testGetFirstTypeArgument_add34__5)).getSimpleName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest$B", ((Class) (o_testGetFirstTypeArgument_add34__5)).getTypeName());
        TestCase.assertEquals("com.google.gson.internal.AmplGsonTypesTest.B", ((Class) (o_testGetFirstTypeArgument_add34__5)).getCanonicalName());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isAnonymousClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isLocalClass());
        TestCase.assertTrue(((Class) (o_testGetFirstTypeArgument_add34__5)).isMemberClass());
        TestCase.assertFalse(((Class) (o_testGetFirstTypeArgument_add34__5)).isEnum());
        TestCase.assertNull(((Class) (o_testGetFirstTypeArgument_add34__5)).getEnumConstants());
        TestCase.assertEquals("java.lang.Object", ((Type) (((AnnotatedType) (((Class) (o_testGetFirstTypeArgument_add34__5)).getAnnotatedSuperclass())).getType())).getTypeName());
        TestCase.assertNull(o_testGetFirstTypeArgument_add34__1);
        TestCase.assertNull(o_testGetFirstTypeArgument_add34__2);
    }

    private static final class A {}

    private static final class B {}

    private static final class C {}

    public static Type getFirstTypeArgument(Type type) throws Exception {
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType ptype = ((ParameterizedType) (type));
        Type[] actualTypeArguments = ptype.getActualTypeArguments();
        if ((actualTypeArguments.length) == 0) {
            return null;
        }
        return $Gson$Types.canonicalize(actualTypeArguments[0]);
    }
}


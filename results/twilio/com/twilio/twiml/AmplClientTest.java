

package com.twilio.twiml;


public class AmplClientTest {
    @org.junit.Test
    public void testXml() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test
    public void testUrl() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf15() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        com.twilio.twiml.Method o_testUrl_cf15__6 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testUrl_cf15__6);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
        org.junit.Assert.assertNull(o_testUrl_cf27__6);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf18__6 = client.getName();
        org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf21__6);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf12() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        com.twilio.twiml.Method o_testUrl_cf12__6 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf12__6)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf12__6)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf12__6)).name(), "POST");
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21_cf195() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf21__6);
        com.twilio.twiml.Method o_testUrl_cf21_cf195__10 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21_cf210_cf1412_failAssert51() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf21__6);
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf21_cf210__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf21_cf210__10);
            com.twilio.twiml.Client vc_384 = ((com.twilio.twiml.Client) (null));
            vc_384.getMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf21_cf210_cf1412 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf15_cf139_cf600_failAssert4() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            com.twilio.twiml.Method o_testUrl_cf15__6 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf15__6);
            java.lang.String o_testUrl_cf15_cf139__10 = client.getUrl();
            org.junit.Assert.assertEquals(o_testUrl_cf15_cf139__10, "http://twilio.ca");
            com.twilio.twiml.Client vc_174 = ((com.twilio.twiml.Client) (null));
            vc_174.getStatusCallback();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf15_cf139_cf600 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf575_failAssert74() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_164 = ((com.twilio.twiml.Client) (null));
            vc_164.getUrl();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf575 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1503__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1500() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1500__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1500__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1494__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1497() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1497__8 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1497__8, "name");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1491() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1491__8 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1491__8)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1491__8)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1491__8)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1497_cf1640() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1497__8 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1497__8, "name");
        com.twilio.twiml.Method o_testXml_cf1497_cf1640__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1497_cf1640__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1500_cf1677_cf2095() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1500__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1500__8);
        com.twilio.twiml.Method o_testXml_cf1500_cf1677__12 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1500_cf1677__12);
        com.twilio.twiml.Method o_testXml_cf1500_cf1677_cf2095__16 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1677_cf2095__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1497_cf1643_cf2195_failAssert26() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1497__8 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1497__8, "name");
            com.twilio.twiml.Method o_testXml_cf1497_cf1643__12 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1497_cf1643__12);
            com.twilio.twiml.Client vc_600 = ((com.twilio.twiml.Client) (null));
            vc_600.getMethod();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1497_cf1643_cf2195 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf176_cf514_failAssert35() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf18_cf176__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf176__10);
            com.twilio.twiml.Client vc_140 = ((com.twilio.twiml.Client) (null));
            vc_140.getUrl();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf176_cf514 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27_cf278_cf1333() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
        org.junit.Assert.assertNull(o_testUrl_cf27__6);
        java.util.List<com.twilio.twiml.Event> o_testUrl_cf27_cf278__10 = client.getStatusCallbackEvents();
        org.junit.Assert.assertNull(o_testUrl_cf27_cf278__10);
        com.twilio.twiml.Method o_testUrl_cf27_cf278_cf1333__14 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf278_cf1333__14)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1606_cf2343() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1494__8);
        com.twilio.twiml.Method o_testXml_cf1494_cf1606__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isEnum());
        java.lang.String o_testXml_cf1494_cf1606_cf2343__130 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1494_cf1606_cf2343__130, "name");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1618_cf2520_failAssert4() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1494__8);
            java.lang.String o_testXml_cf1494_cf1618__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1494_cf1618__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_702 = ((com.twilio.twiml.Client) (null));
            vc_702.getStatusCallback();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1494_cf1618_cf2520 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1500_cf1674() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1500__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1500__8);
        com.twilio.twiml.Method o_testXml_cf1500_cf1674__12 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1500_cf1674__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1714_cf2694_failAssert62() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1503__8 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
            java.lang.String o_testXml_cf1503_cf1714__12 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1503_cf1714__12, "name");
            com.twilio.twiml.Client vc_746 = ((com.twilio.twiml.Client) (null));
            vc_746.getStatusCallbackMethod();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1503_cf1714_cf2694 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf161_cf1103() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf18__6 = client.getName();
        org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
        com.twilio.twiml.Method o_testUrl_cf18_cf161__10 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        com.twilio.twiml.Method o_testUrl_cf18_cf161_cf1103__128 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161_cf1103__128)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf571_failAssert50() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_158 = ((com.twilio.twiml.Client) (null));
            vc_158.getStatusCallbackMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf571 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1496() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1496__8 = client.getMethod();
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1496__8)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1496__8)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1496__8)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1499__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499_cf1620_cf2642_failAssert68() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1499__8);
            java.lang.String o_testXml_cf1499_cf1620__12 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testXml_cf1499_cf1620__12);
            com.twilio.twiml.Client vc_716 = ((com.twilio.twiml.Client) (null));
            vc_716.getUrl();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1499_cf1620_cf2642 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499_cf1623_cf2004_failAssert24() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1499__8);
            java.lang.String o_testXml_cf1499_cf1623__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1499_cf1623__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_550 = ((com.twilio.twiml.Client) (null));
            vc_550.getStatusCallbackEvents();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1499_cf1623_cf2004 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1502() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1502__8 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1502__8, "name");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1505() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1505__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1505__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1508() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1508__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1508__8, "http://twilio.ca");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1508_cf1713() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1508__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1508__8, "http://twilio.ca");
        com.twilio.twiml.Method o_testXml_cf1508_cf1713__12 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).name(), "POST");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf15_cf136_cf793() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        com.twilio.twiml.Method o_testUrl_cf15__6 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testUrl_cf15__6);
        java.lang.String o_testUrl_cf15_cf136__10 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf15_cf136__10);
        com.twilio.twiml.Method o_testUrl_cf15_cf136_cf793__14 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isArray());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf15_cf136_cf793__14)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf161() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf18__6 = client.getName();
        org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
        com.twilio.twiml.Method o_testUrl_cf18_cf161__10 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf18_cf161__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21_cf210_cf1421_failAssert71() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf21__6);
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf21_cf210__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf21_cf210__10);
            com.twilio.twiml.Client vc_390 = ((com.twilio.twiml.Client) (null));
            vc_390.getStatusCallback();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf21_cf210_cf1421 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1618_cf2511_failAssert36() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1494__8);
            java.lang.String o_testXml_cf1494_cf1618__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1494_cf1618__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_696 = ((com.twilio.twiml.Client) (null));
            vc_696.getMethod();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1494_cf1618_cf2511 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf572_failAssert13() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_162 = ((com.twilio.twiml.Client) (null));
            vc_162.getStatusCallback();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf572 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf229() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        com.twilio.twiml.Method o_testUrl_cf24_cf229__10 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).name(), "POST");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1618_cf2526_failAssert33() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1494__8);
            java.lang.String o_testXml_cf1494_cf1618__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1494_cf1618__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_706 = ((com.twilio.twiml.Client) (null));
            vc_706.getStatusCallbackEvents();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1494_cf1618_cf2526 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1717_cf2764() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1503__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
        java.lang.String o_testXml_cf1503_cf1717__12 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1503_cf1717__12);
        com.twilio.twiml.Method o_testXml_cf1503_cf1717_cf2764__16 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1717_cf2764__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf15_cf142_cf1377_failAssert54() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            com.twilio.twiml.Method o_testUrl_cf15__6 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf15__6);
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf15_cf142__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf15_cf142__10);
            com.twilio.twiml.Client vc_376 = ((com.twilio.twiml.Client) (null));
            vc_376.getName();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf15_cf142_cf1377 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf571_failAssert52() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_158 = ((com.twilio.twiml.Client) (null));
            vc_158.getStatusCallbackMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf571 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27_cf272_cf979() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
        org.junit.Assert.assertNull(o_testUrl_cf27__6);
        java.lang.String o_testUrl_cf27_cf272__10 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf27_cf272__10);
        com.twilio.twiml.Method o_testUrl_cf27_cf272_cf979__14 = client.getMethod();
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf27_cf272_cf979__14)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1502_cf1645_cf2665() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1502__8 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1502__8, "name");
        com.twilio.twiml.Method o_testXml_cf1502_cf1645__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1502_cf1645__12)).getDeclaringClass())).getModifiers(), 16401);
        java.lang.String o_testXml_cf1502_cf1645_cf2665__130 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1502_cf1645_cf2665__130, "name");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1505_cf1679() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1505__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1505__8);
        com.twilio.twiml.Method o_testXml_cf1505_cf1679__12 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1679__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1612_cf1913_failAssert11() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1494__8);
            java.lang.String o_testXml_cf1494_cf1612__12 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1494_cf1612__12, "name");
            com.twilio.twiml.Client vc_518 = ((com.twilio.twiml.Client) (null));
            vc_518.getStatusCallbackMethod();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1494_cf1612_cf1913 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1708_cf1986() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1503__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
        com.twilio.twiml.Method o_testXml_cf1503_cf1708__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).name(), "POST");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        com.twilio.twiml.Method o_testXml_cf1503_cf1708_cf1986__130 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).ordinal(), 1);
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708_cf1986__130)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf173_cf744_failAssert45() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf173__10 = client.getUrl();
            org.junit.Assert.assertEquals(o_testUrl_cf18_cf173__10, "http://twilio.ca");
            com.twilio.twiml.Client vc_204 = ((com.twilio.twiml.Client) (null));
            vc_204.getMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf173_cf744 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1494_cf1606() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1494__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1494__8);
        com.twilio.twiml.Method o_testXml_cf1494_cf1606__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1494_cf1606__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf164_cf305_failAssert48() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            com.twilio.twiml.Method o_testUrl_cf18_cf164__10 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf164__10);
            com.twilio.twiml.Client vc_94 = ((com.twilio.twiml.Client) (null));
            vc_94.getStatusCallbackEvents();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf164_cf305 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf244_cf1287_failAssert61() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf24__6 = client.getUrl();
            org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf24_cf244__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf24_cf244__10);
            com.twilio.twiml.Client vc_340 = ((com.twilio.twiml.Client) (null));
            vc_340.getName();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf24_cf244_cf1287 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499_cf1617_cf2316_failAssert54() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1499__8);
            java.lang.String o_testXml_cf1499_cf1617__12 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1499_cf1617__12, "name");
            com.twilio.twiml.Client vc_618 = ((com.twilio.twiml.Client) (null));
            vc_618.getStatusCallback();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1499_cf1617_cf2316 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499_cf1620_cf2645_failAssert46() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1499__8);
            java.lang.String o_testXml_cf1499_cf1620__12 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testXml_cf1499_cf1620__12);
            com.twilio.twiml.Client vc_718 = ((com.twilio.twiml.Client) (null));
            vc_718.getStatusCallbackEvents();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1499_cf1620_cf2645 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1502_cf1657_cf2698_failAssert18() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1502__8 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1502__8, "name");
            java.lang.String o_testXml_cf1502_cf1657__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1502_cf1657__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_740 = ((com.twilio.twiml.Client) (null));
            vc_740.getUrl();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1502_cf1657_cf2698 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1505_cf1685_cf1922() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1505__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1505__8);
        java.lang.String o_testXml_cf1505_cf1685__12 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1505_cf1685__12, "name");
        com.twilio.twiml.Method o_testXml_cf1505_cf1685_cf1922__16 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1505_cf1685_cf1922__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf232_cf1178_failAssert6() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf24__6 = client.getUrl();
            org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
            com.twilio.twiml.Method o_testUrl_cf24_cf232__10 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf24_cf232__10);
            com.twilio.twiml.Client vc_308 = ((com.twilio.twiml.Client) (null));
            vc_308.getUrl();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf24_cf232_cf1178 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1708() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1503__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
        com.twilio.twiml.Method o_testXml_cf1503_cf1708__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).name(), "POST");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1708__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1711_cf1833() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1503__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
        com.twilio.twiml.Method o_testXml_cf1503_cf1711__12 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1503_cf1711__12);
        com.twilio.twiml.Method o_testXml_cf1503_cf1711_cf1833__16 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1503_cf1711_cf1833__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1717_cf2769_failAssert52() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1503__8 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
            java.lang.String o_testXml_cf1503_cf1717__12 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testXml_cf1503_cf1717__12);
            com.twilio.twiml.Client vc_760 = ((com.twilio.twiml.Client) (null));
            vc_760.getName();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1503_cf1717_cf2769 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf244_cf1287_failAssert60() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf24__6 = client.getUrl();
            org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf24_cf244__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf24_cf244__10);
            com.twilio.twiml.Client vc_340 = ((com.twilio.twiml.Client) (null));
            vc_340.getName();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf24_cf244_cf1287 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1499_cf1611() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1499__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1499__8);
        com.twilio.twiml.Method o_testXml_cf1499_cf1611__12 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1499_cf1611__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1508_cf1713_cf2264() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1508__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1508__8, "http://twilio.ca");
        com.twilio.twiml.Method o_testXml_cf1508_cf1713__12 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).name(), "POST");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1508_cf1713__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        java.lang.String o_testXml_cf1508_cf1713_cf2264__130 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1508_cf1713_cf2264__130, "http://twilio.ca");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf235() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        java.lang.String o_testUrl_cf24_cf235__10 = client.getName();
        org.junit.Assert.assertEquals(o_testUrl_cf24_cf235__10, "name");
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27_cf266_cf1218_failAssert65() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf27__6);
            com.twilio.twiml.Method o_testUrl_cf27_cf266__10 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf27_cf266__10);
            com.twilio.twiml.Client vc_314 = ((com.twilio.twiml.Client) (null));
            vc_314.getStatusCallbackMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf27_cf266_cf1218 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1503_cf1714_cf2697_failAssert53() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1503__8 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1503__8, "http://twilio.ca");
            java.lang.String o_testXml_cf1503_cf1714__12 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1503_cf1714__12, "name");
            com.twilio.twiml.Client vc_748 = ((com.twilio.twiml.Client) (null));
            vc_748.getName();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1503_cf1714_cf2697 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf579_failAssert32() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_160 = ((com.twilio.twiml.Client) (null));
            vc_160.getName();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf579 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf176_cf527_failAssert23() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf18_cf176__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf176__10);
            com.twilio.twiml.Client vc_142 = ((com.twilio.twiml.Client) (null));
            vc_142.getStatusCallbackEvents();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf176_cf527 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf235_cf1075() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        java.lang.String o_testUrl_cf24_cf235__10 = client.getName();
        org.junit.Assert.assertEquals(o_testUrl_cf24_cf235__10, "name");
        com.twilio.twiml.Method o_testUrl_cf24_cf235_cf1075__14 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf235_cf1075__14)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1501() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1501__8 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1501__8)).name(), "POST");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1501__8)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1501_cf1591_cf2821_failAssert10() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1501__8 = client.getMethod();
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
            org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1501__8)).name(), "POST");
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getModifiers(), 16401);
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationTitle());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationTitle());
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSigners());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationVersion());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationVersion());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isSynthetic());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getComponentType());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isSynthetic());
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isInterface());
            org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).isSealed());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnumConstants());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getSigners());
            org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getComponentType());
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isLocalClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getDeclaringClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isArray());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isAnnotation());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isPrimitive());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getClassLoader());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isMemberClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isAnonymousClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).desiredAssertionStatus());
            org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isAnnotation());
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingMethod());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isLocalClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
            org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingClass());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isMemberClass());
            org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSimpleName(), "Method");
            org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1501__8)).ordinal(), 1);
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getSpecificationVendor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isPrimitive());
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
            org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getEnclosingConstructor());
            org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).isEnum());
            org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getPackage())).getImplementationVendor());
            org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
            org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1501__8)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
            java.lang.String o_testXml_cf1501_cf1591__126 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testXml_cf1501_cf1591__126);
            com.twilio.twiml.Client vc_766 = ((com.twilio.twiml.Client) (null));
            vc_766.getStatusCallbackEvents();
            java.lang.Object o_134_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1501_cf1591_cf2821 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1504() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        com.twilio.twiml.Method o_testXml_cf1504__8 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testXml_cf1504__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1504_cf1622_cf1940_failAssert13() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1504__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1504__8);
            java.lang.String o_testXml_cf1504_cf1622__12 = client.getName();
            org.junit.Assert.assertEquals(o_testXml_cf1504_cf1622__12, "name");
            com.twilio.twiml.Client vc_534 = ((com.twilio.twiml.Client) (null));
            vc_534.getStatusCallback();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1504_cf1622_cf1940 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1507() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1507__8 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1507__8, "name");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1510() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1510__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1510__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1510_cf1684() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1510__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1510__8);
        com.twilio.twiml.Method o_testXml_cf1510_cf1684__12 = client.getMethod();
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1510_cf1684__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1513() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1513__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1513__8, "http://twilio.ca");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1513_cf1724_cf2133() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1513__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1513__8, "http://twilio.ca");
        java.lang.String o_testXml_cf1513_cf1724__12 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1513_cf1724__12, "name");
        com.twilio.twiml.Method o_testXml_cf1513_cf1724_cf2133__16 = client.getMethod();
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1513_cf1724_cf2133__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21_cf195_cf1148() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf21__6);
        com.twilio.twiml.Method o_testUrl_cf21_cf195__10 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf21_cf195__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        java.lang.String o_testUrl_cf21_cf195_cf1148__128 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testUrl_cf21_cf195_cf1148__128);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf21_cf210_cf1427_failAssert37() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf21__6 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf21__6);
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf21_cf210__10 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf21_cf210__10);
            com.twilio.twiml.Client vc_394 = ((com.twilio.twiml.Client) (null));
            vc_394.getStatusCallbackEvents();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf21_cf210_cf1427 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf15_cf136_cf798_failAssert50() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            com.twilio.twiml.Method o_testUrl_cf15__6 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf15__6);
            java.lang.String o_testUrl_cf15_cf136__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf15_cf136__10);
            com.twilio.twiml.Client vc_216 = ((com.twilio.twiml.Client) (null));
            vc_216.getMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf15_cf136_cf798 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf232_cf1173() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        com.twilio.twiml.Method o_testUrl_cf24_cf232__10 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testUrl_cf24_cf232__10);
        com.twilio.twiml.Method o_testUrl_cf24_cf232_cf1173__14 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).name(), "POST");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf232_cf1173__14)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27_cf266_cf1221_failAssert17() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf27__6);
            com.twilio.twiml.Method o_testUrl_cf27_cf266__10 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf27_cf266__10);
            com.twilio.twiml.Client vc_312 = ((com.twilio.twiml.Client) (null));
            vc_312.getMethod();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf27_cf266_cf1221 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1500_cf1624_cf2005_failAssert24() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            com.twilio.twiml.Method o_testXml_cf1500__8 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1500__8);
            java.lang.String o_testXml_cf1500_cf1624__12 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1500_cf1624__12, "http://twilio.ca");
            com.twilio.twiml.Client vc_550 = ((com.twilio.twiml.Client) (null));
            vc_550.getStatusCallbackEvents();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1500_cf1624_cf2005 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1506() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1506__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1506__8);
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1506_cf1680() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1506__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1506__8);
        com.twilio.twiml.Method o_testXml_cf1506_cf1680__12 = client.getMethod();
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).ordinal(), 1);
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1680__12)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1506_cf1686_cf1923() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1506__8 = client.getStatusCallback();
        org.junit.Assert.assertNull(o_testXml_cf1506__8);
        java.lang.String o_testXml_cf1506_cf1686__12 = client.getName();
        org.junit.Assert.assertEquals(o_testXml_cf1506_cf1686__12, "name");
        com.twilio.twiml.Method o_testXml_cf1506_cf1686_cf1923__16 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getModifiers(), 16401);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).ordinal(), 1);
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).name(), "POST");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testXml_cf1506_cf1686_cf1923__16)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1509() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
        java.lang.String o_testXml_cf1509__8 = client.getUrl();
        org.junit.Assert.assertEquals(o_testXml_cf1509__8, "http://twilio.ca");
        org.junit.Assert.assertEquals("<Client method=\"POST\" url=\"http://twilio.ca\" statusCallbackEvent=\"answered initiated\">name</Client>", client.toXml());
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf18_cf170_cf577_failAssert80() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.lang.String o_testUrl_cf18__6 = client.getName();
            org.junit.Assert.assertEquals(o_testUrl_cf18__6, "name");
            java.lang.String o_testUrl_cf18_cf170__10 = client.getStatusCallback();
            org.junit.Assert.assertNull(o_testUrl_cf18_cf170__10);
            com.twilio.twiml.Client vc_162 = ((com.twilio.twiml.Client) (null));
            vc_162.getStatusCallback();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf18_cf170_cf577 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf27_cf266_cf1226_failAssert17() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
            java.util.List<com.twilio.twiml.Event> o_testUrl_cf27__6 = client.getStatusCallbackEvents();
            org.junit.Assert.assertNull(o_testUrl_cf27__6);
            com.twilio.twiml.Method o_testUrl_cf27_cf266__10 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testUrl_cf27_cf266__10);
            com.twilio.twiml.Client vc_316 = ((com.twilio.twiml.Client) (null));
            vc_316.getName();
            java.lang.Object o_18_0 = client.toUrl();
            org.junit.Assert.fail("testUrl_cf27_cf266_cf1226 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    @org.junit.Test(timeout = 10000)
    public void testUrl_cf24_cf229_cf544() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").build();
        java.lang.String o_testUrl_cf24__6 = client.getUrl();
        org.junit.Assert.assertEquals(o_testUrl_cf24__6, "http://twilio.ca");
        com.twilio.twiml.Method o_testUrl_cf24_cf229__10 = client.getMethod();
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationVersion());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingMethod());
        org.junit.Assert.assertEquals(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getName(), "com.twilio.twiml");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isAnnotation());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).toGenericString(), "public final enum com.twilio.twiml.Method");
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isArray());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isArray());
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getGenericSuperclass())).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getDeclaringClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSimpleName(), "Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getSimpleName(), "Enum");
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSigners());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getSigners());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isMemberClass());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnumConstants());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getCanonicalName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationVersion());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isAnnotation());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getImplementationTitle());
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getGenericSuperclass())).getOwnerType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isLocalClass());
        org.junit.Assert.assertFalse(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).isSealed());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).desiredAssertionStatus());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getComponentType());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).desiredAssertionStatus());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getTypeName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingConstructor());
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getDeclaringClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isMemberClass());
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isAnonymousClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).name(), "POST");
        org.junit.Assert.assertTrue(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isEnum());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getCanonicalName(), "java.lang.Enum");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingClass());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationTitle());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isEnum());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getEnclosingClass());
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).ordinal(), 1);
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getModifiers(), 1025);
        org.junit.Assert.assertFalse(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).isInterface());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isPrimitive());
        org.junit.Assert.assertNull(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).getClassLoader());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isSynthetic());
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getTypeName(), "com.twilio.twiml.Method");
        org.junit.Assert.assertNull(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getEnclosingMethod());
        org.junit.Assert.assertNull(((java.lang.Package) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getPackage())).getSpecificationVendor());
        org.junit.Assert.assertFalse(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).isInterface());
        org.junit.Assert.assertEquals(((java.lang.Class) (((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getSuperclass())).toGenericString(), "public abstract class java.lang.Enum<E>");
        org.junit.Assert.assertEquals(((java.lang.Class) (((com.twilio.twiml.Method) (o_testUrl_cf24_cf229__10)).getDeclaringClass())).getModifiers(), 16401);
        com.twilio.twiml.Method o_testUrl_cf24_cf229_cf544__128 = client.getStatusCallbackMethod();
        org.junit.Assert.assertNull(o_testUrl_cf24_cf229_cf544__128);
        org.junit.Assert.assertEquals("%3CClient+method%3D%22POST%22+url%3D%22http%3A%2F%2Ftwilio.ca%22%3Ename%3C%2FClient%3E", client.toUrl());
    }

    @org.junit.Test(timeout = 10000)
    public void testXml_cf1508_cf1716_cf2022_failAssert64() throws com.twilio.twiml.TwiMLException {
        try {
            com.twilio.twiml.Client client = new com.twilio.twiml.Client.Builder("name").method(com.twilio.twiml.Method.POST).url("http://twilio.ca").statusCallbackEvents(com.google.common.collect.Lists.newArrayList(com.twilio.twiml.Event.ANSWERED, com.twilio.twiml.Event.INITIATED)).build();
            java.lang.String o_testXml_cf1508__8 = client.getUrl();
            org.junit.Assert.assertEquals(o_testXml_cf1508__8, "http://twilio.ca");
            com.twilio.twiml.Method o_testXml_cf1508_cf1716__12 = client.getStatusCallbackMethod();
            org.junit.Assert.assertNull(o_testXml_cf1508_cf1716__12);
            com.twilio.twiml.Client vc_552 = ((com.twilio.twiml.Client) (null));
            vc_552.getMethod();
            java.lang.Object o_20_0 = client.toXml();
            org.junit.Assert.fail("testXml_cf1508_cf1716_cf2022 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


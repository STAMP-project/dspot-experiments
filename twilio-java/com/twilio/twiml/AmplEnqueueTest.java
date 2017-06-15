

package com.twilio.twiml;


/**
 * Test class for {@link Enqueue}.
 */
public class AmplEnqueueTest {
    @org.junit.Test
    public void testXml() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    @org.junit.Test
    public void testUrl() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf25() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        com.twilio.twiml.Method o_testUrl_cf25__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrlMethod();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getTypeName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getCanonicalName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSimpleName(), "Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getName(), "com.twilio.twiml");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testUrl_cf25__10).ordinal(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).toGenericString(), "public final enum com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testUrl_cf25__10).name(), "POST");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf25__10).getDeclaringClass()).getEnclosingMethod());
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf37() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testUrl_cf37__10 = // StatementAdderMethod cloned existing statement
enqueue.getWorkflowSid();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testUrl_cf37__10, "WF123");
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf28() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testUrl_cf28__10 = // StatementAdderMethod cloned existing statement
enqueue.getAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testUrl_cf28__10, "/enqueue");
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf31() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testUrl_cf31__10 = // StatementAdderMethod cloned existing statement
enqueue.getQueueName();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testUrl_cf31__10, "enqueue");
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf22() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        com.twilio.twiml.Method o_testUrl_cf22__10 = // StatementAdderMethod cloned existing statement
enqueue.getMethod();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getName(), "com.twilio.twiml");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSimpleName(), "Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testUrl_cf22__10).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).toGenericString(), "public final enum com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getCanonicalName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getTypeName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testUrl_cf22__10).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testUrl_cf22__10).name(), "GET");
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf34() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testUrl_cf34__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrl();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testUrl_cf34__10, "/wait");
        org.junit.Assert.assertEquals("%3CEnqueue+action%3D%22%2Fenqueue%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E", enqueue.toUrl());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf30_failAssert23_literalMutation348() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("W,F123").build();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getAction(), "/enqueue");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWaitUrl(), "/wait");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWorkflowSid(), "W,F123");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getQueueName(), "enqueue");
            // StatementAdderOnAssert create null value
            com.twilio.twiml.Enqueue vc_6 = (com.twilio.twiml.Enqueue)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6);
            // StatementAdderMethod cloned existing statement
            vc_6.getQueueName();
            // MethodAssertGenerator build local variable
            Object o_14_0 = enqueue.toUrl();
            org.junit.Assert.fail("testUrl_cf30 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testUrl */
    @org.junit.Test(timeout = 10000)
    public void testUrl_cf34_cf226_failAssert18_literalMutation3224() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("Wd123").build();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getAction(), "/enqueue");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWaitUrl(), "/wait");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWorkflowSid(), "Wd123");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getQueueName(), "enqueue");
            // AssertGenerator replace invocation
            java.lang.String o_testUrl_cf34__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrl();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testUrl_cf34__10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "/wait");
            // StatementAdderOnAssert create null value
            com.twilio.twiml.Enqueue vc_70 = (com.twilio.twiml.Enqueue)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_70);
            // StatementAdderMethod cloned existing statement
            vc_70.getWorkflowSid();
            // MethodAssertGenerator build local variable
            Object o_18_0 = enqueue.toUrl();
            org.junit.Assert.fail("testUrl_cf34_cf226 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3637() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testXml_cf3637__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrl();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testXml_cf3637__10, "/wait");
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3625() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        com.twilio.twiml.Method o_testXml_cf3625__10 = // StatementAdderMethod cloned existing statement
enqueue.getMethod();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testXml_cf3625__10).name(), "GET");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).toGenericString(), "public final enum com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSimpleName(), "Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testXml_cf3625__10).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getCanonicalName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getTypeName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getName(), "com.twilio.twiml");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getPackage()).getSpecificationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3625__10).getDeclaringClass()).getSuperclass()).isLocalClass());
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3628() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        com.twilio.twiml.Method o_testXml_cf3628__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrlMethod();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getSpecificationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getCanonicalName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testXml_cf3628__10).ordinal(), 1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getImplementationVendor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<com.twilio.twiml.Method>");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).toGenericString(), "public final enum com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getEnumConstants());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getName(), "com.twilio.twiml");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getSpecificationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSimpleName(), "Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).isSealed());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getImplementationVersion());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getClassLoader());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((com.twilio.twiml.Method)o_testXml_cf3628__10).name(), "POST");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getImplementationTitle());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getTypeName(), "com.twilio.twiml.Method");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((com.twilio.twiml.Method)o_testXml_cf3628__10).getDeclaringClass()).getPackage()).getSpecificationVersion());
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3634() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testXml_cf3634__10 = // StatementAdderMethod cloned existing statement
enqueue.getQueueName();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testXml_cf3634__10, "enqueue");
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3640() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testXml_cf3640__10 = // StatementAdderMethod cloned existing statement
enqueue.getWorkflowSid();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testXml_cf3640__10, "WF123");
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3631() throws com.twilio.twiml.TwiMLException {
        com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
        // AssertGenerator replace invocation
        java.lang.String o_testXml_cf3631__10 = // StatementAdderMethod cloned existing statement
enqueue.getAction();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testXml_cf3631__10, "/enqueue");
        org.junit.Assert.assertEquals("<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>", enqueue.toXml());
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3633_failAssert23_literalMutation3947() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("/enqueue").method(com.twilio.twiml.Method.GET).waitUrl("/wit").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getAction(), "/enqueue");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWaitUrl(), "/wit");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).toXml(), "<Enqueue action=\"/enqueue\" method=\"GET\" waitUrl=\"/wit\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWorkflowSid(), "WF123");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getQueueName(), "enqueue");
            // StatementAdderOnAssert create null value
            com.twilio.twiml.Enqueue vc_942 = (com.twilio.twiml.Enqueue)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_942);
            // StatementAdderMethod cloned existing statement
            vc_942.getQueueName();
            // MethodAssertGenerator build local variable
            Object o_14_0 = enqueue.toXml();
            org.junit.Assert.fail("testXml_cf3633 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.twilio.twiml.EnqueueTest#testXml */
    @org.junit.Test(timeout = 10000)
    public void testXml_cf3637_cf3829_failAssert60_literalMutation6867() throws com.twilio.twiml.TwiMLException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            com.twilio.twiml.Enqueue enqueue = new com.twilio.twiml.Enqueue.Builder().queueName("enqueue").action("4!:[8)} ").method(com.twilio.twiml.Method.GET).waitUrl("/wait").waitUrlMethod(com.twilio.twiml.Method.POST).workflowSid("WF123").build();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getAction(), "4!:[8)} ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWaitUrl(), "/wait");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).toUrl(), "%3CEnqueue+action%3D%224%21%3A%5B8%29%7D+%22+method%3D%22GET%22+waitUrl%3D%22%2Fwait%22+waitUrlMethod%3D%22POST%22+workflowSid%3D%22WF123%22%3Eenqueue%3C%2FEnqueue%3E");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).toXml(), "<Enqueue action=\"4!:[8)} \" method=\"GET\" waitUrl=\"/wait\" waitUrlMethod=\"POST\" workflowSid=\"WF123\">enqueue</Enqueue>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getWorkflowSid(), "WF123");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((com.twilio.twiml.Enqueue)enqueue).getQueueName(), "enqueue");
            // AssertGenerator replace invocation
            java.lang.String o_testXml_cf3637__10 = // StatementAdderMethod cloned existing statement
enqueue.getWaitUrl();
            // MethodAssertGenerator build local variable
            Object o_12_0 = o_testXml_cf3637__10;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_12_0, "/wait");
            // StatementAdderOnAssert create null value
            com.twilio.twiml.Enqueue vc_1006 = (com.twilio.twiml.Enqueue)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1006);
            // StatementAdderMethod cloned existing statement
            vc_1006.getWorkflowSid();
            // MethodAssertGenerator build local variable
            Object o_18_0 = enqueue.toXml();
            org.junit.Assert.fail("testXml_cf3637_cf3829 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


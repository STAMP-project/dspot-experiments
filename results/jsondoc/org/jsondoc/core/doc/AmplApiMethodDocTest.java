

package org.jsondoc.core.doc;


public class AmplApiMethodDocTest {
    private org.jsondoc.core.pojo.ApiMethodDoc first;

    private org.jsondoc.core.pojo.ApiMethodDoc second;

    @org.junit.Test
    public void testNotEqual() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    @org.junit.Test
    public void testEqual() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    @org.junit.Test
    public void testNotEqualMultipleVerbs() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
        second.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.PUT, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    @org.junit.Test
    public void testEqualMultipleVerbs() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.POST, org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqual */
    @org.junit.Test(timeout = 10000)
    public void testEqual_add3() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_546539786 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_546539786, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_330290042 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_330290042, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_2029114054 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2029114054, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1260551865 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1260551865, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_570235587 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_570235587, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_180942088 = new java.util.HashSet<Object>();
	collection_180942088.add("/test");
	org.junit.Assert.assertEquals(collection_180942088, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1619235298 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1619235298, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_268547302 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_268547302, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_700561215 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_700561215, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1457453798 = new java.util.HashSet<Object>();
	collection_1457453798.add("/test");
	org.junit.Assert.assertEquals(collection_1457453798, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_197531329 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_197531329, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_628323647 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_628323647, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqual */
    @org.junit.Test(timeout = 10000)
    public void testEqual_add1_add29_literalMutation171() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_769921297 = new java.util.HashSet<Object>();
	collection_769921297.add("/test");
	org.junit.Assert.assertEquals(collection_769921297, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1241336023 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1241336023, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1870528012 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1870528012, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1393609086 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1393609086, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1637437716 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1637437716, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_952348628 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_952348628, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1460840393 = new java.util.HashSet<Object>();
	collection_1460840393.add("/test");
	org.junit.Assert.assertEquals(collection_1460840393, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1939288468 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1939288468, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_815965707 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_815965707, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1103277329 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1103277329, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_229831947 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_229831947, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_545136467 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_545136467, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_353841720 = new java.util.HashSet<Object>();
	collection_353841720.add("/test");
	org.junit.Assert.assertEquals(collection_353841720, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_264952101 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_264952101, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1942516560 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1942516560, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1780695880 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1780695880, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1421456049 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1421456049, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1393593176 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1393593176, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_612462288 = new java.util.HashSet<Object>();
	collection_612462288.add("/test");
	org.junit.Assert.assertEquals(collection_612462288, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_385739734 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_385739734, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1559479116 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1559479116, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_388927692 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_388927692, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1556516060 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1556516060, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_801617208 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_801617208, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_705575982 = new java.util.HashSet<Object>();
	collection_705575982.add("/test");
	org.junit.Assert.assertEquals(collection_705575982, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2087776157 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2087776157, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1208308742 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1208308742, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_637101317 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_637101317, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1460987061 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1460987061, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1087730260 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1087730260, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_504810246 = new java.util.HashSet<Object>();
	collection_504810246.add("/test");
	org.junit.Assert.assertEquals(collection_504810246, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_602598 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_602598, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_166881722 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_166881722, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_86245699 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_86245699, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_583877465 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_583877465, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_847705936 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_847705936, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1750759269 = new java.util.HashSet<Object>();
	collection_1750759269.add("/test");
	org.junit.Assert.assertEquals(collection_1750759269, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1020569915 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1020569915, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_451926225 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_451926225, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1943267380 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1943267380, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_628956504 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_628956504, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2066018751 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2066018751, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_857789643 = new java.util.HashSet<Object>();
	collection_857789643.add("/test");
	org.junit.Assert.assertEquals(collection_857789643, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_350259879 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_350259879, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1558415123 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1558415123, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_430571657 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_430571657, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_495928812 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_495928812, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_843903130 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_843903130, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_373437683 = new java.util.HashSet<Object>();
	collection_373437683.add("/test");
	org.junit.Assert.assertEquals(collection_373437683, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_384326824 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_384326824, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1071819491 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1071819491, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1111051201 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1111051201, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2054284687 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2054284687, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1757679044 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1757679044, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1251257774 = new java.util.HashSet<Object>();
	collection_1251257774.add("/test");
	org.junit.Assert.assertEquals(collection_1251257774, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_546305995 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_546305995, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_708015336 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_708015336, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_634070981 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_634070981, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_69789531 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_69789531, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_774093637 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_774093637, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqual */
    @org.junit.Test(timeout = 10000)
    public void testEqual_add1_cf44_failAssert6_add276() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_467662628 = new java.util.HashSet<Object>();
	collection_467662628.add("/test");
	org.junit.Assert.assertEquals(collection_467662628, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2049689704 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2049689704, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_72157808 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_72157808, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_949381850 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_949381850, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1324482153 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1324482153, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1571566569 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1571566569, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_379574722 = new java.util.HashSet<Object>();
	collection_379574722.add("/test");
	org.junit.Assert.assertEquals(collection_379574722, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1656297247 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1656297247, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1748263983 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1748263983, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_68255840 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_68255840, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1597496622 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1597496622, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_694600695 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_694600695, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_705575982 = new java.util.HashSet<Object>();
	collection_705575982.add("/test");
	org.junit.Assert.assertEquals(collection_705575982, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2087776157 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2087776157, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1208308742 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1208308742, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_637101317 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_637101317, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1460987061 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1460987061, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1087730260 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1087730260, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_504810246 = new java.util.HashSet<Object>();
	collection_504810246.add("/test");
	org.junit.Assert.assertEquals(collection_504810246, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_602598 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_602598, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_166881722 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_166881722, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_86245699 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_86245699, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_583877465 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_583877465, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_847705936 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_847705936, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_466340876 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_466340876, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_810845021 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_810845021, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1594428356 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1594428356, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_288616058 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_288616058, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1544293683 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1544293683, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
            // AssertGenerator add assertion
            java.util.HashSet collection_251087051 = new java.util.HashSet<Object>();
	collection_251087051.add("/test");
	org.junit.Assert.assertEquals(collection_251087051, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1849678025 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1849678025, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_1813213287 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1813213287, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1649477018 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1649477018, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_94984242 = new java.util.HashSet<Object>();
	collection_94984242.add("/test");
	org.junit.Assert.assertEquals(collection_94984242, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1201749559 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1201749559, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_433858128 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_433858128, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
            second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_28 = (org.jsondoc.core.annotation.ApiMethod)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_28);
            // StatementAdderMethod cloned existing statement
            vc_28.consumes();
            // MethodAssertGenerator build local variable
            Object o_206_0 = first.compareTo(second);
            org.junit.Assert.fail("testEqual_add1_cf44 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testEqualMultipleVerbs_add666() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_345344132 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_345344132, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1478760414 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1478760414, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1450307707 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1450307707, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_166602732 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_166602732, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2008432368 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2008432368, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1526666204 = new java.util.HashSet<Object>();
	collection_1526666204.add("/test");
	org.junit.Assert.assertEquals(collection_1526666204, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1946728060 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1946728060, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_426137008 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_426137008, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_458185844 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_458185844, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1608365884 = new java.util.HashSet<Object>();
	collection_1608365884.add("/test");
	org.junit.Assert.assertEquals(collection_1608365884, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1903773485 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1903773485, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1233378882 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1233378882, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.POST, org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testEqualMultipleVerbs_add664_cf719_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_415835227 = new java.util.HashSet<Object>();
	collection_415835227.add("/test");
	org.junit.Assert.assertEquals(collection_415835227, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_449190247 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_449190247, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1045373462 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1045373462, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1943252191 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1943252191, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_848912885 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_848912885, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2035223390 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2035223390, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_641670534 = new java.util.HashSet<Object>();
	collection_641670534.add("/test");
	org.junit.Assert.assertEquals(collection_641670534, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_440706298 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_440706298, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_107602426 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_107602426, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1214747700 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1214747700, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_800688439 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_800688439, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_838598596 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_838598596, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            // MethodAssertGenerator build local variable
            Object o_202_0 = first.compareTo(second);
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.POST, org.jsondoc.core.pojo.ApiVerb.GET));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_252 = (org.jsondoc.core.annotation.ApiMethod)null;
            // StatementAdderMethod cloned existing statement
            vc_252.produces();
            // MethodAssertGenerator build local variable
            Object o_210_0 = first.compareTo(second);
            org.junit.Assert.fail("testEqualMultipleVerbs_add664_cf719 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testEqualMultipleVerbs_add664_literalMutation703() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1013976542 = new java.util.HashSet<Object>();
	collection_1013976542.add("/test");
	org.junit.Assert.assertEquals(collection_1013976542, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_431989023 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_431989023, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_513142418 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_513142418, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_589606911 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_589606911, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_816276930 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_816276930, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_823051115 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_823051115, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1317489483 = new java.util.HashSet<Object>();
	collection_1317489483.add("/test");
	org.junit.Assert.assertEquals(collection_1317489483, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_134527888 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_134527888, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_581001032 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_581001032, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2125415936 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2125415936, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_43730608 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_43730608, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1274417824 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1274417824, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_415835227 = new java.util.HashSet<Object>();
	collection_415835227.add("/test");
	org.junit.Assert.assertEquals(collection_415835227, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_449190247 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_449190247, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1045373462 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1045373462, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1943252191 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1943252191, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_848912885 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_848912885, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2035223390 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2035223390, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_641670534 = new java.util.HashSet<Object>();
	collection_641670534.add("/test");
	org.junit.Assert.assertEquals(collection_641670534, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_440706298 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_440706298, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_107602426 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_107602426, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1214747700 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1214747700, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_800688439 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_800688439, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_838598596 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_838598596, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        first.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/test"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.POST, org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqual */
    @org.junit.Test(timeout = 10000)
    public void testNotEqual_add1497() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1635233521 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1635233521, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_711175386 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_711175386, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1975388207 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1975388207, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_224988341 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_224988341, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_17447759 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_17447759, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_586410125 = new java.util.HashSet<Object>();
	collection_586410125.add("/second");
	org.junit.Assert.assertEquals(collection_586410125, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1974261957 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1974261957, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1085769902 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1085769902, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_901488697 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_901488697, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_2110406079 = new java.util.HashSet<Object>();
	collection_2110406079.add("/second");
	org.junit.Assert.assertEquals(collection_2110406079, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_27407710 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_27407710, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_208169404 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_208169404, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqual */
    @org.junit.Test(timeout = 10000)
    public void testNotEqual_add1495_literalMutation1529() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_200375076 = new java.util.HashSet<Object>();
	collection_200375076.add("/first");
	org.junit.Assert.assertEquals(collection_200375076, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_78679048 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_78679048, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1260752836 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1260752836, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1062925903 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1062925903, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2050178301 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2050178301, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_526204494 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_526204494, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_268955995 = new java.util.HashSet<Object>();
	collection_268955995.add("/first");
	org.junit.Assert.assertEquals(collection_268955995, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1136612584 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1136612584, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_495223974 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_495223974, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1390642289 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1390642289, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_765253925 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_765253925, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_815481915 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_815481915, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1483753690 = new java.util.HashSet<Object>();
	collection_1483753690.add("/first");
	org.junit.Assert.assertEquals(collection_1483753690, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2022885670 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2022885670, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_742307826 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_742307826, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_842049914 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_842049914, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1278459307 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1278459307, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_684301108 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_684301108, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1622776133 = new java.util.HashSet<Object>();
	collection_1622776133.add("/first");
	org.junit.Assert.assertEquals(collection_1622776133, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1336260952 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1336260952, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_334800024 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_334800024, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_136312634 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_136312634, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_924018627 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_924018627, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1554722225 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1554722225, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqual */
    @org.junit.Test(timeout = 10000)
    public void testNotEqual_cf1516_failAssert8_add1844() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_1152393071 = new java.util.HashSet<Object>();
	collection_1152393071.add("/first");
	org.junit.Assert.assertEquals(collection_1152393071, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1141137524 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1141137524, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_807676905 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_807676905, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1996332483 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1996332483, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2093127089 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2093127089, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_613757141 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_613757141, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_958123512 = new java.util.HashSet<Object>();
	collection_958123512.add("/first");
	org.junit.Assert.assertEquals(collection_958123512, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_192752966 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_192752966, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_47035121 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_47035121, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_864998930 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_864998930, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1335496500 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1335496500, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1829399600 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1829399600, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_494 = (org.jsondoc.core.annotation.ApiMethod)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_494);
            // StatementAdderMethod cloned existing statement
            vc_494.stage();
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            org.junit.Assert.fail("testNotEqual_cf1516 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqual */
    @org.junit.Test(timeout = 10000)
    public void testNotEqual_cf1506_failAssert3_cf1678_add6769() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_1791206771 = new java.util.HashSet<Object>();
	collection_1791206771.add("/first");
	org.junit.Assert.assertEquals(collection_1791206771, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_248151649 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_248151649, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_879466002 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_879466002, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1354771478 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1354771478, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_618453927 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_618453927, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1214606191 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1214606191, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_750915326 = new java.util.HashSet<Object>();
	collection_750915326.add("/first");
	org.junit.Assert.assertEquals(collection_750915326, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_351422533 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_351422533, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1406543118 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1406543118, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1211378508 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1211378508, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_578386844 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_578386844, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_778038169 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_778038169, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_484 = (org.jsondoc.core.annotation.ApiMethod)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_484);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_484);
            // StatementAdderMethod cloned existing statement
            vc_484.responsestatuscode();
            // StatementAdderMethod cloned existing statement
            vc_484.path();
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            org.junit.Assert.fail("testNotEqual_cf1506 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqual */
    @org.junit.Test(timeout = 10000)
    public void testNotEqual_add1495_add1525_literalMutation5862() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1591910176 = new java.util.HashSet<Object>();
	collection_1591910176.add("/first");
	org.junit.Assert.assertEquals(collection_1591910176, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_875029720 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_875029720, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1256723747 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1256723747, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1758504921 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1758504921, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2055403170 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2055403170, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1847923389 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1847923389, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_869218106 = new java.util.HashSet<Object>();
	collection_869218106.add("/first");
	org.junit.Assert.assertEquals(collection_869218106, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1642948770 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1642948770, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1645206449 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1645206449, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1493673251 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1493673251, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_351356138 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_351356138, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1452031910 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1452031910, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1956167857 = new java.util.HashSet<Object>();
	collection_1956167857.add("/first");
	org.junit.Assert.assertEquals(collection_1956167857, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2032798491 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2032798491, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_277844407 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_277844407, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_2072773732 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2072773732, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_867499113 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_867499113, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2060292144 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2060292144, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1321622620 = new java.util.HashSet<Object>();
	collection_1321622620.add("/first");
	org.junit.Assert.assertEquals(collection_1321622620, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_838467130 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_838467130, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1745497413 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1745497413, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_709867065 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_709867065, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2079810159 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2079810159, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_497728786 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_497728786, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1483753690 = new java.util.HashSet<Object>();
	collection_1483753690.add("/first");
	org.junit.Assert.assertEquals(collection_1483753690, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2022885670 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2022885670, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_742307826 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_742307826, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_842049914 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_842049914, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1278459307 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1278459307, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_684301108 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_684301108, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1622776133 = new java.util.HashSet<Object>();
	collection_1622776133.add("/first");
	org.junit.Assert.assertEquals(collection_1622776133, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1336260952 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1336260952, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_334800024 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_334800024, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_136312634 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_136312634, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_924018627 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_924018627, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1554722225 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1554722225, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_235133625 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_235133625, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2089905931 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2089905931, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_36191766 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_36191766, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2034811975 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2034811975, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1753313063 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1753313063, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1866724243 = new java.util.HashSet<Object>();
	collection_1866724243.add("/second");
	org.junit.Assert.assertEquals(collection_1866724243, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_985093527 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_985093527, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_571787014 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_571787014, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_679241034 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_679241034, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1223452725 = new java.util.HashSet<Object>();
	collection_1223452725.add("/second");
	org.junit.Assert.assertEquals(collection_1223452725, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_184714294 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_184714294, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_152255992 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_152255992, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1373483533 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1373483533, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_931020825 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_931020825, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_496041263 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_496041263, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1123625520 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1123625520, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_620600286 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_620600286, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_739733435 = new java.util.HashSet<Object>();
	collection_739733435.add("/second");
	org.junit.Assert.assertEquals(collection_739733435, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1490312196 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1490312196, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_381252533 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_381252533, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_507010451 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_507010451, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_782387841 = new java.util.HashSet<Object>();
	collection_782387841.add("/second");
	org.junit.Assert.assertEquals(collection_782387841, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_609637093 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_609637093, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1975095932 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1975095932, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testNotEqualMultipleVerbs_add7121() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1275319911 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1275319911, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1564957528 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1564957528, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1988453661 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1988453661, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1300184593 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1300184593, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1640489830 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1640489830, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1119696812 = new java.util.HashSet<Object>();
	collection_1119696812.add("/second");
	org.junit.Assert.assertEquals(collection_1119696812, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1534393850 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1534393850, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1295390135 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1295390135, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1449644042 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1449644042, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_963353199 = new java.util.HashSet<Object>();
	collection_963353199.add("/second");
	org.junit.Assert.assertEquals(collection_963353199, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_36622574 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_36622574, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1851094290 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1851094290, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
        second.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.PUT, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testNotEqualMultipleVerbs_cf7139_failAssert6_add7389() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_619543312 = new java.util.HashSet<Object>();
	collection_619543312.add("/first");
	org.junit.Assert.assertEquals(collection_619543312, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1216084780 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1216084780, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_646248438 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_646248438, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            java.util.ArrayList collection_1110272402 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1110272402, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1605209607 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1605209607, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2082570894 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2082570894, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
            // AssertGenerator add assertion
            java.util.HashSet collection_764640322 = new java.util.HashSet<Object>();
	collection_764640322.add("/first");
	org.junit.Assert.assertEquals(collection_764640322, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1969881896 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1969881896, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_712608326 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_712608326, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            java.util.ArrayList collection_2019253223 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2019253223, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_525682531 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_525682531, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1357405776 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1357405776, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            second.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.PUT, org.jsondoc.core.pojo.ApiVerb.POST));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_3428 = (org.jsondoc.core.annotation.ApiMethod)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3428);
            // StatementAdderMethod cloned existing statement
            vc_3428.consumes();
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            org.junit.Assert.fail("testNotEqualMultipleVerbs_cf7139 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testNotEqualMultipleVerbs_add7119_add7154_literalMutation8867() {
        first = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1395699097 = new java.util.HashSet<Object>();
	collection_1395699097.add("/first");
	org.junit.Assert.assertEquals(collection_1395699097, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1616688514 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1616688514, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_89306170 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_89306170, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1151482835 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1151482835, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_430010238 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_430010238, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1840184892 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1840184892, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_908035359 = new java.util.HashSet<Object>();
	collection_908035359.add("/first");
	org.junit.Assert.assertEquals(collection_908035359, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_515277601 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_515277601, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1302827503 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1302827503, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_38199458 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_38199458, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_142166277 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_142166277, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_928260154 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_928260154, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_942045447 = new java.util.HashSet<Object>();
	collection_942045447.add("/first");
	org.junit.Assert.assertEquals(collection_942045447, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1874900884 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1874900884, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1043748923 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1043748923, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_192039386 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_192039386, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_544871153 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_544871153, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2111133340 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2111133340, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_1762138646 = new java.util.HashSet<Object>();
	collection_1762138646.add("/first");
	org.junit.Assert.assertEquals(collection_1762138646, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1036347308 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1036347308, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1736612961 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1736612961, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1948922250 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1948922250, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1527362251 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1527362251, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1144337198 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1144337198, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_876839038 = new java.util.HashSet<Object>();
	collection_876839038.add("/first");
	org.junit.Assert.assertEquals(collection_876839038, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getAuth());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_155861769 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_155861769, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_109417422 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_109417422, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getConsumes());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        java.util.ArrayList collection_1309537637 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1309537637, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1311969232 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1311969232, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_255990391 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_255990391, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getHeaders());;
        // AssertGenerator add assertion
        java.util.HashSet collection_276979738 = new java.util.HashSet<Object>();
	collection_276979738.add("/first");
	org.junit.Assert.assertEquals(collection_276979738, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1970033029 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1970033029, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getPathparameters());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1220859698 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1220859698, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getProduces());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)first).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1339903325 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1339903325, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1991391086 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1991391086, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)first).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.ArrayList collection_37511614 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_37511614, ((org.jsondoc.core.pojo.ApiMethodDoc)first).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)first).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)first).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)first).getResponse());
        first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        second = new org.jsondoc.core.pojo.ApiMethodDoc();
        // MethodCallAdder
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_722293823 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_722293823, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_760588207 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_760588207, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_797106986 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_797106986, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2097961235 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2097961235, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_976840327 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_976840327, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_2131075628 = new java.util.HashSet<Object>();
	collection_2131075628.add("/second");
	org.junit.Assert.assertEquals(collection_2131075628, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2022540924 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2022540924, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_1945311271 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1945311271, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1901866632 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1901866632, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1274925896 = new java.util.HashSet<Object>();
	collection_1274925896.add("/second");
	org.junit.Assert.assertEquals(collection_1274925896, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1164083941 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1164083941, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1648962342 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1648962342, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_1302529611 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1302529611, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1557884271 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1557884271, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.ArrayList collection_173827161 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_173827161, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_389767971 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_389767971, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_235107515 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_235107515, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
        // AssertGenerator add assertion
        java.util.HashSet collection_2000068615 = new java.util.HashSet<Object>();
	collection_2000068615.add("/second");
	org.junit.Assert.assertEquals(collection_2000068615, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_2027595630 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2027595630, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        java.util.ArrayList collection_857456311 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_857456311, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_455642220 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_455642220, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.HashSet collection_1819826325 = new java.util.HashSet<Object>();
	collection_1819826325.add("/second");
	org.junit.Assert.assertEquals(collection_1819826325, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        java.util.LinkedHashSet collection_727797740 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_727797740, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_1406634667 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1406634667, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
        second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
        second.setPath(com.google.common.collect.Sets.newHashSet("/first"));
        second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.PUT, org.jsondoc.core.pojo.ApiVerb.POST));
        org.junit.Assert.assertNotEquals(0, first.compareTo(second));
    }

    /* amplification of org.jsondoc.core.doc.ApiMethodDocTest#testNotEqualMultipleVerbs */
    @org.junit.Test(timeout = 10000)
    public void testNotEqualMultipleVerbs_cf7131_failAssert2_cf7239_add8936() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            first = new org.jsondoc.core.pojo.ApiMethodDoc();
            first.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            first.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            second = new org.jsondoc.core.pojo.ApiMethodDoc();
            // MethodCallAdder
            second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSummary(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_842005882 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_842005882, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPathparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            java.util.ArrayList collection_118575947 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_118575947, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondochints());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponsestatuscode(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getModifiers(), 16409);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            java.util.ArrayList collection_165023978 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_165023978, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocerrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.JSONDoc.MethodDisplay");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_758180089 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_758180089, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getHeaders());;
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_150298436 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_150298436, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getProduces());;
            // AssertGenerator add assertion
            java.util.HashSet collection_486329818 = new java.util.HashSet<Object>();
	collection_486329818.add("/second");
	org.junit.Assert.assertEquals(collection_486329818, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayedMethodString());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getSimpleName(), "MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).name(), "URI");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_1502061367 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_1502061367, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getQueryparameters());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getResponse());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            java.util.ArrayList collection_563946806 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_563946806, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getJsondocwarnings());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getBodyobject());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_2081485955 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_2081485955, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getVerb());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            java.util.HashSet collection_1416821389 = new java.util.HashSet<Object>();
	collection_1416821389.add("/second");
	org.junit.Assert.assertEquals(collection_1416821389, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getPath());;
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.JSONDoc.MethodDisplay)((org.jsondoc.core.pojo.ApiMethodDoc)second).getDisplayMethodAs()).getDeclaringClass()).toGenericString(), "public static final enum org.jsondoc.core.pojo.JSONDoc$MethodDisplay");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiMethodDoc)second).getId());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            java.util.LinkedHashSet collection_915823834 = new java.util.LinkedHashSet<Object>();
	org.junit.Assert.assertEquals(collection_915823834, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getConsumes());;
            // AssertGenerator add assertion
            java.util.ArrayList collection_207000146 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_207000146, ((org.jsondoc.core.pojo.ApiMethodDoc)second).getApierrors());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiMethodDoc)second).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiMethodDoc)second).getStage()).getDeclaringClass()).isAnonymousClass());
            second.setPath(com.google.common.collect.Sets.newHashSet("/second"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.GET, org.jsondoc.core.pojo.ApiVerb.POST));
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            second.setPath(com.google.common.collect.Sets.newHashSet("/first"));
            second.setVerb(com.google.common.collect.Sets.newHashSet(org.jsondoc.core.pojo.ApiVerb.PUT, org.jsondoc.core.pojo.ApiVerb.POST));
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_3420 = (org.jsondoc.core.annotation.ApiMethod)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3420);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_3420);
            // StatementAdderMethod cloned existing statement
            vc_3420.description();
            // StatementAdderOnAssert create null value
            org.jsondoc.core.annotation.ApiMethod vc_3486 = (org.jsondoc.core.annotation.ApiMethod)null;
            // StatementAdderMethod cloned existing statement
            vc_3486.summary();
            org.junit.Assert.assertNotEquals(0, first.compareTo(second));
            org.junit.Assert.fail("testNotEqualMultipleVerbs_cf7131 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}


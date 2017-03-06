

package org.jsondoc.core.doc;


public class AmplApiFlowDocTest {
    private org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.flow.ApiFlowSet
    private class TestFlow {
        @org.jsondoc.core.annotation.flow.ApiFlow(name = "flow", description = "A test flow", steps = { @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F1")
         , @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F2")
         , @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F3") }, group = "Flows A")
        public void flow() {
        }

        @org.jsondoc.core.annotation.flow.ApiFlow(name = "flow2", description = "A test flow 2", steps = { @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F4")
         , @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F5")
         , @org.jsondoc.core.annotation.flow.ApiFlowStep(apimethodid = "F6") }, group = "Flows B")
        public void flow2() {
        }
    }

    @org.junit.Test
    public void testApiDoc() {
        java.util.Set<java.lang.Class<?>> classes = new java.util.HashSet<java.lang.Class<?>>();
        classes.add(org.jsondoc.core.doc.AmplApiFlowDocTest.TestFlow.class);
        java.util.List<org.jsondoc.core.pojo.ApiMethodDoc> apiMethodDocs = new java.util.ArrayList<org.jsondoc.core.pojo.ApiMethodDoc>();
        org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = new org.jsondoc.core.pojo.ApiMethodDoc();
        apiMethodDoc.setId("F1");
        apiMethodDocs.add(apiMethodDoc);
        java.util.Set<org.jsondoc.core.pojo.flow.ApiFlowDoc> apiFlowDocs = jsondocScanner.getApiFlowDocs(classes, apiMethodDocs);
        for (org.jsondoc.core.pojo.flow.ApiFlowDoc apiFlowDoc : apiFlowDocs) {
            if (apiFlowDoc.getName().equals("flow")) {
                org.junit.Assert.assertEquals("A test flow", apiFlowDoc.getDescription());
                org.junit.Assert.assertEquals(3, apiFlowDoc.getSteps().size());
                org.junit.Assert.assertEquals("F1", apiFlowDoc.getSteps().get(0).getApimethodid());
                org.junit.Assert.assertEquals("F2", apiFlowDoc.getSteps().get(1).getApimethodid());
                org.junit.Assert.assertEquals("Flows A", apiFlowDoc.getGroup());
                org.junit.Assert.assertNotNull(apiFlowDoc.getSteps().get(0).getApimethoddoc());
                org.junit.Assert.assertEquals("F1", apiFlowDoc.getSteps().get(0).getApimethoddoc().getId());
            }
            if (apiFlowDoc.getName().equals("flow2")) {
                org.junit.Assert.assertEquals("A test flow 2", apiFlowDoc.getDescription());
                org.junit.Assert.assertEquals(3, apiFlowDoc.getSteps().size());
                org.junit.Assert.assertEquals("F4", apiFlowDoc.getSteps().get(0).getApimethodid());
                org.junit.Assert.assertEquals("F5", apiFlowDoc.getSteps().get(1).getApimethodid());
                org.junit.Assert.assertEquals("Flows B", apiFlowDoc.getGroup());
            }
        }
    }
}


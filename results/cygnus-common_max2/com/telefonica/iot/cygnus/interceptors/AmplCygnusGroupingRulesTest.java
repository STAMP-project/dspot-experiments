/**
 * Copyright 2015-2017 Telefonica Investigaci?n y Desarrollo, S.A.U
 *
 * This file is part of fiware-cygnus (FIWARE project).
 *
 * fiware-cygnus is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * fiware-cygnus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License along with fiware-cygnus. If not, see
 * http://www.gnu.org/licenses/.
 *
 * For those usages not covered by the GNU Affero General Public License please contact with iot_support at tid dot es
 */
/**
 * CygnusGroupingRulesTest
 */


package com.telefonica.iot.cygnus.interceptors;


/**
 * @author frb
 */
public class AmplCygnusGroupingRulesTest {
    @org.junit.Rule
    public org.junit.rules.TemporaryFolder folder = new org.junit.rules.TemporaryFolder();

    /**
     * Constructor.
     */
    public AmplCygnusGroupingRulesTest() {
        org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.FATAL);
    }

    // CygnusGroupingRulesTest
    /**
     * [GroupingRules.constructor] -------- Unexistent/unreadable grouping rules file is detected.
     */
    @org.junit.Test
    public void testConstructorUnexistentFile() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-------- Unexistent/unreadable grouping rules file is detected"));
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules("/a/b/c/unexistent.txt");
        try {
            org.junit.Assert.assertTrue(cygnusGroupingRules.getRules().isEmpty());
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-  OK  - An unexistent/unreadble file '/a/b/c/unexistent.txt' has been detected"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - An unexistent/unreadble file '/a/b/c/unexistent.txt' has not been detected"));
            throw e;
        }// try catch
        
    }

    // testConstructorUnexistentFile
    /**
     * [GroupingRules.constructor] -------- Missing or empty fields in all the grouping rules are detected.
     */
    @org.junit.Test
    public void testConstructorAllRulesWithMissingOrEmptyFields() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-------- Missing or empty fields in all the grouping rules are detected"));
        java.lang.String groupingRulesStr = "{\"grouping_rules\":[{\"fields\":[],\"destination\":\"\"}]}";
        java.io.File file;
        try {
            file = folder.newFile("grouping_rules.conf");
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            out.println(groupingRulesStr);
            out.flush();
            out.close();
        } catch (java.io.IOException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - There was some problem when mocking the grouping rules file"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules(file.getAbsolutePath());
        try {
            org.junit.Assert.assertTrue(cygnusGroupingRules.getRules().isEmpty());
            java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-  OK  - Missing or empty fields in all the grouping rules '") + groupingRulesStr) + "' have been detected"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - Missing or empty fields in all the grouping rules '") + groupingRulesStr) + "'have not been detected"));
            throw e;
        }// try catch
        
    }

    // testConstructorAllRulesWithMissingOrEmptyFields
    /**
     * [GroupingRules.constructor] -------- Syntax errors in the grouping rules are detected.
     */
    @org.junit.Test
    public void testConstructorSyntaxErrorsInRules() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-------- Syntax errors in the grouping rules are detected"));
        java.lang.String groupingRulesStr = "{\"grouping_rules\":[}";
        java.io.File file;
        try {
            file = folder.newFile("grouping_rules.conf");
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            out.println(groupingRulesStr);
            out.flush();
            out.close();
        } catch (java.io.IOException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - There was some problem when mocking the grouping rules file"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules(file.getAbsolutePath());
        try {
            org.junit.Assert.assertTrue(cygnusGroupingRules.getRules().isEmpty());
            java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-  OK  - Syntax errors in the grouping rules '") + groupingRulesStr) + "' have been detected"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - Syntax errors in the grouping rules '") + groupingRulesStr) + "'have not been detected"));
            throw e;
        }// try catch
        
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    @org.junit.Test
    public void testGetMatchingRuleServicePath() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        ruleFields.add("servicePath");
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        ruleFields.add("servicePath");
        ruleFields.add("entityId");
        java.lang.String ruleRegex = "/someServicePathsomeId";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        ruleFields.add("servicePath");
        ruleFields.add("entityId");
        ruleFields.add("entityType");
        java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePathEntityIdEntityType
    /**
     * [GroupingRules.addRule] -------- Adding a new rule works.
     */
    @org.junit.Test
    public void testAddRule() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- Adding a new rule works"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        ruleFields1.add("servicePath");
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        ruleFields2.add("servicePath");
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 2));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - New rule has been added"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - New rule has not been added"));
            throw e;
        }// try catch
        
    }

    // testAddRule
    /**
     * [GroupingRules.addRule] -------- When a rule is added, the index increases by 1.
     */
    @org.junit.Test
    public void testAddRuleIndexIncreased() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- When a rule is added, the index increases by 1"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        ruleFields1.add("servicePath");
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        ruleFields2.add("servicePath");
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        long prevLastIndex = cygnusGroupingRules.getLastIndex();
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        long currLastIndex = cygnusGroupingRules.getLastIndex();
        try {
            org.junit.Assert.assertTrue((currLastIndex == (prevLastIndex + 1)));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - Last index before adding a rule was '") + prevLastIndex) + "', now it is '") + currLastIndex) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - Last index before adding a rule was '") + prevLastIndex) + "', now it is not '") + (prevLastIndex + 1)) + "'"));
            throw e;
        }// try catch
        
    }

    // testUpdateRule
    private org.json.simple.JSONObject createJsonRule(java.util.ArrayList<java.lang.String> fields, java.lang.String regex, java.lang.String destination, java.lang.String fiwareServicePath) {
        org.json.simple.JSONObject jsonRule = new org.json.simple.JSONObject();
        jsonRule.put("id", ((long) (1)));
        org.json.simple.JSONArray jsonArray = new org.json.simple.JSONArray();
        for (java.lang.String field : fields) {
            jsonArray.add(field);
        }// for
        
        jsonRule.put("fields", jsonArray);
        jsonRule.put("regex", regex);
        jsonRule.put("fiware_service_path", fiwareServicePath);
        jsonRule.put("destination", destination);
        return jsonRule;
    }

    // createJsonRule
    private org.json.simple.JSONObject createJsonRules(org.json.simple.JSONObject jsonRule) {
        org.json.simple.JSONArray rulesArray = new org.json.simple.JSONArray();
        rulesArray.add(jsonRule);
        org.json.simple.JSONObject jsonRules = new org.json.simple.JSONObject();
        jsonRules.put("grouping_rules", rulesArray);
        return jsonRules;
    }

    // createJsonRules
    // createSingleRuleGroupingRules
    private com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules createSingleRuleGroupingRules(java.util.ArrayList<java.lang.String> fields, java.lang.String regex, java.lang.String destination, java.lang.String fiwareServicePath, java.lang.String method) {
        org.json.simple.JSONObject jsonRule = createJsonRule(fields, regex, destination, fiwareServicePath);
        org.json.simple.JSONObject jsonRules = createJsonRules(jsonRule);
        java.lang.String groupingRulesStr = jsonRules.toJSONString().replaceAll("\\\\", "");
        java.io.File file;
        try {
            file = folder.newFile("grouping_rules.conf");
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            out.println(groupingRulesStr);
            out.flush();
            out.close();
        } catch (java.io.IOException e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead((("[CygnusGroupingRules." + method) + "]"))) + "- FAIL - There was some problem when mocking the grouping rules file"));
            throw new java.lang.AssertionError(e.getMessage());
        }// try catch
        
        return new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules(file.getAbsolutePath());
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    @org.junit.Test
    public void testUpdateRule() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        // AssertGenerator replace invocation
        boolean o_testUpdateRule__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule__20);
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
        try {
            org.junit.Assert.assertTrue(updatedRule.getFields().get(0).equals("servicePath"));
            org.junit.Assert.assertTrue(updatedRule.getRegex().equals(ruleRegex2));
            org.junit.Assert.assertTrue(updatedRule.getNewFiwareServicePath().equals(ruleServicePath2));
            org.junit.Assert.assertTrue(updatedRule.getDestination().equals(ruleDestination2));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    @org.junit.Test
    public void testDeleteRule() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePathEntityIdEntityType
    /**
     * [GroupingRules.addRule] -------- Adding a new rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testAddRule */
    @org.junit.Test(timeout = 10000)
    public void testAddRule_cf76() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- Adding a new rule works"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRule_cf76__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRule_cf76__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRule_cf76__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRule_cf76__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        try {
            // AssertGenerator replace invocation
            java.lang.String o_testAddRule_cf76__23 = // StatementAdderMethod cloned existing statement
cygnusGroupingRule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testAddRule_cf76__23, "{\"regex\":\"\\/someServicePath2\",\"fiware_service_path\":\"\\/new_svc_path2\",\"destination\":\"new_dest2\",\"id\":2,\"fields\":[\"servicePath\"]}");
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 2));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - New rule has been added"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - New rule has not been added"));
            throw e;
        }// try catch
        
    }

    // testAddRule
    /**
     * [GroupingRules.addRule] -------- When a rule is added, the index increases by 1.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testAddRuleIndexIncreased */
    @org.junit.Test
    public void testAddRuleIndexIncreased_literalMutation9737() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- When a rule is added, the index increases by 1"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_literalMutation9737__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_literalMutation9737__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_literalMutation9737__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_literalMutation9737__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        long prevLastIndex = cygnusGroupingRules.getLastIndex();
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        long currLastIndex = cygnusGroupingRules.getLastIndex();
        try {
            org.junit.Assert.assertTrue((currLastIndex == (prevLastIndex + 1)));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - Last index before adding a rule was '") + prevLastIndex) + "', now it is '") + currLastIndex) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - Last index before adding a rule was '") + prevLastIndex) + "', now it is not '") + (prevLastIndex + 1)) + "'"));
            throw e;
        }// try catch
        
    }

    // testAddRule
    /**
     * [GroupingRules.addRule] -------- When a rule is added, the index increases by 1.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testAddRuleIndexIncreased */
    @org.junit.Test
    public void testAddRuleIndexIncreased_literalMutation9743() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- When a rule is added, the index increases by 1"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_literalMutation9743__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_literalMutation9743__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "vn3lWGlIO$6!Sl";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_literalMutation9743__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_literalMutation9743__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        long prevLastIndex = cygnusGroupingRules.getLastIndex();
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        long currLastIndex = cygnusGroupingRules.getLastIndex();
        try {
            org.junit.Assert.assertTrue((currLastIndex == (prevLastIndex + 1)));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - Last index before adding a rule was '") + prevLastIndex) + "', now it is '") + currLastIndex) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - Last index before adding a rule was '") + prevLastIndex) + "', now it is not '") + (prevLastIndex + 1)) + "'"));
            throw e;
        }// try catch
        
    }

    // testAddRule
    /**
     * [GroupingRules.addRule] -------- When a rule is added, the index increases by 1.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testAddRuleIndexIncreased */
    @org.junit.Test(timeout = 10000)
    public void testAddRuleIndexIncreased_cf9812() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-------- When a rule is added, the index increases by 1"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_cf9812__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_cf9812__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testAddRuleIndexIncreased_cf9812__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testAddRuleIndexIncreased_cf9812__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        long prevLastIndex = cygnusGroupingRules.getLastIndex();
        cygnusGroupingRules.addRule(cygnusGroupingRule);
        long currLastIndex = cygnusGroupingRules.getLastIndex();
        try {
            // AssertGenerator replace invocation
            java.lang.String o_testAddRuleIndexIncreased_cf9812__27 = // StatementAdderMethod cloned existing statement
cygnusGroupingRule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testAddRuleIndexIncreased_cf9812__27, "{\"regex\":\"\\/someServicePath2\",\"fiware_service_path\":\"\\/new_svc_path2\",\"destination\":\"new_dest2\",\"id\":2,\"fields\":[\"servicePath\"]}");
            org.junit.Assert.assertTrue((currLastIndex == (prevLastIndex + 1)));
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "-  OK  - Last index before adding a rule was '") + prevLastIndex) + "', now it is '") + currLastIndex) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.addRule]")) + "- FAIL - Last index before adding a rule was '") + prevLastIndex) + "', now it is not '") + (prevLastIndex + 1)) + "'"));
            throw e;
        }// try catch
        
    }

    // testConstructorUnexistentFile
    /**
     * [GroupingRules.constructor] -------- Missing or empty fields in all the grouping rules are detected.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testConstructorAllRulesWithMissingOrEmptyFields */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testConstructorAllRulesWithMissingOrEmptyFields_cf21379 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorAllRulesWithMissingOrEmptyFields_cf21379_cf21520_failAssert1_literalMutation23179() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-------- Missing or empty fields in all the grouping rules are detected"));
            java.lang.String groupingRulesStr = "{\"grouping_rules\":[{\"fields\":[],\"destination\":\"\"}]}";
            java.io.File file;
            try {
                file = folder.newFile("grouping_rules.conf");
                java.io.PrintWriter out = new java.io.PrintWriter(file);
                out.println(groupingRulesStr);
                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - There was some problem when mocking the grouping rules file"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules(file.getAbsolutePath());
            try {
                // MethodAssertGenerator build local variable
                Object o_34_1 = 1;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_1, 1);
                // StatementAdderOnAssert create random local variable
                boolean vc_3502 = true;
                // StatementAdderOnAssert create random local variable
                org.json.simple.JSONObject vc_3501 = new org.json.simple.JSONObject();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_3498 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                // AssertGenerator replace invocation
                int o_testConstructorAllRulesWithMissingOrEmptyFields_cf21379__32 = // StatementAdderMethod cloned existing statement
vc_3498.isValid(vc_3501, vc_3502);
                // MethodAssertGenerator build local variable
                Object o_34_0 = o_testConstructorAllRulesWithMissingOrEmptyFields_cf21379__32;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_0, 1);
                // StatementAddOnAssert local variable replacement
                long prevLastIndex = cygnusGroupingRules.getLastIndex();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(prevLastIndex, 0L);
                // StatementAdderMethod cloned existing statement
                vc_3498.setId(prevLastIndex);
                // MethodAssertGenerator build local variable
                Object o_41_0 = cygnusGroupingRules.getRules().isEmpty();
                java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "") + groupingRulesStr) + "' have been detected"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - Missing or empty fields in all the grouping rules '") + groupingRulesStr) + "'have not been detected"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testConstructorAllRulesWithMissingOrEmptyFields_cf21379_cf21520 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorAllRulesWithMissingOrEmptyFields
    /**
     * [GroupingRules.constructor] -------- Syntax errors in the grouping rules are detected.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testConstructorSyntaxErrorsInRules */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testConstructorSyntaxErrorsInRules_cf23594 */
    @org.junit.Test(timeout = 10000)
    public void testConstructorSyntaxErrorsInRules_cf23594_cf23735_failAssert9_literalMutation25566() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-------- Syntax errors in the grouping rules are detected"));
            java.lang.String groupingRulesStr = "{\"grouping_rules\":[}";
            java.io.File file;
            try {
                file = folder.newFile("grouping_rules.conf");
                java.io.PrintWriter out = new java.io.PrintWriter(file);
                out.println(groupingRulesStr);
                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - There was some problem when mocking the grouping rules file"));
                throw new java.lang.AssertionError(e.getMessage());
            }// try catch
            
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules(file.getAbsolutePath());
            try {
                // MethodAssertGenerator build local variable
                Object o_34_1 = 1;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_1, 1);
                // StatementAdderOnAssert create random local variable
                boolean vc_3898 = true;
                // StatementAdderOnAssert create random local variable
                org.json.simple.JSONObject vc_3897 = new org.json.simple.JSONObject();
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_3894 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                // AssertGenerator replace invocation
                int o_testConstructorSyntaxErrorsInRules_cf23594__32 = // StatementAdderMethod cloned existing statement
vc_3894.isValid(vc_3897, vc_3898);
                // MethodAssertGenerator build local variable
                Object o_34_0 = o_testConstructorSyntaxErrorsInRules_cf23594__32;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_34_0, 1);
                // StatementAddOnAssert local variable replacement
                long currLastIndex = cygnusGroupingRules.getLastIndex();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(currLastIndex, 0L);
                // StatementAdderMethod cloned existing statement
                vc_3894.setId(currLastIndex);
                // MethodAssertGenerator build local variable
                Object o_41_0 = cygnusGroupingRules.getRules().isEmpty();
                java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "-  OK  - Syntax errors in the grouping rules '") + groupingRulesStr) + "\' have been etected"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.constructor]")) + "- FAIL - Syntax errors in the grouping rules '") + groupingRulesStr) + "'have not been detected"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testConstructorSyntaxErrorsInRules_cf23594_cf23735 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    @org.junit.Test
    public void testDeleteRule_literalMutation26822() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rue works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26822__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26822__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26822__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26822__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    @org.junit.Test
    public void testDeleteRule_literalMutation26832() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26832__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26832__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26832__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDeleteRule_literalMutation26832__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    @org.junit.Test
    public void testDeleteRule_literalMutation26837() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26837__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26837__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26837__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDeleteRule_literalMutation26837__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    @org.junit.Test
    public void testDeleteRule_literalMutation26839() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26839__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26839__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "F=bJ+36jx29MC";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26839__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDeleteRule_literalMutation26839__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    @org.junit.Test
    public void testDeleteRule_literalMutation26829() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26829__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testDeleteRule_literalMutation26829__5);
        java.lang.String ruleRegex = "";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
        // AssertGenerator replace invocation
        boolean o_testDeleteRule_literalMutation26829__11 = cygnusGroupingRules.deleteRule(1);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_testDeleteRule_literalMutation26829__11);
        try {
            org.junit.Assert.assertTrue(((cygnusGroupingRules.getRules().size()) == 0));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
            throw e;
        }// try catch
        
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule_cf26867 */
    @org.junit.Test(timeout = 10000)
    public void testDeleteRule_cf26867_failAssert5_literalMutation29616() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(ruleDestination, "");
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
            cygnusGroupingRules.deleteRule(1);
            try {
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_4321 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                // StatementAdderMethod cloned existing statement
                vc_4321.getRegex();
                // MethodAssertGenerator build local variable
                Object o_18_0 = (cygnusGroupingRules.getRules().size()) == 0;
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testDeleteRule_cf26867 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule_cf26885 */
    @org.junit.Test(timeout = 10000)
    public void testDeleteRule_cf26885_failAssert14_literalMutation29978() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "3S(Dc]T$w e+,";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(ruleServicePath, "3S(Dc]T$w e+,");
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
            cygnusGroupingRules.deleteRule(1);
            try {
                // StatementAdderOnAssert create random local variable
                long vc_4333 = -6339487057837254996L;
                // StatementAddOnAssert local variable replacement
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
                // StatementAdderMethod cloned existing statement
                updatedRule.setId(vc_4333);
                // MethodAssertGenerator build local variable
                Object o_22_0 = (cygnusGroupingRules.getRules().size()) == 0;
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testDeleteRule_cf26885 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testAddRuleIndexIncreased
    /**
     * [GroupingRules.deleteRule] -------- Deleting an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testDeleteRule_cf26864 */
    @org.junit.Test(timeout = 10000)
    public void testDeleteRule_cf26864_failAssert4_add29562() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-------- Deleting an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "deleteRule");
            // AssertGenerator replace invocation
            boolean o_testDeleteRule_cf26864_failAssert4_add29562__13 = // MethodCallAdder
cygnusGroupingRules.deleteRule(1);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_testDeleteRule_cf26864_failAssert4_add29562__13);
            cygnusGroupingRules.deleteRule(1);
            try {
                // StatementAdderOnAssert create null value
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_4319 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                // StatementAdderMethod cloned existing statement
                vc_4319.getNewFiwareServicePath();
                // MethodAssertGenerator build local variable
                Object o_18_0 = (cygnusGroupingRules.getRules().size()) == 0;
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "-  OK  - Rule with ID 1 has been deleted"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.deleteRule]")) + "- FAIL - Rule with ID 1 has not been deleted"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testDeleteRule_cf26864 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32160_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_18_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32160 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    @org.junit.Test(timeout = 10000)
    public void testGetMatchingRuleServicePath_cf32243() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePath_cf32243__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePath_cf32243__5);
        java.lang.String ruleRegex = "/someServicePath";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            // AssertGenerator replace invocation
            java.lang.String o_testGetMatchingRuleServicePath_cf32243__20 = // StatementAdderMethod cloned existing statement
rule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMatchingRuleServicePath_cf32243__20, "{\"regex\":\"\\/someServicePath\",\"fiware_service_path\":\"\\/new_svc_path\",\"destination\":\"new_dest\",\"id\":1,\"fields\":[\"servicePath\"]}");
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
            throw e;
        }// try catch
        
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32172_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "&QIX;=2vQk{ZX";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_18_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32172 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32165_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_18_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32165 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32161_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            java.lang.String ruleRegex = "/somepervicePath";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_18_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32161 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath_literalMutation32213 */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32213_literalMutation37776_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePath_literalMutation32213__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePath_literalMutation32213__5;
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "iB-m[)j(d]tu9Okp";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_23_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "#"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32213_literalMutation37776 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath_literalMutation32155 */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32155_literalMutation32648_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path baseP matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePath_literalMutation32155__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePath_literalMutation32155__5;
            java.lang.String ruleRegex = "";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_23_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32155_literalMutation32648 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testConstructorSyntaxErrorsInRules
    /**
     * [GroupingRules.getMatchingRule] -------- Service path based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePath_literalMutation32203 */
    @org.junit.Test
    public void testGetMatchingRuleServicePath_literalMutation32203_literalMutation36625_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePath_literalMutation32203__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePath_literalMutation32203__5;
            java.lang.String ruleRegex = "/someServicePath";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_23_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rulesW \'") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePath_literalMutation32203_literalMutation36625 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    @org.junit.Test(timeout = 10000)
    public void testGetMatchingRuleServicePathEntityId_cf47913() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePathEntityId_cf47913__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePathEntityId_cf47913__5);
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePathEntityId_cf47913__6 = ruleFields.add("entityId");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePathEntityId_cf47913__6);
        java.lang.String ruleRegex = "/someServicePathsomeId";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            // StatementAddOnAssert local variable replacement
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            // AssertGenerator replace invocation
            java.lang.String o_testGetMatchingRuleServicePathEntityId_cf47913__25 = // StatementAdderMethod cloned existing statement
updatedRule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMatchingRuleServicePathEntityId_cf47913__25, "{\"regex\":\"\\/someServicePathsomeId\",\"fiware_service_path\":\"\\/new_svc_path\",\"destination\":\"new_dest\",\"id\":1,\"fields\":[\"servicePath\",\"entityId\"]}");
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47832_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "5mZ[@pPyyi:t#";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_19_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47832 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47838_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "NZIRt8N[4(+&|?;7";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_19_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47838 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47819_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            java.lang.String ruleRegex = "";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_19_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47819 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47825_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_19_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_21_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47825 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId_literalMutation47858 */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47858_literalMutation51593_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_literalMutation47858__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePathEntityId_literalMutation47858__5;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_literalMutation47858__6 = ruleFields.add("entityId");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetMatchingRuleServicePathEntityId_literalMutation47858__6;
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "soJeId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_25_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_27_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "\' match ser,ice path \'") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47858_literalMutation51593 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId_literalMutation47868 */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityId_literalMutation47868_literalMutation52808_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_literalMutation47868__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePathEntityId_literalMutation47868__5;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_literalMutation47868__6 = ruleFields.add("entityId");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetMatchingRuleServicePathEntityId_literalMutation47868__6;
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_25_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_27_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Mat,hing rules \'") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_literalMutation47868_literalMutation52808 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePath
    /**
     * [GroupingRules.getMatchingRule] -------- Service path and entity ID based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityId_cf47920 */
    @org.junit.Test(timeout = 10000)
    public void testGetMatchingRuleServicePathEntityId_cf47920_literalMutation56482_failAssert24() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path and entity ID based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_cf47920__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePathEntityId_cf47920__5;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityId_cf47920__6 = ruleFields.add("entityId");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetMatchingRuleServicePathEntityId_cf47920__6;
            java.lang.String ruleRegex = "/someServicePathsomeId";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_31_1 = 0;
                // MethodAssertGenerator build local variable
                Object o_25_0 = rule.getDestination();
                // AssertGenerator replace invocation
                java.util.regex.Pattern o_testGetMatchingRuleServicePathEntityId_cf47920__21 = // StatementAdderMethod cloned existing statement
rule.getPattern();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.util.regex.Pattern)o_testGetMatchingRuleServicePathEntityId_cf47920__21).pattern();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((java.util.regex.Pattern)o_testGetMatchingRuleServicePathEntityId_cf47920__21).flags();
                // MethodAssertGenerator build local variable
                Object o_33_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "' and entity ID '") + entityId) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityId_cf47920_literalMutation56482 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66199_failAssert8() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            ruleFields.add("entityType");
            java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_22_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66199 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66197_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            ruleFields.add("entityType");
            java.lang.String ruleRegex = "/4someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_22_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66197 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    @org.junit.Test(timeout = 10000)
    public void testGetMatchingRuleServicePathEntityIdEntityType_cf66296() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
        java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__5 = ruleFields.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__5);
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__6 = ruleFields.add("entityId");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__6);
        // AssertGenerator replace invocation
        boolean o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__7 = ruleFields.add("entityType");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__7);
        java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
        java.lang.String ruleDestination = "new_dest";
        java.lang.String ruleServicePath = "/new_svc_path";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
        java.lang.String servicePath = "/someServicePath";
        java.lang.String entityId = "someId";
        java.lang.String entityType = "someType";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
        try {
            org.junit.Assert.assertEquals(ruleDestination, rule.getDestination());
            // AssertGenerator replace invocation
            java.lang.String o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__22 = // StatementAdderMethod cloned existing statement
rule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testGetMatchingRuleServicePathEntityIdEntityType_cf66296__22, "{\"regex\":\"\\/someServicePathsomeIdsomeType\",\"fiware_service_path\":\"\\/new_svc_path\",\"destination\":\"new_dest\",\"id\":1,\"fields\":[\"servicePath\",\"entityId\",\"entityType\"]}");
            org.junit.Assert.assertEquals(ruleServicePath, rule.getNewFiwareServicePath());
            java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
            throw e;
        }// try catch
        
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66207_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            ruleFields.add("entityType");
            java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "^tTo*`)1ef{43";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_22_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66207 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248 */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248_literalMutation71726_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__5;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__6 = ruleFields.add("entityId");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__6;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__7 = ruleFields.add("entityType");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248__7;
            java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_29_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_31_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAL - Matching rules \'") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66248_literalMutation71726 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223 */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223_literalMutation68397_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__5 = ruleFields.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__5;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__6 = ruleFields.add("entityId");
            // MethodAssertGenerator build local variable
            Object o_11_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__6;
            // AssertGenerator replace invocation
            boolean o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__7 = ruleFields.add("entityType");
            // MethodAssertGenerator build local variable
            Object o_15_0 = o_testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223__7;
            java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePath";
            java.lang.String entityId = "someId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_29_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_31_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66223_literalMutation68397 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testGetMatchingRuleServicePathEntityId
    /**
     * [GroupingRules.getMatchingRule] -------- Service path, entity ID and entity type based matching rules match.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66214 */
    @org.junit.Test
    public void testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66214_failAssert17_literalMutation78200() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-------- Service path, entity ID and entity type based matching rules match"));
            java.util.ArrayList<java.lang.String> ruleFields = new java.util.ArrayList<java.lang.String>();
            ruleFields.add("servicePath");
            ruleFields.add("entityId");
            ruleFields.add("entityType");
            java.lang.String ruleRegex = "/someServicePathsomeIdsomeType";
            java.lang.String ruleDestination = "new_dest";
            java.lang.String ruleServicePath = "/new_svc_path";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields, ruleRegex, ruleDestination, ruleServicePath, "getMatchingRule");
            java.lang.String servicePath = "/someServicePah";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(servicePath, "/someServicePah");
            java.lang.String entityId = "smeId";
            java.lang.String entityType = "someType";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule rule = cygnusGroupingRules.getMatchingRule(servicePath, entityId, entityType);
            try {
                // MethodAssertGenerator build local variable
                Object o_20_0 = rule.getDestination();
                // MethodAssertGenerator build local variable
                Object o_22_0 = rule.getNewFiwareServicePath();
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "-  OK  - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' match service path '") + servicePath) + "', entity ID '") + entityId) + "' and entity type '") + entityType) + "'"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((((((((((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.getMatchingRule]")) + "- FAIL - Matching rules '") + (cygnusGroupingRules.toString(true))) + "' don't match service path '") + servicePath) + "' nor entity ID '") + entityId) + "' nor entity type '") + entityType) + "'"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testGetMatchingRuleServicePathEntityIdEntityType_literalMutation66214 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    @org.junit.Test(timeout = 10000)
    public void testUpdateRule_cf88506() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88506__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88506__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88506__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88506__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88506__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88506__20);
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
        try {
            org.junit.Assert.assertTrue(updatedRule.getFields().get(0).equals("servicePath"));
            org.junit.Assert.assertTrue(updatedRule.getRegex().equals(ruleRegex2));
            org.junit.Assert.assertTrue(updatedRule.getNewFiwareServicePath().equals(ruleServicePath2));
            // AssertGenerator replace invocation
            java.lang.String o_testUpdateRule_cf88506__36 = // StatementAdderMethod cloned existing statement
cygnusGroupingRule.getNewFiwareServicePath();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testUpdateRule_cf88506__36, "/new_svc_path2");
            org.junit.Assert.assertTrue(updatedRule.getDestination().equals(ruleDestination2));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
            throw e;
        }// try catch
        
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    @org.junit.Test
    public void testUpdateRule_literalMutation88458_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
            ruleFields1.add("servicePath");
            java.lang.String ruleRegex1 = "/someServicePath1";
            java.lang.String ruleDestination1 = "";
            java.lang.String ruleServicePath1 = "/new_svc_path1";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
            java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
            ruleFields2.add("servicePath");
            java.lang.String ruleRegex2 = "/someServicePath2";
            java.lang.String ruleDestination2 = "new_dest2";
            java.lang.String ruleServicePath2 = "/new_svc_path2";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
            cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            try {
                // MethodAssertGenerator build local variable
                Object o_26_0 = updatedRule.getFields().get(0).equals("servicePath");
                // MethodAssertGenerator build local variable
                Object o_30_0 = updatedRule.getRegex().equals(ruleRegex2);
                // MethodAssertGenerator build local variable
                Object o_33_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                // MethodAssertGenerator build local variable
                Object o_36_0 = updatedRule.getDestination().equals(ruleDestination2);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testUpdateRule_literalMutation88458 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    @org.junit.Test(timeout = 10000)
    public void testUpdateRule_cf88514() {
        java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
        java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88514__5 = ruleFields1.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88514__5);
        java.lang.String ruleRegex1 = "/someServicePath1";
        java.lang.String ruleDestination1 = "new_dest1";
        java.lang.String ruleServicePath1 = "/new_svc_path1";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
        java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88514__13 = ruleFields2.add("servicePath");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88514__13);
        java.lang.String ruleRegex2 = "/someServicePath2";
        java.lang.String ruleDestination2 = "new_dest2";
        java.lang.String ruleServicePath2 = "/new_svc_path2";
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
        // AssertGenerator replace invocation
        boolean o_testUpdateRule_cf88514__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testUpdateRule_cf88514__20);
        com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
        try {
            org.junit.Assert.assertTrue(updatedRule.getFields().get(0).equals("servicePath"));
            org.junit.Assert.assertTrue(updatedRule.getRegex().equals(ruleRegex2));
            org.junit.Assert.assertTrue(updatedRule.getNewFiwareServicePath().equals(ruleServicePath2));
            // AssertGenerator replace invocation
            java.lang.String o_testUpdateRule_cf88514__36 = // StatementAdderMethod cloned existing statement
updatedRule.toString();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testUpdateRule_cf88514__36, "{\"regex\":\"\\/someServicePath2\",\"fiware_service_path\":\"\\/new_svc_path2\",\"destination\":\"new_dest2\",\"id\":1,\"fields\":[\"servicePath\"]}");
            org.junit.Assert.assertTrue(updatedRule.getDestination().equals(ruleDestination2));
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
        } catch (java.lang.AssertionError e) {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
            throw e;
        }// try catch
        
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    @org.junit.Test
    public void testUpdateRule_literalMutation88467_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
            ruleFields1.add("servicePath");
            java.lang.String ruleRegex1 = "/someServicePath1";
            java.lang.String ruleDestination1 = "new_dest1";
            java.lang.String ruleServicePath1 = "NV?ll5!n,jT2WI";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
            java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
            ruleFields2.add("servicePath");
            java.lang.String ruleRegex2 = "/someServicePath2";
            java.lang.String ruleDestination2 = "new_dest2";
            java.lang.String ruleServicePath2 = "/new_svc_path2";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
            cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            try {
                // MethodAssertGenerator build local variable
                Object o_26_0 = updatedRule.getFields().get(0).equals("servicePath");
                // MethodAssertGenerator build local variable
                Object o_30_0 = updatedRule.getRegex().equals(ruleRegex2);
                // MethodAssertGenerator build local variable
                Object o_33_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                // MethodAssertGenerator build local variable
                Object o_36_0 = updatedRule.getDestination().equals(ruleDestination2);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testUpdateRule_literalMutation88467 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    @org.junit.Test
    public void testUpdateRule_literalMutation88453_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
            ruleFields1.add("servicePath");
            java.lang.String ruleRegex1 = "";
            java.lang.String ruleDestination1 = "new_dest1";
            java.lang.String ruleServicePath1 = "/new_svc_path1";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
            java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
            ruleFields2.add("servicePath");
            java.lang.String ruleRegex2 = "/someServicePath2";
            java.lang.String ruleDestination2 = "new_dest2";
            java.lang.String ruleServicePath2 = "/new_svc_path2";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
            cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            try {
                // MethodAssertGenerator build local variable
                Object o_26_0 = updatedRule.getFields().get(0).equals("servicePath");
                // MethodAssertGenerator build local variable
                Object o_30_0 = updatedRule.getRegex().equals(ruleRegex2);
                // MethodAssertGenerator build local variable
                Object o_33_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                // MethodAssertGenerator build local variable
                Object o_36_0 = updatedRule.getDestination().equals(ruleDestination2);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testUpdateRule_literalMutation88453 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule_literalMutation88459 */
    @org.junit.Test
    public void testUpdateRule_literalMutation88459_literalMutation89848_failAssert22() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88459__5 = ruleFields1.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testUpdateRule_literalMutation88459__5;
            java.lang.String ruleRegex1 = "";
            java.lang.String ruleDestination1 = "n|w_dest1";
            java.lang.String ruleServicePath1 = "/new_svc_path1";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
            java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88459__13 = ruleFields2.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_18_0 = o_testUpdateRule_literalMutation88459__13;
            java.lang.String ruleRegex2 = "/someServicePath2";
            java.lang.String ruleDestination2 = "new_dest2";
            java.lang.String ruleServicePath2 = "/new_svc_path2";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88459__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
            // MethodAssertGenerator build local variable
            Object o_28_0 = o_testUpdateRule_literalMutation88459__20;
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            try {
                // MethodAssertGenerator build local variable
                Object o_35_0 = updatedRule.getFields().get(0).equals("servicePath");
                // MethodAssertGenerator build local variable
                Object o_39_0 = updatedRule.getRegex().equals(ruleRegex2);
                // MethodAssertGenerator build local variable
                Object o_42_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                // MethodAssertGenerator build local variable
                Object o_45_0 = updatedRule.getDestination().equals(ruleDestination2);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testUpdateRule_literalMutation88459_literalMutation89848 should have thrown Exception");
        } catch (java.lang.Exception eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule_literalMutation88451 */
    @org.junit.Test
    public void testUpdateRule_literalMutation88451_literalMutation89398_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an )existent rule works"));
            java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88451__5 = ruleFields1.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_7_0 = o_testUpdateRule_literalMutation88451__5;
            java.lang.String ruleRegex1 = "/someServicePath1";
            java.lang.String ruleDestination1 = "new_dest1";
            java.lang.String ruleServicePath1 = ")7G5pV_^CY.BAh";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
            java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88451__13 = ruleFields2.add("servicePath");
            // MethodAssertGenerator build local variable
            Object o_18_0 = o_testUpdateRule_literalMutation88451__13;
            java.lang.String ruleRegex2 = "/someServicePath2";
            java.lang.String ruleDestination2 = "new_dest2";
            java.lang.String ruleServicePath2 = "/new_svc_path2";
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
            // AssertGenerator replace invocation
            boolean o_testUpdateRule_literalMutation88451__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
            // MethodAssertGenerator build local variable
            Object o_28_0 = o_testUpdateRule_literalMutation88451__20;
            com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
            try {
                // MethodAssertGenerator build local variable
                Object o_35_0 = updatedRule.getFields().get(0).equals("servicePath");
                // MethodAssertGenerator build local variable
                Object o_39_0 = updatedRule.getRegex().equals(ruleRegex2);
                // MethodAssertGenerator build local variable
                Object o_42_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                // MethodAssertGenerator build local variable
                Object o_45_0 = updatedRule.getDestination().equals(ruleDestination2);
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
            } catch (java.lang.AssertionError e) {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                throw e;
            }// try catch
            
            org.junit.Assert.fail("testUpdateRule_literalMutation88451_literalMutation89398 should have thrown Exception");
        } catch (java.lang.Exception eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule_literalMutation88451 */
    @org.junit.Test(timeout = 10000)
    public void testUpdateRule_literalMutation88451_cf89448_failAssert25_literalMutation95768_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an )existent rule works"));
                java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88451__5 = ruleFields1.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_testUpdateRule_literalMutation88451__5;
                java.lang.String ruleRegex1 = "/someServicePath1";
                java.lang.String ruleDestination1 = "new_dest1";
                java.lang.String ruleServicePath1 = "V}M$t9Rj#>0Q_Z";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
                java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88451__13 = ruleFields2.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_18_0 = o_testUpdateRule_literalMutation88451__13;
                java.lang.String ruleRegex2 = "/someServicePath2";
                java.lang.String ruleDestination2 = "new_dest2";
                java.lang.String ruleServicePath2 = "/new_svc_path2";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88451__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
                // MethodAssertGenerator build local variable
                Object o_28_0 = o_testUpdateRule_literalMutation88451__20;
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
                try {
                    // MethodAssertGenerator build local variable
                    Object o_35_0 = updatedRule.getFields().get(0).equals("servicePath");
                    // MethodAssertGenerator build local variable
                    Object o_39_0 = updatedRule.getRegex().equals(ruleRegex2);
                    // MethodAssertGenerator build local variable
                    Object o_42_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                    // StatementAdderOnAssert create null value
                    com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_12485 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12485.toString();
                    // MethodAssertGenerator build local variable
                    Object o_49_0 = updatedRule.getDestination().equals(ruleDestination2);
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
                } catch (java.lang.AssertionError e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                    throw e;
                }// try catch
                
                org.junit.Assert.fail("testUpdateRule_literalMutation88451_cf89448 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testUpdateRule_literalMutation88451_cf89448_failAssert25_literalMutation95768 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule_literalMutation88486 */
    @org.junit.Test(timeout = 10000)
    public void testUpdateRule_literalMutation88486_cf92092_failAssert26_literalMutation95810_failAssert19() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-------- Updating an existent rule works"));
                java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88486__5 = ruleFields1.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_testUpdateRule_literalMutation88486__5;
                java.lang.String ruleRegex1 = "/someServicePath1";
                java.lang.String ruleDestination1 = "";
                java.lang.String ruleServicePath1 = "/new_svc_path1";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
                java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88486__13 = ruleFields2.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_18_0 = o_testUpdateRule_literalMutation88486__13;
                java.lang.String ruleRegex2 = "/someServicePath2";
                java.lang.String ruleDestination2 = "new_dest2";
                java.lang.String ruleServicePath2 = "/new_svc_path2";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88486__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
                // MethodAssertGenerator build local variable
                Object o_28_0 = o_testUpdateRule_literalMutation88486__20;
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
                try {
                    // MethodAssertGenerator build local variable
                    Object o_35_0 = updatedRule.getFields().get(0).equals("servicePath");
                    // MethodAssertGenerator build local variable
                    Object o_39_0 = updatedRule.getRegex().equals(ruleRegex2);
                    // MethodAssertGenerator build local variable
                    Object o_42_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                    // StatementAdderOnAssert create null value
                    com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_13117 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                    // StatementAdderMethod cloned existing statement
                    vc_13117.getDestination();
                    // MethodAssertGenerator build local variable
                    Object o_49_0 = updatedRule.getDestination().equals(ruleDestination2);
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "@kba<v0D@//]fg^ZdCVNrLad*PVyLOd}k!s(JgXQ"));
                } catch (java.lang.AssertionError e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                    throw e;
                }// try catch
                
                org.junit.Assert.fail("testUpdateRule_literalMutation88486_cf92092 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testUpdateRule_literalMutation88486_cf92092_failAssert26_literalMutation95810 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    // testDeleteRule
    /**
     * [GroupingRules.updateRule] -------- Updating an existent rule works.
     */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule */
    /* amplification of com.telefonica.iot.cygnus.interceptors.CygnusGroupingRulesTest#testUpdateRule_literalMutation88448 */
    @org.junit.Test(timeout = 10000)
    public void testUpdateRule_literalMutation88448_cf89172_failAssert13_literalMutation95171_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + ""));
                java.util.ArrayList<java.lang.String> ruleFields1 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88448__5 = ruleFields1.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_7_0 = o_testUpdateRule_literalMutation88448__5;
                java.lang.String ruleRegex1 = "";
                java.lang.String ruleDestination1 = "new_dest1";
                java.lang.String ruleServicePath1 = "/new_svc_path1";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRules cygnusGroupingRules = createSingleRuleGroupingRules(ruleFields1, ruleRegex1, ruleDestination1, ruleServicePath1, "addRule");
                java.util.ArrayList<java.lang.String> ruleFields2 = new java.util.ArrayList<java.lang.String>();
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88448__13 = ruleFields2.add("servicePath");
                // MethodAssertGenerator build local variable
                Object o_18_0 = o_testUpdateRule_literalMutation88448__13;
                java.lang.String ruleRegex2 = "/someServicePath2";
                java.lang.String ruleDestination2 = "new_dest2";
                java.lang.String ruleServicePath2 = "/new_svc_path2";
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule cygnusGroupingRule = new com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule(createJsonRule(ruleFields2, ruleRegex2, ruleDestination2, ruleServicePath2));
                // AssertGenerator replace invocation
                boolean o_testUpdateRule_literalMutation88448__20 = cygnusGroupingRules.updateRule(1, cygnusGroupingRule);
                // MethodAssertGenerator build local variable
                Object o_28_0 = o_testUpdateRule_literalMutation88448__20;
                com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule updatedRule = cygnusGroupingRules.getRules().get(0);
                try {
                    // MethodAssertGenerator build local variable
                    Object o_35_0 = updatedRule.getFields().get(0).equals("servicePath");
                    // MethodAssertGenerator build local variable
                    Object o_39_0 = updatedRule.getRegex().equals(ruleRegex2);
                    // MethodAssertGenerator build local variable
                    Object o_42_0 = updatedRule.getNewFiwareServicePath().equals(ruleServicePath2);
                    // StatementAdderOnAssert create null value
                    com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule vc_12419 = (com.telefonica.iot.cygnus.interceptors.CygnusGroupingRule)null;
                    // StatementAdderMethod cloned existing statement
                    vc_12419.toString();
                    // MethodAssertGenerator build local variable
                    Object o_49_0 = updatedRule.getDestination().equals(ruleDestination2);
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "-  OK  - Rule with ID 1 has been updated"));
                } catch (java.lang.AssertionError e) {
                    java.lang.System.out.println(((com.telefonica.iot.cygnus.utils.CommonUtilsForTests.getTestTraceHead("[CygnusGroupingRules.updateRule]")) + "- FAIL - Rule with ID 1 has not been updated"));
                    throw e;
                }// try catch
                
                org.junit.Assert.fail("testUpdateRule_literalMutation88448_cf89172 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testUpdateRule_literalMutation88448_cf89172_failAssert13_literalMutation95171 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}


/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.editor.dmn.converter;


import DecisionTableOrientation.RULE_AS_ROW;
import DmnJsonConverter.MODEL_NAMESPACE;
import DmnJsonConverter.URI_JSON;
import HitPolicy.ANY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import org.flowable.dmn.model.Decision;
import org.flowable.dmn.model.DecisionRule;
import org.flowable.dmn.model.DecisionTable;
import org.flowable.dmn.model.DmnDefinition;
import org.flowable.dmn.model.InputClause;
import org.flowable.dmn.model.LiteralExpression;
import org.flowable.dmn.model.OutputClause;
import org.flowable.dmn.model.RuleInputClauseContainer;
import org.flowable.dmn.model.RuleOutputClauseContainer;
import org.flowable.dmn.model.UnaryTests;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Yvo Swillens
 */
public class DmnJsonConverterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DmnJsonConverterTest.class);

    private static final String JSON_RESOURCE_1 = "org/flowable/editor/dmn/converter/decisiontable_1.json";

    private static final String JSON_RESOURCE_2 = "org/flowable/editor/dmn/converter/decisiontable_no_rules.json";

    private static final String JSON_RESOURCE_3 = "org/flowable/editor/dmn/converter/decisiontable_2.json";

    private static final String JSON_RESOURCE_4 = "org/flowable/editor/dmn/converter/decisiontable_empty_expressions.json";

    private static final String JSON_RESOURCE_5 = "org/flowable/editor/dmn/converter/decisiontable_order.json";

    private static final String JSON_RESOURCE_6 = "org/flowable/editor/dmn/converter/decisiontable_entries.json";

    private static final String JSON_RESOURCE_7 = "org/flowable/editor/dmn/converter/decisiontable_dates.json";

    private static final String JSON_RESOURCE_8 = "org/flowable/editor/dmn/converter/decisiontable_empty_operator.json";

    private static final String JSON_RESOURCE_9 = "org/flowable/editor/dmn/converter/decisiontable_complex_output_expression_regression.json";

    private static final String JSON_RESOURCE_10 = "org/flowable/editor/dmn/converter/decisiontable_regression_model_v1.json";

    private static final String JSON_RESOURCE_11 = "org/flowable/editor/dmn/converter/decisiontable_regression_model_v1_no_type.json";

    private static final String JSON_RESOURCE_12 = "org/flowable/editor/dmn/converter/decisiontable_regression_model_v1_no_type2.json";

    private static final String JSON_RESOURCE_13 = "org/flowable/editor/dmn/converter/decisiontable_regression_model_v1_no_type3.json";

    private static final String JSON_RESOURCE_14 = "org/flowable/editor/dmn/converter/decisiontable_regression_model_v1_no_type4.json";

    private static final String JSON_RESOURCE_15 = "org/flowable/editor/dmn/converter/decisiontable_aggregation.json";

    private static final String JSON_RESOURCE_16 = "org/flowable/editor/dmn/converter/decisiontable_special_characters.json";

    private static final String JSON_RESOURCE_17 = "org/flowable/editor/dmn/converter/decisiontable_custom_input_expression.json";

    private static final String JSON_RESOURCE_18 = "org/flowable/editor/dmn/converter/decisiontable_collections_collection_input.json";

    private static final String JSON_RESOURCE_19 = "org/flowable/editor/dmn/converter/decisiontable_collections_collection_compare.json";

    private static final String JSON_RESOURCE_20 = "org/flowable/editor/dmn/converter/decisiontable_complex_output_expression.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void testConvertJsonToDmnOK() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_1);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        Assert.assertNotNull(dmnDefinition);
        Assert.assertEquals(MODEL_NAMESPACE, dmnDefinition.getNamespace());
        Assert.assertEquals(URI_JSON, dmnDefinition.getTypeLanguage());
        Assert.assertEquals("definition_abc", dmnDefinition.getId());
        Assert.assertEquals("decisionTableRule1", dmnDefinition.getName());
        Assert.assertNotNull(dmnDefinition.getDecisions());
        Assert.assertEquals(1, dmnDefinition.getDecisions().size());
        Decision decision = dmnDefinition.getDecisions().get(0);
        Assert.assertNotNull(decision);
        Assert.assertEquals("decTable1", decision.getId());
        DecisionTable decisionTable = ((DecisionTable) (decision.getExpression()));
        Assert.assertNotNull(decisionTable);
        Assert.assertEquals("decisionTable_11", decisionTable.getId());
        Assert.assertEquals(ANY, decisionTable.getHitPolicy());
        Assert.assertEquals(RULE_AS_ROW, decisionTable.getPreferredOrientation());
        List<InputClause> inputClauses = decisionTable.getInputs();
        Assert.assertNotNull(inputClauses);
        Assert.assertEquals(2, inputClauses.size());
        List<OutputClause> outputClauses = decisionTable.getOutputs();
        Assert.assertNotNull(outputClauses);
        Assert.assertEquals(1, outputClauses.size());
        // Condition 1
        InputClause condition1 = inputClauses.get(0);
        Assert.assertNotNull(condition1.getInputExpression());
        LiteralExpression inputExpression11 = condition1.getInputExpression();
        Assert.assertNotNull(inputExpression11);
        Assert.assertEquals("Order Size", inputExpression11.getLabel());
        Assert.assertEquals("inputExpression_input1", inputExpression11.getId());
        Assert.assertEquals("number", inputExpression11.getTypeRef());
        // Condition 2
        InputClause condition2 = inputClauses.get(1);
        Assert.assertNotNull(condition2.getInputExpression());
        LiteralExpression inputExpression21 = condition2.getInputExpression();
        Assert.assertNotNull(inputExpression21);
        Assert.assertEquals("Registered On", inputExpression21.getLabel());
        Assert.assertEquals("inputExpression_input2", inputExpression21.getId());
        Assert.assertEquals("date", inputExpression21.getTypeRef());
        // Conclusion 1
        OutputClause conclusion1 = outputClauses.get(0);
        Assert.assertNotNull(conclusion1);
        Assert.assertEquals("Has discount", conclusion1.getLabel());
        Assert.assertEquals("outputExpression_output1", conclusion1.getId());
        Assert.assertEquals("boolean", conclusion1.getTypeRef());
        Assert.assertEquals("newVariable1", conclusion1.getName());
        // Rule 1
        Assert.assertNotNull(decisionTable.getRules());
        Assert.assertEquals(2, decisionTable.getRules().size());
        List<DecisionRule> rules = decisionTable.getRules();
        Assert.assertEquals(2, rules.get(0).getInputEntries().size());
        // input expression 1
        RuleInputClauseContainer ruleClauseContainer11 = rules.get(0).getInputEntries().get(0);
        UnaryTests inputEntry11 = ruleClauseContainer11.getInputEntry();
        Assert.assertNotNull(inputEntry11);
        Assert.assertEquals("< 10", inputEntry11.getText());
        Assert.assertSame(condition1, ruleClauseContainer11.getInputClause());
        // input expression 2
        RuleInputClauseContainer ruleClauseContainer12 = rules.get(0).getInputEntries().get(1);
        UnaryTests inputEntry12 = ruleClauseContainer12.getInputEntry();
        Assert.assertNotNull(inputEntry12);
        Assert.assertEquals("<= date:toDate('1977-09-18')", inputEntry12.getText());
        Assert.assertSame(condition2, ruleClauseContainer12.getInputClause());
        // output expression 1
        Assert.assertEquals(1, rules.get(0).getOutputEntries().size());
        RuleOutputClauseContainer ruleClauseContainer13 = rules.get(0).getOutputEntries().get(0);
        LiteralExpression outputEntry13 = ruleClauseContainer13.getOutputEntry();
        Assert.assertNotNull(outputEntry13);
        Assert.assertEquals("false", outputEntry13.getText());
        Assert.assertSame(conclusion1, ruleClauseContainer13.getOutputClause());
        // input expression 1
        RuleInputClauseContainer ruleClauseContainer21 = rules.get(1).getInputEntries().get(0);
        UnaryTests inputEntry21 = ruleClauseContainer21.getInputEntry();
        Assert.assertNotNull(inputEntry21);
        Assert.assertEquals("> 10", inputEntry21.getText());
        Assert.assertSame(condition1, ruleClauseContainer21.getInputClause());
        // input expression 2
        RuleInputClauseContainer ruleClauseContainer22 = rules.get(1).getInputEntries().get(1);
        UnaryTests inputEntry22 = ruleClauseContainer22.getInputEntry();
        Assert.assertNotNull(inputEntry22);
        Assert.assertEquals("> date:toDate('1977-09-18')", inputEntry22.getText());
        Assert.assertSame(condition2, ruleClauseContainer22.getInputClause());
        // output expression 1
        Assert.assertEquals(1, rules.get(1).getOutputEntries().size());
        RuleOutputClauseContainer ruleClauseContainer23 = rules.get(1).getOutputEntries().get(0);
        LiteralExpression outputEntry23 = ruleClauseContainer23.getOutputEntry();
        Assert.assertNotNull(outputEntry23);
        Assert.assertEquals("true", outputEntry23.getText());
        Assert.assertSame(conclusion1, ruleClauseContainer23.getOutputClause());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnNoRules() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_2);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        Assert.assertNotNull(dmnDefinition);
        Assert.assertEquals(MODEL_NAMESPACE, dmnDefinition.getNamespace());
        Assert.assertEquals("definition_abc", dmnDefinition.getId());
        Assert.assertEquals("decisionTableRule1", dmnDefinition.getName());
        Assert.assertEquals(URI_JSON, dmnDefinition.getTypeLanguage());
        Assert.assertNotNull(dmnDefinition.getDecisions());
        Assert.assertEquals(1, dmnDefinition.getDecisions().size());
        Decision decision = dmnDefinition.getDecisions().get(0);
        Assert.assertNotNull(decision);
        Assert.assertEquals("decTable1", decision.getId());
        DecisionTable decisionTable = ((DecisionTable) (decision.getExpression()));
        Assert.assertNotNull(decisionTable);
        Assert.assertEquals("decisionTable_11", decisionTable.getId());
        Assert.assertEquals(ANY, decisionTable.getHitPolicy());
        Assert.assertEquals(RULE_AS_ROW, decisionTable.getPreferredOrientation());
        List<InputClause> inputClauses = decisionTable.getInputs();
        Assert.assertNotNull(inputClauses);
        Assert.assertEquals(2, inputClauses.size());
        LiteralExpression inputExpression11 = inputClauses.get(0).getInputExpression();
        Assert.assertNotNull(inputExpression11);
        Assert.assertEquals("Order Size", inputExpression11.getLabel());
        Assert.assertEquals("inputExpression_1", inputExpression11.getId());
        Assert.assertEquals("number", inputExpression11.getTypeRef());
        Assert.assertEquals("ordersize", inputExpression11.getText());
        LiteralExpression inputExpression12 = inputClauses.get(1).getInputExpression();
        Assert.assertNotNull(inputExpression12);
        Assert.assertEquals("Registered On", inputExpression12.getLabel());
        Assert.assertEquals("inputExpression_2", inputExpression12.getId());
        Assert.assertEquals("date", inputExpression12.getTypeRef());
        Assert.assertEquals("registered", inputExpression12.getText());
        List<OutputClause> outputClauses = decisionTable.getOutputs();
        Assert.assertNotNull(outputClauses);
        Assert.assertEquals(1, outputClauses.size());
        // Condition 1
        OutputClause outputClause1 = outputClauses.get(0);
        Assert.assertNotNull(outputClause1);
        Assert.assertEquals("Has discount", outputClause1.getLabel());
        Assert.assertEquals("outputExpression_3", outputClause1.getId());
        Assert.assertEquals("newVariable1", outputClause1.getName());
        Assert.assertEquals("boolean", outputClause1.getTypeRef());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmn2OK() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_3);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        Assert.assertNotNull(dmnDefinition);
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnEmptyExpressions() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_4);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        Assert.assertNotNull(dmnDefinition);
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("-", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(2).getInputEntries().get(0).getInputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnConditionOrder() {
        // Test that editor json, which contains the rules in the incorrect order in
        // the rule object,
        // is converted to a dmn model where the rule columns are in the same order
        // as the input/output clauses
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_5);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        Assert.assertNotNull(dmnDefinition);
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        List<DecisionRule> rules = decisionTable.getRules();
        Assert.assertNotNull(rules);
        Assert.assertEquals(1, rules.size());
        Assert.assertNotNull(rules.get(0).getOutputEntries());
        Assert.assertEquals(3, rules.get(0).getOutputEntries().size());
        Assert.assertEquals("outputExpression_14", rules.get(0).getOutputEntries().get(0).getOutputClause().getId());
        Assert.assertEquals("outputExpression_13", rules.get(0).getOutputEntries().get(1).getOutputClause().getId());
        Assert.assertEquals("outputExpression_15", rules.get(0).getOutputEntries().get(2).getOutputClause().getId());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnEntries() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_6);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("OUTPUT ORDER", decisionTable.getHitPolicy().getValue());
        Assert.assertEquals("\"AAA\",\"BBB\"", decisionTable.getInputs().get(0).getInputValues().getText());
        Assert.assertEquals("AAA", decisionTable.getInputs().get(0).getInputValues().getTextValues().get(0));
        Assert.assertEquals("BBB", decisionTable.getInputs().get(0).getInputValues().getTextValues().get(1));
        Assert.assertEquals("\"THIRD\",\"FIRST\",\"SECOND\"", decisionTable.getOutputs().get(0).getOutputValues().getText());
        Assert.assertEquals("THIRD", decisionTable.getOutputs().get(0).getOutputValues().getTextValues().get(0));
        Assert.assertEquals("FIRST", decisionTable.getOutputs().get(0).getOutputValues().getTextValues().get(1));
        Assert.assertEquals("SECOND", decisionTable.getOutputs().get(0).getOutputValues().getTextValues().get(2));
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnDates() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_7);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("== date:toDate('14-06-2017')", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("!= date:toDate('16-06-2017')", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnEmptyOperator() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_8);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("date:toDate('2017-06-01')", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(0).getInputEntries().get(1).getInputEntry().getText());
        Assert.assertNotNull(decisionTable.getRules().get(0).getInputEntries().get(0).getInputClause());
        Assert.assertNotNull(decisionTable.getRules().get(0).getInputEntries().get(1).getInputClause());
        Assert.assertEquals("date:toDate('2017-06-02')", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(1).getInputEntries().get(1).getInputEntry().getText());
        Assert.assertNotNull(decisionTable.getRules().get(1).getInputEntries().get(0).getInputClause());
        Assert.assertNotNull(decisionTable.getRules().get(1).getInputEntries().get(1).getInputClause());
        Assert.assertEquals("date:toDate('2017-06-03')", decisionTable.getRules().get(0).getOutputEntries().get(0).getOutputEntry().getText());
        Assert.assertEquals("", decisionTable.getRules().get(1).getOutputEntries().get(0).getOutputEntry().getText());
        Assert.assertNotNull(decisionTable.getRules().get(0).getOutputEntries().get(0).getOutputClause());
        Assert.assertNotNull(decisionTable.getRules().get(1).getOutputEntries().get(0).getOutputClause());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnComplexOutputExpressionRegression() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_9);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("refVar1 * refVar2", decisionTable.getRules().get(0).getOutputEntries().get(0).getOutputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnComplexOutputExpression() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_20);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("${refVar1 * refVar2}", decisionTable.getRules().get(0).getOutputEntries().get(0).getOutputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnRegressionModelv1() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_10);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals(4, decisionTable.getInputs().size());
        Assert.assertEquals(4, decisionTable.getOutputs().size());
        Assert.assertEquals(4, decisionTable.getRules().get(0).getInputEntries().size());
        Assert.assertEquals(4, decisionTable.getRules().get(0).getOutputEntries().size());
        DecisionRule rule1 = decisionTable.getRules().get(0);
        DecisionRule rule2 = decisionTable.getRules().get(1);
        Assert.assertEquals("== \"TEST\"", rule1.getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("== 100", rule1.getInputEntries().get(1).getInputEntry().getText());
        Assert.assertEquals("== true", rule1.getInputEntries().get(2).getInputEntry().getText());
        Assert.assertEquals("== date:toDate('2017-06-01')", rule1.getInputEntries().get(3).getInputEntry().getText());
        Assert.assertEquals("\"WAS TEST\"", rule1.getOutputEntries().get(0).getOutputEntry().getText());
        Assert.assertEquals("100", rule1.getOutputEntries().get(1).getOutputEntry().getText());
        Assert.assertEquals("true", rule1.getOutputEntries().get(2).getOutputEntry().getText());
        Assert.assertEquals("date:toDate('2017-06-01')", rule1.getOutputEntries().get(3).getOutputEntry().getText());
        Assert.assertEquals("!= \"TEST\"", rule2.getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("!= 100", rule2.getInputEntries().get(1).getInputEntry().getText());
        Assert.assertEquals("== false", rule2.getInputEntries().get(2).getInputEntry().getText());
        Assert.assertEquals("!= date:toDate('2017-06-01')", rule2.getInputEntries().get(3).getInputEntry().getText());
        Assert.assertEquals("\"WASN\'T TEST\"", rule2.getOutputEntries().get(0).getOutputEntry().getText());
        Assert.assertEquals("1", rule2.getOutputEntries().get(1).getOutputEntry().getText());
        Assert.assertEquals("false", rule2.getOutputEntries().get(2).getOutputEntry().getText());
        Assert.assertEquals("date:toDate('2016-06-01')", rule2.getOutputEntries().get(3).getOutputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnRegressionModelv1NoType() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_11);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("string", decisionTable.getInputs().get(0).getInputExpression().getTypeRef());
        Assert.assertEquals("number", decisionTable.getInputs().get(1).getInputExpression().getTypeRef());
        Assert.assertEquals("boolean", decisionTable.getInputs().get(2).getInputExpression().getTypeRef());
        Assert.assertEquals("date", decisionTable.getInputs().get(3).getInputExpression().getTypeRef());
        Assert.assertEquals("string", decisionTable.getOutputs().get(0).getTypeRef());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnRegressionModelv1NoType2() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_12);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("string", decisionTable.getInputs().get(0).getInputExpression().getTypeRef());
        Assert.assertEquals("number", decisionTable.getInputs().get(1).getInputExpression().getTypeRef());
        Assert.assertEquals("boolean", decisionTable.getInputs().get(2).getInputExpression().getTypeRef());
        Assert.assertEquals("date", decisionTable.getInputs().get(3).getInputExpression().getTypeRef());
        Assert.assertEquals("string", decisionTable.getOutputs().get(0).getTypeRef());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnRegressionModelv1NoType3() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_13);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("string", decisionTable.getInputs().get(0).getInputExpression().getTypeRef());
        Assert.assertEquals("string", decisionTable.getOutputs().get(0).getTypeRef());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnRegressionModelv1NoType4() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_14);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("number", decisionTable.getInputs().get(0).getInputExpression().getTypeRef());
        Assert.assertEquals("boolean", decisionTable.getOutputs().get(0).getTypeRef());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnCollectOperator() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_15);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("SUM", decisionTable.getAggregation().getValue());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnStringSpecialCharacters() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_16);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("== \"TEST\"", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("== \"TEST\"", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnCustomExpressions() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_17);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("${inputVar4 != null}", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("#{inputVar4 > date:now()}", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
    }

    @Test
    public void testConvertJsonToDmnCollectionsCollectionInput() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_18);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("${collection:noneOf(collection1, \"testValue\")}", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, \"testValue\")}", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, 'testVar1,testVar2')}", decisionTable.getRules().get(2).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, \'\"testValue1\",\"testValue2\"\')}", decisionTable.getRules().get(3).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, '10,20')}", decisionTable.getRules().get(4).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, 10)}", decisionTable.getRules().get(5).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(6).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:noneOf(collection1, \"testValue\")}", decisionTable.getRules().get(7).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(8).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:anyOf(collection1, \"testValue\")}", decisionTable.getRules().get(9).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(10).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("== \"testValue\"", decisionTable.getRules().get(11).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("== testCollection", decisionTable.getRules().get(12).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("!= \"testValue\"", decisionTable.getRules().get(13).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(collection1, \'\"test,Value1\",\"test,Value2\"\')}", decisionTable.getRules().get(14).getInputEntries().get(0).getInputEntry().getText());
        // extension elements
        Assert.assertEquals("NONE OF", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getExtensionElements().get("operator").get(0).getElementText());
        Assert.assertEquals("\"testValue\"", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getExtensionElements().get("expression").get(0).getElementText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
        Assert.assertEquals("NONE OF", modelerJson.get("rules").get(0).get("inputExpression_1_operator").asText());
        Assert.assertEquals("\"testValue\"", modelerJson.get("rules").get(0).get("inputExpression_1_expression").asText());
        Assert.assertEquals("ALL OF", modelerJson.get("rules").get(1).get("inputExpression_1_operator").asText());
        Assert.assertEquals("\"testValue\"", modelerJson.get("rules").get(1).get("inputExpression_1_expression").asText());
        Assert.assertEquals("ALL OF", modelerJson.get("rules").get(2).get("inputExpression_1_operator").asText());
        Assert.assertEquals("testVar1, testVar2", modelerJson.get("rules").get(2).get("inputExpression_1_expression").asText());
    }

    @Test
    public void testConvertJsonToDmnCollectionsCollectionCompare() {
        JsonNode testJsonResource = parseJson(DmnJsonConverterTest.JSON_RESOURCE_19);
        DmnDefinition dmnDefinition = new DmnJsonConverter().convertToDmn(testJsonResource, "abc", 1, new Date());
        DecisionTable decisionTable = ((DecisionTable) (dmnDefinition.getDecisions().get(0).getExpression()));
        Assert.assertEquals("${collection:noneOf(\"testValue\", input1)}", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(\"testValue\", input1)}", decisionTable.getRules().get(1).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf('testVar1,testVar2', input1)}", decisionTable.getRules().get(2).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(\'\"testValue1\",\"testValue2\"\', input1)}", decisionTable.getRules().get(3).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf('10,20', input1)}", decisionTable.getRules().get(4).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(10, input1)}", decisionTable.getRules().get(5).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(6).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:noneOf(\"testValue\", input1)}", decisionTable.getRules().get(7).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(8).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(\"testValue\", input1)}", decisionTable.getRules().get(9).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("-", decisionTable.getRules().get(10).getInputEntries().get(0).getInputEntry().getText());
        Assert.assertEquals("${collection:allOf(\'\"test,Value1\",\"test,Value2\"\', input1)}", decisionTable.getRules().get(11).getInputEntries().get(0).getInputEntry().getText());
        // extension elements
        Assert.assertEquals("IS NOT IN", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getExtensionElements().get("operator").get(0).getElementText());
        Assert.assertEquals("\"testValue\"", decisionTable.getRules().get(0).getInputEntries().get(0).getInputEntry().getExtensionElements().get("expression").get(0).getElementText());
        ObjectNode modelerJson = new DmnJsonConverter().convertToJson(dmnDefinition);
        Assert.assertNotNull(modelerJson);
        Assert.assertEquals("IS NOT IN", modelerJson.get("rules").get(0).get("inputExpression_1_operator").asText());
        Assert.assertEquals("\"testValue\"", modelerJson.get("rules").get(0).get("inputExpression_1_expression").asText());
        Assert.assertEquals("IS IN", modelerJson.get("rules").get(1).get("inputExpression_1_operator").asText());
        Assert.assertEquals("\"testValue\"", modelerJson.get("rules").get(1).get("inputExpression_1_expression").asText());
        Assert.assertEquals("IS IN", modelerJson.get("rules").get(2).get("inputExpression_1_operator").asText());
        Assert.assertEquals("testVar1, testVar2", modelerJson.get("rules").get(2).get("inputExpression_1_expression").asText());
    }
}


/**
 * Copyright 2011 Red Hat Inc.
 *
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
package org.drools.compiler.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;


public class SimpleBatchExecutionTest extends CommonTestMethodBase {
    private KieSession ksession;

    protected static final String ruleString = "" + (((((("package org.kie.api.persistence \n" + "global String globalCheeseCountry\n") + "\n") + "rule \'EmptyRule\' \n") + "    when\n") + "    then\n") + "end\n");

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testInsertObjectCommand() throws Exception {
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(expected_1, "out_1"));
        commands.add(CommandFactory.newInsert(expected_2, "out_2"));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Object fact_1 = result.getValue("out_1");
        Assert.assertNotNull(fact_1);
        Object fact_2 = result.getValue("out_2");
        Assert.assertNotNull(fact_2);
        ksession.fireAllRules();
        Object[] expectedArr = new Object[]{ expected_1, expected_2 };
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        Collection<? extends Object> factList = ksession.getObjects();
        Assert.assertTrue(((("Expected " + (expectedList.size())) + " objects but retrieved ") + (factList.size())), ((factList.size()) == (expectedList.size())));
        for (Object fact : factList) {
            if (expectedList.contains(fact)) {
                expectedList.remove(fact);
            }
        }
        Assert.assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testInsertElementsCommand() throws Exception {
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        Object[] expectedArr = new Object[]{ expected_1, expected_2 };
        Collection<Object> factCollection = Arrays.asList(expectedArr);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsertElements(factCollection, "out_list", true, null));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Collection<? extends Object> outList = ((Collection<? extends Object>) (result.getValue("out_list")));
        Assert.assertNotNull(outList);
        ksession.fireAllRules();
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        Collection<? extends Object> factList = ksession.getObjects();
        Assert.assertTrue(((("Expected " + (expectedList.size())) + " objects but retrieved ") + (factList.size())), ((factList.size()) == (expectedList.size())));
        for (Object fact : factList) {
            if (expectedList.contains(fact)) {
                expectedList.remove(fact);
            }
        }
        Assert.assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSetGlobalCommand() throws Exception {
        ksession.insert(new Integer(5));
        ksession.insert(new Integer(7));
        ksession.fireAllRules();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newSetGlobal("globalCheeseCountry", "France", true));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Assert.assertNotNull(result);
        Object global = result.getValue("globalCheeseCountry");
        Assert.assertNotNull(global);
        Assert.assertEquals("France", global);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetGlobalCommand() throws Exception {
        ksession.insert(new Integer(5));
        ksession.insert(new Integer(7));
        ksession.fireAllRules();
        ksession.setGlobal("globalCheeseCountry", "France");
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetGlobal("globalCheeseCountry", "cheeseCountry"));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Assert.assertNotNull("GetGlobalCommand result is null!", result);
        Object global = result.getValue("cheeseCountry");
        Assert.assertNotNull("Retrieved global fact is null!", global);
        Assert.assertEquals("Retrieved global is not equal to 'France'.", "France", global);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetObjectCommand() throws Exception {
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        FactHandle handle_1 = ksession.insert(expected_1);
        FactHandle handle_2 = ksession.insert(expected_2);
        ksession.fireAllRules();
        Object fact = ksession.getObject(handle_1);
        Assert.assertNotNull(fact);
        Assert.assertEquals(expected_1, fact);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObject(handle_1, "out_1"));
        commands.add(CommandFactory.newGetObject(handle_2, "out_2"));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Assert.assertNotNull("GetObjectCommand result is null!", result);
        Assert.assertEquals(expected_1, result.getValue("out_1"));
        Assert.assertEquals(expected_2, result.getValue("out_2"));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetObjectsCommand() throws Exception {
        String expected_1 = "expected_1";
        String expected_2 = "expected_2";
        FactHandle handle_1 = ksession.insert(expected_1);
        FactHandle handle_2 = ksession.insert(expected_2);
        ksession.fireAllRules();
        Object object = ksession.getObject(handle_1);
        Assert.assertNotNull(object);
        Assert.assertEquals(expected_1, object);
        object = ksession.getObject(handle_2);
        Assert.assertNotNull(object);
        Assert.assertEquals(expected_2, object);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newGetObjects("out_list"));
        Command cmds = CommandFactory.newBatchExecution(commands);
        ExecutionResults result = ((ExecutionResults) (ksession.execute(cmds)));
        Assert.assertNotNull("GetObjectsCommand result is null!", result);
        List<Object> objectList = ((List) (result.getValue("out_list")));
        Assert.assertTrue("Retrieved object list is null or empty!", ((objectList != null) && (!(objectList.isEmpty()))));
        Collection<? extends Object> factList = ksession.getObjects();
        Object[] expectedArr = new Object[]{ expected_1, expected_2 };
        List<Object> expectedList = new ArrayList<Object>(Arrays.asList(expectedArr));
        Assert.assertTrue(((("Expected " + (expectedList.size())) + " objects but retrieved ") + (factList.size())), ((factList.size()) == (expectedList.size())));
        for (Object fact : factList) {
            if (expectedList.contains(fact)) {
                expectedList.remove(fact);
            }
        }
        Assert.assertTrue("Retrieved object list did not contain expected objects.", expectedList.isEmpty());
    }
}


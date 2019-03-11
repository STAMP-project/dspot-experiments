/**
 * Copyright 2018-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.graph.hook;


import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.graph.OperationView;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.gaffer.user.User.Builder;
import uk.gov.gchq.koryphe.impl.predicate.Exists;
import uk.gov.gchq.koryphe.impl.predicate.IsIn;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;


public class UpdateViewHookTest {
    public static final String TEST_WITH_VALUE = "withTestValue";

    public static final String TEST_WITHOUT_VALUE = "withoutTestValue";

    public static final String TEST_EDGE = "testEdge";

    public static final String A = "A";

    public static final String B = "B";

    private OperationChain opChain;

    private View viewToMerge;

    private HashSet<String> userOpAuths = Sets.newHashSet();

    private HashSet<String> userDataAuths = Sets.newHashSet();

    private HashSet<String> opAuths = Sets.newHashSet();

    private HashSet<String> dataAuths = Sets.newHashSet();

    private HashSet<String> validAuths = Sets.newHashSet();

    private HashSet<String> userAuths = Sets.newHashSet();

    private Builder userBuilder;

    private UpdateViewHook updateViewHook;

    // ***** preExecute TESTS *****//
    @Test
    public void shouldNotAddExtraGroupsToUsersView() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("entity1").edge("edge1").build()).build()).build();
        updateViewHook.setViewToMerge(new View.Builder().entity("entity2").edge("edge2").build());
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertEquals(Sets.newHashSet("entity1"), opView.getView().getEntityGroups());
            Assert.assertEquals(Sets.newHashSet("edge1"), opView.getView().getEdgeGroups());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldNotAddExtraGroupsToUsersViewInGetAdjacentIds() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAdjacentIds.Builder().view(new View.Builder().edge("edge1").build()).build()).build();
        updateViewHook.setViewToMerge(new View.Builder().entity("entity2").edge("edge2").build());
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertEquals(Sets.newHashSet(), opView.getView().getEntityGroups());
            Assert.assertEquals(Sets.newHashSet("edge1"), opView.getView().getEdgeGroups());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldAddExtraGroupsToUsersView() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("entity1").edge("edge1").build()).build()).build();
        updateViewHook.setViewToMerge(new View.Builder().entity("entity2").edge("edge2").build());
        updateViewHook.setAddExtraGroups(true);
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertEquals(Sets.newHashSet("entity1", "entity2"), opView.getView().getEntityGroups());
            Assert.assertEquals(Sets.newHashSet("edge1", "edge2"), opView.getView().getEdgeGroups());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldNotAddExtraGroupsToEmptyUsersView() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View()).build()).build();
        updateViewHook.setViewToMerge(new View.Builder().entity("entity2").edge("edge2").build());
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertEquals(Sets.newHashSet(), opView.getView().getEntityGroups());
            Assert.assertEquals(Sets.newHashSet(), opView.getView().getEdgeGroups());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldNotMergeWithWrongUser() throws Exception {
        updateViewHook.setViewToMerge(viewToMerge);
        updateViewHook.setWithOpAuth(Sets.newHashSet("opA"));
        opChain = new OperationChain(new GetAllElements());
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertNull(opView.getView());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldMergeWithUser() throws Exception {
        updateViewHook.setViewToMerge(viewToMerge);
        updateViewHook.setWithOpAuth(Sets.newHashSet("opA"));
        opChain = new OperationChain(new GetAllElements());
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertTrue(opView.getView().getGroups().contains("testGroup"));
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldMergeAndApplyWhiteList() throws Exception {
        updateViewHook.setViewToMerge(viewToMerge);
        updateViewHook.setWithOpAuth(Sets.newHashSet("opA"));
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet("white2", "white1", "testGroup"));
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("wrong1").entity("white1").entity("white2").edge("testGroup", new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select("prop1").execute(new Exists()).build()).build()).build()).build()).build();
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        GetAllElements op = ((GetAllElements) (opChain.getOperations().get(0)));
        JsonAssert.assertEquals(new View.Builder().entity("white1", new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select("prop1").execute(new IsIn("value1", "value2")).build()).build()).entity("white2").edge("testGroup", new ViewElementDefinition.Builder().preAggregationFilter(new ElementFilter.Builder().select("prop1").execute(new Exists()).select("count").execute(new IsMoreThan(10)).build()).build()).build().toJson(true), op.getView().toJson(true));
        Assert.assertTrue(op.getView().getGroups().contains("testGroup"));
    }

    @Test
    public void shouldApplyWhiteAndBlackLists() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("wrong1").entity("white1").entity("white2").build()).build()).build();
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet("white2", "white1"));
        updateViewHook.setBlackListElementGroups(Sets.newHashSet("white1"));
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertTrue(opView.getView().getEntities().keySet().contains("white2"));
            Assert.assertEquals(1, opView.getView().getEntities().keySet().size());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldApplyWhiteLists() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("wrong1").entity("white1").entity("white2").build()).build()).build();
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet("white2", "white1"));
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertTrue(opView.getView().getEntities().keySet().contains("white2"));
            Assert.assertTrue(opView.getView().getEntities().keySet().contains("white1"));
            Assert.assertEquals(2, opView.getView().getEntities().keySet().size());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldApplyBlackLists() throws Exception {
        opChain = new OperationChain.Builder().first(new GetAllElements.Builder().view(new View.Builder().entity("white1").entity("white2").build()).build()).build();
        updateViewHook.setBlackListElementGroups(Sets.newHashSet("white1"));
        updateViewHook.preExecute(opChain, new uk.gov.gchq.gaffer.store.Context(new User.Builder().opAuth("opA").build()));
        Object op = opChain.getOperations().get(0);
        if (op instanceof OperationView) {
            OperationView opView = ((OperationView) (op));
            Assert.assertTrue(opView.getView().getEntities().keySet().contains("white2"));
            Assert.assertEquals(1, opView.getView().getEntities().keySet().size());
        } else {
            Assert.fail("unexpected operation found.");
        }
    }

    @Test
    public void shouldDoNothingWithNullMerge() throws Exception {
        GetAllElements operationView = new GetAllElements();
        operationView.setView(new View());
        View view = updateViewHook.mergeView(operationView, null).build();
        Assert.assertTrue(view.getGroups().isEmpty());
    }

    @Test
    public void shouldMerge() throws Exception {
        GetAllElements operationView = new GetAllElements();
        operationView.setView(new View());
        View view = updateViewHook.mergeView(operationView, viewToMerge).build();
        Set<String> groups = view.getGroups();
        Assert.assertFalse(groups.isEmpty());
        Assert.assertTrue(groups.contains("testGroup"));
    }

    @Test
    public void shouldDoNothingReturnResult() throws Exception {
        final String testString = "testString";
        Assert.assertEquals(testString, updateViewHook.postExecute(testString, null, null));
        Assert.assertEquals(testString, updateViewHook.onFailure(testString, null, null, null));
    }

    // ***** ApplyToUserTests *****
    @Test
    public void shouldPassWithOnlyOps() throws Exception {
        userOpAuths.add("oA");
        opAuths.add("oA");
        userBuilder.opAuths(opAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertTrue(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldPassWithOnlyData() throws Exception {
        updateViewHook.setWithOpAuth(null);
        Assert.assertTrue("updateViewHook.getWithOpAuth() needs to be empty for this test", (((updateViewHook.getWithOpAuth()) == null) || (updateViewHook.getWithOpAuth().isEmpty())));
        userDataAuths.add("dA");
        dataAuths.add("dA");
        userBuilder.dataAuths(userDataAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        Assert.assertTrue(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldPassWithBoth() throws Exception {
        userDataAuths.add("dA");
        dataAuths.add("dA");
        userOpAuths.add("oA");
        opAuths.add("oA");
        userBuilder.dataAuths(userDataAuths);
        userBuilder.opAuths(userOpAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertTrue(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldFailWithWrongOps() throws Exception {
        userOpAuths.add("oB");
        opAuths.add("oA");
        userBuilder.opAuths(userOpAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertFalse(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldFailWithWrongData() throws Exception {
        userDataAuths.add("dA");
        dataAuths.add("dB");
        userBuilder.dataAuths(userDataAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        Assert.assertFalse(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldFailWithBothWrongOPsData() throws Exception {
        userDataAuths.add("dB");
        dataAuths.add("dA");
        userOpAuths.add("oB");
        opAuths.add("oA");
        userBuilder.dataAuths(userDataAuths);
        userBuilder.opAuths(userOpAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertFalse(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldFailWithOneWrongOPs() throws Exception {
        userDataAuths.add("dA");
        dataAuths.add("dA");
        userOpAuths.add("oB");
        opAuths.add("oA");
        userBuilder.dataAuths(userDataAuths);
        userBuilder.opAuths(userOpAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertFalse(updateViewHook.applyToUser(userBuilder.build()));
    }

    @Test
    public void shouldFailWithOneWrongData() throws Exception {
        userDataAuths.add("dB");
        dataAuths.add("dA");
        userOpAuths.add("oA");
        opAuths.add("oA");
        userBuilder.dataAuths(userDataAuths);
        userBuilder.opAuths(userOpAuths);
        updateViewHook.setWithDataAuth(dataAuths);
        updateViewHook.setWithOpAuth(opAuths);
        Assert.assertFalse(updateViewHook.applyToUser(userBuilder.build()));
    }

    // ***** Serialise Tests *****
    @Test
    public void shouldSerialiseOpAuth() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook.Builder().withOpAuth(Sets.newHashSet(UpdateViewHookTest.TEST_WITH_VALUE)).withoutOpAuth(Sets.newHashSet(UpdateViewHookTest.TEST_WITHOUT_VALUE)).build();
        byte[] serialise = getBytes(updateViewHook);
        UpdateViewHook deserialise = JSONSerialiser.deserialise(serialise, UpdateViewHook.class);
        Assert.assertTrue(deserialise.getWithOpAuth().contains(UpdateViewHookTest.TEST_WITH_VALUE));
        Assert.assertTrue(deserialise.getWithoutOpAuth().contains(UpdateViewHookTest.TEST_WITHOUT_VALUE));
    }

    @Test
    public void shouldSerialiseDataAuths() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook.Builder().withDataAuth(Sets.newHashSet(UpdateViewHookTest.TEST_WITH_VALUE)).withoutDataAuth(Sets.newHashSet(UpdateViewHookTest.TEST_WITHOUT_VALUE)).build();
        byte[] serialise = getBytes(updateViewHook);
        UpdateViewHook deserialise = JSONSerialiser.deserialise(serialise, UpdateViewHook.class);
        Assert.assertTrue(deserialise.getWithDataAuth().contains(UpdateViewHookTest.TEST_WITH_VALUE));
        Assert.assertTrue(deserialise.getWithoutDataAuth().contains(UpdateViewHookTest.TEST_WITHOUT_VALUE));
    }

    @Test
    public void shouldSerialiseElementGroups() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook.Builder().whiteListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_WITH_VALUE)).blackListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_WITHOUT_VALUE)).build();
        byte[] serialise = getBytes(updateViewHook);
        UpdateViewHook deserialise = JSONSerialiser.deserialise(serialise, UpdateViewHook.class);
        Assert.assertTrue(deserialise.getWhiteListElementGroups().contains(UpdateViewHookTest.TEST_WITH_VALUE));
        Assert.assertTrue(deserialise.getBlackListElementGroups().contains(UpdateViewHookTest.TEST_WITHOUT_VALUE));
    }

    @Test
    public void shouldSerialiseViewToMerge() throws Exception {
        View viewToMerge = new View.Builder().entity(UpdateViewHookTest.TEST_EDGE).build();
        UpdateViewHook updateViewHook = new UpdateViewHook.Builder().setViewToMerge(viewToMerge).build();
        byte[] serialise = JSONSerialiser.serialise(updateViewHook, true);
        String s = new String(serialise);
        Assert.assertTrue(s, s.contains(UpdateViewHookTest.TEST_EDGE));
        UpdateViewHook deserialise = JSONSerialiser.deserialise(serialise, UpdateViewHook.class);
        Assert.assertTrue(deserialise.getViewToMerge().equals(viewToMerge));
    }

    public static final String TEST_KEY = "testKey";

    public static final String OTHER = "other";

    @Test
    public void shouldRemoveBlackList() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook();
        updateViewHook.setBlackListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        Assert.assertTrue(updateViewHook.removeElementGroups(getEntry()));
    }

    @Test
    public void shouldKeepWhiteList() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook();
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        Assert.assertFalse(updateViewHook.removeElementGroups(getEntry()));
    }

    @Test
    public void shouldRemoveInBothLists() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook();
        updateViewHook.setBlackListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        Assert.assertTrue(updateViewHook.removeElementGroups(getEntry()));
    }

    @Test
    public void shouldKeepWhiteList2() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook();
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        updateViewHook.setBlackListElementGroups(Sets.newHashSet(UpdateViewHookTest.OTHER));
        Assert.assertFalse(updateViewHook.removeElementGroups(getEntry()));
    }

    @Test
    public void shouldRemoveBlackList2() throws Exception {
        UpdateViewHook updateViewHook = new UpdateViewHook();
        updateViewHook.setWhiteListElementGroups(Sets.newHashSet(UpdateViewHookTest.OTHER));
        updateViewHook.setBlackListElementGroups(Sets.newHashSet(UpdateViewHookTest.TEST_KEY));
        Assert.assertTrue(updateViewHook.removeElementGroups(getEntry()));
    }

    // ***** VALIDATE AUTHS TESTS *****
    @Test
    public void shouldPassExcessAuth() throws Exception {
        userAuths.add(UpdateViewHookTest.A);
        userAuths.add(UpdateViewHookTest.B);
        validAuths.add(UpdateViewHookTest.A);
        Assert.assertTrue(updateViewHook.validateAuths(userAuths, validAuths, true));
    }

    @Test
    public void shouldPassSubsetAuth() throws Exception {
        userAuths.add(UpdateViewHookTest.A);
        validAuths.add(UpdateViewHookTest.A);
        validAuths.add(UpdateViewHookTest.B);
        Assert.assertTrue(updateViewHook.validateAuths(userAuths, validAuths, true));
    }

    @Test
    public void shouldFailMissingAuth() throws Exception {
        userAuths.add(UpdateViewHookTest.B);
        validAuths.add(UpdateViewHookTest.A);
        Assert.assertFalse(updateViewHook.validateAuths(userAuths, validAuths, true));
    }

    @Test
    public void shouldFailEmptyUserAuths() throws Exception {
        validAuths.add(UpdateViewHookTest.A);
        Assert.assertFalse(updateViewHook.validateAuths(userAuths, validAuths, true));
    }

    @Test
    public void shouldFailNullUserAuths() throws Exception {
        validAuths.add(UpdateViewHookTest.A);
        Assert.assertFalse(updateViewHook.validateAuths(null, validAuths, true));
    }

    @Test
    public void shouldPassNullValid() throws Exception {
        Assert.assertTrue(updateViewHook.validateAuths(userAuths, null, true));
    }
}


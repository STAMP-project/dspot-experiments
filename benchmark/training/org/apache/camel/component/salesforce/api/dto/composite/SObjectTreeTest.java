/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api.dto.composite;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thoughtworks.xstream.XStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.camel.component.salesforce.api.utils.JsonUtils;
import org.apache.camel.component.salesforce.api.utils.XStreamUtils;
import org.apache.camel.component.salesforce.dto.generated.Account;
import org.apache.camel.component.salesforce.dto.generated.Asset;
import org.apache.camel.component.salesforce.dto.generated.Contact;
import org.junit.Assert;
import org.junit.Test;


public class SObjectTreeTest extends CompositeTestBase {
    @Test
    public void emptyTreeShouldBeZeroSized() {
        Assert.assertEquals(0, new SObjectTree().size());
    }

    @Test
    public void shouldCollectAllObjectTypesInTheTree() {
        final SObjectTree tree = new SObjectTree();
        tree.addObject(new Account()).addChild(new Contact()).addChild("Assets", new Asset());
        tree.addObject(new Account());
        final Class[] types = tree.objectTypes();
        Arrays.sort(types, (final Class l,final Class r) -> l.getName().compareTo(r.getName()));
        Assert.assertArrayEquals(new Class[]{ Account.class, Asset.class, Contact.class }, types);
    }

    @Test
    public void shouldSerializeToJson() throws JsonProcessingException {
        final ObjectMapper mapper = JsonUtils.createObjectMapper();
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        final ObjectWriter writer = mapper.writerFor(SObjectTree.class);
        final SObjectTree tree = new SObjectTree();
        final SObjectNode account1 = new SObjectNode(tree, simpleAccount);
        account1.addChild("Contacts", smith);
        account1.addChild("Contacts", evans);
        tree.addNode(account1);
        final SObjectNode account2 = new SObjectNode(tree, simpleAccount2);
        tree.addNode(account2);
        final String json = writer.writeValueAsString(tree);
        Assert.assertEquals("Should serialize to JSON as in Salesforce example", ("{\"records\":["// 
         + ((((((((((((((((((((((((((((((((("{"// 
         + "\"attributes\":{\"referenceId\":\"ref1\",\"type\":\"Account\"},")// 
         + "\"Industry\":\"Banking\",")// 
         + "\"Name\":\"SampleAccount\",")// 
         + "\"NumberOfEmployees\":100,")// 
         + "\"Phone\":\"1234567890\",")// 
         + "\"Website\":\"www.salesforce.com\",")// 
         + "\"Contacts\":{")// 
         + "\"records\":[")// 
         + "{")// 
         + "\"attributes\":{\"referenceId\":\"ref2\",\"type\":\"Contact\"},")// 
         + "\"Email\":\"sample@salesforce.com\",")// 
         + "\"LastName\":\"Smith\",")// 
         + "\"Title\":\"President\"")// 
         + "},")// 
         + "{")// 
         + "\"attributes\":{\"referenceId\":\"ref3\",\"type\":\"Contact\"},")// 
         + "\"Email\":\"sample@salesforce.com\",")// 
         + "\"LastName\":\"Evans\",")// 
         + "\"Title\":\"Vice President\"")// 
         + "}")// 
         + "]")// 
         + "}")// 
         + "},")// 
         + "{")// 
         + "\"attributes\":{\"referenceId\":\"ref4\",\"type\":\"Account\"},")// 
         + "\"Industry\":\"Banking\",")// 
         + "\"Name\":\"SampleAccount2\",")// 
         + "\"NumberOfEmployees\":100,")// 
         + "\"Phone\":\"1234567890\",")// 
         + "\"Website\":\"www.salesforce2.com\"")// 
         + "}")// 
         + "]")// 
         + "}")), json);
    }

    @Test
    public void shouldSerializeToXml() {
        final SObjectTree tree = new SObjectTree();
        final SObjectNode account1 = new SObjectNode(tree, simpleAccount);
        account1.addChild("Contacts", smith);
        account1.addChild("Contacts", evans);
        tree.addNode(account1);
        final SObjectNode account2 = new SObjectNode(tree, simpleAccount2);
        tree.addNode(account2);
        final XStream xStream = XStreamUtils.createXStream(SObjectTree.class, Account.class, Contact.class, Asset.class);
        final String xml = xStream.toXML(tree);
        Assert.assertEquals("Should serialize to XML as in Salesforce example", ("<SObjectTreeRequest>"// 
         + (((((((((((((((((((((((((("<records type=\"Account\" referenceId=\"ref1\">"// 
         + "<Name>SampleAccount</Name>")// 
         + "<Phone>1234567890</Phone>")// 
         + "<Website>www.salesforce.com</Website>")// 
         + "<Industry>Banking</Industry>")// 
         + "<NumberOfEmployees>100</NumberOfEmployees>")// 
         + "<Contacts>")// 
         + "<records type=\"Contact\" referenceId=\"ref2\">")// 
         + "<Email>sample@salesforce.com</Email>")// 
         + "<LastName>Smith</LastName>")// 
         + "<Title>President</Title>")// 
         + "</records>")// 
         + "<records type=\"Contact\" referenceId=\"ref3\">")// 
         + "<Email>sample@salesforce.com</Email>")// 
         + "<LastName>Evans</LastName>")// 
         + "<Title>Vice President</Title>")// 
         + "</records>")// 
         + "</Contacts>")// 
         + "</records>")// 
         + "<records type=\"Account\" referenceId=\"ref4\">")// 
         + "<Name>SampleAccount2</Name>")// 
         + "<Phone>1234567890</Phone>")// 
         + "<Website>www.salesforce2.com</Website>")// 
         + "<Industry>Banking</Industry>")// 
         + "<NumberOfEmployees>100</NumberOfEmployees>")// 
         + "</records>")// 
         + "</SObjectTreeRequest>")), xml);
    }

    @Test
    public void shouldSetIdByReferences() {
        final SObjectTree tree = new SObjectTree();
        final SObjectNode account1 = new SObjectNode(tree, simpleAccount);
        account1.addChild("Contacts", smith);
        account1.addChild("Contacts", evans);
        tree.addNode(account1);
        final SObjectNode account2 = new SObjectNode(tree, simpleAccount2);
        tree.addNode(account2);
        tree.setIdFor("ref1", "id1");
        tree.setIdFor("ref4", "id4");
        tree.setIdFor("ref3", "id3");
        tree.setIdFor("ref2", "id2");
        Assert.assertEquals("id1", getId());
        Assert.assertEquals("id2", getId());
        Assert.assertEquals("id3", getId());
        Assert.assertEquals("id4", getId());
    }

    @Test
    public void shouldSetIdByReferencesForNestedObjects() {
        final SObjectTree tree = new SObjectTree();
        final Account account = new Account();
        final SObjectNode accountNode = new SObjectNode(tree, account);
        tree.addNode(accountNode);
        final Contact contact = new Contact();
        final SObjectNode contactNode = new SObjectNode(tree, contact);
        accountNode.addChild("Contacts", contactNode);
        final Asset asset = new Asset();
        final SObjectNode assetNode = new SObjectNode(tree, asset);
        contactNode.addChild("Assets", assetNode);
        Assert.assertEquals("ref1", accountNode.getAttributes().getReferenceId());
        Assert.assertEquals("ref2", contactNode.getAttributes().getReferenceId());
        Assert.assertEquals("ref3", assetNode.getAttributes().getReferenceId());
        tree.setIdFor("ref1", "id1");
        tree.setIdFor("ref3", "id3");
        tree.setIdFor("ref2", "id2");
        Assert.assertEquals("id1", getId());
        Assert.assertEquals("id2", getId());
        Assert.assertEquals("id3", getId());
    }

    @Test
    public void shouldSetReferences() {
        final SObjectTree tree = new SObjectTree();
        final SObjectNode account1 = new SObjectNode(tree, simpleAccount);
        account1.addChild("Contacts", smith);
        account1.addChild("Contacts", evans);
        tree.addNode(account1);
        final SObjectNode account2 = new SObjectNode(tree, simpleAccount2);
        tree.addNode(account2);
        final SObjectNode simpleAccountFromTree = tree.records.get(0);
        Assert.assertEquals("ref1", simpleAccountFromTree.getAttributes().getReferenceId());
        final Iterator<SObjectNode> simpleAccountNodes = simpleAccountFromTree.getChildNodes().iterator();
        Assert.assertEquals("ref2", simpleAccountNodes.next().getAttributes().getReferenceId());
        Assert.assertEquals("ref3", simpleAccountNodes.next().getAttributes().getReferenceId());
        Assert.assertEquals("ref4", account2.getAttributes().getReferenceId());
    }

    @Test
    public void shouldSupportBuildingObjectTree() {
        final SObjectTree tree = new SObjectTree();
        tree.addObject(simpleAccount).addChildren("Contacts", smith, evans);
        tree.addObject(simpleAccount2);
        final SObjectNode firstAccountFromTree = tree.records.get(0);
        Assert.assertSame(simpleAccount, firstAccountFromTree.getObject());
        Assert.assertEquals("Account", firstAccountFromTree.getObjectType());
        final Iterator<SObjectNode> simpleAccountNodes = firstAccountFromTree.getChildNodes().iterator();
        final SObjectNode smithNode = simpleAccountNodes.next();
        Assert.assertSame(smith, smithNode.getObject());
        Assert.assertEquals("Contact", smithNode.getObjectType());
        final SObjectNode evansNode = simpleAccountNodes.next();
        Assert.assertSame(evans, evansNode.getObject());
        Assert.assertEquals("Contact", evansNode.getObjectType());
        final SObjectNode secondAccountFromTree = tree.records.get(1);
        Assert.assertSame(simpleAccount2, secondAccountFromTree.getObject());
        Assert.assertEquals("Account", secondAccountFromTree.getObjectType());
    }

    @Test
    public void treeWithOneNodeShouldHaveSizeOfOne() {
        final SObjectTree tree = new SObjectTree();
        tree.addObject(new Account());
        Assert.assertEquals(1, tree.size());
    }

    @Test
    public void treeWithTwoNestedNodesShouldHaveSizeOfTwo() {
        final SObjectTree tree = new SObjectTree();
        final SObjectNode accountNode = tree.addObject(new Account());
        accountNode.addChild("Contacts", new Contact());
        Assert.assertEquals(2, tree.size());
    }

    @Test
    public void treeWithTwoNodesShouldHaveSizeOfTwo() {
        final SObjectTree tree = new SObjectTree();
        tree.addObject(new Account());
        tree.addObject(new Account());
        Assert.assertEquals(2, tree.size());
    }
}


/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.misc;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DrlRuleDataTest {
    @Test
    public void testHandleDrl() {
        String drl = "rule \"Something\" \n ";
        drl += "dialect \"Java\" \n ";
        drl += "\twhen \n ";
        drl += "\t\tPerson() \n ";
        drl += "\t\tCheesery() \n ";
        drl += "\tthen \n ";
        drl += "\t\tinsert( new Person()) \n ";
        drl += "\t\tinsert( new Car()) \n ";
        drl += "\t\tinsert( new Cheese()) \n ";
        drl += "end ";
        DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);
        Assert.assertEquals(1, s.getHeader().size());
        Assert.assertEquals(2, s.getLhs().size());
        Assert.assertEquals(3, s.getRhs().size());
        Assert.assertEquals("", s.getDescription());
    }

    @Test
    public void testHandleDrlNoLineBreaks() {
        String drl = "rule \"CreditScoreApproval\" \n";
        drl += "\tdialect \"mvel\" \n";
        drl += "	when    then";
        drl += "\t\tapplicant.setApproved(true) \n";
        drl += "\t\tapplicant.setName( \"Toni\" ) \n";
        drl += "\t\tapplicant.setAge( 10 ) \n";
        drl += "end";
        DrlRuleParser s = DrlRuleParser.findRulesDataFromDrl(drl).get(0);
        Assert.assertNotNull(s);
        Assert.assertEquals(1, s.getHeader().size());
        Assert.assertEquals(0, s.getLhs().size());
        Assert.assertEquals(3, s.getRhs().size());
        Assert.assertEquals("", s.getDescription());
    }

    @Test
    public void testHandleDrlWithComment() {
        String drl = "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "rule \"First\" \n";
        drl += "\tdialect \"mvel\" \n";
        drl += "\twhen \n ";
        drl += "\t\tPerson() \n ";
        drl += "\t\tCheesery() \n ";
        drl += "\tthen \n ";
        drl += "\t\tapplicant.setApproved(true) \n";
        drl += "\t\tapplicant.setName( \"Toni\" ) \n";
        drl += "\t\tapplicant.setAge( 10 ) \n";
        drl += "end \n";
        drl += "\n";
        drl += "# Really important information about this rule \n";
        drl += "# Another line because one was not enough \n";
        drl += "#  \n";
        drl += "# @author: trikkola \n";
        drl += "# @created: 29.12.2001 \n";
        drl += "# @edited: 5.5.2005 \n";
        drl += "rule \"Second\" \n";
        drl += "\tdialect \"mvel\" \n";
        drl += "\twhen \n ";
        drl += "\t\tPerson() \n ";
        drl += "\t\tCheesery() \n ";
        drl += "\tthen \n ";
        drl += "\t\tapplicant.setApproved(true) \n";
        drl += "\t\tapplicant.setName( \"Toni\" ) \n";
        drl += "\t\tapplicant.setAge( 10 ) \n";
        drl += "end";
        drl += "\n";
        drl += "rule \"Third\" \n";
        drl += "\tdialect \"mvel\" \n";
        drl += "\twhen \n ";
        drl += "\t\tPerson() \n ";
        drl += "\t\tCheesery() \n ";
        drl += "\tthen \n ";
        drl += "\t\tapplicant.setApproved(true) \n";
        drl += "\t\tapplicant.setName( \"Toni\" ) \n";
        drl += "\t\tapplicant.setAge( 10 ) \n";
        drl += "end";
        List<DrlRuleParser> list = DrlRuleParser.findRulesDataFromDrl(drl);
        Assert.assertEquals(3, list.size());
        DrlRuleParser rd = list.get(0);
        Assert.assertNotNull(rd);
        Assert.assertEquals(1, rd.getHeader().size());
        Assert.assertEquals(2, rd.getLhs().size());
        Assert.assertEquals(3, rd.getRhs().size());
        Assert.assertEquals(1, rd.getMetadata().size());
        Assert.assertNotNull(rd.getDescription());
        Assert.assertNotSame("", rd.getDescription());
        DrlRuleParser rd2 = list.get(1);
        Assert.assertNotNull(rd2);
        Assert.assertEquals(1, rd2.getHeader().size());
        Assert.assertEquals(2, rd2.getLhs().size());
        Assert.assertEquals(3, rd2.getRhs().size());
        Assert.assertEquals(3, rd2.getMetadata().size());
        Assert.assertNotNull(rd2.getDescription());
        String description = "Really important information about this rule\n";
        description += "Another line because one was not enough\n\n";
        Assert.assertEquals(description, rd2.getDescription());
        Assert.assertNotSame("", rd2.getDescription());
        DrlRuleParser rd3 = list.get(2);
        Assert.assertNotNull(rd3);
        Assert.assertEquals(1, rd3.getHeader().size());
        Assert.assertEquals(2, rd3.getLhs().size());
        Assert.assertEquals(3, rd3.getRhs().size());
        Assert.assertNotNull(rd3.getDescription());
        Assert.assertEquals("", rd3.getDescription());
    }
}


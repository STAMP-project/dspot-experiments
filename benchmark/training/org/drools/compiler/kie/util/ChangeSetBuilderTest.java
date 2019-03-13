/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.kie.util;


import ChangeType.ADDED;
import ChangeType.REMOVED;
import ChangeType.UPDATED;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange.Type;
import org.kie.internal.builder.ResourceChangeSet;


public class ChangeSetBuilderTest {
    @Test
    public void testNoChanges() {
        String drl1 = "package org.drools\n" + ((("rule R1 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl2 = "package org.drools\n" + ((("rule R2 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        InternalKieModule kieJar1 = createKieJar(drl1, drl2);
        InternalKieModule kieJar2 = createKieJar(drl1, drl2);
        KieJarChangeSet changes = ChangeSetBuilder.build(kieJar1, kieJar2);
        Assert.assertThat(changes.getChanges().size(), CoreMatchers.is(0));
    }

    @Test
    public void testModified() {
        String drl1 = "package org.drools\n" + ((("rule R1 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl2 = "package org.drools\n" + ((("rule R2 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl3 = "package org.drools\n" + ((("rule R3 when\n" + "   $m : Message( message == \"Good bye World\" )\n") + "then\n") + "end\n");
        InternalKieModule kieJar1 = createKieJar(drl1, drl2);
        InternalKieModule kieJar2 = createKieJar(drl1, drl3);
        KieJarChangeSet changes = ChangeSetBuilder.build(kieJar1, kieJar2);
        String modifiedFile = ((String) (kieJar2.getFileNames().toArray()[1]));
        Assert.assertThat(changes.getChanges().size(), CoreMatchers.is(1));
        ResourceChangeSet cs = changes.getChanges().get(modifiedFile);
        Assert.assertThat(cs, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cs.getChangeType(), CoreMatchers.is(UPDATED));
        Assert.assertThat(cs.getChanges().size(), CoreMatchers.is(2));
        Assert.assertThat(cs.getChanges().get(1), CoreMatchers.is(new org.kie.internal.builder.ResourceChange(ChangeType.ADDED, Type.RULE, "R3")));
        Assert.assertThat(cs.getChanges().get(0), CoreMatchers.is(new org.kie.internal.builder.ResourceChange(ChangeType.REMOVED, Type.RULE, "R2")));
        // ChangeSetBuilder builder = new ChangeSetBuilder();
        // System.out.println( builder.toProperties( changes ) );
    }

    @Test
    public void testRemoved() {
        String drl1 = "package org.drools\n" + ((("rule R1 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl2 = "package org.drools\n" + ((("rule R2 when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        InternalKieModule kieJar1 = createKieJar(drl1, drl2);
        InternalKieModule kieJar2 = createKieJar(drl1);
        KieJarChangeSet changes = ChangeSetBuilder.build(kieJar1, kieJar2);
        String removedFile = ((String) (kieJar1.getFileNames().toArray()[1]));
        Assert.assertThat(changes.getChanges().size(), CoreMatchers.is(1));
        ResourceChangeSet cs = changes.getChanges().get(removedFile);
        Assert.assertThat(cs, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cs.getChangeType(), CoreMatchers.is(REMOVED));
    }

    @Test
    public void testModified2() {
        String drl1 = "package org.drools\n" + ((((((((((("rule \"Rule 1\" when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n") + "rule \"An updated rule\" when\n") + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n") + "rule \"A removed rule\" when\n") + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl1_5 = "package org.drools\n" + ((((((((((("rule \"Rule 1\" when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n") + "rule \"An updated rule\" when\n") + "   $m : Message( message == \"Good Bye World\" )\n") + "then\n") + "end\n") + "rule \"An added rule\" when\n") + "   $m : Message( message == \"Good Bye World\" )\n") + "then\n") + "end\n");
        String drl2 = "package org.drools\n" + ((("rule \"This is the name of rule 3\" when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n");
        String drl3 = "package org.drools\n" + ((("rule \"Another dumb rule\" when\n" + "   $m : Message( message == \"Good bye World\" )\n") + "then\n") + "end\n");
        InternalKieModule kieJar1 = createKieJar(drl1, drl2);
        InternalKieModule kieJar2 = createKieJar(drl1_5, null, drl3);
        KieJarChangeSet changes = ChangeSetBuilder.build(kieJar1, kieJar2);
        // System.out.println( builder.toProperties( changes ) );
        String modifiedFile = ((String) (kieJar2.getFileNames().toArray()[0]));
        String addedFile = ((String) (kieJar2.getFileNames().toArray()[1]));
        String removedFile = ((String) (kieJar1.getFileNames().toArray()[1]));
        Assert.assertThat(changes.getChanges().size(), CoreMatchers.is(3));
        ResourceChangeSet cs = changes.getChanges().get(removedFile);
        Assert.assertThat(cs, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cs.getChangeType(), CoreMatchers.is(REMOVED));
        Assert.assertThat(cs.getChanges().size(), CoreMatchers.is(0));
        cs = changes.getChanges().get(addedFile);
        Assert.assertThat(cs, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cs.getChangeType(), CoreMatchers.is(ADDED));
        Assert.assertThat(cs.getChanges().size(), CoreMatchers.is(0));
        cs = changes.getChanges().get(modifiedFile);
        Assert.assertThat(cs, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cs.getChangeType(), CoreMatchers.is(UPDATED));
        // assertThat( cs.getChanges().size(), is(3) );
        // assertThat( cs.getChanges().get( 0 ), is( new ResourceChange(ChangeType.ADDED, Type.RULE, "An added rule") ) );
        // assertThat( cs.getChanges().get( 1 ), is( new ResourceChange(ChangeType.REMOVED, Type.RULE, "A removed rule") ) );
        // assertThat( cs.getChanges().get( 2 ), is( new ResourceChange(ChangeType.UPDATED, Type.RULE, "An updated rule") ) );
    }

    @Test
    public void testRuleRemoval() throws Exception {
        String drl1 = "package org.drools.compiler\n" + ((("rule R1 when\n" + "   $m : Message()\n") + "then\n") + "end\n");
        String drl2 = "rule R2 when\n" + (("   $m : Message( message == \"Hi Universe\" )\n" + "then\n") + "end\n");
        String drl3 = "rule R3 when\n" + (("   $m : Message( message == \"Hello World\" )\n" + "then\n") + "end\n");
        InternalKieModule kieJar1 = createKieJar(((drl1 + drl2) + drl3));
        InternalKieModule kieJar2 = createKieJar((drl1 + drl3));
        KieJarChangeSet changes = ChangeSetBuilder.build(kieJar1, kieJar2);
        Assert.assertEquals(1, changes.getChanges().size());
        ResourceChangeSet rcs = changes.getChanges().values().iterator().next();
        Assert.assertEquals(1, rcs.getChanges().size());
        Assert.assertEquals(REMOVED, rcs.getChanges().get(0).getChangeType());
    }
}


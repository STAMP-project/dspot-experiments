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
package org.drools.core.event;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.event.kiebase.AfterFunctionRemovedEvent;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;
import org.kie.api.event.kiebase.AfterKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.AfterKiePackageAddedEvent;
import org.kie.api.event.kiebase.AfterKiePackageRemovedEvent;
import org.kie.api.event.kiebase.AfterProcessAddedEvent;
import org.kie.api.event.kiebase.AfterProcessRemovedEvent;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;
import org.kie.api.event.kiebase.AfterRuleRemovedEvent;
import org.kie.api.event.kiebase.BeforeFunctionRemovedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseLockedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageAddedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageRemovedEvent;
import org.kie.api.event.kiebase.BeforeProcessAddedEvent;
import org.kie.api.event.kiebase.BeforeProcessRemovedEvent;
import org.kie.api.event.kiebase.BeforeRuleAddedEvent;
import org.kie.api.event.kiebase.BeforeRuleRemovedEvent;
import org.kie.api.event.kiebase.KieBaseEventListener;


public class RuleBaseEventSupportTest {
    private InternalKnowledgeBase kBase;

    private RuleBaseEventSupportTest.TestRuleBaseListener listener1;

    private RuleBaseEventSupportTest.TestRuleBaseListener listener2;

    private InternalKnowledgePackage pkg;

    @Test
    public void testAddPackageEvents() throws Exception {
        Assert.assertEquals(0, listener1.getBeforePackageAdded());
        Assert.assertEquals(0, listener1.getAfterPackageAdded());
        Assert.assertEquals(0, listener2.getBeforePackageAdded());
        Assert.assertEquals(0, listener2.getAfterPackageAdded());
        Assert.assertEquals(0, listener1.getBeforeRuleAdded());
        Assert.assertEquals(0, listener1.getAfterRuleAdded());
        Assert.assertEquals(0, listener2.getBeforeRuleAdded());
        Assert.assertEquals(0, listener2.getAfterRuleAdded());
        this.kBase.addPackage(pkg);
        Assert.assertEquals(1, listener1.getBeforePackageAdded());
        Assert.assertEquals(1, listener1.getAfterPackageAdded());
        Assert.assertEquals(1, listener2.getBeforePackageAdded());
        Assert.assertEquals(1, listener2.getAfterPackageAdded());
        Assert.assertEquals(2, listener1.getBeforeRuleAdded());
        Assert.assertEquals(2, listener1.getAfterRuleAdded());
        Assert.assertEquals(2, listener2.getBeforeRuleAdded());
        Assert.assertEquals(2, listener2.getAfterRuleAdded());
    }

    @Test
    public void testRemovePackageEvents() throws Exception {
        this.kBase.addPackage(pkg);
        Assert.assertEquals(0, listener1.getBeforePackageRemoved());
        Assert.assertEquals(0, listener1.getAfterPackageRemoved());
        Assert.assertEquals(0, listener2.getBeforePackageRemoved());
        Assert.assertEquals(0, listener2.getAfterPackageRemoved());
        Assert.assertEquals(0, listener1.getBeforeRuleRemoved());
        Assert.assertEquals(0, listener1.getAfterRuleRemoved());
        Assert.assertEquals(0, listener2.getBeforeRuleRemoved());
        Assert.assertEquals(0, listener2.getAfterRuleRemoved());
        this.kBase.removeKiePackage("org.drools.test1");
        Assert.assertEquals(1, listener1.getBeforePackageRemoved());
        Assert.assertEquals(1, listener1.getAfterPackageRemoved());
        Assert.assertEquals(1, listener2.getBeforePackageRemoved());
        Assert.assertEquals(1, listener2.getAfterPackageRemoved());
        Assert.assertEquals(2, listener1.getBeforeRuleRemoved());
        Assert.assertEquals(2, listener1.getAfterRuleRemoved());
        Assert.assertEquals(2, listener2.getBeforeRuleRemoved());
        Assert.assertEquals(2, listener2.getAfterRuleRemoved());
    }

    public static class TestRuleBaseListener implements KieBaseEventListener {
        private String id;

        private int beforePackageAdded = 0;

        private int afterPackageAdded = 0;

        private int beforePackageRemoved = 0;

        private int afterPackageRemoved = 0;

        private int beforeRuleAdded = 0;

        private int afterRuleAdded = 0;

        private int beforeRuleRemoved = 0;

        private int afterRuleRemoved = 0;

        public TestRuleBaseListener() {
        }

        public TestRuleBaseListener(String id) {
            super();
            this.id = id;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            id = ((String) (in.readObject()));
            beforePackageAdded = in.readInt();
            afterPackageAdded = in.readInt();
            beforePackageRemoved = in.readInt();
            afterPackageRemoved = in.readInt();
            beforeRuleAdded = in.readInt();
            afterRuleAdded = in.readInt();
            beforeRuleRemoved = in.readInt();
            afterRuleRemoved = in.readInt();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(id);
            out.writeInt(beforePackageAdded);
            out.writeInt(afterPackageAdded);
            out.writeInt(beforePackageRemoved);
            out.writeInt(afterPackageRemoved);
            out.writeInt(beforeRuleAdded);
            out.writeInt(afterRuleAdded);
            out.writeInt(beforeRuleRemoved);
            out.writeInt(afterRuleRemoved);
        }

        public void afterKiePackageAdded(AfterKiePackageAddedEvent event) {
            // System.out.println( this.id + event );
            (this.afterPackageAdded)++;
        }

        public void beforeKiePackageAdded(BeforeKiePackageAddedEvent event) {
            // System.out.println( this.id + event );
            (this.beforePackageAdded)++;
        }

        protected int getAfterPackageAdded() {
            return afterPackageAdded;
        }

        protected int getBeforePackageAdded() {
            return beforePackageAdded;
        }

        protected String getId() {
            return id;
        }

        public void afterKiePackageRemoved(AfterKiePackageRemovedEvent event) {
            // System.out.println( this.id + event );
            (this.afterPackageRemoved)++;
        }

        public void beforeKiePackageRemoved(BeforeKiePackageRemovedEvent event) {
            // System.out.println( this.id + event );
            (this.beforePackageRemoved)++;
        }

        protected int getAfterPackageRemoved() {
            return afterPackageRemoved;
        }

        protected int getBeforePackageRemoved() {
            return beforePackageRemoved;
        }

        public int getAfterRuleAdded() {
            return afterRuleAdded;
        }

        public int getBeforeRuleAdded() {
            return beforeRuleAdded;
        }

        public void afterRuleAdded(AfterRuleAddedEvent event) {
            // System.out.println( this.id + event );
            (this.afterRuleAdded)++;
        }

        public void beforeRuleAdded(BeforeRuleAddedEvent event) {
            // System.out.println( this.id + event );
            (this.beforeRuleAdded)++;
        }

        public int getAfterRuleRemoved() {
            return afterRuleRemoved;
        }

        public int getBeforeRuleRemoved() {
            return beforeRuleRemoved;
        }

        public void afterRuleRemoved(AfterRuleRemovedEvent event) {
            // System.out.println( this.id + event );
            (this.afterRuleRemoved)++;
        }

        public void beforeRuleRemoved(BeforeRuleRemovedEvent event) {
            // System.out.println( this.id + event );
            (this.beforeRuleRemoved)++;
        }

        public void afterFunctionRemoved(AfterFunctionRemovedEvent event) {
            // TODO Auto-generated method stub
        }

        public void afterKieBaseLocked(AfterKieBaseLockedEvent event) {
            // TODO Auto-generated method stub
        }

        public void afterKieBaseUnlocked(AfterKieBaseUnlockedEvent event) {
            // TODO Auto-generated method stub
        }

        public void beforeFunctionRemoved(BeforeFunctionRemovedEvent event) {
            // TODO Auto-generated method stub
        }

        public void beforeKieBaseLocked(BeforeKieBaseLockedEvent event) {
            // TODO Auto-generated method stub
        }

        public void beforeKieBaseUnlocked(BeforeKieBaseUnlockedEvent event) {
            // TODO Auto-generated method stub
        }

        public void beforeProcessAdded(BeforeProcessAddedEvent event) {
            // TODO Auto-generated method stub
        }

        public void afterProcessAdded(AfterProcessAddedEvent event) {
            // TODO Auto-generated method stub
        }

        public void beforeProcessRemoved(BeforeProcessRemovedEvent event) {
            // TODO Auto-generated method stub
        }

        public void afterProcessRemoved(AfterProcessRemovedEvent event) {
            // TODO Auto-generated method stub
        }
    }
}


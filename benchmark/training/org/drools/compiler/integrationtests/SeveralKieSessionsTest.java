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
package org.drools.compiler.integrationtests;


import KieServices.Factory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


/**
 * Tests evaluation of a backward chaining family relationships example using
 * several KieSessions.
 */
public class SeveralKieSessionsTest {
    // DROOLS-145
    private static final String PACKAGE = SeveralKieSessionsTest.class.getPackage().getName();

    private static final String PACKAGE_PATH = SeveralKieSessionsTest.PACKAGE.replaceAll("\\.", "/");

    private static final String DRL_FILE_NAME = "several_kie_sessions.drl";

    private ReleaseId kieModuleId;

    /**
     * Tests evaluation of a backward chaining family relationships example with
     * two KieSessions created from the same KieBase.
     *
     * KieSessions are constructed using different KieContainer instances.
     */
    @Test
    public void testFamilyWithTwoKieSessionsFromKieContainer() throws Exception {
        final KieServices ks = Factory.get();
        final KieContainer kieContainer = ks.newKieContainer(kieModuleId);
        final KieSession ksession = kieContainer.newKieSession();
        performTestAndDispose(ksession);
        final KieContainer kieContainerOther = ks.newKieContainer(kieModuleId);
        final KieSession ksessionOther = kieContainerOther.newKieSession();
        performTestAndDispose(ksessionOther);
    }

    /**
     * Static class to store results from the working memory.
     */
    public static class ListHolder implements Serializable {
        private static final long serialVersionUID = -3058814255413392428L;

        private List<String> things;

        private List<String> food;

        private List<String> exits;

        private List<String> manList;

        private List<String> personList;

        private List<String> parentList;

        private List<String> motherList;

        private List<String> fatherList;

        private List<String> grandparentList;

        private boolean grandmaBlessedAgeTriggered;

        ListHolder() {
            things = new ArrayList<String>();
            food = new ArrayList<String>();
            exits = new ArrayList<String>();
            manList = new ArrayList<String>();
            personList = new ArrayList<String>();
            parentList = new ArrayList<String>();
            motherList = new ArrayList<String>();
            fatherList = new ArrayList<String>();
            grandparentList = new ArrayList<String>();
            grandmaBlessedAgeTriggered = false;
        }

        public void setThings(List<String> things) {
            this.things = things;
        }

        public List<String> getThings() {
            return things;
        }

        public void setFood(List<String> food) {
            this.food = food;
        }

        public List<String> getFood() {
            return food;
        }

        public void setExits(List<String> exits) {
            this.exits = exits;
        }

        public List<String> getExits() {
            return exits;
        }

        public void setManList(List<String> manList) {
            this.manList = manList;
        }

        public List<String> getManList() {
            return manList;
        }

        public void setPersonList(List<String> PersonList) {
            personList = PersonList;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setParentList(List<String> parentList) {
            this.parentList = parentList;
        }

        public List<String> getParentList() {
            return parentList;
        }

        public void setMotherList(List<String> motherList) {
            this.motherList = motherList;
        }

        public List<String> getMotherList() {
            return motherList;
        }

        public void setGrandparentList(List<String> grandparentList) {
            this.grandparentList = grandparentList;
        }

        public List<String> getGrandparentList() {
            return grandparentList;
        }

        public void setGrandmaBlessedAgeTriggered(boolean grandmaBlessedAgeTriggered) {
            this.grandmaBlessedAgeTriggered = grandmaBlessedAgeTriggered;
        }

        public boolean isGrandmaBlessedAgeTriggered() {
            return grandmaBlessedAgeTriggered;
        }

        public void setFatherList(List<String> fatherList) {
            this.fatherList = fatherList;
        }

        public List<String> getFatherList() {
            return fatherList;
        }
    }
}


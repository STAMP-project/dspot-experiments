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
package org.drools.decisiontable.parser;


import Code.ACTION;
import Code.ACTIVATIONGROUP;
import Code.AGENDAGROUP;
import Code.AUTOFOCUS;
import Code.CALENDARS;
import Code.CONDITION;
import Code.DATEEFFECTIVE;
import Code.DATEEXPIRES;
import Code.DESCRIPTION;
import Code.DURATION;
import Code.LOCKONACTIVE;
import Code.METADATA;
import Code.NAME;
import Code.NOLOOP;
import Code.RULEFLOWGROUP;
import Code.SALIENCE;
import Code.TIMER;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ActionTypeTest {
    @Test
    public void testChooseActionType() {
        Map<Integer, ActionType> actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "C", 0, 1);
        ActionType type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(CONDITION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "CONDITION", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(CONDITION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "A", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(ACTION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "ACTION", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(ACTION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "N", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(NAME, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "NAME", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(NAME, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "I", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DESCRIPTION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "DESCRIPTION", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DESCRIPTION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "P", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(SALIENCE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "PRIORITY", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(SALIENCE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "D", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DURATION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "DURATION", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DURATION, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "T", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(TIMER, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "TIMER", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(TIMER, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "E", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(CALENDARS, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "CALENDARS", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(CALENDARS, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "U", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(NOLOOP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "NO-LOOP", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(NOLOOP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "L", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(LOCKONACTIVE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "LOCK-ON-ACTIVE", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(LOCKONACTIVE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "F", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(AUTOFOCUS, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "AUTO-FOCUS", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(AUTOFOCUS, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "X", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(ACTIVATIONGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "ACTIVATION-GROUP", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(ACTIVATIONGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "G", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(AGENDAGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "AGENDA-GROUP", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(AGENDAGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "R", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(RULEFLOWGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "RULEFLOW-GROUP", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(RULEFLOWGROUP, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "V", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DATEEFFECTIVE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "DATE-EFFECTIVE", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DATEEFFECTIVE, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "Z", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DATEEXPIRES, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "DATE-EXPIRES", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(DATEEXPIRES, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "@", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(METADATA, type.getCode());
        actionTypeMap = new HashMap<Integer, ActionType>();
        ActionType.addNewActionType(actionTypeMap, "METADATA", 0, 1);
        type = ((ActionType) (actionTypeMap.get(new Integer(0))));
        Assert.assertEquals(METADATA, type.getCode());
    }
}


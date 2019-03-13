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
package org.drools.core.factmodel;


import java.util.Calendar;
import java.util.Date;
import org.junit.Test;


/**
 *
 *
 * @version $Id$
 */
public class InstancesHashcodedTest {
    @Test
    public void testInstanceHashcodes() {
        ClassDefinition cd = new ClassDefinition("br.com.auster.TestClass2", null, new String[]{  });
        cd.addField(new FieldDefinition("cutDate", "java.util.Date", true));
        cd.addField(new FieldDefinition("dueDate", "java.util.Date", true));
        cd.addField(new FieldDefinition("issueDate", "java.util.Date", false));
        cd.addField(new FieldDefinition("cycleCode", "java.lang.String", false));
        ClassBuilder cb = new ClassBuilderFactory().getBeanClassBuilder();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date cut = cal.getTime();
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        Date d1 = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 15);
        Date d2 = cal.getTime();
        try {
            Class klass = build(cb, cd);
            Object o1 = klass.newInstance();
            cd.getField("cutDate").getFieldAccessor().setValue(o1, cut);
            cd.getField("dueDate").getFieldAccessor().setValue(o1, d1);
            Object o2 = klass.newInstance();
            cd.getField("cutDate").getFieldAccessor().setValue(o2, cut);
            cd.getField("dueDate").getFieldAccessor().setValue(o2, d2);
            // System.out.println(o1);
            // System.out.println(o1.hashCode());
            // System.out.println(o2);
            // System.out.println(o2.hashCode());
            // 
            // System.out.println(o1.equals(o2));
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
}


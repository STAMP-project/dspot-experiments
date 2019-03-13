/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia;


import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.NotSaved;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Transient;
import dev.morphia.query.FindOptions;
import dev.morphia.query.UpdateResults;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Scott Hernandez
 */
public class TestFrontPageExample extends TestBase {
    @Test
    public void testIt() throws Exception {
        getMorphia().map(TestFrontPageExample.Employee.class);
        getDs().save(new TestFrontPageExample.Employee("Mister", "GOD", null, 0));
        final TestFrontPageExample.Employee boss = getDs().find(TestFrontPageExample.Employee.class).field("manager").equal(null).find(new FindOptions().limit(1)).next();// get an employee without a manager

        Assert.assertNotNull(boss);
        final Key<TestFrontPageExample.Employee> key = getDs().save(new TestFrontPageExample.Employee("Scott", "Hernandez", getDs().getKey(boss), (150 * 1000)));
        Assert.assertNotNull(key);
        final UpdateResults res = getDs().update(boss, getDs().createUpdateOperations(TestFrontPageExample.Employee.class).addToSet("underlings", key));// add Scott as an employee of his manager

        Assert.assertNotNull(res);
        Assert.assertTrue("Should update existing document", res.getUpdatedExisting());
        Assert.assertEquals("Should update one document", 1, res.getUpdatedCount());
        final TestFrontPageExample.Employee scottsBoss = getDs().find(TestFrontPageExample.Employee.class).filter("underlings", key).find(new FindOptions().limit(1)).next();// get Scott's boss

        Assert.assertNotNull(scottsBoss);
        Assert.assertEquals(boss.id, scottsBoss.id);
    }

    @Entity("employees")
    private static class Employee {
        @Reference
        private final List<TestFrontPageExample.Employee> underlings = new ArrayList<TestFrontPageExample.Employee>();// refs are stored*, and loaded automatically


        private final transient boolean stored = true;// not @Transient, will be ignored by Serialization/GWT for example.


        @Id
        private ObjectId id;// auto-generated, if not set (see ObjectId)


        private String firstName;

        // Address address; // by default fields are @Embedded
        private String lastName;// value types are automatically persisted


        private Long salary;// only non-null values are stored


        private Key<TestFrontPageExample.Employee> manager;// references can be saved without automatic


        @Property("started")
        private Date startDate;// fields can be renamed


        @Property("left")
        private Date endDate;

        @Indexed
        private boolean active;// fields can be indexed for better performance


        @NotSaved
        private String readButNotStored;// fields can loaded, but not saved


        @Transient
        private int notStored;// fields can be ignored (no load/save)


        Employee() {
        }

        Employee(final String f, final String l, final Key<TestFrontPageExample.Employee> boss, final long sal) {
            firstName = f;
            lastName = l;
            manager = boss;
            salary = sal;
        }
    }
}


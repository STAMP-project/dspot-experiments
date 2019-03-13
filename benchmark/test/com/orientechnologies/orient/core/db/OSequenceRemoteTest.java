package com.orientechnologies.orient.core.db;


import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.server.AbstractRemoteTest;
import org.junit.Test;


/**
 * Created by Enrico Risa on 19/05/2017.
 */
public class OSequenceRemoteTest extends AbstractRemoteTest {
    ODatabaseDocument db;

    @Test
    public void shouldSequenceWithDefaultValueNoTx() {
        db.command("CREATE CLASS Person EXTENDS V");
        db.command("CREATE SEQUENCE personIdSequence TYPE ORDERED;");
        db.command("CREATE PROPERTY Person.id LONG (MANDATORY TRUE, default \"sequence(\'personIdSequence\').next()\");");
        db.command("CREATE INDEX Person.id ON Person (id) UNIQUE");
        db.getMetadata().reload();
        for (int i = 0; i < 10; i++) {
            OVertex person = db.newVertex("Person");
            person.setProperty("name", ("Foo" + i));
            person.save();
        }
        assertThat(db.countClass("Person")).isEqualTo(10);
    }

    @Test
    public void shouldSequenceWithDefaultValueTx() {
        db.command("CREATE CLASS Person EXTENDS V");
        db.command("CREATE SEQUENCE personIdSequence TYPE ORDERED;");
        db.command("CREATE PROPERTY Person.id LONG (MANDATORY TRUE, default \"sequence(\'personIdSequence\').next()\");");
        db.command("CREATE INDEX Person.id ON Person (id) UNIQUE");
        db.getMetadata().reload();
        db.begin();
        for (int i = 0; i < 10; i++) {
            OVertex person = db.newVertex("Person");
            person.setProperty("name", ("Foo" + i));
            person.save();
        }
        db.commit();
        assertThat(db.countClass("Person")).isEqualTo(10);
    }
}


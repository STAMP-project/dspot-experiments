package org.drools.model;


import Rule.Attribute.AGENDA_GROUP;
import Rule.Attribute.NO_LOOP;
import Rule.Attribute.SALIENCE;
import java.util.ArrayList;
import java.util.List;
import org.drools.model.datasources.DataSource;
import org.drools.model.engine.BruteForceEngine;
import org.drools.model.impl.DataSourceDefinitionImpl;
import org.junit.Assert;
import org.junit.Test;


public class FlowDSLTest {
    @Test
    public void testJoin() {
        DataSource<Person> persons = FlowDSL.storeOf(new Person("Mark", 37), new Person("Edson", 35), new Person("Mario", 40), new Person("Sofia", 3));
        // $mark: Person(name == "Mark") in entry-point "persons"
        // $older: Person(name != "Mark" && age > $mark.age) in entry-point "persons"
        List<String> list = new ArrayList<>();
        Variable<Person> markV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        Rule rule = FlowDSL.rule("join").attribute(SALIENCE, 10).attribute(AGENDA_GROUP, "myGroup").build(FlowDSL.expr(markV, ( mark) -> mark.getName().equals("Mark")), FlowDSL.expr(olderV, ( older) -> !(older.getName().equals("Mark"))), FlowDSL.expr(olderV, markV, ( older, mark) -> (older.getAge()) > (mark.getAge())), FlowDSL.on(olderV, markV).execute(( p1, p2) -> list.add((((p1.getName()) + " is older than ") + (p2.getName())))));
        new BruteForceEngine().bind("persons", persons).evaluate(rule);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("Mario is older than Mark", list.get(0));
        Assert.assertEquals("join", rule.getName());
        Assert.assertEquals(10, ((int) (rule.getAttribute(SALIENCE))));
        Assert.assertEquals("myGroup", rule.getAttribute(AGENDA_GROUP));
        Assert.assertEquals(false, rule.getAttribute(NO_LOOP));
    }

    @Test
    public void testJoinDifferentConstraintOrder() {
        DataSource<Person> persons = FlowDSL.storeOf(new Person("Mark", 37), new Person("Edson", 35), new Person("Mario", 40), new Person("Sofia", 3));
        // $mark: Person(name == "Mark") in entry-point "persons"
        // $older: Person(name != "Mark" && age > $mark.age) in entry-point "persons"
        Variable<Person> markV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        View view = FlowDSL.view(FlowDSL.expr(olderV, ( older) -> !(older.getName().equals("Mark"))), FlowDSL.expr(markV, ( mark) -> mark.getName().equals("Mark")), FlowDSL.expr(markV, olderV, ( mark, older) -> (mark.getAge()) < (older.getAge())));
        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        Assert.assertEquals(1, result.size());
        TupleHandle tuple = result.get(0);
        Assert.assertEquals("Mark", tuple.get(markV).getName());
        Assert.assertEquals("Mario", tuple.get(olderV).getName());
    }

    @Test
    public void testOr() {
        DataSource<Person> persons = FlowDSL.storeOf(new Person("Mark", 37), new Person("Edson", 35), new Person("Mario", 40), new Person("Sofia", 3));
        Variable<Person> markV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        Variable<Person> otherV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        View view = FlowDSL.view(FlowDSL.expr(markV, ( mark) -> mark.getName().equals("Mark")), FlowDSL.or(FlowDSL.expr(otherV, markV, ( other, mark) -> (other.getAge()) > (mark.getAge())), FlowDSL.expr(otherV, markV, ( other, mark) -> (other.getName().compareToIgnoreCase(mark.getName())) > 0)));
        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        Assert.assertEquals(2, result.size());
        TupleHandle tuple = result.get(0);
        Assert.assertEquals("Mark", tuple.get(markV).getName());
        Assert.assertEquals("Mario", tuple.get(otherV).getName());
        tuple = result.get(1);
        Assert.assertEquals("Mark", tuple.get(markV).getName());
        Assert.assertEquals("Sofia", tuple.get(otherV).getName());
    }

    @Test
    public void testNot() {
        DataSource<Person> persons = FlowDSL.storeOf(new Person("Mark", 37), new Person("Edson", 35), new Person("Mario", 40), new Person("Sofia", 3));
        // $oldest: Person()
        // not( Person(age > $oldest.age) )
        Variable<Person> oldestV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        Variable<Person> otherV = FlowDSL.declarationOf(Person.class, new DataSourceDefinitionImpl("persons", false));
        View view = FlowDSL.view(FlowDSL.not(otherV, oldestV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())));
        List<TupleHandle> result = new BruteForceEngine().bind("persons", persons).evaluate(view);
        Assert.assertEquals(1, result.size());
        TupleHandle tuple = result.get(0);
        Assert.assertEquals("Mario", tuple.get(oldestV).getName());
    }
}


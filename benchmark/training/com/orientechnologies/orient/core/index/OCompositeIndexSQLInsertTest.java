package com.orientechnologies.orient.core.index;


import OType.EMBEDDEDLIST;
import OType.INTEGER;
import OType.STRING;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OCompositeIndexSQLInsertTest {
    public ODatabaseDocument db;

    @Test
    public void testIndexInsert() {
        db.command(new OCommandSQL("insert into index:books (key, rid) values ([\"Donald Knuth\", \"The Art of Computer Programming\", 1968], #12:0)")).execute();
    }

    @Test
    public void testIndexOfStrings() {
        db.command(new OCommandSQL("CREATE INDEX test unique string,string")).execute();
        db.command(new OCommandSQL("insert into index:test (key, rid) values (['a','b'], #12:0)")).execute();
    }

    @Test
    public void testCompositeIndexWithRangeAndContains() {
        final OSchema schema = db.getMetadata().getSchema();
        OClass clazz = schema.createClass("CompositeIndexWithRangeAndConditions");
        clazz.createProperty("id", INTEGER);
        clazz.createProperty("bar", INTEGER);
        clazz.createProperty("tags", EMBEDDEDLIST, STRING);
        clazz.createProperty("name", STRING);
        db.command(new OCommandSQL("create index CompositeIndexWithRangeAndConditions_id_tags_name on CompositeIndexWithRangeAndConditions (id, tags, name) NOTUNIQUE")).execute();
        db.command(new OCommandSQL("insert into CompositeIndexWithRangeAndConditions set id = 1, tags = [\"green\",\"yellow\"] , name = \"Foo\", bar = 1")).execute();
        db.command(new OCommandSQL("insert into CompositeIndexWithRangeAndConditions set id = 1, tags = [\"blue\",\"black\"] , name = \"Foo\", bar = 14")).execute();
        db.command(new OCommandSQL("insert into CompositeIndexWithRangeAndConditions set id = 1, tags = [\"white\"] , name = \"Foo\"")).execute();
        db.command(new OCommandSQL("insert into CompositeIndexWithRangeAndConditions set id = 1, tags = [\"green\",\"yellow\"], name = \"Foo1\", bar = 14")).execute();
        List<ODocument> r = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from CompositeIndexWithRangeAndConditions where id > 0 and bar = 1"));
        Assert.assertEquals(1, r.size());
        List<ODocument> r1 = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from CompositeIndexWithRangeAndConditions where id = 1 and tags CONTAINS \"white\""));
        Assert.assertEquals(r1.size(), 1);
        List<ODocument> r2 = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from CompositeIndexWithRangeAndConditions where id > 0 and tags CONTAINS \"white\""));
        Assert.assertEquals(r2.size(), 1);
        List<ODocument> r3 = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from CompositeIndexWithRangeAndConditions where id > 0 and bar = 1"));
        Assert.assertEquals(r3.size(), 1);
        List<ODocument> r4 = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>("select from CompositeIndexWithRangeAndConditions where tags CONTAINS \"white\" and id > 0"));
        Assert.assertEquals(r4.size(), 1);
    }
}


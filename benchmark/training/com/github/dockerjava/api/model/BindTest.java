package com.github.dockerjava.api.model;


import AccessMode.DEFAULT;
import PropagationMode.DEFAULT_MODE;
import PropagationMode.PRIVATE;
import PropagationMode.SHARED;
import PropagationMode.SLAVE;
import SELContext.none;
import SELContext.shared;
import SELContext.single;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BindTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void parseUsingDefaultAccessMode() {
        Bind bind = Bind.parse("/host:/container");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(DEFAULT));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseReadWrite() {
        Bind bind = Bind.parse("/host:/container:rw");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseReadWriteNoCopy() {
        Bind bind = Bind.parse("/host:/container:rw,nocopy");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Is.is(true));
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseReadWriteShared() {
        Bind bind = Bind.parse("/host:/container:rw,shared");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(SHARED));
    }

    @Test
    public void parseReadWriteSlave() {
        Bind bind = Bind.parse("/host:/container:rw,slave");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(SLAVE));
    }

    @Test
    public void parseReadWritePrivate() {
        Bind bind = Bind.parse("/host:/container:rw,private");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(PRIVATE));
    }

    @Test
    public void parseReadOnly() {
        Bind bind = Bind.parse("/host:/container:ro");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.ro));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(none));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseSELOnly() {
        Bind bind = Bind.parse("/host:/container:Z");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(DEFAULT));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(single));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
        bind = Bind.parse("/host:/container:z");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(DEFAULT));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(shared));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseReadWriteSEL() {
        Bind bind = Bind.parse("/host:/container:rw,Z");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.rw));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(single));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseReadOnlySEL() {
        Bind bind = Bind.parse("/host:/container:ro,z");
        MatcherAssert.assertThat(bind.getPath(), Is.is("/host"));
        MatcherAssert.assertThat(bind.getVolume().getPath(), Is.is("/container"));
        MatcherAssert.assertThat(bind.getAccessMode(), Is.is(AccessMode.ro));
        MatcherAssert.assertThat(bind.getSecMode(), Is.is(shared));
        MatcherAssert.assertThat(bind.getNoCopy(), Matchers.nullValue());
        MatcherAssert.assertThat(bind.getPropagationMode(), Is.is(DEFAULT_MODE));
    }

    @Test
    public void parseInvalidAccessMode() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Bind");
        Bind.parse("/host:/container:xx");
    }

    @Test
    public void parseInvalidInput() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Bind 'nonsense'");
        Bind.parse("nonsense");
    }

    @Test
    public void parseNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Error parsing Bind 'null'");
        Bind.parse(null);
    }

    @Test
    public void toStringReadOnly() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:ro").toString(), Is.is("/host:/container:ro"));
    }

    @Test
    public void toStringReadWrite() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw").toString(), Is.is("/host:/container:rw"));
    }

    @Test
    public void toStringReadWriteNoCopy() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw,nocopy").toString(), Is.is("/host:/container:rw,nocopy"));
    }

    @Test
    public void toStringReadWriteShared() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw,shared").toString(), Is.is("/host:/container:rw,shared"));
    }

    @Test
    public void toStringReadWriteSlave() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw,slave").toString(), Is.is("/host:/container:rw,slave"));
    }

    @Test
    public void toStringReadWritePrivate() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw,private").toString(), Is.is("/host:/container:rw,private"));
    }

    @Test
    public void toStringDefaultAccessMode() {
        MatcherAssert.assertThat(Bind.parse("/host:/container").toString(), Is.is("/host:/container:rw"));
    }

    @Test
    public void toStringReadOnlySEL() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:ro,Z").toString(), Is.is("/host:/container:ro,Z"));
    }

    @Test
    public void toStringReadWriteSEL() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:rw,z").toString(), Is.is("/host:/container:rw,z"));
    }

    @Test
    public void toStringDefaultSEL() {
        MatcherAssert.assertThat(Bind.parse("/host:/container:Z").toString(), Is.is("/host:/container:rw,Z"));
    }
}


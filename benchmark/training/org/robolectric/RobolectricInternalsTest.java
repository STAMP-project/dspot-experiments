package org.robolectric;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.internal.Instrument;
import org.robolectric.internal.SandboxTestRunner;
import org.robolectric.internal.bytecode.SandboxConfig;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.util.ReflectionHelpers.ClassParameter;


@SandboxConfig(shadows = { RobolectricInternalsTest.ShadowConstructors.class })
@RunWith(SandboxTestRunner.class)
public class RobolectricInternalsTest {
    private static final String PARAM1 = "param1";

    private static final Byte PARAM2 = ((byte) (24));

    private static final Long PARAM3 = ((long) (10122345));

    @Test
    public void getConstructor_withNoParams() {
        RobolectricInternalsTest.Constructors a = new RobolectricInternalsTest.Constructors();
        RobolectricInternalsTest.ShadowConstructors sa = RobolectricInternalsTest.shadowOf(a);
        assertThat(a.constructorCalled).isFalse();
        assertThat(sa.shadowConstructorCalled).isTrue();
        Shadow.invokeConstructor(RobolectricInternalsTest.Constructors.class, a);
        assertThat(a.constructorCalled).isTrue();
    }

    @Test
    public void getConstructor_withOneClassParam() {
        RobolectricInternalsTest.Constructors a = new RobolectricInternalsTest.Constructors(RobolectricInternalsTest.PARAM1);
        RobolectricInternalsTest.ShadowConstructors sa = RobolectricInternalsTest.shadowOf(a);
        assertThat(a.param11).isNull();
        assertThat(sa.shadowParam11).isEqualTo(RobolectricInternalsTest.PARAM1);
        Shadow.invokeConstructor(RobolectricInternalsTest.Constructors.class, a, ClassParameter.from(String.class, RobolectricInternalsTest.PARAM1));
        assertThat(a.param11).isEqualTo(RobolectricInternalsTest.PARAM1);
    }

    @Test
    public void getConstructor_withTwoClassParams() {
        RobolectricInternalsTest.Constructors a = new RobolectricInternalsTest.Constructors(RobolectricInternalsTest.PARAM1, RobolectricInternalsTest.PARAM2);
        RobolectricInternalsTest.ShadowConstructors sa = RobolectricInternalsTest.shadowOf(a);
        assertThat(a.param21).isNull();
        assertThat(a.param22).isNull();
        assertThat(sa.shadowParam21).isEqualTo(RobolectricInternalsTest.PARAM1);
        assertThat(sa.shadowParam22).isEqualTo(RobolectricInternalsTest.PARAM2);
        Shadow.invokeConstructor(RobolectricInternalsTest.Constructors.class, a, ClassParameter.from(String.class, RobolectricInternalsTest.PARAM1), ClassParameter.from(Byte.class, RobolectricInternalsTest.PARAM2));
        assertThat(a.param21).isEqualTo(RobolectricInternalsTest.PARAM1);
        assertThat(a.param22).isEqualTo(RobolectricInternalsTest.PARAM2);
    }

    @Test
    public void getConstructor_withThreeClassParams() {
        RobolectricInternalsTest.Constructors a = new RobolectricInternalsTest.Constructors(RobolectricInternalsTest.PARAM1, RobolectricInternalsTest.PARAM2, RobolectricInternalsTest.PARAM3);
        RobolectricInternalsTest.ShadowConstructors sa = RobolectricInternalsTest.shadowOf(a);
        assertThat(a.param31).isNull();
        assertThat(a.param32).isNull();
        assertThat(a.param33).isNull();
        assertThat(sa.shadowParam31).isEqualTo(RobolectricInternalsTest.PARAM1);
        assertThat(sa.shadowParam32).isEqualTo(RobolectricInternalsTest.PARAM2);
        assertThat(sa.shadowParam33).isEqualTo(RobolectricInternalsTest.PARAM3);
        Shadow.invokeConstructor(RobolectricInternalsTest.Constructors.class, a, ClassParameter.from(String.class, RobolectricInternalsTest.PARAM1), ClassParameter.from(Byte.class, RobolectricInternalsTest.PARAM2), ClassParameter.from(Long.class, RobolectricInternalsTest.PARAM3));
        assertThat(a.param31).isEqualTo(RobolectricInternalsTest.PARAM1);
        assertThat(a.param32).isEqualTo(RobolectricInternalsTest.PARAM2);
        assertThat(a.param33).isEqualTo(RobolectricInternalsTest.PARAM3);
    }

    @Instrument
    public static class Constructors {
        public boolean constructorCalled = false;

        public String param11 = null;

        public String param21 = null;

        public Byte param22 = null;

        public String param31 = null;

        public Byte param32 = null;

        public Long param33 = null;

        public Constructors() {
            constructorCalled = true;
        }

        public Constructors(String param) {
            param11 = param;
        }

        public Constructors(String param1, Byte param2) {
            param21 = param1;
            param22 = param2;
        }

        public Constructors(String param1, Byte param2, Long param3) {
            param31 = param1;
            param32 = param2;
            param33 = param3;
        }
    }

    @Implements(RobolectricInternalsTest.Constructors.class)
    public static class ShadowConstructors {
        public boolean shadowConstructorCalled = false;

        public String shadowParam11 = null;

        public String shadowParam21 = null;

        public Byte shadowParam22 = null;

        public String shadowParam31 = null;

        public Byte shadowParam32 = null;

        public Long shadowParam33 = null;

        @Implementation
        protected void __constructor__() {
            shadowConstructorCalled = true;
        }

        @Implementation
        protected void __constructor__(String param) {
            shadowParam11 = param;
        }

        @Implementation
        protected void __constructor__(String param1, Byte param2) {
            shadowParam21 = param1;
            shadowParam22 = param2;
        }

        @Implementation
        protected void __constructor__(String param1, Byte param2, Long param3) {
            shadowParam31 = param1;
            shadowParam32 = param2;
            shadowParam33 = param3;
        }
    }
}


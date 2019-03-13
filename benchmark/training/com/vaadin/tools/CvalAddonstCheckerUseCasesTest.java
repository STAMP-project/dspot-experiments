package com.vaadin.tools;


import org.junit.Test;


/**
 * Tests for Use Cases
 */
public class CvalAddonstCheckerUseCasesTest {
    enum License {

        NONE,
        EVAL,
        INVALID,
        REAL,
        EVAL_EXPIRED,
        REAL_EXPIRED;}

    enum Version {

        AGPL,
        CVAL;}

    enum Validated {

        YES,
        NO,
        OLD_KEY;}

    enum Network {

        ON,
        OFF;}

    enum Compile {

        YES,
        NO;}

    enum Cached {

        YES,
        NO;}

    enum Message {

        AGPL("AGPL"),
        VALID(">.* valid"),
        INVALID("not valid"),
        NO_LICENSE("not found"),
        NO_VALIDATED("has not been validated"),
        EXPIRED("has expired"),
        EVALUATION("evaluation");
        String msg;

        Message(String s) {
            msg = s;
        }
    }

    /* TODO: Use more descriptive test names */
    @Test
    public void testUseCase1() throws Exception {
        useCase(1, CvalAddonstCheckerUseCasesTest.License.NONE, CvalAddonstCheckerUseCasesTest.Version.AGPL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.AGPL);
    }

    @Test
    public void testUseCase2() throws Exception {
        useCase(2, CvalAddonstCheckerUseCasesTest.License.NONE, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.NO_LICENSE);
    }

    @Test
    public void testUseCase3() throws Exception {
        useCase(3, CvalAddonstCheckerUseCasesTest.License.NONE, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.NO_LICENSE);
    }

    @Test
    public void testUseCase4() throws Exception {
        useCase(4, CvalAddonstCheckerUseCasesTest.License.EVAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.EVALUATION);
    }

    @Test
    public void testUseCase5() throws Exception {
        useCase(5, CvalAddonstCheckerUseCasesTest.License.INVALID, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.NO_VALIDATED);
    }

    @Test
    public void testUseCase6() throws Exception {
        useCase(6, CvalAddonstCheckerUseCasesTest.License.INVALID, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.INVALID);
    }

    @Test
    public void testUseCase7() throws Exception {
        useCase(7, CvalAddonstCheckerUseCasesTest.License.REAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.VALID);
    }

    @Test
    public void testUseCase8() throws Exception {
        useCase(8, CvalAddonstCheckerUseCasesTest.License.REAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.NO_VALIDATED);
    }

    @Test
    public void testUseCase9() throws Exception {
        useCase(9, CvalAddonstCheckerUseCasesTest.License.REAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.YES, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.VALID);
    }

    @Test
    public void testUseCase10() throws Exception {
        useCase(10, CvalAddonstCheckerUseCasesTest.License.EVAL_EXPIRED, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.EXPIRED);
    }

    @Test
    public void testUseCase11() throws Exception {
        useCase(11, CvalAddonstCheckerUseCasesTest.License.EVAL_EXPIRED, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.YES, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.EXPIRED);
    }

    @Test
    public void testUseCase12() throws Exception {
        useCase(12, CvalAddonstCheckerUseCasesTest.License.REAL_EXPIRED, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.YES, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.EXPIRED);
    }

    @Test
    public void testUseCase13() throws Exception {
        useCase(13, CvalAddonstCheckerUseCasesTest.License.REAL_EXPIRED, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.NO, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.EXPIRED);
    }

    @Test
    public void testUseCase14() throws Exception {
        useCase(14, CvalAddonstCheckerUseCasesTest.License.INVALID, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.OLD_KEY, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.NO, CvalAddonstCheckerUseCasesTest.Message.NO_VALIDATED);
    }

    @Test
    public void testMultipleLicenseUseCase15() throws Exception {
        CvalCheckerTest.addLicensedJarToClasspath("test.foo", CvalAddonsChecker.VAADIN_CVAL);
        System.setProperty(CvalChecker.computeLicenseName("test.foo"), CvalCheckerTest.VALID_KEY);
        useCase(15, CvalAddonstCheckerUseCasesTest.License.REAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.YES, CvalAddonstCheckerUseCasesTest.Network.OFF, CvalAddonstCheckerUseCasesTest.Compile.YES, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.NO_VALIDATED);
    }

    @Test
    public void testMultipleLicenseUseCase16() throws Exception {
        CvalCheckerTest.addLicensedJarToClasspath("test.foo", CvalAddonsChecker.VAADIN_CVAL);
        System.setProperty(CvalChecker.computeLicenseName("test.foo"), CvalCheckerTest.VALID_KEY);
        useCase(16, CvalAddonstCheckerUseCasesTest.License.REAL, CvalAddonstCheckerUseCasesTest.Version.CVAL, CvalAddonstCheckerUseCasesTest.Validated.YES, CvalAddonstCheckerUseCasesTest.Network.ON, CvalAddonstCheckerUseCasesTest.Compile.NO, CvalAddonstCheckerUseCasesTest.Cached.YES, CvalAddonstCheckerUseCasesTest.Message.INVALID);
    }
}


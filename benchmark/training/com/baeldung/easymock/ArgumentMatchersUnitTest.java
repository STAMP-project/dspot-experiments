package com.baeldung.easymock;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ArgumentMatchersUnitTest {
    private IUserService userService = mock(IUserService.class);

    // ====================== equals
    @Test
    public void givenUserService_whenAddNewUser_thenOK() {
        expect(userService.addUser(eq(new User()))).andReturn(true);
        replay(userService);
        boolean result = userService.addUser(new User());
        verify(userService);
        Assert.assertTrue(result);
    }

    // ================ same
    @Test
    public void givenUserService_whenAddSpecificUser_thenOK() {
        User user = new User();
        expect(userService.addUser(same(user))).andReturn(true);
        replay(userService);
        boolean result = userService.addUser(user);
        verify(userService);
        Assert.assertTrue(result);
    }

    // ============= anyX
    @Test
    public void givenUserService_whenSearchForUserByEmail_thenFound() {
        expect(userService.findByEmail(anyString())).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // ================= isA
    @Test
    public void givenUserService_whenAddUser_thenOK() {
        expect(userService.addUser(isA(User.class))).andReturn(true);
        replay(userService);
        boolean result = userService.addUser(new User());
        verify(userService);
        Assert.assertTrue(result);
    }

    // =================== null, not null
    @Test
    public void givenUserService_whenAddNull_thenFail() {
        expect(userService.addUser(isNull())).andReturn(false);
        replay(userService);
        boolean result = userService.addUser(null);
        verify(userService);
        Assert.assertFalse(result);
    }

    @Test
    public void givenUserService_whenAddNotNull_thenOK() {
        expect(userService.addUser(notNull())).andReturn(true);
        replay(userService);
        boolean result = userService.addUser(new User());
        verify(userService);
        Assert.assertTrue(result);
    }

    // number less,great
    @Test
    public void givenUserService_whenSearchForUserByAgeLessThan_thenFound() {
        expect(userService.findByAge(lt(100.0))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByAge(20);
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void givenUserService_whenSearchForUserByAgeGreaterThan_thenFound() {
        expect(userService.findByAge(geq(10.0))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByAge(20);
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // =============== string
    // =============== start
    @Test
    public void givenUserService_whenSearchForUserByEmailStartsWith_thenFound() {
        expect(userService.findByEmail(startsWith("test"))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // ==================end
    @Test
    public void givenUserService_whenSearchForUserByEmailEndsWith_thenFound() {
        expect(userService.findByEmail(endsWith(".com"))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // =================contain
    @Test
    public void givenUserService_whenSearchForUserByEmailContains_thenFound() {
        expect(userService.findByEmail(contains("@"))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // ==================matches
    @Test
    public void givenUserService_whenSearchForUserByEmailMatches_thenFound() {
        expect(userService.findByEmail(matches(".+\\@.+\\..+"))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // ================== combine and, or, not
    @Test
    public void givenUserService_whenSearchForUserByAgeRange_thenFound() {
        expect(userService.findByAge(and(gt(10.0), lt(100.0)))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByAge(20);
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void givenUserService_whenSearchForUserByEmailNotEndsWith_thenFound() {
        expect(userService.findByEmail(not(endsWith(".com")))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.org");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }

    // ================ custom matcher
    @Test
    public void givenUserService_whenSearchForUserByEmailCharCount_thenFound() {
        expect(userService.findByEmail(ArgumentMatchersUnitTest.minCharCount(5))).andReturn(Collections.emptyList());
        replay(userService);
        List<User> result = userService.findByEmail("test@example.com");
        verify(userService);
        Assert.assertEquals(0, result.size());
    }
}


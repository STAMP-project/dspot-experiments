@Test(timeout = 10000)
public void testMergeCharList() throws Exception {
        MergeResult<Character> result;
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("a"), AmplDefaultDiffManagerTest.toCharacters(""), AmplDefaultDiffManagerTest.toCharacters("b"), null);
        int o_testMergeCharList__9 = result.getLog().getLogs(LogLevel.ERROR).size();
        Assert.assertEquals(1, ((int) (o_testMergeCharList__9)));
        List<Character> o_testMergeCharList__12 = AmplDefaultDiffManagerTest.toCharacters("b");
        Assert.assertTrue(o_testMergeCharList__12.contains('b'));
        result.getMerged();
        result = this.mocker.getComponentUnderTest().merge(AmplDefaultDiffManagerTest.toCharacters("bc"), AmplDefaultDiffManagerTest.toCharacters("abc"), AmplDefaultDiffManagerTest.toCharacters("bc"), null);
        int o_testMergeCharList__21 = result.getLog().getLogs(LogLevel.ERROR).size();
        Assert.assertEquals(0, ((int) (o_testMergeCharList__21)));
        List<Character> o_testMergeCharList__24 = AmplDefaultDiffManagerTest.toCharacters("abc");
        Assert.assertTrue(o_testMergeCharList__24.contains('a'));
        Assert.assertTrue(o_testMergeCharList__24.contains('b'));
        Assert.assertTrue(o_testMergeCharList__24.contains('c'));
        result.getMerged();
    }

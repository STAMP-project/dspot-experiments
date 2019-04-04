@Test(timeout = 10000)
public void testChar_add45986_literalMutationChar46277() throws Exception {
  try {
    new ToStringBuilder(this.base).append('A').toString();
  } catch (final UnsupportedOperationException e) {
  }
  new ToStringBuilder(this.base).append("a", 'A').toString();
  ToStringBuilder o_testChar_add45986__10 = 
    new ToStringBuilder(this.base).append("a", '\n').append("b", 'B');
  Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\",", 
    ((StringBuffer) o_testChar_add45986__10.getStringBuffer()).toString());
  Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
  Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
  new ToStringBuilder(this.base).append("a", 'A').append("b", 'B').toString();
  Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
  Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
  Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
}

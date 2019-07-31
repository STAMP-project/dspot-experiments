# DCI

Comparison of amplified test methods (on the left) that detect behavioral change and developer test method (on the right).

```java
@Test(timeout = 10000)
public void readMulti_literalMutationNumber3() {
    BoundedReader mr = new BoundedReader(sr, 0);
    char[] cbuf = new char[4];
    for (int i = 0; i < (cbuf.length); i++) {
        cbuf[i] = 'X';
    }
    final int read = mr.read(cbuf, 0, 4);
    Assert.assertEquals(0, ((int) (read)));
}      
```

```java
@Test(timeout = 5000)
public void testReadBytesEOF() {
   BoundedReader mr = new BoundedReader( sr, 3 );
   BufferedReader br = new BufferedReader( mr );
   br.readLine();
   br.readLine();
}
```
# junit-io

Support for repeatable junit tests involving interactions with the file system.

## Motivation: Idempotence

To the degree possible, each unit test is to stand on its own--be independent of other tests, make few assumptions about its environment, and so on. Among these, there's this notion of idempotence: it shouldn't matter if you run a test once or (even by mistake) twice in succession; the test should pass both times.

Arranging for a test that interacts with the file system to be idempotent is not difficult. But it can be tedious. I've been dragging around *some* version of these few files from project to project to for some while now.

## Setup

This is primarily targeted for use with maven--although other build systems too *can* access and use local maven repos. Since this package is not published on any public maven repo, you'll have to build and install it yourself.

Clone this (git) repo, then

> mvn package install

This installs the package in your local maven repo (typically `$HOME/.m2`). To use it in another project you do something like this in that other project's pom file:

```
 <dependencies>
   
   ..
   <dependency>
     <groupId>com.gnahraf</groupId>
     <artifactId>junit-io</artifactId>
     <version>0.0.1</version>
     <scope>test</scope>
   </dependency>
   
   
 </dependencies>
```

Note the `<scope>`**test**`</scope>` element (*users* of your distribution will **not**  depend on this package). Also note, the Java compiler settings in `pom.xml` are set to version `1.8`. You can probably dial that down it you need to.

## Example Use

Suppose we're testing an object store that persists its state in a directory. Then its unit test class might look something like this. 

```
class ObjectManagerTest extends com.gnahraf.test.IoTestCase {   //          (1)
  
  
  @Test
  public void test100() {
    ObjectManager<Mock> store = makeStore(new Object() { });   //           (2)
    
    HashMap<String, Mock> book = new HashMap<>();
    
    for (int i = 0; i < 100; ++i) {
      Mock item = new Mock();
      item.c = i;
      String id = store.write(item);
      book.put(id, item);
    }
    
    for (String id : book.keySet()) {
      Mock obj = store.read(id);
      assertEquals(book.get(id), obj);
    }
  }
  
  ObjectManager<Mock> makeStore(Object methObj) {
    File dir = getMethodOutputFilepath(methObj);               //           (3)
    return makeStore(dir);
  }
  
  
  ObjectManager<Mock> makeStore(File dir) {
    // create new directory, make and return a new store..
    ..
  }

}

```

A few things to note:

0. Test output files are maintained under the `target/test-outputs` directory. There's one nested subdirectory per class, then one subdirectory nested per test method, and then one nested file or subdirectory per run of the test method.
1. The `new Object() { }` hack. This is a way to pass a test method's name without hardcoding it. (If you rename the method `test100` to `testHundred` everything still works.) Yes, an annotation solution would be more elegant, but this author is lazy.
2. `getMethodOutputFilepath(methObj)` returns a new abstract file path that does not yet exist. If you create a file or directory there and invoke this method again, it'll again return a *new* path that does not yet exist; otherwise, it returns the same path.

Sources are attached during the install. In IDEs like Eclipse this means code completion suggestions are accompanied with on-the-fly generated javadoc comments. 




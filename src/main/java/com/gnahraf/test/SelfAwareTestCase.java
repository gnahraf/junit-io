/**
 * Copyright 2019 Babak Farhang
 */
package com.gnahraf.test;

import java.lang.reflect.Method;




/**
 * Provides a programmatic way (a hack) to return a method's name from code inside it.
 * Use this also to determine test output directories.
 * <p/>
 * (No, it's not sentient.)
 */
public class SelfAwareTestCase {
  

  /**
   * Returns the method name via a hack. Example usage:
   * <pre>
   *   <tt>System.out.println("I'm in " + method(new Object() { }));</tt>
   * </pre>
   * 
   * @param methodLabel  an instance of a class defined in the method you're
   *                           calling from, simplest of which is just <tt>new Object() { }</tt>
   */
  public String method(Object methodLabel) {
    Class<?> clazz = methodLabel.getClass();
    Method testMethod = clazz.getEnclosingMethod();
    if (testMethod == null)
      throw new IllegalArgumentException(
          "argument type not defined inside test case method: " + methodLabel);
    
    // the following check proved unnecessarily restrictive..
    
//    if (!testMethod.getDeclaringClass().equals(getClass()))
//      throw new IllegalArgumentException(
//          "argument type not defined in test case class: " + innerMethodObject);
    
    return testMethod.getName();
  }
  
  
  /**
   * Checks whether the given system property is enabled.
   * Shorthand for {@linkplain #checkEnabled(String, Object, boolean)
   * checkEnabled(sysProperty, null, false)}.
   */
  protected boolean checkEnabled(String sysProperty) {
    return checkEnabled(sysProperty, null, false);
  }
  
  /**
   * Checks whether the given system property is enabled.
   * Shorthand for {@linkplain #checkEnabled(String, Object, boolean)
   * checkEnabled(sysProperty, methodLabel, false)}.
   */
  protected boolean checkEnabled(String sysProperty, Object methodLabel) {
    return checkEnabled(sysProperty, methodLabel, false);
  }

  /**
   * Checks whether the given system property is enabled. Useful for default short-circuiting long running
   * tests.
   * 
   * @param sysProperty
   * @param methodLabel optional method label (see {@linkplain #method(Object)}). If not null, then
   *      before returning <tt>false</tt> a short message to std out directs user how to enable.
   * @param includeTest if <tt>true</tt> then if <tt>-Dtest=<em>TestClass</em></tt> is set, it counts
   *    as the property being set
   *      
   * @return <tt>true</tt> if the property is enabled or if <tt>includeTest</tt> is <tt>true</tt>
   *    and this unit test is being run explicitly
   */
  protected boolean checkEnabled(String sysProperty, Object methodLabel, boolean includeTest) {
    
    boolean enabled =
        "true".equalsIgnoreCase(System.getProperty(sysProperty)) ||
        includeTest && getClass().getSimpleName().equals(System.getProperty("test"));
    
    if (!enabled && methodLabel != null) {
      String msg = "Skipping " + method(methodLabel) + ". To activate run with -D" + sysProperty + "=true";
      if (includeTest)
        msg += (" or -Dtest=" + getClass().getSimpleName());
      System.out.println(msg);
    }
    
    return enabled;
  }

}
/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client;

import com.google.api.client.auth.oauth.OAuthParametersTest;
import com.google.api.client.http.GenericUrlTest;
import com.google.api.client.util.ArrayMapTest;
import com.google.api.client.util.DateTimeTest;

import junit.framework.TestSuite;

/**
 * All tests.
 * 
 * @author Yaniv Inbar
 */
public class AllTests extends TestSuite {

  public static TestSuite suite() {
    TestSuite result = new TestSuite();
    result.addTestSuite(ArrayMapTest.class);
    result.addTestSuite(DateTimeTest.class);
    result.addTestSuite(GenericUrlTest.class);
    result.addTestSuite(OAuthParametersTest.class);
    return result;
  }
}
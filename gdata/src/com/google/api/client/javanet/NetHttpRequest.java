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

package com.google.api.client.javanet;

import com.google.api.client.http.HttpContent;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Yaniv Inbar
 */
final class NetHttpRequest extends LowLevelHttpRequest {

  private final HttpURLConnection connection;
  private HttpContent content;

  NetHttpRequest(String requestMethod, String url) throws IOException {
    HttpURLConnection connection =
        this.connection = (HttpURLConnection) new URL(url).openConnection();
    connection.setRequestMethod(requestMethod);
    connection.setUseCaches(false);
  }

  @Override
  public void addHeader(String name, String value) {
    this.connection.addRequestProperty(name, value);
  }

  @Override
  public LowLevelHttpResponse execute() throws IOException {
    HttpURLConnection connection = this.connection;
    // write content
    HttpContent content = this.content;
    if (content != null) {
      connection.setDoOutput(true);
      addHeader("Content-Type", content.getType());
      String contentEncoding = content.getEncoding();
      if (contentEncoding != null) {
        addHeader("Content-Encoding", contentEncoding);
      }
      long contentLength = content.getLength();
      if (contentLength >= 0) {
        addHeader("Content-Length", Long.toString(contentLength));
      }
      content.writeTo(connection.getOutputStream());
    }
    // Set the http.strictPostRedirect property to prevent redirected
    // POST/PUT/DELETE from being mapped to a GET. This
    // system property was a hack to fix a jdk bug w/out changing back
    // compat behavior. It's bogus that this is a system (and not a
    // per-connection) property, so we just change it for the duration
    // of the connection.
    // See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4023866
    String httpStrictPostRedirect =
        System.getProperty("http.strictPostRedirect");
    try {
      System.setProperty("http.strictPostRedirect", "true");
      // TODO: what happens to request signing on redirect?
      connection.connect();
      return new NetHttpResponse(connection);

    } finally {
      if (httpStrictPostRedirect == null) {
        System.clearProperty("http.strictPostRedirect");
      } else {
        System.setProperty("http.strictPostRedirect", httpStrictPostRedirect);
      }
    }
  }

  @Override
  public void setContent(HttpContent content) {
    this.content = content;
  }
}
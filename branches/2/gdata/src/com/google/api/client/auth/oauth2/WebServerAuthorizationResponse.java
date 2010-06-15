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

package com.google.api.client.auth.oauth2;

import com.google.api.client.util.Key;

/**
 * OAuth 2.0 Web Server Flow: parser for the redirect URL after end user grants
 * or denies authorization.
 * <p>
 * To check if the end user grants authorization, check if {@link #error} is
 * {@code null}. If the end user grants authorization, the next step is to
 * request an access token using {@link VerificationCodeAccessTokenRequest}. Use
 * the {@link #code} in {@link VerificationCodeAccessTokenRequest#code}.
 * <p>
 * Sample usage:
 * 
 * <pre>
 * <code>@Override
 * public void doGet(HttpServletRequest request, HttpServletResponse response)
 *     throws IOException {
 *   StringBuffer fullUrlBuf = request.getRequestURL();
 *   if (request.getQueryString() != null) {
 *     fullUrlBuf.append('?').append(request.getQueryString());
 *   }
 *   WebServerAuthorizationResponse authResponse =
 *       new WebServerAuthorizationResponse(fullUrlBuf.toString());
 *   // check for user-denied error
 *   if (authResponse.error != null) {
 *     // authorization denied...
 *   } else {
 *     // request access token using authResponse.code...
 *   }
 * }
 * </code>
 * </pre>
 * 
 * @since 2.3
 * @author Yaniv Inbar
 */
public class WebServerAuthorizationResponse extends
    AbstractAuthorizationResponse {

  /**
   * (REQUIRED if the end user grants authorization) The verification code
   * generated by the authorization server. The verification code SHOULD expire
   * shortly after it is issued and allowed for a single use. The verification
   * code is bound to the client identifier and redirection URI.
   */
  @Key
  public String code;

  /**
   * @param redirectUrl encoded redirect URL
   * @throws IllegalArgumentException URI syntax exception
   */
  public WebServerAuthorizationResponse(String redirectUrl) {
    super(redirectUrl, true);
  }
}

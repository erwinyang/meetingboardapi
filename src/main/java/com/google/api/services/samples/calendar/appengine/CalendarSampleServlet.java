/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.calendar.appengine;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.repackaged.com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry servlet for the Calendar API App Engine Sample.
 * Demonstrates how to make an authenticated API call using OAuth 2 helper classes.
 */
public class CalendarSampleServlet
    extends AbstractAppEngineAuthorizationCodeServlet {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "appname";

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    // Get the stored credentials using the Authorization Flow
    AuthorizationCodeFlow authFlow = initializeFlow();
    String userId = getUserId(req);
    System.out.println(userId);
    Credential credential = authFlow.loadCredential(userId);
    System.out.println(credential.getRefreshToken());
    
    // Build the Calendar object using the credentials
    @SuppressWarnings("unused")
    Calendar calendar = new Calendar.Builder(
        Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
    
    String id = "dianping.com_2d33353939303931342d333830@resource.calendar.google.com";
    com.google.api.services.calendar.model.Calendar c = calendar.calendars().get(id).execute();
    long now = System.currentTimeMillis();
    now = (now / 86400000L) * 86400000L;
    DateTime begin = new DateTime(now);
    DateTime end = new DateTime(now + 86399999L);
    Events events = calendar.events().list(id).setTimeMin(begin).setTimeMax(end).setMaxAttendees(1).setOrderBy("updated").execute();
    
    // Add the code to make an API call here.

    // Send the results as the response
    resp.setStatus(200);
    resp.setContentType("text/html");
    resp.setCharacterEncoding("utf-8");
    PrintWriter writer = resp.getWriter();
//    writer.println("Success! Now add code here.");
//    writer.println(c.getSummary());
    writer.println(events.toPrettyString());
  }
  @Override
  protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
    return Utils.initializeFlow();
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }
}

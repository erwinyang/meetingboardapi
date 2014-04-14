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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry servlet for the Calendar API App Engine Sample. Demonstrates how to
 * make an authenticated API call using OAuth 2 helper classes.
 */
public class ServiceAccountServlet
		extends HttpServlet {
//
//	/**
//	 * Be sure to specify the name of your application. If the application name
//	 * is {@code null} or blank, the application will log a warning. Suggested
//	 * format is "MyCompany-ProductName/1.0".
//	 */
//	private static final String APPLICATION_NAME = "VisualRooms";
//
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws IOException, ServletException {
//
//		try {
//
//			KeyStore keystore = KeyStore.getInstance("PKCS12");
//			// keystore.load(new ByteArrayInputStream(Base64.decodeBase64(k)),
//			// "notasecret".toCharArray());
//			keystore.load(ServiceAccountTest.class.getClassLoader().getResourceAsStream("privatekey.p12"),"notasecret".toCharArray());
//			PrivateKey key = (PrivateKey) keystore.getKey("privatekey", "notasecret".toCharArray());
//
//			GoogleCredential credential = new GoogleCredential.Builder()
//					.setTransport(new NetHttpTransport())
//					.setJsonFactory(new JacksonFactory())
//					.setServiceAccountId("83307325465-3hvbuqvlcbhhov0frbdfjdlsoigam4n7@developer.gserviceaccount.com")
//					.setServiceAccountScopes(Sets.newHashSet(CalendarScopes.CALENDAR))
//					.setServiceAccountPrivateKey(key)
////					.setServiceAccountPrivateKeyFromP12File(
////							new File("/Users/erwin/workspace/visualization/calendar-appengine-sample/privatekey.p12"))
//					// .setServiceAccountUser("user@example.com")
//					.build();
//
//			// Build the Calendar object using the credentials
//			Calendar calendar = new Calendar.Builder(
//					Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
//					.setApplicationName("VisualRooms")
//					.build();
//
//			String id = "dianping.com_2d33353939303931342d333830@resource.calendar.google.com";
//			com.google.api.services.calendar.model.Calendar c = calendar.calendars().get(id).execute();
//			long now = System.currentTimeMillis();
//			now = (now / 86400000L) * 86400000L;
//			DateTime begin = new DateTime(now);
//			DateTime end = new DateTime(now + 86399999L);
//			Events events = calendar.events().list(id).setTimeMin(begin).setTimeMax(end).setMaxAttendees(1)
//					.setOrderBy("updated").execute();
//
//			// Add the code to make an API call here.
//
//			// Send the results as the response
//			resp.setStatus(200);
//			resp.setContentType("text/html");
//			resp.setCharacterEncoding("utf-8");
//			PrintWriter writer = resp.getWriter();
//			// writer.println("Success! Now add code here.");
//			// writer.println(c.getSummary());
//			writer.println(events.toPrettyString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}

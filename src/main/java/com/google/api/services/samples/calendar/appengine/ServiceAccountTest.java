package com.google.api.services.samples.calendar.appengine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.repackaged.com.google.common.collect.Sets;

public class ServiceAccountTest {

	private static final String k = "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";
	
	public static void main(String[] args) throws Exception {
		System.out.println("Hello!");
		
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new ByteArrayInputStream(Base64.decodeBase64(k)), "notasecret".toCharArray());
//		keystore.load(ServiceAccountTest.class.getClassLoader().getResourceAsStream("privatekey.p12"), "notasecret".toCharArray());
		PrivateKey key = (PrivateKey)keystore.getKey("privatekey", "notasecret".toCharArray());
		
	    GoogleCredential credential = new GoogleCredential.Builder()
					.setTransport(GoogleNetHttpTransport.newTrustedTransport())
			        .setJsonFactory(new JacksonFactory())
			        .setServiceAccountId("83307325465-3hvbuqvlcbhhov0frbdfjdlsoigam4n7@developer.gserviceaccount.com")
			        .setServiceAccountScopes(Sets.newHashSet(CalendarScopes.CALENDAR))
//			        .setServiceAccountPrivateKeyFromP12File(new File("/Users/erwin/workspace/visualization/calendar-appengine-sample/privatekey.p12"))
			        .setServiceAccountPrivateKey(key)
//			        .setServiceAccountUser("user@example.com")
			        .build();
	    
	    // Build the Calendar object using the credentials
	    Calendar calendar = new Calendar.Builder(
	    	GoogleNetHttpTransport.newTrustedTransport(), Utils.JSON_FACTORY, credential)
	        .setApplicationName("appname")
	        .build();
	    
	    String id = "dianping.com_2d33353939303931342d333830@resource.calendar.google.com";
	    com.google.api.services.calendar.model.Calendar c = calendar.calendars().get(id).execute();
	    long now = System.currentTimeMillis();
	    now = (now / 86400000L) * 86400000L;
	    DateTime begin = new DateTime(now);
	    DateTime end = new DateTime(now + 86399999L);
	    Events events = calendar.events().list(id).setTimeMin(begin).setTimeMax(end).setMaxAttendees(1).setOrderBy("updated").execute();
	    System.out.println(events.toPrettyString());
	    
		System.out.println("End");
		
	}
	
}


package com.dianping.meetingboard;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStoreRefreshListener;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Preconditions;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class CopyOfRoomServlet extends AbstractAuthorizationCodeServlet {

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	private static final JsonFactory JSONFACTORY = new JsonFactory();
	private static final String APPLICATION_NAME = "MeetingBoard";
	private static final long serialVersionUID = 1L;

	static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
	static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static GoogleClientSecrets clientSecrets = null;
	private static final AppEngineDataStoreFactory DATA_STORE_FACTORY =
			AppEngineDataStoreFactory.getDefaultInstance();

	private Credential newCredential(String userId) {
		Credential.Builder builder = new Credential.Builder(method).setTransport(transport)
				.setJsonFactory(jsonFactory)
				.setTokenServerEncodedUrl(tokenServerEncodedUrl)
				.setClientAuthentication(clientAuthentication)
				.setRequestInitializer(requestInitializer)
				.setClock(clock);
		if (credentialDataStore != null) {
			builder.addRefreshListener(
					new DataStoreCredentialRefreshListener(userId, credentialDataStore));
		} else if (credentialStore != null) {
			builder.addRefreshListener(new CredentialStoreRefreshListener(userId, credentialStore));
		}
		builder.getRefreshListeners().addAll(refreshListeners);
		return builder.build();
	}
	  
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		
		new AuthorizationCodeFlow.Builder().
		
	    Credential credential = newCredential(userId);
	    if (credentialDataStore != null) {
	      StoredCredential stored = credentialDataStore.get(userId);
	      if (stored == null) {
	        return null;
	      }
	      credential.setAccessToken(stored.getAccessToken());
	      credential.setRefreshToken(stored.getRefreshToken());
	      credential.setExpirationTimeMilliseconds(stored.getExpirationTimeMilliseconds());
		
		
		AuthorizationCodeFlow flow;
		flow.new

		String userId = getUserId(req);
		if (flow == null) {
			flow = initializeFlow();
		}
		credential = flow.loadCredential(userId);

		StringWriter sw = new StringWriter();
		JsonGenerator gen = JSONFACTORY.createGenerator(sw);
		gen.useDefaultPrettyPrinter();

		String id = req.getParameter("id");
		Preconditions.checkNotNull(id);

		AuthorizationCodeFlow authFlow = initializeFlow();
		Credential credential = authFlow.loadCredential(getUserId(req));

		Calendar calendar = new Calendar.Builder(
				Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();

		com.google.api.services.calendar.model.Calendar c = calendar.calendars().get(id).execute();
		long now = System.currentTimeMillis();
		now -= 86400000;
		now = (now / 86400000L) * 86400000L;
		DateTime begin = new DateTime(now);
		DateTime end = new DateTime(now + 86399999L);
		Events events = calendar.events()
				.list(id)
				.setTimeMin(begin)
				.setTimeMax(end)
				.setMaxAttendees(1)
				.execute();

		gen.writeStartObject();

		gen.writeStringField("name", Constants.ROOMS.get(id));
		gen.writeFieldName("events");
		gen.writeStartArray();

		List<Event> eventList = Lists.newArrayList();
		for (Event event : events.getItems()) {
			if (event.getAttendees() != null
					&& event.getAttendees().size() > 0
					&& StringUtils.equals(event.getAttendees().get(0).getResponseStatus(), "accepted")
					&& event.getStart() != null
					&& event.getEnd() != null) {
				eventList.add(event);
			}
		}

		Collections.sort(eventList, new Comparator<Event>() {
			@Override
			public int compare(Event a, Event b) {
				String ta = a.getStart() == null || a.getStart().getDateTime() == null ? null : a.getStart()
						.getDateTime().toString();
				String tb = b.getStart() == null || b.getStart().getDateTime() == null ? null : b.getStart()
						.getDateTime().toString();
				if (ta == null && tb == null) {
					return 0;
				} else if (ta == null && tb != null) {
					return 1;
				} else if (ta != null && tb == null) {
					return -1;
				} else {
					return ta.compareTo(tb);
				}
			}
		});

		for (Event event : eventList) {
			gen.writeStartObject();
			gen.writeStringField("title", event.getSummary() == null || event.isEmpty() ? "未命名会议" : event.getSummary());
			gen.writeStringField("creator",
					event.getCreator() == null || StringUtils.isEmpty(event.getCreator().getDisplayName()) ? "无名氏"
							: event.getCreator().getDisplayName());
			gen.writeStringField("start", event.getStart().getDateTime().toString());
			gen.writeStringField("end", event.getEnd().getDateTime().toString());
			gen.writeEndObject();
		}

		gen.writeEndArray();

		gen.writeEndObject();
		gen.close();
		sw.close();

		resp.setStatus(200);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer = resp.getWriter();
		writer.println(sw.toString());
	}

	private static GoogleAuthorizationCodeFlow initializeFlow() throws IOException {
		Set<String> scopes = new HashSet<String>();
		scopes.add(CalendarScopes.CALENDAR);
		scopes.add(CalendarScopes.CALENDAR_READONLY);

		return new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, getClientSecrets(), scopes)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
	}

	private static GoogleClientSecrets getClientSecrets() throws IOException {
		if (clientSecrets == null) {
			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
					new InputStreamReader(RoomServlet.class.getResourceAsStream("/client_secrets.json")));
			Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("Enter ")
					&& !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
					"Download client_secrets.json file from "
							+ "https://code.google.com/apis/console/?api=calendar#project:83307325465 into "
							+ "src/main/resources/client_secrets.json");
		}
		return clientSecrets;
	}
	*/

}

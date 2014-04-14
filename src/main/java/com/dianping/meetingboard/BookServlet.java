
package com.dianping.meetingboard;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;


public class BookServlet extends AbstractAuthorizationCodeServlet {

	private static final long serialVersionUID = 122117264722150767L;
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSONFACTORY = new JsonFactory();
	private static final String APPLICATION_NAME = "MeetingBoard";
	
	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		return Utils.initializeFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		return Utils.getRedirectUri(req);
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		return "11461664512130162162";
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String roomid = req.getParameter("id");
		String startTime = req.getParameter("start");
		String endTime = req.getParameter("end");
		
	    AuthorizationCodeFlow authFlow = initializeFlow();
	    Credential credential = authFlow.loadCredential(getUserId(req));

		Event event = new Event();
		event.setSummary("(临时会议)");
		event.setLocation("");
		ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
		attendees.add(new EventAttendee().setEmail(roomid));
		event.setAttendees(attendees);
		
		Date now = new Date();
		DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date startDate = null, endDate = null;
		try {
			startDate = fullFormat.parse(dayFormat.format(now) + " " + startTime);
			endDate = fullFormat.parse(dayFormat.format(now) + " " + endTime);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		DateTime start = new DateTime(startDate, TimeZone.getTimeZone("GMT+8"));
		event.setStart(new EventDateTime().setDateTime(start));
		DateTime end = new DateTime(endDate, TimeZone.getTimeZone("GMT+8"));
		event.setEnd(new EventDateTime().setDateTime(end));

		Calendar calendar = new Calendar.Builder(
				Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
		
		Event createdEvent = calendar.events().insert("primary", event).execute();

		boolean result = false;
		for (int i=0; i<10; i++) {
			if (createdEvent == null) {
				System.out.println("created event null");
				break;
			}
			
			Event checkEvent = calendar.events().get("primary", createdEvent.getId()).execute();
			if (checkEvent == null) {
				System.out.println("check event null");
				break;
			}
			
			EventAttendee attendee = null;
			for (EventAttendee ea : checkEvent.getAttendees()) {
				if (StringUtils.equals(ea.getEmail(), roomid)) {
					attendee = ea;
					break;
				}
			}
			
			if (attendee == null) {
				System.out.println("attendee not found");
				break;
			}
				
			if (!StringUtils.equals(attendee.getResponseStatus(), "needsAction")) {
				if (StringUtils.equals(attendee.getResponseStatus(), "accepted")) {
					result = true;
				}
				break;
			}
			
			System.out.println("attendee needsAction, check again");
			
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		}
		
		if (!result && createdEvent != null) {
			calendar.events().delete("primary", createdEvent.getId()).execute();
		}
		
		StringWriter sw = new StringWriter();
		JsonGenerator gen = JSONFACTORY.createJsonGenerator(sw);
		gen.useDefaultPrettyPrinter();
		
		gen.writeStartObject();
		gen.writeBooleanField("result", result);
		gen.writeEndObject();
		gen.close();
		sw.close();

		resp.setStatus(200);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer = resp.getWriter();
		writer.println(sw.toString());
	}

}

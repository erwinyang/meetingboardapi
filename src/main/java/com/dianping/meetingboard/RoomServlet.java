
package com.dianping.meetingboard;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;


public class RoomServlet extends AbstractAuthorizationCodeServlet {

	private static final long serialVersionUID = 122117264722150767L;
	static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
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
	    AuthorizationCodeFlow authFlow = initializeFlow();
	    Credential credential = authFlow.loadCredential(getUserId(req));

		StringWriter sw = new StringWriter();
		JsonGenerator gen = JSONFACTORY.createJsonGenerator(sw);
		gen.useDefaultPrettyPrinter();

		String id = req.getParameter("id");
		Preconditions.checkNotNull(id);

		Calendar calendar = new Calendar.Builder(
				Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();

		long now = System.currentTimeMillis();
//		now -= 86400000;
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

}

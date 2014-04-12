
package com.dianping.meetingboard;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RoomListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final JsonFactory JSONFACTORY = new JsonFactory();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		StringWriter sw = new StringWriter();
		JsonGenerator gen = JSONFACTORY.createGenerator(sw);
		gen.useDefaultPrettyPrinter();

		gen.writeStartArray();
		for (Entry<String, String> entry : Constants.ROOMS.entrySet()) {
			gen.writeStartObject();
			gen.writeFieldName("id");
			gen.writeString(entry.getKey());
			gen.writeFieldName("name");
			gen.writeString(entry.getValue());
			gen.writeEndObject();
		}
		gen.writeEndArray();
		gen.close();
		sw.close();

		resp.setStatus(200);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		PrintWriter writer = resp.getWriter();
		writer.println(sw.toString());
	}
}

package org.n52.movingcode.feed;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.movingcode.runtime.feed.FeedTemplate;

@WebServlet("/feed")
public class FeedServlet extends HttpServlet {

	/**
	 * Generated Serial
	 */
	private static final long serialVersionUID = 8977994392612616210L;
	
	private String feedTitle;
	private String feedSubtitle;
	private String feedAuthorName;
	private String feedAuthorEmail;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.feedTitle = config.getInitParameter("feed.Title");
		this.feedSubtitle = config.getInitParameter("feed.Subtitle");
		this.feedAuthorName = config.getInitParameter("feed.AuthorName");
		this.feedAuthorEmail = config.getInitParameter("feed.AuthorEmail");	
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		response.setContentType("text/plain");

		PrintWriter out = response.getWriter();

		out.println(getBaseUrl(request));

		out.close();

		// response.getWriter().write("<html><body>GET response</body></html>");
		
		
	}

	private static final String getBaseUrl(final HttpServletRequest req) {
		return req.getRequestURL().toString();
	}
	
	private final FeedTemplate makeTemplate(final String baseUrl){
		return new FeedTemplate.Builder()
				.feedTitle(feedTitle)
				.feedSubtitle(feedSubtitle)
				.feedAuthorName(feedAuthorName)
				.feedAuthorEmail(feedAuthorEmail)
				.build();
	}
}

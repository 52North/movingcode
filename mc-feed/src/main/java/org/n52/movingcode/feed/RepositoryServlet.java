package org.n52.movingcode.feed;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.feed.FeedTemplate;

public abstract class RepositoryServlet extends HttpServlet{
	/**
	 * Generated Serial
	 */
	private static final long serialVersionUID = 8977994392612616210L;
	
	private String feedTitle;
	private String feedSubtitle;
	private String feedAuthorName;
	private String feedAuthorEmail;
	
	private static final String packages = "packages";
	
	MovingCodeRepository repo;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.feedTitle = config.getInitParameter("feed.Title");
		this.feedSubtitle = config.getInitParameter("feed.Subtitle");
		this.feedAuthorName = config.getInitParameter("feed.AuthorName");
		this.feedAuthorEmail = config.getInitParameter("feed.AuthorEmail");
		
		// TODO: load repositories
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	static final String getBaseUrl(final HttpServletRequest req) {
		return req.getRequestURL().toString();
	}
	
	final FeedTemplate makeTemplate(final String baseUrl){
		return new FeedTemplate.Builder()
				.feedTitle(feedTitle)
				.feedSubtitle(feedSubtitle)
				.feedAuthorName(feedAuthorName)
				.feedAuthorEmail(feedAuthorEmail)
				.build();
	}
	
	final String makeContentUrl(final HttpServletRequest req){
		String baseUrl = getBaseUrl(req);
		
		return req.getRequestURL().toString();
	}
}

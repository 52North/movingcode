package org.n52.movingcode.feed;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.n52.movingcode.runtime.GlobalRepositoryManager;
import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.feed.FeedTemplate;

/**
 * Abstract servlet class that contains some common logic for the
 * Feed-related servlets.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public abstract class RepositoryServlet extends HttpServlet{
		
	/**
	 * Default serial.
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String feedTitle;
	private String feedSubtitle;
	private String feedAuthorName;
	private String feedAuthorEmail;
	
	private String[] zipFolderRepositories;
	
	MovingCodeRepository repo;

	@Override
	public void init(ServletConfig config) throws ServletException {
		this.feedTitle = FeedConfig.getParameter("feed.Title");
		this.feedSubtitle = FeedConfig.getParameter("feed.Subtitle");
		this.feedAuthorName = FeedConfig.getParameter("feed.AuthorName");
		this.feedAuthorEmail = FeedConfig.getParameter("feed.AuthorEmail");
		this.zipFolderRepositories = parseStringList(FeedConfig.getParameter("repository.zipfolders"));
		
		GlobalRepositoryManager rm = GlobalRepositoryManager.getInstance();
		for (String directory : zipFolderRepositories){
			rm.addLocalZipPackageRepository(directory);
		}
		
		repo = rm;
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
	
	final FeedTemplate makeTemplate(final String baseUrl){
		return new FeedTemplate.Builder()
				.feedTitle(feedTitle)
				.feedSubtitle(feedSubtitle)
				.feedAuthorName(feedAuthorName)
				.feedAuthorEmail(feedAuthorEmail)
				.build();
	}
	
	static final String getBaseUrl(final HttpServletRequest req) {
		return req.getRequestURL().toString();
	}
	
	private static final String[] parseStringList(String s){
		return s.split(",");
	}
}

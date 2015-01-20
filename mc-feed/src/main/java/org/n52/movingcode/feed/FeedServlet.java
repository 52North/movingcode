package org.n52.movingcode.feed;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.feed.CodePackageFeed;

/**
 * Servlet class for code package feeds.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
@WebServlet("/feed")
public class FeedServlet extends RepositoryServlet {
	
	/**
	 * Generated Serial
	 */
	private static final long serialVersionUID = 8977994392612616210L;
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final String baseUrl = getBaseUrl(request);
		final String webRoot = baseUrl.substring(0, baseUrl.indexOf("/feed")) + "/packages/";
		
		response.setContentType("application/atom+xml");
		CodePackageFeed feed = new CodePackageFeed(makeTemplate(baseUrl));
		
		MovingCodePackage[] mcpArray = repo.getLatestPackages().toArray(new MovingCodePackage[0]); 
		Arrays.sort(mcpArray);
		
		for (MovingCodePackage mcp : mcpArray){
			feed.addEntry(mcp, webRoot);
		}
		
		try (ServletOutputStream os = response.getOutputStream()){
			feed.write(os);
		} catch (Exception e){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
	}
}

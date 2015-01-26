/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.movingcode.feed;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

/**
 * Servlet for direct access to the code packages.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
@WebServlet(urlPatterns = "/packages/*")
public class PackageServlet extends RepositoryServlet {
	
	/**
	 * Generated serial.
	 */
	private static final long serialVersionUID = 8856458560873746081L;
	
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
		PackageProperties props = new PackageProperties(request);
		
		if (props.packageId==null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		MovingCodePackage mcp = repo.getPackage(props.packageId);
		
		if (mcp==null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		try {
			switch (props.prop) {
			case XML:
				response.setContentType("text/xml");
				PrintWriter out = response.getWriter();
				out.print(mcp.getDescriptionAsString());
				out.close();
				break;
			case ZIP:
				response.setContentType("application/zip");
				ServletOutputStream os = response.getOutputStream();
				mcp.dumpPackage(os);
				os.close();
				break;

			default:
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			e.printStackTrace();
			return;
		}
		
	}
	
	
	/**
	 * Small properties class that describes the content to be delivered by
	 * the {@link PackageServlet}
	 * 
	 * @author matthias
	 */
	private static final class PackageProperties{
		final PID packageId;
		final Property prop;
		
		/**
		 * Derive properties from the request.
		 * 
		 * @param request
		 */
		PackageProperties(HttpServletRequest request){
			String s = request.getPathInfo();
			if (s.endsWith(".xml")){
				prop = Property.XML;
				packageId = PID.fromString(s.substring(1, s.indexOf(".xml")));
			} else if (s.endsWith(".zip")){
				prop = Property.ZIP;
				packageId = PID.fromString(s.substring(1, s.indexOf(".zip")));
			} else {
				prop = Property.UNKNOWN;
				packageId = null;
			}
		}
	}
	
	private enum Property{
		XML, ZIP, ROOT, UNKNOWN
	}
}

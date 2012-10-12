package org.n52.movingcode.runtime.processors;


import java.io.File;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IOParameterMap;


/**
 * An abstract processor that is going to handle MovingCodePackages.
 * It implements the {@link IProcessor} interface and extends an {@link IOParameterMap}.
 * 
 * So to say it is an IOParameterMap that can be executed through an IProcessor facade.
 * 
 * 
 * @author Matthias Mueller
 *
 */
public abstract class AbstractProcessor extends IOParameterMap implements IProcessor{
	
	private static final long serialVersionUID = -3617747844919275088L;
	protected final File scratchWorkspace;
	protected final MovingCodePackage mcPackage;
	protected final PropertyMap properties;
	
	/**
	 * The default and mandatory constructor for all processors in this framework.
	 * 
	 * @param {@link File} scratchworkspace
	 * @param {@link MovingCodePackage} mcp 
	 * @param {@link PropertyMap} properties
	 */
	public AbstractProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
		super(mcp);
		this.scratchWorkspace = scratchworkspace;
		this.mcPackage = mcp;
		this.properties = properties;
	}
	

}

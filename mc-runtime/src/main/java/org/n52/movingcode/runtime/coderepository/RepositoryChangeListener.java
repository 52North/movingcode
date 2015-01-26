package org.n52.movingcode.runtime.coderepository;

/**
 * Listener interface providing callbacks for 
 * events that updates a {@link MovingCodeRepository}.
 * 
 * @author matthes rieke
 *
 */
public interface RepositoryChangeListener {

	/**
	 * callback is triggered when the repository has been updated.
	 * @param updatedRepo the updated {@link MovingCodeRepository}
	 */
	public void onRepositoryUpdate(MovingCodeRepository updatedRepo);

}

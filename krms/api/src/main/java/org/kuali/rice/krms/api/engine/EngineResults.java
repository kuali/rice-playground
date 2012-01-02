/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.engine;

import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface EngineResults {

	// TODO - need to determine what goes here...

    /**
     * Return the ResultEvent for the given index
     * @param index
     * @return {@link ResultEvent}
     */
	public ResultEvent getResultEvent(int index);

    /**
     * Return the list of ResultEvents
     * @return List<ResultEvent>
     */
	public List<ResultEvent> getAllResults();

    /**
     * Return the ResultEvents of the given type
     * @param type
     * @return List<ResultEvent>
     */
	public List<ResultEvent> getResultsOfType(String type);

    /**
     * Return the attribute of the given key
     * @param key
     * @return Object
     */
	public Object getAttribute(String key);

    /**
     * Set the attribute of the given values
     * @param key
     * @param attribute
     */
	public void setAttribute(String key, Object attribute);

    /**
     * Add the given result
     * @param result
     */
	public void addResult(ResultEvent result);
}

/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.applicationconstants.service;

import java.util.List;

import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.xml.XmlLoader;


/**
 * Defines contract for interacting with {@link ApplicationConstant} objects.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface ApplicationConstantsService extends XmlLoader {
	public static final String APPLICATION_CONSTANTS_CACHE_ID = "org.kuali.rice.ksb.cache.ApplicationConstantsCache";
	
    public void save(ApplicationConstant applicationConstant);
    public void delete(ApplicationConstant applicationConstant);
    public ApplicationConstant findByName(String applicationConstantName);
    public List<ApplicationConstant> findAll();
}
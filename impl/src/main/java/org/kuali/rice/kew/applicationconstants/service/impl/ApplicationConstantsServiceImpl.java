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
package org.kuali.rice.kew.applicationconstants.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.applicationconstants.dao.ApplicationConstantsDAO;
import org.kuali.rice.kew.applicationconstants.service.ApplicationConstantsService;
import org.kuali.rice.kew.applicationconstants.xml.ApplicationConstantsXmlParser;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.springframework.transaction.annotation.Transactional;


/**
 * Default implementation of the {@link ApplicationConstantsService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Transactional
public class ApplicationConstantsServiceImpl implements ApplicationConstantsService {
	private static final Logger LOG = Logger.getLogger(ApplicationConstantsServiceImpl.class);

	private static final String NAME_EMPTY = "applicationconstants.error.emptyname";

	private static final String NAME_HAS_WHITE_SPACE = "applicationconstants.error.namehaswhitespace";

	private static final String VALUE_EMPTY = "applicationconstants.error.emptyvalue";

	private ApplicationConstantsDAO applicationConstantsDAO;

	public void save(ApplicationConstant applicationConstant) {
		validateApplicationConstant(applicationConstant);
		versionBlankConstant(applicationConstant);
		getApplicationConstantsDAO().saveConstant(applicationConstant);
		KEWServiceLocator.getCacheAdministrator().flushEntry(applicationConstant.getApplicationConstantName());
		putConstantInCache(applicationConstant);
	}

	/**
	 * Allows passing in of a new ApplicationConstant object with an existing constant value with first
	 * having to fetch that constant.
	 *
	 * @param applicationConstant that will be saved
	 */
	private void versionBlankConstant(ApplicationConstant applicationConstant) {
		if (applicationConstant.getLockVerNbr() == null) {
			ApplicationConstant fetchedConstant = findByName(applicationConstant.getApplicationConstantName());
			if (fetchedConstant != null) {
				applicationConstant.setLockVerNbr(fetchedConstant.getLockVerNbr());
			}
		}
	}

	public void delete(ApplicationConstant applicationConstant) {
		getApplicationConstantsDAO().deleteConstant(applicationConstant);
		KEWServiceLocator.getCacheAdministrator().flushEntry(applicationConstant.getApplicationConstantName());
	}

	public ApplicationConstant findByName(String applicationConstantName) {
		ApplicationConstant constant = (ApplicationConstant) KEWServiceLocator.getCacheAdministrator().getFromCache(applicationConstantName);
		if (constant == null) {
			constant = getApplicationConstantsDAO().findByName(applicationConstantName);
			if (constant != null) {
				putConstantInCache(constant);
			}
		}
		return constant;
	}

	public List<ApplicationConstant> findAll() {
		List<ApplicationConstant> appConstants = getApplicationConstantsDAO().findAll();
		for (ApplicationConstant constant : appConstants) {
			putConstantInCache(constant);
		}
		return appConstants;
	}

	private void putConstantInCache(ApplicationConstant constant) {
		KEWServiceLocator.getCacheAdministrator().putInCache(constant.getApplicationConstantName(), constant, APPLICATION_CONSTANTS_CACHE_ID);
	}

	private void validateApplicationConstant(ApplicationConstant constant) {
		LOG.debug("Enter validateApplicationConstant(..)...");
		List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();

		if (constant.getApplicationConstantName() == null || "".equals(constant.getApplicationConstantName().trim())) {
			errors.add(new WorkflowServiceErrorImpl("Application constant name is empty.", NAME_EMPTY));
		} else {
			constant.setApplicationConstantName(constant.getApplicationConstantName().trim());
		}

		if (constant.getApplicationConstantName() != null && constant.getApplicationConstantName().trim().indexOf(" ") > 0) {
			errors.add(new WorkflowServiceErrorImpl("Application constant name has white space(s)", NAME_HAS_WHITE_SPACE));
		}

		if (constant.getApplicationConstantValue() == null || "".equals(constant.getApplicationConstantValue().trim())) {
			errors.add(new WorkflowServiceErrorImpl("Application constant value is empty.", VALUE_EMPTY));
		} else {
			constant.setApplicationConstantValue(constant.getApplicationConstantValue().trim());
		}

		LOG.debug("Exit validateApplicationConstant(..) ");
		if (!errors.isEmpty()) {
			throw new WorkflowServiceErrorException("Application Constant Validation Error", errors);
		}
	}

	public void loadXml(InputStream inputStream, String principalId) {
		ApplicationConstantsXmlParser parser = new ApplicationConstantsXmlParser();
		try {
			parser.parseAppConstEntries(inputStream);
		} catch (Exception e) {
			WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error parsing Application Constants  XML file", new WorkflowServiceErrorImpl("Error parsing XML file.", KEWConstants.XML_FILE_PARSE_ERROR));
			wsee.initCause(e);
			throw wsee;
		}
	}

	/**
	 * @return applicationConstantsDAO
	 */
	public ApplicationConstantsDAO getApplicationConstantsDAO() {
		return applicationConstantsDAO;
	}

	/**
	 * @param historyDAO
	 */
	public void setApplicationConstantsDAO(ApplicationConstantsDAO historyDAO) {
		applicationConstantsDAO = historyDAO;
	}
}
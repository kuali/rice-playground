/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.ksb.messaging;

import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.beans.factory.InitializingBean;

/**
 * Used from Spring to register service definitions from an already configured and started KSB.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KSBExporter implements InitializingBean {
	
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KSBExporter.class);
	
	private ServiceDefinition serviceDefinition;
	private boolean forceRefresh = false;
	protected RemotedServiceRegistry remotedServiceRegistry;

	public void afterPropertiesSet() throws Exception {
		this.getServiceDefinition().validate();
		LOG.info("Attempting to expose service with localServiceName '" + this.getServiceDefinition().getLocalServiceName() + "' and QName '" + this.getServiceDefinition().getServiceName() + "'");
		if(getRemotedServiceRegistry()!=null)
			getRemotedServiceRegistry().registerService(this.getServiceDefinition(), this.isForceRefresh());
		else
			KSBServiceLocator.getServiceDeployer().registerService(this.getServiceDefinition(), this.isForceRefresh());
	}

	public ServiceDefinition getServiceDefinition() {
		return serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public boolean isForceRefresh() {
		return forceRefresh;
	}

	public void setForceRefresh(boolean forceRefresh) {
		this.forceRefresh = forceRefresh;
	}

	/**
	 * @return the remotedServiceRegistry
	 */
	public RemotedServiceRegistry getRemotedServiceRegistry() {
		return this.remotedServiceRegistry;
	}

	/**
	 * @param remotedServiceRegistry the remotedServiceRegistry to set
	 */
	public void setRemotedServiceRegistry(
			RemotedServiceRegistry remotedServiceRegistry) {
		this.remotedServiceRegistry = remotedServiceRegistry;
	}

}

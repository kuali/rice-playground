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
package org.kuali.rice.kew.workgroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kew.attribute.Extension;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.xml.XmlLoader;
import org.kuali.rice.kew.xml.export.XmlExporter;
import org.kuali.rice.kim.bo.Person;


/**
 * A service providing data access for {@link Workgroup}s.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkgroupService extends XmlLoader, XmlExporter {

	/**
	 * Retrieves the capabilities of this Workgroup service.  This essentially provides the core with information
	 * on the kinds of activities which the Workgroup service can perform such as reporting, routing, etc.  This
	 * is primarily used by the web-tier of the application to aid in delivery of web-based workgroup services.
	 */
	public WorkgroupCapabilities getCapabilities();

	public Workgroup getBlankWorkgroup();

	public Extension getBlankWorkgroupExtension();

    public Workgroup getWorkgroup(GroupId groupId);

    /**
     * Searches for core Workflow workgroups plus any workgroup extensions if
     * loadWorkgroupExtensions is true. A workgroup extension allow searching and
     * dynamic loading of workgroups maintained in Workflow-external systems.
     * Workgroup extensions are specified and configured based on the needs of
     * a given installation site.
     */
    public Workgroup getWorkgroup(GroupId groupId, boolean loadWorkgroupExtensions);

    public Workgroup getWorkgroup(WorkgroupIdDTO groupIdVO);

    public List<Workgroup> getUsersGroups(String pincipalId) throws KEWUserNotFoundException ;

    public Set<String> getUsersGroupNames(Person member);
    public Set<String> getUsersGroupNames(String principalId);

    public Set<Long> getUsersGroupIds(String principalId);

    public List<Workgroup> getWorkgroupsGroups(Workgroup workgroup);

    public List search(Workgroup workgroup, Map<String, String> extensionValues, boolean useWildCards);

    public List search(Workgroup workgroup, Map<String, String> extensionValues, Person user) throws KEWUserNotFoundException;

    public List search(Workgroup workgroup, Map<String, String> extensionValues, String principalId) throws KEWUserNotFoundException;

    public boolean isUserMemberOfGroup(GroupId groupId, String principalId) throws KEWUserNotFoundException;

    /**
     * Executes a shallow copy of the given workgroup.
     */
    public Workgroup copy(Workgroup workgroup);

    public void removeNameFromCache(GroupNameId name);

    public void removeFromCacheById(WorkflowGroupId id);

    public void save(Workgroup workgroup);

}

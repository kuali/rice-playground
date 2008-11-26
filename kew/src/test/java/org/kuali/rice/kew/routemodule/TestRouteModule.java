
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
package org.kuali.rice.kew.routemodule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routemodule.RouteModule;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestRouteModule implements RouteModule {

    private static Map responsibilityMap = new HashMap();
    
    public List findActionRequests(RouteContext context) throws ResourceUnavailableException, WorkflowException {
    	return findActionRequests(context.getDocument());
    }
    
    public List findActionRequests(DocumentRouteHeaderValue routeHeader) throws ResourceUnavailableException, WorkflowException {
        TestRouteLevel routeLevel = TestRouteModuleXMLHelper.parseCurrentRouteLevel(routeHeader);
        List actionRequests = new ArrayList();
        if (routeLevel == null) {
            return actionRequests;
        }
        for (Iterator iterator = routeLevel.getResponsibilities().iterator(); iterator.hasNext();) {
            TestResponsibility responsibility = (TestResponsibility) iterator.next();
            TestRecipient recipient = responsibility.getRecipient();
            Recipient realRecipient = getRealRecipient(recipient);
            ActionRequestFactory arFactory = new ActionRequestFactory(routeHeader);
            Long responsibilityId = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
            ActionRequestValue request = arFactory.addRootActionRequest(responsibility.getActionRequested(), new Integer(responsibility.getPriority()), realRecipient, "", responsibilityId, Boolean.FALSE, null, null);
            responsibilityMap.put(request.getResponsibilityId(), recipient);
            for (Iterator delIt = responsibility.getDelegations().iterator(); delIt.hasNext();) {
                TestDelegation delegation = (TestDelegation) delIt.next();
                TestRecipient delegationRecipient = delegation.getResponsibility().getRecipient();
                Recipient realDelegationRecipient = getRealRecipient(delegationRecipient);
                responsibilityId = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
                ActionRequestValue delegationRequest = arFactory.addDelegationRequest(request, realDelegationRecipient, responsibilityId, Boolean.FALSE, delegation.getType(), "", null);
                responsibilityMap.put(delegationRequest.getResponsibilityId(), delegationRecipient);
            }
            actionRequests.add(request);
        }
        return actionRequests;
    }

    public Recipient getRealRecipient(TestRecipient recipient) throws WorkflowException {
        Recipient realRecipient = null;
        if (recipient.getType().equals(KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
        	realRecipient = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(recipient.getId()));
        } else if (recipient.getType().equals(KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
        	realRecipient = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(recipient.getId()));
        } else {
        	throw new WorkflowException("Could not resolve recipient with type " + recipient.getType());
        }
        return realRecipient;
    }
    
    public ResponsibleParty resolveResponsibilityId(Long responsibilityId) throws ResourceUnavailableException, WorkflowException {
        TestRecipient recipient = (TestRecipient)responsibilityMap.get(responsibilityId);
        if (recipient == null) {
            return null;
        }
        ResponsibleParty responsibleParty = new ResponsibleParty();
        if (recipient.getType().equals(KEWConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
            responsibleParty.setUserId(new AuthenticationUserId(recipient.getId()));
        } else if (recipient.getType().equals(KEWConstants.ACTION_REQUEST_GROUP_RECIPIENT_CD)) {
        	GroupInfo grpInfo =new GroupInfo();
        	grpInfo.setGroupName(recipient.getId());
            responsibleParty.setGroupId(grpInfo);
        } else if (recipient.getType().equals(KEWConstants.ACTION_REQUEST_ROLE_RECIPIENT_CD)) {
            responsibleParty.setRoleName(recipient.getId());
        } else {
            throw new WorkflowException("Invalid recipient type code of '"+recipient.getType()+"' for responsibility id "+responsibilityId);
        }
        return responsibleParty;
    }
    
}

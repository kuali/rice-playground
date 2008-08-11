/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.rule.event;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.SaveDocumentRule;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiRuleService;

/**
 * This class represents the save event that is part of an eDoc in Kuali. This could be triggered when a user presses the save
 * button for a given document or it could happen when another piece of code calls the save method in the document service.
 * 
 * 
 */
public class SaveDocumentEvent extends KualiDocumentEventBase implements SaveEvent {
    /**
     * Constructs a SaveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public SaveDocumentEvent(String errorPathPrefix, Document document) {
        this("creating save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a SaveDocumentEvent with the given document
     * 
     * @param document
     */
    public SaveDocumentEvent(Document document) {
        this("", document);
    }
    
    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEventBase#KualiDocumentEventBase(java.lang.String, java.lang.String, org.kuali.rice.kns.document.Document)
     */
    public SaveDocumentEvent(String description, String errorPathPrefix, Document document) {
	super(description, errorPathPrefix, document);
    }
    
    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return SaveDocumentRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((SaveDocumentRule) rule).processSaveDocument(document);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#generateEvents()
     */
    @Override
    public List generateEvents() {
        KualiRuleService ruleService = KNSServiceLocator.getKualiRuleService();

        List events = new ArrayList();
        events.addAll(ruleService.generateAdHocRoutePersonEvents(getDocument()));
        events.addAll(ruleService.generateAdHocRouteWorkgroupEvents(getDocument()));
        
        events.addAll(getDocument().generateSaveEvents());

        /*
        if (getDocument() instanceof CashReceiptDocument) {
            events.addAll(ruleService.generateCheckEvents((CashReceiptDocument) getDocument()));
        }

        if (getDocument() instanceof AccountingDocument) {
            events.addAll(ruleService.generateAccountingLineEvents((AccountingDocument) getDocument()));
        }
        */
        return events;
    }
}
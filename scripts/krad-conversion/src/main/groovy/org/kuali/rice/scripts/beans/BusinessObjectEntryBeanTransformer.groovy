/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.scripts.beans

import groovy.util.logging.Log

/**
 * This class converts business object entries into data object entries
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class BusinessObjectEntryBeanTransformer extends SpringBeanTransformer {

    /**
     * Processes BusinessObjectEntry and can be used on attribute definitions
     *
     * @param beanNode
     * @return
     */
    def transformBusinessObjectEntryBean(Node beanNode) {
        if (beanNode?.@parent == "BusinessObjectEntry") {
            beanNode.@parent = "DataObjectEntry";
        }
        transformControlProperty(beanNode, ddBeanControlMap);
        this.removeProperties(beanNode, ddPropertiesRemoveList);
        this.renameProperties(beanNode, ddPropertiesMap);
        renamePropertyBeans(beanNode, ddPropertiesMap, true);

        return beanNode
    }

}

/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * tests the Parameter Type section in Rice.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterTypeWDIT extends WebDriverLegacyITBase {
    /**
     * This overridden method ...
     * 
     * @see edu.samplu.common.MenuLegacyITBase#getLinkLocator()
     */
    String docId;
    String parameterType;
    String parameterCode;
    public static final String TEST_URL = ITUtil.PORTAL + "?channelTitle=Parameter%20Type&channelUrl=" + ITUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.parameter.ParameterTypeBo&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
   
    @Override
    public String getTestUrl() {
        return TEST_URL;
    }
    @Test
    public void testParameterType() throws Exception {
        
        // Create New
        selectFrame("iframeportlet");
        super.waitAndCreateNew();
        List<String> params;
        params=super.testCreateNewParameterType(docId, parameterType,parameterCode);
        
        //Lookup
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        params=super.testLookUpParameterType(params.get(0), params.get(1),params.get(2));
        
        //edit
        params=super.testEditParameterType(params.get(0), params.get(1),params.get(2));

        //Verify if its edited
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        params=super.testVerifyEditedParameterType(params.get(0), params.get(1),params.get(2));

        //copy
        params=super.testCopyParameterType(params.get(0), params.get(1),params.get(2));

        //Verify if its copied
        super.open(ITUtil.getBaseUrlString()+TEST_URL);
        selectFrame("iframeportlet");
        super.testVerifyCopyParameterType(params.get(0), params.get(1),params.get(2));

    }

    
}

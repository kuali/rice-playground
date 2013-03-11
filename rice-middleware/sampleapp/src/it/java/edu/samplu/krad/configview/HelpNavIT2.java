/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.krad.configview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverITBase;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the help widget
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class HelpNavIT2 extends WebDriverLegacyITBase {

    /**
     * URL for the Configuration Test View - Help
     * 
     * <p>
     * Due to a WebDriver bug (feature?) the tooltips can not be tested with WebDriver.
     * {@link HelpWDIT} is being used to test help tooltips.
     * </p>
     * 
     * @see edu.samplu.common.WebDriverITBase#getTestUrl()
     */
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    /**
     * Test the tooltip and external help on the view
     */
    @Test
    public void testViewHelp2() throws Exception {
        navigateToHelp();
        super.testViewHelp2();
    }

    /**
     * Test the external help on the section and fields
     */
    @Test
    public void testExternalHelp2() throws Exception {
        navigateToHelp();
        super.testExternalHelp2();
    }

    /**
     * Test the external help on the sub-section and display only fields
     */
    @Test
    public void testDisplayOnlyExternalHelp2() throws Exception {
        navigateToHelp();
        super.testDisplayOnlyExternalHelp2();
    }

    /**
     * Test the external help on the section and fields with missing help URL
     */
    @Test
    public void testMissingExternalHelp2() throws Exception {
        navigateToHelp();
        super.testMissingExternalHelp2();
    }

    private void navigateToHelp() throws Exception
    {
        waitAndClickByLinkText("KRAD");
        waitAndClickByXpath("(//a[contains(text(),'Configuration Test View')])[3]");
        switchToWindow("Kuali :: Configuration Test View");
        waitAndClickByLinkText("Help");
        Thread.sleep(5000);
        selectFrame("iframeportlet");
    }
}
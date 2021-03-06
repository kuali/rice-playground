--
-- Copyright 2005-2014 The Kuali Foundation
--
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.opensource.org/licenses/ecl2.php
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

# -----------------------------------------------------------------------
# KRCR_NMSPC_T
# -----------------------------------------------------------------------
INSERT INTO KRCR_NMSPC_T (ACTV_IND,APPL_ID,NM,NMSPC_CD,OBJ_ID,VER_NBR)
  VALUES ('Y','RICE','Kuali Rules Test','KR-RULE-TEST','5a83c912-94b9-4b4d-ac3f-88c53380a4a3',1)
/
INSERT INTO KRCR_NMSPC_T (ACTV_IND,APPL_ID,NM,NMSPC_CD,OBJ_ID,VER_NBR)
  VALUES ('Y','RICE','Sample App','KR-SAP','27035960-3755-482e-a2ae-7fac13cc5f45',1)
/

# -----------------------------------------------------------------------
# KRCR_PARM_T
# -----------------------------------------------------------------------
INSERT INTO KRCR_PARM_T (APPL_ID,CMPNT_CD,EVAL_OPRTR_CD,NMSPC_CD,OBJ_ID,PARM_NM,PARM_TYP_CD,VAL,VER_NBR)
  VALUES ('TRAVEL','TEST_COMPONENT','A','KR-SAP','B6A90093AB168D60E040DC0A1F8A1116','TEST_PARAM','HELP','http://www.kuali.org/?system_parm',1)
/

# -----------------------------------------------------------------------
# KRCR_STYLE_T
# -----------------------------------------------------------------------
INSERT INTO KRCR_STYLE_T (ACTV_IND,NM,OBJ_ID,STYLE_ID,VER_NBR,XML)
  VALUES (0,'eDoc.Example1.Style','6166CBA1BC08644DE0404F8189D86C09','2009',2,'<xsl:stylesheet xmlns:my-class="xalan://org.kuali.rice.edl.framework.util.EDLFunctions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
        <!-- widgets is simply more xslt that contains common functionality that greatly simplifies html rendering. It is somewhat complicated but does not require changes or full understanding unless enhancements are required.  -->
        <xsl:include href="widgets"/>
        <xsl:output indent="yes" method="html" omit-xml-declaration="yes" version="4.01"/>
        <!-- variables in the current version of xslt cannot be changed once set. Below they are set to various values often fed by java classes or to values contained in workflow xml. Not all of these are used in this form but are shown because often they can be useful. The ones prefixed with my-class are methods that are exposed by workflow to Edoclite.-->
        <xsl:variable name="actionable" select="/documentContent/documentState/actionable"/>
        <xsl:variable name="docHeaderId" select="/documentContent/documentState/docId"/>
        <xsl:variable name="editable" select="/documentContent/documentState/editable"/>
        <xsl:variable name="globalReadOnly" select="/documentContent/documentState/editable != \'true\'"/>
        <xsl:variable name="docStatus" select="//documentState/workflowDocumentState/status"/>
        <xsl:variable name="isAtNodeInitiated" select="my-class:isAtNode($docHeaderId, \'Initiated\')"/>
        <xsl:variable name="isPastInitiated" select="my-class:isNodeInPreviousNodeList(\'Initiated\', $docHeaderId)"/>
        <xsl:variable name="isUserInitiator" select="my-class:isUserInitiator($docHeaderId)"/>
        <xsl:param name="overrideMain" select="\'true\'"/>
        <!-- mainForm begins here. Execution of stylesheet begins here. It calls other templates which can call other templates. Position of templates beyond this point do not matter. -->
        <xsl:template name="mainForm">
          <html xmlns="">
            <head>
              <script language="javascript"/>
              <xsl:call-template name="htmlHead"/>
            </head>
            <body onload="onPageLoad()">
              <xsl:call-template name="errors"/>
              <!-- the header is usefule because it tells the user whether they are in \'Editing\' mode or \'Read Only\' mode. -->
              <xsl:call-template name="header"/>
              <xsl:call-template name="instructions"/>
              <xsl:variable name="formTarget" select="\'EDocLite\'"/>
              <!-- validateOnSubmit is a function in edoclite1.js which also supports edloclite forms and can be somewhat complicated but does not require modification unless enhancements are required. -->
              <form action="{$formTarget}" enctype="multipart/form-data" id="edoclite" method="post" onsubmit="return validateOnSubmit(this)">
                <xsl:call-template name="hidden-params"/>
                <xsl:call-template name="mainBody"/>
                <xsl:call-template name="notes"/>
                <br/>
                <xsl:call-template name="buttons"/>
                <br/>
              </form>
              <xsl:call-template name="footer"/>
            </body>
          </html>
        </xsl:template>
        <!-- mainBody template begins here. It calls other templates which can call other templates. Position of templates do not matter. -->
        <xsl:template name="mainBody">
          <!--
to debug, or see values of previously created, the uncomment the following line to see value of $docStatus rendered on form. -->
          <!-- $docStatus=<xsl:value-of select="$docStatus" />
-->
          <!-- rest of this all is within the form table -->
          <table align="center" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" width="80%" xmlns="">
            <tr>
              <td align="left" border="3" class="thnormal" colspan="1">
                <br/>
                <h3>
                  Indiana University
                  <br/>
                  EDL EDoclite Example
                </h3>
                <br/>
              </td>
              <td align="center" border="3" class="thnormal" colspan="2">
                <br/>
                <h2>eDocLite Example 1 Form</h2>
              </td>
            </tr>
            <tr>
              <td class="headercell5" colspan="100%">
                <b>User Information</b>
              </td>
            </tr>
            <tr>
              <!-- IU usually autofills initiator name from Authentication system, and makes it readOnly. See other examples for this type of behavior. -->
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'userName\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'userName\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="headercell5" colspan="100%">
                <b>Other Information</b>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'rqstDate\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'rqstDate\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'campus\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'campus\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'description\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'description\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="2">
                <b>(Check all that apply)</b>
              </td>
            </tr>
            <tr>
              <td class="datacell" colspan="2">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'fundedBy\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
                <br/>
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'researchHumans\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
                <br/>
              </td>
            </tr>
            <tr>
              <td class="headercell1" colspan="100%">
                <b>Supporting Materials</b>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="100%">Use the Create Note box below to attach supporting materials to your request. Notes may be added with or without attachments. Click the red \'save\' button on the right.</td>
            </tr>
          </table>
          <br xmlns=""/>
        </xsl:template>
        <xsl:template name="nbsp">
          <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:template>
      </xsl:stylesheet>
')
/
INSERT INTO KRCR_STYLE_T (ACTV_IND,NM,OBJ_ID,STYLE_ID,VER_NBR,XML)
  VALUES (1,'eDoc.Example1.Style','d7f09fa4-23f8-40b0-9346-18b532e640d1','2021',1,'<xsl:stylesheet xmlns:my-class="xalan://org.kuali.rice.edl.framework.util.EDLFunctions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
        <!-- widgets is simply more xslt that contains common functionality that greatly simplifies html rendering. It is somewhat complicated but does not require changes or full understanding unless enhancements are required.  -->
        <xsl:include href="widgets"/>
        <xsl:output indent="yes" method="html" omit-xml-declaration="yes" version="4.01"/>
        <!-- variables in the current version of xslt cannot be changed once set. Below they are set to various values often fed by java classes or to values contained in workflow xml. Not all of these are used in this form but are shown because often they can be useful. The ones prefixed with my-class are methods that are exposed by workflow to Edoclite.-->
        <xsl:variable name="actionable" select="/documentContent/documentState/actionable"/>
        <xsl:variable name="docHeaderId" select="/documentContent/documentState/docId"/>
        <xsl:variable name="editable" select="/documentContent/documentState/editable"/>
        <xsl:variable name="globalReadOnly" select="/documentContent/documentState/editable != \'true\'"/>
        <xsl:variable name="docStatus" select="//documentState/workflowDocumentState/status"/>
        <xsl:variable name="isAtNodeInitiated" select="my-class:isAtNode($docHeaderId, \'Initiated\')"/>
        <xsl:variable name="isPastInitiated" select="my-class:isNodeInPreviousNodeList(\'Initiated\', $docHeaderId)"/>
        <xsl:variable name="isUserInitiator" select="my-class:isUserInitiator($docHeaderId)"/>
        <xsl:param name="overrideMain" select="\'true\'"/>
        <!-- mainForm begins here. Execution of stylesheet begins here. It calls other templates which can call other templates. Position of templates beyond this point do not matter. -->
        <xsl:template name="mainForm">
          <html xmlns="">
            <head>
              <script language="javascript"/>
              <xsl:call-template name="htmlHead"/>
            </head>
            <body onload="onPageLoad()">
              <xsl:call-template name="errors"/>
              <!-- the header is usefule because it tells the user whether they are in \'Editing\' mode or \'Read Only\' mode. -->
              <xsl:call-template name="header"/>
              <xsl:call-template name="instructions"/>
              <xsl:variable name="formTarget" select="\'EDocLite\'"/>
              <!-- validateOnSubmit is a function in edoclite1.js which also supports edloclite forms and can be somewhat complicated but does not require modification unless enhancements are required. -->
              <form action="{$formTarget}" enctype="multipart/form-data" id="edoclite" method="post" onsubmit="return validateOnSubmit(this)">
                <xsl:call-template name="hidden-params"/>
                <xsl:call-template name="mainBody"/>
                <xsl:call-template name="notes"/>
                <br/>
                <xsl:call-template name="buttons"/>
                <br/>
              </form>
              <xsl:call-template name="footer"/>
            </body>
          </html>
        </xsl:template>
        <!-- mainBody template begins here. It calls other templates which can call other templates. Position of templates do not matter. -->
        <xsl:template name="mainBody">
          <!--
to debug, or see values of previously created, the uncomment the following line to see value of $docStatus rendered on form. -->
          <!-- $docStatus=<xsl:value-of select="$docStatus" />
-->
          <!-- rest of this all is within the form table -->
          <table align="center" border="0" cellpadding="0" cellspacing="0" class="bord-r-t" width="80%" xmlns="">
            <tr>
              <td align="left" border="3" class="thnormal" colspan="1">
                <br/>
                <h3>
                  Indiana University
                  <br/>
                  EDL EDoclite Example
                </h3>
                <br/>
              </td>
              <td align="center" border="3" class="thnormal" colspan="2">
                <br/>
                <h2>eDocLite Example 1 Form</h2>
              </td>
            </tr>
            <tr>
              <td class="headercell5" colspan="100%">
                <b>User Information</b>
              </td>
            </tr>
            <tr>
              <!-- IU usually autofills initiator name from Authentication system, and makes it readOnly. See other examples for this type of behavior. -->
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'userName\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'userName\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="headercell5" colspan="100%">
                <b>Other Information</b>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'rqstDate\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'rqstDate\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'campus\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
                <font color="#ff0000">*</font>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'campus\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'description\'"/>
                  <xsl:with-param name="renderCmd" select="\'title\'"/>
                </xsl:call-template>
              </td>
              <td class="datacell">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'description\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="2">
                <b>(Check all that apply)</b>
              </td>
            </tr>
            <tr>
              <td class="datacell" colspan="2">
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'fundedBy\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
                <br/>
                <xsl:call-template name="widget_render">
                  <xsl:with-param name="fieldName" select="\'researchHumans\'"/>
                  <xsl:with-param name="renderCmd" select="\'input\'"/>
                  <xsl:with-param name="readOnly" select="$isPastInitiated"/>
                </xsl:call-template>
                <br/>
              </td>
            </tr>
            <tr>
              <td class="headercell1" colspan="100%">
                <b>Supporting Materials</b>
              </td>
            </tr>
            <tr>
              <td class="thnormal" colspan="100%">Use the Create Note box below to attach supporting materials to your request. Notes may be added with or without attachments. Click the red \'save\' button on the right.</td>
            </tr>
          </table>
          <br xmlns=""/>
        </xsl:template>
        <xsl:template name="nbsp">
          <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:template>
      </xsl:stylesheet>
')
/

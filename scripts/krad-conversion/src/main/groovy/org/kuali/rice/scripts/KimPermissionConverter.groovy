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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import groovy.sql.Sql
import java.util.UUID

/**
 * KimPermissionConverter.groovy
 *
 * A groovy class which can be used to update KNS to KRAD.
 * Examines a Rice database looking for KNS related KIM permissions and creates a SQL script file
 * which creates equivalent KRAD permissions.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
class KimPermissionConverter {

    def config;

    // db connection parameters
    def username;
    def password;
    def url;
    String platform = "";
    def driver;

    // directory and path structure
    def dir;
    def outputFile;
    def sequencePrefix;

    // sql processing vars
    def sql;
    String sqlStatementDelimiter = "";
    def kimAttributeDefinitions = [:];

    // db constants
    String kradNamespaceCode = "KR-KRAD";
    String knsNamespaceCode = "KR-NS";

    String viewInquiryFieldPermTemplateName = "View Inquiry or Maintenance Document Field";
    String viewFieldPermTemplateName = "View Field";
    String viewInquirySectionPermTemplateName = "View Inquiry or Maintenance Document Section";
    String viewGroupPermTemplateName = "View Group";

    // canned select statements
    String selectPermissionTemplateByName = "SELECT * FROM KRIM_PERM_TMPL_T WHERE NM = ? AND NMSPC_CD = ? AND ACTV_IND = 'Y'";
    String selectPermissionsByTemplate = "SELECT * FROM KRIM_PERM_T WHERE PERM_TMPL_ID = ?";
    String selectPermissionAttributes = "SELECT * FROM KRIM_PERM_ATTR_DATA_T WHERE PERM_ID = ?";
    String selectRoleMembersByPermId = "SELECT * FROM KRIM_ROLE_PERM_T WHERE PERM_ID = ?";
    String selectAttributeDefinitionIds = "SELECT NM, NMSPC_CD, KIM_ATTR_DEFN_ID FROM KRIM_ATTR_DEFN_T WHERE NMSPC_CD='KR-NS' OR NMSPC_CD='KR-KRAD' AND ACTV_IND = 'Y'";

    // insert statement help strings
    String insertIntoPermissionTable = "INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, VER_NBR) VALUES ('";
    String insertIntoPermissionAttributesTable = "INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR) VALUES ('";
    String insertIntoRoleMembershipTable = "INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND, VER_NBR) VALUES ('";

    public KimPermissionConverter(config) {
        init(config);
    }

    def init(config) {
        // get database connection config properties
        username = config.datasource.username;
        password = config.datasource.password;
        url = config.datasource.url;
        driver = config.datasource.driver.name;
        platform = config.datasource.platform;

        dir = config.output.dir + config.output.path.db.sql;
        sequencePrefix = config.output.filePrefix;
        sqlStatementDelimiter = (platform.toUpperCase().contains('ORACLE')) ? "/\n" : ";\n";
    }


    /**
     * searches database for KNS related KIM permissions in a Rice database
     * and generates a SQL script file for creating KRAD equivalents of those permissions found.
     */
    public void convertKimPermissions() {
        log.finer("converting KIM permissions");
        connectToDb();
        outputFile = new File(dir, 'KradKimPermissions_' + platform + '.sql');
        outputFile << "-- KRAD KIM Permissions\n";

        // select kim attributes for later use
        kimAttributeDefinitions = sql.rows(selectAttributeDefinitionIds);

        // transform inquiry permissions
        transformViewMaintenanceInquiryFieldPermissions();
        transformViewMaintenanceInquirySectionPermissions();

        // just for debug to know it completed w/o runtime error, remove when completed
        log.finer("finished converting KIM Permissions.")
        outputFile << "-- Finished\n" ;
    }


    /**
     * creates a SQL connection to the rice database
     */
    public void connectToDb() {
        log.finer("connecting to database: " + url + " as user: " + username);
        sql = Sql.newInstance(url, username, password, driver)
    }

    /**
     * Transforms KNS permission used to override the hide field security attribute into the KRAD equivalent.
     * KNS version uses VIEW_MAINTENANCE_INQUIRY_FIELD permission template.
     * KRAD equivalent uses VIEW_FIELD permission template.
     */
    protected void transformViewMaintenanceInquiryFieldPermissions() {
        transformPermissions(viewInquiryFieldPermTemplateName, viewFieldPermTemplateName)
    }

    /**
     * Transforms KNS permission used to override the hide field security attribute into the KRAD equivalent.
     * KNS version uses VIEW_MAINTENANCE_INQUIRY_SECTION permission template.
     * KRAD equivalent uses VIEW_GROUP permission template.
     */
    protected void transformViewMaintenanceInquirySectionPermissions() {
        transformPermissions(viewInquirySectionPermTemplateName, viewGroupPermTemplateName)
    }

    /**
     * Transforms KNS permission used to override the hide field security attribute into the KRAD equivalent.
     *
     * Ouput:
     * - insert new permission into KRIM_PERM_T
     * - insert new permissions attributes (viewId, fieldId, propertyName) into KRIM_PERM_ATTR_DATA_T
     * - insert role assigments into  KRIM_ROLE_MBR_T
     *
     * DB Info needed
     * - KIM_TYP_ID of Krad View Field Permission
     * - ATTR_DEFN_ID of 3 KimAttributes related to Krad View Field Permission
     */
    protected void transformPermissions(knsTemplateName, kradTemplateName) {

        // get KRAD View Field permission template. we need the KimTypeId
        // for creating the new permissions attributes to insert into KRIM_PERM_ATTR_DATA_T table
        def permTemplate = sql.firstRow(selectPermissionTemplateByName, [kradTemplateName, kradNamespaceCode] );

        // get existing kim permission template(s)."
        // should be only 1
        def tmpl = sql.firstRow(selectPermissionTemplateByName,[knsTemplateName, knsNamespaceCode]);
        def tmplId = tmpl.PERM_TMPL_ID;

        // find the existing permissions based off this template
        sql.eachRow(selectPermissionsByTemplate, [tmplId]) { perm ->
            outputFile << "-- Insert new ${kradTemplateName} permission.\n";
            def newPermId = sequencePrefix + perm.PERM_ID;

            // create an open view permission
            def createPermString = insertIntoPermissionTable + newPermId + "', '" + getUuid() + "', '" + permTemplate.PERM_TMPL_ID +
                    "', '" + kradNamespaceCode + "', '" + perm.NM + "', '" +  perm.DESC_TXT + "', 'Y', 1)";
            outputFile << createPermString + "\n" + sqlStatementDelimiter;

            // select existing related attributes for this KNS permission
            // create the corresponding perm attributes for the new krad  permission
            outputFile << "-- Insert new ${kradTemplateName} permission attributes\n";
            sql.eachRow(selectPermissionAttributes, [perm.PERM_ID]){ oldAttr ->
                def newAttributeValues = mapPermissionAttribute( oldAttr );
                def createAttrString = insertIntoPermissionAttributesTable + newAttributeValues['ATTR_DATA_ID'] +
                        "', '" + getUuid() + "', '" + newPermId + "', '" + permTemplate.KIM_TYP_ID + "', '" +
                        newAttributeValues['KIM_ATTR_DEFN_ID'] + "', '" + newAttributeValues['ATTR_VAL'] + "', 1)";
                outputFile << createAttrString + "\n" + sqlStatementDelimiter;
            }

            // get all of the roles assigned to the KNS permission
            outputFile << "-- Insert New Role Memberships (if any) for permission." + "\n";
            sql.eachRow(selectRoleMembersByPermId, [perm.PERM_ID]){ roleMember ->
                // add role assignments for the new KRAD permission
                def newRolePermId =  sequencePrefix + roleMember.ROLE_PERM_ID;
                def createRolePermString = insertIntoRoleMembershipTable + newRolePermId +
                        "', '" + getUuid() + "', '" + roleMember.ROLE_ID + "', '" + newPermId + "', 'Y', 1)";
                outputFile << createRolePermString + "\n" + sqlStatementDelimiter;
            }
        }
    }

    /**
     * */
    /**
     *  Maps KNS Kim Attributes into corresponding KRAD Kim Attributes.
     *
     *  Some KRAD permissions use a different set of attributes than their KNS equivalents.
     *  This method handles the mapping of a KNS Kim Attribute into a KRAD Attribute;
     *
     *  componentName -> viewId
     *  sectionId -> groupId
     *  */
    def mapPermissionAttribute(oldAttr){
        Map newAttribute = [:];
        newAttribute.put('ATTR_DATA_ID', sequencePrefix + oldAttr.ATTR_DATA_ID);

        //  convert KNS componentName, to a KRAD veiwID attribute with a wildCard * appended to the value.
        if (oldAttr.KIM_ATTR_DEFN_ID == getAttributeDefinitionId("componentName")){
            newAttribute.put('KIM_ATTR_DEFN_ID', getAttributeDefinitionId("viewId"));
            newAttribute.put('ATTR_VAL', oldAttr.ATTR_VAL + '*');
        } else if (oldAttr.KIM_ATTR_DEFN_ID == getAttributeDefinitionId("sectionId")){
            newAttribute.put('KIM_ATTR_DEFN_ID', getAttributeDefinitionId("groupId"));
            newAttribute.put('ATTR_VAL', oldAttr.ATTR_VAL);
        } else {
            // copy the old Id and Value into the new
            newAttribute.put('KIM_ATTR_DEFN_ID', oldAttr.KIM_ATTR_DEFN_ID);
            newAttribute.put('ATTR_VAL', oldAttr.ATTR_VAL);
        }

        return newAttribute;
    }

    /**
     * Returns the Attribute Id given the Attribute Name
     * @param name name of the attribute
     * @return id of the attribute
     */
    def String getAttributeDefinitionId(String name){
        return kimAttributeDefinitions.findAll{ it.NM == name}.KIM_ATTR_DEFN_ID[0];
    }

    /**
     * create a unique identifier
     * @return unique identifier string
     */
    def getUuid() {
        return UUID.randomUUID() as String;
    }
}
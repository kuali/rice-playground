/*
 * Copyright 2011 The Kuali Foundation
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
package org.rice.krms.test;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.Facts;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItem;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.framework.engine.ResultLogger;
import org.kuali.rice.krms.framework.engine.result.EngineResultListener;
import org.kuali.rice.krms.framework.engine.result.Log4jResultListener;
import org.kuali.rice.krms.framework.type.ValidationActionType;
import org.kuali.rice.krms.framework.type.ValidationActionTypeService;
import org.kuali.rice.krms.framework.type.ValidationRuleType;
import org.kuali.rice.krms.framework.type.ValidationRuleTypeService;
import org.kuali.rice.krms.impl.repository.ActionAttributeBo;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.ActionBoService;
import org.kuali.rice.krms.impl.repository.ActionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaAttributeBo;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaBoService;
import org.kuali.rice.krms.impl.repository.AgendaBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextAttributeBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.PropositionBoService;
import org.kuali.rice.krms.impl.repository.PropositionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleAttributeBo;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.krms.impl.repository.RuleBoService;
import org.kuali.rice.krms.impl.repository.RuleBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.impl.util.KRMSServiceLocatorInternal;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Validation Integration Test
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.CLEAR_DB)
public class ValidationIntegrationTest extends AbstractBoTest {

    private static final String EVENT_ATTRIBUTE = "Event";
//    private static final String TERM_NAME = "totalProposalDollarAmount";
    private static final String TERM_NAME = "campusCodeTermSpec";

    private static final String CONTEXT_NAME = "ValidationITContext";

    private KrmsTypeRepositoryService krmsTypeRepositoryService;
    private KrmsAttributeDefinitionService krmsAttributeDefinitionService;
    private PropositionBoService propositionBoService;
    private TermBoService termBoService;
    private ContextBoService contextRepository;
    private AgendaBoService agendaBoService;
    private RuleBoService ruleBoService;
    private ActionBoService actionBoService;


	@Before
	public void setup() {
		super.setup();
        krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        krmsTypeRepositoryService = new KrmsTypeBoServiceImpl();
        ((KrmsTypeBoServiceImpl)krmsTypeRepositoryService).setBusinessObjectService(getBoService());

        // like RepositoryCreateAndExecuteIntegrationTest
        propositionBoService = new PropositionBoServiceImpl();
        ((PropositionBoServiceImpl)propositionBoService).setBusinessObjectService(getBoService());
        termBoService = new TermBoServiceImpl();
        ((TermBoServiceImpl)termBoService).setBusinessObjectService(getBoService());
        contextRepository = new ContextBoServiceImpl();
        ((ContextBoServiceImpl)contextRepository).setBusinessObjectService(getBoService());
        agendaBoService = new AgendaBoServiceImpl();
        ((AgendaBoServiceImpl)agendaBoService).setBusinessObjectService(getBoService());
        ((AgendaBoServiceImpl)agendaBoService).setAttributeDefinitionService(krmsAttributeDefinitionService);
        ruleBoService = new RuleBoServiceImpl();
        ((RuleBoServiceImpl)ruleBoService).setBusinessObjectService(getBoService());
        actionBoService = new ActionBoServiceImpl();
        ((ActionBoServiceImpl)actionBoService).setBusinessObjectService(getBoService());
    }

    @Transactional
    @Test
    public void testValidWarning() {
        KrmsAttributeTypeDefinitionAndBuilders ruleDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE, ValidationRuleType.VALID.toString(), true, ValidationRuleType.VALID.toString(),
                ValidationRuleType.VALID.getCode(), "validationRuleTypeService", krmsTypeRepositoryService, 1);
        KrmsAttributeTypeDefinitionAndBuilders actionDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                "ValidationWarningActionAttributeName", KrmsConstants.KRMS_NAMESPACE, "ValidationWarningActionLabel", true, "ValidationWarningActionTypeName",
                ValidationActionType.WARNING.getCode(), "validationWarningActionTypeService", krmsTypeRepositoryService, 1);

        ContextBo contextBo = createContext();
        RuleBo ruleBo = createRuleWithAction(ruleDefs, actionDefs, contextBo, "Warning Message (as description)");
        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute();
        assertTrue(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidWarning() {
        KrmsAttributeTypeDefinitionAndBuilders ruleDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE, ValidationRuleType.INVALID.toString(), true, ValidationRuleType.INVALID.toString(),
                ValidationRuleType.INVALID.getCode(), "validationRuleTypeService", krmsTypeRepositoryService, 1);
        KrmsAttributeTypeDefinitionAndBuilders actionDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                "ValidationWarningActionAttributeName", KrmsConstants.KRMS_NAMESPACE, "ValidationWarningActionLabel", true, "ValidationWarningActionTypeName",
                ValidationActionType.WARNING.getCode(), "validationWarningActionTypeService", krmsTypeRepositoryService, 1);

        ContextBo contextBo = createContext();
        RuleBo ruleBo = createRuleWithAction(ruleDefs, actionDefs, contextBo, "Warning Message (as description)");
        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute();
        assertNotNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testValidError() {
        KrmsAttributeTypeDefinitionAndBuilders ruleDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE, ValidationRuleType.VALID.toString(), true, ValidationRuleType.VALID.toString(),
                ValidationRuleType.VALID.getCode(), "validationRuleTypeService", krmsTypeRepositoryService, 1);
        KrmsAttributeTypeDefinitionAndBuilders actionDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                "ValidationErrorActionAttributeName", KrmsConstants.KRMS_NAMESPACE, "ValidationErrorActionLabel", true, "ValidationErrorActionTypeName",
                ValidationActionType.ERROR.getCode(), "validationErrorActionTypeService", krmsTypeRepositoryService, 1);

        ContextBo contextBo = createContext();
        RuleBo ruleBo = createRuleWithAction(ruleDefs, actionDefs, contextBo, "Error Message (as description)");
        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute();
        assertTrue(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidError() {
        KrmsAttributeTypeDefinitionAndBuilders ruleDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE, ValidationRuleType.INVALID.toString(), true, ValidationRuleType.INVALID.toString(),
                ValidationRuleType.INVALID.getCode(), "validationRuleTypeService", krmsTypeRepositoryService, 1);
        KrmsAttributeTypeDefinitionAndBuilders actionDefs = createKrmsAttributeTypeDefinitionAndBuilders(
                "ValidationErrorActionAttributeName", KrmsConstants.KRMS_NAMESPACE, "ValidationErrorActionLabel", true, "ValidationErrorActionTypeName",
                ValidationActionType.ERROR.getCode(), "validationErrorActionTypeService", krmsTypeRepositoryService, 1);

        ContextBo contextBo = createContext();
        RuleBo ruleBo = createRuleWithAction(ruleDefs, actionDefs, contextBo, "Error Message (as description)");
        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute();
        assertNotNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testDef() {
        ContextDefinition contextDefinition = createContextDefinition(KrmsConstants.KRMS_NAMESPACE);
        createAgendaDefinition(contextDefinition, "ValidationIntegration", KrmsConstants.KRMS_NAMESPACE);

        engineExecute();
    }

    private ContextDefinition createContextDefinition(String nameSpace) {
        // Attribute Defn for context;
        KrmsAttributeDefinition.Builder contextTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Context1Qualifier", nameSpace);
        contextTypeAttributeDefnBuilder.setLabel("Context 1 Qualifier");
        KrmsAttributeDefinition contextTypeAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(contextTypeAttributeDefnBuilder.build());

        // Attr for context;
        KrmsTypeAttribute.Builder krmsTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, contextTypeAttributeDefinition.getId(), 1);

        // KrmsType for context
        KrmsTypeDefinition.Builder krmsContextTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsTestContextType", nameSpace);
        krmsContextTypeDefnBuilder.setAttributes(Collections.singletonList(krmsTypeAttrBuilder));
        KrmsTypeDefinition krmsContextTypeDefinition = krmsContextTypeDefnBuilder.build();
        krmsContextTypeDefinition = krmsTypeRepositoryService.createKrmsType(krmsContextTypeDefinition);

        // Context
        ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create(nameSpace, CONTEXT_NAME);
        contextBuilder.setTypeId(krmsContextTypeDefinition.getId());
        ContextDefinition contextDefinition = contextBuilder.build();
        contextDefinition = contextRepository.createContext(contextDefinition);

        // Context Attribute
        // TODO: do this fur eel
        ContextAttributeBo contextAttribute = new ContextAttributeBo();
        contextAttribute.setAttributeDefinitionId(contextTypeAttributeDefinition.getId());
        contextAttribute.setContextId(contextDefinition.getId());
        contextAttribute.setValue("BLAH");
        getBoService().save(contextAttribute);

        return contextDefinition;
    }

    private void createAgendaDefinition(ContextDefinition contextDefinition, String eventName, String nameSpace ) {
        KrmsTypeDefinition krmsGenericTypeDefinition = createKrmsGenericTypeDefinition(nameSpace);

        AgendaDefinition agendaDef =
            AgendaDefinition.Builder.create(null, "testAgenda", krmsGenericTypeDefinition.getId(), contextDefinition.getId()).build();
        agendaDef = agendaBoService.createAgenda(agendaDef);

        AgendaItem.Builder agendaItemBuilder1 = AgendaItem.Builder.create(null, agendaDef.getId());
        agendaItemBuilder1.setRuleId(createRuleDefinition1(contextDefinition, nameSpace).getId());

        AgendaItem agendaItem1 = agendaBoService.createAgendaItem(agendaItemBuilder1.build());

        AgendaDefinition.Builder agendaDefBuilder1 = AgendaDefinition.Builder.create(agendaDef);
        agendaDefBuilder1.setAttributes(Collections.singletonMap("Event", eventName));
        agendaDefBuilder1.setFirstItemId(agendaItem1.getId());
        agendaDef = agendaDefBuilder1.build();

        agendaBoService.updateAgenda(agendaDef);
    }

    private KrmsTypeDefinition createKrmsGenericTypeDefinition(String nameSpace) {
	    // Attribute Defn for generic type;
        KrmsAttributeDefinition.Builder genericTypeAttributeDefnBuilder = KrmsAttributeDefinition.Builder.create(null, "Event", nameSpace);
        genericTypeAttributeDefnBuilder.setLabel("event name");
        KrmsAttributeDefinition genericTypeAttributeDefinition1 = krmsAttributeDefinitionService.createAttributeDefinition(genericTypeAttributeDefnBuilder.build());

        // Attr for generic type;
        KrmsTypeAttribute.Builder genericTypeAttrBuilder = KrmsTypeAttribute.Builder.create(null, genericTypeAttributeDefinition1.getId(), 1);

		// Can use this generic type for KRMS bits that don't actually rely on services on the bus at this point in time
	    KrmsTypeDefinition.Builder krmsGenericTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsTestGenericType", nameSpace);
	    krmsGenericTypeDefnBuilder.setAttributes(Collections.singletonList(genericTypeAttrBuilder));
	    KrmsTypeDefinition krmsGenericTypeDefinition = krmsTypeRepositoryService.createKrmsType(krmsGenericTypeDefnBuilder.build());

        return krmsGenericTypeDefinition;
    }

    private RuleDefinition createRuleDefinition1(ContextDefinition contextDefinition, String nameSpace) {
        // Rule 1
        RuleDefinition.Builder ruleDefBuilder1 =
            RuleDefinition.Builder.create(null, "Rule1", nameSpace, createKrmsCampusTypeDefinition(nameSpace).getId(), null);
        RuleDefinition ruleDef1 = ruleBoService.createRule(ruleDefBuilder1.build());


        ruleDefBuilder1 = RuleDefinition.Builder.create(ruleDef1);
        ruleDefBuilder1.setProposition(createCompoundProposition(contextDefinition, ruleDef1));
        ruleDef1 = ruleDefBuilder1.build();
        ruleBoService.updateRule(ruleDef1);

        // Action
        ActionDefinition.Builder actionDefBuilder1 = ActionDefinition.Builder.create(null, "testAction1", nameSpace, createKrmsActionTypeDefinition(nameSpace).getId(), ruleDef1.getId(), 1);
        ActionDefinition actionDef1 = actionBoService.createAction(actionDefBuilder1.build());

        return ruleDef1;
    }
    

    private KrmsTypeDefinition createKrmsCampusTypeDefinition(String nameSpace) {
	    // KrmsType for campus svc
        KrmsTypeDefinition.Builder krmsCampusTypeDefnBuilder = KrmsTypeDefinition.Builder.create("CAMPUS", nameSpace);
        KrmsTypeDefinition krmsCampusTypeDefinition = krmsTypeRepositoryService.createKrmsType(krmsCampusTypeDefnBuilder.build());
        return krmsCampusTypeDefinition;
    }

    private KrmsTypeDefinition createKrmsActionTypeDefinition(String nameSpace) {
        KrmsTypeDefinition.Builder krmsActionTypeDefnBuilder = KrmsTypeDefinition.Builder.create("KrmsActionResolverType", nameSpace);
        krmsActionTypeDefnBuilder.setServiceName("testActionTypeService");
        KrmsTypeDefinition krmsActionTypeDefinition = krmsTypeRepositoryService.createKrmsType(krmsActionTypeDefnBuilder.build());

        return krmsActionTypeDefinition;
    }


    private EngineResults engineExecute() {
        Map<String, String> contextQualifiers = new HashMap<String, String>();
        contextQualifiers.put("name", CONTEXT_NAME);
        contextQualifiers.put("namespaceCode", KrmsConstants.KRMS_NAMESPACE);

        SelectionCriteria sc1 = SelectionCriteria.createCriteria(new DateTime(),
                contextQualifiers, Collections.singletonMap(AgendaDefinition.Constants.EVENT, EVENT_ATTRIBUTE));

	    // Need to initialize ResultLogger to get results.
	    // TODO: I'm concerned about how this will deal w/ concurrency.
	    ResultLogger resultLogger = ResultLogger.getInstance();
        EngineResultListener engineResultListener = new EngineResultListener();
	    resultLogger.addListener(engineResultListener);
        resultLogger.addListener(new Log4jResultListener());

        Facts.Builder factsBuilder1 = Facts.Builder.create();
//        factsBuilder1.addFact(TERM_NAME, 49999);
        factsBuilder1.addFact(TERM_NAME, "BL");

        ExecutionOptions xOptions1 = new ExecutionOptions();
        xOptions1.setFlag(ExecutionFlag.LOG_EXECUTION, true);

        EngineResults engineResults = KrmsApiServiceLocator.getEngine().execute(sc1, factsBuilder1.build(), xOptions1);
        assertNotNull(engineResults);
        assertTrue(engineResults.getAllResults().size() > 0);
        print(engineResults);
        return engineResults;
    }

    private void print(EngineResults engineResults) {
        System.out.println(ToStringBuilder.reflectionToString(engineResults, ToStringStyle.MULTI_LINE_STYLE));
    }

    private ContextBo createContext() {
        KrmsAttributeTypeDefinitionAndBuilders defs = createKrmsAttributeTypeDefinitionAndBuilders(
                "ContextAttributeName", KrmsConstants.KRMS_NAMESPACE, "ContextLabel", true, "ContextTypeName",
                "ContextTypeId", "ContextServiceName", krmsTypeRepositoryService, 1);

        ContextBo contextBo = new ContextBo();
        contextBo.setNamespace(KrmsConstants.KRMS_NAMESPACE);
        contextBo.setName(CONTEXT_NAME);
        contextBo.setTypeId(defs.typeDef.getId());
        return (ContextBo)getBoService().save(contextBo);
    }

    private KrmsAttributeTypeDefinitionAndBuilders createKrmsAttributeTypeDefinitionAndBuilders(String attributeName,
            String namespace, String label, boolean active, String typeName, String typeId, String serviceName,
            KrmsTypeRepositoryService krmsTypeRepositoryService, Integer sequenceNumber) {
        KrmsAttributeDefinitionBo attributeDefinitionBo = new KrmsAttributeDefinitionBo();
        attributeDefinitionBo.setNamespace(namespace);
        attributeDefinitionBo.setName(attributeName);
        attributeDefinitionBo.setLabel(label);
        attributeDefinitionBo.setActive(active);
        attributeDefinitionBo = (KrmsAttributeDefinitionBo)getBoService().save(attributeDefinitionBo);
        assertNotNull(attributeDefinitionBo.getId());
        KrmsAttributeDefinition attribDef = KrmsAttributeDefinitionBo.to(attributeDefinitionBo);

        KrmsTypeDefinition.Builder typeDefinition = KrmsTypeDefinition.Builder.create(
            typeName, namespace);
        typeDefinition.setServiceName(serviceName);
        KrmsTypeAttribute.Builder attribDefinitionBuilder = KrmsTypeAttribute.Builder.create(typeId, attribDef.getId(), sequenceNumber);
        typeDefinition.getAttributes().add(attribDefinitionBuilder);
        KrmsTypeDefinition typeDef = krmsTypeRepositoryService.createKrmsType(typeDefinition.build());
        assertNotNull(typeDef);

        return new KrmsAttributeTypeDefinitionAndBuilders(attribDef, attribDefinitionBuilder, typeDef, typeDefinition);
    }

    private RuleBo createRuleWithAction(KrmsAttributeTypeDefinitionAndBuilders ruleBits,
            KrmsAttributeTypeDefinitionAndBuilders actionBits, ContextBo contextBo, String description) {

        RuleBo rule = new RuleBo();
        rule.setTypeId(ruleBits.typeDef.getId());
        rule.setNamespace(ruleBits.typeDef.getNamespace());
        rule.setName(ruleBits.typeDef.getName());
        List<RuleAttributeBo> ruleAttributes = new ArrayList<RuleAttributeBo>();
        rule.setAttributeBos(ruleAttributes);
        RuleAttributeBo ruleType = new RuleAttributeBo();
        ruleAttributes.add(ruleType);
        ruleType.setAttributeDefinitionId(ruleBits.attribDef.getId());
        ruleType.setAttributeDefinition(KrmsAttributeDefinitionBo.from(ruleBits.attribDef));
        ruleType.setValue(ruleBits.typeAttribBuilder.getTypeId());
        ruleType.setRuleId(rule.getId());

        List<ActionBo> actions = new ArrayList<ActionBo>();
        ActionBo action = new ActionBo();
        action.setTypeId(actionBits.typeDef.getId());
        action.setDescription(description);
        actions.add(action);
        action.setNamespace(actionBits.typeDef.getNamespace());
        action.setName(actionBits.typeDef.getName());
        action.setSequenceNumber(actionBits.typeAttribBuilder.getSequenceNumber());
        Set<ActionAttributeBo> actionAttributes = new HashSet<ActionAttributeBo>();
        action.setAttributeBos(actionAttributes);
        ActionAttributeBo actionAttribute = new ActionAttributeBo();
        actionAttributes.add(actionAttribute);
        actionAttribute.setAttributeDefinitionId(actionBits.attribDef.getId());
        actionAttribute.setAttributeDefinition(KrmsAttributeDefinitionBo.from(actionBits.attribDef));
        actionAttribute.setValue("actionAttributeValue");

        rule = (RuleBo) getBoService().save(rule);
        RuleDefinition ruleDef = RuleBo.to(rule);
        
        PropositionDefinition propDef = createPropositionDefinition1(ContextBo.to(contextBo), ruleDef).build();
        propDef = propositionBoService.createProposition(propDef);
        rule.setPropId(propDef.getId());
        rule.setActions(actions);
        rule = (RuleBo) getBoService().save(rule);

        assertNotNull(rule.getId());
        assertNotNull(rule.getPropId());
        assertEquals(1, rule.getActions().size());
        assertNotNull(rule.getActions().get(0).getId());
        assertEquals(1, rule.getActions().get(0).getAttributeBos().size());
        return rule;
    }

    private PropositionDefinition.Builder createPropositionDefinition1(ContextDefinition contextDefinition, RuleDefinition ruleDef1) {
        // Proposition for rule 1
        PropositionDefinition.Builder propositionDefBuilder1 =
            PropositionDefinition.Builder.create(null, PropositionType.SIMPLE.getCode(), ruleDef1.getId(), null /* type code is only for custom props */, Collections.<PropositionParameter.Builder>emptyList());
        propositionDefBuilder1.setDescription("is campus bloomington");

        // PropositionParams for rule 1
        List<PropositionParameter.Builder> propositionParams1 = new ArrayList<PropositionParameter.Builder>();
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, createTermDefinition1(contextDefinition).getId(), PropositionParameterType.TERM.getCode(), 1)
        );
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, "BL", PropositionParameterType.CONSTANT.getCode(), 2)
        );
        propositionParams1.add(
                PropositionParameter.Builder.create(null, null, "=", PropositionParameterType.OPERATOR.getCode(), 3)
        );

        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParams1) {
            propositionParamBuilder.setProposition(propositionDefBuilder1);
        }

        propositionDefBuilder1.setParameters(propositionParams1);

        return propositionDefBuilder1;
    }


    private PropositionDefinition.Builder createCompoundProposition(ContextDefinition contextDefinition,
            RuleDefinition ruleDef1) {
        // Proposition for rule 1
        List<PropositionParameter.Builder> propositionParameterBuilderList = new ArrayList<PropositionParameter.Builder>();
        propositionParameterBuilderList.add(PropositionParameter.Builder.create(null, null, createTermDefinition1(contextDefinition).getId(),
                PropositionParameterType.TERM.getCode(), 1)
        );
        PropositionDefinition.Builder propositionDefBuilder1 =
            PropositionDefinition.Builder.create(null, PropositionType.COMPOUND.getCode(), ruleDef1.getId(), null /* type code is only for custom props */, propositionParameterBuilderList);
        propositionDefBuilder1.setDescription("propositionDefBuilder1 Description");

        // PropositionParams for rule 1
//        List<PropositionParameter.Builder> propositionParams1 = new ArrayList<PropositionParameter.Builder>();
//        propositionParams1.add(
//                PropositionParameter.Builder.create(null, null, createTermDefinition1(contextDefinition).getId(), PropositionParameterType
//                        .TERM.getCode(), 1)
//        );

        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParameterBuilderList) {
            propositionParamBuilder.setProposition(propositionDefBuilder1);
        }

//        propositionDefBuilder1.setParameters(propositionParams1);

        return propositionDefBuilder1;
    }

    private TermDefinition createTermDefinition1(ContextDefinition contextDefinition) {
        // campusCode TermSpec
        TermSpecificationDefinition campusCodeTermSpec =
            TermSpecificationDefinition.Builder.create(null, "campusCodeTermSpec", contextDefinition.getId(),
                    "java.lang.String").build();
        campusCodeTermSpec = termBoService.createTermSpecification(campusCodeTermSpec);

        // Term 1
        TermDefinition termDefinition1 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(campusCodeTermSpec), null).build();
        termDefinition1 = termBoService.createTermDefinition(termDefinition1);

        return termDefinition1;
    }

    private TermDefinition createTermDefinitionInteger(ContextDefinition contextDefinition) {
        // campusCode TermSpec
        TermSpecificationDefinition termSpec =
            TermSpecificationDefinition.Builder.create(null, TERM_NAME, contextDefinition.getId(),
                    "java.lang.Integer").build();
        termSpec = termBoService.createTermSpecification(termSpec);

        // Term 1
        TermDefinition termDefinition1 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(termSpec), null).build();
        termDefinition1 = termBoService.createTermDefinition(termDefinition1);

        return termDefinition1;
    }


    private KrmsAttributeDefinitionBo createEventAttributeDefinition() {
        KrmsAttributeDefinitionService service = KRMSServiceLocatorInternal.getService("krmsAttributeDefinitionService");
        assertNotNull(service);
        KrmsAttributeDefinitionBo attributeDefinitionBo = new KrmsAttributeDefinitionBo();
        attributeDefinitionBo.setNamespace(KrmsConstants.KRMS_NAMESPACE);
        attributeDefinitionBo.setName(EVENT_ATTRIBUTE);
        attributeDefinitionBo.setLabel("Event");
        attributeDefinitionBo.setActive(true);
        attributeDefinitionBo = (KrmsAttributeDefinitionBo) getBoService().save(attributeDefinitionBo);
        assertNotNull(attributeDefinitionBo.getId());
        return attributeDefinitionBo;
    }

    private AgendaBo createAgenda(RuleBo ruleBo, ContextBo contextBo, KrmsAttributeDefinitionBo eventAttributeDefinition) {
        KrmsAttributeTypeDefinitionAndBuilders defs = createKrmsAttributeTypeDefinitionAndBuilders(
                "AgendaAttributeName", KrmsConstants.KRMS_NAMESPACE, "AgendaLabel", true, "AgendaTypeName",
                "AgendaTypeId", "AgendaServiceName", krmsTypeRepositoryService, 1);

        AgendaBo agendaBo = new AgendaBo();
        agendaBo.setActive(defs.typeDef.isActive());
        agendaBo.setContextId(contextBo.getId());
        agendaBo.setName("MyAgenda");
        agendaBo.setTypeId(defs.typeDef.getId());
        agendaBo = (AgendaBo)getBoService().save(agendaBo);

        agendaBo.setFirstItemId(ruleBo.getId());
        AgendaItemBo agendaItemBo = new AgendaItemBo();
        agendaItemBo.setRule(ruleBo);
        agendaItemBo.setAgendaId(agendaBo.getId());
        agendaItemBo = (AgendaItemBo)getBoService().save(agendaItemBo);

        List<AgendaItemBo> agendaItems = new ArrayList<AgendaItemBo>();
        agendaItems.add(agendaItemBo);
        agendaBo.setItems(agendaItems);
        agendaBo.setFirstItemId(agendaItemBo.getId());

        // also add attribute to the agenda to store event
        Set<AgendaAttributeBo> agendaAttributes = new HashSet<AgendaAttributeBo>();
        agendaBo.setAttributeBos(agendaAttributes);
        AgendaAttributeBo agendaAttribute = new AgendaAttributeBo();
        agendaAttributes.add(agendaAttribute);
        agendaAttribute.setAttributeDefinitionId(eventAttributeDefinition.getId());
        agendaAttribute.setAttributeDefinition(eventAttributeDefinition);
        agendaAttribute.setValue(EVENT_ATTRIBUTE);
//        agendaAttribute.setValue("workflow");
        agendaBo = (AgendaBo)getBoService().save(agendaBo);

        contextBo.getAgendas().add(agendaBo);

        return agendaBo;
    }

    class KrmsAttributeTypeDefinitionAndBuilders {
        KrmsTypeDefinition typeDef;
        KrmsTypeDefinition.Builder typeDefBuilder;
        KrmsAttributeDefinition attribDef;
        KrmsTypeAttribute.Builder typeAttribBuilder;
        KrmsAttributeTypeDefinitionAndBuilders(KrmsAttributeDefinition krmsAttributeDefinition, KrmsTypeAttribute.Builder krmsAttributeDefinitionBuilder,
                KrmsTypeDefinition krmsTypeDefinition, KrmsTypeDefinition.Builder krmsTypeDefinitionBuilder) {
            this.typeDef = krmsTypeDefinition;
            this.typeDefBuilder = krmsTypeDefinitionBuilder;
            this.attribDef = krmsAttributeDefinition;
            this.typeAttribBuilder = krmsAttributeDefinitionBuilder;
        }
    }
}

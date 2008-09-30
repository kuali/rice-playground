
INSERT INTO KR_KIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e1', 'e1', 'Y')
/

INSERT INTO KR_KIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e2', 'e2', 'Y')
/

INSERT INTO KR_KIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e3', 'e3', 'Y')
/

INSERT INTO KR_KIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e4', 'e4', 'Y')
/
commit
/


INSERT INTO KR_KIM_ENTITY_ENT_TYPE_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et1', 'et1', 'PERSON', 'e1', 'Y')
/

INSERT INTO KR_KIM_ENTITY_ENT_TYPE_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et2', 'et2', 'PERSON', 'e2', 'Y')
/

INSERT INTO KR_KIM_ENTITY_ENT_TYPE_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et3', 'et3', 'PERSON', 'e3', 'Y')
/

INSERT INTO KR_KIM_ENTITY_ENT_TYPE_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et4', 'et4', 'PERSON', 'e4', 'Y')
/
COMMIT
/


INSERT INTO KR_KIM_ENTITY_NAME_T(ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, NAME_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, DFLT_IND, ACTV_IND) 
    VALUES('en1', 'en1', 'e1', 'PERSON', 'PREFERRED', 'One', '', 'User', 'Y', 'Y')
/
INSERT INTO KR_KIM_ENTITY_NAME_T(ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, NAME_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, DFLT_IND, ACTV_IND) 
    VALUES('en2', 'en2', 'e2', 'PERSON', 'PREFERRED', 'Two', '', 'User', 'Y', 'Y')
/
INSERT INTO KR_KIM_ENTITY_NAME_T(ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, NAME_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, DFLT_IND, ACTV_IND) 
    VALUES('en3', 'en3', 'e3', 'PERSON', 'PREFERRED', 'Three', '', 'User', 'Y', 'Y')
/
INSERT INTO KR_KIM_ENTITY_NAME_T(ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, NAME_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, DFLT_IND, ACTV_IND) 
    VALUES('en4', 'en4', 'e4', 'PERSON', 'PREFERRED', 'Four', '', 'User', 'Y', 'Y')
/
COMMIT
/


INSERT INTO KR_KIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID, ACTV_IND) 
    VALUES('eeid1', 'eeid1', 'e1', 'EMPLOYEE', 'EXTID1', 'Y')
/
INSERT INTO KR_KIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID, ACTV_IND) 
    VALUES('eeid2', 'eeid2', 'e2', 'EMPLOYEE', 'EXTID2', 'Y')
/
INSERT INTO KR_KIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID, ACTV_IND) 
    VALUES('eeid3', 'eeid3', 'e3', 'EMPLOYEE', 'EXTID3', 'Y')
/
INSERT INTO KR_KIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID, ACTV_IND) 
    VALUES('eeid4', 'eeid4', 'e4', 'EMPLOYEE', 'EXTID$', 'Y')
/
COMMIT
/

INSERT INTO KR_KIM_PRINCIPAL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p1', 'p1', 'user1', 'e1', 'Y')
/
INSERT INTO KR_KIM_PRINCIPAL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p2', 'p2', 'user2', 'e2', 'Y')
/
INSERT INTO KR_KIM_PRINCIPAL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p3', 'p3', 'user3', 'e3', 'Y')
/
INSERT INTO KR_KIM_PRINCIPAL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p4', 'p4', 'user4', 'e4', 'Y')
/
COMMIT
/



INSERT INTO KR_KIM_GROUP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, TYP_ID, ACTV_IND) 
    VALUES('g1', 'g1', 'topgroup', 'TEST', 'Top-level group', NULL, 'Y')
/
INSERT INTO KR_KIM_GROUP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, TYP_ID, ACTV_IND) 
    VALUES('g2', 'g2', 'middlegroup', 'TEST', 'middle-level group', NULL, 'Y')
/
INSERT INTO KR_KIM_GROUP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, TYP_ID, ACTV_IND) 
    VALUES('g3', 'g3', 'bottomgroup', 'TEST', 'Bottom-level group', NULL, 'Y')
/
INSERT INTO KR_KIM_GROUP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, TYP_ID, ACTV_IND) 
    VALUES('g4', 'g4', 'bottominactivegroup', 'TEST', 'Bottom-level group (inactive', NULL, 'N')
/
COMMIT
/


INSERT INTO KR_KIM_GROUP_GROUP_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, MEMBER_GRP_ID) 
    VALUES('gg1', 'gg1', 'g1', 'g2')
/
INSERT INTO KR_KIM_GROUP_GROUP_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, MEMBER_GRP_ID) 
    VALUES('gg2', 'gg2', 'g2', 'g3')
/
INSERT INTO KR_KIM_GROUP_GROUP_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, MEMBER_GRP_ID) 
    VALUES('gg3', 'gg3', 'g2', 'g4')
/
COMMIT
/

INSERT INTO KR_KIM_GROUP_PRINCIPAL_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, PRNCPL_ID) 
    VALUES('gp1', 'gp1', 'g2', 'p1')
/
INSERT INTO KR_KIM_GROUP_PRINCIPAL_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, PRNCPL_ID) 
    VALUES('gp2', 'gp2', 'g3', 'p2')
/
INSERT INTO KR_KIM_GROUP_PRINCIPAL_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, PRNCPL_ID) 
    VALUES('gp3', 'gp3', 'g3', 'p3')
/
INSERT INTO KR_KIM_GROUP_PRINCIPAL_T(GRP_MEMBER_ID, OBJ_ID, GRP_ID, PRNCPL_ID) 
    VALUES('gp4', 'gp4', 'g4', 'p4')
/
COMMIT
/


INSERT INTO KR_KIM_ROLE_T(ROLE_ID, OBJ_ID, ROLE_NM, NMSPC_CD, ROLE_DESC, TYP_ID, ACTV_IND) 
    VALUES('r1', 'r1', 'poweruserrole', 'TEST', 'high level role that implies other roles', NULL, 'Y')
/
INSERT INTO KR_KIM_ROLE_T(ROLE_ID, OBJ_ID, ROLE_NM, NMSPC_CD, ROLE_DESC, TYP_ID, ACTV_IND) 
    VALUES('r2', 'r2', 'generalrole', 'TEST', 'role granted to large number of users', NULL, 'Y')
/

INSERT INTO KR_KIM_ROLE_REL_T(ROLE_REL_ID, OBJ_ID, ROLE_ID, CONTAINED_ROLE_ID) 
    VALUES('rr1', 'rr1', 'r1', 'r2')
/

INSERT INTO KR_KIM_ROLE_PRINCIPAL_T(ROLE_MEMBER_ID, OBJ_ID, ROLE_ID, PRNCPL_ID) 
    VALUES('rp1', 'rp1', 'r2', 'p3')
/
INSERT INTO KR_KIM_ROLE_PRINCIPAL_T(ROLE_MEMBER_ID, OBJ_ID, ROLE_ID, PRNCPL_ID) 
    VALUES('rp2', 'rp2', 'r1', 'p1')
/


INSERT INTO KR_KIM_ROLE_GROUP_T(ROLE_MEMBER_ID, OBJ_ID, ROLE_ID, GRP_ID) 
    VALUES('rg1', 'rg1', 'r2', 'g3')
/
COMMIT
/


INSERT INTO KR_KIM_PERMISSION_T(PERM_ID, OBJ_ID, NMSPC_CD, PERM_NM, KIM_TYPE_ID, PERM_DESC, ACTV_IND) 
    VALUES('perm1', 'perm1', 'TEST', 'Permission One', NULL, '', 'Y')
/
INSERT INTO KR_KIM_PERMISSION_T(PERM_ID, OBJ_ID, NMSPC_CD, PERM_NM, KIM_TYPE_ID, PERM_DESC, ACTV_IND) 
    VALUES('perm2', 'perm2', 'TEST', 'Permission Two', NULL, '', 'Y')
/
INSERT INTO KR_KIM_PERMISSION_T(PERM_ID, OBJ_ID, NMSPC_CD, PERM_NM, KIM_TYPE_ID, PERM_DESC, ACTV_IND) 
    VALUES('perm3', 'perm3', 'TEST', 'Permission Three', NULL, '', 'Y')
/
COMMIT
/

INSERT INTO KR_KIM_ROLE_PERMISSION_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm1', 'rperm1', 'r1', 'perm1', 'Y')
/
INSERT INTO KR_KIM_ROLE_PERMISSION_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm2', 'rperm2', 'r1', 'perm2', 'Y')
/
INSERT INTO KR_KIM_ROLE_PERMISSION_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm3', 'rperm3', 'r2', 'perm3', 'Y')
/
COMMIT
/
-- TODO role types and qualifications

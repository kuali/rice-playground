CREATE TABLE EN_MSG_PAYLOAD_T (
	   MESSAGE_QUE_ID			  NUMBER(14) NOT NULL,
	   MESSAGE_PAYLOAD 		      CLOB NOT NULL,
	   CONSTRAINT EN_MSG_PAYLOAD_T PRIMARY KEY (MESSAGE_QUE_ID)
)
/
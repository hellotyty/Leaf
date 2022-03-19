CREATE TABLE LEAF_ALLOC (
  BIZ_TAG VARCHAR(128) NOT NULL DEFAULT '',
  MAX_ID BIGINT NOT NULL DEFAULT '1',
  STEP INT NOT NULL,
  DESCRIPTION VARCHAR(256) DEFAULT NULL,
  UPDATE_TIME DATETIME(3) NOT NULL DEFAULT NOW(3),
  PRIMARY KEY (BIZ_TAG)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_bin COMMENT='LEAF分配表';

package com.adcc.utility.mq.entity.active;

/**
 * AckMode.
 */
public enum  AckMode {
    SESSION_TRANSACTED,
    AUTO_ACKNOWLEDGE,
    CLIENT_ACKNOWLEDGE,
    DUPS_OK_ACKNOWLEDGE,
}

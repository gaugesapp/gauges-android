package com.github.mobile.gauges.core;

import java.io.Serializable;

/**
 * Client data containing an authentication key
 */
public class ClientData implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -1449954462631918663L;

    private String key;

    private String description;

    /**
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}

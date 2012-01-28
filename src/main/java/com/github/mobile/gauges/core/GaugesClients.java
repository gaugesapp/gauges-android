package com.github.mobile.gauges.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Container for all registered API clients
 */
public class GaugesClients implements Serializable, Iterable<Client> {

    /** serialVersionUID */
    private static final long serialVersionUID = -3536711047802796186L;

    private List<Client> clients;

    /**
     * @return clients
     */
    public List<Client> getClients() {
        return clients != null ? clients : Collections.<Client> emptyList();
    }

    /**
     * @param clients
     */
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public Iterator<Client> iterator() {
        return getClients().iterator();
    }
}

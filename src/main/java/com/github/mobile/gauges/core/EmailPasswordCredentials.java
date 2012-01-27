package com.github.mobile.gauges.core;

public class EmailPasswordCredentials {
    public final String emailAddress, password;

    public EmailPasswordCredentials(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    @Override
    public String toString() {
        return "EmailPasswordCredentials{" +
                "emailAddress='" + emailAddress + '\'' +
                ", password len='" + password.length() + '\'' +
                '}';
    }
}

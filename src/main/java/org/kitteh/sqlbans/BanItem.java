package org.kitteh.sqlbans;

import java.util.Date;

public class BanItem {

    private String reason;
    private String admin;
    private Date created;
    private int length;
    private String info;

    public BanItem(String info, String admin, Date created, int length, String reason) {
        this.info = info;
        this.admin = admin;
        this.created = created;
        this.length = length;
        this.reason = reason;
    }

    public String getInfo() {
        return this.info;
    }

    public int getLength() {
        return this.length;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getReason() {
        return this.reason;
    }

    public String getAdmin() {
        return this.admin;
    }
}

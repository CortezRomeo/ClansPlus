package com.cortezromeo.clansplus.api.enums;

public enum Subject {
    INVITE("Invite a player to clan"),
    KICK("Kick a member off of clan"),
    SETCUSTOMNAME("Set clan custom name"),
    SETICON("Set clan icon"),
    SPAWN("Teleport to clan spawn"),
    SETSPAWN("Set clan spawn"),
    SETMESSAGE("Set clan message"),
    SETMANAGER("Promote member to a manager"),
    REMOVEMANAGER("Remove a manager from clan"),
    CHAT("Clan chat"),
    UPGRADE("Upgrade clan"),
    MANAGEALLY("Send ally invite and manage clan's allies");

    private String description;

    Subject(String subjectDescription) {
        this.description = subjectDescription;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

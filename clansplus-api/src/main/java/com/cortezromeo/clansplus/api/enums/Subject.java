package com.cortezromeo.clansplus.api.enums;

public enum Subject {
    INVITE("Invite", "Invite a player to clan"),
    KICK("Kick", "Kick a member off of clan"),
    SETCUSTOMNAME("Set custom name", "Set clan custom name"),
    SETICON("Set icon", "Set clan icon"),
    SPAWN("Spawn", "Teleport to clan spawn"),
    SETSPAWN("Set spawn", "Set clan spawn"),
    SETMESSAGE("Set message", "Set clan message"),
    SETMANAGER("Set manager", "Promote member to a manager"),
    REMOVEMANAGER("Remove manager", "Remove a manager from clan"),
    CHAT("Chat", "Clan chat"),
    UPGRADE("Upgrade", "Upgrade clan"),
    MANAGEALLY("Manage ally", "Send ally invite and manage clan's allies");

    private String name;
    private String description;

    Subject(String name, String subjectDescription) {
        this.name = name;
        this.description = subjectDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

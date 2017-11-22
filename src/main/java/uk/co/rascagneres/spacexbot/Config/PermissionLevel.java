package uk.co.rascagneres.spacexbot.Config;

public enum PermissionLevel{
    User(10),
    BotManager(20),
    ChannelModerator(30),
    ChannelAdmin(40),
    ServerModerator(50),
    ServerAdmin(60),
    ServerOwner(70),
    BotOwner(80);

    private int value;

    PermissionLevel(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}

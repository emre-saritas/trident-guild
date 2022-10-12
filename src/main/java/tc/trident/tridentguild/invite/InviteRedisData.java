package tc.trident.tridentguild.invite;

public class InviteRedisData {
    private String sendingPlayer;
    private String targetPlayerName;
    private Invite invite;
    private InviteDataType dataType;

    public InviteRedisData(String sendingPlayer, String targetPlayerName, Invite invite, InviteDataType dataType) {
        this.sendingPlayer = sendingPlayer;
        this.targetPlayerName = targetPlayerName;
        this.invite = invite;
        this.dataType = dataType;
    }

    public String getTargetPlayerName() {
        return targetPlayerName;
    }
    public InviteDataType getDataType() {
        return dataType;
    }
    public Invite getInvite() {
        return invite;
    }

    public String getSendingPlayer() {
        return sendingPlayer;
    }

    enum InviteDataType {
        INVITE,
        ERROR_HAS_GUILD,
        ERROR_HAS_INVITE,
        JOINED_GUILD
    }
}






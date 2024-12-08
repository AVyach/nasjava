package nas.nas.model;

public class ShareData {
    private String action;
    private String targetUserName;
    private String targetFileUUID;
    private String newPermission;

    public ShareData(final String action, final String targetUserName, final String targetFileUUID, final String newPermission) {
        this.action = action;
        this.targetUserName = targetUserName;
        this.targetFileUUID = targetFileUUID;
        this.newPermission = newPermission;
    }
    
    public void setAction(final String action) {
        this.action = action;
    }
    
    public void setTargetUserName(final String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public void getTargetFileUUID(final String targetFileUUID) {
        this.targetFileUUID = targetFileUUID;
    }

    public void setNewPermission(final String newPermission) {
        this.newPermission = newPermission;
    }
    
    public String getAction() {
        return this.action;
    }

    public String getTargetUserName() {
        return this.targetUserName;
    }

    public String getTargetFileUUID() {
        return this.targetFileUUID;
    }

    public String getNewPermission() {
        return this.newPermission;
    }
}

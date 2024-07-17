package com.aibrains.emergency.models;

public class AllRequestList {

        public String uniqueId, userId, name , phone , comment , status , action_by , timeStamp ;

    public AllRequestList(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public AllRequestList() {
    }

    public AllRequestList(String uniqueId,String userId, String name, String phone, String comment, String status, String action_by, String timeStamp) {
        this.uniqueId = uniqueId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.comment = comment;
        this.status = status;
        this.action_by = action_by;
        this.timeStamp = timeStamp;
    }
}

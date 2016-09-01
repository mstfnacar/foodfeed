package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 09.12.2015.
 */
public class MessageBoxObject {

    private String sender;
    private String recipient;
    private String body;
    private String subject;

    public MessageBoxObject (String sender, String body){
        this.sender = sender;
        this.body = body;
    }

    public String getSender() {

        return sender;
    }

    public String getBody() {
        return body;
    }


}

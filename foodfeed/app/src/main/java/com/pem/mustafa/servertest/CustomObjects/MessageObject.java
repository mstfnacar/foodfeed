package com.pem.mustafa.servertest.CustomObjects;

/**
 * Created by mustafa on 09.12.2015.
 */
public class MessageObject {

    private String sender;
    private String recipient;
    private String body;
    private String subject;

    public MessageObject (String sender, String recipient, String body, String subject){
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
        this.subject = subject;
    }

    public String getSender() {

        return sender;
    }
    public String getRecipient() {

        return recipient;
    }
    public String getBody() {
        return body;
    }
    public String getSubject() {
        return subject;
    }

}

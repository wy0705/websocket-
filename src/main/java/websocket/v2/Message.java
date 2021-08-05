package websocket.v2;

public class Message {

    private String id;
    private String content;
    private String messageType;
    private long timestamp;

    private String rename;

    public String getRename() {
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public Message(String id, String content, long timestamp, String messageType) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public Message(String id, String content, String messageType, long timestamp, String rename) {
        this.id = id;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.rename = rename;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

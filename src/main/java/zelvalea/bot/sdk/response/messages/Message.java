package zelvalea.bot.sdk.response.messages;

public class Message {
    private int id;
    private int date;
    private int peer_id;

    private int from_id;
    private String text;


    public int getId() {return id;}

    public int getDate() {return date;}

    public int getPeerId() {return peer_id;}

    public String getText() {return text;}

    public int getFromId() {return from_id;}
}
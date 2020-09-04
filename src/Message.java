public class Message {
    int payload;
    boolean internal = false;

    public Message(int payload){
        this.payload=payload;
    }
    public void setFlag(boolean flag) {
        this.internal = flag;
    }
}
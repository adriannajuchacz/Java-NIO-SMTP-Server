import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.StandardOpenOption;


public class MyThread extends Thread {
    public static final int INTERNALTHREAD= 0;
    public static final int MSGSEQUENCER= 1;
    public static final int EXTERNALTHREAD= 2;
    public static final int CLIENT= 3;
    int type;
    Queue<Message> inbox;
    Queue<Message> received;
    ArrayList<MyThread> canSendTo = null;

    //threadID == priorität
    int id;

    public MyThread(int id, int type){
        this.inbox = new LinkedList<Message>();
        this.received = new LinkedList<Message>() ;
        this.id = id;
        this.type = type;
    }
    public void setCanSendTo(ArrayList<MyThread> threadList) {
        this.canSendTo = threadList;
    }


    /*
     * in the function run() of the  interface Runnable start the Thread
     * quit - boolean - describes if the main got stopped
     * inboxQueue
     * received
     */
    @Override
    public void run() {
        //while(!quit)
        //check case:
        // 1) client-> extern thread
        // 2) messageSeq
        // 3) messageSeq -> thread
        //inboxQueue.poll
        //handleMessage
        //set local quit to main.quit
        //wirte received to file log

        while(!this.isInterrupted()) {

            try{
                synchronized (this) {
                    this.wait();
                }
            } catch(InterruptedException ex){

                break;
            }
            while(this.inbox.size() != 0) {
                Message msg = this.inbox.poll();
                System.out.println("got message: " + msg.payload);
                if (this.type == MSGSEQUENCER) {
                    // 2) messageSeq
                    msg.setFlag(true);
                    handleMessageSeq(msg);
                } else if(!msg.internal) {
                    // 1) client-> extern thread
                    handleExternal(msg);
                } else {


                }
            }

        }

        System.out.println("was interrupted");
        try {
            writeLogFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessageSeq(Message msg) {
        for (int i = 0; i < this.canSendTo.size(); i++) {
            this.canSendTo.get(i).received.add(msg);

        }
    }

    private void handleExternal(Message msg) {
        MyThread reciever=this.canSendTo.get(0) ;
        reciever.inbox.add(msg);
        synchronized (reciever){
            reciever.notify();
        }
    }

    private void writeLogFile() throws IOException {
		/*
		String fileContent = "";

		String p = "/Thread_" + Integer.toString(this.id) + ".txt";
		Path path = Paths.get(p);
		
	    while(!this.received.isEmpty()) {
	    	Message msg = this.received.poll();
	    	fileContent += Integer.toString(msg.payload) + "\n";
	    }
	    
	    Files.createDirectories(Paths.get("logFiles"));
	    byte[] data = fileContent.getBytes(StandardCharsets.US_ASCII);
		Files.write(path, data);
		*/
        String fileContent = "";
        while(!this.received.isEmpty()) {
            Message msg = this.received.poll();
            fileContent += Integer.toString(msg.payload) + "\n";
        }
        String p="/Thread_" + Integer.toString(this.id) + ".txt";
        String path = "C:\\Users\\mnari\\OneDrive\\Documents\\kraya\\SS19\\2.1\\logFiles"+p;
        Files.write( Paths.get(path), fileContent.getBytes(), StandardOpenOption.CREATE);
    }


}
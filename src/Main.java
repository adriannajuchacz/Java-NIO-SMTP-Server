import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class Main {
    ArrayList<MyThread> threadList;
    //Thread client; not needed
    private MyThread msgSeq;
    public int nrExtThreads;


    public void initialize() {
        int count = 0;

        //messageSeq
        this.msgSeq = new MyThread(count, MyThread.MSGSEQUENCER);
        this.threadList = new ArrayList<MyThread>();
        this.threadList.add(msgSeq);
        count++;

        //interne threads
        MyThread tmp;
        for (int i = 0; i < 7; i++) {
            tmp = new MyThread(count, MyThread.INTERNALTHREAD);

            this.threadList.add(tmp);
            count++;
        }

        //externe threads
        for (int i = 0; i < this.nrExtThreads; i++) {
            tmp = new MyThread(count, MyThread.EXTERNALTHREAD);
            this.threadList.add(tmp);
            count++;

        }

        System.out.println("created Threads: " + count);
        startThreads();
    }


    private void startThreads() {
        for (int i = 0; i < this.threadList.size(); i++) {
            this.threadList.get(i).start();
        }
    }

    private void interruptThreads() {
        for (int i = 0; i < this.threadList.size(); i++) {
            System.out.println("interrupted " + i);
            this.threadList.get(i).interrupt();

        }
    }


    public void runClientSimulation() throws Exception {
        int count = 0;

        while (count <= 1000) {
            //choose ExternalThread Id
            int id = (int) ((Math.random() * this.nrExtThreads) + 5);

            //choose Payload
            int payload = (int) (Math.random() * Integer.MAX_VALUE);
            Message msg = new Message(payload);

            System.out.println("MAIN: sent to: " + id);
            System.out.println("Nr : "+ count);
            MyThread reciever=this.threadList.get(id) ;
            reciever.inbox.add(msg);
            synchronized (reciever){
                reciever.notify();
            }
            count++;

            //noch vllt ändern
            TimeUnit.MICROSECONDS.sleep(100);
        }

        synchronized (this.threadList) {
            this.threadList.notifyAll();
        }

    }

    private void setAuthorities() {
        this.msgSeq.setCanSendTo(this.threadList);
        ArrayList<MyThread> msgSeqList = new ArrayList<MyThread>();
        msgSeqList.add(this.msgSeq);
        for (int i = 1; i < this.threadList.size(); i++) {
            this.threadList.get(i).setCanSendTo(msgSeqList);
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        if (args.length != 1)
            throw new RuntimeException("Expected Number of External Threads");
        main.nrExtThreads = Integer.parseInt(args[0]);

        main.initialize();

        main.setAuthorities();

        main.runClientSimulation();

        main.interruptThreads();
    }

}

public class StudentNetworkSimulator extends NetworkSimulator{

    int astate, bstate, bOnceThru = 0;
    Packet astored_pkt, bstored_pkt;


    //constructor
    public StudentNetworkSimulator(int numMessages, double loss, double corrupt, double avgDelay,
                                   int trace, int seed, int winsize, double delay){
        super(numMessages, loss, corrupt, avgDelay, trace, seed);
    }

    
    // This routine will be called whenever the upper layer at the sender [A]
    // has a message to send.  It is the job of your protocol to insure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving upper layer.
    protected void aOutput(Message message){
    	if(astate == 0){
    		int chksum = calculateChecksum(message.getData(),0,0);
    		astored_pkt = new Packet(0,0,chksum,message.getData());
    		toLayer3(0,astored_pkt);
    		System.out.println("A sending packet 0: " + message.getData());
    		startTimer(0,100.0);
    		astate = 1;
    	}else if(astate==2){
    		int chksum = calculateChecksum(message.getData(),1,1);
    		astored_pkt = new Packet(1,1,chksum,message.getData());
    		toLayer3(0,astored_pkt);
    		System.out.println("A sending packet 1: " + message.getData());
    		startTimer(0,100.0);
    		astate = 3;
    	}else{
    		System.out.println("Timeout, increase time between sent packets!");
    	}

    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by a B-side procedure)
    // arrives at the A-side.  "packet" is the (possibly corrupted) packet
    // sent from the B-side.
    protected void aInput(Packet packet){
    	if(astate == 0){}//Do Nothing
    	if(astate == 1){
    		if(!isPktCorrupt(packet)) System.out.println("A: ACK corrupt");

    		if(packet.getAcknum() == 1) System.out.println("A: got ACK1, we're waiting for ACK 0");

    		if(packet.getAcknum() == 0){
    			stopTimer(0);
    			astate = 2;
    			System.out.println("A: got ACK 0");
    		}else{} // Do Nothing
    	}
    	if(astate == 2){}//Do Nothing
    	if(astate == 3){
    		if(!isPktCorrupt(packet)) System.out.println("A: ACK corrupt");

    		if(packet.getAcknum() == 0) System.out.println("A: got ACK0, we're waiting for ACK 1");

    		if(packet.getAcknum() == 1){
    			stopTimer(0);
    			astate = 0;
    			System.out.println("A: got ACK 1");
    		}else{} // Do Nothing
    	}
    }
    
    // This routine will be called when A's timer expires (thus generating a 
    // timer interrupt). You'll probably want to use this routine to control 
    // the retransmission of packets. See startTimer() and stopTimer(), above,
    // for how the timer is started and stopped. 
    protected void aTimerInterrupt(){
    	System.out.println("A: Timer interrupt, resending packet");
    	toLayer3(0,astored_pkt);
		startTimer(0,100.0);
    }
    
    // This routine will be called once, before any of your other A-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity A).
    protected void aInit(){

    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by an A-side procedure)
    // arrives at the B-side.  "packet" is the (possibly corrupted) packet
    // sent from the A-side.
    protected void bInput(Packet packet){
    	if(!isPktCorrupt(packet)){
    		if(bOnceThru == 1){
    			toLayer3(1,bstored_pkt);
    		}else{
    			return;
    		}
    	}else{
    		if((packet.getSeqnum() == 0 && bstate == 0) ||
    			(packet.getSeqnum() == 1 && bstate == 1)){
    				toLayer5(packet.getPayload());
    				System.out.println("B: got packet " + packet.getSeqnum());
    				bstored_pkt = new Packet(packet);
    				toLayer3(1,bstored_pkt);
    				System.out.println("B: send ACK " + packet.getAcknum());
    				bstate = (bstate + 1) % 2;
    				if(packet.getSeqnum() == 0) bOnceThru = 1;
    			}else{
    				if(bstate == 1 || bOnceThru == 1){
    					toLayer3(1,bstored_pkt);
    					System.out.println("B: sending ACK " + packet.getAcknum());
    				}
    			}
    	}
    	
    }
    
    // This routine will be called once, before any of your other B-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity B).
    protected void bInit(){

    }

	private int calculateChecksum(String payload, int seqno, int ackno){
		int singlecharval;
		int sum = 0;
	
		// calculate checksum
		int max = payload.length();
	     
		for (int i = 0; i < max; i++) {
		    singlecharval = payload.charAt(i);
		    sum += singlecharval;
		}
	
		sum += seqno;
		sum += ackno;
		return sum;
	
	}
	
	private boolean isPktCorrupt(Packet packet){
		return packet.getChecksum() == calculateChecksum(packet.getPayload(),packet.getSeqnum(),packet.getAcknum());
	}

}




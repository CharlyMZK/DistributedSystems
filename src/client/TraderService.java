package src.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TraderService {
	static ArrayList<TCPClient> clients = new ArrayList<>();
	static Timer timer = new Timer();
	
	
	public void scheduleTraderCreation() {
	
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
					try {
						randomlyCreateTrader();
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            }
        }, 100,1000);
	}
	

	
	public static void createATraderThaSellThenBuy() throws UnknownHostException, IOException, InterruptedException {
		
			Thread t1 = new Thread(new Runnable() {
			    public void run()
			    {
			    	if(TraderService.clients.size() < 10) {
			    		TCPClient client = new TCPClient();
						TraderService.clients.add(client);
						System.out.println("New trader came up ! There is now  "+TraderService.clients.size()+" traders.");
						try {
							client.connectToServerAndSellThenBuy();
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}else {
			    		timer.cancel();
			    		timer.purge();
			    	}
			    }});  
			    t1.start();
		}
	
	
	public static void randomlyCreateTrader() throws UnknownHostException, IOException, InterruptedException {
		
			Thread t1 = new Thread(new Runnable() {
			    public void run()
			    {
			    	if(TraderService.clients.size() < 10) {
			    		TCPClient client = new TCPClient();
						TraderService.clients.add(client);
						System.out.println("New trader came up ! There is now  "+TraderService.clients.size()+" traders.");
						try {
							client.connectToServerAndSendRequests();
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	}else {
			    		timer.cancel();
			    		timer.purge();
			    	}
			    }});  
			    t1.start();
		}
	
	
	   public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
	    	TraderService t = new TraderService();
			//t.createATraderThaSellThenBuy();
	    	t.scheduleTraderCreation();
	    }
}

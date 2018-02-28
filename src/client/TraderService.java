package src.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TraderService {
	public static ArrayList<Trader> clients = new ArrayList<>(); // List of requesting traders
	public static Timer timer = new Timer();						// Timer creating traders
	public static int traderCreationInterval = 1000;

	/**
	 * Set a timer who creates a trader every traderCreationInterval
	 */
	public void scheduleTraderCreation() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					randomlyCreateTrader();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 100, traderCreationInterval);
	}

	/**
	 * Creates a trader who sends random requests
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void randomlyCreateTrader() throws UnknownHostException, IOException, InterruptedException {
		Thread traderThread = new Thread(new Runnable() {
			public void run() {
				if (TraderService.clients.size() < 10) {
					Trader client = new Trader();
					TraderService.clients.add(client);
					System.out.println("New trader created. There is now  " + TraderService.clients.size() + " traders.");
					try {
						client.connectToServerAndSendRequests();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					timer.cancel();
					timer.purge();
				}
			}
		});
		traderThread.start();
	}
	
	/**
	 * Launch the traders creation
	 * @param args
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		TraderService t = new TraderService();
		t.scheduleTraderCreation();
		CyclicTrader cyclicTrader = new CyclicTrader();
	}
}

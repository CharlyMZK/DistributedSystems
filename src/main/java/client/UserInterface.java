package main.java.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {
	private BufferedReader stdIn;

	UserInterface() {
		stdIn = new BufferedReader(new InputStreamReader(System.in));
	}

	public String input() throws IOException {
		return stdIn.readLine();
	}

	public void output(String out) {
		System.out.print(out);
	}

	@Override
	public void finalize() throws IOException {
		stdIn.close();
	}
}

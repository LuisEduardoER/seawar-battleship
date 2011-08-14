package Testes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;

import Comunicacao.Constantes;
import Comunicacao.MessageSender;

public class ServerClientTest {

	public static void main(String args[]){
		
		try {
			Socket clientSocket = new Socket(InetAddress.getByName(Constantes.SERVER_ADDRESS), Constantes.SERVER_PORT);
			
			MessageSender send = new MessageSender(clientSocket, Constantes.CONNECT_TOKEN);
			send.run();
			
			clientSocket.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			System.exit(0);
		}
		
	}
}

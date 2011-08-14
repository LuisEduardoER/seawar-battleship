package Comunicacao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MulticastSender implements Runnable {

	private byte[] messageBytes;
	
	public MulticastSender(String mensagem){
		messageBytes = mensagem.getBytes();
	}
	
	@Override
	public void run() {
		
		try {
			DatagramSocket socketEnvio = new DatagramSocket(Constantes.MULTICAST_SENDING_PORT);
			
			InetAddress groupToSend = InetAddress.getByName(Constantes.MULTICAST_ADDRESS);
			
			DatagramPacket pacoteDados = new DatagramPacket(messageBytes,messageBytes.length, groupToSend, Constantes.MULTICAST_LISTENING_PORT);
			
			//Envia o pacote com a mensagem para todos os usuários
			socketEnvio.send(pacoteDados);
			socketEnvio.close();
		
		} catch (IOException ioException) {
			// TODO Auto-generated catch block
			ioException.printStackTrace();
		}
		
	}

}

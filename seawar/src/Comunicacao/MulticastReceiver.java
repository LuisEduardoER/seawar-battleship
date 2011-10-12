package Comunicacao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import modelos.Log;

public class MulticastReceiver implements Runnable{

	IMessageListener messageListener; //trata a mensagem recebida
	Socket socket; //Socket original
	MulticastSocket multicastSocket; //recebe os pacotes do grupo de multicast
	InetAddress multicastGroupAddress;//endereço do grupo multicast
	boolean continuarOuvindo = true;//mantém a execução para ouvir as mensagens multicast
	
	public MulticastReceiver(IMessageListener listener, Socket socket) {
		messageListener = listener;
		this.socket = socket;
		try {
			//instancia o multicast socket para conectar em uma determinada porta para ouvir mensagens
			multicastSocket = new MulticastSocket(Constantes.MULTICAST_LISTENING_PORT);
			//pega o ip de multicast que envia as mensagens
			multicastGroupAddress = InetAddress.getByName(Constantes.MULTICAST_ADDRESS);
			//entra no grupo de multicast para receber as mensagens enviadas
			multicastSocket.joinGroup(multicastGroupAddress);
			//Timeout de 5 segundos caso não receba mensagem alguma
			multicastSocket.setSoTimeout(5000);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void run() {
		while(continuarOuvindo){
			//Cria um buffer para receber as mensagens
			byte[] buffer = new byte[Constantes.MULTICAST_MESSAGE_SIZE];
			
			//Cria um pacote para receber a mensagem
			DatagramPacket pacote = new DatagramPacket(buffer, Constantes.MULTICAST_MESSAGE_SIZE);
			
			try {
				//Recebe o pacote no tamanho definido (chamada bloqueadora)
				multicastSocket.receive(pacote);
			}catch (SocketTimeoutException e) {
				//Se der timeout, continua ouvindo a porta para novas mensagens de entrada
				continue;
			} catch (IOException e) {
				pararDeOuvir();
				break;
			}
			//Transforma a mensagem recebida em uma string
			String mensagem = new String(pacote.getData());
			
			messageListener.mensagemRecebida(mensagem, this.socket);
		}//while
		
		try {
			//Quando parar de ouvir, deixa o grupo de multicast para não receber mais mensagens
			multicastSocket.leaveGroup(multicastGroupAddress);
			//fecha o socket para liberar a porta da conexão
			multicastSocket.close();
		} catch (IOException e) {
			Log.gravarLog("erro de multicastSocket:\n"+e.getMessage());
		}
	}

	private void pararDeOuvir() {
		continuarOuvindo = false;
	}

}

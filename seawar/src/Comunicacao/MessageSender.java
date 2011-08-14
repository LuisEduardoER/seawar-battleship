package Comunicacao;

import java.net.Socket;
import java.util.Formatter;

import modelos.Log;

public class MessageSender implements Runnable{

	Socket clientSocket;
	String messageToSend;
	
	public MessageSender(Socket socketClient, String mensagem){
		clientSocket = socketClient;
		messageToSend = mensagem;
	}
	
	@Override
	public void run() {
		
		try{
			Formatter output = new Formatter(clientSocket.getOutputStream());
			output.format("%s", messageToSend); //Envia a mensagem
			output.flush();//Esvazia a saída de mensagem
		}
		catch(Exception ex){
			Log.gravarLog(
					String.format("Erro ao enviar a mensagem para %s: %s", clientSocket.getInetAddress(), ex.getMessage() )
			);			
		}
		
	}

}

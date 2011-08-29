package Comunicacao;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Formatter;

import modelos.Log;

public class MessageSender implements Runnable {

	Socket clientSocket;
	String messageToSend;
	Object objectToSend;
	
	public MessageSender(Socket socketClient, String mensagem) {
		clientSocket = socketClient;
		messageToSend = mensagem;
	}
	public MessageSender(Socket socketClient, String mensagem, Object objectToSerialize) {
		clientSocket = socketClient;
		messageToSend = mensagem;
		objectToSend = objectToSerialize;
	}

	@Override
	public void run() {

		try {
			
			if(objectToSend != null){
				ObjectOutputStream outObj = new ObjectOutputStream(clientSocket.getOutputStream());
				
				outObj.writeChars(messageToSend + Constantes.TOKEN_SEPARATOR);
				//coloca o objeto serializado como o ultimo elemento da mensagem
				outObj.writeObject(objectToSend);
				outObj.flush();//Envia a mensagem
			}
			else{
				Formatter output = new Formatter(clientSocket.getOutputStream());
				output.format("%s", messageToSend); // Envia a mensagem
				output.flush();// Esvazia a saída de mensagem
			}
		} catch (Exception ex) {
			Log.gravarLog(String.format(
					"Erro ao enviar a mensagem para %s: %s", clientSocket
							.getInetAddress(), ex.getMessage()));
		}

	}

}

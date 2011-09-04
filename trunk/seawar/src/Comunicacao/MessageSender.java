package Comunicacao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Formatter;

import utils.Parser;

import encoder.Base64Coder;

import modelos.Log;

public class MessageSender implements Runnable {

	Socket clientSocket;
	String messageToSend;
	Serializable objectToSend;
	
	public MessageSender(Socket socketClient, String mensagem) {
		clientSocket = socketClient;
		messageToSend = mensagem;
	}
	public MessageSender(Socket socketClient, String mensagem, Serializable objectToSerialize) {
		clientSocket = socketClient;
		messageToSend = mensagem;
		objectToSend = objectToSerialize;
	}

	@Override
	public void run() {

		try {
				Formatter output = new Formatter(clientSocket.getOutputStream());
				if(objectToSend != null){
					output.format("%s%s%s\n", messageToSend, Constantes.TOKEN_SEPARATOR, Parser.ObjetoParaString(objectToSend)); // Formata a mensagem com o objetoSerializado
				}
				else{
					output.format("%s\n", messageToSend); // Formata a mensagem
				}
//				DatagramPacket pacote = new DatagramPacket(messageToSend.getBytes(), messageToSend.getBytes().length);
//				pacote.setSocketAddress(clientSocket.getRemoteSocketAddress());
//				DatagramSocket socketData = new DatagramSocket();
//				socketData.send(pacote);
				output.flush();// Envia a mensagem e esvazia o output
			
		} catch (Exception ex) {
			Log.gravarLog(String.format(
					"Erro ao enviar a mensagem para %s: %s", clientSocket
							.getInetAddress(), ex.getMessage()));
		}

	}
	

}

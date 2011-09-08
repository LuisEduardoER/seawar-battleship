package Comunicacao;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import encoder.Base64Coder;

import modelos.Log;
import modelos.Servidor;

public class MessageReceiver implements Runnable {

	private BufferedReader input;
	InputStreamReader streamReader;
	Socket socket;
	private IMessageListener messageListener;
	private boolean continuarOuvindo = true;
	
	
	public MessageReceiver(IMessageListener listener, Socket clientSocket) {
		messageListener = listener;

		try {
			socket = clientSocket;
			clientSocket.getInetAddress().getHostAddress();
			clientSocket.setSoTimeout(10000);// 10 segundos para timeout
			
			streamReader = new InputStreamReader(socket.getInputStream());
			input = new BufferedReader(streamReader);
		} catch (IOException ioe) {
			Log.gravarLog("Erro de leitura de socket: " + ioe.getMessage());
		}
	}

	@Override
	public void run() {
		String message = null; // String para receber as mensagens que chegam pelo
						// socket

		while (continuarOuvindo) {
			try {
				
				message = input.readLine();	
				//System.out.println(message);
				//TODO: Verificar se pode mesmo deixar assim ou se remove isso
				//pode ter problema de ficar com a thread em loop infinito matando a performance

				//Enquanto tiver mensagem no buffer do socket, vai lendo
//				while(input.ready()){		
//					message += input.readLine();							
//				}
			} catch (SocketTimeoutException timeoutEx) {
				continue; // Se der timeout na leitura do socket,
				// continua a iteração pra poder ouvir proximas mensagens
			} catch (IOException ioe) {
				Log.gravarLog("Timeout de socket: " + ioe.getMessage());
				break; // Se der uma exception de input, para a execução deste
						// leitor de sockets
			}// fim do try-catch

			if (message != null && !message.isEmpty()) {
				if (message.equalsIgnoreCase(Comunicacao.Constantes.DISCONNECT_TOKEN)) {
					pararDeOuvir();
				}
				//Executa a mensagem e passa o socket que foi utilizado
				messageListener.mensagemRecebida(message, this.socket);
				message=null;
			}// fim if de mensagem

		}// fim do while

		try {
			input.close(); // fecha o reader e também o socket
		} catch (IOException e) {
			e.printStackTrace();
		}// fim try-catch

	}

	private void pararDeOuvir() {
		this.continuarOuvindo = false;
	}
}

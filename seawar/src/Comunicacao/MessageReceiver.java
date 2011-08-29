package Comunicacao;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.StringTokenizer;

import modelos.Log;
import modelos.Servidor;

public class MessageReceiver implements Runnable {

	private BufferedReader input;
	Socket socket;
	private String ipRecebido;
	private IMessageListener messageListener;
	private boolean continuarOuvindo = true;

	public MessageReceiver(IMessageListener listener, Socket clientSocket) {
		messageListener = listener;

		try {
			socket = clientSocket;
			ipRecebido = clientSocket.getInetAddress().getHostAddress();
			clientSocket.setSoTimeout(5000);// 5 segundos para timeout
			input = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
		} catch (IOException ioe) {
			Log.gravarLog("Erro de leitura de socket: " + ioe.getMessage());
		}
	}

	@Override
	public void run() {
		String message; // String para receber as mensagens que chegam pelo
						// socket

		while (continuarOuvindo) {
			try {
				message = input.readLine();
			} catch (SocketTimeoutException timeoutEx) {
				continue; // Se der timeout na leitura do socket,
				// continua a iteração pra poder ouvir proximas mensagens
			} catch (IOException ioe) {
				Log.gravarLog("Timeout de socket: " + ioe.getMessage());
				break; // Se der uma exception de input, para a execução deste
						// leitor de sockets
			}// fim do try-catch

			if (message != null) {
				if (message
						.equalsIgnoreCase(Comunicacao.Constantes.DISCONNECT_TOKEN)) {
					pararDeOuvir();
				}
				//Executa a mensagem e passa o socket que foi utilizado
				messageListener.mensagemRecebida(message, this.socket);
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

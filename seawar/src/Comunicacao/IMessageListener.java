package Comunicacao;

import java.net.MulticastSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public interface IMessageListener {
	public void mensagemRecebida(String mensagem, Socket socketOrigem);

	public void receberTokensMensagem(StringTokenizer tokens, Socket socketOrigem);

	public void socketFinalizado(Socket socket);
}

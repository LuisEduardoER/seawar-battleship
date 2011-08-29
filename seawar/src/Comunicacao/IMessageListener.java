package Comunicacao;

import java.net.Socket;
import java.util.StringTokenizer;

public interface IMessageListener {
	public void mensagemRecebida(String mensagem, Socket socketOrigem);

	public void receberTokensMensagem(StringTokenizer tokens, String ipEnviou);

}

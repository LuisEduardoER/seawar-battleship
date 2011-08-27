package Comunicacao;

import java.util.StringTokenizer;

public interface IMessageListener {
	public void mensagemRecebida(String mensagem, String ipDe);

	public void receberTokensMensagem(StringTokenizer tokens);

}

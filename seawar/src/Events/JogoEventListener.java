package Events;

import java.util.EventListener;

//Interface para a declaração da assinatura dos métodos que deverao ser implementados
//em um listener de eventos da classe Jogo, atribuido em qualquer classe que quiser
//ficar monitorando os eventos para se atualizar de acordo com o que ocorre na classe
public interface JogoEventListener extends EventListener {

	void JogadorConectado(Object source, JogoEvent evt);
	
	void JogadorDesconectado(Object source, JogoEvent evt);
	
	void JogadorAtacado(Object source, JogoEvent evt);

	void JogoIniciado(Object source, JogoEvent evt);

	void JogoTerminado(Object source, JogoEvent evt);
	
	//void PingRecebido(Object source, JogoEvent evt);
	
	
}

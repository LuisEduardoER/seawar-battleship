package Events;

import java.util.EventListener;

public interface JogoEventListener extends EventListener {
	void JogadorConectado(Object source, JogoEvent evt);
	
	void JogadorDesconectado(Object source, JogoEvent evt);
	
	void JogadorAtacado(Object source, JogoEvent evt);
	
	//void PingRecebido(Object source, JogoEvent evt);
	
	
}

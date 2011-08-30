package Events;

import java.util.EventListener;

import modelos.Celula;

public interface ClientEventListener extends EventListener {
	void JogadorConectado(Object source);
	
	void JogadorDesconectado(Object source);
	
	void TiroRecebido(Object source, Celula celula);
}

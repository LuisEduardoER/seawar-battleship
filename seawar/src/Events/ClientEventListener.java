package Events;

import java.util.EventListener;

import modelos.Celula;
import modelos.Jogador;
import modelos.Jogo;

public interface ClientEventListener extends EventListener {
	void jogadorConectado(Object source, ClientEvent evt);
	
	void jogadorDesconectado(Object source, ClientEvent evt);
	
	void tiroRecebido(Object source, ClientEvent evt, Celula celula);
	
	void respostaTiro(Object source, ClientEvent evt, Celula celula);
	
	void turnoAlterado(Object source, ClientEvent evt);
	
	void falhaGenerica(Object source, Exception exception);
	
	void fimDeJogo(Object source, ClientEvent evt);
		
	void jogoIniciado(Object source,ClientEvent evt);

	void ativarBot(Object src, Jogador bot);

	void barcosOponentePosicionados(Object src);

	void carregarTelaJogo(Object jogo);

	void exibeMensagem(Object mensagem);
	
	void receberConviteParaJogar(String nomeJogador, int jogoid);
	
	void respostaConviteParaJogar(String nome, Object resposta);
	
	void listaJogosRecebida(Object lista);
	
	void listaJogadoresRecebida(Object lista);

	void carregarTelaPrincipal();

	void jogoCriado(Jogo jogoObject);
}

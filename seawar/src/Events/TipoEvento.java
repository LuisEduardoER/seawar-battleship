package Events;

public enum TipoEvento {
	//Eventos para o servidor
	DisplayAtualizado, 
	JogadoresAtualizados, 
	JogosAtualizados,
	
	//Eventos para o Client no momento do jogo (versao restrita)
	JogadorConectado,
	JogadorDesconectado,
	JogoIniciado,
	RespostaAtaque,
	RecebeuAtaque,
	EncerrouJogo
	
}
package Comunicacao;

public enum TipoMensagem {
	ConectarServidor, 
	RespostaLogin, //Resposta de login � utilizada ap�s verificar se o usu�rio est� autenticado ou n�o
	NovoJogadorConectado, 
	DesconectarServidor, 
	JogadorDesconectado, 
	ReceberListaJogos, 
	EnviarListaJogos, 
	ReceberListaJogadores, 
	EnviarListaJogadores, 
	CriarJogo, 
	JogoCriado, 
	ChamarJogador, 
	SerChamadoPorJogador, 
	RespostaChamada,
	EntrarJogo, 
	OponenteEntrou, 
	IniciarJogo, 
	BarcosPosicionados, 
	BarcosOponentePosicionados, 
	ReceberTabuleiroOponente, 
	AtacarOponente, 
	ReceberAtaque, 
	RespostaAtaque, 
	EnviarMensagemOponente, 
	ReceberMensagemOponente, 
	GanhouJogo, 
	PerdeuJogo, 
	Ping, 
	JogadorTimeout, 
	JogarComBot
}

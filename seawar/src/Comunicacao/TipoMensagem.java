package Comunicacao;

public enum TipoMensagem {
	ConectarServidor, 
	RespostaLogin, //Resposta de login é utilizada após verificar se o usuário está autenticado ou não
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

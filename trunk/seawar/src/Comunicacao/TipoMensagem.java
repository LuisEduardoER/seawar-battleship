package Comunicacao;

public enum TipoMensagem {
	ConectarServidor, NovoJogadorConectado, DesconectarServidor, JogadorDesconectado, ReceberListaJogos, EnviarListaJogos, ReceberListaJogadores, EnviarListaJogadores, CriarJogo, JogoCriado, ChamarJogador, SerChamadoPorJogador, EntrarJogo, OponenteEntrou, IniciarJogo, BarcosPosicionados, BarcosOponentePosicionados, ReceberTabuleiroOponente, AtacarOponente, ReceberAtaque, RespostaAtaque, EnviarMensagemOponente, ReceberMensagemOponente, GanhouJogo, PerdeuJogo, Ping, JogadorTimeout, JogarComBot
}
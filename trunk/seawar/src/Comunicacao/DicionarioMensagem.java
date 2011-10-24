package Comunicacao;

public class DicionarioMensagem {

	public static String GerarMensagemPorTipo(TipoMensagem tipo) {
		String mensagem = "";
		boolean incluirTipo = true;
		switch (tipo) {
		case ConectarServidor:
			mensagem = Constantes.CONNECT_TOKEN + CriarMensagemParametrizada("login","senha");
			incluirTipo = false;
			break;
		case NovoJogadorConectado:
			mensagem = CriarMensagemParametrizada("nome");
			break;
		case JogadorDesconectado:
			mensagem = CriarMensagemParametrizada("jogoid", "nome", "usuarioid");
			break;
		case DesconectarServidor:
			mensagem = Constantes.DISCONNECT_TOKEN;
			incluirTipo = false;
			break;
		case ReceberListaJogos:
			mensagem = CriarMensagemParametrizada("listaJogos");
			break;
		case EnviarListaJogos:
			mensagem = "";//CriarMensagemParametrizada("listaJogos");
			break;
		case ReceberListaJogadores:
			mensagem = CriarMensagemParametrizada("listaJogadores");
			break;
		case EnviarListaJogadores:
			mensagem = "";//CriarMensagemParametrizada("listaJogadores");
			break;
		case CriarJogo:
			mensagem = CriarMensagemParametrizada("jogoid", "nomeCriador");
			break;
		case JogoCriado:
			mensagem = CriarMensagemParametrizada("jogoid", "nomeCriador");
			break;
		case ChamarJogador:
			mensagem = CriarMensagemParametrizada("jogoid", "nome");
			break;
		case SerChamadoPorJogador:
			mensagem = CriarMensagemParametrizada("jogoid", "nome");
			break;
		case RespostaChamada:
			mensagem = CriarMensagemParametrizada("jogoid", "nomeChamou", "resposta");
			break;
		case EntrarJogo:
			mensagem = CriarMensagemParametrizada("jogoid", "nomeEntrou", "posicao");
			break;
		case OponenteEntrou:
			mensagem = CriarMensagemParametrizada("jogoid", "nomeEntrou", "posicao");
			break;
		case IniciarJogo:
			mensagem = CriarMensagemParametrizada("jogoid");
			break;
		case BarcosPosicionados:
			mensagem = CriarMensagemParametrizada("jogoid", "tabuleiro", "status"); // Envia o tabuleiro serializado (como string) para o servidor
								// saber onde marcar os barcos
			break;
		case BarcosOponentePosicionados:
			mensagem = CriarMensagemParametrizada("jogoid", "oponente", "status"); // Não envia o tabuleiro do inimigo, apenas se
								// ele já fez ou não o posicionamento
			break;
		case ReceberTabuleiroOponente:
			// Não faz nada, não se deve receber o tabuleiro do oponente.
			break;
		case AtacarOponente:
			mensagem = CriarMensagemParametrizada("jogoid", "x", "y");
			break;
		case ReceberAtaque:
			mensagem = CriarMensagemParametrizada("jogoid", "x", "y");
			break;
		case RespostaAtaque:
			mensagem = CriarMensagemParametrizada("jogoid", "x", "y", "tipoCelula", "ordem", "barco", "afundou");
			break;
		case EnviarMensagemOponente:
			mensagem = CriarMensagemParametrizada("jogoid", "mensagem");
			break;
		case ReceberMensagemOponente:
			mensagem = CriarMensagemParametrizada("jogoid", "nome", "mensagem");
			break;
		case GanhouJogo:
			mensagem = CriarMensagemParametrizada("jogoid", "nome");
			break;
		case PerdeuJogo:
			mensagem = CriarMensagemParametrizada("jogoid", "nome");
			break;
		case Ping:
			mensagem = Constantes.PING_TOKEN;
			incluirTipo = false;
			break;
		case JogadorTimeout:
			mensagem = CriarMensagemParametrizada("jogoid", "nome", "usuarioid");
			break;
		case JogarComBot:
			mensagem = CriarMensagemParametrizada("jogoid");
			break;
		case SairDeJogo:
			mensagem = CriarMensagemParametrizada("jogoid");
		default:
			mensagem = Constantes.PING_TOKEN;
			incluirTipo = false;
			break;
		}

		if (incluirTipo)
			return "$" + tipo + mensagem; // Envia a mensagem com o tipo como 1º
											// token

		return mensagem; // Envia a mensagem que for definida apenas, com o 1º
							// token sendo o que vier na mensagem
	}

	public static String GerarMensagemPorTipo(TipoMensagem tipo,
			Object... values) {
		String mensagem = "";
		boolean incluirTipo = true;
		switch (tipo) {
		case ConectarServidor:
			mensagem = Constantes.CONNECT_TOKEN;
			incluirTipo = false;
			break;
		case NovoJogadorConectado:
			break;
		case JogadorDesconectado:
			break;
		case DesconectarServidor:
			mensagem = Constantes.DISCONNECT_TOKEN;
			incluirTipo = false;
			break;
		case ReceberListaJogos:
			mensagem = CriarMensagemParametrizadaComValores(values,
					"listarJogosAbertos");
			break;
		case EnviarListaJogos:
			mensagem = CriarMensagemParametrizadaComValores(values,
					"listaJogos");
			break;
		case ReceberListaJogadores:
			mensagem = CriarMensagemParametrizadaComValores(values,
					"listarJogadoresLivres");
			break;
		case EnviarListaJogadores:
			mensagem = CriarMensagemParametrizadaComValores(values,
					"listaJogadores");
			break;
		case CriarJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador1");
			break;
		case JogoCriado:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador1");
			break;
		case ChamarJogador:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador1");
			break;
		case SerChamadoPorJogador:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador1");
			break;
		case EntrarJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador2");
			break;
		case OponenteEntrou:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"jogador2");
			break;
		case IniciarJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid");
			break;
		case BarcosPosicionados:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"tabuleiro", "status"); // Envia o tabuleiro serializado
											// para o servidor saber onde marcar
											// os barcos
			break;
		case BarcosOponentePosicionados:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"oponente", "status"); // Não envia o tabuleiro do inimigo,
											// apenas se ele já fez ou não o
											// posicionamento
			break;
		case ReceberTabuleiroOponente:
			// Não faz nada, não se deve receber o tabuleiro do oponente.
			break;
		case AtacarOponente:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"x", "y");
			break;
		case ReceberAtaque:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"x", "y");
			break;
		case RespostaAtaque:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"x", "y", "tipoCelula", "ordem");
			break;
		case EnviarMensagemOponente:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"mensagem");
			break;
		case ReceberMensagemOponente:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"nome", "mensagem");
			break;
		case GanhouJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"nome");
			break;
		case PerdeuJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid",
					"nome");
			break;
		case Ping:
			mensagem = Constantes.PING_TOKEN;
			incluirTipo = false;
			break;
		case JogadorTimeout:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid");
			break;
		case JogarComBot:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid");
			break;
		case SairDeJogo:
			mensagem = CriarMensagemParametrizadaComValores(values, "jogoid");
		default:
			mensagem = Constantes.PING_TOKEN;
			incluirTipo = false;
			break;
		}

		if (incluirTipo)
			return "$" + tipo + mensagem; // Envia a mensagem com o tipo como 1º
											// token

		return mensagem; // Envia a mensagem que for definida apenas, com o 1º
							// token sendo o que vier na mensagem
	}

	private static String CriarMensagemParametrizada(Object... args) {
		String msgParametrized = "";

		for (int i = 0; i < args.length; i++) {
			msgParametrized += "$" + args[i].toString() + Constantes.VALUE_SEPARATOR + "%s";
		}

		return msgParametrized;
	}

	private static String CriarMensagemParametrizadaComValores(Object[] values,
			Object... args) {
		String msgParametrized = "";

		for (int i = 0; i < args.length; i++) {
			msgParametrized += "$" + args[i].toString() + Constantes.VALUE_SEPARATOR + "%s";
		}

		return String.format(msgParametrized, values);
	}

}

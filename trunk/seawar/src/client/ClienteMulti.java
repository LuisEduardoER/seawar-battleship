package client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import exceptions.FullGameException;
import exceptions.GameException;

import modelos.Celula;
import modelos.Embarcacao;
import modelos.Jogador;
import modelos.Jogo;
import modelos.Log;
import modelos.Tabuleiro;
import modelos.TipoCelula;
import modelos.Usuario;

import Comunicacao.Constantes;
import Comunicacao.DicionarioMensagem;
import Comunicacao.IMessageListener;
import Comunicacao.MessageReceiver;
import Comunicacao.MessageSender;
import Comunicacao.MulticastReceiver;
import Comunicacao.TipoMensagem;
import Events.ClientEvent;
import Events.ClientEventListener;
import Events.JogoEvent;
import Events.JogoEventListener;
import Events.ServerEventListener;
import Events.TipoEvento;

public class ClienteMulti implements IMessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MessageReceiver recebedor; //Objeto responsável por receber as mensagens por socket
	MulticastReceiver recebedorMulticast;//Objecto responsavel por receber mensagens de multicast
	MessageSender sender; //Objeto responsável por enviar as mensagens via socket
	ExecutorService executor;
	Socket mySocket; //socket do jogador
	Jogador perfil; //perfil do jogador
	Jogo jogo; //jogo na instancia do cliente
	
	private final int TOKEN_HEADER = 0;
	private final int TOKEN_VALUE = 1;
	
	//Lista de eventos que possui a classe jogo
	protected EventListenerList listenerList = new EventListenerList();
	
	public ClienteMulti(Socket socket){		
		//Cria um executador, para classes que precisam de execução paralela
		executor = Executors.newCachedThreadPool();
		//Atribui o socket a variavel correta
		mySocket = socket;
		//Instancia um recebedor de mensagem socket e executa-o para ficar escutando o socket
		recebedor = new MessageReceiver(this, mySocket);
		executor.execute(recebedor);
		//Instancia um recebedor de mensagens multicast
		recebedorMulticast = new MulticastReceiver(this, mySocket);
		executor.execute(recebedorMulticast);
		//Instancia um Jogador
		perfil = new Jogador(mySocket);
	}
	
	public boolean Atacar(int x, int y){
		//Verifica se o jogo existe antes de enviar um ataque
		//Também verifica se o jogo ainda não foi encerrado
		if(this.jogo != null && !this.jogo.isJogoEncerrado()){
			if(this.perfil.isMinhaVez()){
				this.perfil.Atacar(this.jogo.getIdJogo(), x, y);
				String mensagemAlerta = String.format("Atirando na coordenada: %d, %d", x,y);
				fireMensagemAlertaEvent(mensagemAlerta);
				//Altera a flag do jogador para ele não poder jogar de novo
				//antes do outro jogador jogar
				this.perfil.setMinhaVez(false);
				fireTurnoAlterado(false);
				//Retorna true para informar que foi possivel fazer o ataque
				return true;
			}
			else{
				fireMensagemAlertaEvent("Não é sua vez de jogar");
			}
		}
		//Retorna falso se não for a vez da pessoa jogar ou se for impossivel fazer um ataque
		return false;
	}
	
	public void TrocarBarcoPosicao(int fromX, int fromY, int toX, int toY ){
		//Se tiver jogo e o tabuleiro de defesa não tiver travado, altera o
		//barco da célula de lugar
		if(jogo != null && !this.perfil.getTabuleiroDefesa().isTravaTabuleiro()){
			Tabuleiro tabuleiro = this.perfil.getTabuleiroDefesa();
			Celula celulaMudar = tabuleiro.encontrarCelula(fromX, fromY);
			if(celulaMudar.getTipoCelula() == TipoCelula.Embarcacao){
				Embarcacao barco = tabuleiro.getEmbarcacao(celulaMudar.x, celulaMudar.y);
				if(tabuleiro.ValidarPosicaoEmbarcacao(barco, toX, toY)){
					barco.setPosicao(toX, toY);
				}
				else
				{
					fireMensagemAlertaEvent("Posição inválida. Há um barco no local ou está fora do tabuleiro");
				}
			}
			else{
				fireMensagemAlertaEvent("Selecione um barco para alterar de lugar");
			}
		}
	}
	
	public void TrocarBarcoOrientacao(int x, int y){
		//Se tiver jogo e o tabuleiro de defesa não tiver travado, 
		//altera a orientação do barco
		if(jogo != null && !this.perfil.getTabuleiroDefesa().isTravaTabuleiro()){
			Tabuleiro tabuleiro = this.perfil.getTabuleiroDefesa();
			Celula celulaMudar = tabuleiro.encontrarCelula(x, y);
			if(celulaMudar.getTipoCelula() == TipoCelula.Embarcacao){
				Embarcacao barco = tabuleiro.getEmbarcacao(celulaMudar.x, celulaMudar.y);
				//faz a rotação do barco
				barco.setVertical(!barco.estaVertical());
				//valida a rotação
				if(tabuleiro.ValidarPosicaoEmbarcacao(barco, x, y)){
					fireMensagemAlertaEvent(String.format("Embarcacao(%s) rotacionada com sucesso", barco.getNomeEmbarcacao()));
				}
				else
				{
					//Desfaz a rotação se estiver inválida
					barco.setVertical(!barco.estaVertical());
					
					fireMensagemAlertaEvent("Rotação inválida. Há um barco no local ou está fora do tabuleiro");
				}
			}
			else{
				fireMensagemAlertaEvent("Selecione um barco para poder rotacionar");
			}
		}
	}
	
	@Override
	public void mensagemRecebida(String mensagem, Socket socketOrigem) {
		
		System.out.println("Mensagem recebida");
		System.out.println(mensagem);
		StringTokenizer tokens = new StringTokenizer(mensagem, Constantes.TOKEN_SEPARATOR);
		receberTokensMensagem(tokens, socketOrigem);

	}

	@Override
	public void receberTokensMensagem(StringTokenizer tokens, Socket socketOrigem) {
		List<String> lstTokens = new ArrayList<String>();
		
		
		//Transforma os tokens em lista
		while (tokens.hasMoreTokens()){
			String token = tokens.nextToken().trim();
			if(!token.isEmpty()){
				lstTokens.add(token);
			}
		}//fim da adatapcao da lista de tokens		
		
		TratarTokens(lstTokens, socketOrigem.getInetAddress().getHostAddress(), socketOrigem);
	}

	private void TratarTokens(List<String> lstTokens, String ipEnviou, Socket socket) {
		if(lstTokens == null || lstTokens.isEmpty())
			return;
		
		String header = lstTokens.get(0);
		//Aqui são a lista de ações para cada tipo de mensagem RECEBIDA (não confunda com enviada)
		if((Constantes.TOKEN_SEPARATOR+header).equalsIgnoreCase(Constantes.CONNECT_TOKEN)){
			jogadorConectado(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogadores.toString())){
			//EnviarListaJogadores(lstTokens, ipEnviou);
		}
		else if((Constantes.TOKEN_SEPARATOR+header).equalsIgnoreCase(Constantes.DISCONNECT_TOKEN)){
			//DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.SerChamadoPorJogador.toString())){
			exibirChamadoParaJogar(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.RespostaChamada.toString())){
			exibirRespostaChamada(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogos.toString())){
			//EnviarListaJogosAbertos(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EntrarJogo.toString())){
			//Utilizado quando EU entrar no jogo
			ConectarJogadorEmJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.CriarJogo.toString())){
			//CriarJogoComJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ChamarJogador.toString())){
			//ChamarJogadorParaJogar(lstTokens, ipEnviou);
		}
		else if(("$"+header).equalsIgnoreCase(Constantes.PING_TOKEN)){
			//AtualizaUltimoPingJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorDesconectado.toString())){
			DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorTimeout.toString())){
			DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogoCriado.toString())){
			avisarJogoCriado(lstTokens, socket);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.NovoJogadorConectado.toString())){
			//Se um novo jogador foi conectado, solicita a lista de jogadores
			this.perfil.solicitarListaJogadores();
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.OponenteEntrou.toString())){
			//Utilizado quando algum oponente entrar no jogo que estou
			ConectarOponenteEmJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.IniciarJogo.toString())){
			//DesbilitarJogoParaNovasConexoes(lstTokens, ipEnviou);
			iniciarTelaDeJogo(lstTokens, socket);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.BarcosPosicionados.toString())){
			//CarregarBarcosDoJogadorNoJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.BarcosOponentePosicionados.toString())){
			BarcosOponenteCarregados(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.AtacarOponente.toString())){
			//ProcessarAtaque(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.RespostaAtaque.toString())){
			ProcessarRespostaAtaque(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberAtaque.toString())){
			ReceberAtaque(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarMensagemOponente.toString())){
			//EnviarMensagemSocktParaAdversario(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberMensagemOponente.toString())){
			//É uma mensagem que apenas o jogador receberá quando o oponente enviar uma mensagem
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.GanhouJogo.toString())){
			DefinirJogadorComoVencedor(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.PerdeuJogo.toString())){
			DefinirJogadorComoPerdedor(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogarComBot.toString())){
			//AtivarBotComoOponenteParaJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberListaJogadores.toString())){
			ReceberListaJogadores(lstTokens, socket);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberListaJogos.toString())){
			ReceberListaJogos(lstTokens, socket);
		}
	}

	private void iniciarTelaDeJogo(List<String> lstTokens, Socket socket) {
		int jogoId = 0;
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				jogoId = Integer.parseInt(split[TOKEN_VALUE]);
				this.jogo.setIdJogo(jogoId);
			}
		}
		
		
	}

	private void avisarJogoCriado(List<String> lstTokens, Socket socket) {
		String nomeCriador = "";
		String strJogoId = "";
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if (split[TOKEN_HEADER].equalsIgnoreCase("jogoid")) {
				strJogoId = split[TOKEN_VALUE];
			}
			else if (split[TOKEN_HEADER].equalsIgnoreCase("nomeCriador")) {
				nomeCriador = split[TOKEN_VALUE];
			}
		}
		//Envia mensagem para receber a lista de jogos novamente
		receberListaJogos();
		//Dispara evento com a mensagem de que alguém criou um jogo
		fireMensagemAlertaEvent(String.format("%s criou o jogo Jogo%s", nomeCriador, strJogoId));
	}

	private void exibirRespostaChamada(List<String> lstTokens, String ipEnviou) {
		String resposta = null;
		String nomeChamado = null;
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("resposta")){
				resposta = split[TOKEN_VALUE];
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("nomeChamou")){
				nomeChamado = split[TOKEN_VALUE];
			}
		}
		fireReceberRespostaConviteEvent(nomeChamado, resposta);
	}

	private void ReceberListaJogos(List<String> lstTokens, Socket socket) {
		List<String> nomeJogos = new ArrayList<String>();
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split.length == 2){
				if(split[TOKEN_HEADER].equalsIgnoreCase("listaJogos")){
					for (String nomeJogo : split[TOKEN_VALUE].split(",")) {
						nomeJogos.add(nomeJogo);
					}
				}
			}
		}
		
		fireReceberListaJogosEvent(nomeJogos);
	}

	private void ReceberListaJogadores(List<String> lstTokens, Socket socket) {
		List<String> nomeJogadores = new ArrayList<String>();
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split.length == 2){
				if(split[TOKEN_HEADER].equalsIgnoreCase("listaJogadores")){
					for (String nomeJogador : split[TOKEN_VALUE].split(",")) {
						nomeJogadores.add(nomeJogador);
					}
				}
			}
		}
		
		fireReceberListaJogadoresEvent(nomeJogadores);
	}

	private void exibirChamadoParaJogar(List<String> lstTokens, String ipEnviou) {
		int idJogo = 0;
		String nomeJogador = null;
		
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if (split[TOKEN_HEADER].equalsIgnoreCase("jogoid")) {
				idJogo = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if (split[TOKEN_HEADER].equalsIgnoreCase("nome")) {
				nomeJogador = split[TOKEN_VALUE];
			}
		}
		//Se as informações recebidas são inválida, ignora 
		if(idJogo < 1 || nomeJogador == null){
			return;
		}
				
		fireReceberConviteEvent(nomeJogador, idJogo);
	}

	//Quando um INIMIGO desconecta do meu jogo
	private void DesconectarJogador(List<String> lstTokens, String ipEnviou) {
		int usuarioId = -1;
		int jogoId = -1;
		String nome = "";
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				jogoId = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("nome")){
				nome = split[TOKEN_VALUE];				             
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("usuarioid")){
				usuarioId=  Integer.parseInt(split[TOKEN_VALUE]);
			}				
		}
		Jogador jogadorDesconectado = null;
		//Se o cara desconectado estava no mesmo jogo que eu, era meu adversário
		if(this.jogo != null && jogoId > 0 && jogoId == this.jogo.getIdJogo()){			
			//Encontra o jogador que foi desconectado e remove-o do jogo
			for (Jogador jogador : this.jogo.getListaJogador()) {
				if(jogador.getLogin().equalsIgnoreCase(nome)){
					jogadorDesconectado = jogador;
					//Solicita ao jogador se ele quer continuar o jogo contra um BOT
					//ou se deseja terminar o jogo, desde que não seja ele o desconectado
					if(jogador != this.perfil){
						if(JOptionPane.showConfirmDialog(null, "Seu adversário saiu do jogo por algum motivo,\ndeseja continuar utilizando um bot em seu lugar?\n(Seus pontos continuarão a ser computados)", "Adversário desconectado", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
							this.jogo.setBot(jogador);
							EnviarDecisaoJogarContraBot();
							fireAtivarBotEvent(this.jogo, jogador);
						}
						else{
							this.jogo.removerJogador(jogador);	
						}
					}
					break;
					
				}
			}
		}
		//Se o jogador desconectado foi um jogador do meu jogo, dispara o evento de jogador desconectado
		if(jogadorDesconectado != null)
			fireJogadorDesconectado(jogadorDesconectado);
		else//senão solicita apenas a nova lista de jogadores
			this.receberListaJogadores();
	}

	//Envia a mensagem para o servidor que você escolheu jogar contra BOT
	private void EnviarDecisaoJogarContraBot() {
		String msg = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.JogarComBot);
		String msgEnviar = String.format(msg,this.jogo.getIdJogo());
		MessageSender send = new MessageSender(this.mySocket, msgEnviar);
		send.run();		
	}

	//Qando eu RECEBO um ataque
	private void ReceberAtaque(List<String> lstTokens, String ipEnviou) {
		if(this.jogo == null)
			return;
		//instancia uma célula para enviar ao evento
		Celula celulaAtacada = new Celula(0,0);
		//Preenche a célula de acordo com os valores dos tokens
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("x")){
				celulaAtacada.x = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("y")){
				celulaAtacada.y = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				int jogoId = Integer.parseInt(split[TOKEN_VALUE]);
				this.jogo.setIdJogo(jogoId);
			}
		}
		//Processa o ataque e indica a célula que foi atacada pelo inimigo
		Celula celulaResposta = this.perfil.getTabuleiroDefesa().atacar(celulaAtacada.x, celulaAtacada.y);
		Embarcacao barcoAcertado = null;
		if(celulaResposta.getTipoCelula() == TipoCelula.Embarcacao){
			barcoAcertado = this.perfil.getTabuleiroDefesa().getEmbarcacao(celulaAtacada.x, celulaAtacada.y);
		}

		//Libera o jogador para fazer outro ataque
		this.perfil.setMinhaVez(true);
		fireTurnoAlterado(true);
		
		//Dispara o evento que informa que uma célula foi atacada
		if(barcoAcertado != null)
			fireReceberAtaque(barcoAcertado, celulaResposta);
		else
			fireReceberAtaque(new Object(), celulaResposta);
		
	}


	//Quando eu processo a resposta do MEU ataque
	private void ProcessarRespostaAtaque(List<String> lstTokens, String ipEnviou) {
		Embarcacao barco = null;
		String nomeBarco = null;
		boolean afundou = false;
		int ordem = 0;
		
		//instancia uma célula para enviar ao evento
		Celula celulaAtacou = new Celula(0,0);
		//define a celula como acertada
		celulaAtacou.acertar();
		//Preenche a célula de acordo com os valores dos tokens
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("x")){
				celulaAtacou.x = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("y")){
				celulaAtacou.y = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("tipoCelula")){
				//Atualiza o Id do jogo para futuras referencias
				celulaAtacou.setTipoCelula(Enum.valueOf(TipoCelula.class, split[TOKEN_VALUE]));
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				int jogoId = Integer.parseInt(split[TOKEN_VALUE]);
				//this.jogo.setIdJogo(jogoId);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("ordem")){
				ordem = Integer.parseInt(split[TOKEN_VALUE]);
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("barco") && split.length == 2){
				//Atualiza o Id do jogo para futuras referencias
				nomeBarco = split[TOKEN_VALUE];
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("afundou")){
				//Atualiza o Id do jogo para futuras referencias
				afundou = Boolean.parseBoolean(split[TOKEN_VALUE]);
				
			}
		}
		
		if(nomeBarco != null && !nomeBarco.isEmpty()){
			barco = this.perfil.getTabuleiroAtaque().encontrarBarco(nomeBarco);
			//Se o barco não tiver naufragado, marca a célula dele como acertada
			if(barco != null && !barco.getNaufragado()){
				Celula[] celulas = barco.getListaCelulas();
				int i = 0;
				while(celulas[i].isAtirada()){
					i++;
				}
				//Se o "i" não estiver fora dos limites das células, acerta a célula em que parou contador "i"
				if(i < celulas.length)
					celulas[i].acertar();
			}
		}
		
		//Dispara o evento que informa o que era a célula atacada pelo jogador
		if(barco != null)
			fireRespostaAtaque(barco, celulaAtacou);
		else
			fireRespostaAtaque(new Object(), celulaAtacou);		
	}

	private void BarcosOponenteCarregados(List<String> lstTokens, String ipEnviou) {
		//int jogoId = -1;
		String nomeAdv = "";
		//String status = "";
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//jogoId = Integer.parseInt(split[TOKEN_VALUE]);
			} 
			else if(split[TOKEN_HEADER].equalsIgnoreCase("oponente")){
				nomeAdv = split[TOKEN_VALUE];
			} 
			else if(split[TOKEN_HEADER].equalsIgnoreCase("status")){
				//status = split[TOKEN_VALUE];
			} 
		}
		
		//Dispara um evento que indica que o oponente já posicionou os barcos
		fireBarcosOponentePosicionadosEvent(nomeAdv);
	}

	private void DefinirJogadorComoVencedor(List<String> lstTokens, String ipEnviou) {
		//Marca EU como jogador vencedor e automaticamente dispara o evento de finalização de jogo
		this.jogo.setVencedor(this.perfil);
		
	}

	private void DefinirJogadorComoPerdedor(List<String> lstTokens, String ipEnviou) {
		//Marca o oponente como vencedor
		Jogador adv = jogo.EncontrarJogadorAdversario(this.perfil);
		if(adv != null)
			this.jogo.setVencedor(adv);
			//Automaticamente dispara evento de finalização de jogo
		else{
			Log.gravarLog("Não foi encontrado o adversário");
			fireFalhaGenericaEvent(this.perfil, new Exception("Você perdeu!"));
		}
	}
	//Método acessado quando eu conectar no servidor
	private void ConectarJogadorEmJogo(List<String> lstTokens, String ipEnviou){
		String posicaoString = "";//lstTokens.get(2);
		int jogoId = -1;
		
		List<Jogador> jogadores = new ArrayList<Jogador>();
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("posicao")){
				posicaoString = split[TOKEN_VALUE];
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				jogoId = Integer.parseInt(split[TOKEN_VALUE]);
				CriarJogoLocal(jogoId);
				this.jogo.setIdJogo(jogoId);
			}

			else if(split[TOKEN_HEADER].equalsIgnoreCase("jogador")){			
				Jogador adv = new Jogador();
				adv.setLogin(split[TOKEN_VALUE]);
				jogadores.add(adv);
			}
		}		
		int posicao = Integer.parseInt(posicaoString);
		//Se a posição for válida, adiciona-o na lista de jogadores do jogo
		if(posicao >= 0){
			try {
				for(Jogador jogador : jogadores){
					this.jogo.AdicionarJogador(jogador);
				}
				//Cria os tabuleiros para o jogador
				this.perfil.setTabuleiroAtaque(new Tabuleiro(Constantes.TAMANHO_TABULEIRO));
				this.perfil.setTabuleiroDefesa(new Tabuleiro(Constantes.TAMANHO_TABULEIRO));	
				//Adiciona o jogador
				this.jogo.AdicionarJogador(perfil, posicao);
				//Será a vez do jogador se ele foi o primeiro a entrar
				boolean jogadorCriou = (posicao == 0);
				
				this.perfil.setMinhaVez(jogadorCriou);

				if(jogadorCriou){
					//Evento que dispara para avisar quando o jogador é o criador do jogo (ou seja, quando ele é adicionado na posição 0 do jogo)
					fireJogoCriadoEvent(this.jogo);
				}
				else{
					//Evento que dispara quando o jogador conecta em um jogo, que não foi o criador, para iniciar a tela do jogo
					fireCarregarTelaJogo(this.jogo);
				}
				
			} catch (FullGameException e) {
				//Grava no log caso haja alguma falha
				Log.gravarLog(e.getMessage());
				fireFalhaGenericaEvent("O jogo está lotado, não tem mais espaço", e);
			} catch (GameException e) {
				//Grava no log caso haja alguma falha
				Log.gravarLog(e.getMessage());
				fireFalhaGenericaEvent("O jogo está lotado, não tem mais espaço", e);
			}
		}
	}
	//Método acessado quando o opoentente conectar no servidor
	private void ConectarOponenteEmJogo(List<String> lstTokens, String ipEnviou){
		String nomeJogador = "";
		String posicaoString = "";
		int jogoId = 0;
		
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[TOKEN_HEADER].equalsIgnoreCase("posicao")){
				posicaoString = split[TOKEN_VALUE];
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("nomeEntrou")){
				nomeJogador = split[TOKEN_VALUE];
			}
			else if(split[TOKEN_HEADER].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				jogoId = Integer.parseInt(split[TOKEN_VALUE]);
				this.jogo.setIdJogo(jogoId);
			}
		}
		Jogador novo = null;
		novo = new Jogador();
		novo.setLogin(nomeJogador);
		//A posição é baseada no indice.
		int posicao = Integer.parseInt(posicaoString);		
		if(posicao >= 0){
			try {
				this.jogo.AdicionarJogador(novo, posicao);

				//Se com a conexão do novo oponente o jogo ficar lotado, dispara o evento para carregar a tela do jogo
				if(this.jogo.isLotado()){
					fireCarregarTelaJogo(this.jogo);
				}
			} catch (GameException e) {
				Log.gravarLog(e.getMessage());
				fireFalhaGenericaEvent("Houve um erro ao tentar conectar oponente no jogo.", e);
			}
		}		
	}

	//Método acessado quando EU conecto no servidor
	private void jogadorConectado(List<String> lstTokens, String ipEnviou) {
		//int jogoId = 10;		
		//CriarJogoLocal(jogoId);		
		//fireCarregarTelaJogo(this.jogo);
		
		this.perfil.setOnline();
		fireCarregarTelaPrincipal();		
	}

	private void CriarJogoLocal(int jogoId) {
		jogo = new Jogo(jogoId, Constantes.CAPACIDADE_JOGO);
		DefinirListenersJogo();
	}

	public void conectar(String login, String senha) {
		perfil.setLogin(login);
		perfil.setSenha(senha);
		perfil.conexaoJogador.conectarJogador();
	}

	public void desconectar(){
		if(perfil.desconectar()){
			fireJogadorDesconectado(perfil);
		}
		else{
			String erro = "Não foi possivel desconectar o jogador";
			fireFalhaGenericaEvent(erro, new GameException(erro));
		}
	}
	
	//Ações que terá a classe Cliente para cada evento disparado pela classe Jogo
	private void DefinirListenersJogo() {
		jogo.AddClientEventListener(new JogoEventListener() {
			
			@Override
			public void JogoTerminado(Object source, JogoEvent evt) {
				fireFimJogoEvent(source);
			}
			
			@Override
			public void JogoIniciado(Object source, JogoEvent evt) {
				fireJogoIniciadoEvent(source);
			}
			
			@Override
			public void JogadorDesconectado(Object source, JogoEvent evt) {
				fireJogadorDesconectado(source);
				
			}
			
			@Override
			public void JogadorConectado(Object source, JogoEvent evt) {
				fireJogadorConectado(source);
				
			}
			
			@Override
			public void JogadorAtacado(Object source, JogoEvent evt) {
				//fireReceberAtaque(source);
				
			}
		});
	}
	

	
	public Jogador getPerfil() {		
		return this.perfil;
	}

	public boolean enviarTabuleiro() {
		if(this.jogo != null)
			return this.perfil.enviarTabuleiroDefesa(this.jogo.getIdJogo());

		return this.perfil.enviarTabuleiroDefesa(this.getPerfil().getJogoId());
	}
	public boolean enviarTabuleiro(int idJogo) {
		return this.perfil.enviarTabuleiroDefesa(idJogo);		
	}
	public boolean enviarRespostaPositiva(int jogoid, String nomeJogador){
		//return perfil.enviarRespostaConvite(jogoid, nomeJogador, Constantes.RESPOSTA_POSITIVA);
		return conectarEmJogo(jogoid);
	}
	public boolean enviarRespostaNegativa(int jogoid, String nomeJogador){
		return perfil.enviarRespostaConvite(jogoid, nomeJogador, Constantes.RESPOSTA_NEGATIVA);		
	}

	public boolean conectarEmJogo(int jogoid) {
		return perfil.conectarEmJogo(jogoid);		
	}
	
	public void limparJogo(){
		this.jogo = null;
		this.perfil.setTabuleiroAtaque(null);
		this.perfil.setTabuleiroDefesa(null);
	}
	
	
	//Manipuladores de listeners de eventos
	public void AddClientEventListener(ClientEventListener listener){
		listenerList.add(ClientEventListener.class, listener);
	}
	public void RemoveClientEventListener(ClientEventListener listener){
		listenerList.remove(ClientEventListener.class, listener);
	}
	private void fireJogadorConectado(Object src){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).jogadorConectado(src, new ClientEvent(src, TipoEvento.JogadorConectado));
	            }
	        }
	}
	private void fireJogadorDesconectado(Object src){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).jogadorDesconectado(src, new ClientEvent(src, TipoEvento.JogadorDesconectado));
	            }
	        }
	}
	private void fireJogoIniciadoEvent(Object src){
		 Object[] listeners = listenerList.getListenerList();
       // Each listener occupies two elements - the first is the listener class
       // and the second is the listener instance
       for (int i=0; i<listeners.length; i+=2) {
           if (listeners[i]==ClientEventListener.class) {
               ((ClientEventListener)listeners[i+1]).jogoIniciado(src, new ClientEvent(src, TipoEvento.JogoIniciado));
           }
       }
	}
	private void fireReceberAtaque(Object src, Celula celulaAtacada){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).tiroRecebido(src, new ClientEvent(src, TipoEvento.RecebeuAtaque), celulaAtacada);
	            }
	        }
	}
	private void fireRespostaAtaque(Object src, Celula celulaAtacou){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).respostaTiro(src, new ClientEvent(src, TipoEvento.RespostaAtaque), celulaAtacou);
	            }
	        }
	}
	private void fireFimJogoEvent(Object src){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).fimDeJogo(src, new ClientEvent(src, TipoEvento.EncerrouJogo));
	            }
	        }
	}
	private void fireFalhaGenericaEvent(Object src, Exception exception){
		 Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==ClientEventListener.class) {
                ((ClientEventListener)listeners[i+1]).falhaGenerica(src, exception);
            }
        }
	}
	private void fireAtivarBotEvent(Object src, Jogador bot){
		 Object[] listeners = listenerList.getListenerList();
       // Each listener occupies two elements - the first is the listener class
       // and the second is the listener instance
       for (int i=0; i<listeners.length; i+=2) {
           if (listeners[i]==ClientEventListener.class) {
               ((ClientEventListener)listeners[i+1]).ativarBot(src, bot);
           }
       }
	}
	private void fireBarcosOponentePosicionadosEvent(Object src){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	       for (int i=0; i<listeners.length; i+=2) {
	           if (listeners[i]==ClientEventListener.class) {
	               ((ClientEventListener)listeners[i+1]).barcosOponentePosicionados(src);
	           }
	       }
	}
	private void fireCarregarTelaJogo(Object jogo){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).carregarTelaJogo(jogo);
	    	 }
	     }
	}
	private void fireCarregarTelaPrincipal(){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).carregarTelaPrincipal();
	    	 }
	     }
	}
	private void fireMensagemAlertaEvent(Object mensagem){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).exibeMensagem(mensagem);
	    	 }
	     }
	}
	private void fireReceberListaJogosEvent(Object listaJogos){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).listaJogosRecebida(listaJogos);
	    	 }
	     }
	}
	private void fireReceberListaJogadoresEvent(Object listaJogadores){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).listaJogadoresRecebida(listaJogadores);
	    	 }
	     }
	}
	private void fireReceberRespostaConviteEvent(String nome, Object resposta){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).respostaConviteParaJogar(nome, resposta);
	    	 }
	     }
	}
	private void fireReceberConviteEvent(String nomeJogador, int jogoid){
		 Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).receberConviteParaJogar(nomeJogador, jogoid);
	    	 }
	     }
	}

	private void fireJogoCriadoEvent(Jogo jogoObject) {
		Object[] listeners = listenerList.getListenerList();
	       // Each listener occupies two elements - the first is the listener class
	       // and the second is the listener instance
	     for (int i=0; i<listeners.length; i+=2) {
	    	 if (listeners[i]==ClientEventListener.class) {
	    		 ((ClientEventListener)listeners[i+1]).jogoCriado(jogoObject);
	    	 }
	     }
	}

	public boolean criarNovoJogo() {
		return this.perfil.criarNovoJogo();
	}

	public void receberListaJogos() {
		this.perfil.solicitarListaJogos();
		
	}

	public void receberListaJogadores() {
		this.perfil.solicitarListaJogadores();
		
	}

	@Override
	public void socketFinalizado(Socket socket) {
		fireJogadorDesconectado(perfil);		
	}

	public void chamarJogador(String nomeJogador) {
		try {
			this.perfil.getConexao().chamarJogador(nomeJogador, this.perfil.getJogoId());
		} catch (Exception e) {
			fireMensagemAlertaEvent(e.getMessage());
		}
		
	}

	public void desistirJogo() {
		try{
			this.perfil.getConexao().desistirJogo(this.jogo.getIdJogo());
			limparJogo();
		}
		catch(Exception e){
			fireMensagemAlertaEvent(e.getMessage());
		}
	}
	

	private void fireTurnoAlterado(Object src){
		 Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ClientEventListener.class) {
	                ((ClientEventListener)listeners[i+1]).turnoAlterado(src, new ClientEvent(src, TipoEvento.DisplayAtualizado));
	            }
	        }
	}
}

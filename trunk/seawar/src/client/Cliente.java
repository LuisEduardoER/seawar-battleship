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
import Comunicacao.TipoMensagem;
import Events.ClientEvent;
import Events.ClientEventListener;
import Events.JogoEvent;
import Events.JogoEventListener;
import Events.ServerEventListener;
import Events.TipoEvento;

public class Cliente implements IMessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MessageReceiver recebedor; //Objeto responsável por receber as mensagens por socket
	MessageSender sender; //Objeto responsável por enviar as mensagens via socket
	ExecutorService executor;
	Socket mySocket; //socket do jogador
	Jogador perfil; //perfil do jogador
	Jogo jogo; //jogo na instancia do cliente
	
	//Lista de eventos que possui a classe jogo
	protected EventListenerList listenerList = new EventListenerList();
	
	public Cliente(Socket socket){		
		//Cria um executador, para classes que precisam de execução paralela
		executor = Executors.newCachedThreadPool();
		//Atribui o socket a variavel correta
		mySocket = socket;
		//Instancia um recebedor de mensagem socket e executa-o para ficar escutando o socket
		recebedor = new MessageReceiver(this, mySocket);
		executor.execute(recebedor);
		//Instancia um Jogador
		perfil = new Jogador(mySocket);
	}
	
	public boolean Atacar(int x, int y){
		//Verifica se o jogo existe antes de enviar um ataque
		//Também verifica se o jogo ainda não foi encerrado
		if(this.jogo != null && !this.jogo.isJogoEncerrado()){
			if(this.perfil.isMinhaVez()){
				this.perfil.Atacar(x, y);
				String mensagemAlerta = String.format("Atirando na coordenada: %d, %d", x,y);
				fireMensagemAlertaEvent(mensagemAlerta);
				//Altera a flag do jogador para ele não poder jogar de novo
				//antes do outro jogador jogar
				this.perfil.setMinhaVez(false);
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
			//ChamarJogadorParaJogar(lstTokens, ipEnviou);
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
			//AtualizarListaJogos(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.NovoJogadorConectado.toString())){
			//AtualizarListaJogadores(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.OponenteEntrou.toString())){
			//Utilizado quando algum oponente entrar no jogo que estou
			ConectarOponenteEmJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.IniciarJogo.toString())){
			//DesbilitarJogoParaNovasConexoes(lstTokens, ipEnviou);			
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
			//Para Cliente receber e popular a lista de jogadores na tela dele
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberListaJogos.toString())){
			//Para o cliente receber e popular al ista de jogos na tela dele
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberTabuleiroOponente.toString())){
			//mensagem que não será utilizada, pois o tabuleiro não será enviado para o cliente mais
		}
	}

	//Quando um INIMIGO desconecta do meu jogo
	private void DesconectarJogador(List<String> lstTokens, String ipEnviou) {
		int usuarioId = -1;
		int jogoId = -1;
		String nome = "";
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[0].equalsIgnoreCase("jogoid")){
				jogoId = Integer.parseInt(split[1]);
			}
			else if(split[0].equalsIgnoreCase("nome")){
				nome = split[1];				             
			}
			else if(split[0].equalsIgnoreCase("usuarioid")){
				usuarioId=  Integer.parseInt(split[1]);
			}				
		}
		
		//Encontra o jogador que foi desconectado e remove-o do jogo
		for (Jogador jogador : this.jogo.getListaJogador()) {
			if(jogador.getLogin().equalsIgnoreCase(nome)){
				//Solicita ao jogador se ele quer continuar o jogo contra um BOT
				//ou se deseja terminar o jogo
				
				//TODO: Verificar uma solução melhor do que um JOptionPane :)
				if(JOptionPane.showConfirmDialog(null, "Seu adversário saiu do jogo por algum motivo,\ndeseja continuar utilizando um bot em seu lugar?\n(Seus pontos continuarão a ser computados)", "Adversário desconectado", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					this.jogo.setBot(jogador);
					EnviarDecisaoJogarContraBot();
					fireAtivarBotEvent(this.jogo, jogador);
				}
				else{
					this.jogo.removerJogador(jogador);	
				}
				break;
			}
		}
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
		//instancia uma célula para enviar ao evento
		Celula celulaAtacada = new Celula(0,0);
		//Preenche a célula de acordo com os valores dos tokens
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[0].equalsIgnoreCase("x")){
				celulaAtacada.x = Integer.parseInt(split[1]);
			}
			else if(split[0].equalsIgnoreCase("y")){
				celulaAtacada.y = Integer.parseInt(split[1]);
			}
			else if(split[0].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				int jogoId = Integer.parseInt(split[1]);
				this.jogo.setIdJogo(jogoId);
			}
		}
		//Processa o ataque e indica a célula que foi atacada pelo inimigo
		Celula celulaResposta = this.perfil.getTabuleiroDefesa().atacar(celulaAtacada.x, celulaAtacada.y);
		
		
		//ATENÇÃO: O cliente não precisa enviar a resposta do ataque
		//		   pois o servidor já cuida desse detalhe
		//Envia a resposta do ataque
		//EnviarRespostaAtaque(celulaResposta);
		
		
		//Dispara o evento que informa que uma célula foi atacada
		fireReceberAtaque(lstTokens, celulaResposta);
		
		//Libera o jogador para fazer outro ataque
		this.perfil.setMinhaVez(true);
	}


	//Quando eu processo a resposta do MEU ataque
	private void ProcessarRespostaAtaque(List<String> lstTokens, String ipEnviou) {
		//instancia uma célula para enviar ao evento
		Celula celulaAtacou = new Celula(0,0);
		//define a celula como acertada
		celulaAtacou.acertar();
		//Preenche a célula de acordo com os valores dos tokens
		for (String string : lstTokens) {
			String[] split = string.split(Constantes.VALUE_SEPARATOR);
			if(split[0].equalsIgnoreCase("x")){
				celulaAtacou.x = Integer.parseInt(split[1]);
			}
			else if(split[0].equalsIgnoreCase("y")){
				celulaAtacou.y = Integer.parseInt(split[1]);
			}
			else if(split[0].equalsIgnoreCase("tipoCelula")){
				//Atualiza o Id do jogo para futuras referencias
				celulaAtacou.setTipoCelula(Enum.valueOf(TipoCelula.class, split[1]));
			}
			else if(split[0].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				int jogoId = Integer.parseInt(split[1]);
				this.jogo.setIdJogo(jogoId);
			}
		}
		//Dispara o evento que informa o que era a célula atacada pelo jogador		
		fireRespostaAtaque(lstTokens, celulaAtacou);
		
	}

	private void BarcosOponenteCarregados(List<String> lstTokens, String ipEnviou) {
		//int jogoId = -1;
		String nomeAdv = "";
		//String status = "";
		for (String token : lstTokens) {
			String[] split = token.split(Constantes.VALUE_SEPARATOR);
			if(split[0].equalsIgnoreCase("jogoid")){
				//jogoId = Integer.parseInt(split[1]);
			} 
			else if(split[0].equalsIgnoreCase("oponente")){
				nomeAdv = split[1];
			} 
			else if(split[0].equalsIgnoreCase("status")){
				//status = split[1];
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
			if(split[0].equalsIgnoreCase("posicao")){
				posicaoString = split[1];
			}
			else if(split[0].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				jogoId = Integer.parseInt(split[1]);
				this.jogo.setIdJogo(jogoId);
			}

			else if(split[0].equalsIgnoreCase("jogador")){			
				Jogador adv = new Jogador();
				adv.setLogin(split[1]);
				jogadores.add(adv);
			}
		}		
		int posicao = Integer.parseInt(posicaoString);
		//Se a posição for válida, adiciona-o na lista de jogadores do jogo
		if(posicao >= 0){
			try {
				//Enquanto a posição não for a 0, preenche as outras com jogadores dummies (sem reação), pois o tratamento é feito no servidor
//				for(int i = 0; i < posicao; i++){					
//					Jogador adv = new Jogador();
//					adv.setLogin("Player"+(i+1));
//					this.jogo.AdicionarJogador(adv);
//					//Evento já disparado pela classe Jogo, capturado e redisparado por esta, não há necessidade de redundancia
//					//fireJogadorConectado(adv);
//				}
				for(Jogador jogador : jogadores){
					this.jogo.AdicionarJogador(jogador);
				}
				//Cria os tabuleiros para o jogador
				this.perfil.setTabuleiroAtaque(new Tabuleiro(Constantes.TAMANHO_TABULEIRO, false));
				this.perfil.setTabuleiroDefesa(new Tabuleiro(Constantes.TAMANHO_TABULEIRO));	
				//Adiciona o jogador
				this.jogo.AdicionarJogador(perfil, posicao);
				//Será a vez do jogador se ele foi o primeiro a entrar
				this.perfil.setMinhaVez((posicao == 0));
				//Evento já disparado pela classe Jogo, capturado e redisparado por esta, não há necessidade de redundancia
				//fireJogadorConectado(perfil);//TODO: Rever
			} catch (FullGameException e) {
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
			if(split[0].equalsIgnoreCase("posicao")){
				posicaoString = split[1];
			}
			else if(split[0].equalsIgnoreCase("nomeEntrou")){
				nomeJogador = split[1];
			}
			else if(split[0].equalsIgnoreCase("jogoid")){
				//Atualiza o Id do jogo para futuras referencias
				jogoId = Integer.parseInt(split[1]);
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
				//Evento abaixo já é disparado automaticamente pela classe Jogo, tornando esta chamada redundante
				//fireJogadorConectado(novo);
			} catch (FullGameException e) {
				Log.gravarLog(e.getMessage());
				fireFalhaGenericaEvent("Houve um erro ao tentar conectar oponente no jogo.", e);
			}
		}		
	}

	//Método acessado quando EU conecto no servidor
	private void jogadorConectado(List<String> lstTokens, String ipEnviou) {

		CriarJogoLocal();
		fireCarregarTelaJogo(this.jogo);
		this.perfil.setOnline();
		
	}

	private void CriarJogoLocal() {
		jogo = new Jogo(10, 2);
		DefinirListenersJogo();
	}

	public void conectar(String login, String senha) {
		perfil.setLogin(login);
		perfil.setSenha(senha);
		//TODO: Enviar login e senha para validação no servidor
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

	public Jogador getPerfil() {		
		return this.perfil;
	}

	public boolean enviarTabuleiro() {
		return this.perfil.enviarTabuleiroAtaque();		
	}
	
//	private void fireTurnoAlterado(Object src){
//		 Object[] listeners = listenerList.getListenerList();
//	        // Each listener occupies two elements - the first is the listener class
//	        // and the second is the listener instance
//	        for (int i=0; i<listeners.length; i+=2) {
//	            if (listeners[i]==ClientEventListener.class) {
//	                ((ClientEventListener)listeners[i+1]).AlterarTurno(src, new ClientEvent(src, TipoEvento.));
//	            }
//	        }
//	}
}

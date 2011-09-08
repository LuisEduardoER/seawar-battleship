package modelos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.event.EventListenerList;

import exceptions.FullGameException;
import exceptions.GameException;

import utils.Parser;

import Comunicacao.Constantes;
import Comunicacao.DicionarioMensagem;
import Comunicacao.IMessageListener;
import Comunicacao.MessageReceiver;
import Comunicacao.MessageSender;
import Comunicacao.MulticastSender;
import Comunicacao.TipoMensagem;
import Events.ServerEvent;
import Events.ServerEventListener;
import Events.TipoEvento;

//Atenção, os listeners do jogo não são criados porque não são necessários no server
//apenas a interface do cliente precisa necessariamente implementá-los

public class ServidorRestrito implements IMessageListener {
		private ExecutorService serverExecutor; //Executor de comandos em threads paralelas
		ServerSocket serversocket; // Socket do servidor
		private boolean continuarRecebendoConexoes; //flag para receber ou não conexões novas
		int capacidade = 2; //quantidade de jogadores possiveis em um jogo
		int tamanhoTabuleiro = Constantes.TAMANHO_TABULEIRO; //numero de células do tabuleiro
		//Define uma lista de eventos para a classe Servidor
		protected EventListenerList listenerList = new EventListenerList();

		public Jogo jogoCorrente;
		public List<Jogador> aListaJogadorJogando;
		
		public ServidorRestrito(){
			jogoCorrente = new Jogo(10,	capacidade);
			serverExecutor = Executors.newCachedThreadPool();
			continuarRecebendoConexoes = true;
			aListaJogadorJogando = new ArrayList<Jogador>();
		}
		
		public void IniciarServidor(){			
			
			try{
				//Cria socket para ouvir uma porta e conseguir manter até 100 conexões				
				ServerSocket serversocket = new ServerSocket(Comunicacao.Constantes.SERVER_PORT, 100, InetAddress.getByName(Constantes.SERVER_ADDRESS));
				
				//Aguarda os 2 jogadores conectarem no servidor
				while(aListaJogadorJogando.size() < capacidade){
					fireDisplayChangeEvent(new ServerEvent(String.format("Aguardando novo jogador"),TipoEvento.DisplayAtualizado));
					
					
					//aguarda a conexão de um client (chamada bloqueadora)
					Socket clientSocket = serversocket.accept();
					//Cria um recebedor pra cada usuário que conectar NO SERVIDOR (antes de fazer o login)
					serverExecutor.execute(new MessageReceiver(this, clientSocket));
					
					fireDisplayChangeEvent(new ServerEvent(String.format("Usuario conectado (%s)", clientSocket.getInetAddress().getHostAddress()),TipoEvento.DisplayAtualizado));
					
				}
				//fecha a conexão do socket servidor para não aceitar mais conexões
				//serversocket.close();
				//Cria um jogo para quando os 2 jogadores tiverem conectados
				

			}
			catch(Exception ex){
				pararDeReceberConexoes();
				Log.gravarLog("Erro: " + ex.getMessage());
			}
			
		}

		private void firePlayerListChangedEvent(ServerEvent serverEvent) {
			Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ServerEventListener.class) {
	                ((ServerEventListener)listeners[i+1]).playerListChanged(serverEvent);
	            }
	        }
			
		}
		private void fireDisplayChangeEvent(ServerEvent evt) {
	        Object[] listeners = listenerList.getListenerList();
	        // Each listener occupies two elements - the first is the listener class
	        // and the second is the listener instance
	        for (int i=0; i<listeners.length; i+=2) {
	            if (listeners[i]==ServerEventListener.class) {
	                ((ServerEventListener)listeners[i+1]).updateDisplay(evt);
	            }
	        }
	    }

		public void pararDeReceberConexoes(){
			continuarRecebendoConexoes = false;
			serverExecutor.shutdown();
		}


		private void ConectarJogadorNovo(List<String> lstTokens, String ipEnviou, Socket socket) {
			Jogador obj = new Jogador(socket);
			for (String token : lstTokens) {
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("login")){					
					obj.setLogin(split[1]);
				}
				else if(split[0].equalsIgnoreCase("senha")){
					obj.setSenha(split[1]);
				}
			}
			try {
				if(!this.jogoCorrente.isVazio()){
					//Se o jogo não estiver vazio, envia mensagem para os jogadores de plantão que mais 1 jogador entrou no jogo na última posição
					for(Jogador jogadorAguardando : this.jogoCorrente.getListaJogador()){
						String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.OponenteEntrou);
						String mensagemEnviar = String.format(mensagem, this.jogoCorrente.getIdJogo(), obj.getLogin(), aListaJogadorJogando.size());
						MessageSender sender = new MessageSender(jogadorAguardando.getConexao().getSocket(), mensagemEnviar);
						serverExecutor.execute(sender);
					}
				}
				obj.setOnline();
				//Se o jogador não tiver nome, utilizará "Player" e a posição dele na lista como identificador
				if(obj.getLogin().isEmpty())
					obj.setLogin("Player"+(aListaJogadorJogando.size()+1));
				
				this.jogoCorrente.AdicionarJogador(obj); //Esta adição causa exception se o jogo estiver lotado
				
				//Adiciona o jogador à lista de quem tá jogando, apenas se o jogador for adicionado no jogo antes
				aListaJogadorJogando.add(obj);
				//Dispara o evento que informa o que deve-se imprimir na tela
				fireDisplayChangeEvent(new ServerEvent(String.format("%s adicionado ao jogo(%s)", obj.getLogin(), this.jogoCorrente.getIdJogo()), TipoEvento.DisplayAtualizado));
				//firePlayerListChangedEvent(new ServerEvent(obj,TipoEvento.JogadoresAtualizados));
				//informa o jogador que ele foi conectado com sucesso!
				String msgConectado = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.ConectarServidor);
				msgConectado = String.format(msgConectado, obj.getLogin(), ""); //Não repassa a senha para o outro lado
				MessageSender send = new MessageSender(obj.getConexao().getSocket(), msgConectado);
				//Espera enviar a mensagem para que continue a execução
				Future futuro = serverExecutor.submit(send);
				futuro.get(); 				
				
				//também informa o jogador qual a posição dele na lista de jogadores 
				String msgEntrouJogo = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.EntrarJogo);
				String msgEntrouJogoEnviar = String.format(msgEntrouJogo, this.jogoCorrente.getIdJogo(), obj.getLogin(), aListaJogadorJogando.indexOf(obj));
				MessageSender sender = new MessageSender(obj.getConexao().getSocket(), msgEntrouJogoEnviar);
				serverExecutor.execute(sender);
				
			} catch (FullGameException e) {
				//Dispara o evento para imprimir na tela do servidor quando houver problemas
				String mensagem = String.format("Não foi possível conectar o jogador %s ao jogo(%s)\nMotivo:%s", obj.getLogin(), this.jogoCorrente.getIdJogo(), e.getMessage()); 
				Log.gravarLog(mensagem);				
				fireDisplayChangeEvent(new ServerEvent(mensagem, TipoEvento.DisplayAtualizado));
				return; //cai fora do método se o jogo estiver cheio
			}catch(Exception e){
				Log.gravarLog(e.getMessage());
			}
			
			//Se o jogo está lotado agora, envia o comando para os jogadores que podem iniciar o posicionamento ou o jogo
			if(this.jogoCorrente.isLotado()){		

				//Cria um tabuleiro para cada jogador
				for(Jogador jogador : aListaJogadorJogando){
					jogador.setTabuleiroDefesa(new Tabuleiro(tamanhoTabuleiro));
					jogador.setTabuleiroAtaque(new Tabuleiro(tamanhoTabuleiro, false));
					jogador.setJogoId(jogoCorrente.getIdJogo());
				}
				
				this.iniciarPosicionamentos(this.jogoCorrente);
			}
			
		}


		//Permite que os jogadores possam posicionar suas embarcações
		private void iniciarPosicionamentos(Jogo jogo) {
			fireDisplayChangeEvent(new ServerEvent("Jogo criado, aguardando posicionamento de frotas",TipoEvento.DisplayAtualizado));
			//faz o estado de "pronto" dos jogadores ficar em falso
			//para indicar quando posicionaram ou não seus barcos
			//PS: no caso, quando o servidor receber o tabuleiro
			for(Jogador jogador : jogo.getListaJogador()){
				jogador.setPronto(false);
			}
			
		}

		private void iniciarPartida(Jogo jogoIniciar) {
			for(Jogador jogador : aListaJogadorJogando){
				String mensagemIniciar = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.IniciarJogo);
				//Mensagem do tipo iniciar jogo, apenas recebe o jogoId
				String mensagemFormatada = String.format(mensagemIniciar, jogoIniciar.getIdJogo());
				
				MessageSender enviador = new MessageSender(jogador.getConexao().getSocket(), mensagemFormatada);
				//Envia a mensagem para o jogador que está aguardando o inicio da partida
				serverExecutor.execute(enviador);
			}			
			fireDisplayChangeEvent(new ServerEvent("Iniciando a partida do jogo",TipoEvento.DisplayAtualizado));
			try{
				jogoIniciar.IniciarPartida();
			}	
			catch(GameException e)
			{
				String mensagem = String.format("Erro:%s\nReiniciando jogo",e.getMessage());
				fireDisplayChangeEvent(new ServerEvent(mensagem, TipoEvento.DisplayAtualizado));
				
			}
		}

		private Jogador encontrarJogadorPorIpEmJogo(Jogo jogo, Socket socketJogador){
			Jogador retorno = null;
					
			//Procura o jogador no jogo selecionado
			retorno = jogo.EncontrarJogador(socketJogador);
						
			return retorno;
			
		}		
		
		public void addServerEventListener(ServerEventListener listener){
			listenerList.add(ServerEventListener.class, listener);
		}
		public void removeServerEventListener(ServerEventListener listener){
			listenerList.remove(ServerEventListener.class, listener);
		}

		private Jogador encontrarJogadorPorIp(String ipJogador){
			Jogador retorno = null;
					
			for(Jogador obj : aListaJogadorJogando){
				if(obj.getIpJogador().equalsIgnoreCase(ipJogador)){
					retorno = obj;
					break;
				}
			}
			
			return retorno;
			
		}
		private Jogo encontrarJogoPorId(int idJogo){
			//Como só é um servidor de 1 jogo, retona este jogo
			return jogoCorrente;
		}

		
		@Override
		public void mensagemRecebida(String mensagem, Socket socketOrigem) {
			this.registrarLog(mensagem);
			
			/*if(IsMulticastMessage(mensagem)){
				//Envia a mensagem multicast (Todos os jogadores conectados receberão)
				serverExecutor.execute(new MulticastSender(mensagem));
			}
			else{
				//Senão, divide a mensagem em tokens e trata ela para depois enviar apenas para o destinatario correto
				
			}*/
			List<String> lstTokens = new ArrayList<String>();
			StringTokenizer tokens = new StringTokenizer(mensagem, Constantes.TOKEN_SEPARATOR);
			receberTokensMensagem(tokens, socketOrigem);
		}
		private void registrarLog(String mensagem) {
			Log.gravarLog(mensagem);
		}

		@Override
		public void receberTokensMensagem(StringTokenizer tokens, Socket socket) {
			List<String> lstTokens = new ArrayList<String>();
			
			
			//Transforma os tokens em lista
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken().trim();
				if(!token.isEmpty()){
					lstTokens.add(token);
				}
			}//fim da adatapcao da lista de tokens		
			
			TratarTokens(lstTokens, socket);
		}


		
		private void TratarTokens(List<String> lstTokens, Socket socket) {
			// TODO Completar a implementação dos métodos da comunicação
			if(lstTokens == null || lstTokens.isEmpty())
				return;
			
			String header = lstTokens.get(0);

			if((Constantes.TOKEN_SEPARATOR+header).equalsIgnoreCase(Constantes.CONNECT_TOKEN)){
				ConectarJogadorNovo(lstTokens, socket.getInetAddress().getHostAddress(), socket);
			}
			else if((Constantes.TOKEN_SEPARATOR+header).equalsIgnoreCase(Constantes.DISCONNECT_TOKEN)){
				DesconectarJogador(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogadores.toString())){
				//EnviarListaJogadores(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.SerChamadoPorJogador.toString())){
				//ChamarJogadorParaJogar(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogos.toString())){
				//EnviarListaJogosAbertos(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EntrarJogo.toString())){
				//ConectarJogadorEmJogo(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.CriarJogo.toString())){
				//CriarJogoComJogador(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ChamarJogador.toString())){
				//ChamarJogadorParaJogar(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Constantes.PING_TOKEN)){
				//AtualizaUltimoPingJogador(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorDesconectado.toString())){
				DesconectarJogador(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorTimeout.toString())){
				DesconectarJogador(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogoCriado.toString())){
				//AtualizarListaJogos(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.NovoJogadorConectado.toString())){
				//AtualizarListaJogadores(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.OponenteEntrou.toString())){
				//LiberarTelaDeJogo(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.IniciarJogo.toString())){
				//DesbilitarJogoParaNovasConexoes(lstTokens, ipEnviou);			
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.BarcosPosicionados.toString())){
				CarregarBarcosDoJogadorNoJogo(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.AtacarOponente.toString())){
				ProcessarAtaque(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.RespostaAtaque.toString())){
				//É uma mensagem que apenas o jogador receberá como feedback do ataque que realizou
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberAtaque.toString())){
				//É uma mensagem que apenas o jogador receberá quando o seu barco for atacado
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarMensagemOponente.toString())){
				//EnviarMensagemSocketParaAdversario(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberMensagemOponente.toString())){
				//É uma mensagem que apenas o jogador receberá quando o oponente enviar uma mensagem
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.GanhouJogo.toString())){
				DefinirJogadorComoVencedor(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.PerdeuJogo.toString())){
				DefinirJogadorComoPerdedor(lstTokens, socket);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogarComBot.toString())){
				AtivarBotComoOponenteParaJogador(lstTokens, socket);
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

		private void CarregarBarcosDoJogadorNoJogo(List<String> lstTokens,Socket socketEnviou) {

			int jogoId = -1;
			Object tabuleiroObject = null;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
				if(split[0].equalsIgnoreCase("tabuleiro")){
					try {
						tabuleiroObject = Parser.StringParaObjeto(split[1]);
					} catch (IOException e) {
						e.printStackTrace();
						fireDisplayChangeEvent(new ServerEvent("Não foi recebido uma string válida", TipoEvento.DisplayAtualizado));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//Carrega o jogo a partir do ID enviado pelo socket
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				//Encontra o jogador que enviou o socket
				Jogador jogador = jogo.EncontrarJogador(socketEnviou);
				if(jogador != null){
					//Converte o tabuleiro serializado em um tabuleiro no servidor
					Tabuleiro tabuleiro = (Tabuleiro)tabuleiroObject;  //Parser.ConverteTabuleiro(lstTokens);
//					Embarcacao[] barcos = (Embarcacao[])tabuleiroObject;
					//Carrega este tabuleiro como o tabuleiro de defesa deste jogador
					jogador.setTabuleiroDefesa(tabuleiro);
//					jogador.getTabuleiroDefesa().setArrEmbarcacoes(barcos);
					//dispara o evento que envia a mensagem que o tabuleiro foi recebido
					fireDisplayChangeEvent(
							new ServerEvent(String.format("Recebido tabuleiro de %s", jogador.getLogin()), TipoEvento.DisplayAtualizado)
							);
					//Marca o jogador como "pronto", assim o servidor sabe que este jogador
					//está pronto para jogar
					jogador.setPronto(true);
				}
				Jogador adv = jogo.EncontrarJogadorAdversario(jogador);
				if(adv == null){
					//TODO:NoEnemyException
					//Informa que o jogador X não tem adversários para receber a mensagem
					//de que ele já está com o tabuleiro pronto
					fireDisplayChangeEvent(
							new ServerEvent(String.format("%s não possui adversários para informar que está pronto", jogador.getLogin()), TipoEvento.DisplayAtualizado)
							);
					return; //para de executar o método
				}
				//Envia mensagem para o inimigo falando o ID do jogo, o nome do adv q posicionou o barco e o status de OK
				String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.BarcosOponentePosicionados);				
				String msgEnviar = String.format(mensagem, jogo.getIdJogo(), jogador.getLogin(), "OK");				
				MessageSender msgAdv = new MessageSender(adv.getConexao().getSocket(),msgEnviar);
				serverExecutor.execute(msgAdv);
			}
			
			//Inicia a partida quando Todos (2) jogadores enviarem os tabuleiros
			if(jogo.jogadoresProntos()){
				//Chama o método que inicia a partida do jogo
				this.iniciarPartida(jogo);				
			}
		}

		private void AtivarBotComoOponenteParaJogador(List<String> lstTokens,Socket socketEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			//Recupera o jogo que conterá o bot
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				//Encontra o jogador que solicitou o bot
				Jogador jogador = jogo.EncontrarJogador(socketEnviou);
				if(jogador != null){
					//Define o adversário do jogador como um bot
					Jogador bot = jogo.EncontrarJogadorAdversario(jogador);
					bot.setOffline();
					bot.setIsBot(true);
					fireDisplayChangeEvent(
							new ServerEvent(String.format("Jogador %s ativou o bot no lugar do jogador %s", jogador.getLogin(), bot.getLogin()), TipoEvento.DisplayAtualizado)
							);
				}
			}			
		}

		/*
		 * Declara o jogador que enviou a mensagem como um perdedor 
		 */
		private void DefinirJogadorComoPerdedor(List<String> lstTokens, Socket socketEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			//Encontra o jogo da pessoa que perdeu
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				//Recupera o jogador que perdeu o jogo
				Jogador jogador = jogo.EncontrarJogador(socketEnviou);
				if(jogador != null){
					//Atualiza a pontuação do cara (200 pontos por perder?)
					int pontos = 0;
					//Verifica quantos barcos ele afundou
					for(Embarcacao barco : jogador.getTabuleiroAtaque().getArrEmbarcacoes()){
						if(barco.getNaufragado()){
							//pontua pelo valor do barco
							pontos += barco.getValorEmbarcacao();
						}else {
							for(Celula celulaBarco : barco.getListaCelulas()){							
								//Pontua de acordo com as células que ele acertou
								pontos += (celulaBarco.getTipoCelula() == TipoCelula.Embarcacao) ? barco.getValorEmbarcacao()/barco.getTamanho() : 0;
							}
						}
					}
					//Define a pontuação calculada com base nos barcos afundados e partes acertadas
					jogador.setPontuacao(pontos);
					
					//Encontra o adversário do jogador
					Jogador adversario = jogo.EncontrarJogadorAdversario(jogador);
					//Declara o jogador como perdedor e o adversário como ganhador
					this.declararVencedor(jogo, adversario);
					this.declararPerdedor(jogo, jogador);
					//Dispara evento para exibir a mensagem no servidor
					fireDisplayChangeEvent(
							new ServerEvent(String.format("%s ganhou do %s no jogo %s", adversario.getLogin(), jogador.getLogin(), jogo.getIdJogo()), TipoEvento.DisplayAtualizado)
							);
				}
			}
			
		}

		/*
		 * Declara o jogador que enviou a mensagem como um vencedor 
		 */
		private void DefinirJogadorComoVencedor(List<String> lstTokens, Socket socketEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				Jogador jogador = jogo.EncontrarJogador(socketEnviou);
				if(jogador != null){
					//Atualiza a pontuação do cara (200 pontos por perder?)
					int pontos = 0;
					//Verifica quantos barcos ele afundou
					for(Embarcacao barco : jogador.getTabuleiroAtaque().getArrEmbarcacoes()){
						if(barco.getNaufragado()){
							//pontua pelo valor do barco
							pontos += barco.getValorEmbarcacao();
						}else {
							for(Celula celulaBarco : barco.getListaCelulas()){							
								//Pontua de acordo com as células que ele acertou
								pontos += (celulaBarco.getTipoCelula() == TipoCelula.Embarcacao) ? barco.getValorEmbarcacao()/barco.getTamanho() : 0;
							}
						}
					}
					jogador.setPontuacao(pontos);
					
					
					Jogador adversario = jogo.EncontrarJogadorAdversario(jogador);
					this.declararVencedor(jogo, jogador);
					this.declararPerdedor(jogo, adversario);
					fireDisplayChangeEvent(
							new ServerEvent(String.format("%s ganhou do %s no jogo %s", jogador.getLogin(), adversario.getLogin(), jogo.getIdJogo()), TipoEvento.DisplayAtualizado)
							);
				}
			}
			
		}

		private void declararVencedor(Jogo jogo,Jogador vencedor) {
			//Define o vencedor no id do usuário
			//this.jogoCorrente.declaraJogadorVencedor(vencedor.getId_usuario());
			this.jogoCorrente.encerrarJogo();
			
			//Envia para o usuário que ele venceu o jogo
			String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.GanhouJogo);
			String mensagemEnviar = String.format(mensagem, jogo.getIdJogo(), vencedor.getLogin());
			MessageSender sender = new MessageSender(vencedor.getConexao().getSocket(), mensagemEnviar);
			serverExecutor.execute(sender);			
		}

		private void declararPerdedor(Jogo jogo, Jogador perdedor) {
			//Define o vencedor no id do usuário
			this.jogoCorrente.encerrarJogo();
			
			//Envia para o usuário que ele venceu o jogo
			String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.PerdeuJogo);
			String mensagemEnviar = String.format(mensagem, jogo.getIdJogo(), perdedor.getLogin());
			MessageSender sender = new MessageSender(perdedor.getConexao().getSocket(), mensagemEnviar);
			serverExecutor.execute(sender);	
		}

		private void ProcessarAtaque(List<String> lstTokens, Socket socketEnviou) {
			boolean venceuJogo = false;	
			int jogoId = -1;
			for (String token : lstTokens) {
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			Celula celula = Parser.ConverteCelula(lstTokens);
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(celula != null){
				Jogador jogador = encontrarJogadorPorIpEmJogo(jogo, socketEnviou);				
			
				Jogador adversario = encontrarAdversarioEmJogo(jogo, jogador);
				celula = adversario.getTabuleiroDefesa().atacar(celula.x, celula.y);
				
				//Envia ataque para o cliente que foi atacado
				Socket clientSocketAtacado = adversario.getConexao().getSocket();
				String mensagemOriginal = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.ReceberAtaque);
//				for(String token : lstTokens){
//					mensagemOriginal += Constantes.TOKEN_SEPARATOR + token;
//				}
				String mensagemAtaque = String.format(mensagemOriginal, jogoId, celula.x, celula.y);
				serverExecutor.execute(new MessageSender(clientSocketAtacado, mensagemAtaque));
				
				venceuJogo = adversario.getTabuleiroDefesa().isTodosBarcosAfundados();
				
				if(jogador.bIsBot && !venceuJogo){
					//TODO: Implementar a lógica de ataque do bot aqui
				}
				else{
				//Se o jogador não for bot:
				//Envia resposta para o cliente que atacou
				String mensagemResposta = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.RespostaAtaque);
				String mensagemFormatada = String.format(mensagemResposta,jogo.getIdJogo(), celula.x, celula.y, celula.getTipoCelula().toString(), 0); 
				//ordem 0 pq eu não sei ainda como reconhecer qual parte do barco ele acertou
				Socket clientSocket = jogador.getConexao().getSocket();
				
				serverExecutor.execute(new MessageSender(clientSocket, mensagemFormatada));
				}

				//Se o jogador venceu o jogo com esse ataque, informa os 2 jogadores
				if(venceuJogo){
					//Limpa os tokens recebidos e adiciona apenas o que interessa
					//para que a mensagem de vencedor seja procesada
					lstTokens.clear();
					lstTokens.add(String.format("%s%s%s", "jogoid", Constantes.VALUE_SEPARATOR, jogo.getIdJogo()));
					//Define o jogador atacante como vencedor da partida
					DefinirJogadorComoVencedor(lstTokens, socketEnviou);
				}
			}
			
			
		}

		private Jogador encontrarAdversarioEmJogo(Jogo jogo, Jogador jogador) {

			Jogador adversario = null;
			if(jogador != null && jogo != null){
				return jogo.EncontrarJogadorAdversario(jogador);
			}
			return adversario;
		}

		private void DesconectarJogador(List<String> lstTokens, Socket socketEnviou) {
			Jogador jogador = this.encontrarJogadorPorIPNoServidoreJogos(socketEnviou);
			if(jogador != null){
				this.jogoCorrente.removerJogador(jogador);				
				aListaJogadorJogando.remove(jogador);
				jogador.setOffline();
				fireDisplayChangeEvent(new ServerEvent(String.format("%s desconectado", jogador.getLogin()),TipoEvento.DisplayAtualizado));
				
				//informa o adversário sobre a saída do oponente
				Jogador adversario = this.jogoCorrente.EncontrarJogadorAdversario(jogador);
				if(adversario != null){				
					String msg = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.JogadorDesconectado);
					String msgFormatada = String.format(msg, this.jogoCorrente.getIdJogo(), jogador.getLogin(), jogador.getId_usuario());
					MessageSender enviador = new MessageSender(adversario.getConexao().getSocket(), msgFormatada);
					serverExecutor.execute(enviador);
				}
			}
		}

		private Jogador encontrarJogadorPorIPNoServidoreJogos(Socket socketJogador) {
			//TODO: Adaptar para fazer loop na lista de jogos quando for mais de 1 jogo rodando no servidor
			
			return this.jogoCorrente.EncontrarJogador(socketJogador);
		}
		
}

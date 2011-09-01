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

import javax.swing.event.EventListenerList;

import exceptions.FullGameException;

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

public class ServidorRestrito implements IMessageListener {
		private ExecutorService serverExecutor;
		private boolean continuarRecebendoConexoes;
		int capacidade = 2;
		int tamanhoTabuleiro = 20;
		//Define uma lista de eventos para a classe Servidor
		protected EventListenerList listenerList = new EventListenerList();

		public Jogo jogoCorrente;
		public List<Jogador> aListaJogadorJogando;
		
		public ServidorRestrito(){
			
			serverExecutor = Executors.newCachedThreadPool();
			continuarRecebendoConexoes = true;
			aListaJogadorJogando = new ArrayList<Jogador>();
		}
		
		public void IniciarServidor(){
			try{
				//Cria socket para ouvir uma porta e conseguir manter até 100 conexões
				ServerSocket serversocket = new ServerSocket(Comunicacao.Constantes.SERVER_PORT, 100);
				
				//Aguarda os 2 jogadores conectarem no servidor
				while(aListaJogadorJogando.size() < capacidade){
					fireDisplayChangeEvent(new ServerEvent(String.format("Aguardando novo jogador"),TipoEvento.DisplayAtualizado));
					
					
					//aguarda a conexão de um client (chamada bloqueadora)
					Socket clientSocket = serversocket.accept();
					//Cria um recebedor pra cada usuário que conectar
					serverExecutor.execute(new MessageReceiver(this, clientSocket));
					
					fireDisplayChangeEvent(new ServerEvent(String.format("Usuario conectado (%s)", clientSocket.getInetAddress().getHostAddress()),TipoEvento.DisplayAtualizado));
					
					Jogador novo = new Jogador(clientSocket);
					novo.setLogin("Jogador "+(aListaJogadorJogando.size()+1));
					aListaJogadorJogando.add(novo);
				}
				
				//Cria um jogo para quando os 2 jogadores tiverem conectados
				jogoCorrente = new Jogo(10,	capacidade);
				
				//Cria um tabuleiro para cada jogador
				for(Jogador jogador : aListaJogadorJogando){
					jogador.setTabuleiroDefesa(new Tabuleiro(tamanhoTabuleiro));
					jogador.setTabuleiroAtaque(new Tabuleiro(tamanhoTabuleiro, false));
					jogador.setJogoId(jogoCorrente.getIdJogo());
					jogoCorrente.AdicionarJogador(jogador);
					
				}
				
				fireDisplayChangeEvent(new ServerEvent("Jogo criado, aguardando movimentos iniciais",TipoEvento.DisplayAtualizado));
				
			}
			catch(Exception ex){
				pararDeReceberConexoes();
				Log.gravarLog("Erro: " + ex.getMessage());
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


		private void ConectarJogadorNovo(List<String> lstTokens, String ipEnviou) {
			Jogador obj = new Jogador();//TODO: Trocar para pgar o objeto pelo DAO
			obj.setIpJogador(ipEnviou);		
			//obj.setOnline();
			
			try {
				if(!this.jogoCorrente.isVazio()){
					//Se o jogo não estiver vazio, envia mensagem para os jogadores de plantão que mais 1 jogador entrou no jogo
					for(Jogador jogadorAguardando : this.jogoCorrente.getListaJogador()){
						String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.EntrarJogo);
						String mensagemEnviar = String.format(mensagem, this.jogoCorrente.getIdJogo(), obj.getLogin());
						MessageSender sender = new MessageSender(jogadorAguardando.getConexao().getSocket(), mensagem);
						serverExecutor.execute(sender);
					}
				}
				this.jogoCorrente.AdicionarJogador(obj);
				//Adiciona o jogador à lista de quem tá jogando, apenas se o jogador for adicionado no jogo antes
				aListaJogadorJogando.add(obj);
				
			} catch (FullGameException e) {
				String mensagem = String.format("Não foi possível conectar o jogador %s ao jogo(%s)\nMotivo:%s", obj.getLogin(), this.jogoCorrente.getIdJogo(), e.getMessage()); 
				Log.gravarLog(mensagem);				
				fireDisplayChangeEvent(new ServerEvent(mensagem, TipoEvento.DisplayAtualizado));
				return; //cai fora do método se o jogo estiver cheio
			}catch(Exception e){
				Log.gravarLog(e.getMessage());
			}
			
			//Se o jogo está lotado agora, envia o comando para os jogadores que podem iniciar o posicionamento ou o jogo
			if(this.jogoCorrente.isLotado()){				
				this.iniciarPartida(this.jogoCorrente);
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
			jogoIniciar.IniciarPartida();
		}

		private Jogador encontrarJogadorPorIpEmJogo(Jogo jogo, String ipJogador){
			Jogador retorno = null;
					
			for(Jogador obj : jogo.aListaJogador){
				if(obj.getIpJogador().equalsIgnoreCase(ipJogador)){
					retorno = obj;
					break;
				}
			}
			
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
			StringTokenizer tokens = new StringTokenizer(mensagem, Constantes.TOKEN_SEPARATOR);
			receberTokensMensagem(tokens, socketOrigem.getInetAddress().getHostAddress());
		}
		private void registrarLog(String mensagem) {
			System.out.println(mensagem);
		}

		@Override
		public void receberTokensMensagem(StringTokenizer tokens,String ipEnviou) {
			List<String> lstTokens = new ArrayList<String>();
			
			
			//Transforma os tokens em lista
			while (tokens.hasMoreTokens()){
				String token = tokens.nextToken().trim();
				if(!token.isEmpty()){
					lstTokens.add(token);
				}
			}//fim da adatapcao da lista de tokens		
			
			TratarTokens(lstTokens, ipEnviou);
		}


		
		private void TratarTokens(List<String> lstTokens, String ipEnviou) {
			// TODO Completar a implementação dos métodos da comunicação
			if(lstTokens == null || lstTokens.isEmpty())
				return;
			
			String header = lstTokens.get(0);
			if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ConectarServidor.toString())){
				ConectarJogadorNovo(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogadores.toString())){
				//EnviarListaJogadores(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.DesconectarServidor.toString())){
				DesconectarJogador(lstTokens, ipEnviou);
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
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.Ping.toString())){
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
				//LiberarTelaDeJogo(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.IniciarJogo.toString())){
				//DesbilitarJogoParaNovasConexoes(lstTokens, ipEnviou);			
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.BarcosPosicionados.toString())){
				CarregarBarcosDoJogadorNoJogo(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.AtacarOponente.toString())){
				ProcessarAtaque(lstTokens, ipEnviou);
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
				DefinirJogadorComoVencedor(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.PerdeuJogo.toString())){
				DefinirJogadorComoPerdedor(lstTokens, ipEnviou);
			}
			else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogarComBot.toString())){
				AtivarBotComoOponenteParaJogador(lstTokens, ipEnviou);
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

		private void CarregarBarcosDoJogadorNoJogo(List<String> lstTokens,
				String ipEnviou) {

			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				Jogador jogador = jogo.EncontrarJogador(ipEnviou);
				if(jogador != null){
					Tabuleiro tabuleiro = Parser.ConverteTabuleiro(lstTokens);
					jogador.setTabuleiroDefesa(tabuleiro);
					fireDisplayChangeEvent(
							new ServerEvent(String.format("Recebido tabuleiro de %s", jogador.getLogin()), TipoEvento.DisplayAtualizado)
							);
				}
			}
			
		}

		private void AtivarBotComoOponenteParaJogador(List<String> lstTokens,
				String ipEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				Jogador jogador = jogo.EncontrarJogador(ipEnviou);
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
		private void DefinirJogadorComoPerdedor(List<String> lstTokens,
				String ipEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(":");
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				Jogador jogador = jogo.EncontrarJogador(ipEnviou);
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
					this.declararVencedor(jogo, adversario);
					this.declararPerdedor(jogo, jogador);
					
					fireDisplayChangeEvent(
							new ServerEvent(String.format("%s ganhou do %s no jogo %s", adversario.getLogin(), jogador.getLogin(), jogo.getIdJogo()), TipoEvento.DisplayAtualizado)
							);
				}
			}
			
		}

		/*
		 * Declara o jogador que enviou a mensagem como um vencedor 
		 */
		private void DefinirJogadorComoVencedor(List<String> lstTokens,
				String ipEnviou) {
			int jogoId = -1;
			for(String token : lstTokens){
				String[] split = token.split(Constantes.VALUE_SEPARATOR);
				if(split[0].equalsIgnoreCase("jogoid")){
					jogoId = Integer.parseInt(split[1]);
				}
			}
			
			Jogo jogo = this.encontrarJogoPorId(jogoId);
			if(jogo != null){
				Jogador jogador = jogo.EncontrarJogador(ipEnviou);
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
			MessageSender sender = new MessageSender(vencedor.getConexao().getSocket(), mensagem);
			serverExecutor.execute(sender);			
		}

		private void declararPerdedor(Jogo jogo, Jogador perdedor) {
			//Define o vencedor no id do usuário
			this.jogoCorrente.encerrarJogo();
			
			//Envia para o usuário que ele venceu o jogo
			String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.PerdeuJogo);
			String mensagemEnviar = String.format(mensagem, jogo.getIdJogo(), perdedor.getLogin());
			MessageSender sender = new MessageSender(perdedor.getConexao().getSocket(), mensagem);
			serverExecutor.execute(sender);	
		}

		private void ProcessarAtaque(List<String> lstTokens, String ipEnviou) {
						
			Celula celula = Parser.ConverteCelula(lstTokens);
			if(celula != null){
				Jogador jogador = encontrarJogadorPorIpEmJogo(jogoCorrente, ipEnviou);				
			
				Jogador adversario = encontrarAdversarioEmJogo(jogoCorrente, jogador);
				celula = adversario.getTabuleiroDefesa().atacar(celula.x, celula.y);
				
				//Envia ataque para o cliente que foi atacado
				Socket clientSocketAtacado = adversario.getConexao().getSocket();
				String mensagemOriginal = "";
				for(String token : lstTokens){
					mensagemOriginal += Constantes.TOKEN_SEPARATOR + token;
				}
				serverExecutor.execute(new MessageSender(clientSocketAtacado, mensagemOriginal));

				if(jogador.bIsBot){
					//TODO: Implementar a lógica de ataque do bot aqui
				}
				else{
				//Se o jogador não for bot:
				//Envia resposta para o cliente que atacou
				String mensagemResposta = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.RespostaAtaque);
				String mensagemFormatada = String.format(mensagemResposta,jogoCorrente.getIdJogo(), celula.x, celula.y, celula.getTipoCelula().toString(), 0); 
				//ordem 0 pq eu não sei ainda como reconhecer qual parte do barco ele acertou
				Socket clientSocket = jogador.getConexao().getSocket();
				
				serverExecutor.execute(new MessageSender(clientSocket, mensagemFormatada));
				}
			}
			
			
		}

		private Socket MontarSocket(String ipJogador) {
			Socket retorno = null;
			try {
				retorno = new Socket(InetAddress.getByName(ipJogador), Constantes.SERVER_PORT);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retorno;
		}

		private Jogador encontrarAdversarioEmJogo(Jogo jogo, Jogador jogador) {

			Jogador adversario = null;
			if(jogador != null && jogo != null){
				return jogo.EncontrarJogadorAdversario(jogador);
			}
			return adversario;
		}

		private void DesconectarJogador(List<String> lstTokens, String ipEnviou) {
			// TODO Auto-generated method stub
			
		}
		
		
}

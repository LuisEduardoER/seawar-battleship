package modelos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.EventListenerList;

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
					jogador.setTabuleiroDefesa(new Tabuleiro(20));
					jogador.setTabuleiroAtaque(new Tabuleiro(20, false));
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
		}

		private Jogador EncontrarJogadorPorIpEmJogo(Jogo jogo, String ipJogador){
			Jogador retorno = null;
					
			for(Jogador obj : jogo.aListaJogador){
				if(obj.getIpJogador().equalsIgnoreCase(ipJogador)){
					retorno = obj;
					break;
				}
			}
			
			return retorno;
			
		}		
		
		public void AddServerEventListener(ServerEventListener listener){
			listenerList.add(ServerEventListener.class, listener);
		}
		public void RemoveServerEventListener(ServerEventListener listener){
			listenerList.remove(ServerEventListener.class, listener);
		}

		private Jogador EncontrarJogadorPorIp(String ipJogador){
			Jogador retorno = null;
					
			for(Jogador obj : aListaJogadorJogando){
				if(obj.getIpJogador().equalsIgnoreCase(ipJogador)){
					retorno = obj;
					break;
				}
			}
			
			return retorno;
			
		}
		private Jogo EncontrarJogoPorId(int idJogo){
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
				//CarregarBarcosDoJogadorNoJogo(lstTokens, ipEnviou);
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

		private void AtivarBotComoOponenteParaJogador(List<String> lstTokens,
				String ipEnviou) {
			// TODO Auto-generated method stub
			
		}

		private void DefinirJogadorComoPerdedor(List<String> lstTokens,
				String ipEnviou) {
			// TODO Auto-generated method stub
			
		}

		private void DefinirJogadorComoVencedor(List<String> lstTokens,
				String ipEnviou) {
			// TODO Auto-generated method stub
			
		}

		private void ProcessarAtaque(List<String> lstTokens, String ipEnviou) {
						
			Celula celula = Parser.ConverteCelula(lstTokens);
			if(celula != null){
				Jogador jogador = EncontrarJogadorPorIpEmJogo(jogoCorrente, ipEnviou);
				Jogador adversario = EncontrarAdversarioEmJogo(jogoCorrente, jogador);
				celula = adversario.getTabuleiroDefesa().atacar(celula.x, celula.y);

				//Envia ataque para o cliente que foi atacado
				Socket clientSocketAtacado = adversario.getConexao().socket;
				String mensagemOriginal = "";
				for(String token : lstTokens){
					mensagemOriginal += Constantes.TOKEN_SEPARATOR + token;
				}
				serverExecutor.execute(new MessageSender(clientSocketAtacado, mensagemOriginal));
				
				//Envia resposta para o cliente que atacou
				String mensagemResposta = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.RespostaAtaque);
				Socket clientSocket = jogador.getConexao().socket;
				serverExecutor.execute(new MessageSender(clientSocket, mensagemResposta));
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

		private Jogador EncontrarAdversarioEmJogo(Jogo jogo, Jogador jogador) {

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

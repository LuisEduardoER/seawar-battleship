package modelos;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Jogo.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.event.EventListenerList;

import exceptions.FullGameException;
import exceptions.GameException;

import Events.JogoEventListener;
import Events.JogoEvent;
import Events.ServerEventListener;
import Events.TipoEvento;


public class Jogo {
	public int iCodJogadorVencedor;
	private int codJogo;
	private int capacidadeJogo;
	List<Jogador> aListaJogador;
	boolean bJogoEncerrado;
	boolean bAceitaNovosJogadores;
	Date DataCriacaoJogo;
	//Lista de eventos que possui a classe jogo
	protected EventListenerList listenerList = new EventListenerList();
	
	public Jogo(int jogoId, int numeroJogadores){
		aListaJogador = new ArrayList<Jogador>();//[numeroJogadores];
		capacidadeJogo = numeroJogadores;
		DataCriacaoJogo = new Date();
		bJogoEncerrado = false;
		bAceitaNovosJogadores = true;
		iCodJogadorVencedor = 0;		
		setIdJogo(jogoId);
	}
	
	public void AdicionarJogador(Jogador obj) throws FullGameException{
		//Verifica se o jogo n�o est� lotado e se aceita novos jogadores ainda
		if(!isLotado() && aceitaNovosJogadores()){
			obj.setJogoId(this.getIdJogo());
			aListaJogador.add(obj);
			fireConnectedEvent(new JogoEvent(obj, TipoEvento.JogadorConectado));
		}
		else{
			throw new FullGameException("Este jogo est� lotado e n�o pode receber mais jogadores");
		}
	}
	public void AdicionarJogador(Jogador obj, int index) throws GameException{
		//Verifica se o jogo n�o est� lotado e se aceita novos jogadores ainda
		if(!isLotado() && aceitaNovosJogadores()){
			//if(this.contemJogador(obj)){
			//	throw new GameException("Jogador j� est� em jogo e n�o pode ser readicionado");
			//}
			
			aListaJogador.add(index, obj);
			obj.setJogoId(this.codJogo);
			fireConnectedEvent(new JogoEvent(obj, TipoEvento.JogadorConectado));
		}
		else{
			throw new FullGameException("Este jogo est� lotado e n�o pode receber mais jogadores");
		}
	}
	private boolean contemJogador(Jogador obj) {
		Jogador encontrado = EncontrarJogador(obj.getId_usuario());
		return (encontrado != null);
	}

	public void removerJogador(Jogador obj){
		if(this.aListaJogador.size() > 0){
			obj.setJogoId(0);//Retira o ID do jogo, para indicar que o jogador n�o est� jogando
			this.aListaJogador.remove(obj);
			fireDisconnectedEvent(new JogoEvent(obj, TipoEvento.JogadorDesconectado));
		}
	}
	public boolean isVazio(){
		return this.aListaJogador.size() == 0;
	}
	public boolean aceitaNovosJogadores(){
		return bAceitaNovosJogadores;
	}
	public boolean isLotado(){
		return this.aListaJogador.size() == capacidadeJogo;
	}
	public void setIdJogo(int codJogo) {
		this.codJogo = codJogo;
	}

	public int getIdJogo() {
		return codJogo;
	}

	public int getCodJogadorVencedor() {
		return iCodJogadorVencedor;
	}

	public void setCodJogadorVencedor(int iCodJogadorVencedor) {
		this.iCodJogadorVencedor = iCodJogadorVencedor;
	}

	public List<Jogador> getListaJogador() {
		return aListaJogador;
	}

	public void setListaJogador(List<Jogador> aListaJogador) {
		this.aListaJogador = aListaJogador;
	}

	public boolean isJogoEncerrado() {
		return bJogoEncerrado;
	}

	private void setJogoEncerrado(boolean bJogoEncerrado) {
		this.bJogoEncerrado = bJogoEncerrado;
	}

	public Date getDataCriacaoJogo() {
		return DataCriacaoJogo;
	}

	public void setDataCriacaoJogo(Date dataCriacaoJogo) {
		DataCriacaoJogo = dataCriacaoJogo;
	}

	public static Jogo iniciarJogo(int jogoId, List<Jogador> jogadores) {
	
		Jogo jogo = new Jogo(jogoId,jogadores.size());
		jogo.setListaJogador(jogadores);
		jogo.setJogoEncerrado(false);
		return jogo;
	}
	

	/**
	 * N�o utilizar este m�todo, para conectar o jogador utilize AdicionarJogador()
	 * PS: Acho que ter� que mudar na documenta��o tamb�m, no diagrama de classe
	 * pq esse m�todo faz sentido no servidor e n�o no jogo
	 */
//	public void conectarJogador(Jogador jogador) {		
//		try {
//			AdicionarJogador(jogador);
//		} catch (FullGameException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void gerarJogo() {
		//TODO Verificar o que faz esse m�todo
	}
	
	public boolean sePingarJogador(Jogador jogador) {
		String strIp = jogador.getIpJogador();
		
		try {
			Socket sock = new Socket(strIp, 80);
			return sock.isConnected();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void encerrarJogo() {
		this.bJogoEncerrado = true;
		fireGameOverEvent(new JogoEvent(getJogadorVencedor(), TipoEvento.EncerrouJogo));
	}
	
	private Jogador getJogadorVencedor() {
		for (Jogador jogador : this.aListaJogador) {
			if(jogador.getId_usuario() == this.iCodJogadorVencedor){
				return jogador;
			}
		}
		return null;
	}

	public void atuarEm(Jogador jogador, Celula celula) {
		Tabuleiro tabuleiro = jogador.getTabuleiroAtaque();
	}
	
	public Jogador EncontrarJogador(Socket socketJogador){
		Jogador jogadorRetorno = null;
		
		for(int i = 0; i < aListaJogador.size(); i++){
			if(aListaJogador.get(i).getConexao().getSocket().equals(socketJogador)){
				jogadorRetorno = aListaJogador.get(i);
				break; //Sai do loop quando encontra o jogador pelo IP
			}
		}
		return jogadorRetorno;
	}
	public Jogador EncontrarJogador(int id){
		Jogador jogadorRetorno = null;
		
		for(int i = 0; i < aListaJogador.size(); i++){
			if(aListaJogador.get(i).getId_usuario() == (id)){
				jogadorRetorno = aListaJogador.get(i);
				break; //Sai do loop quando encontra o jogador pelo IP
			}
		}
		return jogadorRetorno;
	}

	public Jogador EncontrarJogadorAdversario(Jogador jogador) {
		
		for(Jogador adv : aListaJogador){
			if(!adv.getLogin().equals(jogador.getLogin())){
				return adv;
			}
		}
		
		return null;
		
	}
	
	

	public void AddClientEventListener(JogoEventListener listener){
		listenerList.add(JogoEventListener.class, listener);
	}
	public void RemoveClientEventListener(JogoEventListener listener){
		listenerList.remove(JogoEventListener.class, listener);
	}
	private void fireConnectedEvent(JogoEvent evt){
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==JogoEventListener.class) {
                ((JogoEventListener)listeners[i+1]).JogadorConectado(evt.getSource(), evt);
            }
        }
	}
	private void fireDisconnectedEvent(JogoEvent evt){
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==JogoEventListener.class) {
                ((JogoEventListener)listeners[i+1]).JogadorDesconectado(evt.getSource(), evt);
            }
        }
	}
	//M�todo ser� utilizado apenas na classe Cliente, esta n�o precisa deste m�todo
//	private void fireAttackedEvent(JogoEvent evt){
//		Object[] listeners = listenerList.getListenerList();
//        // Each listener occupies two elements - the first is the listener class
//        // and the second is the listener instance
//        for (int i=0; i<listeners.length; i+=2) {
//            if (listeners[i]==JogoEventListener.class) {
//                ((JogoEventListener)listeners[i+1]).JogadorAtacado(evt.getSource(), evt);
//            }
//        }
//	}

	private void fireGameStartedEvent(JogoEvent evt) {
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==JogoEventListener.class) {
                ((JogoEventListener)listeners[i+1]).JogoIniciado(evt.getSource(), evt);
            }
        }
	}

	private void fireGameOverEvent(JogoEvent evt) {
		Object[] listeners = listenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==JogoEventListener.class) {
                ((JogoEventListener)listeners[i+1]).JogoTerminado(evt.getSource(), evt);
            }
        }
	}
	/*
	 * Inicia a partida do jogo (a fase de posicionamento de frota � uma anterior) 
	 */
	public void IniciarPartida() throws GameException {
		if(this.isVazio()){
			return; //implementar uma exception, talvez
		}
		int i = 0;
		Jogador jogadorFirst;
		do{
			jogadorFirst = this.aListaJogador.get(i);
			i++;
		}
		while((jogadorFirst != null && jogadorFirst.isBot()) && i <= this.aListaJogador.size());
		
		if(jogadorFirst == null && i == this.aListaJogador.size()){
			//Se caiu aqui, significa que apenas jogadores BOTS est�o nesse jogo... ent�o remove todos
			this.aListaJogador.removeAll(aListaJogador);
			//TODO: Atirar uma exception aqui
			throw new GameException("Jogo apenas com bots");
		}
		
		//indica que o jogador que conectou primeiro come�ar� a partida
		jogadorFirst.setMinhaVez(true);		
		
		fireGameStartedEvent(new JogoEvent(this, TipoEvento.JogoIniciado));
		
	}

	public boolean jogadoresProntos() {
		int qtdProntos = 0;
		for (Jogador jogador : aListaJogador) {
			if(jogador.isPronto()){
				qtdProntos++;
			}
		}
		//Se todos os jogares est�o prontos, retorna true
		return qtdProntos == aListaJogador.size();
	}

	public void setBot(Jogador jogador) {
		String loginName = jogador.getLogin();
		jogador.setLogin(String.format("%s (BOT)",loginName));
		
	}

	public void setVencedor(Jogador vencedor) {
		this.setCodJogadorVencedor(vencedor.getId_usuario());
		fireGameOverEvent(new JogoEvent(vencedor, TipoEvento.EncerrouJogo));		
	}

	@Override
	public String toString() {
		return String.format("Jogo%d (%d/%d)", this.codJogo, this.aListaJogador.size(), this.capacidadeJogo);
	}
	
}

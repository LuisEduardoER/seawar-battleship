package modelos;

import java.net.InetAddress;
import java.net.Socket;

import dao.JogadorDAO;


//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Jogador.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//	@ Reviewer: @Vitor
//




public class Jogador extends Usuario {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String sIpJogador;
	public boolean bIsBot;
	public Tabuleiro oTabuleiroAtaque;
	public Tabuleiro oTabuleiroDefesa;
	public Conexao conexaoJogador;
	private int jogoId; //Variavel para saber qual o id do jogo q ele est� jogando
	boolean online = false;
	boolean myTurn = false;
	public Jogador(){
		conexaoJogador = new Conexao(this);
	}
	
	public Jogador(Socket clientSocket) {
		//Cria um jogador com um socket especifico
		sIpJogador = clientSocket.getInetAddress().getHostAddress();
		//Cria uma conex�o utilizando o mesmo socket do jogador
		conexaoJogador = new Conexao(this, clientSocket);
	}

	public String getIpJogador() {
		return sIpJogador;
	}
	
	public void setIpJogador(String ipJogador) {
		sIpJogador = ipJogador;
	}
	public boolean estaNaVez(){
		return myTurn;
	}
	public void setVez(boolean isSuaVez){
		myTurn = isSuaVez;
	}
	public boolean isBot() {
		return bIsBot;
	}
	
	public void setIsBot(boolean value) {
		bIsBot = value;
	}
	
	public Tabuleiro getTabuleiroAtaque() {
		return oTabuleiroAtaque;
	}
	
	public void setTabuleiroAtaque(Tabuleiro tabuleiro) {
		oTabuleiroAtaque = tabuleiro;
	}
	
	public Tabuleiro getTabuleiroDefesa() {
		return oTabuleiroDefesa;
	}
	
	public void setTabuleiroDefesa(Tabuleiro tabuleiro) {
		oTabuleiroDefesa = tabuleiro;
	}
	
	public boolean isOnline() {
		
		return online;//JogadorDAO.IsOnline(this.iId_usuario);
	}
	public static boolean isOnline(int id_usuario) {
		
		return JogadorDAO.IsOnline(id_usuario);
		
	}
	public boolean setOffline() {
		//Ativa o bot
		//setIsBot(true);
		//setIpJogador("localhost");
		online = false;
		boolean removido = true ;//TODO: Descomentar: JogadorDAO.RemoverJogador(this);
		return removido;
	}
	
	public boolean setOnline() {
		
		boolean inserido = true;//TODO: Descomentar: JogadorDAO.InserirJogador(this);
		online = true;
		return inserido;
	}
	
	public boolean isJogando() {
		boolean jogando = false;
		
		jogando = JogadorDAO.IsJogando(this.iId_usuario);
		
		return jogando;
	}
	public int getJogoId(){
		return jogoId;
	}
	public void setJogoId(int id){
		jogoId = id;
	}
	
	public Conexao getConexao(){
		return this.conexaoJogador;
	}
	
	public void Atacar(int x, int y){
		Celula cel = getTabuleiroAtaque().encontrarCelula(x, y);
		this.conexaoJogador.enviarAtaque(cel);
	}
}



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
	public String sIpJogador;
	public boolean bIsBot;
	public Tabuleiro oTabuleiroAtaque;
	public Tabuleiro oTabuleiroDefesa;
	
	public String getIpJogador() {
		return sIpJogador;
	}
	
	public void setIpJogador(String ipJogador) {
		sIpJogador = ipJogador;
	}
	
	public boolean isBot() {
		return bIsBot;
	}
	
	private void setIsBot(boolean value) {
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
	
	public boolean isOnLine(int id_usuario) {
		
		return JogadorDAO.IsOnline(id_usuario);
		
	}
	
	public boolean setOffline() {
		//Ativa o bot
		setIsBot(true);
		
		boolean removido = JogadorDAO.RemoverJogador(this);
		return removido;
	}
	
	public boolean setOnline() {
		
		boolean inserido = JogadorDAO.InserirJogador(this);
		
		return inserido;
	}
	
	public boolean isJogando() {
		boolean jogando = false;
		
		jogando = JogadorDAO.IsJogando(this.iId_usuario);
		
		return jogando;
	}
}

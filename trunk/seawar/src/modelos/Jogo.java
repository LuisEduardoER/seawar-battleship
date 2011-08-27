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


public class Jogo {
	public int iCodJogadorVencedor;
	private int codJogo;
	public Jogador[] aListaJogador;
	public boolean bJogoEncerrado;
	public Date DataCriacaoJogo;
	
	public Jogo(int jogoId, int numeroJogadores){
		aListaJogador = new Jogador[numeroJogadores];
		DataCriacaoJogo = new Date();
		bJogoEncerrado = false;
		iCodJogadorVencedor = 0;		
		setIdJogo(jogoId);
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

	public Jogador[] getListaJogador() {
		return aListaJogador;
	}

	public void setListaJogador(Jogador[] aListaJogador) {
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

	public static Jogo iniciarJogo(int jogoId, Jogador[] jogadores) {
	
		Jogo jogo = new Jogo(jogoId,jogadores.length);
		jogo.setListaJogador(jogadores);
		jogo.setJogoEncerrado(false);
		return jogo;
	}
	
	public void conectarJogador(Jogador jogador) {
		
		for(int i = 0; i < aListaJogador.length; i++){
			if(aListaJogador[i] == null)
				aListaJogador[i] = jogador;
		}
	}
	
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
	}
	
	public void atuarEm(Jogador jogador, Celula celula) {
		Tabuleiro tabuleiro = jogador.getTabuleiroAtaque();
	}
}
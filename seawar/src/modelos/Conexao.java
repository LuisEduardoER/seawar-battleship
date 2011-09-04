package modelos;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import exceptions.TabuleiroIOException;

import utils.Parser;

import Comunicacao.Constantes;
import Comunicacao.DicionarioMensagem;
import Comunicacao.IMessageListener;
import Comunicacao.MessageSender;
import Comunicacao.TipoMensagem;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Conexao.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//




public class Conexao implements IMessageListener {
	private String sIp_servidor;
	private int porta_servidor;
	
	ExecutorService executor;
	Socket socket;
	Formatter output;
	Jogador player;
	
	
	public Conexao(Jogador jogador){
		player = jogador;
		executor = Executors.newCachedThreadPool();
		sIp_servidor = Constantes.SERVER_ADDRESS;
		porta_servidor = Constantes.SERVER_PORT;
		try {
			socket = new Socket(InetAddress.getByName(sIp_servidor), porta_servidor);
			
		} catch (UnknownHostException e) {
			//TODO: fazer algo se ocorrer exception se host desconhecido
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Fazer algo se n�o tiver IO
			e.printStackTrace();
		}
	}
	public Conexao(Jogador jogador, Socket clientSocket) {
		socket = clientSocket;
		player = jogador;
		executor = Executors.newCachedThreadPool();
	}
	public Socket getSocket(){
		return this.socket;
	}
	public void conectarJogador() {
		if(player.isOnline())
			return;
		String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.ConectarServidor);
		MessageSender send = new MessageSender(this.socket, mensagem);
		send.run();
	}
	
	public void desconectarJogador() {
		if(!player.isOnline())
			return;
		String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.DesconectarServidor);
		Runnable send = new MessageSender(this.socket, mensagem);
		try {
			//Aguarda o envio da mensagem para continuar a execu��o
			Future futuro = executor.submit(send);
			futuro.get();
			
			this.socket.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void enviarAtaque(Celula celulaAtacada) {
		if(!player.isOnline())
			return;
		
		try {
			Formatter output = new Formatter(socket.getOutputStream());
			String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.AtacarOponente);
			
			output.format(mensagem, celulaAtacada.x, celulaAtacada.y);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void enviarTabuleiro(Tabuleiro tabuleiro) throws TabuleiroIOException{
		if(!player.isOnline()){
			return;
		}
		String mensagem = DicionarioMensagem.GerarMensagemPorTipo(TipoMensagem.BarcosPosicionados);
		String tabuleiroSerializado;
		try {
			tabuleiroSerializado = Parser.ObjetoParaString(tabuleiro);
		} catch (IOException e) {
			tabuleiroSerializado = "";
			e.printStackTrace();
			throw new TabuleiroIOException(e.getMessage());
		}
		String mensagemEnviar = String.format(mensagem, player.getJogoId(), tabuleiroSerializado, "OK");
		MessageSender send = new MessageSender(this.socket, mensagemEnviar);
		executor.execute(send);
	}
	
	public void receberAtaque() {
		if(!player.isOnline())
			return;
	}
	
	public void enviarVitoria(Jogador jogador) {
		if(!player.isOnline())
			return;
	}
	
	public void receberVitoria() {
		if(!player.isOnline())
			return;
	}
	
	public void enviarAcao(String comando) {
		if(!player.isOnline())
			return;
	}
	
	public void receberAcao() {
		if(!player.isOnline())
			return;
	}
	@Override
	public void mensagemRecebida(String mensagem, Socket socketOrigem) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receberTokensMensagem(StringTokenizer tokens, Socket socket) {
		// TODO Auto-generated method stub
		
	}
}
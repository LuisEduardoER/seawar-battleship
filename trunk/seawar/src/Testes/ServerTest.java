package Testes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

import modelos.Tabuleiro;

import utils.Parser;

import GUI.ServerGUI;


public class ServerTest {

	public static void main(String args[]){
		
//		ServerGUI server = new ServerGUI();
//		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		server.IniciarServidor();
		
		try {
			ServerSocket server = new ServerSocket(2225,1);
			System.out.println("Aguardando conexao");
			Socket sock = server.accept();
			InputStreamReader oomg = new InputStreamReader(sock.getInputStream());
			
			BufferedReader read = new BufferedReader(oomg);
			String linha = read.readLine();
			
			Tabuleiro tab=null;
			try {
				tab = (Tabuleiro)Parser.StringParaObjeto(linha);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tab.pintarTabuleiro();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

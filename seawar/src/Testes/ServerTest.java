package Testes;

import javax.swing.JFrame;

import GUI.ServerGUI;


public class ServerTest {

	public static void main(String args[]){
		
		ServerGUI server = new ServerGUI();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.IniciarServidor();
	}
}

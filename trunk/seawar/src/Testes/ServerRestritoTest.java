package Testes;

import javax.swing.JFrame;

import GUI.ServerGUI;
import GUI.ServerRestritoGUI;

public class ServerRestritoTest {

	public static void main(String args[]){
		
		ServerRestritoGUI server = new ServerRestritoGUI();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.IniciarServidor();
	}
}
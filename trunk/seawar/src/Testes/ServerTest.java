
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
		
		ServerGUI server = new ServerGUI();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.IniciarServidor();
		
	}
}

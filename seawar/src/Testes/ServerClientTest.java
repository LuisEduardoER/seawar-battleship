package Testes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;

import modelos.Jogador;

import Comunicacao.Constantes;
import Comunicacao.MessageSender;

public class ServerClientTest {

	static boolean fechar = false;
	public static void main(String args[]){
		
		try {
			Socket clientSocket = new Socket(InetAddress.getByName(Constantes.SERVER_ADDRESS), Constantes.SERVER_PORT);
			Jogador jogador = new Jogador(clientSocket);
			jogador.getConexao().conectarJogador();
						
			int segundos = 0;
			//Verifica se ja pode fechar ou se aguardou mais de 20 segundos para poder fechar o programa
			while(!fechar && segundos <= 20){
				Thread.sleep(5000); //aguarda 5 segundos e verifica se pode fechar já
				segundos += 5;
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			System.exit(0);
		}
		
	}
}

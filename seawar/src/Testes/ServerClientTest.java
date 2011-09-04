package Testes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;

import modelos.Jogador;
import modelos.Tabuleiro;

import Comunicacao.Constantes;
import Comunicacao.MessageSender;

public class ServerClientTest {

	static boolean fechar = false;
	public static void main(String args[]){
		
		try {
			Socket clientSocket = new Socket(InetAddress.getByName(Constantes.SERVER_ADDRESS), Constantes.SERVER_PORT);
			Jogador jogador = new Jogador(clientSocket);
			jogador.conectar();
						
			int segundos = 0;
			jogador.setTabuleiroDefesa(new Tabuleiro(10));
			//Envia o tabuleiro para o servidor
			if(jogador.getConexao().getSocket().isConnected()){
				jogador.setOnline();
				System.out.println("Enviando tabuleiro para servidor");
				Tabuleiro defesa = jogador.getTabuleiroDefesa();
				
				jogador.conexaoJogador.enviarTabuleiro(defesa);
			}
			
			//Verifica se ja pode fechar ou se aguardou mais de 20 segundos para poder fechar o programa
			while(!fechar && segundos <= 25){
				Thread.sleep(5000); //aguarda 5 segundos e verifica se pode fechar já
				segundos += 5;
			}
			jogador.desconectar();
			
			
			
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

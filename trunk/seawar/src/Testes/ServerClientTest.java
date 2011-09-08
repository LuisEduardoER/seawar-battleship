package Testes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;

import client.Cliente;

import modelos.Celula;
import modelos.Jogador;
import modelos.Tabuleiro;

import Comunicacao.Constantes;
import Comunicacao.MessageSender;
import Events.ClientEvent;
import Events.ClientEventListener;

public class ServerClientTest {

	static boolean fechar = false;
	static boolean conectado = false;
	public static void main(String args[]){
		
		try {
			Socket clientSocket = new Socket(InetAddress.getByName(Constantes.SERVER_ADDRESS), Constantes.SERVER_PORT);
			//Jogador jogador = new Jogador(clientSocket);
			Cliente client = new Cliente(clientSocket);
			Jogador jogador = client.getPerfil();
			jogador.conectar();
			int segundos = 0;
			/*jogador.setTabuleiroDefesa(new Tabuleiro(Constantes.TAMANHO_TABULEIRO));
			//Envia o tabuleiro para o servidor
			if(jogador.getConexao().getSocket().isConnected()){
				jogador.setOnline();
				System.out.println("Enviando tabuleiro para servidor");
				Tabuleiro defesa = jogador.getTabuleiroDefesa();
				
				jogador.conexaoJogador.enviarTabuleiro(defesa);
			}
			*/
			while(!conectado && segundos <= 25){
				Thread.sleep(5000); //aguarda 5 segundos e verifica se conectou
				segundos += 5;
				
			}
			segundos = 0; //limpa a variavel dos segundos
			if(conectado && jogador.enviarTabuleiroAtaque()){
				System.out.println("Enviei tabuleiro para servidor");
			}
			//Verifica se ja pode fechar ou se aguardou mais de 20 segundos para poder fechar o programa
			while((!fechar && segundos <= 25)){
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
	
	class clientEventos implements ClientEventListener{

		@Override
		public void ativarBot(Object src, Jogador bot) {
			System.out.println("Ativei o bot");
			
		}

		@Override
		public void barcosOponentePosicionados(Object src) {
			System.out.println("Oponente posicionou barcos");			
		}

		@Override
		public void carregarTelaJogo(Object jogo) {
			System.out.println("Carreguei o jogo");
			
		}

		@Override
		public void exibeMensagem(Object mensagem) {
			System.out.println("Mostra msg");
			
		}

		@Override
		public void falhaGenerica(Object source, Exception exception) {
			System.out.println("#Fail");
			
		}

		@Override
		public void fimDeJogo(Object source, ClientEvent evt) {
			System.out.println("Fim do jogo");
			
		}

		@Override
		public void jogadorConectado(Object source, ClientEvent evt) {
			System.out.println("Conectei ao jogo");
			fechar = true;
		}

		@Override
		public void jogadorDesconectado(Object source, ClientEvent evt) {
			System.out.println("Desconectei do jogo");
			
		}

		@Override
		public void jogoIniciado(Object source, ClientEvent evt) {
			System.out.println("começou o jogo");
			
		}

		@Override
		public void respostaTiro(Object source, ClientEvent evt, Celula celula) {
			System.out.println("Tome tiro, safado!");
			
		}

		@Override
		public void tiroRecebido(Object source, ClientEvent evt, Celula celula) {
			System.out.println("Tomei bala! Socorro");
			
		}

		@Override
		public void turnoAlterado(Object source, ClientEvent evt) {
			System.out.println("minha vez?");
			
		}
		
	}
}

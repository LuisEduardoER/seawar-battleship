package client;

import java.applet.Applet;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import modelos.Jogador;

import Comunicacao.Constantes;
import Comunicacao.IMessageListener;
import Comunicacao.MessageReceiver;
import Comunicacao.MessageSender;

public class Cliente implements IMessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MessageReceiver recebedor;
	MessageSender sender;
	Socket mySocket;
	Jogador perfil;
	
	public Cliente(Socket socket){		
		mySocket = socket;
		recebedor = new MessageReceiver(this, mySocket);		
	}
	
	@Override
	public void mensagemRecebida(String mensagem, Socket socketOrigem) {
		StringTokenizer tokens = new StringTokenizer(mensagem, Constantes.TOKEN_SEPARATOR);
		receberTokensMensagem(tokens, socketOrigem.getInetAddress().getHostAddress());

	}

	@Override
	public void receberTokensMensagem(StringTokenizer tokens, String ipEnviou) {
		List<String> lstTokens = new ArrayList<String>();
		
		
		//Transforma os tokens em lista
		while (tokens.hasMoreTokens()){
			String token = tokens.nextToken().trim();
			if(!token.isEmpty()){
				lstTokens.add(token);
			}
		}//fim da adatapcao da lista de tokens		
		
		TratarTokens(lstTokens, ipEnviou);
	}

	private void TratarTokens(List<String> lstTokens, String ipEnviou) {
		// TODO Auto-generated method stub
		if(lstTokens == null || lstTokens.isEmpty())
			return;
		
		String header = lstTokens.get(0);
		//Aqui são a lista de ações para cada tipo de mensagem RECEBIDA (não confunda com enviada)
		if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ConectarServidor.toString())){
			DefinirJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogadores.toString())){
			//EnviarListaJogadores(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.DesconectarServidor.toString())){
			//DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.SerChamadoPorJogador.toString())){
			//ChamarJogadorParaJogar(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarListaJogos.toString())){
			//EnviarListaJogosAbertos(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EntrarJogo.toString())){
			//ConectarJogadorEmJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.CriarJogo.toString())){
			//CriarJogoComJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ChamarJogador.toString())){
			//ChamarJogadorParaJogar(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.Ping.toString())){
			//AtualizaUltimoPingJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorDesconectado.toString())){
			//DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogadorTimeout.toString())){
			//DesconectarJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogoCriado.toString())){
			//AtualizarListaJogos(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.NovoJogadorConectado.toString())){
			//AtualizarListaJogadores(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.OponenteEntrou.toString())){
			//LiberarTelaDeJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.IniciarJogo.toString())){
			//DesbilitarJogoParaNovasConexoes(lstTokens, ipEnviou);			
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.BarcosPosicionados.toString())){
			//CarregarBarcosDoJogadorNoJogo(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.AtacarOponente.toString())){
			//ProcessarAtaque(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.RespostaAtaque.toString())){
			//É uma mensagem que apenas o jogador receberá como feedback do ataque que realizou
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberAtaque.toString())){
			//É uma mensagem que apenas o jogador receberá quando o seu barco for atacado
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.EnviarMensagemOponente.toString())){
			//EnviarMensagemSocktParaAdversario(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberMensagemOponente.toString())){
			//É uma mensagem que apenas o jogador receberá quando o oponente enviar uma mensagem
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.GanhouJogo.toString())){
			//DefinirJogadorComoVencedor(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.PerdeuJogo.toString())){
			//DefinirJogadorComoPerdedor(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.JogarComBot.toString())){
			//AtivarBotComoOponenteParaJogador(lstTokens, ipEnviou);
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberListaJogadores.toString())){
			//Para Cliente receber e popular a lista de jogadores na tela dele
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberListaJogos.toString())){
			//Para o cliente receber e popular al ista de jogos na tela dele
		}
		else if(header.equalsIgnoreCase(Comunicacao.TipoMensagem.ReceberTabuleiroOponente.toString())){
			//mensagem que não será utilizada, pois o tabuleiro não será enviado para o cliente mais
		}
	}

	private void DefinirJogador(List<String> lstTokens, String ipEnviou) {
		// TODO Auto-generated method stub
		
	}

	public void conectar(String login, String senha) {
		perfil.conexaoJogador.conectarJogador();
	}

}

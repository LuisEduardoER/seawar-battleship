import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Servidor.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//

public class Servidor {
	public List<Jogo> aListaJogos;
	public List<Jogador> aListaJogadorOnline;
	public List<Jogador> aListaJogadorJogando;

	public void getListaJogos() {

	}

	public void setListaJogos(List<Jogo> listaJogos) {
		aListaJogos = listaJogos;
	}

	public List<Jogo> getListaJogadorOnline() {
		return aListaJogos;
	}

	public void setListaJogadorOnline(List<Jogador> listaJogadorOnlne) {

	}

	public void getListaJogadorJogando() {

	}

	public void setListaJogadorJogando(List<Jogador> listaJogadorJogando) {

	}

	public void adicionarJogo(Jogo objJogo) {

	}

	public void removerJogo() {

	}

	public void registrarLog(String log) {
		try {
			Scanner scan = new Scanner(new File("Log.txt"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// M�todo deve ser executado em um intervalo determinado
	// para que o jogador envie um socket para atualizar o tempo dele online
	public void verificarJogador(Jogador objJogador) {

		if (!objJogador.isOnline()) // Quando n�o renovar o tempo, ele n�o deve
									// mais estar jogando
		{
			derrubarJogador(objJogador);
		}
	}

	public void derrubarJogador(Jogador objJogador) {

	}
}

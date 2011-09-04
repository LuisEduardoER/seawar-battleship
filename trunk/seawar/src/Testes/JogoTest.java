package Testes;

import java.io.ObjectInputStream.GetField;

import exceptions.FullGameException;
import modelos.Bot;
import modelos.Jogador;
import modelos.Jogo;
import modelos.Tabuleiro;
import modelos.TipoCelula;

public class JogoTest {

	/**
	 * @param args
	 * @throws FullGameException 
	 */
	public static void main(String[] args) throws FullGameException {
		// TODO Auto-generated method stub
		Jogo jogo = new Jogo(10, 2);
		Jogador p1 = new Jogador();
		Jogador p2 = new Jogador();
		p1.setTabuleiroDefesa(new Tabuleiro(10));
		p2.setTabuleiroDefesa(new Tabuleiro(10));
		jogo.AdicionarJogador(p1);
		jogo.AdicionarJogador(p2);
		p2.setIsBot(true);
		
		p1.setTabuleiroAtaque(p2.getTabuleiroDefesa());
		p2.setTabuleiroAtaque(p1.getTabuleiroDefesa());
		p1.getTabuleiroAtaque().pintarTabuleiro();
		p1.getTabuleiroDefesa().pintarTabuleiro();
		p2.getTabuleiroDefesa().pintarTabuleiro();
		p2.getTabuleiroAtaque().pintarTabuleiro();
		if(p2.isBot()){
			Bot bot = new Bot(p2);
			System.out.println("=================BOT em ação!!!=================");
			bot.getTabuleiroAtaque().pintarTabuleiro();
			bot.atacar();
			bot.getTabuleiroAtaque().pintarTabuleiro();
		}
		else{
			p1.Atacar(3, 4);
			p1.getTabuleiroAtaque().mMatrizCelula[3][4].setTipoCelula(TipoCelula.Agua);
			p1.getTabuleiroAtaque().mMatrizCelula[3][5].setTipoCelula(TipoCelula.Agua);
			p1.getTabuleiroAtaque().pintarTabuleiro();
		}
	}

}

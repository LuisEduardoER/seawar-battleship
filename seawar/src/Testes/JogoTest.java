package Testes;

import java.io.ObjectInputStream.GetField;

import javax.swing.Timer;

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
		p1.setTabuleiroDefesa(new Tabuleiro(10,true));
		p2.setTabuleiroDefesa(new Tabuleiro(10, true));
		jogo.AdicionarJogador(p1);
		jogo.AdicionarJogador(p2);
		p2.setIsBot(true);
		
		p1.setTabuleiroAtaque(p2.getTabuleiroDefesa());
		p1.getTabuleiroAtaque().setMatrizCelula(p2.getTabuleiroDefesa().getMatrizCelula());
		p2.setTabuleiroAtaque(p1.getTabuleiroDefesa());
		p2.getTabuleiroAtaque().setMatrizCelula(p1.getTabuleiroDefesa().getMatrizCelula());
		//p1.getTabuleiroAtaque().pintarTabuleiro();
		//p1.getTabuleiroDefesa().pintarTabuleiro();
		//p2.getTabuleiroDefesa().pintarTabuleiro();
		//p2.getTabuleiroAtaque().pintarTabuleiro();
		if(p2.isBot()){
			Bot bot = new Bot(p2);
			System.out.println("=================BOT em ação!!!=================");
			bot.getTabuleiroAtaque().pintarTabuleiro();
			do{
				bot.atacar();
				bot.getTabuleiroAtaque().pintarTabuleiro();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			while(!bot.afundouEmbarcacao());
		}
		else{
			p1.Atacar(3, 4);
			p1.getTabuleiroAtaque().mMatrizCelula[3][4].setTipoCelula(TipoCelula.Agua);
			p1.getTabuleiroAtaque().mMatrizCelula[3][5].setTipoCelula(TipoCelula.Agua);
			p1.getTabuleiroAtaque().pintarTabuleiro();
		}
	}

}

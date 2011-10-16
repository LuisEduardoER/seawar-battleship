package Testes;

import modelos.Bot;
import modelos.Embarcacao;
import modelos.Jogador;
import modelos.Jogo;
import modelos.Tabuleiro;
import exceptions.FullGameException;

public class JogoTest {

	/**
	 * @param args
	 * @throws FullGameException 
	 */
	public static void main(String[] args) throws FullGameException {		
		// TODO Auto-generated method stub
		int jogos = 0;
		while(jogos < 1000){
			Jogo jogo = new Jogo(10, 2);
			Jogador p1 = new Jogador();
			Jogador p2 = new Jogador();
			p1.setTabuleiroDefesa(new Tabuleiro(10,true));
			p2.setTabuleiroDefesa(new Tabuleiro(10,true));
			
			jogo.AdicionarJogador(p1);
			jogo.AdicionarJogador(p2);
			p2.setIsBot(true);
			
			p1.setTabuleiroAtaque(p2.getTabuleiroDefesa());
			p1.getTabuleiroAtaque().setMatrizCelula(p2.getTabuleiroDefesa().getMatrizCelula());
			p2.setTabuleiroAtaque(p1.getTabuleiroDefesa());
			p2.getTabuleiroAtaque().setMatrizCelula(p1.getTabuleiroDefesa().getMatrizCelula());
			
			if(p2.isBot()){
				Bot bot = new Bot(p2,null);
				System.out.println("=================BOT em ação!!!=================");
				bot.getTabuleiroAtaque().pintarTabuleiro();
					do{
						bot.atacar();
						bot.getTabuleiroAtaque().pintarTabuleiro();
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					while(!p1.getTabuleiroDefesa().isTodosBarcosAfundados());
					System.out.println("O BOT ganhou!!!!JOGO ENCERRADO.");
			}
			jogos++;
		}
		System.out.println(jogos + " testes realizados pelo BOT");
	}

}

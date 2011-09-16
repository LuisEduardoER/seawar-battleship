package guiComponentes;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import modelos.Celula;
import Comunicacao.Constantes;

public class GridBotoes {
	
	BotaoCelula[][] grid;
	
	public GridBotoes(int largura,int altura){
		grid = new BotaoCelula[largura][altura];
	}
	
	public void setBotao(BotaoCelula botao, int x, int y) throws IndexOutOfBoundsException {
		this.grid[x][y] = botao;
	}
	
	public BotaoCelula getBotao(int x, int y) throws IndexOutOfBoundsException {
		return this.grid[x][y];
	}
	
	public int getLargura(){
		return grid.length;
	}
	public int getAltura(){
		return grid[0].length;
	}

	public void repintarTodos() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				Celula celula = grid[i][j].celula;
				grid[i][j].setBackground(getCorBotao(celula));
				grid[i][j].repaint();
			}
		}		
	}
	//retorna uma cor baseada no tipo da celula
	public static Color getCorBotao(Celula celula) {
		Color cor = Constantes.CorAgua;
		switch(celula.getTipoCelula()){
			case Embarcacao:
				if(celula.isAtirada())
					cor = Constantes.CorBarcoAcertado;
				else if (celula.isSelecionada())
					cor = Constantes.CorBarcoSelecionado;
				else
					cor = Constantes.CorBarco ;
				break;
			default:
				if(celula.isAtirada())
					cor = Constantes.CorAguaAcertada;				
				else
					cor = Constantes.CorAgua;
				break;
		}
		return cor;
	}
}

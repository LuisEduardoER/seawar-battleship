package guiComponentes;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

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
}

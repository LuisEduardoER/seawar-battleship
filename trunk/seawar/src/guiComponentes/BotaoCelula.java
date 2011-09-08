package guiComponentes;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

//Classe que extende as funcionalidades do JButton para armazenar as coordenadas da célula que ele guarda
public class BotaoCelula extends JButton{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int coordX;
	int coordY;
		
	public BotaoCelula(int x, int y){
		super();
		this.coordX = x;
		this.coordY = y;
	}	
	public BotaoCelula(String texto,int x, int y){
		super(texto);
		this.coordX = x;
		this.coordY = y;
	}	
	public BotaoCelula(Action a,int x, int y){
		super(a);
		this.coordX = x;
		this.coordY = y;
	}
	public BotaoCelula(Icon icon,int x, int y){
		super(icon);
		this.coordX = x;
		this.coordY = y;
	}
	public BotaoCelula(String texto,Icon icon,int x, int y){
		super(texto,icon);
		this.coordX = x;
		this.coordY = y;
	}
	
	public int getCoordX(){
		return coordX;
	}
	
	public int getCoordY(){
		return coordY;
	}
}
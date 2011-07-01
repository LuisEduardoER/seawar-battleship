package modelos;
import java.awt.*;
//  @ Project : @Seawar
//  @ File Name : Imagens.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando


public class Imagens{
	public String sImagem;
	
			
	public void setImagem(String nomeImagem) {
	sImagem = nomeImagem;
	}
	
	public String getImagem() {
	return sImagem;
	}
	
	public void paint(Graphics g){
		//g.drawImage(sImagem, 0, 0, this);
		
	}
	
	
//Este método esta comentado para testar o metodo cor, mas se necessário pode ser desenvolvido futuramente
	//public void setLocalImagem(Objeto Graphics 2d) {
	
	//}
	
	public Color corBackground()
    {
   	 Color color = new Color(70,130,180);
   	 return color;
    }
}


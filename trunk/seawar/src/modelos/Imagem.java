package modelos;

import java.awt.*;
import javax.swing.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;

//  @ Project : @Seawar
//  @ File Name : Imagem.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando

public class Imagem {
	public String sImagem;

	public void setImagem(String nomeImagem) {
		sImagem = nomeImagem;
	}

	public Icon getImagem() {
		Icon imagem = new ImageIcon(getClass().getResource(
				"1imgParaTeste36x36.gif"));
		return imagem;
	}

	public Icon getImagem2() {
		Icon imagem = new ImageIcon(getClass().getResource(
				"1imgParaTeste32x32.jpg"));
		return imagem;
	}

	public void paint(Graphics g) {
		// g.drawImage(sImagem, 0, 0, this);

	}

	// Este método esta comentado para testar o metodo cor, mas se necessário
	// pode ser desenvolvido futuramente
	// public void setLocalImagem(Objeto Graphics 2d) {

	// }

	public Color corBackground() {
		Color color = new Color(70, 130, 180);
		return color;
	}
}

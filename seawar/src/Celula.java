import java.awt.Point;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Celula.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//




public class Celula extends Point {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3977809244373827326L;
	
	public TipoCelula aTipoCelula;
	
	public TipoCelula getTipoCelula() {
		return this.aTipoCelula;
	}
	
	public void setTipoCelula(TipoCelula tipoCelula) {
		aTipoCelula = tipoCelula;
	}
	
	public void carregarImagem(TipoCelula tipoCelula) {
		//TODO: Rever esta implementa��o, verificar na doc.
	}
}
package modelos;
import java.awt.Point;
import java.io.Serializable;

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




public class Celula extends Point implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean acertada;
	
	public Celula(){
		aTipoCelula = TipoCelula.AreaLivre;
		acertada = false;
	}
	
	public TipoCelula aTipoCelula;
	
	public TipoCelula getTipoCelula() {
		return this.aTipoCelula;
	}
	
	public void setTipoCelula(TipoCelula tipoCelula) {
		aTipoCelula = tipoCelula;
	}
	public void acertar(){
		acertada = true;
	}
	public boolean isAtirada(){
		return acertada;
	}
	public void carregarImagem(TipoCelula tipoCelula) {
		//TODO: Rever esta implementa��o, verificar na doc.
	}
}
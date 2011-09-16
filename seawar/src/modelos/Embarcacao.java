package modelos;

import java.io.Serializable;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Embarcacao.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//




public class Embarcacao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	private Celula[] aListaCelulas;
	private String sNomeEmbarcacao;
	private int iValorEmbarcacao;
	private int tamanho;
	private boolean bNaufragado;
	private boolean bVertical;
	
	public Embarcacao(int tamanho) {
		bVertical = false;
		this.tamanho = tamanho;
		aListaCelulas = new Celula[tamanho];
		gerarCelulas();
	}
	public Embarcacao(int tamanho, boolean vertical){
		bVertical = vertical;
		gerarCelulas();
	}
	public Celula[] getListaCelulas() {
		return aListaCelulas;
	}
	
	public void setListaCelulas(Celula[] celulas) {
		aListaCelulas = celulas;
	}
	
	public String getNomeEmbarcacao() {
		return sNomeEmbarcacao;
	}
	
	public void setNomeEmbarcacao(String nomeEmbarcacao) {
		sNomeEmbarcacao = nomeEmbarcacao;
	}
	
	public int getValorEmbarcacao() {
		return iValorEmbarcacao;
	}
	
	public void setValorEmbarcacao(int valorEmbarcacao) {
		iValorEmbarcacao = valorEmbarcacao * aListaCelulas.length;
	}
	
	public boolean getNaufragado() {
		
		for (Celula celulaBarco : this.aListaCelulas) {
			if(!celulaBarco.isAtirada())
				return false;
		}
		
		return true;
	}
	
//	public void setNaufragado(boolean naufragado) {
//		bNaufragado = naufragado;
//	}

	public int getTamanho() {
		return tamanho;
	}
	public boolean estaVertical() {
		return bVertical;
	}
	public Celula getCelulaAtacada(Celula celula) {
		return getCelula(celula.x, celula.y);
	}
	public Celula getCelula(int x, int y){
		for(int i = 0; i < aListaCelulas.length; i++){
			Celula celulaBarco = aListaCelulas[i];
			if(celulaBarco != null && x == celulaBarco.x && y == celulaBarco.y){
				return celulaBarco;
			}
		}
		return null;
	}
	public void setVertical(boolean makeVertical) {
		bVertical = makeVertical;
		ordenarCelulas();
	}

	private void ordenarCelulas() {
		//Pega a primeira celula da embarcacao para referencia
		Celula celulaAnterior = aListaCelulas[0];
		//Reordena as proximas de acordo
		for(int i = 1; i < aListaCelulas.length; i++){
			Celula celulaAtual = aListaCelulas[i];
			
			//Coloca a pr�xima c�lula na posi��o adequada
			if(bVertical){
				celulaAtual.setLocation(celulaAnterior.x, celulaAnterior.y+1);
			}
			else{
				celulaAtual.setLocation(celulaAnterior.x+1, celulaAnterior.y);
			}
			//Troca a celula de referencia para a proxima celula do loop
			celulaAnterior = celulaAtual;
		}	
		
		return;
	}
	
	//Gera as celulas da embarca��o
	private void gerarCelulas() {
		for(int i = 0; i < tamanho; i++){
			Celula celula = new Celula(0,i);
			celula.setTipoCelula(TipoCelula.Embarcacao);
			aListaCelulas[i] = celula; 
		}
		//Ap�s gerar, ordena-as
		ordenarCelulas();
	}
	public void setPosicao(int posx, int posy) {
		//Pega a primeira celula da embarcacao para referencia
		Celula celulaInicial = aListaCelulas[0];
		celulaInicial.setLocation(posx, posy);
		ordenarCelulas();
	}
	public void setSelecionado(boolean b) {
		for(int i = 0; i < aListaCelulas.length; i++){
			Celula celulaAtual = aListaCelulas[i];
			if(celulaAtual != null){
				celulaAtual.setSelecionada(b);
			}
		}				
	}
	public void rotacionar() {
		//rotaciona o barco
		this.setVertical(!bVertical);
	}
}

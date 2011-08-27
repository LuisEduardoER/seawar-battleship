//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Tabuleiro.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//

//Classe relacionada ao Tabuleiro do Jogo.
public class Tabuleiro {
	public Celula[][] mMatrizCelula;
	public Celula[][] mCelulasAtacadas;
	public boolean bTravaTabuleiro;
	public boolean bTurno;
	public Jogador oJogador;
	public Embarcacao[] arrEmbarcacoes;

	public Tabuleiro(int tamanho) {
		mMatrizCelula = new Celula[tamanho][tamanho];
		mCelulasAtacadas = new Celula[tamanho][tamanho];
	}

	public Celula[][] getMatrizCelula() {
		return mMatrizCelula;
	}

	public void setMatrizCelula(Celula[][] mMatrizCelula) {
		this.mMatrizCelula = mMatrizCelula;
	}

	public Celula[][] getCelulasAtacadas() {
		return mCelulasAtacadas;
	}

	public void setCelulasAtacadas(Celula[][] mCelulasAtacadas) {
		this.mCelulasAtacadas = mCelulasAtacadas;
	}

	public boolean isTravaTabuleiro() {
		return bTravaTabuleiro;
	}

	public void setTravaTabuleiro(boolean bTravaTabuleiro) {
		this.bTravaTabuleiro = bTravaTabuleiro;
	}

	public boolean isTurno() {
		return bTurno;
	}

	public void setTurno(boolean bTurno) {
		this.bTurno = bTurno;
	}

	public Jogador getoJogador() {
		return oJogador;
	}

	public void setoJogador(Jogador oJogador) {
		this.oJogador = oJogador;
	}

	public Embarcacao[] getArrEmbarcacoes() {
		return arrEmbarcacoes;
	}

	public void setArrEmbarcacoes(Embarcacao[] arrEmbarcacoes) {
		this.arrEmbarcacoes = arrEmbarcacoes;
	}

	public boolean seTabuleiroTravado(Jogador jogador) {
		return false;
	}

	public boolean seCelulaAtacada(Celula celula) {
		return false;
	}

	public Celula encontrarCelula(int x, int y) {
		Celula objCelula = mMatrizCelula[x][y];
		return objCelula;
	}
}

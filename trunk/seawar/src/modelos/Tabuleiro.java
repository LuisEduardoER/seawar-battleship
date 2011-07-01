package modelos;

import java.io.Console;
import java.util.Random;
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
	
	private final int QUANTIDADE_EMBARCACOES_POSSIVEIS = 7; 
	
	public Tabuleiro(int tamanho){
		mMatrizCelula = new Celula[tamanho][tamanho];
		mCelulasAtacadas = new Celula[tamanho][tamanho];
		PreencherTabuleiro(mMatrizCelula);
		arrEmbarcacoes = new Embarcacao[QUANTIDADE_EMBARCACOES_POSSIVEIS];
		criarEmbarcacoes();
	}
	private void PreencherTabuleiro(Celula[][] celulas) {
		for(int i = 0; i < celulas.length; i++){
			for(int j = 0; j < celulas[0].length; j++){
				celulas[i][j] = new Celula();
			}
		}
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
		return jogador.getTabuleiroAtaque().isTravaTabuleiro();
	}
	
	public boolean seCelulaAtacada(Celula celula) {
		Celula obj = mCelulasAtacadas[celula.x][celula.y];
		if(obj != null)
		{
			//Celula celulaDefesa = mMatrizCelula[celula.x][celula.y];
			//return celulaDefesa.aTipoCelula != TipoCelula.AreaLivre; 
			return obj.aTipoCelula != TipoCelula.AreaLivre;
		}
		return  false;
	}
	
	public Celula encontrarCelula(int x, int y) {
		Celula objCelula;
		try{
			objCelula = mMatrizCelula[x][y];
		}catch(ArrayIndexOutOfBoundsException ex){
			throw ex;
		}
		return objCelula;

	}
	
	public Celula atacar(int x, int y){
		Celula celAtacada = encontrarCelula(x,y);
		mCelulasAtacadas[x][y] = celAtacada;
		
		switch(celAtacada.getTipoCelula())
		{
		case Agua:
			celAtacada.aTipoCelula = TipoCelula.Agua;
			break;
		case AreaLivre:
			celAtacada.aTipoCelula = TipoCelula.Agua;
			break;
		case Embarcacao:
			acertarEmbarcacao(celAtacada);
			break;
		}
		 
		return celAtacada;
	}
	private void acertarEmbarcacao(Celula celula) {
		// TODO Implementar com o c�digo correto e refatorado
		for(int i = 0; i < arrEmbarcacoes.length; i++){
			Embarcacao barco = arrEmbarcacoes[i];
			Celula parteBarco = barco.getCelulaAtacada(celula);
			if(parteBarco != null){
				// TODO Fazer a l�gica para quando um barco � acertado
			}
		}
	}
	
	public void ajustarEmbarcacoes(){
		for(int i = 0; i < arrEmbarcacoes.length; i++){
			Embarcacao barco = arrEmbarcacoes[i];
			barco.setVertical(i % 2 == 0);
			if(barco != null){
				Celula[] celulasBarco = barco.getListaCelulas();
				for(int j = 0; j < celulasBarco.length; j++){
					Celula celula = celulasBarco[j];
					mMatrizCelula[celula.x][celula.y] = celula;
				}				
			}
		}
	}
	

	private void criarEmbarcacoes() {
		Random randNum = new Random();
		int posx = randNum.nextInt(this.mMatrizCelula.length);
		int posy = randNum.nextInt(this.mMatrizCelula[0].length);
		int maxTentativas = 10000;
		for(int i = 0; i < arrEmbarcacoes.length && maxTentativas > 0; i++){			
		
			Embarcacao barco = gerarEmbarcacao(posx, posy, i+1);
			
			if(ValidarPosicaoEmbarcacao(barco, posx, posy)){
				arrEmbarcacoes[i] = barco;
			}		
			else{
				//inverte a orientacao do barco
				barco.setVertical(!barco.estaVertical());
				//tenta validar de novo
				if(ValidarPosicaoEmbarcacao(barco, posx, posy)){
					arrEmbarcacoes[i] = barco;
				}
				else{
					//Se n�o der mesmo, repete o passo
					i--; //decrementa o i para repetir o passo
					maxTentativas--;
					System.out.println("Tentativas:" + maxTentativas + " | Barco: " + barco.getNomeEmbarcacao());
				}
			}
			if(maxTentativas == 0){
				//throw new Exception("Tentativas de alocar o barco esgotadas");
				// sai do loop se ocorrer este caso
				System.out.println("Limite de tentativas excedidas");
				break;
			}			
			posx = randNum.nextInt(this.mMatrizCelula.length);
			posy = randNum.nextInt(this.mMatrizCelula[0].length);
		}
		
		//Agora atribui cada c�lula da embarcacao nas celulas
		for(int i = 0; i < arrEmbarcacoes.length; i++){
			Embarcacao barco = arrEmbarcacoes[i];
			Celula[] celulasBarco = barco.getListaCelulas();
			for(int j = 0; j < celulasBarco.length; j++){
				int x = celulasBarco[j].x;
				int y = celulasBarco[j].y;
				mMatrizCelula[x][y] = celulasBarco[j];				
			}
		}
	}
	private boolean ValidarPosicaoEmbarcacao(Embarcacao barco, int x, int y) {

		try{
			Celula[] celulas = barco.getListaCelulas();
			//Seta a posi��o da frente do barco
			barco.setPosicao(x, y);
			//Verifica se as posi��es est�o v�lidas
			for(int i = 0; i < celulas.length; i++){
				Celula celulaBarco = celulas[i];
				Celula celulaTabuleiro = this.encontrarCelula(celulaBarco.x, celulaBarco.y);
				boolean celulaOcupada = this.isCelulaOcupadaComBarco(celulaBarco.x,celulaBarco.y);
				
				//Se a coordenada pertence ao tabuleiro e a c�lula est� ocupada por outro barco
				//ent�o a posi��o � inv�lida e retorna falso.
				if(celulaTabuleiro != null && celulaOcupada){
					return false;
				}
			}
		}
		catch(IndexOutOfBoundsException ex){
			//A embarca��o ficar� com alguma parte fora do tabuleiro
			//se cair aqui dentro
			//throw ex;
			return false;
		}
		//S� retorna TRUE se passar por toda a valida��o e n�o sair do m�todo com alguma posi��o falsa
		return true;
	}
	
	private boolean isCelulaOcupadaComBarco(int x, int y) {
		for(int i = 0; i < arrEmbarcacoes.length; i++){
			Embarcacao barco = arrEmbarcacoes[i];
			if(barco != null && barco.contemCelula(x,y)){
				return false;
			}
		}
		return false;
	}
	private Embarcacao gerarEmbarcacao(int posx, int posy, int tamanho) {
		Embarcacao obj = new Embarcacao(tamanho);
		obj.setNomeEmbarcacao("Barco " + tamanho);
		obj.setPosicao(posx, posy);
		return obj;
	}
}

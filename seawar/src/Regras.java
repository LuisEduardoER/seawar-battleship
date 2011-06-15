//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Regras.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//




public class Regras {
	public void validarPosicaoNavio(Embarcacao objEmbarcacao, Tabuleiro objTabuleiro) {
	
	}
	
	public boolean validarTiro(Celula objCelula, Tabuleiro objTabuleiro) {

		//TODO: Retratar a assinatura do m�todo abaixo, deve retornar apenas um array unitario (normal)
		Celula[][] celulas = objTabuleiro.getCelulasAtacadas();
		Celula celulaAtacada = celulas[objCelula.x][objCelula.y];
		
		//Retorna true se a c�lula for uma �rea livre, sen�o o tiro � inv�lido
		return (celulaAtacada.getTipoCelula() == TipoCelula.AreaLivre);
	}
	
	public boolean verificarVencedor(Tabuleiro objTabuleiro) {
		
		//Pressupoe-se que o jogador venceu
		boolean venceu = true;
		//Pega as embarcacoes do tabuleiro
		Embarcacao[] embarcacoes = objTabuleiro.getArrEmbarcacoes();
		
		//Verifica cada embarca��o no tabuleiro,
		//se todas estiverem naufragadas, 
		//o jogador que atirou por �ltimo � o vencedor
		for (int i = 0; i < embarcacoes.length; i++) {
			//Se a embarca��o n�o afundou, marca que o jogador n�o venceu
			if(!embarcacoes[i].bNaufragado){
				venceu = false;
			}
		}
		
		return venceu;
	}
}

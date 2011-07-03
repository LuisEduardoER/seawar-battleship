package Funcionalidades;

import modelos.*;

public class EmbarcacaoTestes {

	public static void main(String args[]){
		Tabuleiro tabDef;
		Tabuleiro tabAtaq;
		
		tabDef = new Tabuleiro(20);
		System.out.println("Tabuleiro defesa criado!");
		System.out.println("------------------------");
		System.out.println("Copie e veja no excel!");
		System.out.println("------------------------");
		Celula[][] celulasDef = tabDef.getMatrizCelula();
		for(int i = 0; i < celulasDef.length; i++){
			
			for(int j = 0; j < celulasDef[0].length; j++){
				System.out.printf("%s", celulasDef[i][j].aTipoCelula.name());				
				if(j != celulasDef[0].length-1)
					System.out.print("\t");
			}
			System.out.println();
		}
	}
	
}

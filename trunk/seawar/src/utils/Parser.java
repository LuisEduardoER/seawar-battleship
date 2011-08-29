package utils;

import java.util.List;

import modelos.Celula;
import modelos.TipoCelula;

public class Parser {

	public static Celula ConverteCelula(List<String> tokens){
		
		Celula celulaRetorno = null;
		try{
			celulaRetorno = new Celula();
			for(int i = 0; i < tokens.size(); i++){
				String token = tokens.get(i);
				//Recupera os parametros que virão no tipo -> chave:valor
				String[] aux = token.split(":");

				//Comparo a chave (que é o mesmo que o parametro que recebe na DicionarioMensagem)
				//e recupero só a parte de valor, assim converto para o que precisa e preencho o objeto
				if(aux[1].equalsIgnoreCase("x")){
					int xCel = Integer.parseInt(aux[1]);
					celulaRetorno.x = xCel;
				}
				else if (aux[1].equalsIgnoreCase("y")){
					int yCel = Integer.parseInt(aux[1]);
					celulaRetorno.y = yCel;
				}
				else if (aux[1].equalsIgnoreCase("tipocelula")){
					TipoCelula tipo = TipoCelula.valueOf(aux[1]);
					celulaRetorno.setTipoCelula(tipo);
				}				
			}					
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return celulaRetorno;
	}
	
}

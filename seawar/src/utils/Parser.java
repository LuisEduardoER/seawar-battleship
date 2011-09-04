package utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import encoder.Base64Coder;

import Comunicacao.Constantes;

import modelos.Celula;
import modelos.Tabuleiro;
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

	public static Tabuleiro ConverteTabuleiro(List<String> lstTokens) {
		Tabuleiro tab = null;
		String valorTokenTabuleiro = null;
		
		for(String token : lstTokens){
			String[] splitToken = valorTokenTabuleiro.split(Constantes.VALUE_SEPARATOR);
			if(splitToken[0].equalsIgnoreCase("tabuleiro")){
				valorTokenTabuleiro = splitToken[1];
				break;
			}
		}
		//valorTokenTabuleiro = lstTokens.get(lstTokens.size()-1); //O último objeto é o serializado SEMPRE (tá na classe MessageSender)
		if(valorTokenTabuleiro != null){
		InputStream inputStream = new ByteArrayInputStream(valorTokenTabuleiro.getBytes());
		try {
			ObjectInputStream objInput = new ObjectInputStream(inputStream);
			Object objetoVariavel = objInput.readObject();
			if(objetoVariavel != null){
				tab = (Tabuleiro)objetoVariavel;				
			}
		} catch (IOException e) {
			// TODO exception de IO invalido
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO exception de classe do objeto não encontrada
			e.printStackTrace();
		}
		
		}
		return tab;
	}
	
	/** Read the object from Base64 string. */
    public static Object StringParaObjeto( String s ) throws IOException ,
                                                        ClassNotFoundException {
        byte [] data = Base64Coder.decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String ObjetoParaString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }
}

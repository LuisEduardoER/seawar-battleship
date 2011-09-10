package utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Formatter;
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
			celulaRetorno = new Celula(0,0);
			for(int i = 0; i < tokens.size(); i++){
				String token = tokens.get(i);
				//Recupera os parametros que virão no tipo -> chave:valor
				String[] split = token.split(Constantes.VALUE_SEPARATOR);

				//Comparo a chave (que é o mesmo que o parametro que recebe na DicionarioMensagem)
				//e recupero só a parte de valor, assim converto para o que precisa e preencho o objeto
				if(split[0].equalsIgnoreCase("x")){
					int xCel = Integer.parseInt(split[1]);
					celulaRetorno.x = xCel;
				}
				else if (split[0].equalsIgnoreCase("y")){
					int yCel = Integer.parseInt(split[1]);
					celulaRetorno.y = yCel;
				}
				else if (split[0].equalsIgnoreCase("tipocelula")){
					TipoCelula tipo = TipoCelula.valueOf(split[1]);
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
    
//    public static String ObjetoParaString(Serializable obj) throws IOException{
//    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        	ObjectOutputStream oos = new ObjectOutputStream( baos );
//			oos.writeObject( obj );
//			oos.close();
//        
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
//        BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream));
//        
//        String retorno = buff.readLine();
//        while(buff.ready()){
//        	retorno += buff.readLine();
//        }
//        
//    	return retorno;
//    }
//    
//    public static Object StringParaObjeto(String mensagem) throws IOException{
//    	 ObjectInputStream ois = new ObjectInputStream( 
//                 new ByteArrayInputStream(  mensagem.getBytes() ) );
//		
//    	Object o = null;
//		try {
//			o = ois.readObject();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ois.close();
//		return o;
//    	
//    }
}

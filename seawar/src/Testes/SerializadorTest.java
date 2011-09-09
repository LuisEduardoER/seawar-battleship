package Testes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Formatter;

import utils.Parser;

import encoder.Base64Coder;

import modelos.Celula;
import modelos.Tabuleiro;

public class SerializadorTest {


	    public static void main( String [] args )  throws IOException,
	                                                      ClassNotFoundException {
	    	
	    	Tabuleiro tab = new Tabuleiro(20);
	        String string = Parser.ObjetoParaString( tab );
	        tab.pintarTabuleiro();
	        System.out.println(string);
	        Socket sock = new Socket(InetAddress.getLocalHost(), 2225);
	        Formatter format = new Formatter(sock.getOutputStream());
	        format.format("%s", string);
	        format.flush();
	        System.out.println(" Encoded serialized version " );
//	        Celula[][] celula = tab.getMatrizCelula();
//	        for (int i = 0; i < celula.length; i++) {
//				for (int j = 0; j < celula[0].length; j++) {
//					System.out.print("|");
//				}
//				System.out.println();
//			}
	        tab.pintarTabuleiro();
	        System.out.println( string );
	        Tabuleiro some = ( Tabuleiro ) fromString( string );
	        System.out.println( "\n\nReconstituted object");
	        System.out.println( some );
	        //some.pintarTabuleiro();


	    }

	    /** Read the object from Base64 string. */
	    private static Object fromString( String s ) throws IOException ,
	                                                        ClassNotFoundException {
	        byte [] data = Base64Coder.decode( s );
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(  data ) );
	        Object o  = ois.readObject();
	        ois.close();
	        return o;
	    }

	    /** Write the object to a Base64 string. */
	    private static String toString( Serializable o ) throws IOException {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream( baos );
	        oos.writeObject( o );
	        oos.close();
	        return new String( Base64Coder.encode( baos.toByteArray() ) );
	    }
	
}

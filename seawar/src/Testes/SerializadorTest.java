package Testes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import encoder.Base64Coder;

import modelos.Tabuleiro;

public class SerializadorTest {

	    public static void main( String [] args )  throws IOException,
	                                                      ClassNotFoundException {
	        String string = toString( new Tabuleiro(20) );
	        System.out.println(" Encoded serialized version " );
	        System.out.println( string );
	        Tabuleiro some = ( Tabuleiro ) fromString( string );
	        System.out.println( "\n\nReconstituted object");
	        System.out.println( some );


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

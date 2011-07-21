package modelos;


import java.awt.*;
import java.awt.*;
import javax.swing.*;
import java.applet.*;


public class Sons extends JApplet{
	public String sLocalSom;
	public boolean bRepetir;
	public AudioClip agua, explosao, tema, somAtivo;

	
	public AudioClip getLocalSom() {
		somAtivo = getAudioClip(getDocumentBase(),"agua.wav");
		return somAtivo;
	}
	
		
	
	public void setLocalSom(String localSom) {
	
	}
	
	public void getRepetir() {
	
	}
	
	public void setRepetir(boolean repetir) {
	
	}
	
	public void tocarSom() {
		//somAtivo = agua;
		somAtivo.play();
	}
}

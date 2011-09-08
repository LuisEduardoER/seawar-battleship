package Comunicacao;

import java.awt.Color;

public interface Constantes {
	public static final String MULTICAST_ADDRESS = "239.0.0.1";
	public static final int MULTICAST_SENDING_PORT = 4350;
	public static final int MULTICAST_LISTENING_PORT = 4339;

	public static final String SERVER_ADDRESS = "10.0.2.15";
	public static final int SERVER_PORT = 4351;

	public static final String TOKEN_SEPARATOR = "$";
	public static final String VALUE_SEPARATOR = ":";
	public static final String DISCONNECT_TOKEN = "$disconnect";
	public static final String CONNECT_TOKEN = "$connect";
	public static final String PING_TOKEN = "$ping";
	public static final int TAMANHO_TABULEIRO = 10;
	
	
	/*Constantes para cores de células*/
	public static final Color CorAgua = new Color(50,50,255); //um tom de azul mais claro que o azul puro
	public static final Color CorAguaAcertada = new Color(150,150,255); //um tom de azul mais claro que o azul sem acerto

	public static final Color CorBarco = new Color(50,50,50); //um tom de cinza
	public static final Color CorBarcoAcertado = new Color(255,100,20); //um tom de laranja

}

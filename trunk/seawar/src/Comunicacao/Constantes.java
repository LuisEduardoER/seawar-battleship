package Comunicacao;

public interface Constantes {
	public static final String MULTICAST_ADDRESS = "239.0.0.1";
	public static final int MULTICAST_SENDING_PORT = 4350;
	public static final int MULTICAST_LISTENING_PORT = 4339;

	public static final String SERVER_ADDRESS = "127.0.0.1";
	public static final int SERVER_PORT = 4351;

	public static final String TOKEN_SEPARATOR = "$";
	public static final String VALUE_SEPARATOR = ":";
	public static final String DISCONNECT_TOKEN = "$disconnect";
	public static final String CONNECT_TOKEN = "$connect";
	public static final String PING_TOKEN = "$ping";
	public static final int TAMANHO_TABULEIRO = 10;

}

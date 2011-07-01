package modelos;
import junit.framework.TestCase;


public class JogadorTest extends TestCase {
	public Jogador objJogador = new Jogador();

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public Jogador testIniciaJogador(){
		//Defino os atributos do obj Jogador para uso nos métodos de teste
		objJogador.setId_usuario(14);
		//Definindo um ip aleatório ex: 192.168.0.1
		objJogador.setIpJogador((int)((Math.random()*255)) + "." + (int)((Math.random()*255)) + "." + (int)((Math.random()*255)) + "." + (int)((Math.random()*255)));
		return objJogador;
	}
	public void testSetOnline(){
		objJogador = testIniciaJogador();
		assertEquals(true,objJogador.setOnline());
	}
	public void testIsOnline(){
		objJogador = testIniciaJogador();
		assertEquals(true,objJogador.isOnLine(14));
	}
	public void testIsJogando(){
		objJogador = testIniciaJogador();
		assertEquals(true,objJogador.isJogando());
	}	
	public void testSetOffline(){
		objJogador = testIniciaJogador();
		assertEquals(true,objJogador.setOffline());
	}

}

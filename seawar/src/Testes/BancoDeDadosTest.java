package Testes;
import modelos.BancoDeDados;

import java.sql.SQLException;

import junit.framework.TestCase;


public class BancoDeDadosTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("Iniciando teste com o bd!!!");
	}
	
	public void testConectarBanco() throws SQLException{
		BancoDeDados bd = new BancoDeDados();
		assertNotNull("Erro ao conectar banco!!",bd.conectarBanco());
	}
	
	public void testAbrirConexao() throws SQLException{
		BancoDeDados bd = new BancoDeDados();
		assertEquals(true,bd.abrirConexao());
	}
	
	public void testFecharConexao() throws SQLException{
		BancoDeDados bd = new BancoDeDados();
		bd.fecharConexaoBanco(bd.conectarBanco());
		System.out.println("Conexão fechada!");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("Concluindo teste com o bd!!!");
	}

}

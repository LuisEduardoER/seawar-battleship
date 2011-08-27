package Testes;

import java.sql.SQLException;

import dao.ConexaoBD;

import junit.framework.TestCase;

public class ConexaoBDTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		System.out.println("Iniciando teste com o bd!!!");
	}

	public void testConectarBanco() throws SQLException {
		ConexaoBD bd = new ConexaoBD();
		assertNotNull("Erro ao conectar banco!!", bd.conectarBanco());
	}

	public void testAbrirConexao() throws SQLException {
		ConexaoBD bd = new ConexaoBD();
		assertEquals(true, bd.abrirConexao());
	}

	public void testFecharConexao() throws SQLException {
		ConexaoBD bd = new ConexaoBD();
		bd.fecharConexaoBanco(bd.conectarBanco());
		System.out.println("Conexão fechada!");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("Concluindo teste com o bd!!!");
	}

}

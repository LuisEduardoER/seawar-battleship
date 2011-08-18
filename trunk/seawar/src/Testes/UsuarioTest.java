package Testes;
import java.util.UUID;

import modelos.Usuario;

import junit.framework.TestCase;


public class UsuarioTest extends TestCase {
	public Usuario objUsuarioTest = new Usuario();

	public Usuario getObjUsuario() {
		return this.objUsuarioTest;
	}

	public void setObjUsuario(Usuario objUsuarioTest) {
		this.objUsuarioTest = objUsuarioTest;
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCadastrarUsuario(){
		Usuario objUsuario = new Usuario();
		//Gero uma string randômica
		UUID uuid = UUID.randomUUID();  
		String myRandom = uuid.toString();
		objUsuario.setLogin(myRandom.substring(10,20));
		objUsuario.setSenha("123456");
		objUsuario.setEmail(myRandom.substring(0,10)+"@gmail.com");
		objUsuario.setNacionalidade("33");
		//Gero um numero randomico
		java.util.Random intRandomico = new java.util.Random();
		objUsuario.setPontuacao(intRandomico.nextInt(9999));
		objUsuario.setUsuarioAtivo(true);
		objUsuario.setDataCancelamento(null);
		java.util.Date dtUtil = new java.util.Date();
		java.sql.Date dataCadastro = new java.sql.Date(dtUtil.getTime());
		objUsuario.setDataCadastro(dataCadastro);

		assertEquals(true, objUsuario.cadastrarUsuario(objUsuario));
		setObjUsuario(objUsuario);
	}

	public void testAtualizarUsuario(){
		//Aqui está o valor 1 no getUsuario, pois busquei o id do primeiro usuário no bd
		Usuario objUsuario = new Usuario().getUsuario(1);
		UUID uuid = UUID.randomUUID();  
		String myRandom = uuid.toString();
		objUsuario.setLogin(myRandom.substring(10,20));
		objUsuario.setEmail(myRandom.substring(0,10)+"@stub.com.br");
		assertEquals(true, objUsuario.atualizarUsuario(objUsuario));
		setObjUsuario(objUsuario);
	}

	public void testExcluirUsuario(){
		Usuario objUsuario = new Usuario();
		//Aqui está o valor 13 no getUsuario, pois busquei um id qualquer de usuário no bd
		assertEquals(true, objUsuario.excluirUsuario(13));
	}
	
	public void testGetListaUsuarios(){
		int usersAtivos = objUsuarioTest.getUsuarios("STATUS_USUARIO = 1;").size();
		if (usersAtivos == 0){
			System.out.println("Não há usuários ativos");
		}
		else{
			System.out.println("Há "+ usersAtivos + " usuarios ativos!!!");
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

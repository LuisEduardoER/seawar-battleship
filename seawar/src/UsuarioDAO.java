import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UsuarioDAO {

	public static Usuario getUsuario(int id)
	{
		Usuario stub = new Usuario();
		
		stub.setDataCadastro(new Date());
		stub.setId_usuario(id);
		stub.setEmail("Stubing@stubbers.com");
		stub.setLogin("Stubs");
		stub.setSenha("123456");
		stub.setNacionalidade("Brasileiro");
		stub.setPontuacao(2915);
		stub.setUsuarioAtivo(true);
		
		return stub; 
	}
	
	public static boolean excluirUsuario(int id)
	{
		boolean excluido = false;
		
		//TODO: chamar a procedure/comando que exclui o cara do banco de dados
		
		return excluido; 
	}

	public static List<Usuario> getUsuarios(String clausula) {
		 List<Usuario> listaUsuarios = new  ArrayList<Usuario>();

		 // TODO Recuperar do banco de dados esses caras de acordo com a clausula where
		 
		 
		 //Stubs
		 listaUsuarios.add(getUsuario(1));
		 listaUsuarios.add(getUsuario(2));
		 listaUsuarios.add(getUsuario(3));
		 listaUsuarios.add(getUsuario(4));
		 listaUsuarios.add(getUsuario(5));
		 listaUsuarios.add(getUsuario(6));
		 //deletar após implementar
		 
		return listaUsuarios;
	}
}

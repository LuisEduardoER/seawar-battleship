import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO {

	public static Usuario getUsuario(int id)
	{
		Usuario objUsuario = new Usuario();
		BancoDeDados objBD = new BancoDeDados();
		if(objBD.abrirConexao())
		{
			try
			{
				//Foi utilizado a classe StringBuilder, que nesse caso realiza a mesma função do que uma
				//concatenação de strings
				StringBuilder strSQL = new StringBuilder("SELECT * FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE COD_ID_USUARIO = " + id);
				ResultSet objRS = objBD.getObjStatement().executeQuery(strSQL.toString());
				while(objRS.next()){
					objUsuario.setLogin(objRS.getString("LOGIN"));
					objUsuario.setSenha(objRS.getString("SENHA"));
					objUsuario.setEmail(objRS.getString("EMAIL"));
					objUsuario.setNacionalidade(objRS.getString("COD_NACIONALIDADE"));
					objUsuario.setDataCadastro(objRS.getDate("DT_CADASTRO"));
					objUsuario.setDataCancelamento(objRS.getDate("DT_CANCELAMENTO"));
					objUsuario.setPontuacao(objRS.getInt("PONTUACAO"));
					objUsuario.setUsuarioAtivo(objRS.getBoolean("STATUS_USUARIO"));
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
			catch (SQLException ex)
			{
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return objUsuario;

	}

	public static boolean cadastrarUsuario(Usuario objUsuario) {
		BancoDeDados objBD = new BancoDeDados();
		if(objBD.abrirConexao())
		{
			try
			{
				//Foi utilizado a classe StringBuilder, que nesse caso realiza a mesma função do que uma
				//concatenação de strings
				StringBuilder strSQL = new StringBuilder("INSERT INTO SEAWAR.USUARIO(LOGIN, SENHA, EMAIL, COD_NACIONALIDADE, DT_CADASTRO, DT_CANCELAMENTO, PONTUACAO, STATUS_USUARIO) VALUES (");
				strSQL.append("'"+objUsuario.getLogin() +"'," );
				strSQL.append("'"+objUsuario.getSenha() +"'," );
				strSQL.append("'"+objUsuario.getsEmail()+"'," );
				strSQL.append(objUsuario.getNacionalidade() +"," );
				strSQL.append("'"+objUsuario.getDataCadastro() +"'," );
				if (objUsuario.getDataCancelamento() != null){
					strSQL.append("'"+objUsuario.getDataCancelamento() +"'," );
				}
				else{
					strSQL.append("NULL," );
				}
				strSQL.append(objUsuario.getPontuacao() +"," );
				strSQL.append(objUsuario.isUsuarioAtivo());
				strSQL.append(");");

				if(objBD.getObjStatement().execute(strSQL.toString()))
				{
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
			catch (SQLException ex)
			{
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true;
	}

	public static boolean atualizarUsuario(Usuario objUsuario) {
		BancoDeDados objBD = new BancoDeDados();
		if(objBD.abrirConexao())
		{
			try
			{
				//Foi utilizado a classe StringBuilder, que nesse caso realiza a mesma função do que uma
				//concatenação de strings
				StringBuilder strSQL = new StringBuilder("UPDATE SEAWAR.USUARIO SET ");
				strSQL.append("LOGIN = '"+objUsuario.getLogin() +"'," );
				strSQL.append("SENHA = '"+objUsuario.getSenha() +"'," );
				strSQL.append("EMAIL = '"+objUsuario.getsEmail()+"'," );
				strSQL.append("COD_NACIONALIDADE = " +objUsuario.getNacionalidade() +"," );
				strSQL.append("DT_CADASTRO = '"+objUsuario.getDataCadastro() +"'," );
				if (objUsuario.getDataCancelamento() != null){
					strSQL.append("DT_CANCELAMENTO = '"+objUsuario.getDataCancelamento() +"'," );
				}
				strSQL.append("PONTUACAO = "+objUsuario.getPontuacao() +"," );
				strSQL.append("STATUS_USUARIO = " +objUsuario.isUsuarioAtivo());
				strSQL.append(" WHERE COD_ID_USUARIO = " +objUsuario.getId_usuario() +";");

				if(objBD.getObjStatement().execute(strSQL.toString()))
				{
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
			catch (SQLException ex)
			{
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true;
	}

	public static boolean excluirUsuario(int id)
	{
		BancoDeDados objBD = new BancoDeDados();
		if(objBD.abrirConexao())
		{
			try
			{
				//Foi utilizado a classe StringBuilder, que nesse caso realiza a mesma função do que uma
				//concatenação de strings
				StringBuilder strSQL = new StringBuilder("DELETE FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE COD_ID_USUARIO = " + id);

				if(objBD.getObjStatement().execute(strSQL.toString()))
				{
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
			catch (SQLException ex)
			{
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
				return false;
			}
		}
		return true; 
	}

	public static List<Usuario> getUsuarios(String clausula) {
		List<Usuario> listaUsuarios = new  ArrayList<Usuario>();
		BancoDeDados objBD = new BancoDeDados();
		if(objBD.abrirConexao())
		{
			try
			{
				//Foi utilizado a classe StringBuilder, que nesse caso realiza a mesma função do que uma
				//concatenação de strings
				StringBuilder strSQL = new StringBuilder("SELECT * FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE " + clausula);
				ResultSet objRS = objBD.getObjStatement().executeQuery(strSQL.toString());
				while(objRS.next()){
					Usuario objUsuario = new Usuario();
					objUsuario.setLogin(objRS.getString("LOGIN"));
					objUsuario.setSenha(objRS.getString("SENHA"));
					objUsuario.setEmail(objRS.getString("EMAIL"));
					objUsuario.setNacionalidade(objRS.getString("COD_NACIONALIDADE"));
					objUsuario.setDataCadastro(objRS.getDate("DT_CADASTRO"));
					objUsuario.setDataCancelamento(objRS.getDate("DT_CANCELAMENTO"));
					objUsuario.setPontuacao(objRS.getInt("PONTUACAO"));
					objUsuario.setUsuarioAtivo(objRS.getBoolean("STATUS_USUARIO"));
					listaUsuarios.add(objUsuario);
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
			catch (SQLException ex)
			{
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return listaUsuarios;
	}
}

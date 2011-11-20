package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import modelos.Usuario;

public class UsuarioDAO {

	public static Usuario getUsuario(int id) {
		Usuario objUsuario = new Usuario();
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"SELECT * FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE COD_ID_USUARIO = " + id);
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						strSQL.toString());
				while (objRS.next()) {
					objUsuario.setId_usuario(objRS.getInt("COD_ID_USUARIO"));
					objUsuario.setLogin(objRS.getString("LOGIN"));
					objUsuario.setSenha(objRS.getString("SENHA"));
					objUsuario.setEmail(objRS.getString("EMAIL"));
					objUsuario.setNacionalidade(objRS
							.getString("COD_NACIONALIDADE"));
					objUsuario.setDataCadastro(objRS.getDate("DT_CADASTRO"));
					objUsuario.setDataCancelamento(objRS
							.getDate("DT_CANCELAMENTO"));
					objUsuario.setPontuacao(objRS.getInt("PONTUACAO"));
					objUsuario.setUsuarioAtivo(objRS
							.getBoolean("STATUS_USUARIO"));
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
		return objUsuario;

	}

	public static boolean cadastrarUsuario(Usuario objUsuario) {
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"INSERT INTO SEAWAR.USUARIO(LOGIN, SENHA, EMAIL, COD_NACIONALIDADE, DT_CADASTRO, DT_CANCELAMENTO, PONTUACAO, STATUS_USUARIO) VALUES (");
				strSQL.append("'" + objUsuario.getLogin() + "',");
				strSQL.append("'" + objUsuario.getSenha() + "',");
				strSQL.append("'" + objUsuario.getsEmail() + "',");
				strSQL.append(objUsuario.getNacionalidade() + ",");
				strSQL.append("'" + objUsuario.getDataCadastro() + "',");
				if (objUsuario.getDataCancelamento() != null) {
					strSQL
							.append("'" + objUsuario.getDataCancelamento()
									+ "',");
				} else {
					strSQL.append("NULL,");
				}
				strSQL.append(objUsuario.getPontuacao() + ",");
				strSQL.append(objUsuario.isUsuarioAtivo());
				strSQL.append(");");

				if (objBD.getObjStatement().execute(strSQL.toString())) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
				return false;
			}
		}
		return true;
	}

	public static boolean atualizarUsuario(Usuario objUsuario) {
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"UPDATE SEAWAR.USUARIO SET ");
				// /strSQL.append("LOGIN = '"+objUsuario.getLogin() +"'," );
				strSQL.append("SENHA = '" + objUsuario.getSenha() + "',");
				strSQL.append("EMAIL = '" + objUsuario.getsEmail() + "',");
				strSQL.append("COD_NACIONALIDADE = "
						+ objUsuario.getNacionalidade() + ",");
				// strSQL.append("DT_CADASTRO = '"+objUsuario.getDataCadastro()
				// +"'," );
				if (objUsuario.getDataCancelamento() != null) {
					strSQL.append("DT_CANCELAMENTO = '"
							+ objUsuario.getDataCancelamento() + "',");
				}
				strSQL.append("PONTUACAO = " + objUsuario.getPontuacao() + ",");
				strSQL
						.append("STATUS_USUARIO = "
								+ objUsuario.isUsuarioAtivo());
				strSQL.append(" WHERE COD_ID_USUARIO = "
						+ objUsuario.getId_usuario() + ";");

				if (objBD.getObjStatement().execute(strSQL.toString())) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
				return false;
			}
		}
		return true;
	}

	public static boolean excluirUsuario(int id) {
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"DELETE FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE COD_ID_USUARIO = " + id);

				if (objBD.getObjStatement().execute(strSQL.toString())) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
				return false;
			}
		}
		return true;
	}

	public static List<Usuario> getUsuarios(String clausula) {
		List<Usuario> listaUsuarios = new ArrayList<Usuario>();
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"SELECT * FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE " + clausula);
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						strSQL.toString());
				while (objRS.next()) {
					Usuario objUsuario = new Usuario();
					objUsuario.setId_usuario(objRS.getInt("COD_ID_USUARIO"));
					objUsuario.setLogin(objRS.getString("LOGIN"));
					objUsuario.setSenha(objRS.getString("SENHA"));
					objUsuario.setEmail(objRS.getString("EMAIL"));
					objUsuario.setNacionalidade(objRS
							.getString("COD_NACIONALIDADE"));
					objUsuario.setDataCadastro(objRS.getDate("DT_CADASTRO"));
					objUsuario.setDataCancelamento(objRS
							.getDate("DT_CANCELAMENTO"));
					objUsuario.setPontuacao(objRS.getInt("PONTUACAO"));
					objUsuario.setUsuarioAtivo(objRS
							.getBoolean("STATUS_USUARIO"));
					listaUsuarios.add(objUsuario);
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}

		return listaUsuarios;
	}

	// Valida o login de um usuario durante uma o processo de login
	public static int validarUsuario(String strLogin, String strSenha) {
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {

			try {
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						"SELECT cod_id_usuario FROM USUARIO WHERE login='"
								+ strLogin + "' AND senha='" + strSenha
								+ "'  ;");

				if (objRS.next()) {
					return objRS.getInt("COD_ID_USUARIO");
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}

		}

		return 0;
	}

	// Verifica se o usuario ja existe
	public static boolean verificarUsuario(String strLogin, String strEmail) {
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {

			try {
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						"SELECT login, email FROM USUARIO WHERE login='"
								+ strLogin + "' or email='" + strEmail + "';");

				if (objRS.next()) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}

		}

		return false;
	}

	public static void atualizarPontuacao(Usuario objUsuario) {		
		
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"UPDATE SEAWAR.USUARIO SET ");
				strSQL.append("PONTUACAO = " + objUsuario.getPontuacao());
				strSQL.append(" WHERE COD_ID_USUARIO = "
						+ objUsuario.getId_usuario() + ";");

				objBD.getObjStatement().executeUpdate(strSQL.toString());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}
			finally{
				objBD.fecharConexaoBanco(objBD.getObjConn());
			}
		}
	}

}

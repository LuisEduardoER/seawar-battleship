package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import modelos.Jogador;
import modelos.Usuario;

public class JogadorDAO {

	public static boolean IsOnline(int IdUsuario) {
		// TODO Implementar para verificar no banco de dados
		boolean bIsOnline = false;
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"SELECT STATUS_USUARIO FROM SEAWAR.USUARIO");
				strSQL.append(" WHERE COD_ID_USUARIO = " + IdUsuario);
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						strSQL.toString());
				objRS.first();
				bIsOnline = objRS.getBoolean("STATUS_USUARIO");
				objRS.close();
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Jogador.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
		return bIsOnline;
	}

	public static boolean IsJogando(int IdUsuario) {
		// TODO Implementar para verificar no banco de dados
		// se tal jogador est� jogando :)
		boolean bIsJogando = false;
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"SELECT STATUS_JOGADOR FROM SEAWAR.JOGADOR");
				strSQL.append(" WHERE COD_ID_USUARIO = " + IdUsuario);
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						strSQL.toString());
				while (objRS.next()) {
					bIsJogando = objRS.getBoolean("STATUS_JOGADOR");
				}
				objRS.close();
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Jogador.class.getName()).log(Level.SEVERE,
						null, ex);
				bIsJogando = false;
			}
		}
		return bIsJogando;
	}

	public static boolean InserirJogador(Jogador jogador) {
		// TODO Implementar para inserir o jogador no banco de dados
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"INSERT INTO SEAWAR.JOGADOR(COD_ID_USUARIO, STATUS_JOGADOR, IP_JOGADOR) VALUES (");
				strSQL.append(jogador.getId_usuario() + ",");
				strSQL.append("1,");
				strSQL.append("'" + jogador.getIpJogador() + "'");
				strSQL.append(");");

				if (objBD.getObjStatement().execute(strSQL.toString())) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Jogador.class.getName()).log(Level.SEVERE,
						null, ex);
				return false;
			}
		}
		return true;
	}

	public static boolean RemoverJogador(Jogador jogador) {
		// TODO Implementar para remover o jogador no banco de dados
		// Esta a��o � semelhante a "desconectar jogador"
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"DELETE FROM SEAWAR.JOGADOR");
				strSQL.append(" WHERE COD_ID_USUARIO = "
						+ jogador.getId_usuario());

				if (objBD.getObjStatement().execute(strSQL.toString())) {
					return true;
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Jogador.class.getName()).log(Level.SEVERE,
						null, ex);
				return false;
			}
		}
		return true;
	}

	public static List<Jogador> getJogadoresOnline() {
		List<Jogador> listaJogadores = new ArrayList<Jogador>();
		ConexaoBD objBD = new ConexaoBD();
		if (objBD.abrirConexao()) {
			try {
				// Foi utilizado a classe StringBuilder, que nesse caso realiza
				// a mesma função do que uma
				// concatenação de strings
				StringBuilder strSQL = new StringBuilder(
						"SELECT cod_id_jogador, SEAWAR.JOGADOR.cod_id_usuario, status_jogador, ip_jogador, SEAWAR.USUARIO.login FROM SEAWAR.JOGADOR");
				strSQL
						.append(" INNER JOIN SEAWAR.USUARIO ON SEAWAR.USUARIO.cod_id_usuario = SEAWAR.JOGADOR.cod_id_usuario");
				strSQL
						.append(" WHERE status_jogador = 1 and status_usuario = 1 and ip_jogador is not null;");
				ResultSet objRS = objBD.getObjStatement().executeQuery(
						strSQL.toString());
				while (objRS.next()) {
					Jogador objJogador = new Jogador();
					// Verificar se precisa criar atributo para o ID_JOGADOR
					// (objRS.getInt("COD_ID_JOGADOR"));
					objJogador.setId_usuario(objRS.getInt("COD_ID_USUARIO"));
					objJogador.setUsuarioAtivo(objRS
							.getBoolean("STATUS_JOGADOR"));
					objJogador.setIpJogador(objRS.getString("IP_JOGADOR"));
					listaJogadores.add(objJogador);
				}
				objBD.fecharConexaoBanco(objBD.getObjConn());
			} catch (SQLException ex) {
				Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}

		return listaJogadores;
	}

}

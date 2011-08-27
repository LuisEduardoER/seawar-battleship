package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelos.Pais;
import modelos.Ranking;

public class RankingDAO {

	//Como esta classse não possui INSERT, DELETE ou UPDATE, só será feito o SELECT
	
	public static List<Ranking> consultarRanking(){
		
		List<Ranking> lstRanking = new ArrayList<Ranking>();
		ConexaoBD objBD = new ConexaoBD();
		if(objBD.abrirConexao())
		{
			try{
			ResultSet objRs = objBD.getObjStatement().executeQuery("SELECT CONCAT(@rownum := @rownum + 1,' º') AS POSICAO, r.login, r.pontuacao, r.cod_nacionalidade FROM seawar.ranking r, (SELECT @rownum := 0) p;");
			while (objRs.next())
			{
				//Se tiver registros, instancia um tipo e atribui seus valores de acordo
				//com o indice da coluna dele no sql
				Ranking objRanking = new Ranking();
				objRanking.setUsuario(objRs.getString("LOGIN"));
				objRanking.setPontuacao(objRs.getInt("PONTUACAO"));
				objRanking.setNacionalidade(Pais.getPais(objRs.getInt("COD_NACIONALIDADE")));
				
				//adiciona o tipo de mercadoria na lista de tipos de mercadorias
				lstRanking.add(objRanking);
			}
			
			objBD.fecharConexaoBanco(objBD.getObjConn());
			
			}catch(SQLException ex){
				
			}
		}
		
		return lstRanking;
	}

	public static Ranking consultarRankingUsuario(String strUsuario){

		Ranking objRanking = new Ranking();
		ConexaoBD objBD = new ConexaoBD();
		if(objBD.abrirConexao())
		{
			try{
			ResultSet objRs = objBD.getObjStatement().executeQuery("SELECT COD_PAIS, NOME_PAIS,COD_ISO FROM SEAWAR.PAIS WHERE COD_PAIS = "+strUsuario+";");
			if (objRs.next())
			{
				//Se tiver registros, instancia um tipo e atribui seus valores de acordo
				//com o indice da coluna dele no sql
				objRanking.setUsuario(objRs.getString("LOGIN"));
				objRanking.setPontuacao(objRs.getInt("PONTUACAO"));
				objRanking.setNacionalidade(Pais.getPais(objRs.getInt("COD_NACIONALIDADE")));
				
			}
			
			objBD.fecharConexaoBanco(objBD.getObjConn());
			}catch(SQLException ex){
				
			}
		}
		
		return objRanking;
	}
	
}

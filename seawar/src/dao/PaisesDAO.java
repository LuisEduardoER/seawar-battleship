package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelos.Pais;

public class PaisesDAO {

	//Como esta classse não possui INSERT, DELETE ou UPDATE, só será feito o SELECT
	
	public static List<Pais> consultarPaises(){
		
		List<Pais> lstPaises = new ArrayList<Pais>();
		ConexaoBD objBD = new ConexaoBD();
		if(objBD.abrirConexao())
		{
			try{
			ResultSet objRs = objBD.getObjStatement().executeQuery("SELECT COD_ID_PAIS, NOME_PAIS,COD_ISO FROM SEAWAR.PAIS;");
			while (objRs.next())
			{
				//Se tiver registros, instancia um tipo e atribui seus valores de acordo
				//com o indice da coluna dele no sql
				Pais objPais = new Pais();
				objPais.setPais(objRs.getInt("COD_ID_PAIS"));
				objPais.setNomePais(objRs.getString("NOME_PAIS"));
				objPais.setCodIso(objRs.getString("COD_ISO"));
				
				//adiciona o tipo de mercadoria na lista de tipos de mercadorias
				lstPaises.add(objPais);
			}
			
			objBD.fecharConexaoBanco(objBD.getObjConn());
			
			}catch(SQLException ex){
				
			}
		}
		
		return lstPaises;
	}

	public static Pais consultarPais(int codigo){

		Pais objPais = new Pais();
		ConexaoBD objBD = new ConexaoBD();
		if(objBD.abrirConexao())
		{
			try{
			ResultSet objRs = objBD.getObjStatement().executeQuery("SELECT COD_ID_PAIS, NOME_PAIS,COD_ISO FROM SEAWAR.PAIS WHERE COD_ID_PAIS = "+codigo+";");
			if (objRs.next())
			{
				//Se tiver registro, instancia um tipo e atribui seus valores de acordo
				objPais.setPais(objRs.getInt("COD_ID_PAIS"));
				objPais.setNomePais(objRs.getString("NOME_PAIS"));
				objPais.setCodIso(objRs.getString("COD_ISO"));
			}
			
			objBD.fecharConexaoBanco(objBD.getObjConn());
			}catch(SQLException ex){
				
			}
		}
		
		return objPais;
	}
	
}

package modelos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelos.Pais;
import dao.RankingDAO;




public class Ranking {
	public Usuario[] aListaUsuario;
	
	private String usuario;
	private int pontuacao;
	private Pais objPais;
	
    public void setUsuario(String strUsuario) {
    	this.usuario = strUsuario;
    }
    
  //Método Get (Retorna) Pais
    public String getUsuario() {
        return usuario;
    }
    
    public void setPontuacao(int pontuacao) {
    	this.pontuacao = pontuacao;
    }
    
  //Método Get (Retorna) Pais
    public int getPontuacao() {
        return pontuacao;
    }
    
    public void setNacionalidade(Pais objPais) {
    	this.objPais = objPais;
    }
    
  //Método Get (Retorna) Pais
    public Pais getNacionalidade() {
        return objPais;
    }
    
	public Usuario[] getListaUsuario() {
	 return aListaUsuario;
	}

	public void setListaUsuario(Usuario[] usuarios) {
		aListaUsuario = usuarios;
	}
	
    public static List<Ranking> getRanking() throws SQLException
    {
        //Instancia uma nova Lista de Categorias
    	List<Ranking> lstRanking = new ArrayList<Ranking>();
        lstRanking = RankingDAO.consultarRanking();
        return lstRanking;
    }
}

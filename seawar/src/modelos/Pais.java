/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modelos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PaisesDAO;

/**
 * 
 * @author SEAWAR
 */
public class Pais {

	private int cod_id_pais;
	private String nome_pais;
	private String cod_iso = "";

	public Pais() {
		// Instanciando Variáveis do Objeto Pais
		cod_id_pais = 0;
		nome_pais = "";
		cod_iso = "";
	}

	// Construtor de Objeto Pais Parametrizado
	public Pais(String nomePais) {
		nome_pais = nomePais;
	}

	// Método Get (Retorna) Pais
	public String getNomePais() {
		return nome_pais;
	}

	// Método Set (Altera) Pais
	public void setPais(int novoCodIdPais) {
		cod_id_pais = novoCodIdPais;
	}

	// Método Get (Retorna) Pais
	public int getCodIdPais() {
		return cod_id_pais;
	}

	// Método Set (Altera) Pais
	public void setNomePais(String novoNomePais) {
		nome_pais = novoNomePais;
	}

	// Método Get (Retorna) Pais
	public String getCodIso() {
		return cod_iso;
	}

	// Método Set (Altera) Pais
	public void setCodIso(String novoCodIso) {
		cod_iso = novoCodIso;
	}

	public static List<Pais> getPaises() throws SQLException {
		// Instancia uma nova Lista de Categorias
		List<Pais> lstPaises = new ArrayList<Pais>();
		lstPaises = PaisesDAO.consultarPaises();
		return lstPaises;
	}

	public static Pais getPais(int codigo) throws SQLException {
		return PaisesDAO.consultarPais(codigo);
	}
}

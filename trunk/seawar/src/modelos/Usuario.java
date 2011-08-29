package modelos;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : Usuario.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//


import java.io.Serializable;
import java.util.*;

import dao.UsuarioDAO;

public class Usuario implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String sLogin;
	public String sSenha;
	public int iId_usuario;
	public String sEmail;
	public String sNacionalidade;
	public int iPontuacao;
	public Date DataCadastro;
	public Date DataCancelamento;
	public boolean bStatusUsuario;

	public String getLogin() {
		return sLogin;
	}

	public void setLogin(String sLogin) {
		this.sLogin = sLogin;
	}

	public String getSenha() {
		return sSenha;
	}

	public void setSenha(String sSenha) {
		this.sSenha = sSenha;
	}

	public int getId_usuario() {
		return iId_usuario;
	}

	public void setId_usuario(int iIdUsuario) {
		iId_usuario = iIdUsuario;
	}

	public String getsEmail() {
		return sEmail;
	}

	public void setEmail(String sEmail) {
		this.sEmail = sEmail;
	}

	public String getNacionalidade() {
		return sNacionalidade;
	}

	public void setNacionalidade(String sNacionalidade) {
		this.sNacionalidade = sNacionalidade;
	}

	public int getPontuacao() {
		return iPontuacao;
	}

	public void setPontuacao(int iPontuacao) {
		this.iPontuacao = iPontuacao;
	}

	public Date getDataCadastro() {
		return DataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		DataCadastro = dataCadastro;
	}

	public Date getDataCancelamento() {
		return DataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		DataCancelamento = dataCancelamento;
	}

	public boolean isUsuarioAtivo() {
		//return bStatusUsuario;
		return true; //teste para chamada da classe Princpal
	}

	public void setUsuarioAtivo(boolean bStatusUsuario) {
		this.bStatusUsuario = bStatusUsuario;
	}

	public void logar(String login, String senha) {

	}

	public boolean cadastrarUsuario(Usuario objUsuario) {
		if (UsuarioDAO.cadastrarUsuario(objUsuario)){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean atualizarUsuario(Usuario objUsuario) {
		if(UsuarioDAO.atualizarUsuario(objUsuario)){
			return true;
		}
		else{
			return false;
		}
	}

	public boolean excluirUsuario(int id_usuario) {
		if(UsuarioDAO.excluirUsuario(id_usuario)){
			return true;
		}
		else{
			return false;
		}
	}

	public Usuario getUsuario(int id_usuario) {

		//TODO: Implementar o DAO
		Usuario user = UsuarioDAO.getUsuario(id_usuario);

		return user;
	}

	public List<Usuario> getUsuarios(String clausula) {

		//TODO: Implementar a logica do DAO
		List<Usuario> usuarios = UsuarioDAO.getUsuarios(clausula);

		return usuarios;
	}
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : @Seawar
//  @ File Name : BancoDeDados.java
//  @ Date : @06/05/2011
//  @ Author : @Fernando
//
//




public class BancoDeDados {
	private Connection objConn = null;
	private static final String sSqlDriver = "com.mysql.jdbc.Driver";
	private static final String sSqlPath = "jdbc:mysql://localhost/seawar";
	private static final String sUsuario = "root";
	private static final String sSenha = "root";
	
    public Connection conectarBanco() throws SQLException
    {
    	try
    	{
    		Class.forName(sSqlDriver);
    		objConn = (Connection) DriverManager.getConnection(sSqlPath, sUsuario, sSenha);
    	}
    	catch (ClassNotFoundException ex)
    	{
    			ex.printStackTrace();
    	}
    	catch (SQLException sqlEx)
    	{
    		sqlEx.printStackTrace();
    	}
    	return objConn;
    }

    public void fecharConexaoBanco(Connection objConn)
    {
    	try
    	{
    		objConn.close();
    	}
    	catch (SQLException sqlEx)
    	{
    		sqlEx.printStackTrace();
    	}
    }  

    public BancoDeDados(Connection objConn) {
        this.objConn = objConn;
        
    }
    
    //Construtor com declara��o default de conex�o de banco de dados.
    public BancoDeDados() {
        
    }

    /**
     * @return the objConn
     */
    public Connection getObjConn() {
        return objConn;
    }

    /**
     * @return the objStatement
     */
    public Statement getObjStatement() {
    	try{
    		return this.objConn.createStatement();
    	}
        catch(Exception ex)
        {
        	return null;
        }
    }

    /**
     * @param objStatement the objStatement to set
     */
    public void setObjStatement(Statement objStatement) {
    }


    public boolean abrirConexao()
    {
        try
        {
        	objConn = this.conectarBanco();
            this.setObjStatement(this.getObjStatement());
            return true;
        }
        catch(Exception ex)
        {
            //aqui podemos pensar em uma hip�tese de retorno da exce��o de erro
        }
        return false;
    }
}

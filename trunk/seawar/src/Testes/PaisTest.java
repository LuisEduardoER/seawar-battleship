package Testes;
import java.sql.SQLException;

import junit.framework.TestCase;
import modelos.Pais;


public class PaisTest extends TestCase {
	public Pais objPaisesTest = new Pais();

	public Pais getObjPaises() {
		return this.objPaisesTest;
	}

	public void setObjPaises(Pais objPaisesTest) {
		this.objPaisesTest = objPaisesTest;
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	
	public void testGetPaises() throws SQLException{
		int qtdPaises = Pais.getPaises().size();
		if (qtdPaises == 0){
			System.out.println("Não houve retorno na busca dos paises");
		}
		else{
			System.out.println("Foram retornados "+ qtdPaises);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

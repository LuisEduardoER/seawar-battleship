package Events;

import java.util.EventObject;

public class ServerEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TipoEventos tipo;
	
	public ServerEvent(Object source, TipoEventos tipo) {
        super(source);
        this.tipo = tipo;
    }

	public TipoEventos getTipo() {
		return tipo;
	}

}


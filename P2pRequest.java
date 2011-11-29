
import java.io.Serializable;

/**
 * Abstracción de un pedido P2p.
 */
public class P2pRequest implements Serializable {
    static final long serialVersionUID = 0;

    int hash_id;
    byte[] data;
	
    /**
     * Constructor por defecto.
     */
    public P2pRequest(){
	hash_id = 0;
        data = null;
    }
	
    /**
     * @param hi Identificador de pedido.
     * @param d  Datos.
     */
    public P2pRequest(int hi,byte[] d){
	hash_id = hi;
        data = d;
    }
}

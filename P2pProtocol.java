import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * Interface P2pProtocol con los métodos remotos a implementar.
 */
public interface P2pProtocol extends Remote {
    
    /**
     * Ejecuta un comando C.
     * @param req Parámetros del comando C.
     */
    String makeConsult(P2pRequest req) throws RemoteException;
    
    /**
     * Ejecuta un comando A.
     * @param req Parámetros del comando A.
     */
    String makeReachable(P2pRequest req) throws RemoteException;
    
    /**
     * Ejecuta un comando D del lado del servidor.
     * @param req Parámetros del comando D del lado del servidor.
     */
    byte[] getSong(P2pRequest req) throws RemoteException;
}

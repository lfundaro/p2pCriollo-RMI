import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lorenzo Fundaró <lorenzofundaro [at] yahoo.com>
 */
public interface P2pProtocol extends Remote {
    
    /**
     * Ejecuta un comando C.
     * @param req Parámetros del comando C.
     * @param cs Socket de comunicación.
     */
    String makeConsult(P2pRequest req) throws RemoteException;
    
    /**
     * Ejecuta un comando A.
     * @param req Parámetros del comando A.
     * @param cs Socket de comunicación.
     */
    String makeReachable(P2pRequest req) throws RemoteException;
    
    /**
     * Ejecuta un comando D del lado del servidor.
     * @param req Parámetros del comando D del lado del servidor.
     * @param cs Socket de comunicación.
     */
    byte[] getSong(P2pRequest req) throws RemoteException;
}

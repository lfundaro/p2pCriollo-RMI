import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manejador del protocolo p2p.
 */
public class P2pProtocolHandler implements P2pProtocol {
    private final int  NULL_HASHID       = 0xffffffff;
    // Estructuras de control
    private static HashMap<String,Song> SongDB;
    private static ArrayList<InetAddress> NodeDB;
    private static ConcurrentHashMap<Integer,String> ConsultDB;
    private static String host;
    private String id;
    private int app_port;
    
    /**
     * Constructor por defecto.
     */
    public P2pProtocolHandler() {
        SongDB = null;
        NodeDB = null;
        ConsultDB = null;
        host = null;
        this.app_port = 5947;
    }
    
    /**
     * Constructor.
     * @param knownNodesFilePath Localización de los nodos conocidos.
     * @param musicLib Localización de la librería.
     * @param id Identificador único del nodo.
     */
    public P2pProtocolHandler(String knownNodesFilePath, String musicLib,
            String id, int app_port){
        ConsultDB = new ConcurrentHashMap<Integer,String>();
        NodeDB = parseKnownNodesFile(knownNodesFilePath);
        SongDB = parseSongFile(musicLib);
	this.id = id;
        this.app_port = app_port;
	try{
	    host = InetAddress.getLocalHost().getHostAddress();
	}
	catch(UnknownHostException e){
	    System.out.println("Error recuperando la Ip del servidor: ");
	}
    }
    
     /**
     * 
     * Parsea la librería de música XSPF.
     * @param musicLib ruta a la biblioteca de música.
     * @return Un HashMap que mapea de "autor-título" a la estructura.
     * canción. La estructura canción contiene información adicional.
     */
    private HashMap<String,Song> parseSongFile(String musicLib){
        HashMap<String,Song> resp = ParseXSPF.parse(musicLib);
        return resp;
    }
    
    /**
     * Parsea el archivo de nodos conocidos.
     * @param knownNodesFilePath ruta al archivo con los nodos conocidos por 
     * este nodo.
     * @return lista enlazada de direcciones IP de los nodos conocidos por
     * este nodo.
     */
    private ArrayList<InetAddress> parseKnownNodesFile(String 
            knownNodesFilePath){
        ArrayList<InetAddress> Nodes = new ArrayList<InetAddress>();
        try {
            BufferedReader nodeFile = new BufferedReader(new
                    FileReader(knownNodesFilePath));
            String line;
            while ((line = nodeFile.readLine()) != null) {
                if (line.length() != 0)
                    Nodes.add(InetAddress.getByName(line));
            }
       
        }
        catch(FileNotFoundException fnf) {
            System.out.println("Error al abrir archivo: "+fnf);
        }
        catch(IOException e){
	    System.out.println("I/O Error: "+e);
	}
        return Nodes;
    }
    
    /**
     * Ejecuta un comando C.
     * @param req Parámetros del comando C.
     * @param cs Socket de comunicación.
     */
    @Override
    public String makeConsult(P2pRequest req) {
        // Crear comunicación con el cliente
        try {
            // Consulta repetida ?
            if (!ConsultDB.isEmpty() && ConsultDB.containsKey(req.hash_id)) {
                // No atiendo la consulta porque ya lo hice en el pasado
                return "";
            }
            else {
                String resultadoFinal = "";
                // Agregar hash de consulta a mi base de datos
                ConsultDB.put(req.hash_id, "");
                // Verificar tipo de consulta: Autor, Titulo o todas
                String tipoReq = new String(req.data);
                String[] st = tipoReq.split("@@");
                String expr = null;
                if (st.length > 1)
                    expr = st[1].toLowerCase();
                
                Pattern regex = Pattern.compile(expr);
                Matcher m;
                Collection<Song> s = SongDB.values();
                Iterator<Song> it = s.iterator();
                boolean match = false;
                Song sg = new Song();
                
                
                if (st[0].compareTo("W") == 0) {
                    // Todas las canciones de la red
                    resultadoFinal = SongDbToString(this.id);
                }
                else if (st[0].compareTo("T") == 0) {
                    // Por título
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.title);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("A") == 0) {
                    // Por autor
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.creator);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("B") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.bitRate);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("TL") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.trackLength);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("ABM") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.album);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("Y") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.year);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("C") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.composer);
                        if (m.find()) { // Hubo match
                            match = true;
                        }
                        m.reset();
                    }
                }
                // Verificar si hubo match
                if (match) {
                    resultadoFinal = resultadoFinal.concat
                            (sg.toString()+"@@"+
                            P2pProtocolHandler.host+"@@"+this.id+"##");
                }
                
                // Preparar estructura de respuestas
                String[] respuesta = new String[NodeDB.size()];
                // Hacer consulta a mis nodos vecinos.
                // Arreglo de threads
                ConsultThread[] ct = new ConsultThread[NodeDB.size()];
                // Crear cada uno de los threads y ejecutarlos.
                for(int i = 0; i < NodeDB.size(); i++) {
                    ct[i] = new ConsultThread(i, respuesta, 
                            NodeDB.get(i).getHostName(), req, "makeConsult");
                    ct[i].start();
                }
                // Espero que todos los threads terminen su ejecución
                for(int i = 0; i < NodeDB.size(); i++) {
                    ct[i].join();
                }
                // Colocar todos los resultados en un solo String
                for(int i = 0; i < NodeDB.size(); i++) {
                    resultadoFinal = resultadoFinal.concat(respuesta[i]);
                }
                // Mandar respuesta
                return resultadoFinal;
            }
        }
        catch(InterruptedException ie) {
            System.out.println("Interrupted exception: "+ie);
            return "";
        }
    }
    
    /**
     * Genera un string representativo de la base de datos de canciones.
     * @param nodeID Identificador único del nodo.
     * @return String representativo de la base de datos de canciones.
     */
    private String SongDbToString(String nodeID) {
        String resp = "";
        // Obtener todas las canciones de SongDB
        Collection<Song> s = SongDB.values();
        Iterator<Song> it = s.iterator();
        while (it.hasNext()) {
            Song se = it.next();
            resp = resp.concat(se.toString()+"@@"+
                    P2pProtocolHandler.host+"@@"+nodeID+"##");
        }
        return resp;
    }
    
    /**
     * Ejecuta un comando A.
     * @param req Parámetros del comando A.
     * @param cs Socket de comunicación.
     */
    @Override
    public String makeReachable(P2pRequest req) {
        // Mandar respuesta al cliente
        try {
            // Consulta repetida ?
            if (!ConsultDB.isEmpty() && ConsultDB.containsKey(req.hash_id)) {
                // No atiendo la consulta porque ya lo hice en el pasado
                return "";
            }
            else {
                // Agregar hash de consulta a mi base de datos
                ConsultDB.put(req.hash_id, "");
                // Agregar mi nombre
                String resp = "";
                resp = resp.concat(
                        (InetAddress.getByName(this.id)).getHostName()+"##");
                // Agregar nombre de nodos vecinos.
                for(int i = 0; i < NodeDB.size(); i++) {
                    resp = resp.concat(NodeDB.get(i).getHostName()+"##");
                }
                // Preparar estructura de respuestas
                String[] respuesta = new String[NodeDB.size()];
                // Hacer consulta a mis nodos vecinos.
                // Arreglo de threads
                ConsultThread[] ct = new ConsultThread[NodeDB.size()];
                // Crear cada uno de los threads y ejecutarlos.
                for(int i = 0; i < NodeDB.size(); i++) {
                    ct[i] = new ConsultThread(i, respuesta, 
                            NodeDB.get(i).getHostName(),req, "makeReachable");
                    ct[i].start();
                }
                // Espero que todos los threads terminen su ejecución
                for(int i = 0; i < NodeDB.  size(); i++) {
                    ct[i].join();
                }
                // Colocar todos los resultados en un solo String
                for(int i = 0; i < NodeDB.size(); i++) {
                    resp = resp.concat(respuesta[i]);
                }
                // Mandar respuesta
                return resp;
            }
        }
        catch(IOException e) {
            System.out.println("Error I/O: "+e);
            return "";
        }
        catch(InterruptedException ie) {
            System.out.println("Interrupted exception: "+ie);
            return "";
        }
    }
    
    /**
     * Ejecuta un comando D del lado del servidor.
     * @param req Parámetros del comando D del lado del servidor.
     * @param cs Socket de comunicación.
     */
    @Override
    public byte[] getSong(P2pRequest req) {
        // Nombre de archivo ?
        String nombreMP3 = new String(req.data);
        // Buscar en SongDB
        String rutaArchivo = SongDB.get(nombreMP3).location;
        // Cargar archivo
        try {
            File cancion = new File(rutaArchivo);
            FileInputStream fin = new FileInputStream(cancion);
            byte contenidoMP3[] = new byte[(int) cancion.length()];
            fin.read(contenidoMP3);
            // Mandar respuesta al cliente
            fin.close();
            return contenidoMP3;
        }
        catch(FileNotFoundException fnf) {
            System.out.println("Error: "+fnf);
            return null;
        } catch(NullPointerException nl) {
            System.out.println("Error: "+nl);
            return null;
        } catch(IOException e) {
            System.out.println("Error I/O: "+e);
            return null;
        }
    }
}
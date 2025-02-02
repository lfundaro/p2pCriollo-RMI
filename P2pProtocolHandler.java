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
     * @param app_port Puerto en donde se está ejecutando la aplicación.
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
     * @return Resultado de la consulta.
     */
    //@Override
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
                String expr = "";
                if (st.length > 1)
                    expr = st[1].toLowerCase();
                
                Pattern regex = Pattern.compile(expr);
                Matcher m;
                Collection<Song> s = SongDB.values();
                Iterator<Song> it = s.iterator();
                Song sg;
                
                
                if (st[0].compareTo("W") == 0) {
                    // Todas las canciones de la red
                    resultadoFinal = SongDbToString(this.id);
                }
                else if (st[0].compareTo("T") == 0) {
                    // Por título
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.title.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("A") == 0) {
                    // Por autor
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.creator.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("B") == 0) {
                    // Búsqueda por bitRate
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.bitRate.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("TL") == 0) {
                    // Búsqueda por trackLength
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.trackLength.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("ABM") == 0) {
                    // Búsqueda por album
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.album.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("Y") == 0) {
                    // Búsqueda por año
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.year.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                else if (st[0].compareTo("G") == 0) {
                    // Búsqueda por género
                    while (it.hasNext()) {
                        sg = it.next();
                        m = regex.matcher(sg.genre.toLowerCase());
                        if (m.find()) { // Hubo match
                            resultadoFinal = resultadoFinal.concat
                                    (sg.toString()+"@@"+
                                    P2pProtocolHandler.host+"@@"+this.id+"##");
                        }
                        m.reset();
                    }
                }
                
                // Preparar estructura de respuestas
                String[] respuesta = new String[NodeDB.size()];
                // Hacer consulta a mis nodos vecinos.
                // Arreglo de threads
                ConsultThread[] ct = new ConsultThread[NodeDB.size()];
                // Crear cada uno de los threads y ejecutarlos.
                for(int i = 0; i < NodeDB.size(); i++) {
                    ct[i] = new ConsultThread(i, respuesta,
                            NodeDB.get(i).getHostName(), req, "makeConsult",
                            app_port);
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
     * @return Lista de nodos alcanzables por este cliente/nodo.
     */
    //@Override
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
                resp = resp.concat(this.id+"##");
                // Agregar nombre de nodos vecinos.
                // Preparar estructura de respuestas
                String[] respuesta = new String[NodeDB.size()];
                // Hacer consulta a mis nodos vecinos.
                // Arreglo de threads
                ConsultThread[] ct = new ConsultThread[NodeDB.size()];
                // Crear cada uno de los threads y ejecutarlos.
                for(int i = 0; i < NodeDB.size(); i++) {
                    ct[i] = new ConsultThread(i, respuesta,
                            NodeDB.get(i).getHostName(),req, "makeReachable",
                            app_port);
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
        catch(InterruptedException ie) {
            System.out.println("Interrupted exception: "+ie);
            return "";
        }
    }
    
    /**
     * Ejecuta un comando D del lado del servidor.
     * @param req Parámetros del comando D del lado del servidor.
     * @return Cadena de bytes que representa la canción.
     */
    //@Override
    public byte[] getSong(P2pRequest req) {
        // Nombre de archivo ?
        String nombreMP3 = new String(req.data);
        // Buscar en SongDB
        Song sg = SongDB.get(nombreMP3);
        String rutaArchivo = sg.location;
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
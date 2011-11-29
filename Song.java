/**
 * Abstracción de canción.
 */
public class Song{
    /**
     *Identificador único del nodo que tiene la canción.
     */
    public String node_id;

    /**
     *Localización de la canción.
     */
    public String location;

    /**
     *Título de la canción.
     */
    public String title;

    /**
     *Creador de la canción.
     */
    public String creator;
    
    /**
     * Bitrate
     */
    public String bitRate;
    
    /**
     * Tracklength
     */
    public String trackLength;
    
    /**
     * Album
     */
    public String album;
    
    /**
     * Año
     */
    public String year;
    
    /**
     * Compositor
     */
    public String composer;
    
    /**
     * 
     * @param l Localización de la canción.
     * @param t Título de la canción.
     * @param c Creador de la canción.
     * @param n Identificador único del nodo que tiene la canción.
     * @param br BitRate
     * @param tl Tamaño de la pista.
     * @param abm Album.
     * @param y Año.
     * @param cmp Compositor.
     */
    public Song(String l, String t, String c, String n, String br, String tl,
            String abm, String y, String cmp){
	location = l;
	title = t.toLowerCase();
	creator = c.toLowerCase();
	node_id = n.toLowerCase();
        bitRate = br.toLowerCase();
        trackLength = tl.toLowerCase();
        album = abm.toLowerCase();
        year  = y.toLowerCase(); 
        composer = cmp.toLowerCase();
    }

    /**
     *Constructor por defecto.
     */
    public Song(){}
    
    /**
     * Retorna una representación en String de esta canción.
     * @return String representativo de la canción.
     */
    @Override
    public String toString() {
        String resp = creator+"@@"+title;
        return resp;
    }    
}
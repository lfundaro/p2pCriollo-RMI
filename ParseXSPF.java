import nanoxml.*;
import java.util.*;
import java.io.*;

/**
 * Parser XSPF
 */
public class ParseXSPF{
    // public static void main(String args[]){
    // 	parse(args[0]);
    // }

    /**
     * Parser de archivos XSPF.
     * @param filename Archivo XSPF que se debe parsear.
     * @return HashMap de la concatenación del título de la canción
     *         con el autor de la canción a objetos de tipo Song.
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String,Song> parse(String filename){
	HashMap<String,Song> sl = new HashMap<String,Song>();
	String library_filename = null;
	boolean directory_library = false;
	try{
	    File f = new File(filename);

	    if(f.isDirectory()){
		directory_library = true;
		library_filename = ParseMP3dir.parse(filename);
	    }
	    else{
		library_filename = filename;
	    }

	    XMLElement xspf = new XMLElement();
	    FileReader reader = new FileReader(library_filename);
	    xspf.parseFromReader(reader);
	    
	    if(xspf.getName().compareTo("playlist") == 0){
		//Busco el elemento trackList
		Enumeration<XMLElement> playlistContents = 
                        xspf.enumerateChildren();
		XMLElement trackList = null;
		do{
		    trackList = (XMLElement) playlistContents.nextElement();
		}while((trackList != null)&&
		       (trackList.getName().compareTo("trackList") != 0));

		if(trackList == null){
		    System.out.println("Error, lista de reproduccion mal"
                            + " formateada: "
                            + "No se encontro trackList");
		    System.exit(1);
		}

		//Itero sobre las canciones
		Enumeration<XMLElement> tracks = trackList.enumerateChildren();
		while(tracks.hasMoreElements()){
		    XMLElement track = (XMLElement)tracks.nextElement();
		    Song s = new Song();
		    Enumeration<XMLElement> attrs = track.enumerateChildren();
		    while(attrs.hasMoreElements()){
			XMLElement attr = (XMLElement)attrs.nextElement();
			get_xspf_attr(attr,s);
		    }
		    sl.put(s.title+"-"+s.creator,s);
		}
	    }
	    else{
		System.out.println("Error, lista de reproduccion mal "
                        + "formateada: "
                        + "No se encontró elemento playlist en el tope "
                        + "del árbol");
		System.exit(1);
	    }
	}
	catch(Exception e){}
	
	if(directory_library){
	    (new File(library_filename)).delete();
	}

	return sl;
    }

    /**
     * Escribe un atributo de una canción almacenado en el archivo
     * xspf en un objeto Song.
     * @param attr Atributo XSPF.
     * @param s Objeto Song.
     */
    public static void get_xspf_attr(XMLElement attr, Song s){
	String attr_name = attr.getName();
        
	if (attr_name.compareTo("location") == 0) {
	    s.location = attr.getContent(); 
        }
	else if (attr_name.compareTo("title") == 0){
	    s.title = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("creator") == 0){
	    s.creator = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("album") == 0){
	    s.album = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("duration") == 0){
	    s.trackLength = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("year") == 0){
	    s.year = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("composer") == 0){
	    s.composer = attr.getContent().toLowerCase();
	}
	else if (attr_name.compareTo("bitrate") == 0){
	    s.bitRate = attr.getContent().toLowerCase();
	}
    }
}
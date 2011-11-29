import java.util.*;
import java.io.*;
import nanoxml.*;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;

/**
   Parser de la librería Mp3
*/
public class ParseMP3dir{
    private static void printStuff(ID3v24Tag tag, MP3AudioHeader header){
	System.out.println("TITLE: "+tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
	System.out.println("ARTIST: "+tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
	System.out.println("ALBUM: "+tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
	System.out.println("YEAR: "+tag.getFirst(ID3v24Frames.FRAME_ID_YEAR));
	System.out.println("COMMENT: "+tag.getFirst(ID3v24Frames.FRAME_ID_COMMENT));
	System.out.println("COMPOSER: "+tag.getFirst(ID3v24Frames.FRAME_ID_COMPOSER));
	System.out.println("GENRE: "+tag.getFirst(ID3v24Frames.FRAME_ID_GENRE));
	System.out.println("LANGUAGE: "+tag.getFirst(ID3v24Frames.FRAME_ID_LANGUAGE));
	System.out.println("MOOD: "+tag.getFirst(ID3v24Frames.FRAME_ID_MOOD));
	System.out.println("LENGTH: "+header.getTrackLengthAsString());
	System.out.println("BITRATE: "+header.getBitRate());
	System.out.println();
    }

    private static void addSong(File f, ID3v24Tag tag, MP3AudioHeader header,
			       XMLElement trackList){

	XMLElement song = new XMLElement();
	song.setName("track");

	XMLElement attr = null;

	attr = genAttr("location");
	attr.setContent(f.getAbsolutePath());
	song.addChild(attr);

	attr = genAttr("title");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_TITLE));
	song.addChild(attr);
	
    	attr = genAttr("creator");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST));
	song.addChild(attr);

    	attr = genAttr("album");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM));
	song.addChild(attr);

    	attr = genAttr("duration");
	attr.setContent(header.getTrackLengthAsString());
	song.addChild(attr);

	/*********** Not in the XSPF definition ***********/
    	attr = genAttr("year");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_YEAR));
	song.addChild(attr);

    	attr = genAttr("composer");
	attr.setContent(tag.getFirst(ID3v24Frames.FRAME_ID_COMPOSER));
	song.addChild(attr);

    	attr = genAttr("bitrate");
	attr.setContent(header.getBitRate());
	song.addChild(attr);

	trackList.addChild(song);
    }

    private static XMLElement genAttr(String name){
	XMLElement attr = new XMLElement();
	attr.setName(name);
	return attr;
    }

    public static String parse(String lib){
	File xspf = null;
	try{
	    // File bla = new File("libreRia/one - nnetallica.mp3");
	    // MP3File mp3bla = (MP3File)AudioFileIO.read(bla);
	    // ID3v24Tag tagbla = mp3bla.getID3v2TagAsv24();
	    // printStuff(tagbla);

	    XMLElement playlist  = new XMLElement();
	    playlist.setName("playlist");

	    XMLElement trackList = new XMLElement();
	    trackList.setName("trackList");

	    playlist.addChild(trackList);

	    LinkedList<File> dirs = new LinkedList<File>();
	    //Añadimos el directorio inicial
	    File initial_dir = new File(lib);
	    if(!initial_dir.isDirectory())
		return "";
	    dirs.add(initial_dir);

	    while(dirs.size() > 0){
		File d = dirs.remove();
		File[] fls = d.listFiles();
		for(File f: fls){
		    if(f.isDirectory()){
			dirs.add(f);
		    }
		    else{
			//System.out.println("TARA");
			MP3File mp3 = null;
			try{
			    mp3 = (MP3File)AudioFileIO.read(f);
			}
			catch(Exception e){}
			if((mp3 != null)&&(mp3.hasID3v2Tag())){
			    ID3v24Tag tag = mp3.getID3v2TagAsv24();
			    MP3AudioHeader header = mp3.getMP3AudioHeader();
			    
			    printStuff(tag,header);
			    addSong(f,tag,header,trackList);
			}
			else{
			    System.out.println("El archivo "+f+" no tiene un tag ID3v2\n");
			}
		    }
		}
	    }

	    xspf = File.createTempFile("libreria",".xspf",initial_dir);
	    FileWriter xspfWriter = new FileWriter(xspf);
	    xspfWriter.write(playlist.toString());
	    xspfWriter.flush();
	    xspfWriter.close();
	}
	catch(Exception e){
	    System.out.println(e);
	}

	return xspf.getAbsolutePath();
    }
    
    // public static void main(String args[]){
    // 	System.out.println(parse(args[0]));
    // }
    
}
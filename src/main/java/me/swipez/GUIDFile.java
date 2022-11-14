package me.swipez;

import java.io.File;

public class GUIDFile {

    File metaFile;
    public int index;
    public String guid;
    public boolean wasConverted = false;

    public GUIDFile(int index, String guid) {
        this.index = index;
        this.guid = guid;
        this.metaFile = null;
    }

    public void setFile(File file){
        this.metaFile = file;
    }
}

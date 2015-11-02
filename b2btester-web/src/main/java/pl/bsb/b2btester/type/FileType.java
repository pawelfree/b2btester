package pl.bsb.b2btester.type;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author swarczak
 */
public enum FileType {

    CER("cer"),
    CRT("crt"),
    PFX("pfx"),
    CRL("crl"),
    ;

    String extension;
    
    private FileType(String extension){
        this.extension = extension;
    }
    
    public String getExtension(){
        return extension;
    }

    public String getExtensionPattern(){
    	return getExtensionsPattern(this);
    }
    
    public static String getExtensionsPattern(FileType... types) {
        return getExtensionsPattern(Arrays.asList(types));
    }
    
    public static String getExtensionsPattern(List<FileType> types) {
        if(types == null || types.isEmpty()) {
            return "/(\\.|\\/)([\\w\\d]+)$/";
        } 
        StringBuilder builder = new StringBuilder("/(\\.|\\/)(");
        for (int i = 0; i < types.size(); i++) {
            FileType type = types.get(i);
            builder.append(type.getExtension().toLowerCase());
            if(i < types.size() - 1) {
                builder.append("|");
            }
        }
        builder.append(")$/");
        return builder.toString();
    }
}

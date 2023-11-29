package edu.lehigh.cse216.knights.backend;

public class FileObject {
    /**
     * The username of the poster
     */
    private String mFileName;
    /**
     * The comments associated with this idea
     */
    private String mFileType;

    private String mBase64;

    /**
     * Constructor with the id, content, likeCount, userid, posterUsername, and
     * comments specified.
     * 
     * @param fileName
     */
    public FileObject(String fileName, String fileType, String base64) {
        this.mFileName = fileName;
        this.mFileType = fileType;
        this.mBase64 = base64;
    }

    public String getmFileName() {
        return this.mFileName;
    }

    public String getmFileType() {
        return this.mFileType;
    }

    public String getmBase64() {
        return this.mBase64;
    }
}

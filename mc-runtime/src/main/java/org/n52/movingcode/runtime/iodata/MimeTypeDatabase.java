package org.n52.movingcode.runtime.iodata;

import java.io.*;
import java.util.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sun.activation.registries.MimeTypeEntry;

public class MimeTypeDatabase {
    private String fname = null;
    private Multimap<String, String> mimeToExtMap = ArrayListMultimap.create();
    private Multimap<String, String> extToMimeMap = ArrayListMultimap.create();

    private static final String[] defaultFileExt = {"dat"};

    /**
     * The construtor that takes a filename as an argument.
     * 
     * @param new_fname
     *        The file name of the mime types file.
     */
    public MimeTypeDatabase(String new_fname) throws IOException {
        File mime_file = null;
        FileReader fr = null;

        this.fname = new_fname; // remember the file name

        mime_file = new File(this.fname); // get a file object

        fr = new FileReader(mime_file);

        try {
            parse(new BufferedReader(fr));
        }
        finally {
            try {
                fr.close(); // close it
            }
            catch (IOException e) {
                // ignore it
            }
        }
    }

    public MimeTypeDatabase(InputStream is) throws IOException {
        parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
    }

    /**
     * Creates an empty DB.
     */
    public MimeTypeDatabase() {
    }

    /**
     * get the MimeTypeEntries based on the file extension
     */
    public MimeTypeEntry[] getMimeTypeEntriesByExt(String file_ext) {
        Collection<String> mimetypes = this.mimeToExtMap.get(file_ext);
        MimeTypeEntry[] entries = new MimeTypeEntry[mimetypes.size()];
        int i = 0;
        for (String mt : mimetypes) {
            entries[i] = new MimeTypeEntry(mt, file_ext);
            i++;
        }
        return entries;
    }

    /**
     * get the MimeTypeEntries based on the file extension
     */
    public MimeTypeEntry[] getMimeTypeEntriesByMime(String mime_type) {
        Collection<String> extensions = this.extToMimeMap.get(mime_type);
        MimeTypeEntry[] entries = new MimeTypeEntry[extensions.size()];
        int i = 0;
        for (String ext : extensions) {
            entries[i] = new MimeTypeEntry(mime_type, ext);
            i++;
        }
        return entries;
    }

    /**
     * Get the MIME type string array corresponding to the file extension.
     */
    public String[] getMIMETypeStrings(String file_ext) {
        if (this.extToMimeMap.containsKey(file_ext)) {
            return this.extToMimeMap.get(file_ext).toArray(new String[this.extToMimeMap.get(file_ext).size()]);
        }
        return null;
    }

    /**
     * Get the File Extension string array corresponding to the mimeType.
     */
    public String[] getExtensionStrings(String mime_type) {
        if (this.mimeToExtMap.containsKey(mime_type)) {
            return this.mimeToExtMap.get(mime_type).toArray(new String[this.mimeToExtMap.get(mime_type).size()]);
        }
        return defaultFileExt;
    }

    /**
     * Appends string of entries to the types registry, must be valid .mime.types format. A mime.types entry
     * is one of two forms:
     * 
     * type/subtype ext1 ext2 ... or type=type/subtype desc="description of type" exts=ext1,ext2,...
     * 
     * Example: # this is a test audio/basic au text/plain txt text type=application/postscript exts=ps,eps
     */
    public void appendToRegistry(String mime_types) {
        try {
            parse(new BufferedReader(new StringReader(mime_types)));
        }
        catch (IOException ex) {
            // can't happen
        }
    }

    public boolean contains(String mime_type) {
        return this.mimeToExtMap.containsKey(mime_type);
    }

    /**
     * Parse a stream of mime.types entries.
     */
    private void parse(BufferedReader buf_reader) throws IOException {
        String line = null, prev = null;

        while ( (line = buf_reader.readLine()) != null) {
            if (prev == null)
                prev = line;
            else
                prev += line;
            int end = prev.length();
            if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
                prev = prev.substring(0, end - 1);
                continue;
            }
            this.parseEntry(prev);
            prev = null;
        }
        if (prev != null)
            this.parseEntry(prev);
    }

    /**
     * Parse single mime.types entry.
     */
    private void parseEntry(String line) {
        String mime_type = null;
        String file_ext = null;
        String currentLine = line.trim();

        if (currentLine.length() == 0) // empty line...
            return; // BAIL!

        // check to see if this is a comment line?
        if (currentLine.charAt(0) == '#')
            return; // then we are done!

        // is it a new format line or old format?
        if (currentLine.indexOf('=') > 0) {
            // new format
            MimeLineTokenizer lt = new MimeLineTokenizer(currentLine);
            while (lt.hasMoreTokens()) {
                String name = lt.nextToken();
                String value = null;
                if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens())
                    value = lt.nextToken();
                if (value == null) {
                    // System.out.println("Bad .mime.types entry: " + line);
                    return;
                }
                if (name.equals("type"))
                    mime_type = value;
                else if (name.equals("exts")) {
                    StringTokenizer st = new StringTokenizer(value, ",");
                    while (st.hasMoreTokens()) {
                        file_ext = st.nextToken();

                        this.extToMimeMap.put(file_ext, mime_type);
                        this.mimeToExtMap.put(mime_type, file_ext);
                        // System.out.println("Added: " + entry.toString());
                    }
                }
            }
        }
        else {
            // old format
            // count the tokens
            StringTokenizer strtok = new StringTokenizer(currentLine);
            int num_tok = strtok.countTokens();

            if (num_tok == 0) // empty line
                return;

            mime_type = strtok.nextToken(); // get the MIME type

            while (strtok.hasMoreTokens()) {
                // MimeTypeEntry entry = null;

                file_ext = strtok.nextToken();
                this.extToMimeMap.put(file_ext, mime_type);
                this.mimeToExtMap.put(mime_type, file_ext);
                // System.out.println("Added: " + entry.toString());
            }
        }
    }

    // for debugging
    /*
     * public static void main(String[] argv) throws Exception { MimeTypeFile mf = new MimeTypeFile(argv[0]);
     * System.out.println("ext " + argv[1] + " type " + mf.getMIMETypeString(argv[1])); System.exit(0); }
     */
}


class MimeLineTokenizer {
    private int currentPosition;
    private int maxPosition;
    private String str;
    private Vector stack = new Vector();
    private static final String singles = "="; // single character tokens

    /**
     * Constructs a tokenizer for the specified string.
     * <p>
     * 
     * @param str
     *        a string to be parsed.
     */
    public MimeLineTokenizer(String str) {
        this.currentPosition = 0;
        this.str = str;
        this.maxPosition = str.length();
    }

    /**
     * Skips white space.
     */
    private void skipWhiteSpace() {
        while ( (this.currentPosition < this.maxPosition)
                && Character.isWhitespace(this.str.charAt(this.currentPosition))) {
            this.currentPosition++;
        }
    }

    /**
     * Tests if there are more tokens available from this tokenizer's string.
     * 
     * @return <code>true</code> if there are more tokens available from this tokenizer's string;
     *         <code>false</code> otherwise.
     */
    public boolean hasMoreTokens() {
        if (this.stack.size() > 0)
            return true;
        skipWhiteSpace();
        return (this.currentPosition < this.maxPosition);
    }

    /**
     * Returns the next token from this tokenizer.
     * 
     * @return the next token from this tokenizer.
     * @exception NoSuchElementException
     *            if there are no more tokens in this tokenizer's string.
     */
    public String nextToken() {
        int size = this.stack.size();
        if (size > 0) {
            String t = (String) this.stack.elementAt(size - 1);
            this.stack.removeElementAt(size - 1);
            return t;
        }
        skipWhiteSpace();

        if (this.currentPosition >= this.maxPosition) {
            throw new NoSuchElementException();
        }

        int start = this.currentPosition;
        char c = this.str.charAt(start);
        if (c == '"') {
            this.currentPosition++;
            boolean filter = false;
            while (this.currentPosition < this.maxPosition) {
                c = this.str.charAt(this.currentPosition++);
                if (c == '\\') {
                    this.currentPosition++;
                    filter = true;
                }
                else if (c == '"') {
                    String s;

                    if (filter) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = start + 1; i < this.currentPosition - 1; i++) {
                            c = this.str.charAt(i);
                            if (c != '\\')
                                sb.append(c);
                        }
                        s = sb.toString();
                    }
                    else
                        s = this.str.substring(start + 1, this.currentPosition - 1);
                    return s;
                }
            }
        }
        else if (singles.indexOf(c) >= 0) {
            this.currentPosition++;
        }
        else {
            while ( (this.currentPosition < this.maxPosition)
                    && singles.indexOf(this.str.charAt(this.currentPosition)) < 0
                    && !Character.isWhitespace(this.str.charAt(this.currentPosition))) {
                this.currentPosition++;
            }
        }
        return this.str.substring(start, this.currentPosition);
    }

    public void pushToken(String token) {
        this.stack.addElement(token);
    }
}

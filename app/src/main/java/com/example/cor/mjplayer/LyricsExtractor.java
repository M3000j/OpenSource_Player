package com.example.cor.mjplayer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by M3000j on 12/19/2017.
 */

public class LyricsExtractor {

    public static String getLyrics(File file) throws Exception {
        String filename = file.getName();
        String fileending = filename.substring(filename.lastIndexOf('.') + 1, filename.length()).toLowerCase();
        if(fileending.equalsIgnoreCase("mp3"))
        {
            return getLyricsID3(file);
        }
        return null;
    }


    private static String getLyricsID3(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        byte buffer[] = new byte[4];
        in.read(buffer, 0, 3);
        if (!Arrays.equals(buffer, new byte[]{'I', 'D', '3', 0})) {
            in.close();
            return null;
        }

        ///mahdodeye lyric ahang ra peyda mikonad
        in.read(buffer, 0, 3);
        boolean ext = (buffer[2] & (byte) 0b0100000) != 0;
        in.read(buffer);
        int len = buffer[3] & 0x7F | (buffer[2] & 0x7F) << 7 | (buffer[1] & 0x7F) << 14 | (buffer[0] & 0x7F) << 21;
        if (ext) {
            in.read(buffer);
            len -= 4;
            int ext_len = byteArrayToInt(buffer);
            in.skip(ext_len);
            len -= ext_len;

        }


        ///mahdodye lyric az bad az uslt aghaz mishavad
        while (len > 0) {
            byte tag_name[] = new byte[4];
            in.read(tag_name);
            len -= 4;
            if (tag_name[0] == 0) break;
            in.read(buffer);
            len -= 4;
            int tag_len = byteArrayToInt(buffer);
            in.read(buffer, 0, 2);
            len -= 2;
            if (Arrays.equals(tag_name, new byte[]{'U', 'S', 'L', 'T'})) {
                byte head[] = new byte[4];
                in.read(head);
                len -= 4;
                tag_len -= 4;
                while (in.read() != 0) {
                    len--;
                    tag_len--;
                }
                if (head[0] == 1) in.read();
                byte tag_value[] = new byte[tag_len];////make array at the size of lyrictext
                in.read(tag_value);
                len -= tag_len;
                in.close();
                Charset charset = null;
                switch (head[0]) {
                    case 0:
                        charset = Charset.forName("ISO-8859-1");
                        break;
                    case 1:
                        charset = Charset.forName("UTF-16");
                        break;
                    case 2:
                        charset = Charset.forName("UTF-16BE");
                        break;
                    case 3:
                        charset = Charset.forName("UTF-8");
                        break;
                    default:
                        return null;
                }
                return new String(tag_value, charset);

            } else {
                in.skip(tag_len);
                len -= tag_len;
            }

        }
        in.close();
        return null;

    }

    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }


}

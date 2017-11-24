/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Administrator
 */
    /*
    //        Path p = Files.createFile(Paths.get(zipFilePath));

        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(file))) {
            Path pp = Paths.get(sourceDirPath);
            Files.walk(pp)
              .filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                  String sp = path.toAbsolutePath().toString().replace(pp.toAbsolutePath().toString(), "").replace(path.getFileName().toString(), "");
                  ZipEntry zipEntry = new ZipEntry(sp + "/" + path.getFileName().toString());
                  try {
                      zs.putNextEntry(zipEntry);
                      zs.write(Files.readAllBytes(path));
                      zs.closeEntry();
                } catch (Exception e) {
                    System.err.println(e);
                }
              });
        }

    
    */

public class ZipUtil {
    public static class ZipOutputFile implements Closeable {
        
        ZipOutputStream z;
                
        ZipOutputFile(File f) throws IOException {
            z = new ZipOutputStream(Files.newOutputStream(f.toPath()));
        }
        
        public void addFile(File f, Path folderRelativeTo) throws IOException {
            ZipEntry zipEntry = new ZipEntry(folderRelativeTo.relativize(f.toPath()).toString());
            z.putNextEntry(zipEntry);
            z.write(Files.readAllBytes(f.toPath()));
            z.closeEntry();
        }
        
        public void addDirectory(Path dir, Path folderRelativeTo) throws IOException {
            Files.walk(dir) 
                .filter(p -> !Files.isDirectory(p))
                .forEach(p -> {
                    try {
                        addFile(p.toFile(), folderRelativeTo);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });
        }

        @Override
        public void close() throws IOException {
            if (z!=null) z.close();
        }
    }

    public static void unZipFile(File zipFile, Path folder) throws IOException{
        byte[] buffer = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = folder.resolve(fileName).toFile();

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
        }

    		
    }        
    
}

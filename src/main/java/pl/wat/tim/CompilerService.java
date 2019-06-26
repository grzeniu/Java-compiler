package pl.wat.tim;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
public class CompilerService {

    private static final String BASE_FILE_PATH = "uploaded";

    void runProgram(List<MultipartFile> filesList) {
        saveFiles(filesList);
        compile();
        makeJar();
        getOutputStringFromJar();
    }

    private void getOutputStringFromJar() {
        try {
            Process p = Runtime.getRuntime().exec("java -jar uploaded\\App.jar");
            p.waitFor();

            InputStream is = p.getInputStream();

            byte b[] = new byte[is.available()];
            is.read(b, 0, b.length);

            System.out.println(new String(b));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void makeJar() {
        try {

            File dir = new File(System.getProperty("user.dir") + "\\" + BASE_FILE_PATH);
            String cmd = "jar cfe App.jar Main *.class";
            Runtime.getRuntime().exec(cmd, null, dir);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void compile() {
        try {
            String compileCommand = "javac " + BASE_FILE_PATH + "\\*.java";
            Runtime.getRuntime().exec(compileCommand);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void saveFiles(List<MultipartFile> filesList) {
        createDirectory("\\" + BASE_FILE_PATH);

        filesList
                .stream()
                .filter(file -> !file.isEmpty())
                .forEach(mulipartFile -> {
                    try {
                        File file = new File(getFilePath(mulipartFile.getOriginalFilename()));
                        mulipartFile.transferTo(file);
                        removePackageLine(file);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                });

    }

    private void removePackageLine(File myFile) throws IOException {
        Scanner fileScanner = new Scanner(myFile);
        fileScanner.nextLine();

        FileWriter fileStream = new FileWriter(myFile);
        BufferedWriter out = new BufferedWriter(fileStream);
        while (fileScanner.hasNextLine()) {
            String next = fileScanner.nextLine();
            if (!next.contains("package "))
                out.write(next);
            else
                out.newLine();
            out.newLine();
        }
        out.close();
    }

    private String getFilePath(String fileName) {
        return System.getProperty("user.dir") + "\\" + BASE_FILE_PATH + "\\" + fileName;
    }

    private void createDirectory(String path) {
        try {
            Files.createDirectory(Paths.get(System.getProperty("user.dir") + path));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

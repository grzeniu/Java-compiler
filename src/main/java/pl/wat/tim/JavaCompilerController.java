package pl.wat.tim;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JavaCompilerController {

    private final CompilerService compilerService;

    @PostMapping(path = "files/upload")
    public ResponseEntity register(@RequestPart("filesList") List<MultipartFile> filesList) {
        compilerService.runProgram(filesList);

        return new ResponseEntity(HttpStatus.CREATED);
    }
}

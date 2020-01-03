package sample.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sample.communication.files.ServerFileManager;
import sample.springboot.dto.MusicDto;
import sample.springboot.repository.MusicsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class MusicsController {

    @Autowired
    private MusicsRepository musicsRepository;

    @GetMapping(value = "/musics")
    public List<MusicDto> listMusics() {
        return musicsRepository.fetchMusics();
    }

    @GetMapping(value = "/download")
    public ResponseEntity downloadMusic(@RequestParam String music) {
        if (music.startsWith("..")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Music Name!");
        }

        if(!musicsRepository.existsByName(music)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Music Not Found! Check the list of available musics at /musics");
        }

        Path path = Paths.get(ServerFileManager.getMusicPath(music));

        if (!path.toFile().isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Music File Not Found! Try to upload the music again!");
        }

        ByteArrayResource resource = null;
        try {
            resource = new ByteArrayResource(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + music + ".mp3" + "\"")
                .body(resource);
    }

}

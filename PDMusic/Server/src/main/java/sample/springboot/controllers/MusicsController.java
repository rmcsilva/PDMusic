package sample.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.springboot.dto.MusicDto;
import sample.springboot.repository.MusicsRepository;

import java.util.List;

@RestController
public class MusicsController {

    @Autowired
    private MusicsRepository musicsRepository;

    @GetMapping(value = "/musics")
    public List<MusicDto> listMusics() {
        return musicsRepository.fetchMusics();
    }

}

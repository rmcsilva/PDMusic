package sample.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sample.springboot.dto.MusicDto;
import sample.springboot.entity.Music;

import java.util.List;

public interface MusicsRepository extends JpaRepository<Music, Integer> {
    @Query("SELECT new sample.springboot.dto.MusicDto(m.name, m.author, m.album, m.year, m.duration, m.genre, u.username) "
            + "FROM Music m INNER JOIN m.user u")
    List<MusicDto> fetchMusics();
    Boolean existsByName(String name);
}

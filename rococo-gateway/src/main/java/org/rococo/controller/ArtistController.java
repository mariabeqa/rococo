package org.rococo.controller;


import org.rococo.model.ArtistJson;
import org.rococo.service.api.ArtistGrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistGrpcClient artistGrpcClient;

    public ArtistController(ArtistGrpcClient artistGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
    }

    @GetMapping()
    public Page<ArtistJson> getAll(@RequestParam(required = false) String name,
                                   @PageableDefault Pageable pageable) {
        return artistGrpcClient.getAll(name, pageable);
    }

    @GetMapping("/{id}")
    public ArtistJson findArtistById(@PathVariable("id") String id) {
        return artistGrpcClient.findArtistById(id);
    }

    @PatchMapping()
    public ArtistJson updateArtist(@RequestBody ArtistJson artist) {
        return artistGrpcClient.updateArtist(artist);
    }

    @PostMapping()
    public ArtistJson addArtist(@RequestBody ArtistJson artist) {
        return artistGrpcClient.addArtist(artist);
    }
}

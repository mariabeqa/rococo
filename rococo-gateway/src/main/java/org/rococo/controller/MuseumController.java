package org.rococo.controller;


import org.rococo.model.MuseumJson;
import org.rococo.service.api.MuseumGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/api/museum")
public class MuseumController {

  private final MuseumGrpcClient museumGrpcClient;

  @Autowired
  public MuseumController(MuseumGrpcClient museumGrpcClient) {
    this.museumGrpcClient = museumGrpcClient;
  }

  @GetMapping()
  public Page<MuseumJson> getAll(@RequestParam(required = false) String title,
                                 @PageableDefault Pageable pageable) {
    return museumGrpcClient.getAll(title, pageable);
  }

  @GetMapping("/{id}")
  public MuseumJson findMuseumById(@PathVariable("id") String id) {
    return museumGrpcClient.findMuseumById(id);
  }

  @PatchMapping()
  public MuseumJson updateMuseum(@RequestBody MuseumJson museum) {
    return museumGrpcClient.updateMuseum(museum);
  }

  @PostMapping()
  public MuseumJson addMuseum(@RequestBody MuseumJson museum) {
    return museumGrpcClient.addMuseum(museum);
  }

  @DeleteMapping()
  public void deleteMuseum(@RequestParam String museumId) {
    museumGrpcClient.deleteMuseum(museumId);
  }
}

package guru.qa.rococo.controller;


import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.PaintingGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/painting")
public class PaintingController {

  private final PaintingGrpcClient paintingGrpcClient;

  @Autowired
  public PaintingController(PaintingGrpcClient paintingGrpcClient) {
    this.paintingGrpcClient = paintingGrpcClient;
  }

  @GetMapping()
  public Page<PaintingJson> getAll(@RequestParam(required = false) String title,
                                   @PageableDefault Pageable pageable) {
    return paintingGrpcClient.getAll(title, pageable);
  }

  @GetMapping("/{id}")
  public PaintingJson findPaintingById(@PathVariable("id") String id) {
    return paintingGrpcClient.findPaintingById(id);
  }

  @GetMapping("/author/{id}")
  public Page<PaintingJson> findPaintingByAuthorId(@PathVariable("id") String id,
                                                   @PageableDefault Pageable pageable) {
    return paintingGrpcClient.findPaintingByAuthorId(id, pageable);
  }

  @PatchMapping()
  public PaintingJson updatePainting(@RequestBody PaintingJson painting) {
    return paintingGrpcClient.updatePainting(painting);
  }

  @PostMapping()
  public PaintingJson addPainting(@RequestBody PaintingJson painting) {
    return paintingGrpcClient.addPainting(painting);
  }
}

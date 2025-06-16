package guru.qa.rococo.controller;


import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.api.CountryGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/api/country")
public class CountryController {

  private final CountryGrpcClient countryGrpcClient;

  @Autowired
  public CountryController(CountryGrpcClient countryGrpcClient) {
    this.countryGrpcClient = countryGrpcClient;
  }

  @GetMapping()
  public Page<CountryJson> getAll(@RequestParam(required = false) String title,
                                  @PageableDefault Pageable pageable) {
    return countryGrpcClient.getAll(title, pageable);
  }

  @GetMapping("/{id}")
  public CountryJson findCountryById(@PathVariable("id") String id) {
    return countryGrpcClient.findCountryById(id);
  }

  @GetMapping("/name/{name}")
  public CountryJson findCountryByName(@PathVariable("name") String name) {
    return countryGrpcClient.findCountryByName(name);
  }
}

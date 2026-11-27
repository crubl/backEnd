package app.tourment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tournaments")
public class TourmentController {
    private final TourmentService service;

    public TourmentController(TourmentService service) {
        this.service = service;
    }

    public record CreateTourmentRequest(
            @NotBlank(message = "type не должно быть пустым") String type,
            @NotBlank(message = "name не должно быть пустым") String name,
            int gameId,
            int minExp,
            int minPlayers,
            int duration) {}

    @GetMapping
    public List<Tourment> getAll(@RequestParam(required = false) String type) {
        return service.getAll(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tourment create(@Valid @RequestBody CreateTourmentRequest req) {
        return service.create(req.type(), req.name(), req.gameId(),
                req.minExp(), req.minPlayers(), req.duration());
    }

    @GetMapping("/{id}")
    public Tourment getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Tourment update(@PathVariable int id, @RequestBody Map<String, String> body) {
        return service.update(id, body.get("name"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PostMapping("/{id}/status")
    public Tourment changeStatus(@PathVariable int id) {
        return service.changeStatus(id);
    }
}
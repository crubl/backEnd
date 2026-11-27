package app.player;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    public record CreatePlayerRequest(
            @NotBlank(message = "name не должно быть пустым") String name,
            @NotBlank(message = "type не должно быть пустым") String type) {}

    public record AddExpRequest(
            @NotNull(message = "amount обязательно")
            @PositiveOrZero(message = "amount не может быть отрицательным")
            Long amount) {}

    @GetMapping
    public List<Player> getAll(@RequestParam(required = false) String type) {
        return service.getAll(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(@Valid @RequestBody CreatePlayerRequest req) {
        return service.create(req.name(), req.type());
    }

    @GetMapping("/{id}")
    public Player getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Player update(@PathVariable int id, @RequestBody Map<String, String> body) {
        return service.update(id, body.get("name"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @PostMapping("/{id}/exp")
    public Player addExp(@PathVariable int id, @Valid @RequestBody AddExpRequest req) {
        return service.addExp(id, req.amount());
    }
}
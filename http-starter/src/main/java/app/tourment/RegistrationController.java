package app.tourment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    public record RegisterRequest(
            @Positive(message = "tournamentId должен быть положительным") int tournamentId,
            @Positive(message = "playerId должен быть положительным") int playerId) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Registration register(@Valid @RequestBody RegisterRequest req) {
        return service.register(req.tournamentId(), req.playerId());
    }

    @GetMapping
    public List<Registration> getAll() {
        return service.getAll();
    }
}
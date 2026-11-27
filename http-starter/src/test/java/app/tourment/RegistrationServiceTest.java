package app.tourment;

import app.Storage;
import app.exception.ConflictException;
import app.exception.NotFoundException;
import app.player.Player;
import app.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RegistrationServiceTest {

    private Storage store;
    private PlayerService playerService;
    private TourmentService tourmentService;
    private RegistrationService service;

    @BeforeEach
    void setUp() {
        store = new Storage();
        playerService = new PlayerService(store);
        tourmentService = new TourmentService(store);
        service = new RegistrationService(store, playerService, tourmentService);
    }

    @Test
    void register_success() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t = tourmentService.create("NOOB", "Cup", 1, 0, 1, 7);
        Registration reg = service.register(t.getId(), p.getId());
        assertThat(reg.getTournamentId()).isEqualTo(t.getId());
        assertThat(reg.getPlayerId()).isEqualTo(p.getId());
        assertThat(store.registrations).containsValue(reg);
    }

    @Test
    void register_whenAlreadyRegistered_throwsConflict() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t = tourmentService.create("NOOB", "Cup", 1, 0, 1, 7);
        service.register(t.getId(), p.getId());
        assertThatThrownBy(() -> service.register(t.getId(), p.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("уже зарегистрирован");
    }

    @Test
    void register_whenNotEnoughExp_throwsConflict() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t = tourmentService.create("PRO", "Pro Cup", 1, 100, 1, 7);
        assertThatThrownBy(() -> service.register(t.getId(), p.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Недостаточно опыта");
    }

    @Test
    void register_whenTourmentNotOpen_throwsConflict() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t = tourmentService.create("NOOB", "Cup", 1, 0, 1, 7);
        Player otherPlayer = playerService.create("Petr", "NOOB");
        service.register(t.getId(), otherPlayer.getId());
        tourmentService.changeStatus(t.getId());
        assertThatThrownBy(() -> service.register(t.getId(), p.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("статус турнира");
    }

    @Test
    void register_whenLimitReached_throwsConflict() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t1 = tourmentService.create("NOOB", "Cup1", 1, 0, 1, 7);
        Tourment t2 = tourmentService.create("NOOB", "Cup2", 1, 0, 1, 7);
        Tourment t3 = tourmentService.create("NOOB", "Cup3", 1, 0, 1, 7);
        service.register(t1.getId(), p.getId());
        service.register(t2.getId(), p.getId());
        assertThatThrownBy(() -> service.register(t3.getId(), p.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("лимит активных турниров");
    }

    @Test
    void register_freesSlotAfterTourmentFinished() {
        Player p = playerService.create("Ivan", "NOOB");
        Tourment t1 = tourmentService.create("NOOB", "Cup1", 1, 0, 1, 7);
        Tourment t2 = tourmentService.create("NOOB", "Cup2", 1, 0, 1, 7);
        Tourment t3 = tourmentService.create("NOOB", "Cup3", 1, 0, 1, 7);
        service.register(t1.getId(), p.getId());
        service.register(t2.getId(), p.getId());
        tourmentService.changeStatus(t1.getId());
        tourmentService.changeStatus(t1.getId());
        Registration reg = service.register(t3.getId(), p.getId());
        assertThat(reg).isNotNull();
    }

    @Test
    void register_whenPlayerNotExists_throwsNotFound() {
        Tourment t = tourmentService.create("NOOB", "Cup", 1, 0, 1, 7);
        assertThatThrownBy(() -> service.register(t.getId(), 999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void register_whenTourmentNotExists_throwsNotFound() {
        Player p = playerService.create("Ivan", "NOOB");
        assertThatThrownBy(() -> service.register(999, p.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}